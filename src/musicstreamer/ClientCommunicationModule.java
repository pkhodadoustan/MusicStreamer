/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author 018639476
 */
public class ClientCommunicationModule{
    //Assume Call Semantic is "Maybe"
    
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;
    public ClientCommunicationModule() throws SocketException, UnknownHostException {
        
        //clinet uses any arbitrary socket
        socket = new DatagramSocket(); 
        address = InetAddress.getByName("localhost");
    }
    public String sendMessage(String msg) throws IOException {
        //converting String msg to buffer
        System.out.println("in client cm msg: "+msg);
        System.out.println("in client cm, msg bytes: "+msg.getBytes().length);
        buf = msg.getBytes();
        System.out.println("in client cm, buffer size: "+buf.length);
        
        //creating a sending packet to the port number of the server that has the address of the client
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        System.out.println("in client cm, packet length: "+packet.getLength());
        socket.send(packet);  
        //System.out.println("client comm module message sent!");
        //convert sending packet immidiatly after send to recieving packet
        buf = new byte[32768];
        packet = new DatagramPacket(buf, buf.length); 
        socket.receive(packet);
        System.out.println("in clinet Communication module, packet received: "+packet.toString());
        
        //converting data in buffer to String
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }
    public void close() {
        socket.close();
    }
    
}
