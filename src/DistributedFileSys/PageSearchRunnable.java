/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DistributedFileSys;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 018639476
 */
public class PageSearchRunnable implements Runnable{
    ChordMessageInterface peer;
    long pageGuid;
    String keyword;
    List<String> results; //list of string json arrays "[[], [], [], ...]"
    
        public PageSearchRunnable(ChordMessageInterface peer, long pageGuid, String keyword, List<String> results) {
        this.peer = peer;
        this.pageGuid = pageGuid;
        this.keyword = keyword;
        this.results=results;
    }

    @Override
    //run method requests the peer to look into its page with pageGuid to find the songs with keyword
    //peer will then return the found results to run
    //the run method will populate the results with searchPage results from the peer.
    public void run() {
        try {
            results.add(peer.searchPage(keyword, pageGuid));
        } catch (RemoteException ex) {
            Logger.getLogger(PageSearchRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
}
