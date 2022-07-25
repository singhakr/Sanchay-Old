/*
 * KwikCellRenderer.java
 *
 * Created on March 25, 2009, 12:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.manager.gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class KwikCellRenderer extends JLabel implements TableCellRenderer {
    
    protected String langEnc = GlobalProperties.getIntlString("hin::utf8");
    
    /** Creates a new instance of KwikCellRenderer */
    public KwikCellRenderer(String langEnc) {
    }

    public Component getTableCellRendererComponent
    (
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    )
    {
        if(column == 0)
        {
            setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        }
        else if(column == 1)
        {
            setHorizontalAlignment(javax.swing.SwingConstants.CENTER);            
        }
        else if(column == 2)
        {
            setHorizontalAlignment(javax.swing.SwingConstants.LEFT);            
        }
        
        setText(value.toString());
        
        return this;
    }    
}
