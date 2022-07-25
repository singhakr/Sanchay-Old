/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.xml.gui;

/**
 *
 * @author user
 */
public class XMLQueryListItem extends Object {

    private String itemName;
    private String parent;  //this will be null if ListItem is an attribute
    private String owner;   //this will be non-null only if ListItem is an attribute
    private int itemType;
    
    XMLQueryListItem(String itemName,int itemType,String parent,String owner) {
        this.itemName=itemName;
        this.itemType=itemType;
        this.parent=parent;
        this.owner=owner;
    }
    
    public int getItemType(){
        return this.itemType;
    }
    
    public String getItemName() {
        return this.itemName;
    }
    
    public String getParentOfItem(){
        return this.parent;
    }
    
    public String getOwnerOfItem(){
        return this.owner;
    }
}
