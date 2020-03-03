/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;

/**
* SongDispatcher is the main responsable for obtaining the songs 
*
* @author  Oscar Morales-Ponce
* @version 0.15
* @since   02-11-2019 
*/

import java.io.IOException;
import java.io.InputStream;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.Base64;
import java.io.FileNotFoundException;

public class SongDispatcher {
        static final int FRAGMENT_SIZE = 8192; 
    public SongDispatcher()
    {
        
    }
    
    /* 
    * getSongChunk: Gets a chunk of a given song
    * @param key: Song ID. Each song has a unique ID 
    * @param fragment: The chunk corresponds to 
    * [fragment * FRAGMENT_SIZE, FRAGMENT_SIZE]
    */
    public String getSongChunk(String key, Long fragment) throws FileNotFoundException, IOException //!!!!!!!!!!!!! Long key -> String key
    {
        byte buf[] = new byte[FRAGMENT_SIZE];

        File file = new File("data\\"+key+".mp3"); //!!!!!!!!!!! "./" + key
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.skip(fragment * FRAGMENT_SIZE);
        inputStream.read(buf);
        inputStream.close(); 
        try {
            Thread.sleep(10);
        } catch (InterruptedException e)
        {
            
        }
        
        // Encode in base64 so it can be transmitted 
         return Base64.getEncoder().encodeToString(buf);
    }
    
    /* 
    * getFileSize: Gets a size of the file
    * @param key: Song ID. Each song has a unique ID 
     */
    public Integer getFileSize(String key) throws FileNotFoundException, IOException //!!!!!!!!!!!!! Long key -> String key
    {
        System.out.println("in SongDispatcher, getFileSize method!");
        File file = new File("data\\"+key+".mp3");       //!!!!!!!!!!!!! "./" + key  
        Integer total =  (int)file.length();
        
        return total;
    }
    
}
