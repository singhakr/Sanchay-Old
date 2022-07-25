/*
 * SanchayBackup.java
 *
 * Created on May 22, 2007, 8:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class SanchayBackup implements FileChangeListener {
    
    public static String bakExt = GlobalProperties.getIntlString(".bak");
//    private static final SanchayBackup instance = new SanchayBackup();
    
    /** Creates a new instance of SanchayBackup */
    public SanchayBackup()
    {
    }

//    public static SanchayBackup getInstance() {
//        return instance;
//    }
    
    public void fileChanged(String filePath)
    {
        try {
            SanchayBackup.backup(filePath);
            System.out.println(GlobalProperties.getIntlString("File_") + filePath + GlobalProperties.getIntlString("_backed_up."));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void copy(String src, String dst) throws IOException
    {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        
        in.close();
        out.close();
    }    

    public static void backup(String filePath) throws IOException
    {
        String bakFilePath = filePath + bakExt;
        
        File bakFile = new File(bakFilePath);
        bakFile.delete();
        
        SanchayBackup.copy(filePath, bakFilePath);
    }
}
