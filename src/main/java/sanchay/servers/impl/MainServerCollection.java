/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author User
 */
//public class MainServerCollection implements Runnable {
public class MainServerCollection {

//    final ExecutorService service = Executors.newCachedThreadPool();
    private final static LinkedHashMap <UUID, SanchayMainServer> serverCollection = new LinkedHashMap<UUID, SanchayMainServer>();
    private final static LinkedHashMap <UUID, Thread> serverThreadCollection = new LinkedHashMap<UUID, Thread>();
//    private volatile boolean stop;
    
    public MainServerCollection() 
    {
    }

    public void execute(UUID uuid, SanchayMainServer mainServer) {
        serverCollection.put(uuid, mainServer);

        System.out.println("Sanchay Main server: " + uuid);

        SanchayServerThread thread = new SanchayServerThread(mainServer);
        serverThreadCollection.put(uuid, thread);
        
        thread.start();
    }
//
//    public void run() {
//    }

    public static SanchayMainServer getAuthenticationSever(UUID uuid) {
        synchronized (serverCollection) {
            return serverCollection.get(uuid);
        }
    }

    public SanchayMainServer removeAuthenticationSever(UUID uuid) {
        synchronized (serverCollection) {
            Thread serverThread = serverThreadCollection.remove(uuid);
            serverThread.interrupt();
            return serverCollection.remove(uuid);
        }
    }

//    public void stop() {
//        stop = true;
//    }        
    
//    final class MainServerTask implements Runnable {
//
//        @Override
//        public void run() {
//            System.out.println("Running main server task ...");
//        }
//    };
}
