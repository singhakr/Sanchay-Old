/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sanchay.servers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
import sanchay.servers.impl.SanchayMainServer;
import sanchay.servers.impl.SessionException;

/**
 *
 * @author User
 */
public interface SanchayLauncherSessionRI extends Remote {

    public SanchayMainServer getSanchayMainServerInstance(UUID sessionId) throws SessionException, RemoteException;

    public void logout(UUID sessionId) throws RemoteException;    
    
}
