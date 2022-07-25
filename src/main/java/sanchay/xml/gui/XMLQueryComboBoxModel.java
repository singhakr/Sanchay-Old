/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.xml.gui;

import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author user
 */
public class XMLQueryComboBoxModel extends AbstractListModel implements ComboBoxModel{

    private Object selectedItem;
    private List anArrayList;
    
    public XMLQueryComboBoxModel(List arrayList){
        anArrayList=arrayList;
    }
    
    @Override
    public int getSize() {
        return anArrayList.size();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getElementAt(int index) {
        return anArrayList.get(index);
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selectedItem=anItem;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
