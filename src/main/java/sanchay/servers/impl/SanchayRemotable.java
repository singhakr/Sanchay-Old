/*
 * SanchayActivable.java
 *
 * Created on November 3, 2005, 10:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.servers.impl;

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.rmi.activation.*;

import sanchay.GlobalProperties;
import sanchay.properties.PropertiesManager;
import sanchay.servers.SanchayServerRI;

/**
 *
 *  @author Anil Kumar Singh
 */
//public abstract class SanchayActivable
//public abstract class SanchayRemotable extends RemoteServer
public abstract class SanchayRemotable
    implements SanchayServerRI, Serializable {

    protected final PropertiesManager propman;
    
    protected SanchayRemotable(String propManPath, String cs) throws RemoteException, IOException {
        super();
        
        File propManFile = new File(propManPath);
        
        if(propManFile.exists())       
            propman = new PropertiesManager(propManPath, cs);
        else
            propman = null;
    }    
	
    public PropertiesManager getPropertiesManager() throws RemoteException
    {
        return propman;
    }
    
    public String checkConnection()
    {
        return GlobalProperties.getIntlString("Connected_successfully");
    }
}
