/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.help;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Locale;
import java.io.IOException;
import javax.help.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class HelpApp extends JApplet{
    private String helpSetName = null;
    private String helpSetURL = null;
    private HelpSet hs;
    private HelpBroker hb;
    static JFrame frame;

    public HelpApp() {
	super();
    }

    public HelpApp(String hsName, String hsURL) {
	helpSetName = hsName;
	helpSetURL = hsURL;
        if (hs == null) {
	    createHelpSet();
	    hb = hs.createHelpBroker();
	}
	hb.setDisplayed(true);
    }

    public void init() {
	helpSetName = getParameter("HELPSETNAME");
	helpSetURL = getParameter("HELPSETURL");
    }


    public void stop() {
	hs = null;
	hb = null;
    }

    private void createHelpSet() {
	ClassLoader loader = this.getClass().getClassLoader();
	URL url;
	try {
//            System.out.println("HHEERE....   "+helpSetName);
            
	    url = HelpSet.findHelpSet(loader, helpSetName);
            
//            System.out.println("HHEERE....   "+url);
            
	    debug ("findHelpSet url=" + url);
	    if (url == null) {
		url = new URL(getCodeBase(), helpSetURL);
		debug("codeBase url=" + url);
	    }
	    hs = new HelpSet(loader, url);
	} catch (Exception ee) {
//	    System.out.println ("Trouble in createHelpSet;");
	    ee.printStackTrace();
	    return;
	}
    }

       /**
     * For printf debugging.
     */
    private final boolean debug = false;
    private void debug(String str) {
        if (debug) {
//            System.out.println("HelpButton: " + str);
        }
    }

}
 