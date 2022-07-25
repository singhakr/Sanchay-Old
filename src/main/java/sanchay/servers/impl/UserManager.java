/*
 * Created on Sep 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.servers.impl;

import java.io.*;
import java.rmi.*;
//import java.rmi.activation.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import sanchay.GlobalProperties;
import sanchay.common.types.*;
import sanchay.properties.PropertiesTable;
import sanchay.servers.UserManagerRI;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UserManager extends SanchayServer
    implements UserManagerRI, Serializable
{
    private final SanchayMainServer sanchayMainServer;
 
    private Hashtable users; // logged in users

    public UserManager(String propManPath, String cs, SanchayMainServer sanchayMainServer) throws RemoteException, IOException
    {
        super(propManPath, cs);
        
        this.sanchayMainServer = sanchayMainServer;
    }    

    public String addUser(String user, String password) throws RemoteException
    {
	if(user.equals("") || user.equalsIgnoreCase(UserType.ANONYMOUS.toString()))
	    return GlobalProperties.getIntlString("User_already_exists.");
	
        PropertiesTable users = (PropertiesTable) propman.getPropertyContainer(GlobalProperties.getIntlString("users"), PropertyType.PROPERTY_TABLE);
        
        Vector rows = null;
        try {
            rows = users.getRows(GlobalProperties.getIntlString("User"), user);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        if(rows != null && rows.size() > 0)
            return GlobalProperties.getIntlString("User_already_exists.");

        String cols[] = {user, password};
        users.addRow(cols);
        
        try {
            propman.savePropertyContainer(GlobalProperties.getIntlString("users"), PropertyType.PROPERTY_TABLE);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RemoteException();
        }

        return user;
    }

    public String removeUser(String user) throws RemoteException
    {
	if(user.equals("") || user.equalsIgnoreCase(UserType.ANONYMOUS.toString()))
	    return GlobalProperties.getIntlString("This_user_cannot_be_removed.");
	
        PropertiesTable users = (PropertiesTable) propman.getPropertyContainer(GlobalProperties.getIntlString("users"), PropertyType.PROPERTY_TABLE);
        
        Vector rows = null;
        try {
            rows = users.getRows(GlobalProperties.getIntlString("User"), user);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        if(rows == null || rows.size() == 0)
            return GlobalProperties.getIntlString("User_does_not_exist.");

        try {
            users.removeRows(GlobalProperties.getIntlString("User"), user);
            propman.savePropertyContainer(GlobalProperties.getIntlString("users"), PropertyType.PROPERTY_TABLE);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RemoteException();
        }

        return user;
    }
	
    /**
     * 
     * Authenticates the user along with his password. Return user name for authenticated user.
     */
    public String authenticateUser(String user, String password) throws RemoteException
    {
	if(user.equals("") || user.equalsIgnoreCase(UserType.ANONYMOUS.toString()))
	    return user;
	
        PropertiesTable users = (PropertiesTable) propman.getPropertyContainer(GlobalProperties.getIntlString("users"), PropertyType.PROPERTY_TABLE);
        
        String outColNames[] = {GlobalProperties.getIntlString("Password")};

        Vector outVals = null;
        try {
            outVals = users.getValues(GlobalProperties.getIntlString("User"), user, outColNames);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        if(outVals == null || outVals.size() == 0)
	{
	    System.out.println(GlobalProperties.getIntlString("User_does_not_exist:_") + user);
            return GlobalProperties.getIntlString("User_does_not_exist.");
	}

        String pw = (String) ((Vector) outVals.get(0)).get(0);
        
        if(pw.equals(password) == false)
	{
	    System.out.println(GlobalProperties.getIntlString("Incorrect_user_name_or_password:_") + user);
            return GlobalProperties.getIntlString("Incorrect_user_name_or_password.");
	}
            
	System.out.println(GlobalProperties.getIntlString("User_authenticated:_") + user);
        return user;
    }

    public boolean isLoggedIn(String user) throws RemoteException
    {
	if(user.equals("") || user.equalsIgnoreCase(UserType.ANONYMOUS.toString()))
	    return false;
	
        if(users.get(user) != null)
            return true;
            
        return false;
    }

    public String loginUser(String user, String password) throws RemoteException
    {
	if(user.equals("") || user.equalsIgnoreCase(UserType.ANONYMOUS.toString()))
	    return user;
	
        if(isLoggedIn(user))
            return GlobalProperties.getIntlString("User_is_already_logged_in.");
            
        String ret = authenticateUser(user, password);

        if(ret.equals(user) == false)
            return ret;
        
        users.put((user), new Integer(1));
        
        return ret;
    }

    public String logout(String user) throws RemoteException
    {
	if(user.equals("") || user.equalsIgnoreCase(UserType.ANONYMOUS.toString()))
	    return user;

	if(isLoggedIn(user))
        {
            users.remove(user);
            return user;
        }

        return GlobalProperties.getIntlString("User_is_not_logged_in.");
    }

    public PropertiesTable getUserTable(String user) throws RemoteException
    {
	if(user.equals("") || user.equalsIgnoreCase(UserType.ANONYMOUS.toString()))
	    return null;

	if(isLoggedIn(user))
	{
            PropertiesTable users = (PropertiesTable) propman.getPropertyContainer(GlobalProperties.getIntlString("users"), PropertyType.PROPERTY_TABLE);
	    
//	    if(type.ord >= UserType.GUEST.ord)
//		users.setEditable(true);
//	    
//	    users.setEditable(false);

	    return users;
	}

	return null;
    }

    public PropertiesTable getTaskTable(String user) throws RemoteException
    {
	if(user.equals("") || user.equalsIgnoreCase(UserType.ANONYMOUS.toString()))
	    return null;

	if(isLoggedIn(user))
	{
            PropertiesTable users = (PropertiesTable) propman.getPropertyContainer(GlobalProperties.getIntlString("users"), PropertyType.PROPERTY_TABLE);
        
//	    String colNames[] = {"User", "UserType"};
//	    String vals[] = {user, type.toString()};
//	    String outColNames[] = {"PMPath", "PMCharset"};
//
//	    Vector outVals = null;
//	    try {
//		outVals = users.getValuesAnd(colNames, vals, outColNames);
//	    } catch (Exception e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	    }
//
//	    if(outVals == null || outVals.size() == 0)
//	    {
//		System.out.println("User does not exist: " + user + " " + type);
//		return null;
//	    }
//
//	    String pmPath = (String) ((Vector) outVals.get(0)).get(0);
//	    String pmCharset = (String) ((Vector) outVals.get(0)).get(1);
//	    
//	    try {
//		PropertiesManager upm = new PropertiesManager(pmPath, pmCharset);
//                PropertiesTable tasks = (PropertiesTable) propman.getPropertyContainer("tasks", PropertyType.PROPERTY_TABLE);
//
//		if(type.ord >= UserType.GUEST.ord)
//		    tasks.setEditable(true);
//
//		tasks.setEditable(false);
//		
//		return tasks;
//		
//	    } catch (FileNotFoundException ex) {
//		ex.printStackTrace();
//		return null;
//	    } catch (IOException ex) {
//		ex.printStackTrace();
//		return null;
//	    }
	}

	return null;
    }

//    public PropertiesTable getUserInfo(String user) throws RemoteException
//    {
//	if(type.equals(UserType.ANONYMOUS))
//	    return null;
//	
//        PropertiesTable users = (PropertiesTable) propman.getPropertyContainer("users", PropertyType.PROPERTY_TABLE);
//        PropertiesTable userdata = (PropertiesTable) propman.getPropertyContainer("userdata", PropertyType.PROPERTY_TABLE);
//        
//        int rownum = users.getRowCount();
//        if(rownum != userdata.getRowCount())
//            throw new RemoteException();
//        
//        int colnum = users.getColumnCount() + userdata.getColumnCount() - 2;
//        String colNames[] = new String[colnum];
//        String data[][] = new String[rownum][colnum];
//
//        int i = 0;
//        for(i = 0; i < users.getColumnCount(); i++)
//        {
//            colNames[i] = users.getColumnName(i);
//            
//            for(int k = 0; k < rownum; k++)
//                data[k][i] = (String) users.getValueAt(k, i);
//        }
//
//        int ui = userdata.getColumnIndex("User");
//        int uti = userdata.getColumnIndex("UserType");
//        
//        for(int j = 0; j < userdata.getColumnCount(); j++)
//        {
//            String cn = userdata.getColumnName(j);
//            
//            if(j != ui && j != uti)
//            {
//                colNames[i] = users.getColumnName(j);
//            
//                for(int k = 0; k < rownum; k++)
//                    data[k][i] = (String) users.getValueAt(k, i);
//                
//                i++;
//            }
//        }
//        
//        PropertiesTable userinfo = new PropertiesTable(data, colNames);
//        
//        return userinfo;
//    }
    
    public final static UserManager getUserManagerServerInstance(String propManPath, String cs, SanchayMainServer sanchayMainServer)
    {
        UserManager userManagerServer = null;
        
        try {
            userManagerServer = new UserManager(propManPath, cs, sanchayMainServer);
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return userManagerServer;
    }

    public static void main(String[] args) {
    }
}
