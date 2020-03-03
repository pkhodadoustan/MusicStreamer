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
public class Release {
    int id;
    String name;

    public Release(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void print()
    {
        System.out.println("name: "+name+", id: "+id );
    }
}
