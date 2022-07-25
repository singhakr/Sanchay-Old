/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.servers.utils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.server.dto.model.files.RemoteSftpFile;
import sanchay.server.dto.tree.impl.RemoteFileNode;

/**
 *
 * @author User
 */
public class JSchSSHUtils {
    
    public static void listDirectory(
//        ChannelSftp channelSftp, String path, List<String> list) throws SftpException
        ChannelSftp channelSftp, String path, RemoteFileNode rootNode) throws SftpException
    {
//        
//        File[] hfiles = homeDir.listFiles();
//        
//        for (int i = 0; i < hfiles.length; i++) {
//            File file = hfiles[i];
//            System.out.println("File in home directory: " + hfiles[i].getName());
//        }        
        
        String foundPath = null;
        
//        RemoteFileNode rootNode = RemoteFileNode.getRemoteFileNodeInstance(null);

        Vector<LsEntry> files = channelSftp.ls(path);
        
//        RemoteFileNode node = RemoteFileNode.getRemoteFileNodeInstance(null);
        RemoteFileNode node = null;
        RemoteSftpFile remoteFile = null;
        RemoteSftpFile rootRemoteFile = null;
        
        if(rootNode != null)
        {
            rootRemoteFile = rootNode.getRemoteSftpFile();
        }
        
        for (LsEntry entry : files)
        {
            if(entry == null)
            {
                System.out.println("Null LsEntry");
                return;
            }
            
            // If file
            if (!entry.getAttrs().isDir())
            {
                if(!entry.getFilename().startsWith("."))
                {
                    foundPath = path + "/" + entry.getFilename();
                 
                    remoteFile = new RemoteSftpFile(entry, rootRemoteFile, foundPath);
//                    node = RemoteFileNode.getRemoteFileNodeInstance(remoteFile, null, null, null, RemoteFileNode.SFTP_MODE);            
                    node = RemoteFileNode.getRemoteFileNodeInstance(remoteFile, null, null, RemoteFileNode.SFTP_MODE);            
                    rootNode.add(node);

                    //                list.add(foundPath);

                    System.out.println("Found file:" + foundPath);
                }
            }
            // If directory
            else
            {
                if (!entry.getFilename().equals(".") &&
                    !entry.getFilename().equals("..") &&
                    !entry.getFilename().startsWith("."))
                {
                    foundPath = path + "/" + entry.getFilename();
                    
//                    if(node != null)
//                    {
                    remoteFile = new RemoteSftpFile(entry, rootRemoteFile, foundPath);
//                    node = RemoteFileNode.getRemoteFileNodeInstance(remoteFile, null, null, null, RemoteFileNode.SFTP_MODE);            
                    node = RemoteFileNode.getRemoteFileNodeInstance(remoteFile, null, null, RemoteFileNode.SFTP_MODE);            
                    rootNode.add(node);
                 
                    listDirectory(channelSftp, foundPath, node);

                    System.out.println("Found file:" + foundPath);
//                    }
                }
            }
        }

        return;
//        return rootNode;
    }    
    
public static void downloadDir(String sourcePath, String destPath, ChannelSftp sftpChannel) throws SftpException { // With subfolders and all files.

    System.out.println("Downloading ...");

    // Create local folders if absent.
    try {
        new File(sourcePath).mkdirs();
    } catch (Exception e) {
        System.out.println("Error creating directory at : " + destPath);
        System.out.println(e.getMessage());
    }
    sftpChannel.lcd(destPath);

    // Copy remote folders one by one.
    try{
        JSchSSHUtils.lsFolderCopy(sourcePath, destPath, sftpChannel); // Separated because loops itself inside for subfolders.    
    }
    catch(Exception e)
    {
        e.printStackTrace();
        System.out.println(e.getMessage());        
    }
    
}

private static void lsFolderCopy(String sourcePath, String destPath, ChannelSftp sftpChannel) throws SftpException { // List source (remote, sftp) directory and create a local copy of it - method for every single directory.

    System.out.println("Recursively downloading ...");

    Vector<ChannelSftp.LsEntry> list = sftpChannel.ls(sourcePath); // List source directory structure.
    for (ChannelSftp.LsEntry oListItem : list)
    { // Iterate objects in the list to get file/folder names.
            if (!oListItem.getAttrs().isDir())
            { // If it is a file (not a directory).
                if (!(new File(destPath + "/" + oListItem.getFilename())).exists() || (oListItem.getAttrs().getMTime() > Long.valueOf(new File(destPath + "/" + oListItem.getFilename()).lastModified() / (long) 1000).intValue())) { // Download only if changed later.
                    new File(destPath + "/" + oListItem.getFilename());

                    String spath = sourcePath + "/" + oListItem.getFilename();
                    String dpath = destPath + "/" + oListItem.getFilename();

                    System.out.println("Source path: " + spath);
                    System.out.println("Destination path: " + spath);

                    sftpChannel.get(sourcePath + "/" + oListItem.getFilename(), destPath + "/" + oListItem.getFilename()); // Grab file from source ([source filename], [destination filename]).
                }
            } else if (!(".".equals(oListItem.getFilename()) || "..".equals(oListItem.getFilename()))) {
                new File(destPath + "/" + oListItem.getFilename()).mkdirs(); // Empty folder copy.
                
                try 
                {
                JSchSSHUtils.lsFolderCopy(sourcePath + "/" + oListItem.getFilename(), destPath + "/" + oListItem.getFilename(), sftpChannel); // Enter found folder on server to read its contents and create locally.
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println(e.getMessage());        
                }
            }
        }
    }    

    public static void cloneDirectory(RemoteFileNode remoteFileNode, ChannelSftp channelSftp, boolean overwriteExisting) {
        
        RemoteSftpFile rfile = remoteFileNode.getRemoteSftpFile();
        
        if(rfile != null)
        {
            LsEntry entry = rfile.getLsEntry();
            
            if(entry == null)
                return;
            
            if(entry.getAttrs().isDir())
            {
                File dir = new File(rfile.getPath());
                
                if(!dir.exists())
                    dir.mkdir();                
            
                int ccount = remoteFileNode.getChildCount();

                for (int i = 0; i < ccount; i++) {
                    RemoteFileNode childNode = (RemoteFileNode) remoteFileNode.getChildAt(i);

                    RemoteSftpFile childRemoteFile = childNode.getRemoteSftpFile();

                    LsEntry childEntry = childRemoteFile.getLsEntry();

                    if(childEntry != null)
                    {
                        if(childEntry.getAttrs().isDir())
                            cloneDirectory(childNode, channelSftp, overwriteExisting);
                        else                            
                            cloneFile(childRemoteFile, channelSftp, overwriteExisting);
                    }
                }                
            }
            else
            {
                cloneFile(rfile, channelSftp, overwriteExisting);                
            }
        }
    }

    public static RemoteFileNode getAnnotationDirNodeSftp(RemoteFileNode remoteFileNode) {
        
        int ccount = remoteFileNode.getChildCount();
                
        if(ccount > 0)
        {
            for (int i = 0; i < ccount; i++) {

                RemoteFileNode childNode = (RemoteFileNode) remoteFileNode.getChildAt(i);

                RemoteSftpFile childRemoteFile = childNode.getRemoteSftpFile();

                if(childRemoteFile.getLsEntry() != null)
                {
                    return childNode;
                }
            }
        }
        
        return null;
    }
    
    public static boolean cloneFile(RemoteSftpFile remoteFile, ChannelSftp channelSftp, boolean overwriteExisting)
    {
//        String homeDir = FileUtils.getUserDirectoryPath();
//        
//        homeDir = homeDir.replaceAll("\\\\", "/");

//        Pattern lPattern = Pattern.compile("[\\\\]");
//        Matcher lMatcher = lPattern.matcher(homeDir);
//        
//        lMatcher.replaceAll("/");

        if(remoteFile == null)
            return false;
        
        String destPath = remoteFile.getPath();
        String sourcePath = destPath;
        
//        destPath = removePrefix(destPath, "./");
//        destPath = homeDir + "/" + destPath;

        try {            
            File localFile = new File(destPath);

            if(!localFile.exists() || overwriteExisting)
            {
                System.out.println("Downloading: " + sourcePath + " to " + destPath);
                channelSftp.get(sourcePath, destPath);
            }
            
        } catch (SftpException ex) {
            Logger.getLogger(JSchSSHUtils.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            
            return false;
        }

        return true;        
    }
    
    public static boolean uploadFile(RemoteSftpFile remoteFile, ChannelSftp channelSftp, boolean overwriteExisting)
    {
//        String homeDir = FileUtils.getUserDirectoryPath();
//        
//        homeDir = homeDir.replaceAll("\\\\", "/");

//        Pattern lPattern = Pattern.compile("[\\\\]");
//        Matcher lMatcher = lPattern.matcher(homeDir);
//        
//        lMatcher.replaceAll("/");

        if(remoteFile == null)
            return false;
        
        String destPath = remoteFile.getPath();
        String sourcePath = destPath;
        
//        destPath = removePrefix(destPath, "./");
//        destPath = homeDir + "/" + destPath;

        try {            
            File localFile = new File(destPath);

            if(overwriteExisting)
            {
                System.out.println("Uploading: " + destPath + " to " + sourcePath);
                channelSftp.put(sourcePath, destPath);
            }
            
        } catch (SftpException ex) {
            Logger.getLogger(JSchSSHUtils.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            
            return false;
        }

        return true;        
    }
    
    public static String removePrefix(String s, String prefix)
    {
        if (s != null && prefix != null && s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }
}
