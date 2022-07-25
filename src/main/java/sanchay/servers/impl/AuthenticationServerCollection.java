/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 *
 * @author User
 */
//public class AuthenticationServerCollection implements Runnable {
public class AuthenticationServerCollection {

//    final ExecutorService service = Executors.newCachedThreadPool();

    private final static LinkedHashMap <UUID, AuthenticationSever> serverCollection = new LinkedHashMap<UUID, AuthenticationSever>();
    private final static LinkedHashMap <UUID, Thread> serverThreadCollection = new LinkedHashMap<UUID, Thread>();
//    private volatile boolean stop;
    
    public AuthenticationServerCollection() 
    {
    }

    public void execute(UUID uuid, AuthenticationSever authServer) {
        serverCollection.put(uuid, authServer);

        System.out.println("Authentication server: " + uuid);

        SanchayServerThread thread = new SanchayServerThread(authServer);
        serverThreadCollection.put(uuid, thread);
        
        thread.start();
    }

//    public void run() {
//    }

    public static AuthenticationSever getAuthenticationSever(UUID uuid) {
        synchronized (serverCollection) {
            return serverCollection.get(uuid);
        }
    }

    public AuthenticationSever removeAuthenticationSever(UUID uuid) {
        synchronized (serverCollection) {
            Thread serverThread = serverThreadCollection.remove(uuid);
            serverThread.interrupt();
            return serverCollection.remove(uuid);
        }
    }

//    public void stop() {
//        stop = true;
//    }    
//    
//    final class AuthenticationTask implements Runnable {
//
//        @Override
//        public void run() {
//            System.out.println("Running authentication task ...");
//        }
//    };
}
