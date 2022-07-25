/*
 * ParallelMarkupTask.java
 *
 * Created on April 21, 2006, 9:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.resources.aggregate;

import java.io.*;
import java.util.*;
import javax.swing.*;
import sanchay.GlobalProperties;
import sanchay.corpus.parallel.impl.ParallelMarkupAnalyzer;

import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;
import sanchay.table.*;

/**
 *
 * @author anil
 */
public class ParallelMarkupTask extends AggregateResourceImpl implements AggregateResource {

    private String languages[];
    
    private PropertyTokens srcCorpusPT;
    private PropertyTokens tgtCorpusPT;
    private PropertyTokens tgtCorpusUTF8PT;
    private PropertyTokens commentsPT;

    private DefaultComboBoxModel srcTamMarkers;
    private DefaultComboBoxModel tgtTamMarkers;
    private LinkedHashMap srcTMIndex;
    private LinkedHashMap tgtTMIndex;
    
    // Elements values are SanchayTables with sentence number as the key
    // and SanchayTable containing SL and TL marker indices
    // in each row
    private LinkedHashMap markerMappingTables; // Indices

    // Parallel to above two PropertyTokens
    // Elements values are SanchayTables with sentence number as the key
    // and SanchayTable containing start position, end position and marker index
    // in each row
    private LinkedHashMap srcSenMarkups;
    private LinkedHashMap tgtSenMarkups;
    
    private int currentPosition;

    /** Creates a new instance of ParallelMarkupTask */
    public ParallelMarkupTask() {
	this("", "", null, "");
    }

    public ParallelMarkupTask(String taskFile, String taskCharset, String langs[], String nm) {
	super(taskFile, taskCharset, (langs == null ? GlobalProperties.getIntlString("hin::utf8") : langs[0]), nm);
	
	languages = langs;
	
	if(languages == null)
	{
	    languages = new String[]{GlobalProperties.getIntlString("eng::utf8"), GlobalProperties.getIntlString("hin::utf8")};
	}
    }
    
    public String[] getLanguages()
    {
	return languages;
    }

    public int read(String f, String charset) throws FileNotFoundException, IOException
    {
	filePath = f;
	this.charset = charset;
	
	KeyValueProperties kvTaskProps = new KeyValueProperties(f, charset);
	setProperties(kvTaskProps);

	srcCorpusPT = new PropertyTokens(kvTaskProps.getPropertyValue("SLCorpusFile"), kvTaskProps.getPropertyValue("SLCorpusCharset"));
	tgtCorpusPT = new PropertyTokens(kvTaskProps.getPropertyValue("TLCorpusFile"), kvTaskProps.getPropertyValue("TLCorpusCharset"));
	tgtCorpusUTF8PT = new PropertyTokens(kvTaskProps.getPropertyValue("TLCorpusUTF8File"), GlobalProperties.getIntlString("UTF-8"));

	PropertyTokens srcTMPT = new PropertyTokens(kvTaskProps.getPropertyValue("SLTMFile"), kvTaskProps.getPropertyValue("SLTMCharset"));
	PropertyTokens tgtTMPT = new PropertyTokens(kvTaskProps.getPropertyValue("TLTMFile"), kvTaskProps.getPropertyValue("TLTMCharset"));

	srcTamMarkers = new DefaultComboBoxModel(srcTMPT.getCopyOfTokens());
	tgtTamMarkers = new DefaultComboBoxModel(tgtTMPT.getCopyOfTokens());

	srcTMIndex = new LinkedHashMap(srcTamMarkers.getSize());
	tgtTMIndex = new LinkedHashMap(tgtTamMarkers.getSize());

	int count = srcTMPT.countTokens();
	for(int i = 0; i < count; i++)
	    srcTMIndex.put(srcTMPT.getToken(i), Integer.toString(i));

	count = tgtTMPT.countTokens();
	for(int i = 0; i < count; i++)
	    tgtTMIndex.put(tgtTMPT.getToken(i), Integer.toString(i));

	int senCount = srcCorpusPT.countTokens();
	if(senCount > 0 && tgtCorpusPT.countTokens() > 0
		&& senCount == tgtCorpusPT.countTokens())
	{
	    currentPosition = 1;
/*                srcSentence = srcCorpusPT.getToken(currentPosition - 1);
	    tgtSentence = tgtCorpusPT.getToken(currentPosition - 1);
	    tgtUTF8Sentence = tgtCorpusUTF8PT.getToken(currentPosition - 1);*/

/*                srcSenJTextArea.setText(srcSentence);
	    tgtSenJTextArea.setText(tgtSentence);
	    tgtSenUTF8JTextArea.setText(tgtUTF8Sentence);*/

	    try
	    {
		srcSenMarkups = SanchayTableModel.readMany(kvTaskProps.getPropertyValue("SLCorpusFile") + ".marked", GlobalProperties.getIntlString("UTF-8"));
		tgtSenMarkups = SanchayTableModel.readMany(kvTaskProps.getPropertyValue("TLCorpusFile") + ".marked", GlobalProperties.getIntlString("UTF-8"));
		markerMappingTables = SanchayTableModel.readMany(f + ".mapping", GlobalProperties.getIntlString("UTF-8"));

		commentsPT = new PropertyTokens(kvTaskProps.getPropertyValue("TaskPropFile") + ".comments", GlobalProperties.getIntlString("UTF-8"));
	    }
	    catch(FileNotFoundException e)
	    {
		srcSenMarkups = new LinkedHashMap(senCount);
		tgtSenMarkups = new LinkedHashMap(senCount);
		markerMappingTables = new LinkedHashMap(senCount);

		commentsPT = new PropertyTokens(senCount);

		for(int i = 1; i <= senCount; i++)
		{
		    srcSenMarkups.put(Integer.toString(i), new SanchayTableModel(0, 3));
		    tgtSenMarkups.put(Integer.toString(i), new SanchayTableModel(0, 3));
		    markerMappingTables.put(Integer.toString(i), new SanchayTableModel(0, 4));

		    commentsPT.addToken("");
		}
	    }
	}
	else
	    throw new IOException(GlobalProperties.getIntlString("Error_in_task_properties_for_the_task:_") + getName());
	
	return 0;
    }
    
    public int save(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
	PropertyTokens spt = PropertyTokens.getPropertyTokens(srcTamMarkers);
	PropertyTokens tpt = PropertyTokens.getPropertyTokens(tgtTamMarkers);

	KeyValueProperties kvTaskProps = (KeyValueProperties) getProperties();

	spt.save(kvTaskProps.getPropertyValue("SLTMFile"), kvTaskProps.getPropertyValue("SLTMCharset"));
	tpt.save(kvTaskProps.getPropertyValue("TLTMFile"), kvTaskProps.getPropertyValue("TLTMCharset"));

	kvTaskProps.addProperty("CurrentPosition", Integer.toString(currentPosition + 1));
	kvTaskProps.save(kvTaskProps.getPropertyValue("TaskPropFile"), kvTaskProps.getPropertyValue("TaskPropCharset"));

	int count = commentsPT.countTokens();
	for(int i = 0; i < count; i++)
	{
	    String cmt = commentsPT.getToken(i);
	    String spstr[] = cmt.split("[\n]");

	    cmt = "";
	    for(int j = 0; j < spstr.length; j++)
	    {
		if(j < spstr.length - 1)
		    cmt += spstr[j] + " ";
		else
		    cmt += spstr[j];
	    }

	    commentsPT.modifyToken(cmt, i);
	}

	commentsPT.save(kvTaskProps.getPropertyValue("TaskPropFile") + ".comments", GlobalProperties.getIntlString("UTF-8"));

	SanchayTableModel.saveMany(srcSenMarkups, kvTaskProps.getPropertyValue("SLCorpusFile") + ".marked", GlobalProperties.getIntlString("UTF-8"));
	SanchayTableModel.saveMany(tgtSenMarkups, kvTaskProps.getPropertyValue("TLCorpusFile") + ".marked", GlobalProperties.getIntlString("UTF-8"));
	SanchayTableModel.saveMany(markerMappingTables, kvTaskProps.getPropertyValue("TaskPropFile") + ".mapping", GlobalProperties.getIntlString("UTF-8"));

	return 0;
    }

    public PropertyTokens getSrcCorpusPT() {
        return srcCorpusPT;
    }

    public PropertyTokens getTgtCorpusPT() {
        return tgtCorpusPT;
    }

    public PropertyTokens getTgtCorpusUTF8PT() {
        return tgtCorpusUTF8PT;
    }

    public PropertyTokens getCommentsPT() {
        return commentsPT;
    }

    public DefaultComboBoxModel getSrcTamMarkers() {
        return srcTamMarkers;
    }

    public DefaultComboBoxModel getTgtTamMarkers() {
        return tgtTamMarkers;
    }

    public LinkedHashMap getSrcTMIndex() {
        return srcTMIndex;
    }

    public LinkedHashMap getTgtTMIndex() {
        return tgtTMIndex;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int p) {
        currentPosition = p;
    }

    public LinkedHashMap getMarkerMappingTables() {
        return markerMappingTables;
    }

    public LinkedHashMap getSrcSenMarkups() {
        return srcSenMarkups;
    }

    public LinkedHashMap getTgtSenMarkups() {
        return tgtSenMarkups;
    }
    
    public SanchayTableModel indexToStringMappingTable(SanchayTableModel indexTable)
    {
        int rcount = indexTable.getRowCount();
        int ccount = indexTable.getColumnCount(); // Must be four

        SanchayTableModel stringTable = new SanchayTableModel(rcount, ccount - 2);
        
        for(int i = 0; i <  rcount; i++)
        {
            String ind1 = (String) indexTable.getValueAt(i, 0);
            String ind2 = (String) indexTable.getValueAt(i, 1);
            String ind3 = (String) indexTable.getValueAt(i, 2);
            String ind4 = (String) indexTable.getValueAt(i, 3);
            
            String string1 = "";
            String string2 = "";
            
            if(ind1.equals("-1") == true || ind2.equals("-1") == true)
                string1 = GlobalProperties.getIntlString("NONE");
            else
            {
                int index = Integer.parseInt(ind2);
                string1 = ind1 + "::" + (String) srcTamMarkers.getElementAt(index);
            }

            if(ind3.equals("-1") == true || ind4.equals("-1") == true)
                string2 = GlobalProperties.getIntlString("NONE");
            else
            {
                int index = Integer.parseInt(ind4);
                string2 = ind3 + "::" + (String) tgtTamMarkers.getElementAt(index);
            }

            stringTable.setValueAt(string1, i, 0);
            stringTable.setValueAt(string2, i, 1);
        }
        
        return stringTable;
    }
    
    public SanchayTableModel stringToIndexMappingTable(SanchayTableModel stringTable)
    {
        int rcount = stringTable.getRowCount();
        int ccount = stringTable.getColumnCount(); // Must be two

        SanchayTableModel indexTable = new SanchayTableModel(rcount, ccount + 2);
        
        for(int i = 0; i < rcount; i++)
        {
            String string1 = (String) stringTable.getValueAt(i, 0);
            String string2 = (String) stringTable.getValueAt(i, 1);

            String ind1 = ""; // marker index in SL sentence
            String ind2 = ""; // marker index in SL marker list
            String ind3 = ""; // marker index in TL sentence
            String ind4 = ""; // marker index in TL marker list

            if(string1.equals(GlobalProperties.getIntlString("NONE")) == true)
            {
                ind1 = "-1";
                ind2 = "-1";
            }
            else
            {
                String parts[] = string1.split("::");
                ind1 = parts[0];
                ind2 = (String) srcTMIndex.get(parts[1]);
            }

            if(string2.equals(GlobalProperties.getIntlString("NONE")) == true)
            {
                ind3 = "-1";
                ind4 = "-1";
            }
            else
            {
                String parts[] = string2.split("::");
                ind3 = parts[0];
                ind4 = (String) tgtTMIndex.get(parts[1]);
            }
        
            indexTable.setValueAt(ind1, i, 0);
            indexTable.setValueAt(ind2, i, 1);
            indexTable.setValueAt(ind3, i, 2);
            indexTable.setValueAt(ind4, i, 3);
        }
        
        return indexTable;
    }
    
    public int getSentenceCount()
    {
	return srcCorpusPT.countTokens();
    }
    
    public Vector getDifferentMarkupExtents(ParallelMarkupTask task, int lang)
    {
	int count = Math.max(getSentenceCount(), task.getSentenceCount());
	
	Vector diff = new Vector(0, 5);
	
	for (int i = 0; i < count; i++)
	{
	    SanchayTableModel ret = areDifferentMarkupExtents(task, i, lang);
	    
	    if(ret != null)
		diff.add(ret);
	}
	
	return diff;
    }
    
    // Return null if same
    public SanchayTableModel areDifferentMarkupExtents(ParallelMarkupTask task, int sentence, int lang)
    {
	SanchayTableModel thisMarkup = null;
	SanchayTableModel otherMarkup = null;
	
	if(lang == ParallelMarkupAnalyzer.SL)
	{
	    if(sentence < getSentenceCount())
		thisMarkup = (SanchayTableModel) srcSenMarkups.get(new Integer(sentence + 1));

	    if(sentence < task.getSentenceCount())
		otherMarkup = (SanchayTableModel) task.getSrcSenMarkups().get(new Integer(sentence + 1));
	}
	else if(lang == ParallelMarkupAnalyzer.TL)
	{
	    if(sentence < getSentenceCount())
		thisMarkup = (SanchayTableModel) tgtSenMarkups.get(new Integer(sentence + 1));
	    
	    if(sentence < task.getSentenceCount())
		otherMarkup = (SanchayTableModel) task.getTgtSenMarkups().get(new Integer(sentence + 1));
	}
	
	if(thisMarkup == null && otherMarkup == null)
	    return null;

	if(thisMarkup == null || otherMarkup == null)
	    return otherMarkup;
	
	int ocount = otherMarkup.getRowCount();
	for (int i = 0; i < ocount; i++)
	{
	    int tcount = thisMarkup.getRowCount();
	    boolean found = false;
	    
	    for (int j = 0; j < tcount; j++)
	    {
		if(((String) otherMarkup.getValueAt(i, 0)).equalsIgnoreCase((String) thisMarkup.getValueAt(j, 0)) == true
			&& ((String) otherMarkup.getValueAt(i, 1)).equalsIgnoreCase((String) thisMarkup.getValueAt(j, 1)) == true)
		    found = true;
	    }

	    if(found == false)
		return otherMarkup;
	}
	
	return null;
    }
    
    public Vector getDifferentMarkups(ParallelMarkupTask task, int lang)
    {
	int count = Math.max(getSentenceCount(), task.getSentenceCount());
	
	Vector diff = new Vector(0, 5);
	
	for (int i = 0; i < count; i++)
	{
	    SanchayTableModel ret = areDifferentMarkups(task, i, lang);
	    
	    if(ret != null)
		diff.add(ret);
	}
	
	return diff;
    }
    
    public SanchayTableModel areDifferentMarkups(ParallelMarkupTask task, int sentence, int lang)
    {
	SanchayTableModel thisMarkup = null;
	SanchayTableModel otherMarkup = null;
	
	if(lang == ParallelMarkupAnalyzer.SL)
	{
	    if(sentence < getSentenceCount())
		thisMarkup = (SanchayTableModel) srcSenMarkups.get(new Integer(sentence + 1));
	    
	    if(sentence < task.getSentenceCount())
		otherMarkup = (SanchayTableModel) task.getSrcSenMarkups().get(new Integer(sentence + 1));
	}
	else if(lang == ParallelMarkupAnalyzer.TL)
	{
	    if(sentence < getSentenceCount())
		thisMarkup = (SanchayTableModel) tgtSenMarkups.get(new Integer(sentence + 1));
	    
	    if(sentence < task.getSentenceCount())
		otherMarkup = (SanchayTableModel) task.getTgtSenMarkups().get(new Integer(sentence + 1));
	}
	
	if(thisMarkup == null && otherMarkup == null)
	    return null;
	
	if(thisMarkup == null || otherMarkup == null)
	    return otherMarkup;
	
	int ocount = otherMarkup.getRowCount();
	for (int i = 0; i < ocount; i++)
	{
	    int tcount = thisMarkup.getRowCount();
	    boolean found = false;
	    
	    for (int j = 0; j < tcount; j++)
	    {
		if(((String) otherMarkup.getValueAt(i, 0)).equalsIgnoreCase((String) thisMarkup.getValueAt(j, 0)) == true
			&& ((String) otherMarkup.getValueAt(i, 1)).equalsIgnoreCase((String) thisMarkup.getValueAt(j, 1)) == true
			&& ((String) otherMarkup.getValueAt(i, 2)).equalsIgnoreCase((String) thisMarkup.getValueAt(j, 2)) == true)
		    found = true;
	    }

	    if(found == false)
		return otherMarkup;
	}
	
	return null;
    }
    
    public Vector getDifferentMarkerMappings(ParallelMarkupTask task)
    {
	int count = Math.max(getSentenceCount(), task.getSentenceCount());
	
	Vector diff = new Vector(0, 5);
	
	for (int i = 0; i < count; i++)
	{
	    SanchayTableModel ret = areDifferentMarkerMappings(task, i);
	    
	    if(ret != null)
		diff.add(ret);
	}
	
	return diff;
    }
    
    public SanchayTableModel areDifferentMarkerMappings(ParallelMarkupTask task, int sentence)
    {
	SanchayTableModel thisMapping = (SanchayTableModel) markerMappingTables.get(new Integer(sentence));
	SanchayTableModel otherMapping = (SanchayTableModel) task.getMarkerMappingTables().get(new Integer(sentence));
	
	if(thisMapping == null && otherMapping == null)
	    return null;
	
	if(thisMapping == null || otherMapping == null)
	    return otherMapping;
	
	int ocount = otherMapping.getRowCount();
	for (int i = 0; i < ocount; i++)
	{
	    int tcount = thisMapping.getRowCount();
	    boolean found = false;
	    
	    for (int j = 0; j < tcount; j++)
	    {
		if(((String) otherMapping.getValueAt(i, 0)).equalsIgnoreCase((String) thisMapping.getValueAt(j, 0)) == true
			&& ((String) otherMapping.getValueAt(i, 1)).equalsIgnoreCase((String) thisMapping.getValueAt(j, 1)) == true
			&& ((String) otherMapping.getValueAt(i, 2)).equalsIgnoreCase((String) thisMapping.getValueAt(j, 2)) == true
			&& ((String) otherMapping.getValueAt(i, 3)).equalsIgnoreCase((String) thisMapping.getValueAt(j, 3)) == true)
		    found = true;
	    }

	    if(found == false)
		return otherMapping;
	}
	
	return null;
    }
    
    public Vector getDifferentMarkers(ParallelMarkupTask task, int lang)
    {
	PropertyTokens thisMarkerList = null;
	PropertyTokens otherMarkerList = null;
	
	Vector diff = new Vector(0, 3);
	
	if(lang == ParallelMarkupAnalyzer.SL)
	{
	    thisMarkerList = getSrcCorpusPT();
	    otherMarkerList = task.getSrcCorpusPT();
	}
	else if(lang == ParallelMarkupAnalyzer.TL)
	{
	    thisMarkerList = getTgtCorpusPT();
	    otherMarkerList = task.getTgtCorpusPT();
	}
	
	int ocount = otherMarkerList.countTokens();
	for (int i = 0; i < ocount; i++)
	{
	    int tcount = thisMarkerList.countTokens();
	    boolean found = false;
	    
	    for (int j = 0; j < tcount; j++)
	    {
		if(otherMarkerList.getToken(i).equals(thisMarkerList.getToken(j)) == true)
		    found = true;
	    }

	    if(found == false)
		diff.add(otherMarkerList.getToken(i));
	}
	
	return diff;
    }
    
    public Vector getDifferentComments(ParallelMarkupTask task)
    {
	int count = Math.max(getSentenceCount(), task.getSentenceCount());
	
	Vector diff = new Vector(0, 5);
	
	for (int i = 0; i < count; i++)
	{
	    String ret = areDifferentComments(task, i);
	    
	    if(ret != null)
		diff.add(ret);
	}
	
	return diff;
    }
    
    public String areDifferentComments(ParallelMarkupTask task, int sentence)
    {
	if(sentence >= getCommentsPT().countTokens() && sentence >= task.getCommentsPT().countTokens())
	{
	    System.out.println(getName());
	    return null;
	}

	if(getCommentsPT().countTokens() != task.getCommentsPT().countTokens())
	{
	    if(sentence >= getCommentsPT().countTokens())
		return task.getCommentsPT().getToken(sentence);
	    else if(sentence >= task.getCommentsPT().countTokens())
		return getCommentsPT().getToken(sentence);
	}
	

	if(((String) task.getCommentsPT().getToken(sentence)).equals("")
		&& ((String) getCommentsPT().getToken(sentence)).equals(""))
	    return null;
	
	if(task.getCommentsPT().getToken(sentence).equalsIgnoreCase(getCommentsPT().getToken(sentence)))
	    return null;
	
	return task.getCommentsPT().getToken(sentence);
    }
}
