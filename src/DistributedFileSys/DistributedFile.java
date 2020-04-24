/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DistributedFileSys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 018639476
 */
public class DistributedFile implements Serializable{
    String filename;
    int numberOfPages;
    int pageSize; //max size of each page in file
    long size; // file size
    List<Page> pages;

    public DistributedFile(String filename, int numberOfPages, int pageSize, long size, ArrayList<Page> pages) {
        this.filename = filename;
        this.numberOfPages = numberOfPages;
        this.pageSize = pageSize;
        this.size = size;
        this.pages = pages;
    }
    
    public DistributedFile(String filename) {
        this(filename, 0, 1024, 0, new ArrayList<Page>());
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
    
    
    @Override
    public String toString()
    {
        return "filename: " + filename + "\nnumberOfPages:  " + numberOfPages + "\npageSize: " + pageSize + "\nsize: " + size;
    }
    
}
