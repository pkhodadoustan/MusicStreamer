/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.player.*;
import javazoom.jl.decoder.JavaLayerException;
import UI.Login;
import com.google.gson.JsonObject;


/**
 *
 * @author 018639476
 */
public class MusicStreamer {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            //testing client server communication
            //running server : ServerCommunicationModule
            Gson gson = new Gson();
            ServerCommunicationModule serverCM = new ServerCommunicationModule();
            serverCM.start();
            
            //creating ClientProxy object to communicate with server through ClientCommunicationModule
            ClientProxy proxy = new ClientProxy();
            JsonObject jsonSong = proxy.synchExecution("findSongById", "SOMZWCG12A8C13C480");
            SongRecord songRes = gson.fromJson(jsonSong.get("ret").getAsString(), SongRecord.class);
            System.out.println("Song found by ID: "+songRes.getSong().getId());
            
            //find songs by artist using client server communication
            JsonObject jsonSongListByArtist = proxy.synchExecution("findSongByArtist", "Casual");
            SongRecord[] SongListByArtistArr = gson.fromJson(jsonSongListByArtist.get("ret").getAsString(), SongRecord[].class);
            List<SongRecord> SongListByArtist = Arrays.asList(SongListByArtistArr);
            System.out.println("in MusicStreamer: "+SongListByArtist.get(0).getSong().getTitle());
            
            //find songs by Title using client server communication
            JsonObject jsonSongListByTitle = proxy.synchExecution("findSongByTitle", "I Didn't Mean To");
            SongRecord[] SongListByTitleArr = gson.fromJson(jsonSongListByTitle.get("ret").getAsString(), SongRecord[].class);
            List<SongRecord> SongListByTitle = Arrays.asList(SongListByTitleArr);
            System.out.println("in MusicStreamer: "+SongListByTitle.get(0).getSong().getTitle()+", "+SongListByTitle.get(0).getArtist().getName());
            
            Login loginWin  = new Login();
            loginWin.setVisible(true);
            
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
          /**
     * Play a given audio file.
     * @param audioFilePath Path of the audio file.
     */
/*
    public static Player mp3play(String file) {
        try {
            // It uses CECS327InputStream as InputStream to play the song 
             InputStream is = new CECS327InputStream(file); //you need to add (file, proxy)
             Player mp3player = new Player(is);
             //mp3player.play();
             return mp3player;
	     }
	     catch (JavaLayerException exception) 
         {
	       exception.printStackTrace();
	     }
         catch (IOException exception)
         {
             exception.printStackTrace();
         } 
        return null;
    }
*/    
}
