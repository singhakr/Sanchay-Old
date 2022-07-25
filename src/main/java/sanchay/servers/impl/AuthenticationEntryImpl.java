/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.UUID;
import sanchay.auth.Encryptor;
import sanchay.auth.SQLiteJDBC;
import sanchay.servers.AuthenticationEntryRI;
import sanchay.servers.AuthenticationSeverRI;

/**
 *
 * @author User
 */
public class AuthenticationEntryImpl extends UnicastRemoteObject implements AuthenticationEntryRI {

//    private AuthenticationSession authenticationSession;
    
    public AuthenticationEntryImpl() throws RemoteException
    {
        super();
    }
    
    public UUID login(String login, char[] password) throws AuthorizationException, RemoteException {
        String pass = Encryptor.encryptPassword(password);
        try {
            if (SQLiteJDBC.userExists(login, pass)) {
                System.out.println("User " + login + " logged in.");                
                
                return AuthSessionStorage.INSTANCE.generateSessionId(login);
            } else {
                throw new AuthorizationException("User with such username/password combination does not exist", 1);
            }
        } catch (SQLException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            throw new AuthorizationException("Exception while authorization. Root cause: " + e);
        }
    }    
    
    public Serializable authenticateUser(String userName, String password, UUID sessionId)  throws RemoteException, SessionException
    {
//        if(AuthSessionStorage.INSTANCE.sessionIdExists(sessionId))
//        {
//            System.out.println("You are already logged in. Please logout first.");
//            return null;
//        }
//        UUID sessionId = AuthSessionStorage.INSTANCE.getSessionId(userName);
        AuthenticationSeverRI authenticationSever = AuthenticationSession.getAuthenticationSeverInstance(sessionId);
        
        return authenticationSever.authenticateUser(userName, password);
    }
//
//    public void setAuthenticationSession(AuthenticationSession authenticationSession) {
//        this.authenticationSession = authenticationSession;
//    }
}
