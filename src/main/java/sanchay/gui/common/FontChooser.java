/*
 * FontChooser1.java
 *
 * Created on April 16, 2006, 9:00 PM
 */

package sanchay.gui.common;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

import sanchay.GlobalProperties;

/**
 *
 * @author  anil
 */
public class FontChooser extends javax.swing.JDialog implements ActionListener {
    
    protected String langEncCode;

    protected DefaultComboBoxModel fontFamilies;
    protected DefaultComboBoxModel fontSizes;
    
    protected SimpleAttributeSet attributes;
    protected Font newFont;
    protected Color newColor;
    
    protected Font presentFont;
    protected Color presentColor;
    
    protected boolean update;
    
    /** Creates new form FontChooser1 */
    public FontChooser(java.awt.Frame parent, String langEnc, Font presentFont, Color presentColor, boolean modal) {
	this(parent, langEnc, modal);
	
	this.presentFont = presentFont;
	this.presentColor = presentColor;
	
	initAttributes();
    }

    public FontChooser(java.awt.Dialog parent, String langEnc, Font presentFont, Color presentColor, boolean modal) {
	this(parent, langEnc, modal);
	
	this.presentFont = presentFont;
	this.presentColor = presentColor;
	
	initAttributes();
    }

    public FontChooser(java.awt.Frame parent, String langEnc, boolean modal) {
	super(parent, GlobalProperties.getIntlString("Font_Chooser"), true);
	
	init(langEnc, modal);
    }

    public FontChooser(java.awt.Dialog parent, String langEnc, boolean modal) {
	super(parent, GlobalProperties.getIntlString("Font_Chooser"), true);

	init(langEnc, modal);
    }
    
    private void init(String langEnc, boolean modal)
    {
	initComponents();
	
	String lCode = SanchayLanguages.getLanguageCodeFromLECode(langEnc);
	String eCode = SanchayLanguages.getEncodingCodeFromLECode(langEnc);
	
	String langName = SanchayLanguages.getLanguageName(lCode);
	String encName = SanchayLanguages.getEncodingName(eCode);
	
	setTitle(GlobalProperties.getIntlString("Font_Chooser:_") + langName + ", " + encName);
	
	setSize(450, 500);
	attributes = new SimpleAttributeSet();

	// Make sure that any way the user cancels the window does the right thing
	addWindowListener(new WindowAdapter() {
	  public void windowClosing(WindowEvent e) {
	    closeAndCancel();
	  }
	});

	getRootPane().setDefaultButton(okButton);
	
	langEncCode = langEnc;

	fontFamilies = new DefaultComboBoxModel();
	SanchayLanguages.fillFonts(fontFamilies, langEncCode, allSystemFonts.isSelected());
	fontName.setModel(fontFamilies);

	fontSizes = new DefaultComboBoxModel();
	SanchayLanguages.fillFontSizes(fontSizes, 4, 64, 2);
	fontSize.setModel(fontSizes);

	fontName.addActionListener(this);
        fontSize.addActionListener(this);
        fontBold.addActionListener(this);
        fontItalic.addActionListener(this);

	// Set up the color chooser panel and attach a change listener so that color
	// updates get reflected in our preview label.
	colorChooser.getSelectionModel()
		    .addChangeListener(new ChangeListener() {
	  public void stateChanged(ChangeEvent e) {
	    updatePreviewColor();
	  }
	});

	previewLabel.setForeground(colorChooser.getColor());

	initAttributes();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        attributesJPanel = new javax.swing.JPanel();
        fontJPanel = new javax.swing.JPanel();
        fontName = new javax.swing.JComboBox();
        allSystemFonts = new javax.swing.JCheckBox();
        sizeJLabel = new javax.swing.JLabel();
        fontSize = new javax.swing.JComboBox();
        fontBold = new javax.swing.JCheckBox();
        fontItalic = new javax.swing.JCheckBox();
        colorChooser = new javax.swing.JColorChooser();
        sampleJPanel = new javax.swing.JPanel();
        previewLabel = new javax.swing.JLabel();
        commandsJPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        attributesJPanel.setLayout(new java.awt.BorderLayout());

        fontJPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        fontJPanel.add(fontName);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sanchay"); // NOI18N
        allSystemFonts.setText(bundle.getString("All_System_Fonts")); // NOI18N
        allSystemFonts.setToolTipText(bundle.getString("Do_you_want_to_view_all_the_system_fonts?")); // NOI18N
        allSystemFonts.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        allSystemFonts.setMargin(new java.awt.Insets(0, 0, 0, 0));
        allSystemFonts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allSystemFontsActionPerformed(evt);
            }
        });
        fontJPanel.add(allSystemFonts);

        sizeJLabel.setLabelFor(fontName);
        sizeJLabel.setText(bundle.getString("Size:")); // NOI18N
        fontJPanel.add(sizeJLabel);

        fontSize.setEditable(true);
        fontJPanel.add(fontSize);

        fontBold.setText(bundle.getString("Bold")); // NOI18N
        fontBold.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fontBold.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fontJPanel.add(fontBold);

        fontItalic.setText(bundle.getString("Italic")); // NOI18N
        fontItalic.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fontItalic.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fontJPanel.add(fontItalic);

        attributesJPanel.add(fontJPanel, java.awt.BorderLayout.CENTER);
        attributesJPanel.add(colorChooser, java.awt.BorderLayout.SOUTH);

        getContentPane().add(attributesJPanel, java.awt.BorderLayout.NORTH);

        sampleJPanel.setLayout(new java.awt.BorderLayout());

        previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previewLabel.setText(bundle.getString("This_is_how_this_font_looks.")); // NOI18N
        previewLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 10, 1));
        sampleJPanel.add(previewLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(sampleJPanel, java.awt.BorderLayout.CENTER);

        okButton.setText(bundle.getString("OK")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(okButton);

        cancelButton.setText(bundle.getString("Cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        commandsJPanel.add(cancelButton);

        getContentPane().add(commandsJPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void allSystemFontsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allSystemFontsActionPerformed
// TODO add your handling code here:
        update = false;
        SanchayLanguages.fillFonts(fontFamilies, langEncCode, allSystemFonts.isSelected());
        update = true;        
    }//GEN-LAST:event_allSystemFontsActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// TODO add your handling code here:
	closeAndCancel();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
// TODO add your handling code here:
	closeAndSave();
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void initAttributes()
    {
        update = true;
        
	if(presentFont != null)
	{
	    fontName.setSelectedItem(presentFont.getFamily());
	    
	    fontSize.setSelectedItem(Integer.toString(presentFont.getSize()));
	    
	    fontBold.setSelected(presentFont.isBold());
	    fontItalic.setSelected(presentFont.isItalic());
	}
	
	if(presentColor != null)
	{
	    colorChooser.setColor(presentColor);
	    previewLabel.setForeground(presentColor);
	}
	
	updateFont(null);
    }
    
    private void updateFont(ActionEvent ae)
    {
	// Check the name of the font
	if (!StyleConstants.getFontFamily(attributes)
			       .equals(fontName.getSelectedItem())) {
	      StyleConstants.setFontFamily(attributes, 
					   (String)fontName.getSelectedItem());
	}
	// Check the font size (no error checking yet)
	if (StyleConstants.getFontSize(attributes) != 
				       getSelectedFontSize()) {
	    StyleConstants.setFontSize(attributes, getSelectedFontSize());
	}
	// Check to see if the font should be bold
	if (StyleConstants.isBold(attributes) != fontBold.isSelected()) {
	    StyleConstants.setBold(attributes, fontBold.isSelected());
	}
	// Check to see if the font should be italic
	if (StyleConstants.isItalic(attributes) != fontItalic.isSelected()) {
	    StyleConstants.setItalic(attributes, fontItalic.isSelected());
	}
	// and update our preview label
	updatePreviewFont();
    }
    
    // Ok, something in the font changed, so figure that out and make a
    // new font for the preview label
    public void actionPerformed(ActionEvent ae) {
        if(update)
            updateFont(ae);
    }

    // Get the appropriate font from our attributes object and update
    // the preview label
    protected void updatePreviewFont() {
	String name = StyleConstants.getFontFamily(attributes);
	boolean bold = StyleConstants.isBold(attributes);
	boolean ital = StyleConstants.isItalic(attributes);
	int size = StyleConstants.getFontSize(attributes);

	Font fnt = null;
	
	//Bold and italic don?t work properly in beta 4.
	if(SanchayLanguages.findSystemFont(name) != null)
	{
	    fnt = new Font(name, (bold ? Font.BOLD : 0) +
				    (ital ? Font.ITALIC : 0), size);
	}
	else
	{
	    fnt = SanchayLanguages.getFont(name);
	    fnt = fnt.deriveFont((bold ? Font.BOLD : 0) + (ital ? Font.ITALIC : 0), size);
	}

	previewLabel.setFont(fnt);
    }

    // Get the appropriate color from our chooser and update previewLabel
    protected void updatePreviewColor() {
    previewLabel.setForeground(colorChooser.getColor());
	// Manually force the label to repaint
	previewLabel.repaint();
    }

    public String getSelectedFontFamily()
    {
	return (String) fontName.getSelectedItem();
    }

    public int getSelectedFontSize()
    {
	return Integer.parseInt((String) fontSize.getSelectedItem());
    }
    
    public Font getNewFont() { return newFont; }
    
    public Color getNewColor() { return newColor; }
    
    public AttributeSet getAttributes() { return attributes; }

    public void closeAndSave() {
	// Save font & color information
	newFont = previewLabel.getFont();
	newColor = previewLabel.getForeground();

	// Close the window
	setVisible(false);
    }

    public void closeAndCancel() {
	// Erase any font information and then close the window
	newFont = null;
	newColor = null;
	setVisible(false);
    }
    
    public void enableFontFamilies(boolean b)
    {
	fontName.setEnabled(b);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		new FontChooser(new javax.swing.JFrame(), GlobalProperties.getIntlString("eng-utf8"), true).setVisible(true);
	    }
	});
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JCheckBox allSystemFonts;
    protected javax.swing.JPanel attributesJPanel;
    protected javax.swing.JButton cancelButton;
    protected javax.swing.JColorChooser colorChooser;
    protected javax.swing.JPanel commandsJPanel;
    protected javax.swing.JCheckBox fontBold;
    protected javax.swing.JCheckBox fontItalic;
    protected javax.swing.JPanel fontJPanel;
    protected javax.swing.JComboBox fontName;
    protected javax.swing.JComboBox fontSize;
    protected javax.swing.JButton okButton;
    protected javax.swing.JLabel previewLabel;
    protected javax.swing.JPanel sampleJPanel;
    protected javax.swing.JLabel sizeJLabel;
    // End of variables declaration//GEN-END:variables
    
}
