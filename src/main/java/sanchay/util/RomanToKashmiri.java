/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import sanchay.GlobalProperties;

/**
 *
 * @author eklavya
 */
public class RomanToKashmiri {
    /** Creates a new instance of UrduToRoman */
    public RomanToKashmiri() {
    }
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        if(args.length != 2)
        {
            System.out.println(GlobalProperties.getIntlString("USAGE:"));
            System.out.println("java -cp dist/Sanchay.jar sanchay.util.RomanToKashmiri inFilePath outFilePath");
            System.out.println(GlobalProperties.getIntlString("\tFile_path_could_be_of_a_file_or_directory."));
            System.out.println(GlobalProperties.getIntlString("\tConversion_will_be_performed_recursively."));
            System.exit(1);
        }
        
        String inFilePath = args[0];
        String outFilePath = args[1];
        
        try {
            UtilityFunctions.romanToKashmiriBatch(inFilePath, outFilePath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
