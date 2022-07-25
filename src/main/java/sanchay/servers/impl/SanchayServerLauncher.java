/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

/**
 *
 * @author User
 */

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.auth.Encryptor;
import sanchay.auth.SQLiteJDBC;
import sanchay.servers.AuthenticationSessionRI;
import sanchay.servers.SanchayLauncherSessionRI;
import sanchay.servers.impl.AuthenticationSever;
import sanchay.servers.AuthenticationSeverRI;
import sanchay.servers.SanchayServerLauncherRI;
import sanchay.servers.AuthenticationEntryRI;

/**
 *
 * @author User
 */
public class SanchayServerLauncher extends UnicastRemoteObject implements SanchayServerLauncherRI, Serializable {

    private static final String LAUNCHER_LOGIN = "LauncherLogin";
    private static final String LAUNCHER_SESSION = "LauncherSession";
    
    private static final String AUTH_LOGIN = "AuthorizationLogin";
    private static final String AUTH_SESSION = "AuthorizationSession";
    
    private static boolean connected = false;
    
//    private SanchayLauncherSession sanchayLauncherSession;
    
    public static boolean isConnected() {
        return connected;
    }

    public static void setConnected(boolean aConnected) {
        connected = aConnected;
    }
    
    public SanchayServerLauncher() throws RemoteException
    {
        super();
    }
    
    public SanchayMainServer getSanchayMainServerInstance() throws RemoteException
    {
        if(connected)
        {
            SanchayMainServer sanchayMainServer = null;

            try {
                sanchayMainServer = new SanchayMainServer("", "", "", "UTF-8");
    //            sanchayMainServer.getAuthenticationSever().`
            } catch (IOException ex) {
                Logger.getLogger(SanchayMainServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            //5. create the shared directory
    //        pp.createDirectory(pp.getDefaultDirectoryPath());
            sanchayMainServer.getRMIFileSystem().createDirectory(sanchayMainServer.getRMIFileSystem().getDefaultDirectoryPathOnServer());

            return sanchayMainServer;
        }
        else
            return null;
    }
    public AuthenticationSever getAuthenticationSeverInstance() throws RemoteException
    {
        if(connected)
        {
            AuthenticationSever authenticationSever = null;

            try {
                authenticationSever = new AuthenticationSever();
    //            sanchayMainServer.getAuthenticationSever().`
            } catch (IOException ex) {
                Logger.getLogger(SanchayMainServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            return authenticationSever;
        }
        else
            return null;
    }

    public UUID login(String login, char[] password) throws AuthorizationException, RemoteException {
        String pass = Encryptor.encryptPassword(password);
        try {
            if (SQLiteJDBC.userExists(login, pass)) {
                System.out.println("User " + login + " logged in.");
                
                return LauncherSessionStorage.INSTANCE.generateSessionId(login);
            } else {
                throw new AuthorizationException("User with such username/password combination does not exist", 1);
            }
        } catch (SQLException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            throw new AuthorizationException("Exception while authorization. Root cause: " + e);
        }
    }    
   
    public static void main(String[] args) {
        try {
            // Add:
            // -Djava.security.policy=server.policy
            // to the VM arguments when running the server
//        if(System.getSecurityManager()==null) {
//            System.setSecurityManager(new SecurityManager());
//        }

//1. create our remote object
//        RMIFileSystemImpl p = new RMIFileSystemImpl();
//        SanchayServerLauncherRI sanchayServerLauncher = null;

            SQLiteJDBC.createTable();
            SQLiteJDBC.fillTable();
//            DataGenerator dataGenerator = new DataGenerator();

            // "Data generator"
//            SanchayMainServer sanchayMainServer = new SanchayMainServer("", "", "", "UTF-8");
//            AuthenticationSeverRI AuthenticationSever = new AuthenticationSever();
//            MainServerCollection mainServerCollection = new MainServerCollection();
//            AuthenticationServerCollection authenticationServerCollection = new AuthenticationServerCollection();

            // Login service
            SanchayServerLauncherRI sanchayServerLauncher = new SanchayServerLauncher();
            AuthenticationEntryRI authenticationEntry = new AuthenticationEntryImpl();
            
            // Session service
//            SanchayLauncherSessionRI sanchayLauncherSession = new SanchayLauncherSession(mainServerCollection);
//            AuthenticationSessionRI authenticationSession = new AuthenticationSession(authenticationServerCollection);
            SanchayLauncherSessionRI sanchayLauncherSession = new SanchayLauncherSession();
            AuthenticationSessionRI authenticationSession = new AuthenticationSession();

//            ((SanchayLauncherSession) sanchayLauncherSession);.setAuthenticationSession((AuthenticationSession) authenticationSession);

            //2. create the registry
            Registry registry;

            registry = LocateRegistry.createRegistry(1099);
                //3. export the object
            //        RMIFileSystemRI pp = (RMIFileSystemRI)UnicastRemoteObject.exportObject((Remote) p,0);
            //4. register the remote object of the registry
            //        registry.rebind("sanchayRMIProtocol", (Remote) pp);
            registry.rebind(LAUNCHER_LOGIN, sanchayServerLauncher);
//            AuthenticationEntry authenticationEntryInterface = (AuthenticationEntry)UnicastRemoteObject.exportObject((Remote) authenticationEntry,0);
//            registry.rebind(AUTH_LOGIN, (Remote) authenticationEntryInterface);
            registry.rebind(AUTH_LOGIN, authenticationEntry);

//            registry.rebind(LAUNCHER_SESSION, sanchayLauncherSession);
//            registry.rebind(AUTH_SESSION, authenticationSession);
            SanchayLauncherSessionRI sanchayLauncherSessionRI = (SanchayLauncherSessionRI)UnicastRemoteObject.exportObject((Remote) sanchayLauncherSession, 0);
            AuthenticationSessionRI authenticationSessionRI = (AuthenticationSessionRI)UnicastRemoteObject.exportObject((Remote) authenticationSession, 0);
            // Session classes are not Unicast...
            registry.rebind(LAUNCHER_SESSION, sanchayLauncherSessionRI);
            registry.rebind(AUTH_SESSION, authenticationSessionRI);

            ((SanchayServerLauncher) sanchayServerLauncher).setConnected(true);
            
            //5. create the shared directory
            //        pp.createDirectory(pp.getDefaultDirectoryPath());
            //        sanchayServerLaucher.getRMIFileSystem().createDirectory(sanchayServerLaucher.getRMIFileSystem().getDefaultDirectoryPathOnServer());

//            Thread sanchayMainServerThread = new Thread(mainServerCollection);
//            sanchayMainServerThread.start();
//
//            Thread authenticationServerThread = new Thread(authenticationServerCollection);
//            authenticationServerThread.start();

            System.out.println("Server has started...");

        } catch (SQLException ex) {
                Logger.getLogger(SanchayServerLauncher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
                Logger.getLogger(SanchayServerLauncher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(SanchayServerLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public void setSanchayLauncherSession(SanchayLauncherSession sanchayLauncherSession) {
//        this.sanchayLauncherSession = sanchayLauncherSession;
//    }
}

