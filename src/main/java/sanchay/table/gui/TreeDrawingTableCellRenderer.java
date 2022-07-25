/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.table.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author anil
 */
public class TreeDrawingTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer, Serializable {

    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    	return this;
    }

    protected Component setValue(Object value, Object cellObject) {

        if(cellObject == null)
        {
           setText((value == null) ? "" : value.toString());
           return this;
        }
        else
        {
            if(cellObject instanceof SSFNode)
            {
                SSFNode node = (SSFNode) cellObject;

                String attribs[] = FSProperties.getSemanticAttributes();

                String values[] = node.getFeatureStructures().getOneOfAttributeValues(attribs);

                if(values != null && values.length == 2)
                {
                    JPanel panel = new JPanel();
                    
                    LayoutManager layout = new GridLayout(2, 1, 0, 2);

                    panel.setLayout(layout);

                    JLabel firstLabel = new JLabel();
                    JLabel secondLabel = new JLabel();

                    firstLabel.setText((value == null) ? "" : value.toString());
                    secondLabel.setText(values[1]);

                    panel.add(firstLabel);
                    panel.add(secondLabel);

                    return panel;
                }
                else
                {
                    setText((value == null) ? "" : value.toString());
                   return this;
                }
            }
            else
            {
                setText((value == null) ? "" : value.toString());
               return this;
            }
        }
    }
}
