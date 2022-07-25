/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.server.dto.model.files.RemoteFile;
import sanchay.server.dto.tree.impl.RemoteFileNode;
import sanchay.servers.clients.SanchayRMIClient;
import sanchay.servers.RMIFileSystemRI;

/**
 *
 * @author User
 */
public class RMIUtils {
    
    public static String getDefaultDirectoryPathOnClient()
    {
       return System.getProperty("user.dir");        
    }

    public static String getAbsolutePathOnClient(String relativePathOnServer)
    {
        String defaultDirPathOnClient = getDefaultDirectoryPathOnClient();
        
        File clientFile = new File(defaultDirPathOnClient, relativePathOnServer);
        
        String absolutePathOnClient = clientFile.getAbsolutePath();
        
        return absolutePathOnClient;
    }

    public static String getRelativePathOnClient(String longPath, String shortPath)
    {
        String path = "";
        
        try {
           // Two absolute paths
//           File absolutePath1 = new File("C:\\Users\\Desktop\\Programiz\\Java\\Time.java");
           File longPathFile = new File(longPath);
           System.out.println("Long path on client: " + longPath);
//           File absolutePath2 = new File("C:\\Users\\Desktop");
           File shortPathFile = new File(shortPath);
           System.out.println("Short Path on client: " + shortPath);

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
    
    public static boolean listDirectories(String parentDirectory, RemoteFileNode rootNode, RMIFileSystemRI rmiFS) throws RemoteException
    {
        String foundPath = null;
        String absPathServer = null;
        String absPathClient = null;
        String defaultServerDirPath = rmiFS.getDefaultDirectoryPathOnServer();
        
        File files[] = null;
        
        if(parentDirectory.equals(""))
            files = rmiFS.listFiles(defaultServerDirPath);
        else
            files = rmiFS.listFiles(defaultServerDirPath + "/" + parentDirectory);
        
        RemoteFileNode node = null;
        File remoteFile = null;
//        File rootRemoteFile = null;
        
//        if(rootNode != null)
//        {
//            rootRemoteFile = rootNode.getRemoteRMIFile();
//        }
        
        for (File file : files)
        {
            remoteFile = file;
            
            if(file == null)
            {
                System.out.println("Null remote RMI file");
                return false;
            }
            
            // If file
//            if (!file.isDirectory())
            if (!rmiFS.isDirectory(file))
            {
                if(!file.getName().startsWith("."))
                {
                    if(parentDirectory.endsWith("/") || parentDirectory.equals(""))
                    {
                        foundPath = parentDirectory + file.getName();
                        absPathServer = defaultServerDirPath + "/" + foundPath;
                    }
                    else
                    {
                        foundPath = parentDirectory + "/" + file.getName();
                        absPathServer = foundPath;
                    }

                    System.out.println("Found path: " + foundPath);
                                        
//                    absPathServer = rmiFS.getAbsolutePathOnServer(file);
//                    absPathServer = foundPath;
                    System.out.println("Absolute path on server: " + absPathServer);

                    foundPath = rmiFS.getRelativePathOnServer(absPathServer, defaultServerDirPath);

                    absPathClient = RMIUtils.getAbsolutePathOnClient(foundPath);
                    System.out.println("Absolute path on client: " + absPathClient);

                    System.out.println("Relative found path on server: " + foundPath);
                    
                    RemoteFile rfile = new RemoteFile(file.getName(), foundPath, absPathServer, absPathClient, false);
                 
//                    remoteFile = new RemoteSftpFile(entry, rootRemoteFile, foundPath);
//                    node = RemoteFileNode.getRemoteFileNodeInstance(null, remoteFile, rfile, rmiFS, RemoteFileNode.RMI_MODE);            
//                    node = RemoteFileNode.getRemoteFileNodeInstance(null, null, rfile, rmiFS, RemoteFileNode.RMI_MODE);            
                    node = RemoteFileNode.getRemoteFileNodeInstance(null, null, rfile, RemoteFileNode.RMI_MODE);            
                    rootNode.add(node);

                    //                list.add(foundPath);

                    System.out.println("Found file:" + foundPath);
                }
            }
            // If directory
            else
            {
                if (!file.getName().equals(".") &&
                    !file.getName().equals("..") &&
                    !file.getName().startsWith("."))
                {
                    if(parentDirectory.endsWith("/") || parentDirectory.equals(""))
                    {
                        foundPath = parentDirectory + file.getName();
                        absPathServer = defaultServerDirPath + "/" + foundPath;
                    }
                    else
                    {
                        foundPath = parentDirectory + "/" + file.getName();
                        absPathServer = foundPath;
                    }

                    System.out.println("Found path: " + foundPath);
                                        
//                    absPathServer = rmiFS.getAbsolutePathOnServer(file);
                    System.out.println("Absolute path on server: " + absPathServer);

                    foundPath = rmiFS.getRelativePathOnServer(absPathServer, defaultServerDirPath);

                    absPathClient = RMIUtils.getAbsolutePathOnClient(foundPath);
                    System.out.println("Absolute path on client: " + absPathClient);

                    System.out.println("Relative found path on server: " + foundPath);
                    
                    RemoteFile rfile = new RemoteFile(file.getName(), foundPath, absPathServer, absPathClient, true);

//                    node = RemoteFileNode.getRemoteFileNodeInstance(null, null, rfile, rmiFS, RemoteFileNode.RMI_MODE);            
                    node = RemoteFileNode.getRemoteFileNodeInstance(null, null, rfile, RemoteFileNode.RMI_MODE);            
                    rootNode.add(node);
                 
                    RMIUtils.listDirectories(foundPath, node, rmiFS);

                    System.out.println("Found file:" + foundPath);
//                    }
                }
            }
        }

        return true;        
    }
    
    public static void uploadFile(String clientpath, String serverpath, RMIFileSystemRI rmifsInferface) throws RemoteException, IOException {  
        FileInputStream in = null;
        
        try {
            File clientpathfile = new File(clientpath);
            
            byte [] mydata=new byte[(int) clientpathfile.length()];
            
            in = new FileInputStream(clientpathfile);
            
            System.out.println("uploading to server...");
            
            in.read(mydata, 0, mydata.length);
            
            rmifsInferface.uploadFileToServer(mydata, serverpath, (int) clientpathfile.length());
            
            in.close();        
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SanchayRMIClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SanchayRMIClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void downloadFile(String clientpath, String serverpath, RMIFileSystemRI rmifsInferface) throws RemoteException, IOException {  

        byte [] mydata = rmifsInferface.downloadFileFromServer(serverpath);
        
        System.out.println("downloading...");
        
        File clientpathfile = new File(clientpath);
        
        FileOutputStream out=new FileOutputStream(clientpathfile);				
        
        out.write(mydata);
        
        out.flush();
        
        out.close();        
    }

    public static RemoteFileNode getAnnotationDirNodeRMI(RemoteFileNode remoteFileNode) {
        
        int ccount = remoteFileNode.getChildCount();
                
        if(ccount > 0)
        {
            for (int i = 0; i < ccount; i++) {

                RemoteFileNode childNode = (RemoteFileNode) remoteFileNode.getChildAt(i);

                RemoteFile childRemoteFile = childNode.getRemoteRMIFile();

                if(childRemoteFile != null)
                {
                    return childNode;
                }
            }
        }
        
        return null;
    }

    public static void cloneDirectory(RemoteFileNode remoteFileNode, RMIFileSystemRI rmiFileSystem, boolean overwriteExisting) throws RemoteException {
        
        RemoteFile rfile = remoteFileNode.getRemoteRMIFile();
        
        if(rfile != null)
        {            
//            if(rfile.isDirectory())
            if(rfile.isDirectory())
            {
                File dir = new File(rfile.getRelativePath());
                
                if(!dir.exists())
                    dir.mkdir();                
            
                int ccount = remoteFileNode.getChildCount();

                for (int i = 0; i < ccount; i++) {
                    RemoteFileNode childNode = (RemoteFileNode) remoteFileNode.getChildAt(i);

                    RemoteFile childRemoteFile = childNode.getRemoteRMIFile();

                    if(childRemoteFile != null)
                    {
                        if(childRemoteFile.isDirectory())
                            cloneDirectory(childNode, rmiFileSystem, overwriteExisting);
                        else                            
                            cloneFile(childRemoteFile, rmiFileSystem, overwriteExisting);
                    }
                }                
            }
            else
            {
                cloneFile(rfile, rmiFileSystem, overwriteExisting);                
            }
        }
    }
    
//    public static String getClientPath(RemoteFile file)
//    {
//        Path pathAbsolute = Paths.get(file.getAbsolutePath());
//        Path pathBase = Paths.get(file.getRelativePath());
//        Path pathRelative = pathBase.relativize(pathAbsolute);
////        
////        String relPath = file.getRelativePath();
////        String name = file.getFileName();
////        
////        File curFile = new File(relPath);
////        
////        File parentFile = File(".");
//        
//        File curFile = new File(".", pathRelative.toString());
//        
//        return curFile.getPath();
//    }
    
    public static boolean cloneFile(RemoteFile remoteFile, RMIFileSystemRI rmiFileSystem, boolean overwriteExisting)
    {
        if(remoteFile == null)
            return false;
        
//        String sourcePath = remoteFile.getRelativePath();
//        String destPath = remoteFile.getRelativePath();
        String sourcePath = remoteFile.getAbsolutePathOnServer();
        String destPath = remoteFile.getAbsolutePathOnClient();
        
//        destPath = removePrefix(destPath, "./");
//        destPath = homeDir + "/" + destPath;

        try {            
            File localFile = new File(destPath);

            if(!localFile.exists() || overwriteExisting)
            {
                System.out.println("Downloading: " + sourcePath + " to " + destPath);
                try {
                    downloadFile(destPath, sourcePath, rmiFileSystem);
                } catch (IOException ex) {
                    Logger.getLogger(RMIUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        } catch (Exception ex) {
            Logger.getLogger(JSchSSHUtils.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            
            return false;
        }

        return true;        
    }
}
