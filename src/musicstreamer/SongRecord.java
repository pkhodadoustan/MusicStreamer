/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicstreamer;

import com.google.gson.Gson;

/**
 *
 * @author 018639476
 */
public class SongRecord {
    Release release;
    Artist artist;
    Song song;

    public SongRecord(Release release, Artist artist, Song song) {
        this.release = release;
        this.artist = artist;
        this.song = song;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        String jsonSelf = gson.toJson(this);
        return jsonSelf;
    }
    
}
