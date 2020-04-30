/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import DistributedFileSys.DFS;
import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import musicstreamer.SongRecord;

/**
 *
 * @author 018639476
 */
public class searchDFS implements Runnable{
    List<SongRecord> songsFound;
    int filePageNumber;
    String fileName;
    DFS dfs;
    String artistKeyword;
    String titleKeyword;
    String id;
    
    public searchDFS(int filePageNumber, String fileName, DFS dfs, String artistKeyword, String titleKeyword, String id, List<SongRecord> recordsFound) {
        this.filePageNumber = filePageNumber;
        this.fileName = fileName;
        this.songsFound = recordsFound;
        this.dfs = dfs;
        this.artistKeyword =  artistKeyword;
        this.titleKeyword = titleKeyword;
        this.id = id;
    }
    
    @Override
    public void run() {
        try {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            Gson gson = new Gson();
            byte[] fileConetentBytes = dfs.read(fileName, filePageNumber);
            String filesStr = new String(fileConetentBytes);
            SongRecord[] records = gson.fromJson(filesStr, SongRecord[].class);
            
            if(!id.equals("")) {
                for(SongRecord record: records)
                {
                    if (record.getSong().getId().equals(id)) {
                        songsFound.add(record);
                        System.out.println("record: " + record.getSong().getTitle());
                    }
                }
            } else if(!artistKeyword.equals("") && titleKeyword.equals("")) {
                for(SongRecord record: records)
                {
                    if(record.getArtist().getName().toLowerCase().contains(artistKeyword.toLowerCase())) {
                        songsFound.add(record);
                        System.out.println("record: " + record.getSong().getTitle());
                    }
                }
            } else if(artistKeyword.equals("") && !titleKeyword.equals("")) {
                for(SongRecord record: records)
                {
                    if(record.getSong().getTitle().toLowerCase().contains(titleKeyword.toLowerCase())) {
                        songsFound.add(record);
                        System.out.println("record: " + record.getSong().getTitle());
                    }
                }
            } else if(!artistKeyword.equals("") && !titleKeyword.equals("")) {
                for(SongRecord record: records)
                {
                    if (record.getSong().getTitle().toLowerCase().contains(titleKeyword.toLowerCase()) ||
                        record.getArtist().getName().toLowerCase().contains(artistKeyword.toLowerCase())) {
                        songsFound.add(record);
                        System.out.println("record: " + record.getSong().getTitle());
                    }
                }
            } 
        } catch (Exception ex) {
            Logger.getLogger(searchDFS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
