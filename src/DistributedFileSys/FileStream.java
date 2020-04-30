/**
* RemoteInputFileStream Implements an Input Stream for big
* files. It creates a server and return the address
* The client must call connect() before reading
*
* @author  Oscar Morales-Ponce
* @author Pardis Khodadoustan
* @version 0.16
* @since   03-3-2019
*/

package DistributedFileSys;

import java.io.*;
import java.nio.*;

/**
 *
 * @author 018639476
 */
public class FileStream extends InputStream implements Serializable {
   
    private int currentPosition;
    private byte[] byteBuffer;
    private int size;
    public  FileStream(String pathName) throws FileNotFoundException, IOException    {
      File file = new File(pathName);
      size = (int)file.length();
      byteBuffer = new byte[size];
      
      FileInputStream fileInputStream = new FileInputStream(pathName);
      int i = 0;
      while (fileInputStream.available()> 0)
      {
	byteBuffer[i++] = (byte)fileInputStream.read();
      }
      fileInputStream.close();
      currentPosition = 0;	  
    }
    
    public  FileStream() throws FileNotFoundException    {
      currentPosition = 0;	  
    }
    
    public  FileStream(byte[] sequence) throws FileNotFoundException    { 
      currentPosition = 0;
      size = sequence.length;
      byteBuffer = new byte[size];
      for(int i = 0; i<size; i++)
          byteBuffer[i] = sequence[i];
    }
    
    public int read() throws IOException //reads the data already inside the buffer one byte at a time
    {
 	if (currentPosition < size)
 	  return (int)byteBuffer[currentPosition++];
 	return 0;
    }

    
    public int available() throws IOException
    {
	return size - currentPosition;
    }
    
    public int getSize()
    {
        return size;
    }

}
