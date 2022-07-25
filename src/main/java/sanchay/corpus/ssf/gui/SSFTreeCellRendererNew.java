/*
 * SSFTreeCellRendererNew.java
 *
 * Created on August 11, 2006, 5:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.tree.gui.MultiLingualTreeCellRenderer;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author Anil Kumar Singh
 */
public class SSFTreeCellRendererNew extends MultiLingualTreeCellRenderer implements TreeCellRenderer
{

    protected int mode = 0;

    public static final int SYNTACTIC_ANNOTATION = 0;
    public static final int PROPBANK_ANNOTATION = 1;
    
    /** Creates a new instance of SSFTreeCellRendererNew */
    public SSFTreeCellRendererNew(int cols, String langEncs[], int mode)
    {
	super(cols, langEncs);
        
        this.mode = mode;
    }

    public int getMode()
    {
        return mode;
    }

    public void setMode(int m)
    {
        mode = m;
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
    	super.getTreeCellRendererComponent(
                tree, value, selected,
                expanded, leaf, row,
                hasFocus);

        DefaultMutableTreeNode nodeRendered = (DefaultMutableTreeNode) value;
        
        FeatureStructures fs = null;
        String fsStr = "";
        String nameStr = "";
        String ldStr = "";

        if(nodeRendered.getClass().equals(SSFPhrase.class))
        {
            nameStr = ((SSFNode) nodeRendered).getName();
            ldStr = ((SSFNode) nodeRendered).getLexData();
	    
            fs = ((SSFNode) nodeRendered).getFeatureStructures();

            fs.hideAttribute(SSFNode.HIGHLIGHT);

            if(mode == PROPBANK_ANNOTATION)
            {
                fs.hideAttribute("drel");
                fs.hideAttribute("dmrel");
                fs.hideAttribute("reftype");
                fs.hideAttribute("coref");
            }

            fsStr = "";

            if(fs != null)
//                fsStr = fs.makeString();
                fsStr = fs.makeStringForRendering();
        }
        else if(nodeRendered.getClass().equals(SSFLexItem.class))
        {
            nameStr = ((SSFNode) nodeRendered).getName();
            ldStr = ((SSFNode) nodeRendered).getLexData();

            fs = ((SSFNode) nodeRendered).getFeatureStructures();
            fsStr = "";

            if(fs != null)
            {
//                fsStr = fs.makeString();

                fs.hideAttribute(SSFNode.HIGHLIGHT);

                if(mode == PROPBANK_ANNOTATION)
                {
                    fs.hideAttribute("drel");
                    fs.hideAttribute("dmrel");
                    fs.hideAttribute("reftype");
                    fs.hideAttribute("coref");
                }
                
                fsStr = fs.makeStringForRendering();
            }
        }
	
        JLabel nameLabel = (JLabel) getComponent(0);
        JLabel ldLabel = (JLabel) getComponent(1);
        JLabel fsLabel = (JLabel) getComponent(2);

//        UtilityFunctions.setComponentFont(nameLabel, langEncs[0]);
//        UtilityFunctions.setComponentFont(ldLabel, langEncs[1]);
//        UtilityFunctions.setComponentFont(fsLabel, langEncs[2]);

//    if(nodeRendered instanceof SSFNode && ((SSFNode) nodeRendered).isHighlighted())
//    {
//        nameLabel.setBackground(Color.YELLOW);
//        ldLabel.setBackground(Color.YELLOW);
//        fsLabel.setBackground(Color.YELLOW);
//    }

        nameLabel.setForeground(new Color(0, 0, 140));
        ldLabel.setForeground(new Color(140, 0, 0));
        fsLabel.setForeground(new Color(0, 140, 0));

        nameLabel.setText(nameStr);
        ldLabel.setText(ldStr);

//        FeatureStructures tfss = new FeatureStructuresImpl();
//
//        try
//        {
//            tfss.readString(fsStr);
//
//            if(tfss.countAltFSValues() > 0)
//            {
//                FeatureStructure tfs = tfss.getAltFSValue(0);
//                tfs.removeAttribute(SSFNode.HIGHLIGHT);
//
////                fsStr = tfss.makeString();
//                fsStr = tfss.makeStringForRendering();
//            }
//        } catch (Exception ex)
//        {
//            Logger.getLogger(SSFTreeCellRendererNew.class.getName()).log(Level.SEVERE, null, ex);
//        }

//        String hregex = " " + SSFNode.HIGHLIGHT + "[\\s]*=[\\s]*[\'\"][^\'^\"]*[\'\"]";
//
//        fsStr.replaceAll(hregex, "");
//        fsStr.replaceAll(hregex, "");

        fsLabel.setText(fsStr);

        setLanguages();

        return this;
    }
}
