/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DistributedFileSys;

import java.io.Serializable;

/**
 *
 * @author 018639476
 */
public class Page implements Serializable{
    long guid;
    int number;
    int size; 

    public Page(long guid, int number, int size) {
        this.guid = guid;
        this.number = number;
        this.size = size;
    }

    public long getGuid() {
        return guid;
    }

    public void setGuid(long guid) {
        this.guid = guid;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    @Override
    public String toString() {
        String pageStr = "  {\n" + "   Page " + number + ":\n" + "   guid: " + guid + ":\n" + "   size " + size + ":\n" + "  }\n";
        return pageStr;
    }
            
    
}
