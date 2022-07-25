/*
 * SSFTreeCellRendererNew.java
 *
 * Created on August 11, 2006, 4:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.tree.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.gui.common.SanchayLanguages;

import sanchay.util.UtilityFunctions;/**
 *
 * @author anil
 */
public class MultiLingualTreeCellRenderer extends JPanel implements TreeCellRenderer
{
    protected static int columns;
    protected static String langEncs[];

    protected int fontSize = SanchayLanguages.DEFAULT_FONT_SIZE;
    protected int fontSizeEng = SanchayLanguages.DEFAULT_FONT_SIZE_ENG;
    
    /** Creates a new instance of SSFTreeCellRendererNew */
    public MultiLingualTreeCellRenderer(int cols, String langEncs[])
    {
	columns = cols;
	this.langEncs = langEncs;
	
	initComponents();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        DefaultMutableTreeNode nodeRendered = (DefaultMutableTreeNode) value;

        if(selected)
        {
            setBackground(UIManager.getColor("Tree.selectionBackground"));
        }
        else if(nodeRendered instanceof SSFNode && ((SSFNode) nodeRendered).isHighlighted())
            setBackground(Color.YELLOW);
        else
        {
            setBackground(new Color(255, 255, 255));
        }
	
        for (int i = 0; i < columns; i++)
        {
            JLabel label = (JLabel) getComponent(i);
            Font font = label.getFont();
            String txt = label.getText();
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            FontMetrics fm = toolkit.getFontMetrics(font);
            int height = label.getHeight();
            int width = fm.stringWidth(txt);
            label.setSize(width, height);

//            if(nodeRendered instanceof SSFNode && ((SSFNode) nodeRendered).isHighlighted())
//            {
//                label.setBackground(Color.YELLOW);
//            }
        }

//	if(hasFocus)
//	{
//            setBorder(javax.swing.BorderFactory.createLineBorder(Color.RED));
//	}
//        else
//	{
//            setBorder(javax.swing.BorderFactory.createEmptyBorder());
//	}
            
        setLanguages();

        return this;
    }
    
    protected void initComponents()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
	setOpaque(true);
	
	for (int i = 0; i < columns; i++)
	{
	    JLabel label = new JLabel();
	    add(label);
	}
    }
    
    protected void setLanguages()
    {
	for (int i = 0; i < columns; i++)
	{
	    Component c = getComponent(i);
	    UtilityFunctions.setComponentFont(c, langEncs[i]);
	    Font font = c.getFont();
	    Font newFont = null;
	    
//	    int style = font.getStyle();

            int style = Font.BOLD;
	    
	    if(langEncs[i].startsWith("eng"))
		newFont = font.deriveFont(style, fontSizeEng);
	    else
		newFont = font.deriveFont(style, fontSize);
	    
	    c.setFont(newFont);
	}
    }
    
    public static int getColumnCount()
    {
	return columns;
    }
    
    public static void getColumnCount(int col)
    {
	columns = col;
    }
    
    public static String getLangEnc(int col)
    {
	return langEncs[col];
    }
    
    public static void setLangEnc(String langEnc, int col)
    {
	langEncs[col] = langEnc;
    }

    public void setFontSize(int size)
    {
        fontSize = size;
    }
    
    public void increaseFontSize()
    {
	fontSize += 1;
	fontSizeEng += 1;
    }
    
    public void decreaseFontSize()
    {
	fontSize -= 1;
	fontSizeEng -= 1;
    }
}
