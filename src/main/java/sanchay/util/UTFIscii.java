/*
 * UTFIscii.java
 *
 * Created on January 27, 2007, 5:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util;

import java.io.UnsupportedEncodingException;
import sanchay.GlobalProperties;

/**
 *
 * @author root
 */
public class UTFIscii {
    
    /** Creates a new instance of UTFIscii */
    public UTFIscii() {
    }

    public static boolean isUTF8(String str)
    {
        int utfCount = 0;
        int count = str.length();
        
        for(int i = 0; i < count; i++)
        {
            char ch = str.charAt(i);

            if(isUTF8(ch))
                utfCount++;
        }

        boolean isUTF8 = true;

        if(utfCount > (count - utfCount))
            isUTF8 = true;
        else
            isUTF8 = false;
        
        return isUTF8;
    }

    public static boolean isUTF8Old1(String str)
    {
        boolean isUTF8 = true;
        int utfCount = 0;
        
        int count = str.length();
        
        for(int i = 0; i < count; i++)
        {
            char ch = str.charAt(i);
            
            if(ch == '\ufffd')
            {
                isUTF8 = false;
            }
            else
            {
                utfCount++;
                isUTF8 = true;
            }
        
//            System.out.println("Is UTF8: " + isUTF8);
//            System.out.println("UnicodeBlock: " + ub);
        }

        if(utfCount > (count - utfCount))
            isUTF8 = true;
        else
            isUTF8 = false;

 //       System.out.println("Is UTF8: " + isUTF8);
        
        return isUTF8;
    }

    public static boolean isUTF8(char ch)
    {
        if(ch == '\ufffd')
            return false;
            
        int codePoint = (int) ch;

        Character.UnicodeBlock ub = Character.UnicodeBlock.of(codePoint);

        if(ub == null)// || Character.isLetterOrDigit(codePoint) == false)
        {
            return false;
        }
        else if
        (
                ub.equals(Character.UnicodeBlock.BASIC_LATIN)
                || ub.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT)
                || ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_A)
                || ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_B)
                || ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL)
         )
        {
            return false;
        }

        return true;
    }
    
    public static boolean isUTF8Old(String str)
    {
        boolean isUTF8 = true;
        int utfCount = 0;
        
        int count = str.length();
        
        for(int i = 0; i < count; i++)
        {
            char ch = str.charAt(i);
            
            int codePoint = (int) ch;
            
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(codePoint);
            
            if(ub == null)
            {
                isUTF8 = false;
            }
            else if
            (
                    ub.equals(Character.UnicodeBlock.BASIC_LATIN) == false
                    && ub.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) == false
                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) == false
                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) == false
                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) == false
             )
            {
                utfCount++;
                isUTF8 = true;
            }
        
//            System.out.println("Is UTF8: " + isUTF8);
//            System.out.println("UnicodeBlock: " + ub);
        }

        if(utfCount > (count - utfCount))
            isUTF8 = true;
        else
            isUTF8 = false;

 //       System.out.println("Is UTF8: " + isUTF8);
        
        return isUTF8;
    }
    
//    public static boolean isUTF8(String str)
//    {
//        String isoStr = null;
//        String utf8Str = null;
//
//        try {
//            isoStr = new String(str.getBytes(), "ISO-8859-1");
//            utf8Str = new String(str.getBytes(), "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace();
//        }
//        
//        boolean isUTF8 = true;
//        int utfCount = 0;
//        
//        int count = Math.min(isoStr.length(), utf8Str.length());
//        
//        for(int i = 0; i < count; i++)
//        {
//            char isoCh = isoStr.charAt(i);
//            char utfCh = utf8Str.charAt(i);
//            
//            int isoCodePoint = (int) isoCh;
//            int utf8CodePoint = (int) utfCh;
//            
//            Character.UnicodeBlock isoUB = Character.UnicodeBlock.of(isoCodePoint);
//            Character.UnicodeBlock utf8UB = Character.UnicodeBlock.of(utf8CodePoint);
//            
//            if(isoUB == null)
//            {
//                isUTF8 = false;
//            }
//            else if(utf8UB == null)
//            {
//                isUTF8 = false;
//            }
//            else if
//            (
//                    (isoUB.equals(Character.UnicodeBlock.BASIC_LATIN) == false
//                    && isoUB.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) == false
//                    && isoUB.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) == false
//                    && isoUB.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) == false
//                    && isoUB.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) == false)
//                        ||
//                    (utf8UB.equals(Character.UnicodeBlock.BASIC_LATIN) == false
//                    && utf8UB.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) == false
//                    && utf8UB.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) == false
//                    && utf8UB.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) == false
//                    && utf8UB.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) == false)
//             )
//            {
//                utfCount++;
//                isUTF8 = true;
//            }
//            else
//            {
//                isUTF8 = false;
//            }
//        
////            System.out.println("Is UTF8: " + isUTF8);
////            System.out.println("UnicodeBlock: " + ub);
//        }
//
//        if(utfCount > (count - utfCount))
//            isUTF8 = true;
//        else
//            isUTF8 = false;
//
// //       System.out.println("Is UTF8: " + isUTF8);
//        
//        return isUTF8;
//    }
    
    public static void main(String[] args) {
        String iscStr = "???????";
        String utf8Str = "????????";

        String iscStrConv = null;
        String utf8StrConv = null;

        try {
            iscStrConv = new String(iscStr.getBytes(), GlobalProperties.getIntlString("UTF-8"));
            utf8StrConv = new String(utf8Str.getBytes(), GlobalProperties.getIntlString("ISO-8859-1"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        
        System.out.println(GlobalProperties.getIntlString("Is_UTF8:_") + isUTF8(iscStr));
        System.out.println(GlobalProperties.getIntlString("Is_UTF8:_") + isUTF8(utf8Str));
        System.out.println(GlobalProperties.getIntlString("Is_UTF8:_") + isUTF8(iscStrConv));
        System.out.println(GlobalProperties.getIntlString("Is_UTF8:_") + isUTF8(utf8StrConv));
        
//        for(int i = 0; i < iscStr.length(); i++)
//        {
//            char ch = iscStr.charAt(i);
//            char chConv = iscStrConv.charAt(i);
////            Character ch = new Character(iscStr.charAt(i));
//            int codePoint = (int) ch;
//            int codePointConv = (int) chConv;
//            
//            Character.UnicodeBlock ub = Character.UnicodeBlock.of(codePoint);
//            Character.UnicodeBlock ubConv = Character.UnicodeBlock.of(codePointConv);
//            
//            if
//            (
//                    (ub.equals(Character.UnicodeBlock.BASIC_LATIN) == false
//                    && ub.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) == false
//                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) == false
//                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) == false
//                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) == false)
//                        ||
//                    (ubConv.equals(Character.UnicodeBlock.BASIC_LATIN) == false
//                    && ubConv.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) == false
//                    && ubConv.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) == false
//                    && ubConv.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) == false
//                    && ubConv.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) == false)
//             )
//            {
//                isUTF8 = true;
//            }
//            else
//            {
//                isUTF8 = false;
//            }
//        
//            System.out.println("Is UTF8: " + isUTF8);
//            System.out.println("UnicodeBlock: " + ub);
//        }
//
//        for(int i = 0; i < utf8Str.length(); i++)
//        {
//            char ch = utf8Str.charAt(i);
//            char chConv = utf8StrConv.charAt(i);
////            Character ch = new Character(utf8Str.charAt(i));
//            int codePoint = (int) ch;
//            int codePointConv = (int) chConv;
//            
//            Character.UnicodeBlock ub = Character.UnicodeBlock.of(codePoint);
//            Character.UnicodeBlock ubConv = Character.UnicodeBlock.of(codePointConv);
//            
//            if
//            (
//                    (ub.equals(Character.UnicodeBlock.BASIC_LATIN) == false
//                    && ub.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) == false
//                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) == false
//                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) == false
//                    && ub.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) == false)
//                        ||
//                    (ubConv.equals(Character.UnicodeBlock.BASIC_LATIN) == false
//                    && ubConv.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) == false
//                    && ubConv.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) == false
//                    && ubConv.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) == false
//                    && ubConv.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) == false)
//             )
//            {
//                isUTF8 = true;
//            }
//            else
//            {
//                isUTF8 = false;
//            }
//        
//            System.out.println("Is UTF8: " + isUTF8);
//            System.out.println("UnicodeBlock: " + ub);
//        }
    }
}
