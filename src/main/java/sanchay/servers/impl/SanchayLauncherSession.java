/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.impl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.servers.AuthenticationSeverRI;
import sanchay.servers.SanchayLauncherSessionRI;

/**
 *
 * @author User
 */
public class SanchayLauncherSession implements SanchayLauncherSessionRI {
    
    private static final MainServerCollection mainServerCollection = new MainServerCollection();
    
    public SanchayLauncherSession() throws RemoteException {
        super();
    }
    
//    public SanchayLauncherSession(MainServerCollection mainServCollection)  throws RemoteException {
//        super();
//        mainServerCollection = mainServCollection;
//    }
    
    public void initSanchayMainServer(UUID uuid, SanchayMainServer mainServer)
    {
        mainServerCollection.execute(uuid, mainServer);
    }

    public SanchayMainServer getSanchayMainServerInstance(UUID sessionId) throws SessionException, RemoteException
    {
        if (!LauncherSessionStorage.INSTANCE.sessionIdExists(sessionId)) {
            throw new SessionException("Session id does not exist", 2);
        }

        SanchayMainServer mainServer = null;
        
        try {
            mainServer = new SanchayMainServer("", "", "", "UTF-8");

            initSanchayMainServer(sessionId, mainServer);

//            sanchayMainServer.getAuthenticationSever().`

            //5. create the shared directory
    //        pp.createDirectory(pp.getDefaultDirectoryPath());
            mainServer.getRMIFileSystem().createDirectory(mainServer.getRMIFileSystem().getDefaultDirectoryPathOnServer());

        } catch (IOException ex) {
            Logger.getLogger(SanchayMainServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mainServer;
    }
    
    public void logout(UUID sessionId) throws RemoteException {
        LauncherSessionStorage.INSTANCE.removeSessionId(sessionId);
        mainServerCollection.removeAuthenticationSever(sessionId);
    }
    
}
