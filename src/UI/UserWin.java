/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import com.google.gson.Gson;
import javax.swing.DefaultListModel;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import javazoom.jl.player.Player;
import Client.ClientProxy;
import musicstreamer.SongRecord;
import musicstreamer.User;
import com.google.gson.JsonObject;
import java.util.Arrays;
import Client.CECS327RemoteInputStream;

/**
 *
 * @author 018639476
 * @author 015222816
 */

class MyPlayer implements Runnable
{
    Player player;
    ClientProxy proxy;
    
    //to make the remote invokation -> Userwindow uses -> proxy uses -> communication modul contacts -> server communication module uses -> server dispatcher -> has songManager & songDistaptcher as registered object
    public MyPlayer(String songRecordId, ClientProxy proxy) {
        InputStream in = null;
        try {
            this.proxy = proxy;
            
            // It uses CECS327RemoteInputStream as InputStream to play the song
            in = new CECS327RemoteInputStream(songRecordId, proxy);
            this.player = new Player(in);
        } catch (Exception ex) {
            Logger.getLogger(MyPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(MyPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            player.play();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}

public class UserWin extends javax.swing.JFrame {
    String currentRecordId;
    Thread thread;
    User user;
    ClientProxy proxy;
    Gson gson;

    /**
     * Creates new form UserWin
     */
    public UserWin(User user, ClientProxy proxy) {
        initComponents();
        this.proxy = proxy;
        gson = new Gson();
        currentRecordId = "";
        this.user = user;
        welcomeLabel.setText("welcome "+user.getfName());
        
        try{
            //Initialize combo box
            for(int i  = 0; i<user.getPlayList().size(); i++)
            {
                PlayListComboBox.addItem("play List " + i );
            }
            
            //initialize user playlist to content of playList 0 (index = 0) of the user if exists.
            //String list of title + srtist name + id of user favorite playlist
            DefaultListModel<String> listOfFavoriteSongs = new DefaultListModel<>();
            
            if(user.getPlayList().size()>0)
            {
                for(int i = 0; i<user.getPlayList().get(0).size(); i++)
                {
                    JsonObject jsonSong = proxy.synchExecution("findSongById", user.getPlayList().get(0).get(i));
                    System.out.println("button clicked: "+jsonSong.toString());
                    SongRecord songRcord = gson.fromJson(jsonSong.get("ret").getAsString(), SongRecord.class);
                    listOfFavoriteSongs.addElement(songRcord.getSong().getTitle()+ " _ " +
                        songRcord.getArtist().getName()+" _ "+
                        songRcord.getSong().getId());
                }
            }
            
            playlist_UI.setModel(listOfFavoriteSongs);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    //function to decide to search by artist or song title
    public int searchByArtistOrName(String artistText, String titleText){
       
        //condition 1: if artist has text & title is empty
        if(!artistText.isEmpty() && titleText.isEmpty()){
            return 1;
        }
        //condition 2: if artist is empty & title has text
        else if(artistText.isEmpty() && !titleText.isEmpty()){
            return 2;
        }
        //condition 3: if artist & title has text
        else if (!artistText.isEmpty() && !titleText.isEmpty()){
           return 3; 
        }
        //condition 4: if both are empty
        else{
            return 0;
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        welcomeLabel = new javax.swing.JLabel();
        playBtn = new javax.swing.JButton();
        stopBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        songNameLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        KeywordText = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        WarningText = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        SongList = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        playlist_UI = new javax.swing.JList<>();
        savePlaylistBtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        artistLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        termsLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        durationLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        releaseNameLabel = new javax.swing.JLabel();
        DeletFromPlaylistBtn = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        PlayListComboBox = new javax.swing.JComboBox<>();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        welcomeLabel.setText("Welcome");

        playBtn.setText("Play");
        playBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playBtnActionPerformed(evt);
            }
        });

        stopBtn.setText("Stop");
        stopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopBtnActionPerformed(evt);
            }
        });

        jLabel1.setText("Song Name");

        songNameLabel.setText("-");

        jLabel2.setText("Search Songs");

        searchBtn.setText("Search");
        searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchBtnMouseClicked(evt);
            }
        });

        WarningText.setText("-");

        SongList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SongListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(SongList);

        jButton1.setText("Add to Playlist");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        playlist_UI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playlist_UIMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(playlist_UI);

        savePlaylistBtn.setText("Save Playlist");
        savePlaylistBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                savePlaylistBtnMouseClicked(evt);
            }
        });

        jLabel4.setText("Artist Name");

        artistLabel.setText("-");

        jLabel6.setText("Terms");

        termsLabel.setText("-");

        jLabel5.setText("Duration");

        durationLabel.setText("-");

        jLabel7.setText("Release Name");

        releaseNameLabel.setText("-");

        DeletFromPlaylistBtn.setText("Delete");
        DeletFromPlaylistBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DeletFromPlaylistBtnMouseClicked(evt);
            }
        });

        jLabel8.setText("Your Playlists");

        jLabel9.setText("Search Reuslts");

        PlayListComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                PlayListComboBoxItemStateChanged(evt);
            }
        });
        PlayListComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PlayListComboBoxMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(KeywordText, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(WarningText, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(welcomeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(playBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(stopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel7))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(artistLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                                            .addComponent(termsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(durationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(releaseNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(35, 35, 35)
                                        .addComponent(songNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 13, Short.MAX_VALUE)))
                        .addGap(32, 32, 32)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(42, 42, 42)
                        .addComponent(PlayListComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(98, 98, 98))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(savePlaylistBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DeletFromPlaylistBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(161, 161, 161))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(235, 235, 235))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(welcomeLabel)
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(KeywordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(62, 62, 62)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchBtn)
                            .addComponent(WarningText))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(songNameLabel)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(artistLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(releaseNameLabel)
                    .addComponent(jLabel8)
                    .addComponent(PlayListComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(termsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(durationLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(playBtn)
                            .addComponent(stopBtn)
                            .addComponent(jButton1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(savePlaylistBtn)
                    .addComponent(DeletFromPlaylistBtn))
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnMouseClicked

        String keyword = KeywordText.getText();
            
        while (true){
            WarningText.setText("Searching...");
             // implement findsongby name
            List <SongRecord> recordList;
            try {
                //find a list of songs by artist name
                JsonObject jsonSongListByArtist = proxy.synchExecution("findSongByKeyword", keyword);
                SongRecord[] SongListByArtistArr = gson.fromJson(jsonSongListByArtist.get("ret").getAsString(), SongRecord[].class);
                recordList = Arrays.asList(SongListByArtistArr);

                //for loop to generate multiple songs
                DefaultListModel<String> listOfSongs = new DefaultListModel<>();
                for(int i = 0; i < recordList.size(); i++){
                    listOfSongs.addElement(recordList.get(i).getSong().getTitle()+" _ "+recordList.get(i).getSong().getId());
                }
                SongList.setModel(listOfSongs);
                break;
            } catch (Exception ex) {
                Logger.getLogger(UserWin.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        WarningText.setText("");
    }//GEN-LAST:event_searchBtnMouseClicked

    private void playBtnActionPerformed(java.awt.event.ActionEvent evt) {                                        
        // TODO add your handling code here:
        if(thread!=null)
        {
            thread.stop();
            thread = null;
        }
        try{
            if(currentRecordId!=null && !currentRecordId.equals(""))
            {
                System.out.println("in userWin play button, id is: " + currentRecordId);
                MyPlayer myPlayer = new MyPlayer(currentRecordId, proxy);
                thread = new Thread(myPlayer);
                thread.start();
            }
        }
        catch(Exception e)
        {
            System.out.println("in UserWin, play button: " + e.getMessage());
        }
    }

    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopBtnActionPerformed
        // TODO add your handling code here:
        try{
                thread.stop();
                thread = null;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_stopBtnActionPerformed

    //add to playList button
    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        
        //get index of selected p;aylist from combobox
        int selectedPlaylistIndex = PlayListComboBox.getSelectedIndex();
        
        //get the song id of the song and add it to corresponding user playlist
        String song = SongList.getSelectedValue();
        if(song!=null && !song.equals(""))
        {
            String[] attrs = song.split(" _ ");
            String id = attrs[1];
            user.addSongToPlayList(id, selectedPlaylistIndex);
        }  
        
        //displaying song info in playlist for each song id record in the corresponding playlist
        DefaultListModel<String> listOfFavoriteSongs = new DefaultListModel<>();
        
        for(int i = 0; i<user.getPlayList().get(selectedPlaylistIndex).size(); i++)
        {
            try {
                String songId = user.getPlayList().get(selectedPlaylistIndex).get(i);
                JsonObject jsonSong = proxy.synchExecution("findSongById", songId);
                System.out.println("button clicked: "+jsonSong.toString());
                SongRecord songRcord = gson.fromJson(jsonSong.get("ret").getAsString(), SongRecord.class);
                listOfFavoriteSongs.addElement(songRcord.getSong().getTitle()+ " _ " +
                        songRcord.getArtist().getName()+" _ "+
                        songRcord.getSong().getId());
            } catch (Exception ex) {
                Logger.getLogger(UserWin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        playlist_UI.setModel(listOfFavoriteSongs);

    }//GEN-LAST:event_jButton1MouseClicked

    private void savePlaylistBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_savePlaylistBtnMouseClicked
        try {
            // TODO add your handling code here:
            System.out.println("Saving the user changes to database on server ...");
            JsonObject jsonBoolSaved = proxy.synchExecution("saveUser", user);
            Boolean saved = gson.fromJson(jsonBoolSaved.get("ret").getAsString(), Boolean.class);
            if(saved)
                System.out.println("Successfully saved user playlist.");
            else
                System.out.println("Error! Could not save playlist!");
        } catch (IOException ex) {
            Logger.getLogger(UserWin.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error! Could not save playlist: "+ex.getMessage());
        }
    }//GEN-LAST:event_savePlaylistBtnMouseClicked

    private void SongListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SongListMouseClicked
        try {
            // TODO add your handling code here:
            String songarrts = SongList.getSelectedValue();
            String[] attrs = songarrts.split(" _ ");
            currentRecordId = attrs[1];
            //SongRecord sr = sm.findSongById(currentRecordId);
            
            JsonObject jsonSong = proxy.synchExecution("findSongById", currentRecordId);
            SongRecord sr = gson.fromJson(jsonSong.get("ret").getAsString(), SongRecord.class);
            songNameLabel.setText(sr.getSong().getTitle());
            artistLabel.setText(sr.getArtist().getName());
            releaseNameLabel.setText(sr.getRelease().getName());
            termsLabel.setText(sr.getArtist().getTerms());  
            durationLabel.setText((sr.getSong().getDuration()/60)+" minutes");
        } catch (Exception ex) {
            Logger.getLogger(UserWin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_SongListMouseClicked

    private void playlist_UIMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playlist_UIMouseClicked
        try {
            // TODO add your handling code here:
            
            String songarrts = playlist_UI.getSelectedValue();
            String[] attrs = songarrts.split(" _ ");
            currentRecordId = attrs[2];
            JsonObject jsonSong = proxy.synchExecution("findSongById", currentRecordId);
            SongRecord sr = gson.fromJson(jsonSong.get("ret").getAsString(), SongRecord.class);
            songNameLabel.setText(sr.getSong().getTitle());
            artistLabel.setText(sr.getArtist().getName());
            releaseNameLabel.setText(sr.getRelease().getName());
            termsLabel.setText(sr.getArtist().getTerms());
            durationLabel.setText((sr.getSong().getDuration()/60)+" minutes");
        } catch (Exception ex) {
            Logger.getLogger(UserWin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_playlist_UIMouseClicked

    private void DeletFromPlaylistBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeletFromPlaylistBtnMouseClicked
        // TODO add your handling code here:
        int index = PlayListComboBox.getSelectedIndex();
//        user.deletePlaylist(index);
//        PlayListComboBox.removeItemAt(index);
        //delete all items in combo box
        PlayListComboBox.removeAllItems();
        
        //delete plalist from user
        user.deletePlaylist(index);
        
        //delete UI playlist
//        DefaultListModel<String> listOfFavoriteSongs = new DefaultListModel<>();
//        playlist_UI.setModel(listOfFavoriteSongs); 

        // update comboBox and playlist
        try{
            //Initialize combo box
            for(int i  = 0; i<user.getPlayList().size(); i++)
            {
                PlayListComboBox.addItem("play List " + i );
            }
            
            //initialize user playlist to content of playList 0 (index = 0) of the user if exists.
            //String list of title + srtist name + id of user favorite playlist
            DefaultListModel<String> listOfFavoriteSongs = new DefaultListModel<>();
            
            if(user.getPlayList().size()>0)
            {
                for(int i = 0; i<user.getPlayList().get(0).size(); i++)
                {
                    JsonObject jsonSong = proxy.synchExecution("findSongById", user.getPlayList().get(0).get(i));
                    System.out.println("button clicked: "+jsonSong.toString());
                    SongRecord songRcord = gson.fromJson(jsonSong.get("ret").getAsString(), SongRecord.class);
                    listOfFavoriteSongs.addElement(songRcord.getSong().getTitle()+ " _ " +
                        songRcord.getArtist().getName()+" _ "+
                        songRcord.getSong().getId());
                }
            }
            
            playlist_UI.setModel(listOfFavoriteSongs);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_DeletFromPlaylistBtnMouseClicked

    private void PlayListComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlayListComboBoxMouseClicked
     
    }//GEN-LAST:event_PlayListComboBoxMouseClicked

    private void PlayListComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_PlayListComboBoxItemStateChanged
   // TODO add your handling code here:
        //update the content of playList UI based on the current selected item of combobox
        
        int currentItemIndex = PlayListComboBox.getSelectedIndex();
        if(currentItemIndex<0)//if comboBox items are deleted, the above would return -1
            return; 
        //create a model for update/show
        DefaultListModel<String> listOfFavoriteSongs = new DefaultListModel<>();
        
        //if user has a playlist
        if(user.getPlayList().size()>0)
        {
            //populate the list-model if the playlist for currentItemIndex exists.
            if(user.getPlayList().size()>0)
            {
                for(int i = 0; i<user.getPlayList().get(currentItemIndex).size(); i++)
                {
                    try {
                        JsonObject jsonSong = proxy.synchExecution("findSongById", user.getPlayList().get(currentItemIndex).get(i));
                        System.out.println("button clicked: "+jsonSong.toString());
                        SongRecord songRcord = gson.fromJson(jsonSong.get("ret").getAsString(), SongRecord.class);
                        listOfFavoriteSongs.addElement(songRcord.getSong().getTitle()+ " _ " +
                                songRcord.getArtist().getName()+" _ "+
                                songRcord.getSong().getId());
                    } catch (IOException ex) {
                        Logger.getLogger(UserWin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            //display the updated/created list-model
            playlist_UI.setModel(listOfFavoriteSongs); 
        }        // TODO add your handling code here:
    }//GEN-LAST:event_PlayListComboBoxItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserWin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserWin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserWin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserWin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new UserWin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DeletFromPlaylistBtn;
    private javax.swing.JTextField KeywordText;
    private javax.swing.JComboBox<String> PlayListComboBox;
    private javax.swing.JList<String> SongList;
    private javax.swing.JLabel WarningText;
    private javax.swing.JLabel artistLabel;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton playBtn;
    private javax.swing.JList<String> playlist_UI;
    private javax.swing.JLabel releaseNameLabel;
    private javax.swing.JButton savePlaylistBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JLabel songNameLabel;
    private javax.swing.JButton stopBtn;
    private javax.swing.JLabel termsLabel;
    private javax.swing.JLabel welcomeLabel;
    // End of variables declaration//GEN-END:variables
}
