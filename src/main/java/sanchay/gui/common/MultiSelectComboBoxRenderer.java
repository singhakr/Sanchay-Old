/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.gui.common;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author User
 */
public class MultiSelectComboBoxRenderer<E extends BasicComboBoxRenderer> implements ListCellRenderer<E> {

    private String[] items;
    private boolean[] selected;

    public MultiSelectComboBoxRenderer(String[] items){
         this.items = items;
         this.selected = new boolean[items.length];
    }

//    public Component getListCellRendererComponent(JTable table, Object value,
//            boolean isSelected, boolean hasFocus, int index) {
//         // Create here a JLabel with the text
//         // Create here a JCheckBox
//         // Add them to a layoutmanager
//         return this;
//    }

    public void setSelected(int i, boolean flag)
    {
         this.selected[i] = flag;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
//         // Create here a JLabel with the text
//         // Create here a JCheckBox
//         // Add them to a layoutmanager
         return new JList();
    }
    
}
