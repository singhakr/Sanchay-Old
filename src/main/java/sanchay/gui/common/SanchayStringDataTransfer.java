/*
 * SanchayStringDataTransfer.java
 *
 * Created on October 23, 2005, 5:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import java.awt.datatransfer.*;
import java.awt.*;
import java.io.*;
import sanchay.GlobalProperties;
/**
 *
 *  @author Anil Kumar Singh
 */
public class SanchayStringDataTransfer implements ClipboardOwner {

    /**
    * Empty implementation of the ClipboardOwner interface.
    */
    public void lostOwnership( Clipboard aClipboard, Transferable aContents) {
        //do nothing
    }

    /**
    * Place a String on the clipboard, and make this class the
    * owner of the Clipboard's contents.
    */
    public void setClipboardContents( String aString ){
        StringSelection stringSelection = new StringSelection( aString );
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( stringSelection, this );
    }

    /**
    * Get the String residing on the clipboard.
    *
    * @return any text found on the Clipboard; if none found, return an
    * empty String.
    */
    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) &&
                                      contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if ( hasTransferableText ) {
              try {
                result = (String)contents.getTransferData(DataFlavor.stringFlavor);
              }
              catch (UnsupportedFlavorException ex){
                //highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
              }
              catch (IOException ex) {
                System.out.println(ex);
              }
        }
        
        return result;
    }

    public static void main (String[]  aArguments ){
        SanchayStringDataTransfer tableTransfer = new SanchayStringDataTransfer();

        //display what is currently on the clipboard
        System.out.println(GlobalProperties.getIntlString("Clipboard_contains:") + tableTransfer.getClipboardContents() );

        //change the contents and then re-display
        tableTransfer.setClipboardContents(GlobalProperties.getIntlString("blah,_blah,_blah"));
        System.out.println(GlobalProperties.getIntlString("Clipboard_contains:") + tableTransfer.getClipboardContents() );
    }
} 
