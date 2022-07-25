/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sanchay.servers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
import sanchay.servers.impl.AuthorizationException;

/**
 *
 * @author User
 */
public interface SanchayServerLauncherRI extends Remote {
    public UUID login(String login, char[] password) throws AuthorizationException, RemoteException;    
}
