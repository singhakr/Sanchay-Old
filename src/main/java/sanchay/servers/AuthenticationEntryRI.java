/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sanchay.servers;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
import sanchay.servers.impl.AuthorizationException;
import sanchay.servers.impl.SessionException;

/**
 *
 * @author User
 */
public interface AuthenticationEntryRI extends Remote {
    public UUID login(String login, char[] password) throws AuthorizationException, RemoteException;
    public Serializable authenticateUser(String userName, String password, UUID sessionId)  throws RemoteException, SessionException;
}
