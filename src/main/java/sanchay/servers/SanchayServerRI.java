/*
 * SanchayServerRI.java
 *
 * Created on October 31, 2005, 6:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.servers;

import java.rmi.*;

import sanchay.properties.*;

/**
 *
 *  @author Anil Kumar Singh
 */
//public interface SanchayServerRI extends Remote {
public interface SanchayServerRI  {
    public PropertiesManager getPropertiesManager() throws RemoteException;
    public String checkConnection() throws RemoteException;
    
}
