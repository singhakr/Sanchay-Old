/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.tmp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author anil
 */
public class JTreeExample extends JPanel {
    private JTree tree;
    private DefaultTreeModel treeModel;


    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {

            @Override
            public void run()
            {
                createAndShowGUI();             
            }
        });
    }

    private static void createAndShowGUI()
    {
        JFrame frame = new JFrame("My Warehouse");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTreeExample newContentPane = new JTreeExample();
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
    }

    public JTreeExample()
    {
        setLayout(new GridLayout(1, 3));
        JLabel lbl_parts = new JLabel("PARTS TO BE SHIPPED");       
        tree = new JTree(getTreeModel());
        tree.setDragEnabled(true);        
        tree.setPreferredSize(new Dimension(200,400));
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(tree);

        JLabel lbl_ship = new JLabel("SHIPPING BOX");
        treeModel = getTreeModel();
        JTree secondTree = new JTree(treeModel);
        secondTree.setPreferredSize(new Dimension(200,400));        
        secondTree.setTransferHandler(new TransferHandler() {

            @Override
            public boolean importData(TransferSupport support)
            {
                if (!canImport(support))
                {
                    return false;
                }

                JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();

                TreePath path = dl.getPath();
                int childIndex = dl.getChildIndex();

                String data;
                try
                {
                    data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                }
                catch (UnsupportedFlavorException e)
                {
                    return false;                   
                }
                catch (IOException e)
                {
                    return false;                   
                }

                if (childIndex == -1)
                {
                    childIndex = tree.getModel().getChildCount(path.getLastPathComponent());
                }

                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(data);
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                treeModel.insertNodeInto(newNode, parentNode, childIndex);

                tree.makeVisible(path.pathByAddingChild(newNode));
                tree.scrollRectToVisible(tree.getPathBounds(path.pathByAddingChild(newNode)));

                return true;
            }

            public boolean canImport(TransferSupport support)
            {
                if (!support.isDrop())
                {
                    return false;                   
                }

                support.setShowDropLocation(true);
                if (!support.isDataFlavorSupported(DataFlavor.stringFlavor))
                {
                    System.err.println("only string is supported");
                    return false;                   
                }

                JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();

                TreePath path = dl.getPath();

                if (path == null)
                {
                    return false;                   
                }
                return true;
            }                       
        });
        JScrollPane secondScroll = new JScrollPane();
        secondScroll.setViewportView(secondTree);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(lbl_parts, BorderLayout.NORTH);
        topPanel.add(scroll, BorderLayout.CENTER);

        JPanel btmPanel = new JPanel(new BorderLayout());
        btmPanel.add(lbl_ship, BorderLayout.NORTH);
        btmPanel.add(secondScroll, BorderLayout.CENTER);

        add(topPanel);
        add(btmPanel);        

    }

    private static DefaultTreeModel getTreeModel()
    {
        MutableTreeNode root =  new DefaultMutableTreeNode("15663-1");                        

        DefaultMutableTreeNode cover = new DefaultMutableTreeNode("Cover");
        cover.insert(new DefaultMutableTreeNode("2x PEMS"), 0);
        cover.insert(new DefaultMutableTreeNode("2x SCREWS"), 0);
        root.insert(cover, 0);

        DefaultMutableTreeNode base = new DefaultMutableTreeNode("Base");
        base.insert(new DefaultMutableTreeNode("4x SCREWS"), 0);
        base.insert(new DefaultMutableTreeNode("4x HANDLES"), 0);
        root.insert(base, 0);

        DefaultTreeModel model = new DefaultTreeModel(root);
        return model;
    }    
}
