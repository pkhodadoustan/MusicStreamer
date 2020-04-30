/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DistributedFileSys;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 018639476
 */
public class CommandLine {
    final String LOCAL_PATH = "D:\\compSci\\CSULB\\Spring2020\\CECS327\\MusicStreamer\\Data\\";
    DFS dfs;

    public DFS getDfs() {
        return dfs;
    }
    
    public CommandLine(int selfPort, int joinPort) throws Exception {
        dfs = new DFS(selfPort);
        if(joinPort>0 && selfPort>0)
        {
          dfs.join("127.0.0.1", joinPort);
          dfs.peerPrint();
          TimeUnit.SECONDS.sleep(1);//to give it time to set up predecessor and successor
          /*if first peer on chord then create metdata */
          if(dfs.getChord().getId()==dfs.getChord().getSuccessorId()) //dfs.chord.getPredecessor()==null
          {
            System.out.println("Metadata being created ...");
            dfs.createMetadata();
          }
        }
    }
    
    public void cmdUserInterface() {
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            printUserMenu();
            String option = bufferReader.readLine();
            while(!option.equals("end"))
            {
                if(option.equals("print"))
                    dfs.peerPrint();
                if(option.equals("ls"))
                    System.out.println(dfs.ls());
                if(option.equals("append"))
                {
                    System.out.println("Enter file name : ");
                    String fileName = bufferReader.readLine();
                    
                    System.out.println("Enter page name : ");
                    String pageName = bufferReader.readLine();
                    
                    try{
                        dfs.append(LOCAL_PATH, pageName,fileName );
                    }
                    catch(FileNotFoundException ex)
                    {
                        printUserMenu();
                        option = bufferReader.readLine();
                    }
                }
                if(option.equals("mv"))
                {
                    System.out.println("Enter file name: ");
                    String filename = bufferReader.readLine();
                    System.out.println("Enter new name: ");
                    String newName = bufferReader.readLine();
                    dfs.mv(filename, newName);
                }
                if(option.equals("delete"))
                {
                    System.out.println("Enter file name to delete from DFS: ");
                    String filename = bufferReader.readLine();
                    dfs.delete(filename);
                }
                if(option.equals("read"))
                {
                    System.out.println("Enter file name to read from DFS: ");
                    String filename = bufferReader.readLine();
                    System.out.println("Enter page number: ");
                    int page = Integer.parseInt(bufferReader.readLine());
                    String pageStr = new String(dfs.read(filename, page));
                    System.out.println("Bytes in page"+page+" of the file "+filename+": \n"+pageStr);
                }
                if(option.equals("head"))
                {
                    System.out.println("Enter file name for head: ");
                    String filename = bufferReader.readLine();
                    String head = new String(dfs.head(filename));
                    System.out.println("Bytes in head of the file "+filename+": \n"+head);
                }
                if(option.equals("tail"))
                {
                    System.out.println("Enter file name for tail: ");
                    String filename = bufferReader.readLine();
                    String tail = new String(dfs.tail(filename));
                    System.out.println("Bytes in the tail of the file "+filename+": \n"+tail);
                }
                if(option.equals("touch"))
                {
                    System.out.println("Enter file name to be created: ");
                    String filename = bufferReader.readLine();
                    dfs.touch(filename);
                }
                System.out.println("\n--------------------------------------\n");
                printUserMenu();
                option = bufferReader.readLine();
            }
        } catch (Exception ex) {
            Logger.getLogger(CommandLine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void printUserMenu() {
        System.out.println("Select from the option below: ");
        System.out.println("print\n"+"ls\n"+"read\n"+"head\n"+"tail\n"+"append\n"+"delete\n"+"mv\n"+"touch\n"+"end\n");
    }
    
    static public void run(String args[]) throws Exception
    {
        if (args.length < 2 ) {
            throw new IllegalArgumentException("Parameter: <port>");
        }
        CommandLine commandLine=new CommandLine( Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        commandLine.cmdUserInterface();
     } 
}
