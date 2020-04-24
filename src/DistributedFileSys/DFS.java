/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DistributedFileSys;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.rmi.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.math.BigInteger;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
import javax.json.stream.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
// import a json package


/* JSON Format

 {
    "metadata" :
    {
        file :
        {
            name  : "File1"
            numberOfPages : "3"
            pageSize : "1024"
            size : "2291"
            page :
            {
                number : "1"
                guid   : "22412"
                size   : "1024"
            }
            page :
            {
                number : "2"
                guid   : "46312"
                size   : "1024"
            }
            page :
            {
                number : "3"
                guid   : "93719"
                size   : "243"
            }
        }
    }
}
 
 
 */

/**
 *
 * @author 018639476
 */
//DFS: Distribute File System Class
//This class provides methodes for controlling the files on a peer
//DFS instance represents a conroller for the portion of distributed file system on a peer with a specific port
public class DFS {
    final String METADATA_KEY = "metadata.json";
    int port;
    Chord  chord;
    
    private long md5(String objectName)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(objectName.getBytes());
            BigInteger bigInt = new BigInteger(1,m.digest());
            return Math.abs(bigInt.longValue());
        }
        catch(NoSuchAlgorithmException e)
        {
                e.printStackTrace();
                
        }
        return 0;
    }
    
    
    
    public DFS(int port) throws Exception
    {
        this.port = port;
        long guid = md5("" + port);
        chord = new Chord(port, guid);
        Files.createDirectories(Paths.get(guid+"/repository"));
        System.out.println("dfs constructed.");
    }
    
    public  void join(String Ip, int port) throws Exception
    {
        chord.joinRing(Ip, port);
    }
    
    public String readMetaData() throws Exception //gets the content of Metadata file from the peer that has it
    {
        //JsonParser jsonParser = null;
        long guid = md5(METADATA_KEY);
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        InputStream metadataraw = peer.get(guid);
        byte[] metadataBytes = new byte[metadataraw.available()];
        int i = 0;
        while(metadataraw.available()>0)
        {
            metadataBytes[i] = (byte)metadataraw.read();
            i++;
        }
        String metadataStr = new String(metadataBytes);
        return metadataStr;
    }
    
    public void writeMetaData(InputStream stream) throws Exception //updates the content of Metadata file 
    {
        //JsonParser jsonParser = null;
        long guid = md5(METADATA_KEY);
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, stream);
    }
   
    public void mv(String oldName, String newName) throws Exception
    {
        // TODO:  Change the name in Metadata
        String metadataStr = readMetaData();
        //convert the string content of metadata to list of file objects
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        //find and rename the file
        for(DistributedFile file : files)
        {
            if(file.getFilename().equals(oldName))
            {
               file.setFilename(newName);
               break;
            }
        }
        metadataStr = gson.toJson(files); //updated content of metadata
        System.out.println("updated metadata: " + metadataStr);
        // Write Metadata
        FileStream fileStream = new FileStream(metadataStr.getBytes());
        writeMetaData(fileStream);
        
        //find file on Chord
        long guidObj =  md5(oldName);
        long newGuidObj = md5(newName);
        ChordMessageInterface peer = chord.locateSuccessor(guidObj);
        File file = new File("./"+peer.getId()+"/repository/" + guidObj);
        file.renameTo(new File ("./"+peer.getId()+"/repository/" + newGuidObj));
        
    }

    
    public String ls() throws Exception
    {
        // TODO: returns all the files in the Metadata
        String listOfFiles = "";
        //get the string content of metadata
        String metadataStr = readMetaData();
        //convert the string content of metadata to list of file objects
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        //get the filenames from each file in list of files
        for(DistributedFile f : files)
            listOfFiles = listOfFiles + f.getFilename()+"\n";
        return listOfFiles;
    }

    
    public void touch(String fileName) throws Exception
    {
        // TODO: Create the file fileName by adding a new entry to the Metadata
        String metadataStr = readMetaData();
        //convert the string content of metadata to list of file objects
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        
        //make new file
        DistributedFile newFile = new DistributedFile(fileName);
        
        //add a file to array of files
        List<DistributedFile> filesList = new ArrayList<>(Arrays.asList(files));
        filesList.add(newFile);
        files = filesList.toArray(new DistributedFile[filesList.size()]);
        
        //Write Metadata
        String updatedContent = gson.toJson(files);
        FileStream fileStream = new FileStream(updatedContent.getBytes());
        writeMetaData(fileStream);
        
        //append newFile to Chord
        long guidObj = md5(fileName);
        ChordMessageInterface peer = chord.locateSuccessor(guidObj);
        peer.put(guidObj, fileStream);
        
    }
    public void delete(String fileName) throws Exception
    {
        //delete file from Chord
        long guidObj = md5(fileName);
        //locate the peer that has the file with guidObj on it
        ChordMessageInterface peer = chord.locateSuccessor(guidObj); 
        //delete the file on the peer
        peer.delete(guidObj); 
        
        // delete Metadata.filename
         String metadataStr = readMetaData();
         
        //convert the string content of metadata to list of file objects
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
       // DistributedFile[] updatedFiles = new DistributedFile[files.length];
        
        //delete the file with filename from the array
        List<DistributedFile> updatedFilesList = new ArrayList(Arrays.asList(files));
        for(int i = 0; i<updatedFilesList.size(); i++)
        {
            if(updatedFilesList.get(i).getFilename().equals(fileName))
            {
                updatedFilesList.remove(i);
                break;
            }
        }
        DistributedFile[] updatedFiles = updatedFilesList.toArray(new DistributedFile[updatedFilesList.size()]);
        
        // Write Metadata
        String updatedContent = gson.toJson(updatedFiles);
        FileStream fileStream = new FileStream(updatedContent.getBytes());
        try {
            writeMetaData(fileStream);
        }
        catch (Exception e) {
            System.out.println("Error in DFS.delete. Could not write to metadata.json. " + e);
        }
        
    }
    
    public byte[] read(String fileName, int pageNumber) throws Exception
    {
        // TODO: read pageNumber from fileName
        int maxPageSize = 1024;
        long guidObj = md5(fileName);
        //locate the peer that has the file with guidObj on it
        ChordMessageInterface peer = chord.locateSuccessor(guidObj); 
        InputStream fileraw = peer.get(guidObj);
        //byte[] fileBytes = new byte[1024];
        byte[] pageBytes = new byte[maxPageSize];
        int pos = 0;
        int i = 0;
        while(fileraw.available()>0 && i<maxPageSize)
        {
            byte readByte = (byte)fileraw.read();
            
            if(pos >= (maxPageSize*(pageNumber-1)))
            {
              pageBytes[i] = readByte;
              i++;
            }
            pos++;
        }
        return pageBytes;
    }
    
    public byte[] tail(String fileName) throws Exception
    {
        // TODO: return the last page of the fileName
        String metadataStr = readMetaData();
        //convert the string content of metadata to list of file objects
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        
        long fileSize = 0;
        for(DistributedFile file : files)
        {
            if(file.getFilename().equals(fileName))
            {
               fileSize = file.getSize();
               break;
            }
        }
        int tailNumber = (int)(fileSize/1024) + 1;
        
        return read(fileName, tailNumber);
    }
    public byte[] head(String fileName) throws Exception
    {
        // TODO: return the first page of the fileName
        return read(fileName, 1);
    }
    public void append(String filePath, String fileName) throws Exception
    {
        // TODO: append data to fileName. If it is needed, add a new page.
        // Let guid be the last page in Metadata.filename
        
        //getting guid for the file using hash function MD5, and filename as key
        long guid = md5(fileName); 
        //reading the content of the file into inputStream
        //locate the file on peer local disk
        //append the file to peer's Chord repository
        // inputStream = new FileInputStream(file);
        FileStream InputFileStream = new FileStream(filePath+fileName);
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, InputFileStream);
       
        //adding the informatioin of new file to metadata
        if(guid!=md5(METADATA_KEY))
        {
            //Creating file json object to be stored in metadata.jsin 
            long size = new File(filePath+fileName).length();
            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
            jsonBuilder.add("filename", fileName);
            jsonBuilder.add("numberOfPages", (size/1024)+1);
            jsonBuilder.add("pageSize", "1024");
            jsonBuilder.add("size", size);

            JsonObject fileJs = jsonBuilder.build();
            String fileJsStr = fileJs.toString();
            
            //reading metadata into a string
            String metadataStr = readMetaData();
            
            //appending the new string to string comtent of metadata
            String metadataStrWithFile = metadataStr.substring(0, metadataStr.length()-1) +"\n,"+ fileJsStr+ metadataStr.substring(metadataStr.length()-1, metadataStr.length());   
            //deleting the first comma if first record
            if(metadataStr.equals("[]"))
            {
               metadataStrWithFile = metadataStrWithFile.substring(0, 2) + metadataStrWithFile.substring(3, metadataStrWithFile.length()); 
            }
            
            //Reading the file json string to InputStream as sequence of bytes
            FileStream fileStream = new FileStream(metadataStrWithFile.getBytes());

            //Write the file info to Metadatas 
            try {
                writeMetaData(fileStream);
            }
            catch (Exception e) {
                System.out.println("Error in DFS.append. Could not write to metadata.json. " + e);
            }

        }
    }
    public void peerPrint()
    {
        try {
            System.out.println("Self GUID: " + chord.getId());
            chord.Print();
        } catch (RemoteException ex) {
            Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
