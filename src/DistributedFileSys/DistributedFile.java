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
    List<Page> pages;

    public DistributedFile(String filename, int numberOfPages, ArrayList<Page> pages) {
        this.filename = filename;
        this.numberOfPages = numberOfPages;
        this.pages = pages;
    }
    
    public DistributedFile(String filename) {
        this(filename, 0, new ArrayList<Page>());
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

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
    
    void addPage(Page page)
    {
        pages.add(page);
        numberOfPages++;
    }
    
    @Override
    public String toString()
    {
        String fileStr = " filename: " + filename + '\n' + " number Of Pages:  " + numberOfPages+ '\n' + " Pages: {\n";
        for(Page p: pages)
        {
            fileStr  = fileStr + p.toString();
        }
        fileStr = fileStr + " }\n";
        return fileStr;
    }
    
}
