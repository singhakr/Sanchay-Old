/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.parallel.gui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import sanchay.corpus.parallel.Alignable;
import sanchay.corpus.parallel.AlignmentUnit;

/**
 *
 * @author anil
 */
public class AlignableSelection implements Transferable, ClipboardOwner {

    private static final int ALIGNABLE = 0;

    public static final DataFlavor alignableFlavor = new DataFlavor(AlignmentUnit.class,  "Alignable");

    public static final DataFlavor[] flavors = {
        alignableFlavor
    };

    private Alignable data;

    /**
     * Creates a <code>Transferable</code> capable of transferring
     * the specified <code>AlignmentUnit</code>.
     */
    public AlignableSelection(Alignable data) {
        this.data = data;
    }

    /**
     * Returns an array of flavors in which this <code>Transferable</code>
     * can provide the data. <code>AlignmentUnit</code>
     * is properly supported.
     *
     * @return an array of length one, whose elements are <code>AlignmentUnit</code>
     */
    public DataFlavor[] getTransferDataFlavors() {
        // returning flavors itself would allow client code to modify
        // our internal behavior
	return (DataFlavor[])flavors.clone();
    }

    /**
     * Returns whether the requested flavor is supported by this
     * <code>Transferable</code>.
     *
     * @param flavor the requested flavor for the data
     * @return true if <code>flavor</code> is equal to
     *   <code>AlignmentUnit</code>
     *   false if <code>flavor</code>
     *   is not one of the above flavors
     * @throws NullPointerException if flavor is <code>null</code>
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
	// JCK Test StringSelection0003: if 'flavor' is null, throw NPE
        for (int i = 0; i < flavors.length; i++) {
	    if (flavor.equals(flavors[i])) {
	        return true;
	    }
	}
	return false;
    }

    /**
     * Returns the <code>Transferable</code>'s data in the requested
     * <code>DataFlavor</code> if possible. If the desired flavor is
     * <code>AlignmentUnit</code>, or an equivalent flavor,
     * the <code>AlignmentUnit</code> representing the selection is
     * returned. 
     *
     * @param flavor the requested flavor for the data
     * @return the data in the requested flavor, as outlined above
     * @throws UnsupportedFlavorException if the requested data flavor is
     *         not equivalent to either <code>AlignmentUnit</code>
     * @throws IOException if an IOException occurs while retrieving the data.
     *         By default, AlignmentUnitSelection never throws this exception, but a
     *         subclass may.
     * @throws NullPointerException if flavor is <code>null</code>
     */
    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException
    {
	// JCK Test StringSelection0007: if 'flavor' is null, throw NPE
	if (flavor.equals(flavors[ALIGNABLE])) {
	    return (Object)data;
	} else {
	    throw new UnsupportedFlavorException(flavor);
	}
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
