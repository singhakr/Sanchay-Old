/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.table.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer
{

    protected static Border noFocusBorder;
    protected String langEnc = "eng::utf8";
    protected Color unselectedForeground;
    protected Color unselectedBackground;
    protected final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
    /** map from table to map of rows to map of column heights */
    protected final Map cellSizes = new HashMap();

    int rows = 3;
    int columns = 100;

    public MultiLineCellRenderer(String langEnc, int rows, int columns)
    {
        super();

        this.rows = rows;
        this.columns = columns;
        setRows(rows);
        setColumns(columns);

        noFocusBorder = BorderFactory.createMatteBorder(1, 2, 1, 2, new Color(150, 150, 255));

        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
//        setOpaque(false);
        setBorder(noFocusBorder);

        this.langEnc = langEnc;

        UtilityFunctions.setComponentFont(this, langEnc);
    }

    public void setForeground(Color c)
    {
        super.setForeground(c);
        unselectedForeground = c;
    }

    public void setBackground(Color c)
    {
        super.setBackground(c);
        unselectedBackground = c;
    }

    public void updateUI()
    {
        super.updateUI();
        setForeground(null);
        setBackground(null);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column)
    {

        if (isSelected)
        {
            super.setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else
        {
            super.setForeground((unselectedForeground != null) ? unselectedForeground
                    : table.getForeground());
            super.setBackground((unselectedBackground != null) ? unselectedBackground
                    : table.getBackground());
        }

        if (hasFocus)
        {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column))
            {
                super.setForeground(UIManager.getColor("Table.focusCellForeground"));
                super.setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        } else
        {
            setBorder(noFocusBorder);
        }

//        int rowHeight = (int) getPreferredSize().getHeight();
//        if (table.getRowHeight() != rowHeight)
//        {
//            table.setRowHeight(rowHeight);
//        }

//        adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

//        setForeground(adaptee.getForeground());
//        setBackground(adaptee.getBackground());
//        setBorder(adaptee.getBorder());
//        setFont(adaptee.getFont());
//        setText(adaptee.getText());

        // This line was very important to get it working with JDK1.4
//        TableColumnModel columnModel = table.getColumnModel();
//
//        setSize(columnModel.getColumn(column).getWidth(), 100000);
//
//        int height_wanted = (int) getPreferredSize().getHeight();
//
//        addSize(table, row, column, height_wanted);
//
//        height_wanted = findTotalMaximumRowSize(table, row);
//
//        if (height_wanted > 1 && height_wanted != table.getRowHeight(row))
//        {
//            table.setRowHeight(row, height_wanted);
//        }

        if (table instanceof SanchayJTable)
        {
            Object cellObject = ((SanchayJTable) table).getCellObject(row, column);

            Component c = setValue(value, cellObject);

            return c;
        } else
        {
            setText((value == null) ? "" : value.toString());
        }

        return this;
    }

    private void addSize(JTable table, int row, int column, int height)
    {
        Map rows = (Map) cellSizes.get(table);

        if (rows == null)
        {
            cellSizes.put(table, rows = new HashMap());
        }

        Map rowheights = (Map) rows.get(new Integer(row));

        if (rowheights == null)
        {
            rows.put(new Integer(row), rowheights = new HashMap());
        }

        rowheights.put(new Integer(column), new Integer(height));
    }

    /**
     * Look through all columns and get the renderer.  If it is
     * also a TextAreaRenderer, we look at the maximum height in
     * its hash table for this row.
     */
    private int findTotalMaximumRowSize(JTable table, int row)
    {
        int maximum_height = 0;
        Enumeration columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements())
        {
            TableColumn tc = (TableColumn) columns.nextElement();
            TableCellRenderer cellRenderer = tc.getCellRenderer();
            if (cellRenderer instanceof MultiLineCellRenderer)
            {
                MultiLineCellRenderer tar = (MultiLineCellRenderer) cellRenderer;
                maximum_height = Math.max(maximum_height,
                        tar.findMaximumRowSize(table, row));
            }
        }
        return maximum_height;
    }

    private int findMaximumRowSize(JTable table, int row)
    {
        Map rows = (Map) cellSizes.get(table);
        if (rows == null)
        {
            return 0;
        }
        Map rowheights = (Map) rows.get(new Integer(row));
        if (rowheights == null)
        {
            return 0;
        }
        int maximum_height = 0;
        for (Iterator it = rowheights.entrySet().iterator();
                it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            int cellHeight = ((Integer) entry.getValue()).intValue();
            maximum_height = Math.max(maximum_height, cellHeight);
        }
        return maximum_height;
    }

    protected Component setValue(Object value, Object cellObject)
    {

        if (cellObject == null)
        {
            setText((value == null) ? "" : value.toString());
            return this;
        } else
        {
            if (cellObject instanceof SSFNode)
            {
                SSFNode node = (SSFNode) cellObject;

                String attribs[] = FSProperties.getSemanticAttributes();

                String values[] = node.getFeatureStructures().getOneOfAttributeValues(attribs);

                if (values != null && values.length == 2)
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
                } else
                {
                    setText((value == null) ? "" : value.toString());
                    return this;
                }
            }
            if (cellObject instanceof AlignmentUnit)
            {
                AlignmentUnit alignmentUnit = (AlignmentUnit) cellObject;

                Object alignmentObject = alignmentUnit.getAlignmentObject();

                if (alignmentObject instanceof SSFSentence)
                {
                    SSFSentence sentence = (SSFSentence) alignmentObject;

                    setText(sentence.getRoot().makeRawSentence());
                } else if (alignmentObject instanceof SSFNode)
                {
                    SSFNode node = (SSFNode) alignmentObject;
                    setText(node.makeRawSentence());
                } else
                {
                    setText((value == null) ? "" : value.toString());
                }

                return this;
            } else
            {
                setText((value == null) ? "" : value.toString());
                return this;
            }
        }
    }
}
