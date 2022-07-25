/*
 * Created on Sep 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.common.types;

import java.io.*;
import java.util.*;
import java.rmi.*;
//import java.rmi.activation.*;

import sanchay.gui.clients.*;
import sanchay.common.*;
import sanchay.properties.*;
import sanchay.servers.impl.SanchayMainServer;
import sanchay.servers.impl.UserManager;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class ServerType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected String title;
    
    protected ServerType(String title, String id, String pk) {
        super(id, pk);
        this.title = title;

        if (ServerType.last() != null) {
            this.prev = ServerType.last();
            ServerType.last().next = this;
        }

        types.add(this);
	ord = types.size();
    }

    public static int size()
    {
        return types.size();
    }
    
    public static SanchayType first()
    {
        return (SanchayType) types.get(0);
    }
    
    public static SanchayType last()
    {
        if(types.size() > 0)
            return (SanchayType) types.get(types.size() - 1);
        
        return null;
    }

    public static SanchayType getType(int i)
    {
        if(i >=0 && i < types.size())
            return (SanchayType) types.get(i);
        
        return null;
    }

    public static Enumeration elements()
    {
        return new TypeEnumerator(ServerType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = ServerType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = ServerType.elements();
        return SanchayType.findFromId(enm, i);
    }
    
    public static Remote getNewServer(ServerType serverType, String address, boolean remote)
    {
        Remote server = null;
//        String ststr = serverType.toString();
        String ststr = serverType.getId();

        if(System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());
      
        if(remote)
        {
            try {
                String location = "rmi://" + address + "/" + ststr;
		
		System.out.println("Location: " + location);

                // Since you can't create an instance of an interface, what we get 
                // back from the lookup method is a remote reference to an object
                // that implements a RemoteInterface.
                //  
                // Then we cast the remote reference (serialized stub instance)
                // returned from Naming.lookup to the RemoteInterface so we can
                // call the interface method(s).    
                //         

                server = (Remote) Naming.lookup(location);
		System.out.println("Server Class Name: " + server.getClass().getName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else
        {
//            try {
//                if(serverType == SANCHAY_MAIN)
//                    server = new SanchayMainServer(SanchayMain.getSanchayComponentPM(ststr));
//                else if(serverType == USER_MANAGER)
//                    server = new UserManagerServer(SanchayMain.getSanchayComponentPM(ststr));
//            } catch (RemoteException ex) {
//                ex.printStackTrace();
//            }
        }
        
        return server;
    }

    public String toString() { return this.title; }

    public static final ServerType SANCHAY_SERVER = new ServerType("Sanchay Server", "SanchayServer", "sanchay.servers.impl");
//    public static final ServerType SANCHAY_MAIN = new ServerType("SanchayMainServer", "sanchay.servers");
    public static final ServerType USER_MANAGER = new ServerType("User Manager Server", "UserManagerServer", "sanchay.servers.impl");
    public static final ServerType RESOURCE_MANAGER = new ServerType("Resource Manager Server", "ResourceManagerServer", "sanchay.servers.impl");
//    public static final ServerType TASK_MANAGER = new ServerType("TaskManagerServer", "sanchay.servers");
//    public static final ServerType PROPERTIES_MANAGER = new ServerType("Properties Manager Server", "PropertiesManagerServer", "sanchay.servers");
//    public static final ServerType PARALLEL_MARKUP = new ServerType("ParallelMarkupServer", ParallelMarkup, "sanchay.servers");
//    public static final ServerType SYNTACTIC_ANNOTATION = new ServerType("SyntacticAnnotationServer", "sanchay.servers");
//    public static final ServerType PROPERTIES_MANAGER = new ServerType("PropertiesManagerServer", "sanchay.servers");
//    public static final ServerType UD_MANAGER = new ServerType("UDManagerServer", "sanchay.servers");
//    public static final ServerType CORPUS_MANAGER = new ServerType("CorpusManagerServer", "sanchay.servers");
//    public static final ServerType NGRAMLM = new ServerType("NGramLMServer", "sanchay.servers");
//    public static final ServerType IBM_MODEL = new ServerType("IBMModelServer", "sanchay.servers");
//    public static final ServerType DSF = new ServerType("DSFServer", "sanchay.servers");
//    public static final ServerType GTAC = new ServerType("GTACServer", "sanchay.servers");
//    public static final ServerType PARALLEL_CORPUS = new ServerType("ParallelCorpusServer", "sanchay.servers");
//    public static final ServerType ML_ANNOTATION = new ServerType("MLAnnotationServer", "sanchay.servers");
//    public static final ServerType TEXT_ENCODING = new ServerType("TextEncodingServer", "sanchay.servers");
//    public static final ServerType NLI = new ServerType("NLIServer", "sanchay.servers");
//    public static final ServerType NS = new ServerType("NSServer", "sanchay.servers");
}
