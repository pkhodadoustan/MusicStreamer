/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
* The Dispatcher implements DispatcherInterface. 
*
* @author  Oscar Morales-Ponce
* @Pardis Khodadoustan
* @version 0.15
* @since   02-11-2019 
*/

import java.util.HashMap;
import java.util.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerDispatcher implements DispatcherInterface{
        HashMap<String, Object> ListOfObjects;
    

    public ServerDispatcher()
    {
        ListOfObjects = new HashMap<String, Object>();
    }
    
    /* 
    * dispatch: Executes the remote method in the corresponding Object
    * @param request: Request: it is a Json file
    {
        "remoteMethod":"getSongChunk", //findById
        "objectName":"SongServices",   //SongManager
        "param":
          {
              "song":490183,            //Id
              "fragment":2
          }
    }
    */
    public String dispatch(String request)
    {
        JsonObject jsonReturn = new JsonObject();
        JsonParser parser = new JsonParser();
        JsonObject jsonRequest = parser.parse(request).getAsJsonObject();
        System.out.println("in server dispatcher, jsonRequest: "+ jsonRequest);
        //String returnMsg = "";
        
        try {
            // Obtains the object pointing to SongServices
            System.out.println("in dispatcher: "+jsonRequest.get("objectName").getAsString());
            Object object = ListOfObjects.get(jsonRequest.get("objectName").getAsString());
            System.out.println("in ServerDispatcher, object retreived: "+object.toString());
            Method[] methods = object.getClass().getMethods();
            System.out.println("in ServerDispatcher, methods length: "+methods.length);
            Method method = null;
            // Obtains the method
            for (int i=0; i<methods.length; i++)
            {   
                if (methods[i].getName().equals(jsonRequest.get("remoteMethod").getAsString()))
                    method = methods[i];
            }
            if (method == null)
            {
                jsonReturn.addProperty("error", "Method does not exist");
                return jsonReturn.toString();
            }
            // Prepare the  parameters 
            Class[] types =  method.getParameterTypes();
            Object[] parameter = new Object[types.length];
            String[] strParam = new String[types.length];
            JsonObject jsonParam = jsonRequest.get("param").getAsJsonObject();
            int j = 0;
            for (Map.Entry<String, JsonElement>  entry  :  jsonParam.entrySet())
            {
                strParam[j++] = entry.getValue().getAsString();
            }
            // Prepare parameters
            for (int i=0; i<types.length; i++)
            {
                switch (types[i].getCanonicalName())
                {
                    case "java.lang.Long":
                        parameter[i] =  Long.parseLong(strParam[i]);
                        break;
                    case "java.lang.Integer":
                        parameter[i] =  Integer.parseInt(strParam[i]);
                        break;
                    case "java.lang.String":
                        parameter[i] = new String(strParam[i]);
                        break;
                }
            }
            // Prepare the return
            Class returnType = method.getReturnType();
            String ret = "";
            switch (returnType.getCanonicalName())
                {
                    case "java.lang.Long":
                        ret = method.invoke(object, parameter).toString();
                        break;
                    case "java.lang.Integer":
                        ret = method.invoke(object, parameter).toString();
                        System.out.println("in server dispatcher, found return type Integer, ret: "+ret);
                        break;
                    case "java.lang.String":
                        ret = (String)method.invoke(object, parameter);
                        break;
                    case "musicstreamer.SongRecord":
                        ret = method.invoke(object, parameter).toString();
                        break;
                    case "java.util.List":
                        Gson gson = new Gson();
                        ret = gson.toJson(method.invoke(object, parameter));
                        System.out.println("ret in ServerDispatcher: "+ret);
                        break;
                }
                jsonReturn.addProperty("ret", ret);
                System.out.println("in server dispatcher, jsonReturn: "+jsonReturn.toString());
                //returnMsg = ret;
   
        } catch ( IllegalAccessException e)
        {
        //    System.out.println(e);
            System.out.println("in Server Dispatcher. Error: "+ e.getMessage());
            System.out.println("IllegalAccessException");
            jsonReturn.addProperty("error", "Error on " + jsonRequest.get("objectName").getAsString() + "." + jsonRequest.get("remoteMethod").getAsString());
        }
        catch (InvocationTargetException e)
        {
            System.out.println("InvocationTargetException");
            System.out.println("in Server Dispatcher. Error: "+ e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println("In ServerDispatcher, Error: "+e.getMessage());
        }
     
        return jsonReturn.toString();
    }

    /* 
    * registerObject: It register the objects that handle the request
    * @param remoteMethod: It is the name of the method that 
    *  objectName implements. 
    * @objectName: It is the main class that contaions the remote methods
    * each object can contain several remote methods
    */
    public void registerObject(Object remoteMethod, String objectName)
    {
        ListOfObjects.put(objectName, remoteMethod);
    }
 
}
