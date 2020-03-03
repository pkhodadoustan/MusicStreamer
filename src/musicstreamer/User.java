/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 *@author 018639476
 * @author kareena chan
 */
public class User //implements Serializable
{
    String fName;
    String lName;
    String username;//should be unique for each user
    String password;
    List<List<String>> playLists; //list of user's favorite song ids

    public User(String fName, String lName, String username, String password) {
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.password = password;
        playLists = new ArrayList<>();
    }
    
    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
        public List<List<String>> getPlayList() {
        return playLists;
    }

    public void setPlayList(List<List<String>> playLists) {
        this.playLists = playLists;
    }
    
    public void addSongToPlayList(String id, int playListIndex){
        if(!playLists.get(playListIndex).contains(id))//no duplicate
            playLists.get(playListIndex).add(id);
    }
    
    public void removeSongFromPlayList(String id, int playListIndex) 
    {
        for(int i = 0; i<playLists.get(playListIndex).size(); i++)
        {
            if(playLists.get(playListIndex).get(i).equals(id))
            {
                playLists.get(playListIndex).remove(i);
                break;
            }
        }
    }
    
    public void addPlayList()
    {
        playLists.add(new ArrayList<String>());
    }
    
    public void deletePlaylist(int playListIndex){
        playLists.remove(playListIndex);
    }
    
    public String toJson()
    {
        //converting User object to Json
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
    
    public boolean saveUser() 
    {
        try{
            String userJson = this.toJson();
            FileWriter writer = new FileWriter("data\\users\\"+this.username+".json");
            writer.write(userJson);
            writer.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        
    }

}
