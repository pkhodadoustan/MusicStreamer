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
    
    public long md5(String objectName)
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

    public int getPort() {
        return port;
    }

    public Chord getChord() {
        return chord;
    }
    
        public void createMetadata() throws FileNotFoundException, IOException
    {
        String metadataStr = "[]";
        FileStream fileStream = new FileStream(metadataStr.getBytes());
        writeMetaData(fileStream);
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
    
    public void writeMetaData(InputStream stream) throws RemoteException, IOException //updates the content of Metadata file 
    {
        long guid = md5(METADATA_KEY);
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, stream);
    }
    
    public DistributedFile[] getMetadataFiles() throws Exception
    {
        String metadataStr = readMetaData();
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        return files;
    }
   
    /* modifies  a file name in metadata.json*/
    public void mv(String oldName, String newName) throws Exception
    {
        String metadataStr = readMetaData();
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        /*find and rename the file in metadata*/
        for(DistributedFile file : files)
        {
            if(file.getFilename().equals(oldName))
            {
               file.setFilename(newName);
               break;
            }
        }
        /* write metdata with modified file name */
        metadataStr = gson.toJson(files); 
        writeMetaData(new FileStream(metadataStr.getBytes()));
    }

    /* lists file names in metadata.json*/
    public String ls() throws Exception
    {
        String listOfFiles = "";
        String metadataStr = readMetaData();
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        /*get string format if each file in metadata */
        for(DistributedFile f : files)
            listOfFiles = listOfFiles + f.toString()+"\n";
        return listOfFiles;
    }
    
    /* Create the file fileName by adding a new entry to the Metadata */
    public void touch(String fileName) throws Exception
    {
        String metadataStr = readMetaData();
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        
        /* make and add a new file to files array */
        List<DistributedFile> filesList = new ArrayList<>(Arrays.asList(files));
        filesList.add(new DistributedFile(fileName));
        files = filesList.toArray(new DistributedFile[filesList.size()]);
        
        /* Write the update content including new file to Metadata*/
        String updatedContent = gson.toJson(files);
        FileStream fileStream = new FileStream(updatedContent.getBytes());
        writeMetaData(fileStream);
    }
    
    /* a file is a an abstract category on metadata file that contains list of pages
     * a page is a local file that is uploaded to the Chord
     */
    public void append(String pagePath, String PageName, String fileName) throws Exception
    {
      /* Adding the page (local file) to a peer on Chord*/   
        /*getting guid for the file using hash function MD5, and filename as key*/
        long guid = md5(PageName);
        /*reading the content of the local file into inputStream to be used as a page*/
        FileStream inputFileStream = new FileStream(pagePath+PageName);
        /*append the file to successor peer of the page guid on Chord*/
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, inputFileStream);
        
      /* Adding the file and page information to the metadata*/
        /* Getting json-formatted string of metadata*/
        String metadataStr = readMetaData();
        /* convert the string content of metadata to list of DistributedFile objects*/
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        /* if the file already exists find and append the page information to it */
        boolean fileFound = false;
        for(DistributedFile file : files)
        {
            if(file.getFilename().equals(fileName))
            {
               file.addPage(new Page(guid, file.getNumberOfPages()+1, inputFileStream.getSize()));
               fileFound = true;
               break;
            }
        }
        /* if the file does not exist, add a new file with appended page */
        if(!fileFound)
        {
            DistributedFile newFile = new DistributedFile(fileName, 0, new ArrayList<Page>());
            newFile.addPage(new Page(guid, newFile.getNumberOfPages()+1, inputFileStream.getSize()));
            List<DistributedFile> filesList = new ArrayList<>(Arrays.asList(files));
            filesList.add(newFile);
            files = filesList.toArray(new DistributedFile[filesList.size()]);
        }
        inputFileStream.close();
        
        /* Write updated array of files to metadata */
        String updatedContent = gson.toJson(files);
        FileStream fileStream = new FileStream(updatedContent.getBytes());
        writeMetaData(fileStream);
        fileStream.close();
    }
    
    public void delete(String fileName) throws Exception
    {
        String metadataStr = readMetaData();
        Gson gson = new Gson();
        DistributedFile[] files = gson.fromJson(metadataStr, DistributedFile[].class);
        
        /* delete all pages of a file from Chord */
        List<DistributedFile> filesList = new ArrayList<>(Arrays.asList(files));
        for(DistributedFile file : filesList)
        {
            if(file.getFilename().equals(fileName))
            {
                for(Page page: file.getPages())
                {
                    ChordMessageInterface peer = chord.locateSuccessor(page.getGuid());
                    peer.delete(page.getGuid());
                }
                /* delete file from metadata.json*/
                filesList.remove(file);
                break;
            }
        }
        
        /*write updated content to metadata.json*/
        files = filesList.toArray(new DistributedFile[filesList.size()]);
        String updatedContent = gson.toJson(files);
        FileStream fileStream = new FileStream(updatedContent.getBytes());
        writeMetaData(fileStream);   
    }
    
    public String readFile(String filename) throws RemoteException, IOException
    {
        long guid = md5(filename);
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        InputStream fileRaw = peer.get(guid);
        byte[] fileBytes = new byte[fileRaw.available()];
        int i = 0;
        while(fileRaw.available()>0)
        {
            fileBytes[i] = (byte)fileRaw.read();
            i++;
        }
        String fileStr = new String(fileBytes);
        return fileStr;
    }
    
    /*reads page content (in bytes)& returns a byte array of the content of the page */
    public byte[] read(String fileName, int pageNumber) throws Exception
    {
        DistributedFile[] files = getMetadataFiles();
        for(DistributedFile file : files)
        {
            if(file.getFilename().equals(fileName) && pageNumber<=file.getNumberOfPages())
            {
                long guid = file.getPages().get(pageNumber-1).getGuid();
                ChordMessageInterface peer = chord.locateSuccessor(guid);
                InputStream pageContent = peer.get(guid);
                byte[] pageBytes = new byte[pageContent.available()];
                int i = 0;
                while(pageContent.available()>0)
                {
                    pageBytes[i] = (byte)pageContent.read();
                    i++;
                }
                return pageBytes;
            }
        }
        return (new byte[0]);     
    }
    
    /* return the last page of the fileName */
    public byte[] tail(String fileName) throws Exception
    {
        DistributedFile[] files = getMetadataFiles();
        for(DistributedFile file : files)
        {
            if(file.getFilename().equals(fileName))
            {
                long guid = file.getPages().get(file.getNumberOfPages()-1).getGuid();
                ChordMessageInterface peer = chord.locateSuccessor(guid);
                InputStream pageContent = peer.get(guid);
                byte[] pageBytes = new byte[pageContent.available()];
                int i = 0;
                while(pageContent.available()>0)
                {
                    pageBytes[i] = (byte)pageContent.read();
                    i++;
                }
                return pageBytes;
            }
        }
        return (new byte[0]); 
    }
    
    /* return the first page of the fileName */
    public byte[] head(String fileName) throws Exception
    {
        return read(fileName, 1);
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
