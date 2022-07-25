package sanchay.table.gui;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.gui.common.SanchayDataFlavors;

public class SanchayTransferableObject implements Transferable {

  DataFlavor flavors[] = {SanchayDataFlavors.SSF_LEXITEM_FLAVOR, SanchayDataFlavors.SSF_PHRASE_FLAVOR};

  protected Object sanchayTransferableObject;

  public SanchayTransferableObject(Object sanchayTransferableObject) {
    this.sanchayTransferableObject = sanchayTransferableObject;
  }

  public Object getSanchayTransferableObject()
  {
      return sanchayTransferableObject;
  }

  public synchronized DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor.getRepresentationClass() == SSFPhrase.class
            || flavor.getRepresentationClass() == SSFLexItem.class);
  }

  public synchronized Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor)) {
      return (Object) sanchayTransferableObject;
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }
}