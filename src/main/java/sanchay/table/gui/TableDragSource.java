package sanchay.table.gui;


import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeModel;
//import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.parallel.gui.AlignmentEvent;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.tree.SanchayTreeModel;
import sanchay.tree.gui.TreeViewerEvent;

public class TableDragSource implements DragSourceListener, DragGestureListener {

  protected DragSource source;

  protected DragGestureRecognizer recognizer;

  protected SanchayTransferableObject transferable;

  protected Object oldDragObject;

  protected SanchayJTable sourceTable;

  protected int mode;

  protected boolean dragStarted = false;

  public TableDragSource(SanchayJTable table, int actions, int mode) {
    sourceTable = table;
    source = new DragSource();
    recognizer = source.createDefaultDragGestureRecognizer(sourceTable,actions,this);

        this.mode = mode;
  }

    public boolean dragStarted()
    {
        return dragStarted;
    }

    public void dragStarted(boolean ds)
    {
        dragStarted = ds;
    }

  public DragGestureRecognizer getRecognizer()
    {
      return recognizer;
  }

  /*
   * Drag Gesture Handler
   */
  public void dragGestureRecognized(DragGestureEvent dge) {

      dragStarted = true;
      
      Point p = dge.getDragOrigin();
      int r = sourceTable.rowAtPoint(p);
      int c = sourceTable.columnAtPoint(p);

      Object dragObject = sourceTable.getCellObject(r, c);

      if(dragObject instanceof SSFNode || dragObject instanceof AlignmentUnit)
      {
        if (dragObject == null)
        {
          // We can't move the root node or an empty selection
            return;
        }
        
        oldDragObject = dragObject;
        transferable = new SanchayTransferableObject(dragObject);

        if(dge.getDragAction() == DnDConstants.ACTION_MOVE)
            source.startDrag(dge, DragSource.DefaultMoveDrop, transferable, this);
        else if(dge.getDragAction() == DnDConstants.ACTION_COPY)
            source.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
      }
  }

  /*
   * Drag Event Handlers
   */
  public void dragEnter(DragSourceDragEvent dsde) {
      int r = sourceTable.getSelectedRow();
      int c = sourceTable.getSelectedColumn();
  }

  public void dragExit(DragSourceEvent dse) {
  }

  public void dragOver(DragSourceDragEvent dsde) {
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
  }

  public void dragDropEnd(DragSourceDropEvent dsde) {    
      // sourceTable.fireTreeViewerEvent(new TreeViewerEvent(sourceTable, TreeViewerEvent.TREE_CHANGED_EVENT));
      /*
     * to support move or copy, we have to check which occurred:
     */
      
     /////////
    if (dsde.getDropSuccess())
    {
        if(mode == SanchayJTable.TREE_MODE)
        {
            if(dsde.getDropAction() == DnDConstants.ACTION_MOVE)
            {
                DefaultMutableTreeNode oldNodeParent = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) oldDragObject).getParent();
                ((SanchayTreeModel) sourceTable.getObjectModel()).removeNodeFromParent((DefaultMutableTreeNode) oldDragObject);

                if(oldNodeParent.getChildCount()==0)
                    ((SanchayTreeModel) sourceTable.getObjectModel()).removeNodeFromParent(oldNodeParent);

                sourceTable.fireTreeViewerEvent(new TreeViewerEvent(sourceTable, TreeViewerEvent.TREE_CHANGED_EVENT));
            }
            else if(dsde.getDropAction() == DnDConstants.ACTION_COPY)
            {
    //            DefaultMutableTreeNode oldNodeParent = (DefaultMutableTreeNode) oldNode.getParent();
    //            ((SanchayTreeModel) sourceTable.getObjectModel()).removeNodeFromParent(oldNode);
    //
    //            if(oldNodeParent.getChildCount()==0)
    //                ((SanchayTreeModel) sourceTable.getObjectModel()).removeNodeFromParent(oldNodeParent);

                sourceTable.fireTreeViewerEvent(new TreeViewerEvent(sourceTable, TreeViewerEvent.TREE_CHANGED_EVENT));
            }
        }
        else if(mode == SanchayJTable.ALIGNMENT_MODE)
        {            
            if(dsde.getDropAction() == DnDConstants.ACTION_MOVE)
            {
                sourceTable.fireTreeViewerEvent(new AlignmentEvent(sourceTable, AlignmentEvent.ALIGNMENT_CHANGED_EVENT));
            }
            else if(dsde.getDropAction() == DnDConstants.ACTION_COPY)
            {
                sourceTable.fireTreeViewerEvent(new AlignmentEvent(sourceTable, AlignmentEvent.ALIGNMENT_CHANGED_EVENT));
            }
        }
    }

    /*
     * to support move only... if (dsde.getDropSuccess()) {
     * ((DefaultTreeModel)sourceTree.getModel()).removeNodeFromParent(oldNode); }
     */
      dragStarted = false;
  }

   /* public void dragEnter(DragSourceDragEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void dragOver(DragSourceDragEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void dropActionChanged(DragSourceDragEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void dragExit(DragSourceEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void dragDropEnd(DragSourceDropEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void dragGestureRecognized(DragGestureEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/
}
