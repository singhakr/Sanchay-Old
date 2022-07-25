/*
 * SSFTreeCellRenderer.java
 *
 * Created on October 10, 2005, 6:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.regex.*;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.tree.*;
import sanchay.corpus.ssf.features.*;
import sanchay.util.UtilityFunctions;
/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public class SSFTreeCellRenderer extends DefaultTreeCellRenderer {

    private static ImageIcon phraseOpenIcon;
    private static ImageIcon phraseClosedIcon;
    private static ImageIcon lexItemIcon;
    private String language;
    
    /** Creates a new instance of SSFTreeCellRenderer */
    public SSFTreeCellRenderer(String lang) {
	language = lang;
    }

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
    {
        super.getTreeCellRendererComponent(
            tree, value, sel,
            expanded, leaf, row,
            hasFocus);

        setOpenIcon(SSFTreeCellRenderer.getPhraseOpenIcon());
        setClosedIcon(SSFTreeCellRenderer.getPhraseClosedIcon());
        setLeafIcon(SSFTreeCellRenderer.getLexItemIcon());

	UtilityFunctions.setComponentFont(this, language);
        String fontFamily = getFont().getFamily();
//	System.out.println(language + ":\t" + fontFamily);
//        Font presentFont = this.getFont();
//        setFont(new Font("Lohit Bengali", Font.BOLD, 18));
//        setFont(presentFont.deriveFont(Font.BOLD, 26));
        
        DefaultMutableTreeNode nodeRendered = (DefaultMutableTreeNode) value;
        String textHTML = "";
        
        FeatureStructures fs = null;
        String fsStr = "";
        String nameStr = "";
        String ldStr = "";

        if(nodeRendered.getClass().equals(SSFPhrase.class))
        {
	    nameStr = ((SSFNode) nodeRendered).getName();
	    ldStr = ((SSFNode) nodeRendered).getLexData();
	    
            fs = ((SSFNode) nodeRendered).getFeatureStructures();
            fsStr = "";

            if(fs != null)
                fsStr = fs.makeString();

	    if(language == null || language.equals(GlobalProperties.getIntlString("hin::utf8")) || language.equals(GlobalProperties.getIntlString("eng")) || language.equals(GlobalProperties.getIntlString("eng::utf8")))
	    {
		Pattern p1 = Pattern.compile("<");

		Matcher m1 = p1.matcher(fsStr);
		fsStr = m1.replaceAll("&lt;");
		m1 = p1.matcher(nameStr);
		nameStr = m1.replaceAll("&lt;");
		m1 = p1.matcher(ldStr);
		ldStr = m1.replaceAll("&lt;");

		Pattern p2 = Pattern.compile(">");

		Matcher m2 = p2.matcher(fsStr);
		fsStr = m2.replaceAll("&gt;");
		m2 = p2.matcher(nameStr);
		nameStr = m2.replaceAll("&gt;");
		m2 = p2.matcher(ldStr);
		ldStr = m2.replaceAll("&gt;");
	    }
            
//            textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><font face=\"helvetica,arial,sans-serif\" color=#000080>"
//                    + ((SSFNode) nodeRendered).getName() + " </font><font face=\"helvetica,arial,sans-serif\" color=#008000>"
//                    + fsStr + "</font></html>";

//	    if(((SSFNode) nodeRendered).getLexData().equals("") == true)
//		textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><font color=#000080>"
//			+ ((SSFNode) nodeRendered).getName() + " </font><font color=#008000>"
//			+ fsStr + "</font></html>";
//	    else
//		textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><FONT color=#000080>"
//			+ ((SSFNode) nodeRendered).getName() + " </font><font color=#800000>" + ((SSFNode) nodeRendered).getLexData()
//			+ " </font><font color=#008000>" + fsStr + "</font></html>";

	    if(((SSFNode) nodeRendered).getLexData().equals("") == true)
		textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><font face=\"helvetica,arial,sans-serif\" color=#000080>"
			+ nameStr + " </font><font face=\"helvetica,arial,sans-serif\" color=#008000>"
			+ fsStr + "</font></html>";
	    else
		textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><font face=\"helvetica,arial,sans-serif\"color=#000080>"
			+ nameStr + " </font><font face=\"" + fontFamily + "\" color=#800000>" + ldStr
			+ " </font><FONT face=\"helvetica,arial,sans-serif\" color=#008000>" + fsStr + "</font></html>";
	}
        else if(nodeRendered.getClass().equals(SSFLexItem.class))
        {
	    nameStr = ((SSFNode) nodeRendered).getName();
	    ldStr = ((SSFNode) nodeRendered).getLexData();

	    fs = ((SSFNode) nodeRendered).getFeatureStructures();
            fsStr = "";

            if(fs != null)
                fsStr = fs.makeString();
            
	    if(language == null || language.equals(GlobalProperties.getIntlString("hin::utf8")) || language.equals(GlobalProperties.getIntlString("eng")) || language.equals(GlobalProperties.getIntlString("eng::utf8")))
	    {
		Pattern p1 = Pattern.compile("<");

		Matcher m1 = p1.matcher(fsStr);
		fsStr = m1.replaceAll("&lt;");
		m1 = p1.matcher(nameStr);
		nameStr = m1.replaceAll("&lt;");
		m1 = p1.matcher(ldStr);
		ldStr = m1.replaceAll("&lt;");

		Pattern p2 = Pattern.compile(">");

		Matcher m2 = p2.matcher(fsStr);
		fsStr = m2.replaceAll("&gt;");
		m2 = p2.matcher(nameStr);
		nameStr = m2.replaceAll("&gt;");
		m2 = p2.matcher(ldStr);
		ldStr = m2.replaceAll("&gt;");
	    }
            
//            textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><FONT face=\"helvetica,arial,sans-serif\" color=#000080>"
//                    + ((SSFNode) nodeRendered).getName() + " </font><font face=\"helvetica,arial,sans-serif\" color=#800000>" + ((SSFNode) nodeRendered).getLexData()
//                    + " </font><font face=\"helvetica,arial,sans-serif\" color=#008000>" + fsStr + "</font></html>";
            
//            textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><FONT color=#000080>"
//                    + ((SSFNode) nodeRendered).getName() + " </font><font color=#800000>" + ((SSFNode) nodeRendered).getLexData()
//                    + " </font><font color=#008000>" + fsStr + "</font></html>";

            textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><font face=\"helvetica,arial,sans-serif\" color=#000080>"
                    + nameStr + " </font><font face=\"" + fontFamily + "\" color=#800000>" + ldStr
                    + " </font><font face=\"helvetica,arial,sans-serif\" color=#008000>" + fsStr + "</font></html>";
	}

	if(language == null || language.equals(GlobalProperties.getIntlString("hin::utf8")) || language.equals(GlobalProperties.getIntlString("eng")) || language.equals(GlobalProperties.getIntlString("eng::utf8")))
	    setText(textHTML);
	else
	{
	    setForeground(new Color(0, 0, 140));
	    
	    String text = "";
	    
	    if(nameStr.equals(""))
		text = ldStr;
	    else if(ldStr.equals(""))
		text = nameStr;
	    else
		text = nameStr + " :: " + ldStr;
	    
	    if(fsStr.equals("") == false)
		text = text + " " + fsStr;

	    setText(text);
	}
        
        return this;
    }

    public static ImageIcon getPhraseOpenIcon()
    {
        return phraseOpenIcon;
    }
    
    public static ImageIcon getPhraseClosedIcon()
    {
        return phraseClosedIcon;
    }
    
    public static ImageIcon getLexItemIcon()
    {
        return lexItemIcon;
    }
}
