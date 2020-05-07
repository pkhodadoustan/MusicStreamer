
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
    List<SongRecord> songs;
    
    public SongManager(DFS dfs) throws Exception
    {
        this.dfs = dfs;
        songs = new ArrayList<>();
    }
 
    /**
     * @param artist - String indicating the artist to search for
     * @return List of songs by the artist
     */
    public List<SongRecord> findSongByKeyword(String keyword)
    {
        Gson gson = new Gson();
        songs = new ArrayList<>();
        try {
            //songs = dfs.searchPeerSongs(artist);
            List<String> results = dfs.searchPeerSongs(keyword);
            for(String result: results)
            {
                List<SongRecord> foundSongs = new ArrayList<>(Arrays.asList(gson.fromJson(result, SongRecord[].class)));
                songs.addAll(foundSongs);
            }
            
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
        SongRecord foundSong = null;
        for(SongRecord song: songs)
        {
            if(song.getSong().getId().toLowerCase().equals(id.toLowerCase()))
            {
                foundSong = song;
                break;
            }
        }
        return foundSong;
    }
  
    //The following are not communicating with Chord yet
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
