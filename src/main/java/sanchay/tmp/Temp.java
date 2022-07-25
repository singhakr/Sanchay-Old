/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.tmp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sanchay.xml.XMLUtils;

/**
 *
 * @author anil
 */
public class Temp {
    
    public static void tryParse()
    {
        try {
//            org.w3c.dom.Element rootElement = XMLUtils.parseDomXML("/home/anil/projects/trec/trec-cds/trec-cds-data-sample/nxml/2631534.nxml");
//
//            NodeList paragraphs = rootElement.getElementsByTagName("p");

            org.jdom.Element rootElement = null;
            try {
                rootElement = XMLUtils.parseJDOMXML("file:///home/anil/projects/trec/trec-cds/trec-cds-data-sample/nxml/2631534.nxml", "UTF-8");
            } catch (JDOMException ex) {
                Logger.getLogger(Temp.class.getName()).log(Level.SEVERE, null, ex);
            }

//            org.w3c.dom.Element rootElement = null;
//
//            try {
//                rootElement = XMLUtils.parseW3CXML("/home/anil/projects/trec/trec-cds/trec-cds-data-sample/nxml/2631534.nxml",
//                        "UTF-8", false);
//            } catch (SAXException ex) {
//                Logger.getLogger(Temp.class.getName()).log(Level.SEVERE, null, ex);
//            }

//            try {
//              Builder parser = new Builder();
//              Document doc = parser.build("http://www.cafeconleche.org/");
//            }
//            catch (ParsingException ex) {
//              System.err.println("Cafe con Leche is malformed today. How embarrassing!");
//            }
//            catch (IOException ex) {
//              System.err.println("Could not connect to Cafe con Leche. The site may be down.");
//            }
            
            String tag = rootElement.getName();
            
            System.err.println("Root element: " + tag);        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Temp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Temp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[])
    {
//        try {
//            TwitterAccess.setProxy();
//            
////            URL url = new URL("http://stackoverflow.com/questions/1381617");
//            URL url = new URL("http://twitter.com/Pratya_Goyal/status/477299154381590528");
//            
//            URLConnection con = null;
//            
//            try {
//                con = url.openConnection();
//            } catch (IOException ex) {
//                Logger.getLogger(Temp.class.getName()).log(Level.SEVERE, null, ex);
//            }
            
//            Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
//            Matcher m = p.matcher(con.getContentType());
//            /* If Content-Type doesn't match this pre-conception, choose default and 
//             * hope for the best. */
//            String charset = m.matches() ? m.group(1) : "ISO-8859-1";
//            Reader r = null;
//            
//            try {
////                r = new InputStreamReader(con.getInputStream(), charset);
//                r = new InputStreamReader(con.getInputStream(), "UTF-8");
//            } catch (IOException ex) {
//                Logger.getLogger(Temp.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            StringBuilder buf = new StringBuilder();
//            while (true) {
//              int ch = 0;
//              
//                try {
//                    ch = r.read();
//                } catch (IOException ex) {
//                    Logger.getLogger(Temp.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                
//              if (ch < 0)
//                break;
//              buf.append((char) ch);
//            }
//            String str = buf.toString();
//            
//            System.out.println(str);
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(Temp.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        tryParse();
    }
}
