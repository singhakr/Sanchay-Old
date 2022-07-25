package sanchay.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import sanchay.GlobalProperties;

public class ConvertCharset {

    public ConvertCharset() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        if(args.length != 5)
        {
            System.out.println(GlobalProperties.getIntlString("USAGE:"));
            System.out.println("java -cp dist/Sanchay.jar sanchay.util.ConvertCharset fromCharset toCharset inFilePath outFilePath extractHTML(true or false)");
            System.exit(1);
        }
        
        String fromCharset = args[0];
        String toCharset = args[1];
        String inFilePath = args[2];
        String outFilePath = args[3];
        boolean extractHTML = Boolean.parseBoolean(args[4]);
        
        try {
            UtilityFunctions.convertCharsetBatch(fromCharset, toCharset, inFilePath, outFilePath, extractHTML);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
