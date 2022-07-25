/*
 * FlattenDirectoryStructure.java
 *
 * Created on December 17, 2007, 8:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public class FlattenDirectoryStructure {
    
    /** Creates a new instance of FlattenDirectoryStructure */
    public FlattenDirectoryStructure() {
    }

    public static void main(String[] args) {
        
        String path = "F:\\docs\\corpus-building\\lexmasterclass\\wildgreenyonder";
        
        if(args.length > 0)
            path = args[0];
        
        String charset = GlobalProperties.getIntlString("UTF-8");

        if(args.length > 1)
            charset = args[1];

        // 
        // ***** BE CAREFUL ABOUT THIS *****
        //
        boolean delOriginal = true;
        // 
        // ***** BE CAREFUL ABOUT THIS *****
        //
        
        if(args.length > 2)
            delOriginal = Boolean.parseBoolean(args[2]);
        
        File pathFile = new File(path);
        
        try {  
            UtilityFunctions.flattenDirectoryStructure(pathFile, pathFile, delOriginal, null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
