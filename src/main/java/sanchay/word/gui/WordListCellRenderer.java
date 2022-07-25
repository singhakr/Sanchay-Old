/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.word.gui;

import java.awt.Component;
import java.io.File;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ToolTipManager;

/**
 *
 * @author anil
 */
public class WordListCellRenderer extends JLabel implements ListCellRenderer {
        Hashtable sourceTypes;

    public WordListCellRenderer(Hashtable sourceTypes)
    {
         super();

         this.sourceTypes = sourceTypes;

         setOpaque(true);
    }

    public Component getListCellRendererComponent
    (
         JList list,
         Object value,
         int index,
         boolean isSelected,
         boolean cellHasFocus
    )
    {
        ToolTipManager.sharedInstance().registerComponent(list);

        this.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        this.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());

        File srcFile = (File) value;

        this.setText(srcFile.getAbsolutePath());

        if(isSelected)
        {
            //get the value of tip text from a map
            String srcType = (String) sourceTypes.get(srcFile);

            String tipText = (srcType != null) ? srcType : srcFile.getName();
            list.setToolTipText(tipText);
        }

         return this;
     }
}
