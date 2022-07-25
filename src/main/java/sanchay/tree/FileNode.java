/*
 * FileNode.java
 *
 * Created on May 16, 2006, 4:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.tree;

import com.jcraft.jsch.ChannelSftp;
import java.io.File;
import javax.swing.tree.*;
import sanchay.server.dto.tree.ExplorableTreeNode;

public class FileNode extends DefaultMutableTreeNode implements ExplorableTreeNode
{
    protected boolean explored = false;
    
    protected FileNode(File file, ChannelSftp.LsEntry remoteFile) {
	setUserObject(file);
    }
    
    public static FileNode getFileNodeInstance(File file)
    {
        FileNode fn = new FileNode(file, null);
        
        return fn;
    }

    public boolean getAllowsChildren() { return isDirectory(); }
    public boolean isLeaf() 	 { return !isDirectory(); }
    private File getFile()		 { return (File)getUserObject(); }
    public boolean isExplored() { return explored; }
    
    public boolean isDirectory() {
	File file = getFile();
	return file.isDirectory();
    }
    public String toString() {
	File file = (File)getUserObject();
	String filename = file.toString();
	int index = filename.lastIndexOf(File.separator);
	
	return (index != -1 && index != filename.length()-1) ?
	    filename.substring(index+1) :
	    filename;
    }
    public void explore() {
	if(!isDirectory())
	    return;
	
	if(!isExplored()) {
	    File file = getFile();
	    File[] children = file.listFiles();
	    
	    if(children == null)
		return;
	    
	    for(int i=0; i < children.length; ++i)
		add(new FileNode(children[i], null));
	    
	    explored = true;
	}
    }
}
