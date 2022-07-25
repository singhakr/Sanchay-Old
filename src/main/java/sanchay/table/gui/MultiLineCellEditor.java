/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.table.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import sanchay.corpus.parallel.AlignmentUnit;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class MultiLineCellEditor extends DefaultCellEditor
{
    protected Object value;
    protected String langEnc = "eng::utf8";

    public MultiLineCellEditor(String langEnc)
    {
        super(new JTextField());
        
        final JTextArea textArea = new JTextArea();

        this.langEnc = langEnc;

        UtilityFunctions.setComponentFont(textArea, langEnc);
        
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        editorComponent = scrollPane;

        delegate = new DefaultCellEditor.EditorDelegate()
        {
            public void setValue(Object val)
            {
                value = val;
                
                if (value instanceof AlignmentUnit)
                {
                    AlignmentUnit alignmentUnit = (AlignmentUnit) value;

                    Object alignmentObject = alignmentUnit.getAlignmentObject();

                    if (alignmentObject instanceof SSFSentence)
                    {
                        SSFSentence sentence = (SSFSentence) alignmentObject;

                        textArea.setText(sentence.getRoot().makeRawSentence());
                    } else if (alignmentObject instanceof SSFNode)
                    {
                        SSFNode node = (SSFNode) alignmentObject;
                        textArea.setText(node.makeRawSentence());
                    } else
                    {
                        textArea.setText((value == null) ? "" : value.toString());
                    }
                }
                else
                    textArea.setText((value != null) ? value.toString() : "");
            }

            public Object getCellEditorValue()
            {
                if(value instanceof String)
                    return textArea.getText();
                else
                    return value;
            }
        };
    }
}
