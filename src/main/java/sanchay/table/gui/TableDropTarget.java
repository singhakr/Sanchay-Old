package sanchay.table.gui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import sanchay.GlobalProperties;
import sanchay.corpus.parallel.AlignmentBlock;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.parallel.gui.SanchayAlignableDataTransfer;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.gui.common.SanchayDataFlavors;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.tree.gui.TreeViewerEvent;

/*package sanchay.tree.gui;
 
 
import java.awt.dnd.DropTargetListener;*/

public class TableDropTarget implements DropTargetListener {
    
    protected DropTarget target;
    
    protected SanchayTransferableObject transferable;
    
    protected SanchayJTable targetTable;

    protected int mode;

    protected boolean dropEnded = true;
    
    public TableDropTarget(SanchayJTable table, int mode) {
        targetTable = table;
        target = new DropTarget(targetTable, this);

        this.mode = mode;
    }

    public boolean dropEnded()
    {
        return dropEnded;
    }

    public void dropEnded(boolean de)
    {
        dropEnded = de;
    }

    public DropTarget getDropTarget()
    {
        return target;
    }
    
  /*
   * Drop Event Handlers
   */
    private Object getDropObjectForEvent(DropTargetDragEvent dtde) {
        
        Point p = dtde.getLocation();
        int r = targetTable.rowAtPoint(p);
        int c = targetTable.columnAtPoint(p);
              
        DropTargetContext dtc = dtde.getDropTargetContext();
        SanchayJTable table = (SanchayJTable) dtc.getComponent();
        
        return table.getCellObject(r, c);
//
//    Point p = dtde.getLocation();
//    DropTargetContext dtc = dtde.getDropTargetContext();
//    SanchayJTable table = (SanchayJTable) dtc.getComponent();
//    TreePath path = table.getComponentAt(p);
//    return (TreeNode) path.getLastPathComponent();
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
      dropEnded = true;

        Object dropObject = getDropObjectForEvent(dtde);
        if (dropObject == null) {
//        if (node.isLeaf()) {
            dtde.rejectDrag();
        } else {
            // start by supporting move operations
            //dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            dtde.acceptDrag(dtde.getDropAction());
        }
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
        Object dropObject = getDropObjectForEvent(dtde);
       // targetTable.fireTreeViewerEvent(new TreeViewerEvent(targetTable, TreeViewerEvent.TREE_CHANGED_EVENT));
        if (dropObject == null) {
//        if (node.isLeaf()) {
            dtde.rejectDrag();
        }
        else {
            // start by supporting move operations
            //dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            dtde.acceptDrag(dtde.getDropAction());
        }
    }
    
    public void dragExit(DropTargetEvent dte) {
       // targetTable.fireTreeViewerEvent(new TreeViewerEvent(targetTable, TreeViewerEvent.TREE_CHANGED_EVENT));
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
  public void dragDropEnd(DropTargetDragEvent dtde){ 
      dropEnded = true;
  }
    
    public void drop(DropTargetDropEvent dtde) {
        
        Point p = dtde.getLocation();
        int r = targetTable.rowAtPoint(p);
        int c = targetTable.columnAtPoint(p);
        
        DropTargetContext dtc = dtde.getDropTargetContext();

        SanchayJTable table = (SanchayJTable) dtc.getComponent();
        Object dropObject = table.getCellObject(r, c);

        if(dropObject == null || dropObject.toString().equals(""))
            return;
//    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath
//        .getLastPathComponent();
///////
//        if (parent.isLeaf()) {
  //          dtde.rejectDrop();
    //        return;
      //  }
/////////
        
        try {
            Transferable tr = dtde.getTransferable();

            if(tr == null)
                return;

            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (tr.isDataFlavorSupported(flavors[i])) {
                    dtde.acceptDrop(dtde.getDropAction());
                    
                    Object dragObject = tr.getTransferData(flavors[i]);

                    if(dragObject == null)
                        return;
                        
                    if(mode == SanchayJTable.TREE_MODE)
                    {
                        TreeNode node = (TreeNode) dragObject;
                        TreeNode parent = (TreeNode) dropObject;

                        int a= node.getChildCount();
                        int flag=0;
                        if(
                                (
                                    ((node.isLeaf() && parent.isLeaf()) || (!node.isLeaf() && parent.isLeaf()))
                                    && targetTable.allowsLeafDependencies() == false
                                )
                                || (node.equals(dropObject))
                        )
                        {
    //                        dtde.rejectDrop();
                            return;
    //                        throw new Exception();
                         //   dtde.rejectDrop();
                          //  break;
                        }

                        String depAttribs[] = null;

                        if(dtde.getDropAction() == DnDConstants.ACTION_MOVE)
                        {
                            SanchayAlignableDataTransfer.transferData(targetTable, dragObject, dropObject, mode, dtde.getDropAction(), table, r, c);

//                            DefaultTreeModel model = (DefaultTreeModel) table.getObjectModel();
//                            model.insertNodeInto((MutableTreeNode) node, (MutableTreeNode) dropObject, 0);
//
//                            depAttribs = FSProperties.getDependencyTreeAttributes();
//
//                            if(node instanceof SSFPhrase && dropObject instanceof SSFPhrase)
//                            {
//                                String referredName = ((SSFPhrase) dropObject).getAttributeValue(GlobalProperties.getIntlString("name"));
//
//                                if(referredName != null && referredName.equals("") == false)
//                                    ((SSFPhrase) node).setReferredName(referredName, SSFPhrase.DEPENDENCY_STRUCTURE_MODE);
//                            }
                        }
                        else if(dtde.getDropAction() == DnDConstants.ACTION_COPY)
                        {
                            depAttribs = FSProperties.getDependencyGraphAttributes();

                            setLabel(dtde);
                        }

    //                    String refAtVal[] = ((SSFPhrase) node).getOneOfAttributeValues(depAttribs);
    //
    //                    if(refAtVal != null && refAtVal[0] != null && refAtVal[0].equals("") == false)
    //                    {
    //                        ((SSFPhrase) node).setAttributeValue(refAtVal[0], "");
    //                    }

    //                  setLabel(dtde);

                        dtde.dropComplete(true);
                        return;
                    }
                    else if(mode == SanchayJTable.ALIGNMENT_MODE && dragObject instanceof AlignmentUnit
                            && dropObject instanceof AlignmentUnit)
                    {
                        // call the data transfer method
                        SanchayAlignableDataTransfer.transferData(targetTable, dragObject, dropObject, mode, dtde.getDropAction(), table, r, c);

                        dtde.dropComplete(true);
                        return;
                    }
                }
            }
            
            dtde.dropComplete(true);
//            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }
    }

  private void setLabel(DropTargetDropEvent dtde)
  {
      Point p = dtde.getLocation();
      int r = targetTable.rowAtPoint(p);
      int c = targetTable.columnAtPoint(p);

      SSFNode treeRoot = (SSFNode) targetTable.getTreeRoot();

      SSFNode tempRoot = (SSFNode) targetTable.getTreeRoot().getChildAt(0);
      SSFNode  treeNode = (SSFNode) targetTable.getCellObject(r, c);
      
      SSFProperties ssfp = SSFNode.getSSFProperties();

      String rootName = ssfp.getProperties().getPropertyValueForPrint("rootName");
      
      if(treeRoot.getName().equals(rootName) == false)
          return;

      if(treeNode != tempRoot && (treeNode instanceof SSFPhrase || targetTable.allowsLeafDependencies()))
      {
          String depAttribs[] = null;

            if(dtde.getDropAction() == DnDConstants.ACTION_COPY)
                depAttribs = FSProperties.getDependencyGraphAttributes();
//            else if(dtde.getDropAction() == DnDConstants.ACTION_MOVE)
//                depAttribs = FSProperties.getDependencyTreeAttributes();

          String atrribName = (String) JOptionPane.showInputDialog(targetTable,
                GlobalProperties.getIntlString("Select_the_relation"), GlobalProperties.getIntlString("Relation"), JOptionPane.INFORMATION_MESSAGE, null, depAttribs, "drel");

         String atrribVal = JOptionPane.showInputDialog(GlobalProperties.getIntlString("Please_enter_the_relation_value"), "");

         if(atrribVal != null)
         {
//              int name = 1;
//
//              if(((SSFPhrase) treeNode.getParent()).getAttributeValue("name")==null || (((SSFPhrase) treeNode.getParent()).getAttributeValue("name").equals("")))
//              {
//                  Vector nodes = ((SSFPhrase) tempRoot).getNodesForAttribVal("name", "" + name);
//
//
//                  while(nodes != null && nodes.size()>0)
//                  {
//                        name++;
//                        nodes = ((SSFPhrase) tempRoot).getNodesForAttribVal("name","" + name);
//                  }
//
//                    ((SSFPhrase) treeNode.getParent()).setAttributeValue("name", "" + name);
////                    treeNode.setAttributeValue("drel", atrribVal+":"+name);
//                    treeNode.setAttributeValue(atrribName, atrribVal+":"+name);
//                    targetTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
//              }
//              else
              {
//                  treeNode.setAttributeValue("drel", atrribVal+":"+((SSFPhrase) treeNode.getParent()).getAttributeValue("name"));

                  if(dtde.getDropAction() == DnDConstants.ACTION_COPY)
                  {
                        SSFNode sourceNode = null;
                        try {
                            if(treeNode instanceof SSFPhrase)
                                sourceNode = (SSFNode) dtde.getTransferable().getTransferData(SanchayDataFlavors.SSF_PHRASE_FLAVOR);
                            else if(treeNode instanceof SSFLexItem)
                                sourceNode = (SSFNode) dtde.getTransferable().getTransferData(SanchayDataFlavors.SSF_LEXITEM_FLAVOR);

                            sourceNode = ((SSFPhrase) treeNode.getRoot()).getNodeForAttribVal(GlobalProperties.getIntlString("name"), sourceNode.getAttributeValue(GlobalProperties.getIntlString("name")), true);
                        } catch (UnsupportedFlavorException ex) {
                            Logger.getLogger(TableDropTarget.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(TableDropTarget.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if(atrribVal.equals(""))
                            sourceNode.setAttributeValue(atrribName, treeNode.getAttributeValue(GlobalProperties.getIntlString("name")));
                        else
                            sourceNode.setAttributeValue(atrribName, atrribVal+":" + treeNode.getAttributeValue(GlobalProperties.getIntlString("name")));
                  }
//                  else if(dtde.getDropAction() == DnDConstants.ACTION_MOVE)
//                  {
//                        treeNode.setAttributeValue(atrribName, atrribVal+":"+((SSFPhrase) treeNode.getParent()).getAttributeValue("name"));
//                  }

                  targetTable.fireTreeViewerEvent(new TreeViewerEvent(this, TreeViewerEvent.TREE_CHANGED_EVENT));
              }
          }
      }
  }
    
 /*   public void dragEnter(DropTargetDragEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    public void dragOver(DropTargetDragEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    public void dropActionChanged(DropTargetDragEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    public void dragExit(DropTargetEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
  
    public void drop(DropTargetDropEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/
}
