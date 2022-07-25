package sanchay.servers.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Bernardo Lopes - a32040
 * @author Tiago Padrão - a33061
 */
public class RMIFileSystemServer {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException {
        // Add:
        // -Djava.security.policy=server.policy
        // to the VM arguments when running the server
//        if(System.getSecurityManager()==null) {
//            System.setSecurityManager(new SecurityManager());
//        }
        
        //1. create our remote object
        RMIFileSystemImpl p = new RMIFileSystemImpl(null);
        //2. create the registry
        Registry registry = LocateRegistry.createRegistry(1099);
        //3. export the object
//        RMIFileSystemRI pp = (RMIFileSystemRI)UnicastRemoteObject.exportObject((Remote) p,0);
        //4. register the remote object of the registry
//        registry.rebind("sanchayRMIProtocol", (Remote) pp);
        registry.rebind("sanchayRMIProtocol", (Remote) p);
        //5. create the shared directory
//        pp.createDirectory(pp.getDefaultDirectoryPath());
        p.createDirectory(p.getDefaultDirectoryPathOnServer());
    }
}
