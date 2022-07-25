/*
 * FileMonitor.java
 *
 * Created on May 22, 2007, 8:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util.file;

/**
 *
 * @author anil
 */

import java.util.*;
import java.io.*;
import java.net.*;
import sanchay.GlobalProperties;

public class FileMonitor {

    private static final FileMonitor instance = new FileMonitor();

    private Timer timer;
    private Hashtable timerEntries;

    public static FileMonitor getInstance() {
        return instance;
    }

    protected FileMonitor() { 
        // Create timer, run timer thread as daemon.
    	timer = new Timer(true);
	timerEntries = new Hashtable();
    }
    
    /** Add a monitored file with a FileChangeListener.
     * @param listener listener to notify when the file changed.
     * @param fileName name of the file to monitor.
     * @param period polling period in milliseconds.
     */
    public void addFileChangeListener(FileChangeListener listener, 
    				      String fileName, 
				      long period) 
			throws FileNotFoundException {
	removeFileChangeListener(listener, fileName);
	FileMonitorTask task = new FileMonitorTask(listener, fileName);
	timerEntries.put(fileName + listener.hashCode(), task);
	timer.schedule(task, period, period);
    }

    /** Remove the listener from the notification list.
     * @param listener the listener to be removed.
     */
    public void removeFileChangeListener(FileChangeListener listener, 
    					 String fileName) {
        FileMonitorTask task = (FileMonitorTask) timerEntries.remove(fileName 
							+ listener.hashCode());
	if (task != null) {
	    task.cancel();
	}
    }

    protected void fireFileChangeEvent(FileChangeListener listener, 
    					String fileName) {
	listener.fileChanged(fileName);
    }

    class FileMonitorTask extends TimerTask {
        FileChangeListener listener;
        String fileName;
        File monitoredFile;
	long lastModified;

        public FileMonitorTask(FileChangeListener listener, String fileName) 
			throws FileNotFoundException {
	    this.listener = listener;
	    this.fileName = fileName;
	    this.lastModified = 0;

	    monitoredFile = new File(fileName);
	    if (!monitoredFile.exists()) {  // but is it on CLASSPATH?
	        URL fileURL = 
		    listener.getClass().getClassLoader().getResource(fileName);
		if (fileURL != null) {
		    monitoredFile = new File(fileURL.getFile());
		}
		else {
		    throw new FileNotFoundException(GlobalProperties.getIntlString("File_Not_Found:_")
		    					+ fileName);
		}
	    }
	    this.lastModified = monitoredFile.lastModified();
	}

        public void run() {
	    long lastModified = monitoredFile.lastModified();
	    if (lastModified != this.lastModified) {
	        this.lastModified = lastModified;
		fireFileChangeEvent(this.listener, this.fileName);
	    }
	}
    }
}
