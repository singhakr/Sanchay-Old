package sanchay.servers.impl;



import sanchay.servers.RMIFileSystemRI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bernardo Lopes - a32040
 * @author Tiago Padrão - a33061
 */
//public class RMIFileSystemImpl extends UnicastRemoteObject implements RMIFileSystemRI, Serializable{
public class RMIFileSystemImpl implements RMIFileSystemRI, Serializable{

    private final SanchayMainServer sanchayMainServer;
    
    public RMIFileSystemImpl(SanchayMainServer sanchayMainServer) throws RemoteException {
//    public RMIFileSystemImpl(String s) throws RemoteException {
//		File storageDir = new File (s);
//		storageDir.mkdir();
        this.sanchayMainServer = sanchayMainServer;
    }    
    
    @Override
    public String getDefaultDirectoryPathOnServer() throws RemoteException {
       return System.getProperty("user.home") + "/RFS";

    }
    
    @Override
    public File[] listFiles(String directoryName) throws RemoteException {
        File file = null;
        File[] fileList = null;
        
        try {
            file = new File(directoryName);
            fileList = file.listFiles();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        return fileList;
    }
    
    @Override
    public void createDirectory(String directoryName) throws RemoteException {
        File directory = new File(directoryName);
        
        try{
            directory.mkdir();
        } 
        catch(SecurityException se){
            se.printStackTrace();
        } 
    }

    @Override
    public void deleteFile(String name) throws RemoteException {
        File fileName = new File(name);
        
        try {
            fileName.delete();
        }
        catch(SecurityException se){
            se.printStackTrace();
        }
    }
    
    @Override
    public void deleteDirectory(String name) throws RemoteException {
        File directory = new File(name);
        File[] files = directory.listFiles();
        
        if (files != null) { 
            for (File f:files) {
                if (f.isDirectory()) {
                    deleteDirectory(f.getAbsolutePath());
                } 
                else {
                    f.delete();
                }
            }
        }
        directory.delete();
    }

    @Override
    public void rename(String name, String nameNew) throws RemoteException {
        File fileName = new File(name);
        File renamed = new File(nameNew);
        
        try {
            fileName.renameTo(renamed);
        }
        catch(SecurityException se){
            se.printStackTrace();
        }
    }
    
    @Override
    public void createFile(String name) throws RemoteException {
        File file = new File(name);
        
        try{
            file.createNewFile();
        } 
        catch(SecurityException se){
            se.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(RMIFileSystemImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  

    @Override
    public boolean isDirectory(File file) throws RemoteException {
        return file.isDirectory();
    }

    @Override
    public boolean isFile(File file) throws RemoteException {
        return file.isFile();
    }

    @Override
    public long getLength(File file) throws RemoteException {
        return file.length();
    }

    @Override
    public long getLastModifiedDate(File file) throws RemoteException {
        return file.lastModified();
    }

    public String getAbsolutePathOnServer(String relativePathOnServer) throws RemoteException
    {
        String defaultDirPathOnServer = getDefaultDirectoryPathOnServer();
        
        File clientFile = new File(defaultDirPathOnServer, relativePathOnServer);
        
        String absolutePathOnServer = clientFile.getAbsolutePath();
        
        return absolutePathOnServer;
    }

    public String getRelativePathOnServer(String longPath, String shortPath) throws RemoteException
    {
        String path = "";
        
        try {
           // Two absolute paths
//           File absolutePath1 = new File("C:\\Users\\Desktop\\Programiz\\Java\\Time.java");
           File longPathFile = new File(longPath);
           System.out.println("Long path on server: " + longPath);
//           File absolutePath2 = new File("C:\\Users\\Desktop");
           File shortPathFile = new File(shortPath);
           System.out.println("Short Path on server: " + shortPath);

           // convert the absolute path to URI
           URI path1 = longPathFile.toURI();
           URI path2 = shortPathFile.toURI();

           // create a relative path from the two paths
           URI relativePath = path2.relativize(path1);

           // convert the URI to string
           path = relativePath.getPath();

           System.out.println("Relative Path: " + path);


         } catch (Exception e) {
           e.getStackTrace();
         }

        return path;
    }

    public String getPath(File file) throws RemoteException
    {
        if(file!= null)
            return file.getPath();
        
        return null;
    }
    
    public void uploadFileToServer(byte[] mydata, String serverpath, int length) throws RemoteException {
			
    	try {
            File serverpathfile = new File(serverpath);
            FileOutputStream out=new FileOutputStream(serverpathfile);
            byte [] data=mydata;

            out.write(data);
            out.flush();
            out.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    	
        System.out.println("Done writing data...");
		
    }
	
    public byte[] downloadFileFromServer(String serverpath) throws RemoteException {

        byte [] mydata;	

        File serverpathfile = new File(serverpath);			
        mydata=new byte[(int) serverpathfile.length()];
        FileInputStream in;
        try {
                in = new FileInputStream(serverpathfile);
                try {
                        in.read(mydata, 0, mydata.length);
                } catch (IOException e) {

                        e.printStackTrace();
                }						
                try {
                        in.close();
                } catch (IOException e) {

                        e.printStackTrace();
                }

        } catch (FileNotFoundException e) {

                e.printStackTrace();
        }		

        return mydata;			 
    }
}
