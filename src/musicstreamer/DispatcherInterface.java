/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;

/**
* @author  Oscar Morales-Ponce
* @version 0.15
* @since   02-11-2019 
 */

public interface DispatcherInterface {
/*
* Executes a remote method.
* @param request is a Json string send by the ProxyInterface
{
    "remoteMethod":"getSongChunk",
    "objectName":"SongServices",
    "param":
      {
          "song":490183,
          "fragment":2
      }
}
* To execute a method it does
* ListOfObjects["SongServices"].class.getMethod("getSongChunk").invoke(songId, 2);
*/
    public String dispatch(String request);
/*
* Register the objects and methods that the dispatcher supports.
* It inserts remoteObject into a hash map of objects. For example,
* hashMap<String, Object> ListOfObjects 
* @param request is a Json string send by the ProxyInterface
* ListOfObjects["SongServices"].put(objectName, remoteObject)
*/
    public void registerObject(Object remoteMethod, String objectName);
    
}
