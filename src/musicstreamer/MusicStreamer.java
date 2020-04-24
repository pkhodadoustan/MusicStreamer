/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.player.*;
import javazoom.jl.decoder.JavaLayerException;
import UI.Login;
import com.google.gson.JsonObject;
import DistributedFileSys.CommandLine;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 018639476
 */
public class MusicStreamer {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CommandLine.main(args);
        } catch (Exception ex) {
            Logger.getLogger(MusicStreamer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
}
