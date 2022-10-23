/*
 * SanchayLanguages.java
 *
 * Created on February 15, 2006, 10:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import com.ibm.icu.lang.UScript;
import guk.im.GateIM;

import java.awt.*;
import java.awt.im.spi.InputMethodDescriptor;
import java.io.*;
import java.lang.Character.UnicodeBlock;
import java.util.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import jmathlib.toolbox.jmathlib.system.java;

import sanchay.GlobalProperties;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.MultiPropertyTokens;
import sanchay.properties.PropertiesTable;
import sanchay.properties.PropertyTokens;
import sanchay.util.UtilityFunctions;

/**
 * @author Anil Kumar Singh
 */
public class SanchayLanguages {
    // Languages and their ISO 3-letter codes and vice-versa
    protected static KeyValueProperties allLanguages;
    protected static KeyValueProperties revAllLanguages;

    protected static KeyValueProperties allEncodings;
    protected static KeyValueProperties revAllEncodings;

    protected static MultiPropertyTokens allLangEncodings;

    protected static PropertiesTable unicodeBlocks;
    protected static PropertiesTable allUnicodeBlocks;

    protected static KeyValueProperties allInputMethods;
    protected static String previousLocaleName;

    // Default fonts for each language-encoding pair
    protected static KeyValueProperties langEncFonts;
    protected static KeyValueProperties defLangEncFonts;

    // All fonts for each language-encoding pair
    // One LinkedHashMap for each inside the outer LinkedHashMap
    protected static LinkedHashMap allFonts;
    protected static LinkedHashMap allFontsFlat;
    protected static LinkedHashMap multiLingualFonts;

    public static int DEFAULT_FONT_STYLE = Font.BOLD;
    public static int DEFAULT_FONT_SIZE = 14;
    public static int DEFAULT_FONT_SIZE_ENG = 12;

    public static float minDisplayableFraction = (float) 0.3;
    public static float maxDisplayableFraction = (float) 0.7;

    protected static LinkedHashMap installedLocales;
    protected static String selectedLocaleName;
    protected static boolean kbMapShown;

    protected static LinkedHashMap<String, String> twoLetter2ThreeLetterCodeMap;
    protected static LinkedHashMap<String, String> threeLetter2TwoLetterCodeMap;

    /**
     * Creates a new instance of SanchayLanguages
     */
    public SanchayLanguages() {
    }

    public static void initLanguages() {
        loadFonts();

        initLocaleCodes();
        initInputMethods();
        kbMapShown = false;
    }

    public static void initInputMethods() {
        System.setProperty("java.ext.dirs", "ext-lib");

        try {
            allInputMethods = new KeyValueProperties(GlobalProperties.resolveRelativePath("props/input-methods.txt"), GlobalProperties.getIntlString("UTF-8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        installedLocales = new LinkedHashMap();
        selectedLocaleName = "System input method";
        previousLocaleName = selectedLocaleName;
        Locale selectedLocale = Locale.getDefault();

        installedLocales.put(selectedLocaleName, selectedLocale);

        Iterator enm = allInputMethods.getPropertyKeys();

        while (enm.hasNext()) {
            String imClassName = (String) enm.next();
            String imProvider = (String) allInputMethods.getPropertyValue(imClassName);

            loadInputMethods(imClassName, imProvider);
        }
    }

    public static void initLocaleCodes() {
        twoLetter2ThreeLetterCodeMap = new LinkedHashMap();
        threeLetter2TwoLetterCodeMap = new LinkedHashMap();

        Locale loc[] = Locale.getAvailableLocales();

        for (int i = 0; i < loc.length; i++) {
            Locale locale = loc[i];

            String twoLetterCode = locale.getLanguage();
            String threeLetterCode = locale.getISO3Language();

            twoLetter2ThreeLetterCodeMap.put(twoLetterCode, threeLetterCode);
        }

        threeLetter2TwoLetterCodeMap = (LinkedHashMap<String, String>) UtilityFunctions.getReverseMap(twoLetter2ThreeLetterCodeMap);
    }

    private static void loadInputMethods(String imClassName, String provider) {
        try {
            InputMethodDescriptor imd = (InputMethodDescriptor) Class.forName(imClassName).newInstance();
//            Class imdClass = Class.forName(imClassName);
//            Class[] parameterType = null;
//            Object[] objectType = null;
//            InputMethodDescriptor imd = (InputMethodDescriptor) imdClass.getDeclaredConstructor(parameterType).newInstance(objectType);
//            InputMethodDescriptor imd = new DevanagariInputMethodDescriptor();

            Locale locales[] = imd.getAvailableLocales();

            for (int i = 0; i < locales.length; i++) {
                installedLocales.put(locales[i].getDisplayName() + " (" + provider + ")", locales[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LinkedHashMap getAllInputMethods() {
        return installedLocales;
    }

    public static void loadFonts() {
        allFonts = new LinkedHashMap(0, 3);
        allFontsFlat = new LinkedHashMap(0, 3);

        try {
            allLanguages = new KeyValueProperties(GlobalProperties.resolveRelativePath("props/languages.txt"), GlobalProperties.getIntlString("UTF-8"));
            revAllLanguages = allLanguages.getReverse();

            allEncodings = new KeyValueProperties(GlobalProperties.resolveRelativePath("props/encodings.txt"), GlobalProperties.getIntlString("UTF-8"));
            revAllEncodings = allEncodings.getReverse();

            allLangEncodings = new MultiPropertyTokens(GlobalProperties.resolveRelativePath("props/lang-encs.txt"), GlobalProperties.getIntlString("UTF-8"));

            unicodeBlocks = new PropertiesTable(GlobalProperties.resolveRelativePath("props/lang-unicode-blocks.txt"), GlobalProperties.getIntlString("UTF-8"));
            allUnicodeBlocks = new PropertiesTable(GlobalProperties.resolveRelativePath("props/unicode-blocks.txt"), GlobalProperties.getIntlString("UTF-8"));

            allUnicodeBlocks.stringsToLowerCase();

            langEncFonts = new KeyValueProperties(GlobalProperties.resolveRelativePath("props/lang-enc-font-props.txt"), GlobalProperties.getIntlString("UTF-8"));
            defLangEncFonts = new KeyValueProperties(GlobalProperties.resolveRelativePath("props/lang-enc-default-fonts.txt"), GlobalProperties.getIntlString("UTF-8"));

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Load all fonts
        Iterator enm = langEncFonts.getPropertyKeys();
        LinkedHashMap fonts = null;

        while (enm.hasNext()) {
            String key = (String) enm.next();
            String val = langEncFonts.getPropertyValue(key);

            if (val.equals("_SYSTEM_FONTS_"))
                continue;

            String parts[] = val.split(":");

            if (parts == null)
                continue;

            for (int i = 0; i < parts.length; i++) {
                val = GlobalProperties.getHomeDirectory() + "/" + parts[i];
                File fontFile = new File(val);

                fonts = new LinkedHashMap(3, 3);

                try {
                    loadFonts(fontFile, fonts, allFontsFlat);

                    if (allFonts.get(key) == null)
                        allFonts.put(key, fonts);
                    else
                        ((LinkedHashMap) allFonts.get(key)).putAll(fonts);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (FontFormatException ex) {
                    ex.printStackTrace();
                }
            }
        }

        multiLingualFonts = new LinkedHashMap(3, 3);

        try {
            loadMultiLingualFonts(new File(GlobalProperties.getHomeDirectory() + "/" + "fonts/multi-lingual"), multiLingualFonts, allFontsFlat);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (FontFormatException ex) {
            ex.printStackTrace();
        }

//	printFontList(System.out);
    }

    public static void loadFonts(File f /* file or directory */, LinkedHashMap fonts, LinkedHashMap fontsFlat)
            throws FontFormatException, IOException {
        if (f.isFile() && f.getAbsolutePath().contains(".svn") == false) {
            FileInputStream fis = new FileInputStream(f);
            Font font = Font.createFont(Font.TRUETYPE_FONT, f);

            String fontFamily = font.getFamily();

            Font sysFont = findSystemFont(fontFamily);

            if (sysFont == null) {
                Font fnt = (Font) fontsFlat.get(fontFamily);

                if (fnt == null)
                    font = font.deriveFont(DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);
                else
                    font = fnt;
            } else
                font = new Font(fontFamily, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);

            fonts.put(fontFamily, font);
            fontsFlat.put(fontFamily, font);
//	    System.out.println(fontFamily);
        }

        File files[] = f.listFiles();

        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {
            loadFonts(files[i], fonts, fontsFlat);
        }
    }

    // These will be Unicode fonts containing glyphs for more than one languages
    // An entry will be made in every relevant language-encoding font LinkedHashMap
    // Not recursive, unlike loadFonts
    public static void loadMultiLingualFonts(File f /* file or directory */, LinkedHashMap fonts, LinkedHashMap fontsFlat)
            throws FontFormatException, IOException {
        loadFonts(f, fonts, fontsFlat);

        // Find the languages a font supports and make an entry for each of those languages.

        // Loop over languages
        int rcount = unicodeBlocks.getRowCount();


        for (int i = 0; i < rcount; i++) {
            String lang = (String) unicodeBlocks.getValueAt(i, 0);
            String uniBlock = (String) unicodeBlocks.getValueAt(i, 1);
            String startCodeStr = (String) unicodeBlocks.getValueAt(i, 2);
            String endCodeStr = (String) unicodeBlocks.getValueAt(i, 3);

            int startCode = Integer.parseInt(startCodeStr, 16);
            int endtCode = Integer.parseInt(endCodeStr, 16);

            String langEncCode = getLangEncCode(lang);

            // Loop over fonts
            Iterator itr = fonts.keySet().iterator();

            while (itr.hasNext()) {
                // Make an entry in allFonts if applicable

                String fontFam = (String) itr.next();
                Font fnt = (Font) fonts.get(fontFam);

                if (canDisplay(fnt, startCode, endtCode, minDisplayableFraction)) {
                    LinkedHashMap fontHT = (LinkedHashMap) allFonts.get(langEncCode);

                    if (fontHT == null) {
                        fontHT = new LinkedHashMap(3, 3);
                        allFonts.put(langEncCode, fontHT);
                    }

                    if (fontHT.get(fontFam) == null) {
                        fontHT.put(fontFam, fnt);
//			System.out.println(fontFam + ": " + langEncCode);
                    }
                }
            }
        }
    }

    public static Font getFontFor(int startUniCode, int endUniCode, float maxDisplayableFraction) {
        Iterator itr = allFonts.keySet().iterator();

        Font font = null;

        while (itr.hasNext()) {
            String key = (String) itr.next();

            LinkedHashMap fonts = (LinkedHashMap) allFonts.get(key);

            Iterator itrf = fonts.keySet().iterator();

            while (itrf.hasNext()) {
                String fontFamily = (String) itrf.next();
                font = (Font) fonts.get(fontFamily);

                if (canDisplay(font, startUniCode, endUniCode, maxDisplayableFraction))
                    return font;
            }
        }

        return null;
    }

    public static boolean canDisplay(Font fnt, int startUniCode, int endUniCode, float minDisplayableFraction) {
        if (startUniCode > endUniCode)
            return false;

        float displayable = 0;

        for (int i = startUniCode; i <= endUniCode; i++) {
            if (fnt.canDisplay(i))
                displayable++;
        }

        float displayableFraction = displayable / ((float) (endUniCode - startUniCode + 1));

        if (displayableFraction >= minDisplayableFraction)
            return true;

        return false;
    }

    public static Font findSystemFont(String fontFam) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String fontFamilies[] = env.getAvailableFontFamilyNames();

        Arrays.sort(fontFamilies);

        if (Arrays.binarySearch(fontFamilies, fontFam) >= 0)
            return new Font(fontFam, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);

        return null;
    }

    public static String getLangEncCode(String languageName) {
        if (allLanguages == null)
            SanchayLanguages.initLanguages();

        return allLanguages.getPropertyValue(languageName) + GlobalProperties.getIntlString("::utf8");
    }

    public static String getLangEncCodeFromLanguageCode(String languageCode) {
        if (allLanguages == null)
            SanchayLanguages.initLanguages();

        return languageCode + GlobalProperties.getIntlString("::utf8");
    }

    public static String getLangEncCode(String languageName, String encodingName) {
        String langCode = getLanguageCode(languageName);
        String encCode = getEncodingCode(encodingName);

        return langCode + "::" + encCode;
    }

    public static String getLanguageName(String languageEncCode /* Could be just the language code */) {
        if (revAllLanguages == null)
            SanchayLanguages.initLanguages();

        String parts[] = languageEncCode.split("::");

        if (parts != null && parts.length > 1)
            languageEncCode = parts[0];

        return revAllLanguages.getPropertyValue(languageEncCode);
    }

    public static String getEncodingName(String languageEncCode /* Could be just the encoding code */) {
        if (revAllEncodings == null)
            SanchayLanguages.initLanguages();

        String parts[] = languageEncCode.split("::");

        if (parts != null && parts.length > 1)
            languageEncCode = parts[1];

        return revAllEncodings.getPropertyValue(languageEncCode);
    }

    public static String getLanguageCode(String langName) {
        if (allLanguages == null)
            SanchayLanguages.initLanguages();

        return allLanguages.getPropertyValue(langName);
    }

    public static String getLanguageCodeFromLECode(String leCode) {
        return leCode.split("::")[0];
    }

    public static String getEncodingCodeFromLECode(String leCode) {
        return leCode.split("::")[1];
    }

    public static String getEncodingCode(String encName) {
        return allEncodings.getPropertyValue(encName);
    }

    public static KeyValueProperties getAllLanguages() {
        if (allLanguages == null)
            SanchayLanguages.initLanguages();

        return allLanguages;
    }

    public static PropertyTokens getEncodings(String langCode) {
        if (allEncodings == null)
            SanchayLanguages.initLanguages();

        PropertyTokens commonEncs = (PropertyTokens) allLangEncodings.getMultiPropertiesMap().get(GlobalProperties.getIntlString("common"));
        PropertyTokens encs = (PropertyTokens) allLangEncodings.getMultiPropertiesMap().get(langCode);

        encs.addAllProperties(commonEncs);

        return encs;
    }

    // Returns a LinkedHashMap containing fonts
    public static LinkedHashMap getLangEncFonts(String langenc) {
        if (allFonts == null)
            SanchayLanguages.initLanguages();

        return (LinkedHashMap) allFonts.get(langenc);
    }

    public static Font getDefaultLangEncFont(String langenc) {
        if (defLangEncFonts == null)
            SanchayLanguages.initLanguages();

        String fontFamily = defLangEncFonts.getPropertyValue(langenc);

        if (fontFamily != null)
            return (Font) getLangEncFonts(langenc).get(fontFamily);

        return new Font("Sans Serif", DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);
    }

    public static Font getFont(String langenc, String family) {
        if (allFonts == null)
            SanchayLanguages.initLanguages();

        return (Font) getLangEncFonts(langenc).get(family);
    }

    public static Font getFont(String family) {
        if (allFonts == null)
            SanchayLanguages.initLanguages();

        return (Font) allFontsFlat.get(family);
    }

    public static void fillLanguages(DefaultComboBoxModel cbm, String selLang) {
        String langs[] = getLanguageList();

        cbm.removeAllElements();

        for (int i = 0; i < langs.length; i++)
            cbm.addElement(langs[i]);

        cbm.setSelectedItem(selLang);
    }

    public static void fillLanguages(DefaultComboBoxModel cbm) {
        fillLanguages(cbm, GlobalProperties.getIntlString("Hindi"));
    }

    public static String[] getLanguageList() {
        // Filling up languages
        Vector allLanguages = new Vector(0, 3);

        Iterator enm = SanchayLanguages.getAllLanguages().getPropertyKeys();

        while (enm.hasNext()) {
            String key = (String) enm.next();
            allLanguages.add(key);
        }

        String langs[] = new String[allLanguages.size()];
        allLanguages.toArray(langs);

        Arrays.sort(langs);

        return langs;
    }

    public static void fillEncodings(DefaultComboBoxModel cbm, String langCode) {
        PropertyTokens encs = SanchayLanguages.getEncodings(langCode);

        Vector allEncodings = encs.getCopyOfTokens();

        Object encodings[] = allEncodings.toArray();

        Arrays.sort(encodings);

        String initEnc = GlobalProperties.getIntlString("UTF-8");

        cbm.removeAllElements();

        for (int i = 0; i < encodings.length; i++)
            cbm.addElement(getEncodingName((String) encodings[i]));

        cbm.setSelectedItem(initEnc);
    }

    public static void fillFonts(DefaultComboBoxModel cbm, String langEncCode, boolean allSystemFonts) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String fontFamilies[] = env.getAvailableFontFamilyNames();

        cbm.removeAllElements();

        Vector fontVector = null;

        LinkedHashMap fonts = getLangEncFonts(langEncCode);

        if (langEncCode.equalsIgnoreCase(GlobalProperties.getIntlString("eng::utf8")) || allSystemFonts) {
            fontVector = new Vector(fontFamilies.length, fonts.size());

            for (int i = 0; i < fontFamilies.length; i++) {
                fontVector.add(fontFamilies[i]);
//            System.out.println(fontFamilies[i]);
            }
        }

        if (fontVector == null)
            fontVector = new Vector(fonts.size());

        Iterator itr = fonts.keySet().iterator();

        while (itr.hasNext()) {
            String fontFamily = (String) itr.next();
            fontVector.add(fontFamily);
        }

        Object fontFamilyArray[] = fontVector.toArray();
        Arrays.sort(fontFamilyArray);

        for (int i = 0; i < fontFamilyArray.length; i++)
            cbm.addElement(fontFamilyArray[i]);
    }

    public static void fillFontSizes(DefaultComboBoxModel cbm, int min, int max, int step) {
        cbm.removeAllElements();

        for (int i = min; i <= max; i += step)
            cbm.addElement((new Integer(i)).toString());
    }

    public static void printFontList(PrintStream ps) {
        Iterator enm = langEncFonts.getPropertyKeys();
        LinkedHashMap fonts = null;

        while (enm.hasNext()) {
            String key = (String) enm.next();
            String val = langEncFonts.getPropertyValue(key);

            ps.println(key);

            fonts = getLangEncFonts(key);

            Iterator fitr = fonts.keySet().iterator();

            while (fitr.hasNext()) {
                String fontFamily = (String) fitr.next();
                ps.println("\t" + fontFamily);
            }
        }
    }

    public static void increaseFontSize() {
        DEFAULT_FONT_SIZE += 2;
        DEFAULT_FONT_SIZE_ENG += 2;
    }

    public static void decreaseFontSize() {
        DEFAULT_FONT_SIZE -= 2;
        DEFAULT_FONT_SIZE_ENG -= 2;
    }

    public static void showKBMap(Component cmp) {
        Object imObject = cmp.getInputContext().getInputMethodControlObject();
        if (imObject != null && imObject instanceof GateIM) {

            if (kbMapShown) {
                ((GateIM) imObject).setMapVisible(false);
                kbMapShown = false;
            } else {
                ((GateIM) imObject).setMapVisible(true);
                kbMapShown = true;
            }
        }
    }

    public static String selectLanguage(Component cmp, String prevLangEncCode) {
        String lCode = SanchayLanguages.getLanguageCodeFromLECode(prevLangEncCode);
        String eCode = SanchayLanguages.getEncodingCodeFromLECode(prevLangEncCode);
        String allLanguages[] = SanchayLanguages.getLanguageList();

        Arrays.sort(allLanguages);

        String initLang = SanchayLanguages.getLanguageName(prevLangEncCode);

        String currentEncoding = SanchayLanguages.getEncodingName(eCode);

        String selectedLanguage = (String) JOptionPane.showInputDialog(cmp,
                GlobalProperties.getIntlString("Select_the_language"), GlobalProperties.getIntlString("Language"), JOptionPane.INFORMATION_MESSAGE, null,
                allLanguages, initLang);

        String leCode = SanchayLanguages.getLangEncCode(selectedLanguage, currentEncoding);

        return leCode;
    }

    public static String selectEncoding(Component cmp, String prevLangEncCode) {
        String lCode = SanchayLanguages.getLanguageCodeFromLECode(prevLangEncCode);
        String eCode = SanchayLanguages.getEncodingCodeFromLECode(prevLangEncCode);
        Vector allEncodings = SanchayLanguages.getEncodings(lCode).getCopyOfTokens();

        Object encs[] = allEncodings.toArray();

        Object encNames[] = new Object[encs.length];

        for (int i = 0; i < encNames.length; i++) {
            encNames[i] = SanchayLanguages.getEncodingName((String) encs[i]);
        }

        Arrays.sort(encs);

        String initEnc = SanchayLanguages.getEncodingName(eCode);

        String currentLanguage = SanchayLanguages.getLanguageName(prevLangEncCode);

        String selectedEncoding = (String) JOptionPane.showInputDialog(cmp,
                GlobalProperties.getIntlString("Select_the_encoding"), GlobalProperties.getIntlString("Encoding"), JOptionPane.INFORMATION_MESSAGE, null,
                encNames, initEnc);

        String leCode = SanchayLanguages.getLangEncCode(currentLanguage, selectedEncoding);

        return leCode;
    }

    public static String selectInputMethod(Component cmp) {
        Object installedLocaleNames[] = installedLocales.keySet().toArray();
        Arrays.sort(installedLocaleNames);

        previousLocaleName = selectedLocaleName;

        selectedLocaleName = (String) JOptionPane.showInputDialog(cmp,
                GlobalProperties.getIntlString("Select_the_input_method"), GlobalProperties.getIntlString("Input_Method"), JOptionPane.INFORMATION_MESSAGE, null,
                installedLocaleNames, selectedLocaleName);

        return (selectedLocaleName != null && selectedLocaleName.equals("") == false) ? selectedLocaleName : "System input method";
    }

    public static void selectInputMethodForComponent(Component cmp) {
        Object installedLocaleNames[] = installedLocales.keySet().toArray();
        Arrays.sort(installedLocaleNames);

        previousLocaleName = selectedLocaleName;

        selectedLocaleName = (String) JOptionPane.showInputDialog(cmp,
                GlobalProperties.getIntlString("Select_the_input_method"), GlobalProperties.getIntlString("Input_Method"), JOptionPane.INFORMATION_MESSAGE, null,
                installedLocaleNames, selectedLocaleName);

        if (selectedLocaleName != null && selectedLocaleName.equals("") == false) {
            changeInputMethod(cmp, selectedLocaleName);
        } else {
            selectedLocaleName = "System input method";
        }
    }

    public static void changeInputMethod(Component cmp, String localeName) {
        boolean changed = cmp.getInputContext().selectInputMethod((Locale) installedLocales.get(localeName));

        Object imObject = cmp.getInputContext().getInputMethodControlObject();

        if (imObject != null && imObject instanceof GateIM) {
            ((GateIM) imObject).setMapVisible(false);
        }
    }

    public static void changeInputMethod(Component cmp, Locale locale) {
        boolean changed = cmp.getInputContext().selectInputMethod(locale);

        Object imObject = cmp.getInputContext().getInputMethodControlObject();

        if (imObject != null && imObject instanceof GateIM) {
            ((GateIM) imObject).setMapVisible(false);
        }
    }

    public static String switchtInputMethod(Component cmp) {
        if (previousLocaleName.equals(selectedLocaleName))
            return null;

        String tmp = selectedLocaleName;
        selectedLocaleName = previousLocaleName;
        previousLocaleName = tmp;

        return selectedLocaleName;
    }

    public static void switchtInputMethodOfComponent(Component cmp) {
        if (previousLocaleName.equals(selectedLocaleName))
            return;

        changeInputMethod(cmp, previousLocaleName);

        String tmp = selectedLocaleName;
        selectedLocaleName = previousLocaleName;
        previousLocaleName = tmp;
    }

    public static void fillAllLanguages(DefaultComboBoxModel cbm, String selLang) {
        String[] langs = Locale.getISOLanguages();

        for (int i = 0; i < langs.length; i++) {
            String langCode = langs[i];
            Locale l = new Locale(langCode);

            cbm.addElement(l.getDisplayLanguage());
        }

        cbm.setSelectedItem(selLang);
    }

    public static void fillAllLanguages(DefaultComboBoxModel cbm) {
        fillAllLanguages(cbm, GlobalProperties.getIntlString("Hindi"));
    }

    public static void fillAllUnicodeBlocks(DefaultComboBoxModel cbm, Character.UnicodeBlock selBlock) {
        Character.UnicodeBlock prevBlock = null;

        for (int i = Character.MIN_CODE_POINT; i < Character.MAX_CODE_POINT; i++) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(i);

            if (block == null)
                continue;

            if (prevBlock == null || prevBlock.toString().equals(block.toString()) == false)
                cbm.addElement(block);

            prevBlock = block;
        }
    }

    public static void fillAllUnicodeBlocks(DefaultComboBoxModel cbm) {
        fillAllUnicodeBlocks(cbm, Character.UnicodeBlock.DEVANAGARI);
    }

    public static void fillAllScripts(DefaultComboBoxModel cbm, int script) {
        cbm.removeAllElements();

        int rcount = allUnicodeBlocks.getRowCount();

        LinkedHashMap<String, String> allScripts = new LinkedHashMap<String, String>();

        for (int i = 0; i < rcount; i++) {
            String scriptName = (String) allUnicodeBlocks.getValueAt(i, 3);

            if (scriptName.equalsIgnoreCase("Common"))
                continue;

//            int scode = UCharacter.getPropertyValueEnum(UProperty.SCRIPT, scriptName);
//            scriptName = UScript.getName(scode);

            scriptName = UtilityFunctions.toTitleCase(scriptName);

            allScripts.put(scriptName, scriptName);
        }

        Iterator itr = allScripts.keySet().iterator();

        while (itr.hasNext()) {
            String scriptName = (String) itr.next();

            cbm.addElement(scriptName);
        }

        cbm.setSelectedItem(UScript.getName(script));
    }

    public static void fillAllScripts(DefaultComboBoxModel cbm) {
        fillAllScripts(cbm, UScript.DEVANAGARI);
    }

    public static Vector<Character> getSupportedCharacters(Font font) {
        Vector<Character> chars = new Vector<Character>();

        for (int i = Character.MIN_CODE_POINT; i < Character.MAX_CODE_POINT; i++) {
            if (font.canDisplay(i))
                chars.add(new Character((char) i));
        }

        return chars;
    }

    public static Vector<Character> getSupportedCharacters(Character.UnicodeBlock block) {
        Vector<Character> chars = new Vector<Character>();

        String name = block.toString().replaceAll("_", " ").toLowerCase();

        String startCodeStr = (String) allUnicodeBlocks.getValue("Name", name, "RangeStart");
        String endCodeStr = (String) allUnicodeBlocks.getValue("Name", name, "RangeEnd");

        if (startCodeStr != null && startCodeStr.equals("") == false
                && endCodeStr != null && endCodeStr.equals("") == false) {
            int startCode = Integer.parseInt(startCodeStr, 16);
            int endtCode = Integer.parseInt(endCodeStr, 16);

            for (int i = startCode; i <= endtCode; i++) {
                if (UnicodeBlock.of(i) != null)
                    chars.add(new Character((char) i));
            }
        }

        return chars;
    }

    public static UnicodeBlock getUnicodeBlockForScript(String scriptName) {
        Vector<UnicodeBlock> blocks = getUnicodeBlocksForScript(scriptName);

        return blocks.firstElement();
    }

    public static Vector<UnicodeBlock> getUnicodeBlocksForScript(String scriptName) {
        Vector<UnicodeBlock> blocks = new Vector<UnicodeBlock>();

        int rcount = allUnicodeBlocks.getRowCount();

        for (int i = 0; i < rcount; i++) {
            String iscriptName = (String) allUnicodeBlocks.getValueAt(i, 3);

            iscriptName = UtilityFunctions.toTitleCase(iscriptName);

            if (scriptName.equalsIgnoreCase(iscriptName)) {
                String blockName = (String) allUnicodeBlocks.getValueAt(i, 0);

                blocks.add(UnicodeBlock.forName(blockName));
            }
        }

        return blocks;
    }

    public static Vector<Character> getSupportedCharacters(String scriptName) {
        Vector<Character> chars = new Vector<Character>();

        Vector<UnicodeBlock> blocks = getUnicodeBlocksForScript(scriptName);

        int count = blocks.size();

        for (int i = 0; i < count; i++) {
            chars.addAll(getSupportedCharacters(blocks.get(i)));
        }

        return chars;
    }

    public static int getStartCode(Character.UnicodeBlock block) {
        String name = block.toString().replaceAll("_", " ").toLowerCase();

        String startCodeStr = (String) allUnicodeBlocks.getValue("Name", name, "RangeStart");

        if (startCodeStr != null && startCodeStr.equals("") == false)
            return Integer.parseInt(startCodeStr, 16);

        return 0;
    }

    public static int getEndCode(Character.UnicodeBlock block) {
        String name = block.toString().replaceAll("_", " ").toLowerCase();

        String endCodeStr = (String) allUnicodeBlocks.getValue("Name", name, "RangeEnd");

        if (endCodeStr != null && endCodeStr.equals("") == false)
            return Integer.parseInt(endCodeStr, 16);

        return 0;
    }

    public static Font getFontFor(Character.UnicodeBlock block) {
        int startCode = getStartCode(block);
        int endCode = getEndCode(block);

        Font font = getFontFor(startCode, endCode, maxDisplayableFraction);

        return font;
    }

    public static boolean isRTL(String langEnc) {
        String langCode = getLanguageCodeFromLECode(langEnc);

        if (langCode.equals("urd") || langCode.equals("kas")) {
            return true;
        }

        return false;
    }

    public static boolean setTextDirection(Component c, String langEnc) {
        String langCode = getLanguageCodeFromLECode(langEnc);

        if (langCode.equals("urd") || langCode.equals("kas")) {
//            c.set

            return true;
        }

        return false;
    }

    public static Locale getLocale(String langEnc) {
        String langCode = getLanguageCodeFromLECode(langEnc);

        if (langCode == null)
            return Locale.getDefault();

        String twoLetterCode = threeLetter2TwoLetterCodeMap.get(langCode);

        if (twoLetterCode == null)
            return Locale.getDefault();

        Locale loc = new Locale(twoLetterCode);

        return loc;
    }
}
