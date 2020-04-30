/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author 018639476
 */
import DistributedFileSys.DFS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import musicstreamer.SongManager;

/**
 *
 * @author 018639476
 */
public class ServerCommunicationModule extends Thread{
    //attributes for communicating with client
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[32768];
    //attribute instance of ServerDispatcher
    ServerDispatcher dispatcher = new ServerDispatcher();
    
    //instance of Objects as remote methods for client call
    SongManager songManager;
    SongDispatcher songDispatcher;
    
    public ServerCommunicationModule() throws SocketException, FileNotFoundException, Exception {
        socket = new DatagramSocket(4445); //the number of server socket should be determined, unlike client that can be any arbitrary socket
        System.out.println("Before SongManager Created on Server-Side");
        //server joining port. infat SongManager has a dfs object for accessing data on distributed file system 
        DFS dfs = new DFS(1999);
        dfs.join("127.0.0.1", 2000);
        songManager = new SongManager(dfs);
        System.out.println("After SongManager Created on Server-Side");
        songDispatcher = new SongDispatcher();
        
        //adding object of remote mthods and its name as called in client json request to serverDispatcher
        dispatcher.registerObject(songDispatcher, "SongDispatcher"); 
        dispatcher.registerObject(songManager, "SongManager"); 
    }
    public void run() {
        running = true;
        System.out.println("buffer length in serve: "+buf.length);
        while (running) {
            //create a recieving packet
            buf = new byte[32768]; //re-allocate or flush the buffer, otherwise it will get full, and your messages will not be received.
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            System.out.println("in server cm, created packet length:"+ packet.getLength());
            try {
                //blocks until messages arrives
                //stores the message inside the packet parameter
                //stores the buffer in the message to buffer of the packet
                socket.receive(packet);
                System.out.println("in server cm, received packet length:"+ packet.getLength()+", packe data: "+packet.getData().toString());
                
            } catch (IOException ex) {
                Logger.getLogger(ServerCommunicationModule.class.getName()).log(Level.SEVERE, null, ex);
            }
            //retrieving address and port of the client from recieved message
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
/*            
            //creat a reply packet to be sent to client
            //buffer object has the data from recieved message
            packet = new DatagramPacket(buf, buf.length, address, port);
*/            
            //retrieving recieved message from recieved packet
            
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("in server cm, receieved message: "+received);
            //pass the received message to Dispatcher
            //should it wait for the return result or not because both files are on server side and it will be syncronous
            String reply = dispatcher.dispatch(received);
            
            //store the reply to buffer
            buf = reply.getBytes();
            
            //create a send packet
            packet = new DatagramPacket(buf, buf.length, address, port);
            
            if (received.equals("end")) {//here is where you can add call semantics, when to close the thread for reply
                running = false;
                continue;
            }
            try {
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(ServerCommunicationModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        socket.close();
    }
}
