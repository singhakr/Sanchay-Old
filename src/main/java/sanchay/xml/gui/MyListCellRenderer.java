/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.xml.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author user
 */
public class MyListCellRenderer extends JLabel implements ListCellRenderer{

    private static final Color HIGHLIGHT_COLOR= new Color(0,0,128);
    
  /*  public MyListCellRenderer(){
        setOpaque(true);
    }*/
    
    public Component getListCellRendererComponent(JList list, Object value, int index, 
            boolean isSelected, boolean cellHasFocus) {
        //throw new UnsupportedOperationException("Not supported yet.");
        XMLQueryListItem li= (XMLQueryListItem) value;
        setText(li.getItemName());
        /*if(isSelected){
            setBackground(HIGHLIGHT_COLOR);
            setForeground(Color.GRAY);
        }
        else{
            setBackground(Color.GRAY);
            setForeground(Color.black);
        }*/
        return this;
    }

}
