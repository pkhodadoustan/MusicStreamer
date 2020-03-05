/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 *
 * @author Pardis Khodadoustan
 */
public class ClientProxy{
    ClientCommunicationModule cm;
    List<JsonObject> catalogMethods; //list of remote methods in catalog.json file
    Gson gson;
    
    public ClientProxy() throws SocketException, UnknownHostException, FileNotFoundException {
        cm = new ClientCommunicationModule();
        gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("data\\catalog.json"));
        JsonObject[] data = gson.fromJson(reader, JsonObject[].class);
        catalogMethods = Arrays.asList(data);
        //System.out.println("in Proxy construction: "+ catalogMethods.get(0).toString());
    }
    
    public JsonObject synchExecution(String remoteMethod, Object... param) throws IOException
    {
        JsonObject jsonMethod = new JsonObject();
        
       for(JsonObject method: catalogMethods)
       {
           String catalogRemoteMethod = method.get("remoteMethod").getAsString();
           //System.out.println("catalog mthod: "+catalogRemoteMethod+", and called method: "+remoteMethod);
           if(catalogRemoteMethod.equals(remoteMethod))
           {
               JsonObject jsonParam = new JsonObject(); //to hold the parameters of the remote methos
               jsonMethod.addProperty("remoteMethod", remoteMethod);
               jsonMethod.addProperty("objectName", method.get("objectName").getAsString());
               System.out.println("in proxy: method found, "+jsonMethod.toString());
               int i = 0;
               for(Object arg: param)
               {
                   jsonParam.addProperty("param"+i, arg.toString());
                   //System.out.println("in sync param: "+jsonParam.toString());
                   i++;
               }
               jsonMethod.add("param", jsonParam);
               System.out.println("in sync proxy, method is: "+jsonMethod.toString());
               break;
           }
       }
       
       //send the request to server side through client communication module
        String reply = cm.sendMessage(jsonMethod.toString());
        //unmarshalling
        JsonObject replyJsonObject = gson.fromJson(reply, JsonObject.class);
        System.out.println("reply in sync:"+replyJsonObject.toString());
        
       return replyJsonObject;
    }
}
