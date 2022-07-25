/*
 * FSTreeCellRenderer.java
 *
 * Created on October 10, 2005, 6:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.gui;

import java.awt.*;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.tree.*;
        
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.features.impl.*;
import sanchay.util.UtilityFunctions;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public class FSTreeCellRenderer extends DefaultTreeCellRenderer {

    java.util.ResourceBundle bundle = sanchay.GlobalProperties.getResourceBundle(); // NOI18N

    private static ImageIcon featureStructuresOpenIcon;
    private static ImageIcon featureStructuresClosedIcon;

    private static ImageIcon featureStructureOpenIcon;
    private static ImageIcon featureStructureClosedIcon;

    private static ImageIcon featureAttributeOpenIcon;
    private static ImageIcon featureAttributeClosedIcon;

    private static ImageIcon featureValueIcon;

    private String language;
    
    /** Creates a new instance of FSTreeCellRenderer */
    public FSTreeCellRenderer(String lang) {
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

        Font presentFont = this.getFont();
	UtilityFunctions.setComponentFont(this, language);
//        setFont(new Font("Lohit Bengali", Font.BOLD, 20));
//        setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
//        setFont(presentFont.deriveFont(Font.BOLD, 14));
        
        DefaultMutableTreeNode nodeRendered = (DefaultMutableTreeNode) value;
        String textHTML = "";
        
        if(nodeRendered.getClass().getName().endsWith("FeatureStructuresImpl"))
        {
//            textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><font face=\"helvetica,arial,sans-serif\" color=#000080>Feature Structures</font>";
            textHTML = "<html><META_http-equiv=\"Content-Type\"_content=\"text/html;_charset=UTF-8\"><font_face=\"helvetica,arial,sans-serif\"_color=#000080>Feature_Structures</font>";

            setOpenIcon(FSTreeCellRenderer.getFeatureStructuresOpenIcon());
            setClosedIcon(FSTreeCellRenderer.getFeatureStructuresClosedIcon());
            setLeafIcon(FSTreeCellRenderer.getFeatureStructuresOpenIcon());
        }
        else if(nodeRendered.getClass().getName().endsWith("FeatureStructureImpl"))
        {
            String name = ((FeatureStructureImpl) nodeRendered).getName();
            
            textHTML = "<html><META_http-equiv=\"Content-Type\"_content=\"text/html;_charset=UTF-8\"><FONT_face=\"helvetica,arial,sans-serif\"_color=#000080>Feature_Structure_"
                    + "</font><font face=\"helvetica,arial,sans-serif\" color=#008000>" + name + "</font></html>";

            setOpenIcon(FSTreeCellRenderer.getFeatureStructureOpenIcon());
            setClosedIcon(FSTreeCellRenderer.getFeatureStructureClosedIcon());
            setLeafIcon(FSTreeCellRenderer.getFeatureStructureOpenIcon());
        }
        else if(nodeRendered.getClass().getName().endsWith("FeatureAttributeImpl"))
        {
            String name = ((FeatureAttributeImpl) nodeRendered).getName();

            textHTML = "<html><META_http-equiv=\"Content-Type\"_content=\"text/html;_charset=UTF-8\"><FONT_face=\"helvetica,arial,sans-serif\"_color=#000080>Feature_Attribute_"
                    + "</font><font face=\"helvetica,arial,sans-serif\" color=#008000>" + name + "</font></html>";

            setOpenIcon(FSTreeCellRenderer.getFeatureAttributeOpenIcon());
            setClosedIcon(FSTreeCellRenderer.getFeatureAttributeClosedIcon());
            setLeafIcon(FSTreeCellRenderer.getFeatureAttributeOpenIcon());
        }
        else if(nodeRendered.getClass().getName().endsWith("FeatureValueImpl"))
        {
            String val = (String) ((FeatureValueImpl) nodeRendered).getValue();

            textHTML = "<html><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><FONT face=\"helvetica,arial,sans-serif\" color=#800000>"
                    + val + "</font></html>";

            setOpenIcon(FSTreeCellRenderer.getFeatureValueIcon());
            setClosedIcon(FSTreeCellRenderer.getFeatureValueIcon());
            setLeafIcon(FSTreeCellRenderer.getFeatureValueIcon());
        }

        setText(textHTML);
        
        return this;
    }
   
    public static ImageIcon getFeatureStructuresOpenIcon()
    {
        return featureStructuresOpenIcon;
    }
    
    public static ImageIcon getFeatureStructuresClosedIcon()
    {
        return featureStructuresClosedIcon;
    }
    
    public static ImageIcon getFeatureStructureOpenIcon()
    {
        return featureStructureOpenIcon;
    }
    
    public static ImageIcon getFeatureStructureClosedIcon()
    {
        return featureStructureClosedIcon;
    }
    
    public static ImageIcon getFeatureAttributeOpenIcon()
    {
        return featureAttributeOpenIcon;
    }
    
    public static ImageIcon getFeatureAttributeClosedIcon()
    {
        return featureAttributeClosedIcon;
    }
    
    public static ImageIcon getFeatureValueIcon()
    {
        return featureValueIcon;
    }
}
