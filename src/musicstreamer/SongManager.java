
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 018639476
 * @author 015429789
 */
public class SongManager {
    List<SongRecord> dataList; //contains the list of all song-records
    HashMap <String, List<Integer>> songArtistMap; //maps the name of an artist (key) to the list of song-records' indexes by that artist (value).
    HashMap <String, List<Integer>> songTitleMap; //maps the title of a song (key) to a list of indexes that correspond to song-records in dataList with that name (value).
    HashMap <String, Integer> songIdMap;
    
    public SongManager() throws FileNotFoundException
    {
        //reading from .json file and converting a list of json strings to a list of java objects using Gson. 
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("data\\music.json"));
        SongRecord[] data = gson.fromJson(reader, SongRecord[].class);
        dataList = Arrays.asList(data);
        
        //creating the map <artist-name, song-record-index-list>
        //indeces are based on location of each songRecord in List<SongRecord> dataList
        songArtistMap = new HashMap<>();
        
        //creating the map <song-title, song-record-index-list>
        //indeces are based on location of each songRecord in List<SongRecord> dataList
        songTitleMap = new HashMap<>();
        
        //creating the map <id, song-record>
        //each song has a unique id, each id is hashed for each song
        songIdMap = new HashMap<>();
        
        //iterate through the list of songs imported from music.json then place them in songArtistMap and songTitleMap
        for(int i = 0; i<dataList.size(); i++)
        {
            //if the song's artist is already stored in songArtistMap (as a key), add the song index to the value's list
            //else add the artist as a key and index as a value to the map
            String artistKey = dataList.get(i).getArtist().getName().toLowerCase();  //get the current artist name
            if(songArtistMap.containsKey(artistKey))//if the key exists
            {
                List<Integer> indexList = songArtistMap.get(artistKey);//get the existing list (value) for the key
                indexList.add(i);//append i to existing list
                songArtistMap.put(artistKey, indexList);//replace the old list with updated list for the key in the map
            }
            else //if key does not exist
            {
                List<Integer> indexList = new ArrayList<>(); //make a new list for the key
                indexList.add(i); //add the index to the list
                songArtistMap.put(artistKey, indexList); //add the key and list (value) to the map
            }
            
            //if the song title is already a key in songTitleMap, add the song index to the value's list
            //else add the song title as a key and index as a value to the map
            String titleKey = dataList.get(i).getSong().getTitle().toLowerCase(); //get the current song title
            if(songTitleMap.containsKey(titleKey))//if the key exists
            {
                List<Integer> indexList = songTitleMap.get(titleKey);//get the existing list (value) for the key
                indexList.add(i);//append i to existing list
                songTitleMap.put(titleKey, indexList);//replace the old list with updated list for the key in the map
            }
            else //if key does not exist
            {
                List<Integer> indexList = new ArrayList<>(); //make a new list for the key
                indexList.add(i); //add the index to the list
                songTitleMap.put(titleKey, indexList); //add the key and list (value) to the map
            }
            
            String id = dataList.get(i).getSong().getId();  //get the current id
            songIdMap.put(id, i);//add new song (because each song has a unique id
            
        }
    }

    /**
     * Gets the HashMap of artists mapped to all their songs
     * @return HashMap with artist name (string) as the key and a list of songs (list of indexes from dataList) as the value
     */
    public HashMap<String, List<Integer>> getSongArtistMap() {
        return songArtistMap;
    }
    
    /**
     * Gets the HashMap of all songs with the same title
     * @return HashMap with song title (string) as the key and a list of songs (list of indexes from dataList) as the value
     */
    public HashMap<String, List<Integer>> getSongTitleMap() {
        return songTitleMap;
    }
    
    public HashMap<String, Integer> getIdMap() {
        return songIdMap;
    }
    
    /**
     * 
     * @param artist - String indicating the artist to search for
     * @return List of songs by the artist
     */
    public List<SongRecord> findSongByArtist(String artist)
    {
        List<Integer> l = songArtistMap.get(artist.toLowerCase());
        List<SongRecord> records = new ArrayList<>();
        if(l==null || l.size()==0)
        {
            System.out.println("Error! Artist not found!");
            return records;
        }
        for(Integer i:l)
        {
            records.add(dataList.get(i));
        }
        return records;
    }
    
    /**
     * 
     * @param title - String indicating the song title to search for
     * @return List of songs with the song title
     */
    public List<SongRecord> findSongByTitle(String title)
    {
        List<Integer> songIndexes = songTitleMap.get(title.toLowerCase());
        List<SongRecord> records = new ArrayList<>();
        if(songIndexes==null || songIndexes.size()==0)
        {
            System.out.println("Error! Title not found!");
            return records;
        }
        for(Integer i:songIndexes)
        {
            records.add(dataList.get(i));
        }
        return records;
    }
    
        /**
     * 
     * @param id - String indicating the song title to search for
     * @return a song record with the song id
     */
    public SongRecord findSongById(String id)
    {
        Integer songIndex = songIdMap.get(id);
        SongRecord songRecord = dataList.get(songIndex);
        //Gson gson = new Gson();
       // String jsonSongRecord = gson.toJson(songRecord);
        return songRecord;
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

/*    
    public Boolean saveUser(User user)
    {
        return user.saveUser();
    }
*/
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
