/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;

/**
* The CECS327RemoteInputStream extends InputStream class. The class implements 
* markers that are used in AudioInputStream
*
* @author  Oscar Morales-Ponce
* @version 0.15
* @since   2019-01-24 
*/


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import com.google.gson.JsonObject;
import java.util.concurrent.Semaphore; 
import java.util.logging.Level;
import java.util.logging.Logger;
public class CECS327RemoteInputStream extends InputStream{
        /**
    * Total number of bytes in the file
    */
    protected int total = 0;
    /**
    * Marker
    */
    protected int mark = 0;
    /**
    * Current reading position
    */
    protected int pos = 0;
     /**
     * It stores a buffer with FRAGMENT_SIZE bytes for the current reading. 
     * This variable is useful for UDP sockets. Thus bur is the datagram
     */
    protected byte buf[];
    /**
     * It prepares for the nuext buffer. In UDP sockets you can read nextbufer 
     * while buf is in use
     */
    protected byte nextBuf[];
     /**
     * It is used to read the buffer
     */
    protected int fragment = 0;
    protected static final int FRAGMENT_SIZE = 8192;
     /**
     * File name to stream
     */
    protected String fileName;
    /**
    * Instance of an implementation of proxyInterface
    */
    protected ClientProxy proxy;
    
    Semaphore sem; 


    /**
     * Constructor of the class. Initialize the variables and reads the first 
     * frament in nextBuf
     * @param fileName The name of the file
     * @param proxy The proxy object for remote method call on server
    */
    public CECS327RemoteInputStream(String fileName, ClientProxy proxy) throws IOException {
        sem = new Semaphore(1); 
        try
        {
            sem.acquire(); 
        } catch (InterruptedException exc) { 
            System.out.println(exc);
        }
        this.proxy = proxy;
        this.fileName = fileName;        
        this.buf  = new byte[FRAGMENT_SIZE];	
        this.nextBuf  = new byte[FRAGMENT_SIZE];
        System.out.println("in remoteInputStream before calling sync method of proxy");
        JsonObject jsonRet = proxy.synchExecution("getFileSize", fileName);
        System.out.println("in remoteInputStream after calling sync method of proxy: "+jsonRet.toString());
        this.total = Integer.parseInt(jsonRet.get("ret").getAsString());
        System.out.println("in remteInputStream, total file size: "+total);
        getBuff(fragment);
        fragment++;
     }

    /**
     * getNextBuff reads the buffer. It gets the data using
     * the remote method getSongChunk
    */
    protected void getBuff(int fragment) throws IOException
    {
        new Thread()
        {
            public void run() {
             
                try {
                    //JsonObject jsonRet = proxy.synchExecution("getSongChunk", fileName, fileName, fragment);
                    JsonObject jsonRet = proxy.synchExecution("getSongChunk", fileName, fragment);
                    String s = jsonRet.get("ret").getAsString();
                    nextBuf = Base64.getDecoder().decode(s);
                    sem.release();
                    System.out.println("Read buffer");
                } catch (IOException ex) {
                    Logger.getLogger(CECS327RemoteInputStream.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
       
     }

    /**
     * Reads the next byte of data from the input stream.
    */
    @Override
    public synchronized int read() throws IOException {
     
     
	  if (pos >= total) 
	  {	
            pos = 0;      
            return -1;
	  }
	  int posmod = pos % FRAGMENT_SIZE;
	  if (posmod == 0)
	  {
          try
          {
            sem.acquire(); 
          }catch (InterruptedException exc) 
          { 
                System.out.println(exc);
          }
	      for (int i=0; i< FRAGMENT_SIZE; i++)
		      buf[i] = nextBuf[i];
          
	      getBuff(fragment);
	      fragment++;
	  }
	  int p = pos % FRAGMENT_SIZE;
	  pos ++;
      return buf[p] & 0xff; 
    }

    /**
     * Reads some number of bytes from the input stream and stores them
     * into the buffer array b.
    */
    @Override
    public synchronized int read(byte b[], int off, int len)  throws IOException{
        if (b == null) {
            throw new NullPointerException();
	    } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
	    }

	    if (pos >= total) {
     	    return -1;
	    }
	    int avail = total - pos;
	    if (len > avail) {
		  len = avail;
	    }
	    if (len <= 0) {
		  return 0;
	    }
	    for (int i = off; i< off+len;  i++)
		    b[i] = (byte)read();
	    return len;
    }

    /**
     * Skips over and discards n bytes of data from this input stream.
    */
    @Override
    public synchronized long skip(long n) throws IOException {
        long k = total - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        fragment = (int)Math.floor(pos / FRAGMENT_SIZE);
        getBuff(fragment);
        fragment++;
        getBuff(fragment);
        return k;
    }

    /**
     * Returns an estimate of the number of bytes that can be read 
     * (or skipped over) from this input stream without blocking by 
     * the next invocation of a method for this input stream.
    */
    @Override
    public synchronized int available() {
        return total - pos;
    }

    /**
     * Tests if this input stream supports the mark and reset methods.
    */
    @Override
    public boolean markSupported() {
        return true;
    }
	
    /**
     * Marks the current position in this input stream.
    */
    @Override
    public void mark(int readAheadLimit) {
        mark = pos;
    }

    /**
     * Repositions this stream to the position at the time the 
     * mark method was last called on this input stream.
    */
    @Override
    public synchronized void reset() throws IOException {
        pos = mark;
        fragment = (int)Math.floor(pos / FRAGMENT_SIZE);
        getBuff(fragment);
        fragment++;
        getBuff(fragment);
    }
	
    /**
     * Closes this input stream and releases any system resources 
     * associated with the stream.
    */
    @Override
    public void close() throws IOException {
    }
    
}
