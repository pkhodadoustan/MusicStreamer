/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import UI.Login;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import musicstreamer.SongRecord;
import musicstreamer.User;

/**
 *
 * @author 018639476
 */
public class Client {
    
    public static void main(String[] args)
    {
        
        try{
            Gson gson = new Gson();
            
            //creating ClientProxy object to communicate with server through ClientCommunicationModule
            ClientProxy proxy = new ClientProxy();
            
            //test get user
            JsonObject jsonUser = proxy.synchExecution("getUser", "12345");
            User user = gson.fromJson(jsonUser.get("ret").getAsString(), User.class);
            System.out.println("testing getUser: "+ user.getfName());
                    
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
            
            //creating user window
            Login loginWin  = new Login(proxy);
            loginWin.setVisible(true);
            System.out.println("New Client is Running!");
        }
        catch(Exception e)
        {
            System.out.println("Error! New Client failed. " + e.getMessage());
        }
    }
}
