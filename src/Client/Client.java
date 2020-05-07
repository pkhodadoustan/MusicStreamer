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
