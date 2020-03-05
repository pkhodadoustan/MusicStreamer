/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 018639476
 */
public class Server {
    
    public static void main(String[] args)
    {
        try {
            ServerCommunicationModule serverCM = new ServerCommunicationModule();
            serverCM.start();
            System.out.println("server is running!");
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error! Server could not run. "+ex.getMessage());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error! Server could not run. "+ex.getMessage());
        }
    }
    
}
