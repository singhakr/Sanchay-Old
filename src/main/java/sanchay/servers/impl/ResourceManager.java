/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.servers.ResourceManagerRI;

/**
 *
 * @author User
 */
public class ResourceManager extends SanchayServer
    implements ResourceManagerRI, Serializable {

    private final SanchayMainServer sanchayMainServer;

    public ResourceManager(String propManPath, String cs, SanchayMainServer sanchayMainServer) throws RemoteException, IOException {
        super(propManPath, cs);
        this.sanchayMainServer = sanchayMainServer;
    }
    
    public final static ResourceManager getResourceManagerServerInstance(String propManPath, String cs, SanchayMainServer sanchayMainServer)
    {
        ResourceManager resourceManagerServer = null;
        
        try {
            resourceManagerServer = new ResourceManager(propManPath, cs, sanchayMainServer);
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return resourceManagerServer;
    }
    
}
