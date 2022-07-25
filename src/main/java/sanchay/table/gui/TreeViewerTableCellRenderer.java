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
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author anil
 */
public class TreeViewerTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer, Serializable {

    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if(table instanceof SanchayJTable && value instanceof AlignmentUnit)
        {
            Object cellObject = ((SanchayJTable) table).getCellObject(row, column);
            Component c = setValue(value, cellObject);

            return c;
        }

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
            if(cellObject instanceof AlignmentUnit)
            {
                AlignmentUnit alignmentUnit = (AlignmentUnit) cellObject;

                Object alignmentObject = alignmentUnit.getAlignmentObject();

                if(alignmentObject instanceof SSFSentence)
                {
                    SSFSentence sentence = (SSFSentence) alignmentObject;

                    setText(sentence.getRoot().makeRawSentence());
                }
                else if(alignmentObject instanceof SSFNode)
                {
                    SSFNode node = (SSFNode) alignmentObject;
                    String txt = node.convertToBracketForm(1);

//                    if(txt.contains("''"))
//                        txt = txt.replaceAll("''", "'\"'");
//
//                    if(txt.contains("''''"))
//                        txt = txt.replaceAll("''''", "'\"'");

                    setText(txt);
                }
                else
                    setText((value == null) ? "" : value.toString());

                return this;
            }
            else
            {
                setText((value == null) ? "" : value.toString());
               return this;
            }
        }
    }
}
