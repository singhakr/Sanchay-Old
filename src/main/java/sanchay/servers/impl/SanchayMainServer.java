/*
 * Created on Sep 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.servers.impl;

import sanchay.servers.RMIFileSystemRI;
import java.io.*;
import java.rmi.*;
import org.slf4j.LoggerFactory;
import sanchay.servers.ResourceManagerRI;
//import java.rmi.activation.*;
import sanchay.servers.SanchayServerRI;
import sanchay.servers.UserManagerRI;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To import sanchay.servers.ResourceManagerRI;
change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SanchayMainServer extends SanchayServer
    implements SanchayServerRI, Runnable, Serializable {

    /**
     * 
     */
    
    protected final UserManagerRI userManager ;
    protected final ResourceManagerRI resourceManager;
    
    protected final RMIFileSystemRI rmiFileSystem;
//    protected final transient AuthenticationSeverRI authenticationSever;

    private static final transient org.slf4j.Logger log = LoggerFactory.getLogger(SanchayMainServer.class);

    public SanchayMainServer(String propManPath, String userManagerPath, String resourceManagerPath, String cs) throws RemoteException, IOException 
    {        
        super(propManPath, cs);

        userManager = new UserManager(userManagerPath, cs, this);          
        resourceManager = new ResourceManager(resourceManagerPath, cs, this);  
        rmiFileSystem = new RMIFileSystemImpl(this);
//        authenticationSever = new AuthenticationSever();
    }    

    public UserManagerRI getUserManager() throws RemoteException
    {
	return userManager;
    }

    public ResourceManagerRI getResourceManager() throws RemoteException
    {
	return resourceManager;
    }

    public RMIFileSystemRI getRMIFileSystem() throws RemoteException
    {
	return rmiFileSystem;
    }

//    public AuthenticationSeverRI getAuthenticationSever() throws RemoteException
//    {
//	return authenticationSever;
//    }
    
    public static org.slf4j.Logger getLogger()
    {
        return log;
    }
    
//    public boolean authenticateUser(String userName, String password)
//    {
//        return authenticationSever.authenticateUser(userName, password);
//    }

    @Override
    public void run() {
        
        System.out.println("Running main server task.");
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
