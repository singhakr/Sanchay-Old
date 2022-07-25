/*
 * EncodingConverterUtils.java
 *
 * Created on January 16, 2008, 4:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.text.enc.conv;

/**
 *
 * @author anil
 */
public class EncodingConverterUtils {
    
    /** Creates a new instance of EncodingConverterUtils */
    public EncodingConverterUtils() {
    }
    
    public static SanchayEncodingConverter createEncodingConverter(String langEncFrom, String langEncTo)
    {
        if(langEncFrom == null ||langEncTo == null)
            return null;

        SanchayEncodingConverter converter = null;
        
        if(langEncFrom.endsWith("::utf8") && langEncTo.endsWith("::wx"))
        {
            converter = new UTF82WX(langEncFrom);
        }
        else if(langEncFrom.endsWith("::wx") && langEncTo.endsWith("::utf8"))
        {
            converter = new WX2UTF8(langEncTo);
        }
        else if(langEncFrom.endsWith("::suman") && langEncTo.endsWith("::utf8"))
        {
            converter = new Suman2UTF8(langEncTo);
        }
        else if
        (
                (langEncFrom.endsWith("::utf8") && langEncTo.endsWith("::utf8"))
                || langEncFrom.startsWith("kas::")
        )
        {
            converter = new UTF8ToUTF8(langEncFrom, langEncTo);
        }
        
        return converter;
    }
    
    public static String[] getFromEncodings()
    {
        return new String[]
        {
            "hin::utf8",
            "ben::utf8",
            "gur::utf8",
            "guj::utf8",
            "ori::utf8",
            "tam::utf8",
            "tel::utf8",
            "kan::utf8",
            "kas::utf8",
            "kas::roman",
            "mal::utf8",
            "hin::wx",
            "ben::wx",
            "gur::wx",
            "guj::wx",
            "ori::wx",
            "tam::wx",
            "tel::wx",
            "kan::wx",
            "mal::wx",
            "hin::suman"
        };
    }
    
    public static String[] getToEncodings()
    {
        return new String[]
        {
            "hin::utf8",
            "ben::utf8",
            "gur::utf8",
            "guj::utf8",
            "ori::utf8",
            "tam::utf8",
            "tel::utf8",
            "kan::utf8",
            "kas::utf8",
            "kas::roman",
            "mal::utf8",
            "hin::wx",
            "ben::wx",
            "gur::wx",
            "guj::wx",
            "ori::wx",
            "tam::wx",
            "tel::wx",
            "kan::wx",
            "mal::wx"
        };        
    }
}
