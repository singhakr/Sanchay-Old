/*
 * UtilityFunctions.java
 *
 * Created on September 29, 2005, 10:06 PM
 */

package sanchay.util;

import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import java.awt.*;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;

import org.clapper.util.html.HTMLUtil;
import sanchay.GlobalProperties;
import sanchay.common.types.CorpusType;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.gui.common.*;
import sanchay.properties.KeyValueProperties;
import sanchay.table.gui.SanchayJTable;
import sanchay.table.*;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.tree.gui.action.AttributeValueActionListener;

/**
 * @author Anil Kumar Singh
 */
public class UtilityFunctions {

    /**
     * Creates a new instance of UtilityFunctions
     */
    public UtilityFunctions() {
    }

    public static boolean addItemToJCoboBox(JComboBox cb, Object item) {
        if (item == null)
            return false;

        if (item.getClass().getName().equals(""))
            return false;

        boolean found = false;

        int count = cb.getItemCount();
        for (int i = 0; i < count; i++) {
            if (cb.getItemAt(i).equals(item) == true) {
                found = true;
                i = count;
            }
        }

        if (found == false) {
            cb.addItem(item);
            return true;
        }

        return false;
    }

    public static boolean isWordBoundary(char c) {
        if (c == ' ' || c == ',' || c == '.' || c == '|' || c == ':' || c == '-'
                || c == '+' || c == '=' || c == '\\' || c == '&' || c == '\"'
                || c == '/' || c == '!' || c == '@' || c == '#'
                || c == '$' || c == '%' || c == '(' || c == ')' || c == '_'
                || c == '{' || c == '}' || c == '[' || c == ']' || c == ';'
                || c == '<' || c == '>' || c == '?')
        // || c == '\'' || c == '`' || c == '^' || c == '*' || c == '~'
        {
            return true;
        }

        return false;
    }

    public static boolean isWordChar(char c) {
        int ci = (int) c;

        if (ci >= '0' && ci <= 'z')
            return true;

        return false;
    }

    public static Color generateColor(Color startColor, int steps, int increment) {
        if (increment > 255 / 4 - 1)
            return startColor;

        int red = startColor.getRed();
        int blue = startColor.getBlue();
        int green = startColor.getGreen();

        int lowerLimit = 100;

        for (int i = 0; i < steps; i++) {
            // Cyclic change
            if (red + increment < 255 && red + increment >= lowerLimit)
                red += increment;
            else if (red + increment - 255 >= lowerLimit)
                red = red + increment - 255;
            else
                red = lowerLimit;

            if (blue - increment >= 0 && blue + increment >= lowerLimit)
                blue -= increment;
            else if (blue + increment - 255 >= lowerLimit)
                blue = blue - increment + 255;
            else
                blue = lowerLimit + increment;

            if (green + increment < 255 && green + increment >= lowerLimit)
                green += increment;
            else if (green + increment - 255 >= lowerLimit)
                green = green + increment - 255;
            else
                green = 255 - increment;
        }

        return new Color(red, green, blue);
    }

    public static String validateFilePath(String fp, String workspace) {
        if (fp.startsWith(File.separator) == true) {
            if (fp.startsWith(workspace) == false) {
//                JOptionPane.showMessageDialog(this, "Files should be in the workspace directory: " + workspace, "Error", JOptionPane.ERROR_MESSAGE);
                return "";
            }
//            else
//            {
//                fn = taskPropFile.substring(ws.length());
//                fn.replaceFirst(ws, "");
//            }
        }

        return fp;
    }

    public static boolean areDifferent(String[] strings) {
        if (strings == null && strings.length == 1)
            return false;

        String stringsCopy[] = new String[strings.length];

        // Copy
        for (int i = 0; i < strings.length; i++)
            stringsCopy[i] = strings[i];

        Arrays.sort(stringsCopy);

        boolean different = false;
        for (int i = 1; i < stringsCopy.length; i++) {
            if (stringsCopy[i].equals(stringsCopy[i - 1]) == false) {
                different = true;
                i = stringsCopy.length; // break
            }
        }

        return different;
    }

    public static boolean areDifferentCombinations(String[] strings1, String[] strings2) {
        Arrays.sort(strings1);
        Arrays.sort(strings2);

        return areDifferentPermutations(strings1, strings2);
    }

    public static boolean areDifferentPermutations(String[] strings1, String[] strings2) {
        if (strings1.length != strings2.length)
            return true;

        int common = countcommonStrings(strings1, strings2);

        // if sizes are the same
        if (common < strings1.length)
            return true;

        return false;
    }

    public static int countcommonStrings(String[] strings1, String[] strings2) {
        String cstrings[] = getCommonStrings(strings1, strings2);

        if (cstrings == null)
            return 0;

        return cstrings.length;
    }

    // Returns an array of common strings.
    public static String[] getCommonStrings(String[] strings1, String[] strings2) {
        Vector csindices = getCommonStringIndices(strings1, strings2);

        if (csindices == null)
            return null;

        int count = ((Vector) csindices.get(0)).size();

        String cstrings[] = new String[count];

        for (int i = 0; i < count; i++) {
            cstrings[i] = strings1[((Integer) ((Vector) csindices.get(0)).get(i)).intValue()];
        }

        return cstrings;
    }

    // Returns a Vector of Vector. The outer Vector contains two Vectors, one for each arguement array.
    // The inner Vector contains indices of common strings.
    public static Vector getCommonStringIndices(String[] strings1, String[] strings2) {
        if (strings1 == null || strings2 == null)
            return null;

        Vector common = new Vector(2);
        common.add(new Vector(0, 3));
        common.add(new Vector(0, 3));

        for (int i = 0, j = 0; i < strings1.length || j < strings2.length; ) {
            if (strings1[i].compareTo(strings2[j]) == 0) {
                ((Vector) common.get(0)).add(new Integer(i));
                ((Vector) common.get(1)).add(new Integer(j));
                i++;
                j++;
            } else if (strings1[i].compareTo(strings2[j]) < 0) {
                i++;
            } else {
                j++;
            }
        }

        return common;
    }

    // Returns a Vector of Vector. The outer Vector contains two Vectors, one for each arguement array.
    // The inner Vector contains indices of non-common strings.
    public static Vector getUncommonStringIndices(String[] strings1, String[] strings2) {
        Vector csindices = getCommonStringIndices(strings1, strings2);

        Vector common = new Vector(2);
        common.add(new Vector(0, 3));
        common.add(new Vector(0, 3));

        if (csindices == null) {
            if (strings1 == null && strings2 == null)
                return null;
            else if (strings1 == null) {
                for (int i = 0; i < strings2.length; i++)
                    ((Vector) common.get(1)).add(new Integer(i));

                return common;
            } else if (strings2 == null) {
                for (int i = 0; i < strings1.length; i++)
                    ((Vector) common.get(0)).add(new Integer(i));

                return common;
            }
        }

        for (int i = 0; i < strings2.length; i++) {
            // TODO
        }

        return common;
    }

    public static String[] removeBlankLines(String[] lines) {
        Vector linvec = new Vector(0, 5);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals("") == false)
                linvec.add(lines[i]);
        }

        String lns[] = new String[linvec.size()];
        for (int i = 0; i < lns.length; i++)
            lns[i] = (String) linvec.get(i);

        return lns;
    }

    public static void fitColumnsToContent(JTable table, int hgap) {

        int ccount = table.getColumnCount();

        if (ccount <= 0)
            return;

        SanchayTableModel model = (SanchayTableModel) table.getModel();
        TableColumn column = null;
        //        Component comp = null;
        int cellWidth = 0;
        int inset = 20;
        //    int tableWidth = 0;

        for (int i = 0; i < ccount; i++) {
            String longVal = model.getValueAt(model.getLongestValueRowIndex(i), i).toString();

            FontMetrics fm = table.getFontMetrics(table.getFont());
            cellWidth = fm.stringWidth(longVal);

            column = table.getColumnModel().getColumn(i);

            column.setPreferredWidth(cellWidth + inset + hgap);
            //            tableWidth += cellWidth + inset;
        }

//    Dimension size = new Dimension();
//    size.width = tableWidth;
//    size.height = (table.getRowHeight() + table.getRowMargin()) * model.getRowCount();
//
//    table.setPreferredScrollableViewportSize(size);

        table.revalidate();
        table.repaint();
    }

    public static void fitColumnsToContent(JTable table) {

        int ccount = table.getColumnCount();

        if (ccount <= 0)
            return;

        SanchayTableModel model = (SanchayTableModel) table.getModel();

        if (model == null)
            return;

        TableColumn column = null;
//        Component comp = null;
        int cellWidth = 0;
        int inset = 20;
//    int tableWidth = 0;

        for (int i = 0; i < ccount; i++) {
            Object longValObj = model.getValueAt(model.getLongestValueRowIndex(i), i);

            if (longValObj == null)
                continue;

            String longVal = longValObj.toString();

            FontMetrics fm = table.getFontMetrics(table.getFont());
            cellWidth = fm.stringWidth(longVal);

            column = table.getColumnModel().getColumn(i);

            column.setPreferredWidth(cellWidth + inset);
//            tableWidth += cellWidth + inset;
        }

//    Dimension size = new Dimension();
//    size.width = tableWidth;
//    size.height = (table.getRowHeight() + table.getRowMargin()) * model.getRowCount();
//	
        table.revalidate();
        table.repaint();
    }

    public static boolean flattenDirectoryStructure(File outDir, File curDir, boolean delOriginal, String curPrefix) throws FileNotFoundException, IOException {
        if (outDir.exists() == false) {
            outDir.mkdirs();
        }

        if (curDir.isFile()) {
            File newFile = new File(outDir, curPrefix + "-" + curDir.getName());

            try {
                UtilityFunctions.copyFile(curDir, newFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (delOriginal)
                return curDir.delete();
            else {
                System.out.println(GlobalProperties.getIntlString("Couldn't_delete_file:_") + curDir);
                return false;
            }
        }

        boolean success = true;

        File files[] = curDir.listFiles();

        for (int i = 0; i < files.length; i++) {

            if (curPrefix == null || curPrefix.equals(""))
                success = flattenDirectoryStructure(outDir, files[i], delOriginal, curDir.getName());
            else
                success = flattenDirectoryStructure(outDir, files[i], delOriginal, curPrefix + "-" + curDir.getName());
        }

        if (delOriginal)
            success &= curDir.delete();

        return success;
    }

    public static boolean removeDirectoryRecursive(File dir) throws FileNotFoundException, IOException {
        if (dir.isFile())
            return dir.delete();

        boolean success = true;

        File files[] = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            success = removeDirectoryRecursive(files[i]);
        }

        return success;
    }

    public static boolean removeEmptyDirectoryRecursive(File dir) throws FileNotFoundException, IOException {
        if (dir.isFile())
            return true;

        boolean success = true;

        File files[] = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            success = removeEmptyDirectoryRecursive(files[i]);
        }

        files = dir.listFiles();

        if (dir.isDirectory() && files.length == 0)
            dir.delete();

        return success;
    }

    // Assuming that the incoming file(s) have been preprocessed
    public static void extractWordTypes(/*File or dir*/ File inFile, String cs, Hashtable wrdTypes)
            throws FileNotFoundException, IOException {
        if (inFile.isFile()) {
            BufferedReader inReader = null;

            if (cs != null && cs.equals("") == false)
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
            else
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

            String line = "";

            while ((line = inReader.readLine()) != null) {
                line = line.trim();

                String wrds[] = line.split("[ ]");

                for (int i = 0; i < wrds.length; i++) {
                    Integer wtfreq = (Integer) wrdTypes.get(wrds[i]);

                    if (wtfreq == null)
                        wrdTypes.put(wrds[i], new Integer(1));
                    else {
                        wrdTypes.put(wrds[i], new Integer(wtfreq.intValue() + 1));
                    }
                }
            }
        }

        File files[] = inFile.listFiles();

        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {
            extractWordTypes(files[i], cs, wrdTypes);
        }
    }

    public static void saveTextToFile(String text, String path, String cs) throws FileNotFoundException, IOException {
        PrintStream ps = new PrintStream(path, cs);

        ps.print(text);

        ps.close();
    }

    public static String getTextFromFile(String path, String cs) throws FileNotFoundException, IOException {
        String text = "";
        File inFile = new File(path);

        if (inFile.isFile()) {
            BufferedReader inReader = null;

            if (cs != null && cs.equals("") == false)
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
            else
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

            String line = "";

            while ((line = inReader.readLine()) != null) {
                text += line + "\n";
            }
        }

        return text;
    }

    public static String[] getTextLinesFromFile(String path, String cs) throws FileNotFoundException, IOException {
        Vector lines = new Vector(10, 10);
        File inFile = new File(path);

        if (inFile.isFile()) {
            BufferedReader inReader = null;

            if (cs != null && cs.equals("") == false)
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
            else
                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

            String line = "";

            while ((line = inReader.readLine()) != null) {
                lines.add(line);
            }
        }

        String lineArray[] = new String[lines.size()];
        return (String[]) lines.toArray(lineArray);
    }

    // Assuming that the incoming file(s) have been preprocessed
    public static void appendWordTypes(/*File or dir*/ String inFile, String ics, String outFile, String ocs)
            throws FileNotFoundException, IOException {
        Hashtable newWTs = new Hashtable(0, 25);

        UtilityFunctions.extractWordTypes(new File(inFile), ics, newWTs);

        System.out.println(GlobalProperties.getIntlString("Word_types_in_the_new_directory/file:_") + newWTs.size());

        File ofile = new File(outFile);

        if (ofile.exists() == false)
            ofile.createNewFile();

        KeyValueProperties oldWTs = new KeyValueProperties();
        oldWTs.read(outFile, ocs);

        System.out.println(GlobalProperties.getIntlString("Word_types_in_the_old_file:_") + oldWTs.countProperties());

        Enumeration enm = newWTs.keys();

        while (enm.hasMoreElements()) {
            String wt = (String) enm.nextElement();
            Integer freq = (Integer) newWTs.get(wt);

            String oldFreqStr = oldWTs.getPropertyValue(wt);

            if (oldFreqStr == null) {
                oldWTs.addProperty(wt, freq.toString());
            } else {
                Integer oldFreq = new Integer(Integer.parseInt(oldFreqStr));
                oldWTs.addProperty(wt, (new Integer(oldFreq.intValue() + freq.intValue())).toString());
            }
        }

        oldWTs.save(outFile, ocs);
    }

    public static void setComponentFont(Component c, String lang) {
        if (lang == null || lang.equals(GlobalProperties.getIntlString("eng")) || lang.equals(GlobalProperties.getIntlString("eng::utf8"))) {
            Font presentFont = c.getFont();
            c.setFont(presentFont.deriveFont(Font.BOLD, SanchayLanguages.DEFAULT_FONT_SIZE_ENG));
            return;
        }

        Font presentFont = c.getFont();

//	c.setFont(new Font(fontName, Font.BOLD, 18));
        Font newFont = SanchayLanguages.getDefaultLangEncFont(lang);

        if (newFont != null) {

            newFont = newFont.deriveFont(newFont.getSize());

            if (c instanceof JTextComponent) {
                SanchayLanguages.setTextDirection(c, lang);
            }

            if (c instanceof JTextPane) {
                JTextComponent jtc = (JTextComponent) c;

                StyledDocument doc = (StyledDocument) jtc.getDocument();

//		if(doc != null && doc.getLength() > 0) {
                if (doc != null) {
                    jtc.setLocale(SanchayLanguages.getLocale(lang));

                    Style attrs = doc.getStyle("default");
                    //                StyleConstants.setFontFamily(attr,"Arial");
                    //                SimpleAttributeSet attrs = new SimpleAttributeSet();
                    StyleConstants.setFontFamily(attrs, newFont.getFontName());
                    StyleConstants.setFontSize(attrs, newFont.getSize());
                    StyleConstants.setBold(attrs, newFont.isBold());
                    StyleConstants.setBold(attrs, newFont.isItalic());

                    doc.setCharacterAttributes(0, doc.getLength(), attrs, true);

                    jtc.updateUI();
                }
            } else {
                c.setLocale(SanchayLanguages.getLocale(lang));
                c.setFont(newFont);
            }
        } else {
//        JOptionPane.showMessageDialog(null, GlobalProperties.getIntlString("No_font_found_for_the_selected_encoding."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
            presentFont = c.getFont();
            c.setLocale(SanchayLanguages.getLocale(lang));
            c.setFont(presentFont.deriveFont(Font.BOLD, SanchayLanguages.DEFAULT_FONT_SIZE_ENG));
        }
//	Locale l = new Locale("bn");
//	c.setLocale(l);
//
//	Locale.setDefault(l);
//	c.setFont(new Font("Lohit Bengali", Font.BOLD, 14));
    }

    public static void setComponentFont(Component c, String lang, int size) {
        setComponentFont(c, lang);
        Font presentFont = c.getFont();
        c.setFont(presentFont.deriveFont(Font.BOLD, size));
    }

    public static void setJOptionPaneFont(JOptionPane pane, String lang) {
        if (lang == null || lang.equals(GlobalProperties.getIntlString("eng")) || lang.equals(GlobalProperties.getIntlString("eng::utf8"))) {
            Font presentFont = pane.getFont();
            Font newFont = presentFont.deriveFont(Font.BOLD, SanchayLanguages.DEFAULT_FONT_SIZE_ENG);
            pane.putClientProperty(GlobalProperties.getIntlString("OptionPane.font"), newFont);
            return;
        }

        Font presentFont = pane.getFont();

//	c.setFont(new Font(fontName, Font.BOLD, 18));
        Font newFont = SanchayLanguages.getDefaultLangEncFont(lang);

        if (newFont != null) {
            pane.putClientProperty(GlobalProperties.getIntlString("OptionPane.font"), presentFont.deriveFont(Font.BOLD, SanchayLanguages.DEFAULT_FONT_SIZE_ENG));
        } else {
            JOptionPane.showMessageDialog(null, GlobalProperties.getIntlString("No_font_found_for_the_selected_encoding."), GlobalProperties.getIntlString("Error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void setJOptionPaneFont(JOptionPane pane, String lang, int size) {
        setJOptionPaneFont(pane, lang);
        Font presentFont = pane.getFont();
        pane.putClientProperty(GlobalProperties.getIntlString("OptionPane.font"), presentFont.deriveFont(Font.BOLD, size));
    }

    public static void fillCorpusTypes(JComboBox jcb) {
        // Filling up Corpus Types
        Vector allCorpusTypes = new Vector(0, 3);

        DefaultComboBoxModel cbm = new DefaultComboBoxModel();

        Enumeration enm = CorpusType.elements();

        while (enm.hasMoreElements()) {
            CorpusType ctype = (CorpusType) enm.nextElement();
            cbm.addElement(ctype);
        }

        CorpusType initCorpusType = CorpusType.SSF_FORMAT;

        jcb.setModel(cbm);
        jcb.setSelectedItem(initCorpusType);
    }

    public static void fillComboBoxIntegers(DefaultComboBoxModel cbm, int beg, int end, int step) {
        cbm.removeAllElements();

        for (int i = beg; i <= end; i += step) {
            cbm.addElement(new Integer(i));
        }
    }

    public static String selectItemOnTyping(String subStringTyped, JComboBox jcb) {
        int count = jcb.getItemCount();

        for (int i = 0; i < count; i++) {
            String item = (String) jcb.getItemAt(i);

            if (item.startsWith(subStringTyped))
                return item;
        }

        return null;
    }

    public static void moveFile(File inFile, File outFile) throws IOException {
        copyFile(inFile, outFile);
        inFile.delete();
    }

    public static void copyFile(File inFile, File outFile) throws IOException {
        InputStream in = new FileInputStream(inFile);
        OutputStream out = new FileOutputStream(outFile);

        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);

        in.close();
        out.close();
    }

    public static void moveFile(File inFile, String ics, File outFile, String ocs) throws IOException {
        moveFile(inFile.getAbsolutePath(), ics, outFile.getAbsolutePath(), ocs);
    }

    public static void moveFile(String inFile, String ics, String outFile, String ocs) throws IOException {
        copyFile(inFile, ics, outFile, ocs);

        (new File(inFile)).delete();
    }

    public static void copyFile(File inFile, String ics, File outFile, String ocs) throws IOException {
        copyFile(inFile.getAbsolutePath(), ics, outFile.getAbsolutePath(), ocs);
    }

    public static void copyFile(String inFile, String ics, String outFile, String ocs) throws IOException {
        BufferedReader inReader = null;

        if (ics != null && ics.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ics));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        File ifile = new File(inFile);
        File ofile = new File(outFile);

        if (ofile.isDirectory()) {
            ofile = new File(ofile, ifile.getName());
            outFile = ofile.getAbsolutePath();
        }

        String line = "";
        PrintStream ps = new PrintStream(outFile, ocs);

        while ((line = inReader.readLine()) != null) {
            ps.println(line);
        }

        inReader.close();
        ps.close();
    }

    public static Object findEqualObject(ListModel lm, Object obj) {
        int count = lm.getSize();

        for (int i = 0; i < count; i++) {
            Object lobj = lm.getElementAt(i);

            if (obj instanceof File) {
                if (((File) lobj).getAbsolutePath().equals(((File) obj).getAbsolutePath()))
                    return lobj;
            } else {
                if (lobj.equals(obj))
                    return lobj;
            }
        }

        return null;
    }

    public static int findIndexOfEqualObject(ListModel lm, Object obj) {
        int count = lm.getSize();

        for (int i = 0; i < count; i++) {
            Object lobj = lm.getElementAt(i);

            if (obj instanceof File) {
                String lstr = ((File) lobj).getAbsolutePath();
                String str = ((File) obj).getAbsolutePath();

                if (lstr.equals(str))
                    return i;
            } else {
                if (lobj.equals(obj))
                    return i;
            }
        }

        return -1;
    }

    public static void trimSpaces(String inFile, String ics,
                                  String outFile, String ocs) throws FileNotFoundException, IOException {

        BufferedReader inReader = null;

        if (ics != null && ics.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ics));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";
        PrintStream ps = new PrintStream(outFile, ocs);

        while ((line = inReader.readLine()) != null) {
            line = line.trim();
            ps.println(line);
        }
    }

    public static void naiiveTokenization(String inFile, String ics,
                                          String outFile, String ocs, String langEnc) throws FileNotFoundException, IOException {
        BufferedReader inReader = null;

        if (ics != null && ics.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ics));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";
        PrintStream ps = new PrintStream(outFile, ocs);

        int id = 0;
        while ((line = inReader.readLine()) != null) {
//	    line = line.replaceAll("(?dum)[\\.][ ]*$", "\u0964");
            line = line.replaceAll("(?dum)[\\.][ ]*$", " \\.");
            line = line.replaceAll("(?dum)[\u0964]", " " + "\u0964");

            line = line.replaceAll("(?dum)[?]", " ?");
            line = line.replaceAll("(?dum)[!][ ]*$", " !");

            line = line.replaceAll("(?dum)[\\|]", " " + "\u0964");

            line = line.replaceAll("(?dum)[\\-] ", " -- ");
            line = line.replaceAll("(?dum) [\\-]", " -- ");
            line = line.replaceAll("(?dum) [\\-] ", " -- ");
            line = line.replaceAll("(?dum)[\\-]", " - ");
            line = line.replaceAll("(?dum)[ ]+", " ");
            line = line.replaceAll("(?dum)- -", "--");
            line = line.replaceAll("(?dum)-- --", "--");

            line = line.replaceAll("(?dum)[,]", " , ");
            line = line.replaceAll("(?dum)[:]", " : ");
            line = line.replaceAll("(?dum)[;]", " ; ");
            line = line.replaceAll("(?dum)[\\%]", " % ");
            line = line.replaceAll("(?dum)[\\@]", " @ ");
            line = line.replaceAll("(?dum)[\\/]", " / ");
            line = line.replaceAll("(?dum)[\\\\]", " \\ ");
            line = line.replaceAll("(?dum)[\\+]", " + ");
//	    line = line.replaceAll("(?dum)[\\-]", " - ");
            line = line.replaceAll("(?dum)[\\=]", " = ");
            line = line.replaceAll("(?dum)[\\*]", " * ");
            line = line.replaceAll("(?dum)[\\&]", " & ");
            line = line.replaceAll("(?dum)[\\_]", " _ ");
            line = line.replaceAll("(?dum)[\\~]", " ~ ");
            line = line.replaceAll("(?dum)[\\^]", " ^ ");
            line = line.replaceAll("(?dum)[\\<]", " < ");
            line = line.replaceAll("(?dum)[\\>]", " > ");
            line = line.replaceAll("(?dum)[\\#]", " # ");

            line = line.replaceAll("(?dum)[\\(]", " ( ");
            line = line.replaceAll("(?dum)[\\)]", " ) ");
            line = line.replaceAll("(?dum)[\\[]", " [ ");
            line = line.replaceAll("(?dum)[\\]]", " ] ");
            line = line.replaceAll("(?dum)[\\{]", " { ");
            line = line.replaceAll("(?dum)[\\}]", " } ");

            line = line.replaceAll("(?dum)[ ]+[']", " ' ");
            line = line.replaceAll("(?dum)['][ ]+", " ' ");

            line = line.replaceAll("(?dum)[`]", " ` ");
            line = line.replaceAll("(?dum)[\"]", " \" ");

            line = line.replaceAll("(?dum)[ ]+", " ");

            line = line.replaceAll("(?dum)^[ ]+", "");
            line = line.replaceAll("(?dum)[ ]+$", "");

            String curWord = "";

            boolean singleQuote = false;
            boolean doubleQuote = false;

            String wds[] = line.split("[ ]");

            int wcount = wds.length;

            line = "";

            for (int i = 0; i < wcount; i++) {
                wds[i] = wds[i].trim();

                curWord = wds[i];

                if (curWord.equals("`") || curWord.equals("'")) {
                    if (singleQuote) // ending
                    {
                        curWord = "'2";
                        singleQuote = false;
                    } else // beginning
                    {
                        curWord = "'1";
                        singleQuote = true;
                    }
                } else if (curWord.equals("\"")) {
                    if (doubleQuote) // ending
                    {
                        curWord = "\"2";
                        doubleQuote = false;
                    } else // beginning
                    {
                        curWord = "\"1";
                        doubleQuote = true;
                    }
                }

                // Apostrophe
                String parts[] = curWord.split("[']");
                if (parts != null && parts.length == 2) {
                    curWord = parts[0] + " '" + parts[1];
                }

                if (i < wcount - 1)
                    line += (curWord + " ");
                else
                    line += curWord;
            }

            SSFSentence sen = new SSFSentenceImpl();

            try {
                sen.makeSentenceFromRaw(line);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            id++;
            sen.setId("" + id);

            sen.print(ps);
        }

        inReader.close();
        ps.close();
    }

    public static void naiivePreprocessing(String inFile, String ics,
                                           String outFile, String ocs, String langEnc) throws FileNotFoundException, IOException {
        BufferedReader inReader = null;

        if (ics != null && ics.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ics));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";
        PrintStream ps = new PrintStream(outFile, ocs);

        Pattern p = Pattern.compile("[^\u0964]$", Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES | Pattern.MULTILINE);

        Pattern pBullet = Pattern.compile("[0-9][\\s]*\\.", Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES | Pattern.MULTILINE);

        //For shlokas etc.
        Pattern pShloka1 = Pattern.compile("\u0964[\\s]*\u0964[\\s]*([0-9]+)[\\s]*\u0964[\\s]*\u0964", Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES | Pattern.MULTILINE);
        Pattern pShloka2 = Pattern.compile("\u0964[\\s]*([0-9]+)[\\s]*\u0964", Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES | Pattern.MULTILINE);
        Pattern pShloka3 = Pattern.compile("\u0964[\\s]*\u0964", Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.UNIX_LINES | Pattern.MULTILINE);

        String prevLine = "";

        while ((line = inReader.readLine()) != null) {
            line = line.replaceAll("(?dum)[\\.][ ]*$", "\u0964");

            //Sentence breaking by dot
            Matcher mBullet = pBullet.matcher(line);

            if (mBullet.find() == false) {
//            if(line.trim().matches("^[0-9]+") == false)
                line = line.replaceAll("(?dum)[\\.]", " \\." + "\n");
            }

            line = line.replaceAll("(?dum)[\u0964][ ]*$", "\u0964");
            line = line.replaceAll("(?dum)[?][ ]*$", "?");
            line = line.replaceAll("(?dum)[!][ ]*$", "!");

            line = line.replaceAll("(?dum)[\\|]", " " + "\u0964");

            Matcher mShloka1 = pShloka1.matcher(line);
            Matcher mShloka2 = pShloka2.matcher(line);
            Matcher mShloka3 = pShloka3.matcher(line);

            if (mShloka1.find())
                line = line.replaceAll(mShloka1.group(0), " " + "\u0964\u0964 " + mShloka1.group(1) + " \u0964\u0964" + "\n");
            else if (mShloka2.find())
                line = line.replaceAll(mShloka2.group(0), " " + "\u0964 " + mShloka2.group(1) + " \u0964" + "\n");
            else if (mShloka3.find())
                line = line.replaceAll(mShloka3.group(0), " " + "\u0964\u0964" + "\n");
            else
                line = line.replaceAll("(?dum)[\u0964]", " " + "\u0964" + "\n");

            line = line.replaceAll("(?dum)[\\?]", " ?\n");
            line = line.replaceAll("(?dum)[!]", " !\n");

            line = line.replaceAll("(?dum)\\-\\-", " -- ");
            line = line.replaceAll("(?dum)[,]", " , ");
            line = line.replaceAll("(?dum)[:]", " : ");
            line = line.replaceAll("(?dum)[;]", " ; ");
            line = line.replaceAll("(?dum)[\\%]", " % ");
            line = line.replaceAll("(?dum)[\\@]", " @ ");
            line = line.replaceAll("(?dum)[\\/]", " / ");
            line = line.replaceAll("(?dum)[\\\\]", " \\ ");
            line = line.replaceAll("(?dum)[\\+]", " + ");
//	    line = line.replaceAll("(?dum)[\\-]", " - ");
            line = line.replaceAll("(?dum)[\\=]", " = ");
            line = line.replaceAll("(?dum)[\\*]", " * ");
            line = line.replaceAll("(?dum)[\\&]", " & ");
            line = line.replaceAll("(?dum)[\\_]", " _ ");
            line = line.replaceAll("(?dum)[\\~]", " ~ ");
            line = line.replaceAll("(?dum)[\\^]", " ^ ");
            line = line.replaceAll("(?dum)[\\<]", " < ");
            line = line.replaceAll("(?dum)[\\>]", " > ");
            line = line.replaceAll("(?dum)[\\#]", " # ");

            line = line.replaceAll("(?dum)[\\(]", " ( ");
            line = line.replaceAll("(?dum)[\\)]", " ) ");
            line = line.replaceAll("(?dum)[\\[]", " [ ");
            line = line.replaceAll("(?dum)[\\]]", " ] ");
            line = line.replaceAll("(?dum)[\\{]", " { ");
            line = line.replaceAll("(?dum)[\\}]", " } ");

            line = line.replaceAll("(?dum)[,]", " , ");
            line = line.replaceAll("(?dum)[`]", " ` ");
            line = line.replaceAll("(?dum)[']", " ' ");
            line = line.replaceAll("(?dum)[\"]", " \" ");

            line = line.replaceAll("(?dum)[ ]+", " ");

            line = line.replaceAll("(?dum)^[ ]+", "");
            line = line.replaceAll("(?dum)[ ]+$", "");

            if (line.equals("") && prevLine.equals("")) {
                prevLine = line;
            } else if (line.equals("") && !prevLine.equals("")) {
                ps.println(line);
                prevLine = line;
            } else if (prevLine.trim().equals("") == false) {
                Matcher m = p.matcher(line);

                if (m.find()) {
                    Matcher mBullet1 = pBullet.matcher(line);

                    if (mBullet1.find() == false) {
                        line += " ";
                        ps.print(line);
                    } else {
                        ps.println(line);
                        prevLine = line;
                    }
                }
            } else {
                ps.println(line);
                prevLine = line;
            }
        }

        inReader.close();
        ps.close();
    }

    public static void urduToRoman(String inFile, String outFile) throws FileNotFoundException, IOException {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";
        PrintStream ps = new PrintStream(outFile);

        while ((line = inReader.readLine()) != null) {
            line = line.replaceAll("(?dum)[\u200E]", " ");
            line = line.replaceAll("(?dum)[\u200D]", "");
            line = line.replaceAll("(?dum)[\u200C]", "");
            line = line.replaceAll("(?dum)[\u0603]", "");
            line = line.replaceAll("(?dum)[\u0622]", "a");
            line = line.replaceAll("(?dum)[\u0627]", "A");
            line = line.replaceAll("(?dum)[\u0628]", "b");
            line = line.replaceAll("(?dum)[\u067E]", "p");
            line = line.replaceAll("(?dum)[\u062A]", "t");
            line = line.replaceAll("(?dum)[\u0679]", "T");
            line = line.replaceAll("(?dum)[\u062B]", "X");
            line = line.replaceAll("(?dum)[\u062C]", "j");
            line = line.replaceAll("(?dum)[\u0686]", "c");
            line = line.replaceAll("(?dum)[\u062D]", "H");
            line = line.replaceAll("(?dum)[\u062E]", "K");
            line = line.replaceAll("(?dum)[\u062F]", "d");
            line = line.replaceAll("(?dum)[\u0688]", "D");
            line = line.replaceAll("(?dum)[\u0630]", "z");
            line = line.replaceAll("(?dum)[\u0631]", "r");
            line = line.replaceAll("(?dum)[\u0691]", "R");
            line = line.replaceAll("(?dum)[\u0698]", "Z");
            line = line.replaceAll("(?dum)[\u0632]", "J");
            line = line.replaceAll("(?dum)[\u0633]", "s");
            line = line.replaceAll("(?dum)[\u0634]", "S");
            line = line.replaceAll("(?dum)[\u0635]", "x");
            line = line.replaceAll("(?dum)[\u0636]", "zV");
            line = line.replaceAll("(?dum)[\u0637]", "w");
            line = line.replaceAll("(?dum)[\u0638]", "zO");
            line = line.replaceAll("(?dum)[\u0639]", "E");
            line = line.replaceAll("(?dum)[\u063A]", "G");
            line = line.replaceAll("(?dum)[\u0641]", "f");
            line = line.replaceAll("(?dum)[\u0642]", "q");
            line = line.replaceAll("(?dum)[\u06A9]", "k");
            line = line.replaceAll("(?dum)[\u06AF]", "g");
            line = line.replaceAll("(?dum)[\u0644]", "l");
            line = line.replaceAll("(?dum)[\u0645]", "m");
            line = line.replaceAll("(?dum)[\u0646]", "n");
            line = line.replaceAll("(?dum)[\u06BA]", "N");
            line = line.replaceAll("(?dum)[\u0648]", "v");
            line = line.replaceAll("(?dum)[\u0621]", "o");
            line = line.replaceAll("(?dum)[\u06CC]", "y");
            line = line.replaceAll("(?dum)[\u06D2]", "Y");
            line = line.replaceAll("(?dum)[\u0624]", "W");
            line = line.replaceAll("(?dum)[\u0626]", "i");
            line = line.replaceAll("(?dum)[\u06D3]", "e");
            line = line.replaceAll("(?dum)[\u06C1]", "h");
            line = line.replaceAll("(?dum)[\u06BE]", "Q");
            line = line.replaceAll("(?dum)[\u0650]", "L");
            line = line.replaceAll("(?dum)[\u064E]", "F");
            line = line.replaceAll("(?dum)[\u064F]", "P");
            line = line.replaceAll("(?dum)[\u0652]", "u");
            line = line.replaceAll("(?dum)[\u0651]", "U");
            line = line.replaceAll("(?dum)[\u064B]", "I");
            line = line.replaceAll("(?dum)[\u064D]", "B");
            line = line.replaceAll("(?dum)[\u064E]", "C");
            line = line.replaceAll("(?dum)[\u064F]", "1");
            line = line.replaceAll("(?dum)[\u0652]", "2");
            line = line.replaceAll("(?dum)[\u0651]", "3");
            line = line.replaceAll("(?dum)[\u064B]", "4");
            line = line.replaceAll("(?dum)[\u064D]", "5");
            line = line.replaceAll("(?dum)[\u064C]", "6");
            line = line.replaceAll("(?dum)[\u0670]", "7");
            line = line.replaceAll("(?dum)[\u0657]", "8");
            line = line.replaceAll("(?dum)[\u0656]", "9");

            line = line.replaceAll("(?dum)[\u06F0]", "0");
            line = line.replaceAll("(?dum)[\u06F1]", "1");
            line = line.replaceAll("(?dum)[\u06F2]", "2");
            line = line.replaceAll("(?dum)[\u06F3]", "3");
            line = line.replaceAll("(?dum)[\u06F4]", "4");
            line = line.replaceAll("(?dum)[\u06F5]", "5");
            line = line.replaceAll("(?dum)[\u06F6]", "6");
            line = line.replaceAll("(?dum)[\u06F7]", "7");
            line = line.replaceAll("(?dum)[\u06F8]", "8");
            line = line.replaceAll("(?dum)[\u06F9]", "9");

            line = line.replaceAll("(?dum)[\u06D4]", ".");
            line = line.replaceAll("(?dum)[\u0601]", "\'");

            ps.println(line);
        }
    }

    public static void urduToRomanBatch(String inFilePath, String outFilePath) throws FileNotFoundException, IOException {
        File inFile = new File(inFilePath);
        File outFile = new File(outFilePath);

        if (inFile.isFile() == true) {
            if ((new File(outFilePath)).isDirectory()) {
                File odir = new File(outFilePath);

                if (odir.exists() == false) {
                    odir.mkdir();
                }

                odir = new File(odir, inFile.getParentFile().getName() + "-" + inFile.getName());

                System.out.println("Converting file " + inFile.getAbsolutePath());
                urduToRoman(inFilePath, odir.getAbsolutePath());
            } else {
                System.out.println("Converting file " + inFile.getAbsolutePath());
                urduToRoman(inFilePath, outFilePath);
            }
        } else {
            if (inFile.isDirectory() == true) {
                File files[] = inFile.listFiles();

                for (int i = 0; i < files.length; i++) {
                    urduToRomanBatch(files[i].getAbsolutePath(), outFilePath);
                }
            }
        }
    }

    public static String kashmiriToRomanString(String line) {
        line = line.replaceAll("(?dum)[\u0627]", "a");
        line = line.replaceAll("(?dum)[\u0622]", "L");
        line = line.replaceAll("(?dum)[\u0628]", "b");
        line = line.replaceAll("(?dum)[\u0629]", "M");
        line = line.replaceAll("(?dum)[\u0686]", "c");
        line = line.replaceAll("(?dum)[\u062B]", "J");
        line = line.replaceAll("(?dum)[\u062F]", "d");
        line = line.replaceAll("(?dum)[\u0688]", "D");
        line = line.replaceAll("(?dum)[\u0639]", "e");
        line = line.replaceAll("(?dum)[\u0641]", "F");
        line = line.replaceAll("(?dum)[\u06AF]", "g");
        line = line.replaceAll("(?dum)[\u063A]", "G");
        line = line.replaceAll("(?dum)[\u06BE]", "o");
        line = line.replaceAll("(?dum)[\u062D]", "h");
        line = line.replaceAll("(?dum)[\u06CC]", "i");
        line = line.replaceAll("(?dum)[\u0656]", "u");
        line = line.replaceAll("(?dum)[\u062C]", "j");
        line = line.replaceAll("(?dum)[\u0636]", "B");
        line = line.replaceAll("(?dum)[\u0643]", "q");
        line = line.replaceAll("(?dum)[\u062E]", "H");
        line = line.replaceAll("(?dum)[\u0644]", "l");
        line = line.replaceAll("(?dum)[\u065B]", "K");
        line = line.replaceAll("(?dum)[\u0645]", "m");
        line = line.replaceAll("(?dum)[\u0658]", "k");
        line = line.replaceAll("(?dum)[\u0646]", "n");
        line = line.replaceAll("(?dum)[\u06BA]", "N");
        line = line.replaceAll("(?dum)[\u06C1]", "O");
        line = line.replaceAll("(?dum)[\u06AE]", "E");
        line = line.replaceAll("(?dum)[\u067E]", "f");
        line = line.replaceAll("(?dum)[\u064B]", "W");
        line = line.replaceAll("(?dum)[\u0642]", "Q");
        line = line.replaceAll("(?dum)[\u064D]", "w");
        line = line.replaceAll("(?dum)[\u0631]", "r");
        line = line.replaceAll("(?dum)[\u0691]", "R");
        line = line.replaceAll("(?dum)[\u0633]", "s");
        line = line.replaceAll("(?dum)[\u0635]", "A");
        line = line.replaceAll("(?dum)[\u062A]", "t");
        line = line.replaceAll("(?dum)[\u0679]", "T");
        line = line.replaceAll("(?dum)[\u0648]", "v");
        line = line.replaceAll("(?dum)[\u06C4]", "V");
        line = line.replaceAll("(?dum)[\u0634]", "S");
        line = line.replaceAll("(?dum)[\u0698]", "C");
        line = line.replaceAll("(?dum)[\u06D2]", "I");
        line = line.replaceAll("(?dum)[\u06ED]", "x");
        line = line.replaceAll("(?dum)[\u0632]", "Z");
        line = line.replaceAll("(?dum)[\u0621]", "u");
        line = line.replaceAll("(?dum)[\u064F]", "p");
        line = line.replaceAll("(?dum)[\u0657]", "P");
        line = line.replaceAll("(?dum)[\u0650]", "y");
        line = line.replaceAll("(?dum)[\u064E]", "Y");
        line = line.replaceAll("(?dum)[\u0640]", ".");
        line = line.replaceAll("(?dum)[\u0672]", "X");

        line = line.replaceAll("(?dum)[\u06F0]", "0");
        line = line.replaceAll("(?dum)[\u06F1]", "1");
        line = line.replaceAll("(?dum)[\u06F2]", "2");
        line = line.replaceAll("(?dum)[\u06F3]", "3");
        line = line.replaceAll("(?dum)[\u06F4]", "4");
        line = line.replaceAll("(?dum)[\u06F5]", "5");
        line = line.replaceAll("(?dum)[\u06F6]", "6");
        line = line.replaceAll("(?dum)[\u06F7]", "7");
        line = line.replaceAll("(?dum)[\u06F8]", "8");
        line = line.replaceAll("(?dum)[\u06F9]", "9");
        line = line.replaceAll("(?dum)[\u0601]", "\'");
        line = line.replaceAll("(?dum)[\u0654]", "zD");
        line = line.replaceAll("(?dum)[\u0630]", "zA");
        line = line.replaceAll("(?dum)[\u0637]", "zB");
        line = line.replaceAll("(?dum)[\u0638]", "zC");
        line = line.replaceAll("(?dum)[\u0655]", "zE");

        line = line.replaceAll("(?dum)[\u0655]", "zE");

        line = line.replaceAll("(?dum)[\u06D4]", ".");
        line = line.replaceAll("(?dum)[\u060C]", ",");
        line = line.replaceAll("(?dum)[\u061B]", ";");
        line = line.replaceAll("(?dum)[\u061F]", "?");

        return line;
    }

    public static void kashmiriToRoman(String inFile, String outFile) throws FileNotFoundException, IOException {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";
        PrintStream ps = new PrintStream(outFile);

        while ((line = inReader.readLine()) != null) {
            line = kashmiriToRomanString(line);
            ps.println(line);
        }
    }

    public static void kashmiriToRomanBatch(String inFilePath, String outFilePath) throws FileNotFoundException, IOException {
        File inFile = new File(inFilePath);
        File outFile = new File(outFilePath);

        if (inFile.isFile() == true) {
            if ((new File(outFilePath)).isDirectory()) {
                File odir = new File(outFilePath);

                if (odir.exists() == false) {
                    odir.mkdir();
                }

                odir = new File(odir, inFile.getParentFile().getName() + "-" + inFile.getName());

                System.out.println("Converting file " + inFile.getAbsolutePath());
                kashmiriToRoman(inFilePath, odir.getAbsolutePath());
            } else {
                System.out.println("Converting file " + inFile.getAbsolutePath());
                kashmiriToRoman(inFilePath, outFilePath);
            }
        } else {
            if (inFile.isDirectory() == true) {
                File files[] = inFile.listFiles();

                for (int i = 0; i < files.length; i++) {
                    kashmiriToRomanBatch(files[i].getAbsolutePath(), outFilePath);
                }
            }
        }
    }

    public static String romanToKashmiriString(String line) {
        line = line.replaceAll("(?dum)[z][D]", "\u0654");
        line = line.replaceAll("(?dum)[z][E]", "\u0655");
        line = line.replaceAll("(?dum)[z][A]", "\u0630");
        line = line.replaceAll("(?dum)[z][B]", "\u0637");
        line = line.replaceAll("(?dum)[z][C]", "\u0638");

        line = line.replaceAll("(?dum)[a]", "\u0627");
        line = line.replaceAll("(?dum)[b]", "\u0628");
        line = line.replaceAll("(?dum)[M]", "\u0629");
        line = line.replaceAll("(?dum)[c]", "\u0686");
        line = line.replaceAll("(?dum)[J]", "\u062B");
        line = line.replaceAll("(?dum)[d]", "\u062F");
        line = line.replaceAll("(?dum)[D]", "\u0688");
        line = line.replaceAll("(?dum)[e]", "\u0639");

        line = line.replaceAll("(?dum)[F]", "\u0641");

        line = line.replaceAll("(?dum)[g]", "\u06AF");
        line = line.replaceAll("(?dum)[G]", "\u063A");
        line = line.replaceAll("(?dum)[o]", "\u06BE");
        line = line.replaceAll("(?dum)[h]", "\u062D");
        line = line.replaceAll("(?dum)[i]", "\u06CC");
        line = line.replaceAll("(?dum)[u]", "\u0656");
        line = line.replaceAll("(?dum)[j]", "\u062C");
        line = line.replaceAll("(?dum)[B]", "\u0636");
        line = line.replaceAll("(?dum)[q]", "\u0643");
        line = line.replaceAll("(?dum)[H]", "\u062E");
        line = line.replaceAll("(?dum)[l]", "\u0644");
        line = line.replaceAll("(?dum)[L]", "\u0622");
        line = line.replaceAll("(?dum)[K]", "\u065B");
        line = line.replaceAll("(?dum)[m]", "\u0645");
        line = line.replaceAll("(?dum)[k]", "\u0658");
        line = line.replaceAll("(?dum)[n]", "\u0646");
        line = line.replaceAll("(?dum)[N]", "\u06BA");
        line = line.replaceAll("(?dum)[O]", "\u06C1");
        line = line.replaceAll("(?dum)[E]", "\u06AE");
        line = line.replaceAll("(?dum)[f]", "\u067E");
        line = line.replaceAll("(?dum)[W]", "\u064B");
        line = line.replaceAll("(?dum)[Q]", "\u0642");
        line = line.replaceAll("(?dum)[w]", "\u064D");
        line = line.replaceAll("(?dum)[r]", "\u0631");
        line = line.replaceAll("(?dum)[R]", "\u0691");
        line = line.replaceAll("(?dum)[s]", "\u0633");
        line = line.replaceAll("(?dum)[A]", "\u0635");
        line = line.replaceAll("(?dum)[t]", "\u062A");
        line = line.replaceAll("(?dum)[T]", "\u0679");
        line = line.replaceAll("(?dum)[v]", "\u0648");
        line = line.replaceAll("(?dum)[V]", "\u06C4");
        line = line.replaceAll("(?dum)[S]", "\u0634");
        line = line.replaceAll("(?dum)[C]", "\u0698");
        line = line.replaceAll("(?dum)[I]", "\u06D2");
        line = line.replaceAll("(?dum)[x]", "\u06ED");
        line = line.replaceAll("(?dum)[Z]", "\u0632");
        line = line.replaceAll("(?dum)[u]", "\u0621");
        line = line.replaceAll("(?dum)[p]", "\u064F");
        line = line.replaceAll("(?dum)[P]", "\u0657");
        line = line.replaceAll("(?dum)[y]", "\u0650");
        line = line.replaceAll("(?dum)[Y]", "\u064E");
        line = line.replaceAll("(?dum)[X]", "\u0672");
//            line = line.replaceAll("(?dum)[?]", "\u061F");
//            line = line.replaceAll("(?dum)[,]", "\u062C");

        line = line.replaceAll("(?dum)[0]", "\u06F0");
        line = line.replaceAll("(?dum)[1]", "\u06F1");
        line = line.replaceAll("(?dum)[2]", "\u06F2");
        line = line.replaceAll("(?dum)[3]", "\u06F3");
        line = line.replaceAll("(?dum)[4]", "\u06F4");
        line = line.replaceAll("(?dum)[5]", "\u06F5");
        line = line.replaceAll("(?dum)[6]", "\u06F6");
        line = line.replaceAll("(?dum)[7]", "\u06F7");
        line = line.replaceAll("(?dum)[8]", "\u06F8");
        line = line.replaceAll("(?dum)[9]", "\u06F9");

        line = line.replaceAll("(?dum)[.]", "\u06D4");
        line = line.replaceAll("(?dum)[,]", "\u060C");
        line = line.replaceAll("(?dum)[;]", "\u061B");
        line = line.replaceAll("(?dum)[?]", "\u061F");

        return line;
    }

    public static void romanToKashmiri(String inFile, String outFile) throws FileNotFoundException, IOException {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";
        PrintStream ps = new PrintStream(outFile);

        while ((line = inReader.readLine()) != null) {
            line = romanToKashmiriString(line);
            ps.println(line);
        }
    }

    public static void romanToKashmiriBatch(String inFilePath, String outFilePath) throws FileNotFoundException, IOException {
        File inFile = new File(inFilePath);
        File outFile = new File(outFilePath);

        if (inFile.isFile() == true) {
            if ((new File(outFilePath)).isDirectory()) {
                File odir = new File(outFilePath);

                if (odir.exists() == false) {
                    odir.mkdir();
                }

                odir = new File(odir, inFile.getParentFile().getName() + "-" + inFile.getName());

                System.out.println("Converting file " + inFile.getAbsolutePath());
                romanToKashmiri(inFilePath, odir.getAbsolutePath());
            } else {
                System.out.println("Converting file " + inFile.getAbsolutePath());
                romanToKashmiri(inFilePath, outFilePath);
            }
        } else {
            if (inFile.isDirectory() == true) {
                File files[] = inFile.listFiles();

                for (int i = 0; i < files.length; i++) {
                    romanToKashmiriBatch(files[i].getAbsolutePath(), outFilePath);
                }
            }
        }
    }

    public static void santaliToDevanagari(String inFile, String outFile) throws FileNotFoundException, IOException {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";
        PrintStream ps = new PrintStream(outFile);

        while ((line = inReader.readLine()) != null) {
            line = line.replaceAll("(?dum)[A]", "अ");
            line = line.replaceAll("(?dum)[B]", "ब");
            line = line.replaceAll("(?dum)[C]", "च");
            line = line.replaceAll("(?dum)[D]", "ठ");
            line = line.replaceAll("(?dum)[E]", "�?");
            line = line.replaceAll("(?dum)[F]", "ङ");
            line = line.replaceAll("(?dum)[G]", "ग");
            line = line.replaceAll("(?dum)[H]", "ख");
            line = line.replaceAll("(?dum)[I]", "इ");
            line = line.replaceAll("(?dum)[J]", "ज");
            line = line.replaceAll("(?dum)[K]", "क");
            line = line.replaceAll("(?dum)[L]", "ल");
            line = line.replaceAll("(?dum)[M]", "ण");
            line = line.replaceAll("(?dum)[N]", "अं");
            line = line.replaceAll("(?dum)[O]", "ओ");
            line = line.replaceAll("(?dum)[P]", "प");
            line = line.replaceAll("(?dum)[Q]", "ञ");
            line = line.replaceAll("(?dum)[R]", "र");
            line = line.replaceAll("(?dum)[S]", "स");
            line = line.replaceAll("(?dum)[T]", "त");
            line = line.replaceAll("(?dum)[U]", "ऊ");
            line = line.replaceAll("(?dum)[V]", "व");
            line = line.replaceAll("(?dum)[W]", "व");
            line = line.replaceAll("(?dum)[X]", "");
            line = line.replaceAll("(?dum)[Y]", "य");
            line = line.replaceAll("(?dum)[Z]", "ड");
            line = line.replaceAll("(?dum)[a]", "औ");
            line = line.replaceAll("(?dum)[b]", "ब");
            line = line.replaceAll("(?dum)[c]", "च");
            line = line.replaceAll("(?dum)[d]", "ड");
            line = line.replaceAll("(?dum)[e]", "�?");
            line = line.replaceAll("(?dum)[f]", "ङ");
            line = line.replaceAll("(?dum)[g]", "ग");
            line = line.replaceAll("(?dum)[h]", "ह");
            line = line.replaceAll("(?dum)[i]", "इ");
            line = line.replaceAll("(?dum)[j]", "ज");
            line = line.replaceAll("(?dum)[k]", "क");
            line = line.replaceAll("(?dum)[l]", "ल");
            line = line.replaceAll("(?dum)[m]", "म");
            line = line.replaceAll("(?dum)[n]", "न");
            line = line.replaceAll("(?dum)[o]", "आ");
            line = line.replaceAll("(?dum)[p]", "प");
            line = line.replaceAll("(?dum)[q]", "ञ");
            line = line.replaceAll("(?dum)[r]", "र");
            line = line.replaceAll("(?dum)[s]", "स");
            line = line.replaceAll("(?dum)[t]", "ट");
            line = line.replaceAll("(?dum)[u]", "ऊ");
            line = line.replaceAll("(?dum)[v]", "व");
            line = line.replaceAll("(?dum)[w]", "व");
            line = line.replaceAll("(?dum)[x]", "");
            line = line.replaceAll("(?dum)[y]", "य");
            line = line.replaceAll("(?dum)[z]", "ड");

            line = line.replaceAll("(?dum)[0]", "०");
            line = line.replaceAll("(?dum)[1]", "१");
            line = line.replaceAll("(?dum)[2]", "२");
            line = line.replaceAll("(?dum)[3]", "३");
            line = line.replaceAll("(?dum)[4]", "४");
            line = line.replaceAll("(?dum)[5]", "५");
            line = line.replaceAll("(?dum)[6]", "६");
            line = line.replaceAll("(?dum)[7]", "७");
            line = line.replaceAll("(?dum)[8]", "८");
            line = line.replaceAll("(?dum)[9]", "९");
            ps.println(line);
        }
    }

    public static void konkaniRomanToDevanagari(String inFile, String outFile) throws FileNotFoundException, IOException {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";
        PrintStream ps = new PrintStream(outFile);

        while ((line = inReader.readLine()) != null) {
            line = line.replaceAll("(?dum)[chh]", "च");
            line = line.replaceAll("(?dum)[tth]", "ठ");
            line = line.replaceAll("(?dum)[ddh]", "ढ");
            line = line.replaceAll("(?dum)[ie]", "य");

            line = line.replaceAll("(?dum)[o]", "अ");
            line = line.replaceAll("(?dum)[a]", "आ");
            line = line.replaceAll("(?dum)[i]", "इ");
            line = line.replaceAll("(?dum)[i]", "ई");
            line = line.replaceAll("(?dum)[u]", "उ");
            line = line.replaceAll("(?dum)[u]", "ऊ");
            line = line.replaceAll("(?dum)[e]", "�?");
            line = line.replaceAll("(?dum)[e]", "�?");
            line = line.replaceAll("(?dum)[e]", "�?");
            line = line.replaceAll("(?dum)[ai]", "�?");
            line = line.replaceAll("(?dum)[oi]", "�?");
            line = line.replaceAll("(?dum)[o]", "ओ");
            line = line.replaceAll("(?dum)[o]", "ऑ");
            line = line.replaceAll("(?dum)[au]", "औ");
            line = line.replaceAll("(?dum)[om]", "अं");
            line = line.replaceAll("(?dum)[on]", "अं");
            line = line.replaceAll("(?dum)[k]", "क");
            line = line.replaceAll("(?dum)[kh]", "ख");
            line = line.replaceAll("(?dum)[g]", "ग");
            line = line.replaceAll("(?dum)[gh]", "घ");
            line = line.replaceAll("(?dum)[ng]", "ङ");
            line = line.replaceAll("(?dum)[ch]", "च़");
            line = line.replaceAll("(?dum)[z]", "ज़");
            line = line.replaceAll("(?dum)[j]", "ज");
            line = line.replaceAll("(?dum)[zh]", "�?़");
            line = line.replaceAll("(?dum)[jh]", "�?");
            line = line.replaceAll("(?dum)[nh]", "ञ");
            line = line.replaceAll("(?dum)[tt]", "ट");
            line = line.replaceAll("(?dum)[dd]", "ड");
            line = line.replaceAll("(?dum)[nn]", "ण");
            line = line.replaceAll("(?dum)[t]", "त");
            line = line.replaceAll("(?dum)[th]", "थ");
            line = line.replaceAll("(?dum)[d]", "द");
            line = line.replaceAll("(?dum)[dh]", "ध");
            line = line.replaceAll("(?dum)[n]", "न");
            line = line.replaceAll("(?dum)[p]", "प");
            line = line.replaceAll("(?dum)[f]", "फ");
            line = line.replaceAll("(?dum)[b]", "ब");
            line = line.replaceAll("(?dum)[bh]", "भ");
            line = line.replaceAll("(?dum)[m]", "म");
            line = line.replaceAll("(?dum)[r]", "र");
            line = line.replaceAll("(?dum)[l]", "ल");
            line = line.replaceAll("(?dum)[x]", "श");
            line = line.replaceAll("(?dum)[x]", "ष");
            line = line.replaceAll("(?dum)[s]", "स");
            line = line.replaceAll("(?dum)[h]", "ह");
            line = line.replaceAll("(?dum)[ll]", "ळ");
            line = line.replaceAll("(?dum)[v]", "व");
            line = line.replaceAll("(?dum)[0]", "०");
            line = line.replaceAll("(?dum)[1]", "१");
            line = line.replaceAll("(?dum)[2]", "२");
            line = line.replaceAll("(?dum)[3]", "३");
            line = line.replaceAll("(?dum)[4]", "४");
            line = line.replaceAll("(?dum)[5]", "५");
            line = line.replaceAll("(?dum)[6]", "६");
            line = line.replaceAll("(?dum)[7]", "७");
            line = line.replaceAll("(?dum)[8]", "८");
            line = line.replaceAll("(?dum)[9]", "९");
            ps.println(line);
        }
    }

    public static void centre(Component c) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle r = c.getBounds();

        int x = (screenSize.width - r.width) / 2;
        int y = (screenSize.height - r.height) / 2;

        c.setBounds(x, y, r.width, r.height);
    }

    public static void setVisibleRowCount(JTable table, int rows) {
        int height = 0;
        for (int row = 0; row < rows; row++)
            height += table.getRowHeight(row);

        table.setPreferredScrollableViewportSize(new Dimension(
                table.getPreferredScrollableViewportSize().width,
                height
        ));
    }

    public static void printCharsetChars(int from, int to, String textFile, String charset, boolean addNumbers, String separator) throws FileNotFoundException, IOException {
        PrintStream ps = new PrintStream(textFile, charset);

        for (int i = from; i <= to; i++) {
            if (addNumbers) {
                if (separator == null || separator.equals(""))
                    ps.println(i + "\t" + (char) i);
                else
                    ps.println(i + separator + (char) i);
            } else
                ps.println((char) i);
        }
    }

    public static void addLineNumbers(int from, String inFile, String outFile, String charset, String separator) throws FileNotFoundException, IOException {
        BufferedReader inReader = null;
        PrintStream ps = new PrintStream(outFile, charset);

        if (charset != null && charset.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), charset));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";

        int i = from;

        while ((line = inReader.readLine()) != null) {
            line = i++ + separator + line;

            ps.println(line);
        }
    }

    public static boolean convertCharset(String fromCharset, String toCharset, String inFile, String outFile, boolean extractHTML) throws FileNotFoundException, IOException {

        BufferedReader inReader = null;

        PrintStream ps = null;

        String lines[] = getTextLinesFromFile(inFile, fromCharset);
//        String outLines[] = new String[lines.length];

        String text = "";

        for (int i = 0; i < lines.length; i++)
            text += lines[i] + "\n";

        if (extractHTML) {
            String tagBegin = "<head>";
            String tagEnd = "</head>";

            while (text.contains(tagBegin)) {
                int tagBeginIndex = text.indexOf(tagBegin);
                int tagEndIndex = text.indexOf(tagEnd);

                if (tagBeginIndex != -1 && tagEndIndex != -1)
                    text = text.substring(0, tagBeginIndex) + text.substring(tagEndIndex + tagEnd.length(), text.length());
            }

            tagBegin = "<style>";
            tagEnd = "</style>";


            int i = 0;
            int enough = 100;

            while (i < enough && text.contains(tagBegin) && text.contains(tagEnd)) {
                int tagBeginIndex = text.indexOf(tagBegin);
                int tagEndIndex = text.indexOf(tagEnd);

                if (tagBeginIndex != -1 && tagEndIndex != -1 && tagEndIndex > tagBeginIndex)
                    text = text.substring(0, tagBeginIndex) + text.substring(tagEndIndex + tagEnd.length(), text.length());

                i++;
            }

            text = text.replaceAll("(?dum)<[^<^>]+>", "");
            text = text.replaceAll("(?dum)[ ]+[\n]", "\n");
            text = text.replaceAll("(?dum)[\n][ ]+", "\n");
            text = text.replaceAll("(?dum)[\n]{3,}+", "\n\n");

            text = text.trim();
            text = HTMLUtil.convertCharacterEntities(text);

            if (toCharset == null)
                ps = new PrintStream(outFile);
            else
                ps = new PrintStream(outFile, toCharset);

            ps.println(text);

            ps.close();
        }

        if (extractHTML == false) {
            if (toCharset == null)
                ps = new PrintStream(outFile);
            else
                ps = new PrintStream(outFile, toCharset);

            ps.println(text);

            ps.close();
        }

//        for (int i = 0; i < lines.length; i++)
//        {
//            if(extractHTML)
//                outLines[i] = lines[i].replaceAll("(?dum)<[^<^>]+>", "");
//            else
//                ps.println(lines[i]);
//        }
//
//        if(extractHTML)
//        {
//            for (int i = 0; i < lines.length; i++)
//                outLines[i] = outLines[i].replaceAll("(?dum)<![^<^>]+>", "");
//
//            for (int i = 0; i < lines.length; i++)
//            {
//                outLines[i] = outLines[i].trim();
//                outLines[i] = HTMLUtil.convertCharacterEntities(outLines[i]);
//
//                ps.println(outLines[i]);
//            }
//        }

//        if(extractHTML == false)
//        {
//            if(fromCharset != null && fromCharset.equals("") == false)
//                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), fromCharset));
//            else
//                inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
//
//            String line = "";
//
//            System.out.println("Writing file " + inFile);
//
//            while((line = inReader.readLine()) != null) {
//    //            String toLine = new String(line.getBytes(), toCharset);
//    //            System.out.println(toLine);
//
//                if(extractHTML)
//                {
//                    line = line.replaceAll("(?dum)<[^<^>]+>", "");
//                    line = line.replaceAll("(?dum)<![^<^>]+>", "");
//
//                    line = line.trim();
//
//                    line = HTMLUtil.convertCharacterEntities(line);
//                }
//
//                ps.println(line);
//            }
//
//            inReader.close();
//        }

//        if(extractHTML)
//        {
//            org.htmlparser.Parser parser = null;
//
//            try
//            {
//                parser = new org.htmlparser.Parser(inFile);
//                NodeFilter textTagFilter = new AndFilter(new NodeClassFilter(TextNode.class),
//                        new NotFilter(new HasParentFilter(new TagNameFilter("style"))));
//
//                NodeList textElements = null;
//
//                try
//                {
//                    textElements = parser.extractAllNodesThatMatch(textTagFilter);
//                }
//                catch(org.htmlparser.util.EncodingChangeException e)
//                {
//                    e.printStackTrace();
//                    return false;
//                }
//                catch(Exception e)
//                {
//                    e.printStackTrace();
//                    return false;
//                }
//
//                if(toCharset == null)
//                    ps = new PrintStream(outFile);
//                else
//                    ps = new PrintStream(outFile, toCharset);
//
//                int pcount = textElements.size();
//
//                boolean prevEmpty = false;
//
//                for(int i = 0; i < pcount; i++)
//                {
//                    org.htmlparser.Node textElement = (org.htmlparser.Node) textElements.elementAt(i);
//                    text = textElement.getText();
//
//                    text = HTMLUtil.convertCharacterEntities(text);
//
//                    text = text.trim();
//
//                    boolean thisEmpty = text.equals("");
//
//                    if(!(prevEmpty && thisEmpty))
//                       ps.println(text);
//
//                    prevEmpty = thisEmpty;
//                }
//
//                ps.close();
//            } catch (ParserException ex)
//            {
//                Logger.getLogger(UtilityFunctions.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        Runtime.getRuntime().gc();

        return true;
    }

    public static Vector convertCharsetBatch(String fromCharset, String toCharset, String inFilePath, String outFilePath, boolean extractHTML) throws FileNotFoundException, IOException {

        Vector problematicFile = new Vector(0, 100);

        File inFile = new File(inFilePath);
        File outFile = new File(outFilePath);

        if (inFile.isFile() == true) {
            File odir = new File(outFilePath);

            if (odir.isDirectory() || odir.exists() == false) {
                if (odir.exists() == false) {
                    odir.mkdirs();
                }

                odir = new File(odir, inFile.getParentFile().getName() + "-" + inFile.getName() + ".txt");

//                System.out.println("Converting file " + inFile.getAbsolutePath());

                boolean converted = convertCharset(fromCharset, toCharset, inFilePath, odir.getAbsolutePath(), extractHTML);

                if (converted == false)
                    problematicFile.add(inFilePath);
            } else {
//                System.out.println("Converting file " + inFile.getAbsolutePath());
                boolean converted = convertCharset(fromCharset, toCharset, inFilePath, outFilePath, extractHTML);

                if (converted == false)
                    problematicFile.add(inFilePath);
            }
        } else {
            if (inFile.isDirectory() == true) {
                File files[] = null;

                if (extractHTML) {
                    java.io.FileFilter filter = new FileFilterImpl(new String[]{GlobalProperties.getIntlString("htm"), GlobalProperties.getIntlString("html"), GlobalProperties.getIntlString("xhtml"), GlobalProperties.getIntlString("xml"), GlobalProperties.getIntlString("txt")}, GlobalProperties.getIntlString("Web_pages"));
                    files = inFile.listFiles(filter);
                } else
                    files = inFile.listFiles();

                for (int i = 0; i < files.length; i++) {
                    problematicFile.addAll(convertCharsetBatch(fromCharset, toCharset, files[i].getAbsolutePath(), outFilePath, extractHTML));
                }
            }
        }

        return problematicFile;
    }

    public static String reverseString(String str) {
        char rev[] = new char[str.length()];

        for (int i = rev.length - 1; i >= 0; i--)
            rev[i] = str.charAt(str.length() - i - 1);

        String revStr = new String(rev);

        return revStr;
    }

    public static boolean flagOn(long flags, long flag) {
        return ((flags & flag) == flag ? true : false);
    }

    public static long switchOnFlags(long flags, long flag) {
        return (flags | flag);
    }

    public static long switchOffFlags(long flags, long flag) {
        return (flags & (~flag));
    }

    public static String[] getUniqueStrings(String[] strings) {
        int ucount = 0;

        Arrays.sort(strings);

        for (int i = 0; i < strings.length; i++) {
            if (i == 0 || strings[i].equals(strings[i - 1]) == false)
                ucount++;
        }

        String uniqueStrings[] = new String[ucount];

        return uniqueStrings;
    }

    public static int getCurrentLine(JTextArea textArea) {
        int cpos = textArea.getCaretPosition();
        int line = 0;

        try {
            line = textArea.getLineOfOffset(cpos);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        return line;
    }

    public static void main(String[] args) {
//	UtilityFunctions uf = new UtilityFunctions();
//	String ifile = "/home/anil/tmp/401.utf8";
//	String tfile = "/home/anil/tmp/401.utf8.tmp";
//	String ofile = "/home/anil/tmp/401.utf8.preprocessed";

        try {
//	    uf.naiivePreprocessing(ifile, "UTF-8", tfile, "UTF-8", "hin::utf8");
//	    uf.trimSpaces(tfile, "UTF-8", ofile, "UTF-8");

//	    UtilityFunctions.printCharsetChars(161, 254, "/home/anil/tmp/iscii-chars.txt", "ISO-8859-1", true, "\t");

            UtilityFunctions.appendWordTypes("/home/anil/ftp/anil/ltrc/resources/stable/ciil-corpus/raw/Telugu/raw-ver-0.1/telugu-raw-ver-0.1-utf8",
                    GlobalProperties.getIntlString("UTF-8"),
                    "/home/anil/myproj/sanchay/eclipse/Sanchay/props/spell-checker/telugu-word-types-ciil.txt",
                    GlobalProperties.getIntlString("UTF-8"));

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println(UtilityFunctions.reverseString(GlobalProperties.getIntlString("reverse")));
    }

    public static void increaseFontSize(Component c) {
        Font font = c.getFont();
        Font newFont = font.deriveFont(font.getStyle(), font.getSize() + 1);
        c.setFont(newFont);
    }

    public static void decreaseFontSize(Component c) {
        Font font = c.getFont();
        Font newFont = font.deriveFont(font.getStyle(), font.getSize() - 1);
        c.setFont(newFont);
    }

    public static void increaseFontSize(Component c, int pt) {
        Font font = c.getFont();
        Font newFont = font.deriveFont(font.getStyle(), font.getSize() + pt);
        c.setFont(newFont);
    }

    public static void decreaseFontSize(Component c, int pt) {
        Font font = c.getFont();
        Font newFont = font.deriveFont(font.getStyle(), font.getSize() - pt);
        c.setFont(newFont);
    }

    public static void replaceInFile(String replace, String with, File inFile, File outFile, String cs) throws FileNotFoundException, IOException {
        BufferedReader inReader = null;
        PrintStream ps = new PrintStream(outFile, cs);

        if (cs != null && cs.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), cs));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));

        String line = "";

        System.out.println(GlobalProperties.getIntlString("Writing_file_") + outFile);

        while ((line = inReader.readLine()) != null) {

            line = line.replaceAll(replace, with);

            ps.println(line);
        }

        inReader.close();
        ps.close();
    }

    public static void replaceInFileInPlace(String replace, String with, File inFile, String cs) throws FileNotFoundException, IOException {
        BufferedReader inReader = null;

        File outFile = new File(inFile.getAbsolutePath() + ".tmp.tmp");

        replaceInFile(replace, with, inFile, outFile, cs);
        inFile.delete();

        copyFile(outFile, inFile);
        outFile.delete();
    }

//    public static SanchayTableModel getTextStatsBatch(File inFile, String cs, SanchayTableModel stats) throws FileNotFoundException, IOException
//    {
//        SanchayTableModel allStatTable = new SanchayTableModel(3, 2);
//        
//        if(inFile.isFile() == true)
//        {
//            if(stats == null)
//                allStatTable = getTextStats(inFile, cs);
//            else
//            {
//                int charCount = Long.parseLong((String) statTable.getValueAt(0, 1));
//                int wordCount = Long.parseLong((String) statTable.getValueAt(1, 1));
//                int lineCount = Long.parseLong((String) statTable.getValueAt(2, 1));
//
//                allStatTable.setValueAt(Long.toString((long) charCount), 0, 1);
//                allStatTable.setValueAt(Long.toString((long) wordCount), 1, 1);
//                allStatTable.setValueAt(Long.toString((long) lineCount), 2, 1);
//            }
//        }
//        else
//        {
//            if(inFile.isDirectory() == true)
//            {
//                File files[] = inFile.listFiles();
//
//                int charCount = 0;
//                int wordCount = 0;
//                int lineCount = 0;
//                
//                allStatTable = new SanchayTableModel(3, 2);
//                
//                for(int i = 0; i < files.length; i++)
//                {
//                    SanchayTableModel statTable = getTextStatsBatch(files[i], cs);
//
//                    charCount += Long.parseLong((String) statTable.getValueAt(0, 1));
//                    wordCount += Long.parseLong((String) statTable.getValueAt(1, 1));
//                    lineCount += Long.parseLong((String) statTable.getValueAt(2, 1));
//                }
//                    
//                allStatTable.setValueAt(Long.toString((long) charCount), 0, 1);
//                allStatTable.setValueAt(Long.toString((long) wordCount), 1, 1);
//                allStatTable.setValueAt(Long.toString((long) lineCount), 2, 1);
//            }
//        }
//        
//        return allStatTable;
//    }
//   
//    public static SanchayTableModel getTextStatsBatch(File[] files, String cs) throws FileNotFoundException, IOException
//    {
//        String lines[] = getTextLinesFromFile(file.getAbsolutePath(), cs);
//        
////        String lines[] = txt.split("[\\n]");
//        
//        long charCount = 0;
//        long wordCount = 0;
//        long lineCount = lines.length;
//
//        for (int i = 0; i < lineCount; i++)
//        {
//            charCount += lines[0].length();
//
//            String tokenizedString = lines[0];
//            //String tokenizedString = UtilityFunctions.naiiveTokenization();
//
//            String wds[] = tokenizedString.split("[ ]");
//
//            wordCount += wds.length;
//        }
//        
//        SanchayTableModel statTable = new SanchayTableModel(3, 2);
//        
//        statTable.setValueAt("Character count", 0, 0);
//        statTable.setValueAt(Long.toString((long) charCount), 0, 1);
//        statTable.setValueAt("Word count", 1, 0);
//        statTable.setValueAt(Long.toString((long) wordCount), 1, 1);
//        statTable.setValueAt("Line count", 2, 0);
//        statTable.setValueAt(Long.toString((long) lineCount), 2, 1);
//        
//        return statTable;
//    }

    public static SanchayTableModel getTextStatsInFiles(File files[], String cs) throws FileNotFoundException, IOException {
        SanchayTableModel matches = null;

//        int sentences = 0;
//        int words = 0;
//        int characters = 0;
//
//        if(files != null && files.length > 1)
//        {
//            int ccount = 0;
//            
//            for (int i = 0; i < files.length; i++)
//            {
//                File file = (File) files[i];
//    
//                if(i == 0)
//                {
//                    matches = getTextStats(file, cs);
//
//                    matches.addColumn("File");
//
//                    int rcount = matches.getRowCount();
//                    ccount = matches.getColumnCount();
//                    
//                    for (int j = 0; j < rcount; j++)
//                    {
//                        matches.setValueAt(file.getName(), j, ccount - 1);
//                    }
//                    
//                    sentences = matches.getValueAt();
//                    chunks = story.countChunks();
//                    words = story.countWords();
//                    characters = story.countcharacters();
//                }
//                else
//                {
//                    SanchayTableModel fileMatches = getStats(story);
//
//                    int rcount = matches.getRowCount();
//                    int frcount = fileMatches.getRowCount();
//                    
//                    for (int j = 0; j < frcount; j++)
//                    {
//                        Vector rowData = fileMatches.getRow(j);
//                        rowData.add("");
//                        
//                        matches.addRow(rowData);
//                        matches.setValueAt(file.getName(), rcount + j, ccount - 1);
//                    }
//                    
//                    sentences += story.countSentences();
//                    chunks += story.countChunks();
//                    words += story.countWords();
//                    characters += story.countcharacters();
//
//                    matches.insertRow(rcount);
//
//                    matches.setValueAt(Integer.toString(sentences), 0, 0);
//                    matches.setValueAt(Integer.toString(chunks), 0, 1);
//                    matches.setValueAt(Integer.toString(words), 0, 2);
//                    matches.setValueAt(Integer.toString(characters), 0, 3);
//                    matches.setValueAt("Total (All Files)", 0, 4);
//                }
//            }
//            
//            matches.insertRow(0);            
//        }
//        else
//        {
//            matches = getStats(ssfStory);
//        }
//        
//        showSearchResults(matches, langEnc);

        return matches;
    }

    public static SanchayTableModel getTextStats(File file, String cs) throws FileNotFoundException, IOException {
        String lines[] = getTextLinesFromFile(file.getAbsolutePath(), cs);

//        String lines[] = txt.split("[\\n]");

        long charCount = 0;
        long wordCount = 0;
        long lineCount = lines.length;

        for (int i = 0; i < lineCount; i++) {
            charCount += lines[0].length();

            String tokenizedString = lines[0];
            //String tokenizedString = UtilityFunctions.naiiveTokenization();

            String wds[] = tokenizedString.split("[ ]");

            wordCount += wds.length;
        }

        SanchayTableModel statTable = new SanchayTableModel(3, 2);

        statTable.setValueAt(GlobalProperties.getIntlString("Characters"), 0, 0);
        statTable.setValueAt(Long.toString((long) charCount), 0, 1);
        statTable.setValueAt(GlobalProperties.getIntlString("Words"), 1, 0);
        statTable.setValueAt(Long.toString((long) wordCount), 1, 1);
        statTable.setValueAt(GlobalProperties.getIntlString("Lines"), 2, 0);
        statTable.setValueAt(Long.toString((long) lineCount), 2, 1);

        return statTable;
    }

    public static SanchayTableModel getTextStats(String txt) {
        String lines[] = txt.split("[\\n]");

        long charCount = 0;
        long wordCount = 0;
        long lineCount = lines.length;

        for (int i = 0; i < lineCount; i++) {
            charCount += lines[0].length();

            String tokenizedString = lines[0];
            //String tokenizedString = UtilityFunctions.naiiveTokenization();

            String wds[] = tokenizedString.split("[ ]");

            wordCount += wds.length;
        }

        SanchayTableModel statTable = new SanchayTableModel(3, 2);

        statTable.setValueAt(GlobalProperties.getIntlString("Characters"), 0, 0);
        statTable.setValueAt(Long.toString((long) charCount), 0, 1);
        statTable.setValueAt(GlobalProperties.getIntlString("Words"), 1, 0);
        statTable.setValueAt(Long.toString((long) wordCount), 1, 1);
        statTable.setValueAt(GlobalProperties.getIntlString("Lines"), 2, 0);
        statTable.setValueAt(Long.toString((long) lineCount), 2, 1);

        return statTable;
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static int getFilledLength(Object[] array) {
        int count = array.length;

        int length = 0;

        for (int i = 0; i < count; i++) {
            if (array[i] != null)
                length++;
        }

        return length;
    }

    public static String getUnicodeHex(String s) {
        int count = s.length();
        String hex = "";

        for (int i = 0; i < count; i++) {
            hex += UtilityFunctions.getUnicodeHex(s.charAt(i));
        }

        return hex;
    }

    public static String getEscapedUnicodeHex(String s) {
        int count = s.length();
        String hex = "";

        for (int i = 0; i < count; i++) {
            hex += "\\u" + UtilityFunctions.getUnicodeHex(s.charAt(i));
        }

        return hex;
    }

    public static String getUnicodeHex(char c) {
        String hex = Integer.toHexString((int) c);

        if (hex.length() == 3)
            hex = "0" + hex;
        else if (hex.length() == 2)
            hex = "00" + hex;

        return hex;
    }

    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    public static BufferedImage toCompatibleImage(BufferedImage image, GraphicsConfiguration gc) {
        if (gc == null)
            gc = getDefaultConfiguration();
        int w = image.getWidth();
        int h = image.getHeight();
        int transparency = image.getColorModel().getTransparency();
        BufferedImage result = gc.createCompatibleImage(w, h, transparency);
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage(image, null);
        g2.dispose();
        return result;
    }

    public static BufferedImage copy(BufferedImage source, BufferedImage target) {
        Graphics2D g2 = target.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double scalex = (double) target.getWidth() / source.getWidth();
        double scaley = (double) target.getHeight() / source.getHeight();
        AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
        g2.drawRenderedImage(source, xform);
        g2.dispose();
        return target;
    }

    public static BufferedImage getScaledInstance(BufferedImage image, int width, int height, GraphicsConfiguration gc) {
        if (gc == null)
            gc = getDefaultConfiguration();
        int transparency = image.getColorModel().getTransparency();
        return copy(image, gc.createCompatibleImage(width, height, transparency));
    }

    public static void maxmize(Window window) {
        int inset = 5;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 3);
    }

    public static boolean isPossiblyUTF8String(String str) {
        boolean isUTF8 = false;

        int len = str.length();
        int score = 0;

        for (int i = 0; i < len; i++) {
            if (Character.isUnicodeIdentifierPart(str.charAt(i)))
                score++;
        }

        if (score >= len / 5)
            isUTF8 = true;

        return isUTF8;
    }
//
//    public static boolean isInteger(String str)
//    {
//        Pattern p = Pattern.compile("^[0-9]+$");
//
//        Matcher m = p.matcher(str);
//
//        if(m.find())
//            return true;
//
//        return false;
//    }

    public static int getTotalValue(Map<String, Integer> map) {
        int total = 0;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String object = entry.getKey();
            Integer integer = entry.getValue();

            total += integer;
        }

        return total;
    }

    //
    public static LinkedHashMap getTopNElements(LinkedHashMap allMatches, int topMatches) {
        Iterator itr = allMatches.keySet().iterator();

        int j = 0;
        LinkedHashMap retMatches = new LinkedHashMap(topMatches);

        while (j < topMatches && itr.hasNext()) {
            Object contextKey = itr.next();

            retMatches.put(contextKey, allMatches.get(contextKey));
            j++;
        }

        return retMatches;
    }

    public static Vector getTopNElements(Vector allMatches, int topMatches) {
        if (topMatches <= 0)
            return null;

        int count = allMatches.size();

        Vector retMatches = new Vector(topMatches);

        count = Math.min(count, topMatches);

        for (int i = 0; i < count; i++) {
            retMatches.add(allMatches.get(i));
        }

        return retMatches;
    }

    public static void printVector(Vector vec, PrintStream ps) {
        int count = vec.size();

        for (int i = 0; i < count; i++) {
            ps.println(vec.get(i));
        }
    }

    public static void printArray(Object arr[], PrintStream ps, String sep) {
        for (int i = 0; i < arr.length; i++) {
            if (i < arr.length - 1)
                ps.print(arr[i] + sep);
            else
                ps.print(arr[i]);
        }
    }

    public static String getSpacedOutString(String str) {
        String out = "";

        String parts[] = str.split("");

        for (int i = 0; i < parts.length; i++) {
            String string = parts[i];

            out += " " + string;
        }

        return out.trim();
    }

    public static boolean isASCIIVowel(char c) {
        if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u')
            return true;

        return false;
    }

    public static Color getColor(String clr) {
        if (clr.equalsIgnoreCase(GlobalProperties.getIntlString("Red")))
            return Color.RED;
        if (clr.equalsIgnoreCase(GlobalProperties.getIntlString("Blue")))
            return Color.BLUE;
        if (clr.equalsIgnoreCase(GlobalProperties.getIntlString("Green")))
            return Color.GREEN;
        if (clr.equalsIgnoreCase(GlobalProperties.getIntlString("White")))
            return Color.WHITE;
        if (clr.equalsIgnoreCase(GlobalProperties.getIntlString("Black")))
            return Color.BLACK;
        if (clr.equalsIgnoreCase(GlobalProperties.getIntlString("Magenta")))
            return Color.MAGENTA;
        if (clr.equalsIgnoreCase(GlobalProperties.getIntlString("Cyan")))
            return Color.CYAN;

        return Color.BLACK;
    }

    public static Stroke getStroke(String strk) {
        if (strk.equalsIgnoreCase(GlobalProperties.getIntlString("Solid")))
            return new BasicStroke(4.0f);
        if (strk.equalsIgnoreCase(GlobalProperties.getIntlString("Dotted")))
            return new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);

        return new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
    }

    public static void makeExactMatchRegexes(List vec) {
        int count = vec.size();

        for (int i = 0; i < count; i++) {
            vec.set(i, "^" + ((String) vec.get(i)) + "$");
        }
    }

    public static void backFromExactMatchRegex(List vec) {
        int count = vec.size();

        for (int i = 0; i < count; i++) {
            String regex = ((String) vec.get(i));

            if (regex.startsWith("^") && regex.length() > 0)
                regex = regex.substring(1);

            if (regex.endsWith("$") && regex.length() > 0)
                regex = regex.substring(0, regex.length() - 1);

            vec.set(i, regex);
        }
    }

    public static String backFromExactMatchRegex(String regex) {
        if (regex.startsWith("^") && regex.length() > 0)
            regex = regex.substring(1);

        if (regex.endsWith("$") && regex.length() > 0)
            regex = regex.substring(0, regex.length() - 1);

        return regex;
    }

    public static List getUnique(List vec) {
        List retVec = null;

        try {
            retVec = vec.getClass().newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(UtilityFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(UtilityFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }

        LinkedHashMap hash = new LinkedHashMap(vec.size());

        int count = vec.size();

        for (int i = 0; i < count; i++) {
            hash.put(vec.get(i), new Integer(1));
        }

        Iterator itr = hash.keySet().iterator();

        while (itr.hasNext())
            retVec.add(itr.next());

        return retVec;
    }

    public static List getIntersection(List vec1, List vec2) {
        if (vec1 == null || vec2 == null)
            return null;

//        if(vec1 == null)
//            return vec2;
//
//        if(vec2 == null)
//            return vec1;

        List retVec = null;

        try {
            retVec = vec1.getClass().newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(UtilityFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(UtilityFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }

        LinkedHashMap hash1 = new LinkedHashMap(vec1.size());

        int count = vec1.size();

        for (int i = 0; i < count; i++) {
            hash1.put(vec1.get(i), new Integer(1));
        }

        LinkedHashMap hash2 = new LinkedHashMap(vec2.size());

        count = vec2.size();

        for (int i = 0; i < count; i++) {
            hash2.put(vec2.get(i), new Integer(1));
        }

        Iterator itr = hash1.keySet().iterator();

        while (itr.hasNext()) {
            Object key = itr.next();

            if (hash2.get(key) != null)
                retVec.add(key);
        }

        itr = hash2.keySet().iterator();

        while (itr.hasNext()) {
            Object key = itr.next();

            if (hash1.get(key) != null)
                retVec.add(key);
        }

        retVec = getUnique(retVec);

        return retVec;
    }

    public static Map getReverseMap(Map map) {
        Map revMap = null;

        try {
            revMap = map.getClass().newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(UtilityFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(UtilityFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }

        Iterator itr = map.keySet().iterator();

        while (itr.hasNext()) {
            Object key = itr.next();
            Object val = map.get(key);

            revMap.put(val, key);
        }

        return revMap;
    }

    public static void mergeMap(Map tgtMap, Map srcMap) {
        Iterator itr = srcMap.keySet().iterator();

        while (itr.hasNext()) {
            Object key = itr.next();
            Object srcVal = srcMap.get(key);

            if (srcVal instanceof Integer) {
                Integer tgtVal = (Integer) tgtMap.get(key);

                if (tgtVal == null)
                    tgtMap.put(key, srcVal);
                else
                    tgtMap.put(key, new Integer(((Integer) srcVal).intValue() + ((Integer) tgtVal).intValue()));
            } else if (srcVal instanceof Long) {
                Long tgtVal = (Long) tgtMap.get(key);

                if (tgtVal == null)
                    tgtMap.put(key, srcVal);
                else
                    tgtMap.put(key, new Long(((Long) srcVal).longValue() + ((Long) tgtVal).longValue()));
            } else if (srcVal instanceof Float) {
                Float tgtVal = (Float) tgtMap.get(key);

                if (tgtVal == null)
                    tgtMap.put(key, srcVal);
                else
                    tgtMap.put(key, new Float(((Float) srcVal).floatValue() + ((Float) tgtVal).floatValue()));
            } else if (srcVal instanceof Double) {
                Double tgtVal = (Double) tgtMap.get(key);

                if (tgtVal == null)
                    tgtMap.put(key, srcVal);
                else
                    tgtMap.put(key, new Double(((Double) srcVal).doubleValue() + ((Double) tgtVal).doubleValue()));
            } else
                tgtMap.put(key, srcVal);
        }
    }

    public static void mergeFiles(String[] inputFiles, String outputFile) throws FileNotFoundException, IOException {
        File ofile = new File(outputFile);

        if (ofile.getParentFile().exists() == false) {
            ofile.getParentFile().mkdirs();
        }

        if (inputFiles.length == 1)
            copyFile(new File(inputFiles[0]), new File(outputFile));
        else {
            OutputStream outReader = new FileOutputStream(outputFile);

            for (int i = 0; i < inputFiles.length; i++) {
                String inputFile = inputFiles[i];

                InputStream inReader = new FileInputStream(inputFile);

                byte[] buf = new byte[1024];

                int len;

                while ((len = inReader.read(buf)) > 0) {
                    outReader.write(buf, 0, len);
                }

                inReader.close();
            }

            outReader.close();
        }
    }

    public static Vector arrayToVector(Object array[]) {
        Vector vec = new Vector(array.length);

        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            vec.add(object);
        }

        return vec;
    }

    public static String getRoutePath(File srcPath, File tgtPath) {
        Vector<File> srcAncestors = getAncestorDirectories(srcPath);
        Vector<File> tgtAncestors = getAncestorDirectories(tgtPath);

        int scount = srcAncestors.size();
        int tcount = tgtAncestors.size();

        int i = 0;

        for (; i < Math.min(scount, tcount); i++) {
            File srcAncestor = srcAncestors.get(i);
            File tgtAncestor = tgtAncestors.get(i);

            if (srcAncestor.equals(tgtAncestor) == false)
                break;
        }

        String routePath = "";

        for (int j = i; j <= scount; j++) {
            routePath = "../" + routePath;
        }

        for (int j = i; j < tcount; j++) {
            File tgtAncestor = tgtAncestors.get(j);
            routePath = routePath + tgtAncestor.getName() + "/";
        }

        if (tgtPath.isDirectory())
            routePath = routePath + tgtPath.getName();

        return routePath;
    }

    public static Vector<File> getAncestorDirectories(File file) {
        Vector<File> ancestors = new Vector<File>();

        while (file != null && file.getParentFile() != null) {
            ancestors.add(file.getParentFile());
            file = file.getParentFile();
        }

        Collections.reverse(ancestors);

        return ancestors;
    }

    public static boolean isAncestorDirectory(File file, File posAncestorDirectory) {
        boolean isParent = false;

        while (file != null && isParent == false) {
            if (file.getParentFile().getAbsolutePath().equals(posAncestorDirectory.getAbsolutePath())) {
                isParent = true;
            } else
                file = file.getParentFile();
        }

        return isParent;
    }

    public static String toTitleCase(String inStr) {
        String outStr = "";

        String parts[] = inStr.split(" ");

        for (int i = 0; i < parts.length; i++) {
            String string = parts[i];

            if (string.length() == 1)
                string = "" + Character.toUpperCase(string.charAt(0));
            else if (string.length() > 1)
                string = Character.toUpperCase(string.charAt(0)) + string.substring(1);

            if (i < parts.length - 1)
                string += " ";

            outStr += string;
        }

        return outStr;
    }

    public static void addAll(InputMap imTo, InputMap imFrom) {
        KeyStroke keyStrokes[] = imFrom.keys();

        for (int i = 0; i < keyStrokes.length; i++) {
            KeyStroke keyStroke = keyStrokes[i];
            imTo.put(keyStroke, imFrom.get(keyStroke));
        }
    }

    public static void addAll(ActionMap amTo, ActionMap amFrom) {
        Object actionNames[] = amFrom.keys();

        for (int i = 0; i < actionNames.length; i++) {
            Object actionName = actionNames[i];
            amTo.put(actionName, amFrom.get(actionName));
        }
    }

    public static void widthToFit(JTable table, int mode) {
        int ccount = table.getColumnCount();

        Dimension size = table.getPreferredSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (ccount <= 0)
            return;

        SanchayTableModel model = (SanchayTableModel) table.getModel();
        TableColumn column = null;
        //        Component comp = null;

        if (mode == SanchayJTable.ALIGNMENT_MODE && ccount == 3) {
            column = table.getColumnModel().getColumn(0);
            column.setPreferredWidth((int) (0.42 * (float) screenSize.width));

            column = table.getColumnModel().getColumn(1);
            column.setPreferredWidth((int) (0.05 * (float) screenSize.width));

            column = table.getColumnModel().getColumn(2);
            column.setPreferredWidth((int) (0.42 * (float) screenSize.width));
        }

        table.revalidate();
        table.repaint();
    }

    public static boolean areConsequentNumbers(int nums[]) {
        int prev = nums[0];

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != prev + 1)
                return false;

            prev = nums[i];
        }

        return true;
    }

    public static boolean fileExists(String path) {
        File f = new File(path);

        return f.exists();
    }

    public static boolean isInteger(String str) {
        if (str == null)
            return false;

//        str = str.trim();

        Pattern p = Pattern.compile("^\\-?[0-9][0-9]*$");

        Matcher m = p.matcher(str);

        if (m.find())
            return true;

        return false;
    }

    public static boolean isFile(String str) {
        if (str == null)
            return false;

        File file = new File(str);

        if (file.isFile())
            return true;

        return false;
    }

    public static String getRepeatString(int width, String string) {
        String repeatString = "";

        for (int i = 0; i < width; i++) {
            repeatString += string;
        }

        return repeatString;
    }

    public static Object getFirstKey(Map map) {
        if (map == null || map.isEmpty())
            return null;

        Object keys[] = map.keySet().toArray();

        return keys[0];
    }

    public static Object getLastKey(Map map) {
        if (map == null || map.isEmpty())
            return null;

        Object keys[] = map.keySet().toArray();

        return keys[keys.length - 1];
    }

    public static Object getFirstValue(Map map) {
        if (map == null || map.isEmpty())
            return null;

        Collection vals = map.values();

        Object keys[] = vals.toArray();

        return keys[0];
    }

    public static Object getLastValue(Map map) {
        if (map == null || map.isEmpty())
            return null;

        Collection vals = map.values();

        Object keys[] = vals.toArray();

        return keys[keys.length - 1];
    }

    public static DefaultComboBoxModel getComboBoxModel(FeatureStructures fss) {
        int count = fss.countAltFSValues();

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();

        for (int i = 0; i < count; i++) {
            FeatureStructure fs = fss.getAltFSValue(i);

//            dcbm.addElement(fs.makeString());
            dcbm.addElement(fs);
        }

        return dcbm;
    }

    public static SanchayTableJPanel getMandatoryFSTable(FeatureStructures fss) {
        SanchayTableJPanel sanchayTableJPanel = SanchayTableJPanel.createFeatureTableJPanel(null, null);

        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();
        int mcount = fsProperties.countMandatoryAttributes();

        for (int i = 1; i < mcount; i++) {
            String fname = fsProperties.getMandatoryAttribute(i);
            String fvalue = fsProperties.getMandatoryAttributeValue(i);

            JMenu mfMenu = new JMenu(fname);

            String vals[] = fvalue.split("::");

            for (int j = 0; j < vals.length; j++) {
//                mfMenu.add(new JMenuItem(new NodeMandatoryFeatureValueTreeAction(jtree, fname, vals[j], sanchayTreeJPanel)));
            }

//            mandatoryFeatureValueEditingJMenu.add(mfMenu);
        }

        return sanchayTableJPanel;
    }

    public static void fillMandatoryFSEditJPanel(SanchayTreeJPanel sanchayTreeJPanel, FeatureStructure fs, JPanel fsEditJPanel,
                                                 String langEnc) {
//        JPanel fsEditJPanel = new JPanel(new java.awt.GridLayout(0, 2, 3, 3));

        FSProperties fsProperties = FeatureStructuresImpl.getFSProperties();
        int mcount = fsProperties.countMandatoryAttributes();

        for (int i = 0; i < mcount; i++) {
            String fname = fsProperties.getMandatoryAttribute(i);
            String fvalue = fsProperties.getMandatoryAttributeValue(i);

            JLabel mfLabel = new JLabel(fname);

            fsEditJPanel.add(mfLabel);

            JComboBox jcb = new JComboBox();
            jcb.setEditable(true);

            DefaultComboBoxModel dcbm = new DefaultComboBoxModel();

            jcb.addActionListener(new AttributeValueActionListener(sanchayTreeJPanel, fname, fs, dcbm, langEnc));

            fsEditJPanel.add(jcb);

            String vals[] = fvalue.split("::");

            for (int j = 0; j < vals.length; j++) {
                dcbm.addElement(vals[j]);
//                mfMenu.add(new JMenuItem(new NodeMandatoryFeatureValueTreeAction(jtree, fname, vals[j], sanchayTreeJPanel)));
            }

            jcb.setModel(dcbm);

            UtilityFunctions.setComponentFont(jcb, langEnc);

//            mandatoryFeatureValueEditingJMenu.add(mfMenu);
        }

//        return fsEditJPanel;
    }

    public static Map sortByKeys(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getKey())
                        .compareTo(((Map.Entry) (o2)).getKey());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Map sort(Map map, Comparator cmp) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, cmp);

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Object getLastElement(Map map) {
        Object element = null;

        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();

            element = map.get(key);
        }

        return element;
    }

    public static Index getCopy(Index index) {
        Index copyIndex = new HashIndex();

        Iterator itr = index.iterator();

        while (itr.hasNext()) {
            String str = (String) itr.next();

            copyIndex.add(str);
        }

        return copyIndex;
    }

    public static boolean isPunctuation(String str) {
        if (Pattern.matches("\\p{Punct}", str)) {
            return true;
        }

        return false;
    }
}
