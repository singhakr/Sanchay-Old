/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author User
 */
public class SanchayStringUtils {

    public static String getLastOccurrenceOfRegex(String inString, String begString, String endString)
    {
        Pattern pattern3 = Pattern.compile(begString + "(.*?)" + endString);

        Matcher matcher3 = pattern3.matcher(inString);

        String s = null;

        while (matcher3.find()) {
            s = matcher3.group();
        }
        
        return s;
    }

    public static String getLastOccurrenceOfRegex(String inString, String regex)
    {
        Pattern pattern3 = Pattern.compile(regex);

        Matcher matcher3 = pattern3.matcher(inString);

        String s = null;

        while (matcher3.find()) {
            s = matcher3.group();
        }
        
        return s;
    }

    public static int getLastStartIndexOfRegex(String inString, String regex)
    {
        Pattern pattern3 = Pattern.compile(regex);

        Matcher matcher3 = pattern3.matcher(inString);

        String s = null;
        
        int index = -1;

        while (matcher3.find()) {
            index = matcher3.start();
        }
        
        return index;
    }
}
