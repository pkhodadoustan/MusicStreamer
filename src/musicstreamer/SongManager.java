
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;

import DistributedFileSys.ChordMessageInterface;
import DistributedFileSys.CommandLine;
import DistributedFileSys.DFS;
import DistributedFileSys.DistributedFile;
import DistributedFileSys.Page;
import Server.searchDFS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pardis Khodadoustan
 */
public class SongManager {
    DFS dfs;
    
    public SongManager(DFS dfs) throws Exception
    {
        this.dfs = dfs;
    }
 
    /**
     * @param artist - String indicating the artist to search for
     * @return List of songs by the artist
     */
    public List<SongRecord> findSongByArtist(String artist)
    {
        List<SongRecord> songs = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            DistributedFile[] files = dfs.getMetadataFiles();
            for(DistributedFile file : files)
            {
                for(int i=1; i <= file.getNumberOfPages(); i++)
                {
                    searchDFS search = new searchDFS(i, file.getFilename(), dfs, artist, "", "", songs);
	            executor.execute(search);
                }
                
            }
            executor.shutdown();
	    // Wait until all threads are finish
            while (!executor.isTerminated()) {}
            
        } catch (Exception ex) {
            Logger.getLogger(SongManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return songs;
    }
    
    /**
     * @param title - String indicating the song title to search for
     * @return List of songs with the song title
     */
    public List<SongRecord> findSongByTitle(String title)
    {
        List<SongRecord> songs = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            DistributedFile[] files = dfs.getMetadataFiles();
            for(DistributedFile file : files)
            {
                for(int i=1; i <= file.getNumberOfPages(); i++)
                {
                    searchDFS search = new searchDFS(i, file.getFilename(), dfs, "", title, "", songs);
	            executor.execute(search);
                }
                
            }
            executor.shutdown();
	    // Wait until all threads are finish
            while (!executor.isTerminated()) {}
            
        } catch (Exception ex) {
            Logger.getLogger(SongManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return songs;
    }
    
        /**
     * 
     * @param id - String indicating the song title to search for
     * @return a song record with the song id
     */
    public SongRecord findSongById(String id)
    {
        List<SongRecord> songs = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            DistributedFile[] files = dfs.getMetadataFiles();
            for(DistributedFile file : files)
            {
                for(int i=1; i <= file.getNumberOfPages(); i++)
                {
                    searchDFS search = new searchDFS(i, file.getFilename(), dfs, "", "", id, songs);
	            executor.execute(search);
                }
                
            }
            executor.shutdown();
	    // Wait until all threads are finish
            while (!executor.isTerminated()) {}
            return songs.get(0);
        } catch (Exception ex) {
            Logger.getLogger(SongManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public User getUser (String username)
    {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader("data\\users\\"+username+".json"));
            User user = gson.fromJson(reader, User.class);
            return user;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SongManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Boolean saveUser(String jsonUser)
    {
        Gson gson = new Gson();
        JsonObject replyJsonObject = gson.fromJson(jsonUser, JsonObject.class);
        System.out.println("In SongManager, Inflated json object is:"+replyJsonObject.toString());
        String username = replyJsonObject.get("username").getAsString();
        System.out.println("In SongManager, username: "+username);
                
        try
        {
            FileWriter writer = new FileWriter("data\\users\\"+username+".json");
            writer.write(jsonUser);
            writer.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
            
}
