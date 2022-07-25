/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.parallel.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.util.LinkedHashMap;
import javax.swing.tree.DefaultMutableTreeNode;
import sanchay.GlobalProperties;
import sanchay.corpus.parallel.Alignable;
import sanchay.corpus.parallel.AlignmentBlock;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.gui.common.SanchayStringDataTransfer;
import sanchay.table.gui.SanchayJTable;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.tree.SanchayTreeModel;
import sanchay.tree.gui.TreeViewerEvent;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class SanchayAlignableDataTransfer implements ClipboardOwner {

    /**
    * Empty implementation of the ClipboardOwner interface.
    */
    public void lostOwnership( Clipboard aClipboard, Transferable aContents) {
        //do nothing
    }

    /**
    * Place a String on the clipboard, and make this class the
    * owner of the Clipboard's contents.
    */
    public void setClipboardContents( Alignable alignable ){
        AlignableSelection alignmentUnitSelection = new AlignableSelection( alignable );
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( alignmentUnitSelection, this );
    }

    /**
    * Get the String residing on the clipboard.
    *
    * @return any text found on the Clipboard; if none found, return an
    * empty String.
    */
    public Alignable getClipboardContents() {
        Alignable result = null;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) &&
                                      contents.isDataFlavorSupported(AlignableSelection.alignableFlavor);
        if ( hasTransferableText ) {
              try {
                result = (Alignable)contents.getTransferData(AlignableSelection.alignableFlavor);
              }
              catch (UnsupportedFlavorException ex){
                //highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
              }
              catch (IOException ex) {
                System.out.println(ex);
              }
        }

        return result;
    }

    public static void startTransfer(SanchayJTable targetTable, Object dragObject, int mode,
            int action, SanchayJTable table, int r, int c)
    {
        if(mode == SanchayJTable.TREE_MODE)
        {
            SanchayMutableTreeNode node = (SanchayMutableTreeNode) dragObject;
            SanchayMutableTreeNode oldParent = (SanchayMutableTreeNode) node.getParent();

            if(node == null)
                return;

            if(action == DnDConstants.ACTION_MOVE)
            {
//                ((SanchayTreeModel) table.getObjectModel()).removeNodeFromParent((DefaultMutableTreeNode) dragObject);
                oldParent.remove(node);
                ((SanchayTreeModel) table.getObjectModel()).reload(oldParent);

                if(oldParent.getChildCount()==0)
                {
                    ((SanchayTreeModel) table.getObjectModel()).removeNodeFromParent((DefaultMutableTreeNode) oldParent);
                }

                table.fireTreeViewerEvent(new TreeViewerEvent(table, TreeViewerEvent.TREE_CHANGED_EVENT));
            }
        }
    }

    public static void transferData(SanchayJTable targetTable, Object dragObject, Object dropObject, int mode,
            int action, SanchayJTable table, int r, int c)
    {
        if(mode == SanchayJTable.TREE_MODE)
        {
            SanchayMutableTreeNode node = (SanchayMutableTreeNode) dragObject;
            SanchayMutableTreeNode oldParent = (SanchayMutableTreeNode) node.getParent();
            SanchayMutableTreeNode parent = (SanchayMutableTreeNode) dropObject;

            int a= node.getChildCount();
            int flag=0;

            if(node == null || parent == null)
                return;

            if(
                (
                    ((node.isLeaf() && parent.isLeaf()) || (!node.isLeaf() && parent.isLeaf()))
                        && targetTable.allowsLeafDependencies() == false
                )
                    || (node.equals(dropObject)))
            {
//                        dtde.rejectDrop();
                return;
//                        throw new Exception();
             //   dtde.rejectDrop();
              //  break;
            }

            String depAttribs[] = null;

            if(action == DnDConstants.ACTION_MOVE)
            {                
//                ((SanchayTreeModel) table.getObjectModel()).removeNodeFromParent((DefaultMutableTreeNode) dragObject);
//                oldParent.remove(node);
//                ((SanchayTreeModel) table.getObjectModel()).reload(oldParent);
//
//                if(parent.getChildCount()==0)
//                {
//                    ((SanchayTreeModel) table.getObjectModel()).removeNodeFromParent((DefaultMutableTreeNode) parent);
//                }
//
//                table.fireTreeViewerEvent(new TreeViewerEvent(table, TreeViewerEvent.TREE_CHANGED_EVENT));

//                DefaultTreeModel model = (DefaultTreeModel) table.getObjectModel();
//                model.insertNodeInto((MutableTreeNode) node, (MutableTreeNode) dropObject, 0);

                if(!(parent instanceof SSFLexItem))
                {
                    parent.insert(node, 0);
                    ((SanchayTreeModel) table.getObjectModel()).reload(parent);
                }

                depAttribs = FSProperties.getDependencyTreeAttributes();

                if(
                        (node instanceof SSFPhrase && dropObject instanceof SSFPhrase && targetTable.allowsLeafDependencies() == false)
                        || (targetTable.allowsLeafDependencies() == true)
                )
                {
                    if(dropObject instanceof SSFLexItem)
                    {
                        LinkedHashMap c2dTreeMap = table.getCFG2DepTreeMap();

                        LinkedHashMap d2cTreeMap = (LinkedHashMap) UtilityFunctions.getReverseMap(c2dTreeMap);
                        
                        SSFNode cfgNode = (SSFNode) d2cTreeMap.get(dropObject);

                        SSFNode dropParent = (SSFNode) ((SSFNode) dropObject).getParent();

                        SSFPhrase dropPhrase = new SSFPhrase(((SSFNode) dropObject).getId(),
                                ((SSFNode) dropObject).getLexData(), ((SSFNode) dropObject).getName(), ((SSFNode) dropObject).getFeatureStructures());

                        int dropObjectIndex = dropParent.getIndex((SSFNode) dropObject);

                        dropParent.remove((SSFNode) dropObject);

                        dropParent.insert(dropPhrase, dropObjectIndex);

                        c2dTreeMap.put(cfgNode, dropPhrase);

                        dropObject = dropPhrase;
                        parent = dropPhrase;

                        parent.insert(node, 0);
                        ((SanchayTreeModel) table.getObjectModel()).reload(parent);
                    }

                    String referredName = ((SSFNode) dropObject).getAttributeValue(GlobalProperties.getIntlString("name"));

                    if(referredName != null && referredName.equals("") == false)
                    {
                        String prevDrel[] = ((SSFNode) node).getOneOfAttributeValues(depAttribs);

                        if(prevDrel == null || prevDrel.length != 2)
                            ((SSFNode) node).setAttributeValue("drel", "UNDEF:" + referredName);
                        else
                        {
                            String drelType= prevDrel[0];
                            String drel = prevDrel[1];

                            String parts[] = drel.split(":");

                            ((SSFNode) node).setAttributeValue(drelType, parts[0] + ":" + referredName);
                        }
//                        ((SSFPhrase) node).setReferredName(referredName, SSFPhrase.DEPENDENCY_STRUCTURE_MODE);
                    }
                }
            }
            else if(action == DnDConstants.ACTION_COPY)
            {
//                depAttribs = FSProperties.getDependencyGraphAttributes();
//
//                setLabel(dtde);
            }
        }
        else if(mode == SanchayJTable.ALIGNMENT_MODE
                && ((dragObject instanceof Alignable && dropObject instanceof Alignable)
                    || (dragObject instanceof AlignmentUnit && dropObject instanceof AlignmentUnit)))
        {
            AlignmentBlock alignmentBlock = targetTable.getAlignmentBlock();

            AlignmentUnit alignmentUnitDrag = null;
            AlignmentUnit alignmentUnitDrop = null;

            if(dragObject instanceof Alignable && dropObject instanceof Alignable)
            {
                alignmentUnitDrag = ((Alignable) dragObject).getAlignmentUnit();
                alignmentUnitDrop = ((Alignable) dropObject).getAlignmentUnit();
            }
             else if(dragObject instanceof AlignmentUnit && dropObject instanceof AlignmentUnit)
            {
                alignmentUnitDrag = (AlignmentUnit) dragObject;
                alignmentUnitDrop = (AlignmentUnit) dropObject;
            }

            int amode = alignmentBlock.getMode();
            
            if(action == DnDConstants.ACTION_MOVE)
            {

                if(amode == AlignmentBlock.SENTENCE_ALIGNMENT_MODE)
                {
                    alignmentUnitDrag = (AlignmentUnit) table.getCellObject(alignmentUnitDrag.getIndex(), alignmentUnitDrag.getParallelIndex());

                    boolean validAlignment = true;

                    if(c == 2)
                        validAlignment = alignmentBlock.isValidAlignment(alignmentUnitDrag, alignmentUnitDrop);
                    else if(c == 0)
                        validAlignment = alignmentBlock.isValidAlignment(alignmentUnitDrop, alignmentUnitDrag);

                    if(validAlignment && alignmentBlock.areAlreadyAligned(alignmentUnitDrag, alignmentUnitDrop) == false)
                    {
                        alignmentUnitDrop.addAlignedUnit(alignmentUnitDrag);
                        alignmentUnitDrag.addAlignedUnit(alignmentUnitDrop);

                        if(c == 2)
                        {
                            alignmentBlock.addEdge(alignmentUnitDrag, alignmentUnitDrop);
    //                                    SanchayEdge e = new SanchayEdge(alignmentUnitDrag, alignmentUnitDrag.getIndex(), 0, alignmentUnitDrop, alignmentUnitDrop.getIndex(), 2);
    //                                    edges.addEdge(e);
                        }
                        else if(c == 0)
                        {
                            alignmentBlock.addEdge(alignmentUnitDrop, alignmentUnitDrag);
    //                                    SanchayEdge e = new SanchayEdge(alignmentUnitDrop, alignmentUnitDrop.getIndex(), 0, alignmentUnitDrag, alignmentUnitDrag.getIndex(), 2);
    //                                    edges.addEdge(e);
                        }

                        table.changeSelection(r, c, false, false);
                    }
                }
                else if(amode == AlignmentBlock.PHRASE_ALIGNMENT_MODE)
                {
                    alignmentUnitDrag = (AlignmentUnit) table.getCellObject(alignmentUnitDrag.getParallelIndex(), alignmentUnitDrag.getIndex());

                    if( alignmentBlock.areAlreadyAligned(alignmentUnitDrag, alignmentUnitDrop) == false)
                    {
                        alignmentUnitDrop.addAlignedUnit(alignmentUnitDrag);
                        alignmentUnitDrag.addAlignedUnit(alignmentUnitDrop);

                        if(r == 2)
                        {
                            alignmentBlock.addEdge(alignmentUnitDrag, alignmentUnitDrop);
        //                                    SanchayEdge e = new SanchayEdge(alignmentUnitDrag, 0, alignmentUnitDrag.getIndex(), alignmentUnitDrop, 2, alignmentUnitDrop.getIndex());
        //                                    edges.addEdge(e);
                        }
                        else if(r == 0)
                        {
                            alignmentBlock.addEdge(alignmentUnitDrop, alignmentUnitDrag);
        //                                    SanchayEdge e = new SanchayEdge(alignmentUnitDrop, 0, alignmentUnitDrop.getIndex(), alignmentUnitDrag, 2, alignmentUnitDrag.getIndex());
        //                                    edges.addEdge(e);
                        }
                    }
                }
            }
            else if(action == DnDConstants.ACTION_COPY)
            {
                if(amode == AlignmentBlock.SENTENCE_ALIGNMENT_MODE)
                {
                    alignmentUnitDrag = (AlignmentUnit) table.getCellObject(alignmentUnitDrag.getIndex(), alignmentUnitDrag.getParallelIndex());

                    alignmentUnitDrop.removeAlignedUnit(alignmentUnitDrag.getAlignmentKey());
                    alignmentUnitDrag.removeAlignedUnit(alignmentUnitDrop.getAlignmentKey());
    //                                alignmentUnitDrag.saveAlignments();
    //                                alignmentUnitDrop.saveAlignments();

                    if(c == 2)
                    {
                        alignmentBlock.removeEdge(alignmentUnitDrag, alignmentUnitDrop);
    //                                    edges.removeEdge(alignmentUnitDrag.getIndex(), 0, alignmentUnitDrop.getIndex(), 2);
                    }
                    else if(c == 0)
                    {
                        alignmentBlock.removeEdge(alignmentUnitDrop, alignmentUnitDrag);
    //                                    edges.removeEdge(alignmentUnitDrop.getIndex(), 0, alignmentUnitDrag.getIndex(), 2);
                    }

                    table.changeSelection(r, c, false, false);
                }
                else if(amode == AlignmentBlock.PHRASE_ALIGNMENT_MODE)
                {
                    alignmentUnitDrag = (AlignmentUnit) table.getCellObject(alignmentUnitDrag.getParallelIndex(), alignmentUnitDrag.getIndex());

                    alignmentUnitDrop.removeAlignedUnit(alignmentUnitDrag.getAlignmentKey());
                    alignmentUnitDrag.removeAlignedUnit(alignmentUnitDrop.getAlignmentKey());
    //                                alignmentUnitDrag.saveAlignments();
    //                                alignmentUnitDrop.saveAlignments();

                    if(r == 2)
                    {
                        alignmentBlock.removeEdge(alignmentUnitDrag, alignmentUnitDrop);
    //                                    edges.removeEdge(0, alignmentUnitDrag.getIndex(), 2, alignmentUnitDrop.getIndex());
                    }
                    else if(r == 0)
                    {
                        alignmentBlock.removeEdge(alignmentUnitDrop, alignmentUnitDrag);
    //                                    edges.removeEdge(0, alignmentUnitDrop.getIndex(), 2, alignmentUnitDrag.getIndex());
                    }
                }
            }
        }
    }

    public static void main (String[]  aArguments ){
        SanchayStringDataTransfer tableTransfer = new SanchayStringDataTransfer();

        //display what is currently on the clipboard
        System.out.println(GlobalProperties.getIntlString("Clipboard_contains:") + tableTransfer.getClipboardContents() );

        //change the contents and then re-display
        tableTransfer.setClipboardContents(GlobalProperties.getIntlString("blah,_blah,_blah"));
        System.out.println(GlobalProperties.getIntlString("Clipboard_contains:") + tableTransfer.getClipboardContents() );
    }
}
