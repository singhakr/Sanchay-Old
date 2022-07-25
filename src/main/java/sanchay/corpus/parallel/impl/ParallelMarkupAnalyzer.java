/*
 * ParallelMarkupTaskComparison.java
 *
 * Created on April 22, 2006, 12:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.parallel.impl;

import java.util.*;
import javax.swing.DefaultComboBoxModel;
import sanchay.GlobalProperties;
import sanchay.properties.PropertyTokens;

import sanchay.resources.aggregate.ParallelMarkupTask;
import sanchay.table.*;

/**
 *
 * @author Anil Kumar Singh
 */
public class ParallelMarkupAnalyzer {

    private int numLanguages;

    // The first one will be taken as the reference, against which the others will be compared.
    private ParallelMarkupTask tasks[];

    private SanchayTableModel comparisonSummaryTable;
    private SanchayTableModel listSummaryTable;
    private SanchayTableModel querySummaryTable;

    private SanchayTableModel summaryAnalysis;

    private int[] overallComparison;

    private SanchayTableModel markupExtentComparison[];
    private SanchayTableModel markupComparison[];
    private SanchayTableModel markerMappingComparison;
    private SanchayTableModel markerListComparison[];
    private SanchayTableModel comments;

    private SanchayTableModel usedMarkerList;
    private SanchayTableModel unusedMarkerList;
    private SanchayTableModel markerContextsList;
    private SanchayTableModel wordList;
    private SanchayTableModel markupList;
    private SanchayTableModel markupMappingList;
    private SanchayTableModel markerMappingList;
    private SanchayTableModel sentenceList;
    
    // The last dimension will be for language: could be inapplicable for some comparisons, like for mappings
    private boolean different[][][];
    
    private boolean compared[][];
    private boolean listed[];
    
    // Comparison types:
    public static final int OVERALL_ANNOTATION = 0;
    public static final int MARKUP_EXTENT = 1;
    public static final int MARKUP = 2;
    public static final int MARKER_MAPPING = 3;
    public static final int MARKER_LIST = 4;
    public static final int COMMENTS = 5;
    public static final int _COMPARISON_LAST_ = 6;
    
    // Analysis (actually, just listing) types:
    public static final int OVERALL_LISTING = 0;
    public static final int USED_MARKER_LIST = 1;
    public static final int UNUSED_MARKER_LIST = 2;
    public static final int WORD_LIST = 3;
    public static final int MARKUP_LIST = 4;
    public static final int MARKUP_MAPPING_LIST = 5;
    public static final int MARKER_MAPPING_LIST = 6;
    public static final int SENTENCE_LIST = 7;
    public static final int _LISTING_LAST_ = 8;

    // Querying types:
    public static final int UNDEF_QUERY = 0;
    public static final int MARKERS_QUERY = 1; // For a particular sequence of words
    public static final int MARKED_EXTENTS_QUERY = 2; // For a particular marker
    public static final int MARKER_CONTEXTS_QUERY = 3; // For a particular marker
    public static final int MAPPING_CONTEXTS_QUERY = 4; // For a particular marker mapping
    public static final int MARKER_TRANSLATIONS_QUERY = 5; // For a particular marker
    public static final int _QUERY_LAST_ = 6;
    
    // Languages of the parallel corpus
    public static final int UL = -1; // Undefined
    public static final int SL = 0; // Source
    public static final int TL = 1; // Target
    
    /**
     * Three major category of opeations can be performed:<br>
     * (a) Comparison<br>
     * (b) Listing<br>
     * (c) Basic querying<br>
     * <br>
     * Comparison operations:<br>
     * 1. Source language (SL) markup<br>
     * 2. Target language (TL) markup<br>
     * 3. SL-TL marker mapping<br>
     * 4. SL marker list<br>
     * 5. TL marker list<br>
     * <br>
     * Listing (over all the tasks) operations:<br>
     * 1. Complete sorted list of used unique SL markers for all the tasks<br>
     * 2. Complete sorted list of used unique TL markers for all the tasks<br>
     * 3. Complete sorted list of unused unique SL markers for all the tasks<br>
     * 4. Complete sorted list of unused unique TL markers for all the tasks<br>
     * <br>
     * Querying operations:<br>
     * 5. Contexts for a particular SL marker<br>
     * 6. Contexts for a particular TL marker<br>
     * 7. Possible translations of a particular SL marker<br>
     * 8. Possible translations of a particular TL marker<br>
     * 9. Contexts for a particular SL-TL mapping<br>
     */
    
    /** Creates a new instance of ParallelMarkupTaskComparison */
    public ParallelMarkupAnalyzer(ParallelMarkupTask tasks[], int numLanguages /* Currently only 2 is possible */) {
//	this.numLanguages = numLanguages;
	this.numLanguages = 2;
	
	different = new boolean[tasks.length][_COMPARISON_LAST_][numLanguages];
	compared = new boolean[_COMPARISON_LAST_][numLanguages];
	listed = new boolean[_LISTING_LAST_];
	
	this.tasks = tasks;

	markupExtentComparison = new SanchayTableModel[numLanguages];
	markupComparison = new SanchayTableModel[numLanguages];
	markerListComparison = new SanchayTableModel[numLanguages];
    }
    
    public int countTasks()
    {
	return tasks.length;
    }
    
    public ParallelMarkupTask[] getTasks()
    {
	return tasks;
    }
    
    public ParallelMarkupTask getTask(int i)
    {
	if(i < 0 || i >= countTasks())
	    return null;

	return tasks[i];
    }
    
    private void compare(int comparisonType, int lang)
    {
	switch(comparisonType)
	{
	    case OVERALL_ANNOTATION:
		if(compared[OVERALL_ANNOTATION][0] == false)
		{
		    compareOverall();
		    compared[OVERALL_ANNOTATION][0] = true;
		}
		break;
	    case MARKUP_EXTENT:
		if(compared[MARKUP_EXTENT][lang] == false)
		{
		    compareMarkupExtent(lang);
		    compared[MARKUP_EXTENT][lang] = true;
		}
		break;
	    case MARKUP:
		if(compared[MARKUP][lang] == false)
		{
		    compareMarkup(lang);
		    compared[MARKUP][lang] = true;
		}
		break;
	    case MARKER_MAPPING:
		if(compared[MARKER_MAPPING][0] == false)
		{
		    compareMarkerMapping();
		    compared[MARKER_MAPPING][0] = true;
		}
		break;
	    case MARKER_LIST:
		if(compared[MARKER_LIST][lang] == false)
		{
		    compareMarkerList(lang);
		    compared[MARKER_LIST][lang] = true;
		}
		break;
	    case COMMENTS:
		if(compared[COMMENTS][0] == false)
		{
		    compareComments();
		    compared[COMMENTS][0] = true;
		}
		break;
	}    
    }
    
    private void list(int listType)
    {
	switch(listType)
	{
	    case OVERALL_LISTING:
		if(listed[OVERALL_LISTING] == false)
		{
		    listOverall();
		    listed[OVERALL_LISTING] = true;
		}
		break;
	    case USED_MARKER_LIST:
		if(listed[USED_MARKER_LIST] == false)
		{
		    listUsedMarkers();
		    listed[USED_MARKER_LIST] = true;
		}
		break;
	    case UNUSED_MARKER_LIST:
		if(listed[UNUSED_MARKER_LIST] == false)
		{
		    listUnusedMarkers();
		    listed[UNUSED_MARKER_LIST] = true;
		}
		break;
	    case WORD_LIST:
		if(listed[WORD_LIST] == false)
		{
		    listWords();
		    listed[WORD_LIST] = true;
		}
		break;
	    case MARKUP_LIST:
		if(listed[MARKUP_LIST] == false)
		{
		    listMarkups();
		    listed[MARKUP_LIST] = true;
		}
		break;
	    case MARKUP_MAPPING_LIST:
		if(listed[MARKUP_MAPPING_LIST] == false)
		{
		    listMarkupMappings();
		    listed[MARKUP_MAPPING_LIST] = true;
		}
		break;
	    case MARKER_MAPPING_LIST:
		if(listed[MARKER_MAPPING_LIST] == false)
		{
		    listMarkerMappings();
		    listed[MARKER_MAPPING_LIST] = true;
		}
		break;
	    case SENTENCE_LIST:
		if(listed[SENTENCE_LIST] == false)
		{
		    listSentences();
		    listed[SENTENCE_LIST] = true;
		}
		break;
	}    
    }
    
    private void query(int queryType, Object data1, Object data2, int slang, int tlang, int window, int minFreq, int maxFreq)
    {
	switch(queryType)
	{
	    case MARKERS_QUERY:
		queryMarkers((String) data1, slang, minFreq, maxFreq); // for a sequence of words
		break;
	    case MARKED_EXTENTS_QUERY:
		queryMarkedExtents((String) data1, slang, minFreq, maxFreq); // for a marker
		break;
	    case MARKER_CONTEXTS_QUERY:
		queryMarkerContexts((String) data1, slang, window, minFreq, maxFreq); // for a marker
		break;
	    case MAPPING_CONTEXTS_QUERY:
		queryMappingContexts((Vector) data1, window, minFreq, maxFreq); // for a marker mapping
		break;
	    case MARKER_TRANSLATIONS_QUERY:
		queryMarkerTranslations((String) data1, slang, tlang, minFreq, maxFreq); // for a marker
		break;
	}    
    }
    
    public String getComparisonTitle(int comparisonType)
    {
	switch(comparisonType)
	{
	    case OVERALL_ANNOTATION:
		return GlobalProperties.getIntlString("Overall_Annotation");
	    case MARKUP_EXTENT:
		return GlobalProperties.getIntlString("Markup_Extent");
	    case MARKUP:
		return GlobalProperties.getIntlString("Markup");
	    case MARKER_MAPPING:
		return GlobalProperties.getIntlString("Marker_Mapping");
	    case MARKER_LIST:
		return GlobalProperties.getIntlString("Marker_List");
	    case COMMENTS:
		return GlobalProperties.getIntlString("Comments");
	}    
	
	return null;
    }
    
    public String getListingTitle(int listType)
    {
	switch(listType)
	{
	    case OVERALL_LISTING:
		return GlobalProperties.getIntlString("Overall_List");
	    case USED_MARKER_LIST:
		return GlobalProperties.getIntlString("Used_Marker_List");
	    case UNUSED_MARKER_LIST:
		return GlobalProperties.getIntlString("Unused_Marker_List");
	    case WORD_LIST:
		return GlobalProperties.getIntlString("Word_List");
	    case MARKUP_LIST:
		return GlobalProperties.getIntlString("Markup_List");
	    case MARKUP_MAPPING_LIST:
		return GlobalProperties.getIntlString("Markup_Mapping_List");
	    case MARKER_MAPPING_LIST:
		return GlobalProperties.getIntlString("Marker_Mapping_List");
	    case SENTENCE_LIST:
		return GlobalProperties.getIntlString("Sentence_List");
	}    
	
	return null;
    }
    
    public String getQueryTitle(int queryType)
    {
	switch(queryType)
	{
	    case MARKERS_QUERY:
		return GlobalProperties.getIntlString("Marker_Query");
	    case MARKED_EXTENTS_QUERY:
		return GlobalProperties.getIntlString("Marked_Extent_Query");
	    case MARKER_CONTEXTS_QUERY:
		return GlobalProperties.getIntlString("Marker_Context_Query");
	    case MAPPING_CONTEXTS_QUERY:
		return GlobalProperties.getIntlString("Mapping_Context__Query");
	    case MARKER_TRANSLATIONS_QUERY:
		return GlobalProperties.getIntlString("Marker_Translation_Query");
	}    
	
	return null;
    }
    
    private boolean isLanguageSpecificComparison(int comparisonType)
    {
	switch(comparisonType)
	{
	    case OVERALL_ANNOTATION:
		return false;
	    case MARKUP_EXTENT:
		return true;
	    case MARKUP:
		return true;
	    case MARKER_MAPPING:
		return false;
	    case MARKER_LIST:
		return true;
	    case COMMENTS:
		return false;
	}    

	return false;
    }
    
    private boolean isLanguageSpecificQuery(int queryType)
    {
	switch(queryType)
	{
	    case MARKERS_QUERY:
		return true;
	    case MARKED_EXTENTS_QUERY:
		return true;
	    case MARKER_CONTEXTS_QUERY:
		return true;
	    case MAPPING_CONTEXTS_QUERY:
		return false;
	    case MARKER_TRANSLATIONS_QUERY:
		return true;
	}    

	return false;
    }
    
    public boolean areDifferent(int comparisonType, int lang)
    {
	if(tasks != null || tasks.length == 1)
	    return false;
	
	compare(comparisonType, lang);

	for (int i = 1; i < tasks.length; i++)
	{
	    if(isDifferent(i, comparisonType, lang) == true)
		return true;
	}

	return false;
    }
    
    public boolean isDifferent(int taskIndex /* except the first, reference node */, int comparisonType, int lang)
    {
	compare(comparisonType, lang);
	
	return different[taskIndex][comparisonType][lang];
    }
    
    public SanchayTableModel comparisonSummary()
    {
	if(comparisonSummaryTable != null)
	    return comparisonSummaryTable;

	compare(OVERALL_ANNOTATION, UL);

	comparisonSummaryTable = newComparisonTableModel();
	
	comparisonSummaryTable.setColumnIdentifier(0, GlobalProperties.getIntlString("Comparison_Type"));
	
	for(int i = 0; i < _COMPARISON_LAST_; i++)
	{
	    int lcount = 0;
	    
	    if(isLanguageSpecificComparison(i))
		lcount = numLanguages;
	    else
		lcount = 1;

	    for (int l = 0; l < lcount; l++)
	    {
		comparisonSummaryTable.addRow();
		int row = comparisonSummaryTable.getRowCount() - 1;

		for(int j = 1; j < tasks.length; j++)
		{
		    if(different[j][i][l] == false)
			comparisonSummaryTable.setValueAt(GlobalProperties.getIntlString("Same"), row, j + 1);
		    else
			comparisonSummaryTable.setValueAt(GlobalProperties.getIntlString("Different"), row, j + 1);
		}

		if(lcount > 1)
		    comparisonSummaryTable.setValueAt(getComparisonTitle(i) + "(" + tasks[0].getLanguages()[l] + ")", row, 0);
		else
		    comparisonSummaryTable.setValueAt(getComparisonTitle(i), row, 0);
	    }
	}
	
	if(tasks.length >= 1)
	    comparisonSummaryTable.removeColumn(1);
	
	return comparisonSummaryTable;
    }
    
    public SanchayTableModel listSummary()
    {
	if(listSummaryTable != null)
	    return listSummaryTable;

	list(OVERALL_LISTING);

	listSummaryTable = newComparisonTableModel();
	
	listSummaryTable.setColumnIdentifier(0, GlobalProperties.getIntlString("Listing_Type"));
	
	for(int i = 0; i < _LISTING_LAST_; i++)
	{
	    listSummaryTable.addRow();
	    int row = listSummaryTable.getRowCount() - 1;

	    listSummaryTable.setValueAt(getListingTitle(i), row, 0);
	}

	while(listSummaryTable.getColumnCount() > 1)
	{
	    listSummaryTable.removeColumn(1);
	}
	
	return listSummaryTable;
    }
    
    public SanchayTableModel querySummary()
    {
	if(querySummaryTable != null)
	    return querySummaryTable;

	querySummaryTable = newComparisonTableModel();
	
	querySummaryTable.setColumnIdentifier(0, GlobalProperties.getIntlString("Query_Type"));
	
	for(int i = 1; i < _QUERY_LAST_; i++)
	{
	    querySummaryTable.addRow();
	    int row = querySummaryTable.getRowCount() - 1;

	    querySummaryTable.setValueAt(getQueryTitle(i), row, 0);
	}

	while(querySummaryTable.getColumnCount() > 1)
	{
	    querySummaryTable.removeColumn(1);
	}
	
	return querySummaryTable;
    }
    
    private SanchayTableModel newComparisonTableModel()
    {
	SanchayTableModel sanchayTableModel = new SanchayTableModel(0, tasks.length + 1);
	sanchayTableModel.setEditable(false);

	String colNames[] = new String[tasks.length + 1];
	colNames[0] = GlobalProperties.getIntlString("Annotation");
	
	for(int i = 0; i < tasks.length; i++)
	{
	    String name = tasks[i].getName();
	    
	    if(name == null || name.equals(""))
		colNames[i + 1] = "Task-" + (i + 1);
	    else
		colNames[i + 1] = name;
	}
	
	sanchayTableModel.setColumnIdentifiers(colNames);
	
	return sanchayTableModel;
    }
    
    private SanchayTableModel newListTableModel()
    {
	SanchayTableModel sanchayTableModel = new SanchayTableModel(0, 2 * numLanguages);
	sanchayTableModel.setEditable(false);

	String colNames[] = new String[2 * numLanguages];
	
	for(int i = 0; i < 2 * numLanguages; i++)
	{
	    if(i % 2 == 0)
		colNames[i] = tasks[0].getLanguages()[i/2];
	    else
		colNames[i] = GlobalProperties.getIntlString("Frequency_(") + tasks[0].getLanguages()[i/2] + ")";
	}
	
	sanchayTableModel.setColumnIdentifiers(colNames);
	
	return sanchayTableModel;
    }
    
    // Comparison
    public int[] compareOverall()
    {
	if(compared[OVERALL_ANNOTATION][0] == true)
	    return overallComparison;

	for(int i = 1; i < tasks.length; i++)
	{
	    boolean diff = false;
	    for(int j = 1; j < _COMPARISON_LAST_; j++)
	    {
		int lcount = 0;

		if(isLanguageSpecificComparison(j))
		    lcount = numLanguages;
		else
		    lcount = 1;

		for (int l = 0; l < lcount; l++)
		{
		    compare(j, l);

		    if(isDifferent(i, j, l) == true)
			diff = true;
		}
	    }

	    if(diff == true)
		different[i][OVERALL_ANNOTATION][0] = true;
	}

	if(tasks.length == 1)
	    overallComparison = new int[]{0};
	else
	{
	    Vector ocvec = new Vector(1, 3);
	    
	    ocvec.add(new Integer(0));
	    
	    for (int i = 1; i < tasks.length; i++)
	    {
		if(tasks[0].equals(tasks[i]) == false)
		{
		    ocvec.add(new Integer(i));
		    different[i][OVERALL_ANNOTATION][0] = true;
		}
	    }
	    
	    if(ocvec.size() == 1)
		overallComparison = null;
	    else
	    {
		overallComparison = new int[ocvec.size()];

		for (int i = 0; i < ocvec.size(); i++)
		    overallComparison[i] = ((Integer) ocvec.get(i)).intValue();
	    }
	}
	
	compared[OVERALL_ANNOTATION][0] = true;
	return overallComparison;
    }

    public SanchayTableModel compareMarkupExtent(int lang)
    {
	if(compared[MARKUP_EXTENT][lang] == true)
	    return markupExtentComparison[lang];

	// Get difference markups for all the tasks for one sentence
	// Merge columns
	// Merge rows for all sentences
	
	int maxSenCount = 0;
	
	for (int i = 0; i < tasks.length; i++)
	{
	    int scount = tasks[i].getSentenceCount();
	    
	    if(maxSenCount < scount)
		maxSenCount = scount;
	}	

	Vector senTables = new Vector(0, 5);
	SanchayTableModel emptyTable = new SanchayTableModel();
	
	for (int i = 0; i < maxSenCount; i++)
	{
	    boolean isDiff = false;
	    Vector taskTables = new Vector();
	    
	    for (int j = 0; j < tasks.length; j++)
	    {
		if(j == 0)
		{
		    if(lang == SL)
			taskTables.add(tasks[0].getSrcSenMarkups().get(new Integer(i + 1)));
		    else if(lang == TL)
			taskTables.add(tasks[0].getTgtSenMarkups().get(new Integer(i + 1)));
		}
		else
		{
		    SanchayTableModel retTable = tasks[0].areDifferentMarkupExtents(tasks[j], i, lang);
		    
		    if(retTable == null)
			taskTables.add(emptyTable);
		    else
		    {
			isDiff = true;
			taskTables.add(retTable);
		    }
		}
	    }
		
	    if(isDiff)
	    {
		SanchayTableModel mergedTaskTable = SanchayTableModel.mergeColumns(taskTables);
		senTables.add(mergedTaskTable);
	    }
	}
	
	markupExtentComparison[lang] = SanchayTableModel.mergeRows(senTables, true);
	
	compared[MARKUP_EXTENT][lang] = true;
	return markupExtentComparison[lang];
    }

    public SanchayTableModel compareMarkup(int lang)
    {
	if(compared[MARKUP][lang] == true)
	    return markupComparison[lang];
	
	// Get difference markups for all the tasks for one sentence
	// Merge columns
	// Merge rows for all sentences
	
	int maxSenCount = 0;
	
	for (int i = 0; i < tasks.length; i++)
	{
	    int scount = tasks[i].getSentenceCount();
	    
	    if(maxSenCount < scount)
		maxSenCount = scount;
	}	

	Vector senTables = new Vector(0, 5);
	SanchayTableModel emptyTable = new SanchayTableModel();
	
	for (int i = 0; i < maxSenCount; i++)
	{
	    boolean isDiff = false;
	    Vector taskTables = new Vector();
	    
	    for (int j = 0; j < tasks.length; j++)
	    {
		if(j == 0)
		{
		    if(lang == SL)
			taskTables.add(tasks[0].getSrcSenMarkups().get(new Integer(i + 1)));
		    else if(lang == TL)
			taskTables.add(tasks[0].getTgtSenMarkups().get(new Integer(i + 1)));
		}
		else
		{
		    SanchayTableModel retTable = tasks[0].areDifferentMarkups(tasks[j], i, lang);
		    
		    if(retTable == null)
			taskTables.add(emptyTable);
		    else
		    {
			isDiff = true;
			taskTables.add(retTable);
		    }
		}
	    }
		
	    if(isDiff)
	    {
		SanchayTableModel mergedTaskTable = SanchayTableModel.mergeColumns(taskTables);
		senTables.add(mergedTaskTable);
	    }
	}
	
	markupComparison[lang] = SanchayTableModel.mergeRows(senTables, true);
	
	compared[MARKUP][lang] = true;
	return markupComparison[lang];
    }
    
    public SanchayTableModel compareMarkerMapping()
    {
	if(compared[MARKER_MAPPING][0] == true)
	    return markerMappingComparison;
	
	// Get difference markups for all the tasks for one sentence
	// Merge columns
	// Merge rows for all sentences
	
	int maxSenCount = 0;
	
	for (int i = 0; i < tasks.length; i++)
	{
	    int scount = tasks[i].getSentenceCount();
	    
	    if(maxSenCount < scount)
		maxSenCount = scount;
	}	

	Vector senTables = new Vector(0, 5);
	SanchayTableModel emptyTable = new SanchayTableModel();
	
	for (int i = 0; i < maxSenCount; i++)
	{
	    boolean isDiff = false;
	    Vector taskTables = new Vector();
	    
	    for (int j = 0; j < tasks.length; j++)
	    {
		if(j == 0)
		{
		    taskTables.add(tasks[0].getMarkerMappingTables().get(new Integer(i + 1)));
		}
		else
		{
		    SanchayTableModel retTable = tasks[0].areDifferentMarkerMappings(tasks[j], i);
		    
		    if(retTable == null)
			taskTables.add(emptyTable);
		    else
		    {
			isDiff = true;
			taskTables.add(retTable);
		    }
		}
	    }
		
	    if(isDiff)
	    {
		SanchayTableModel mergedTaskTable = SanchayTableModel.mergeColumns(taskTables);
		senTables.add(mergedTaskTable);
	    }
	}
	
	markerMappingComparison = SanchayTableModel.mergeRows(senTables, true);
	
	compared[MARKER_MAPPING][0] = true;
	return markerMappingComparison;
    }
    
    public SanchayTableModel compareMarkerList(int lang)
    {
	if(compared[MARKER_LIST][lang] == true)
	    return markerListComparison[lang];
	
	markerListComparison[lang] = newComparisonTableModel();
	markerListComparison[lang].removeColumn(0);
	
	DefaultComboBoxModel refMarkers = null;

	if(lang == SL)
	    refMarkers = tasks[0].getSrcTamMarkers();
	else if(lang== TL)
	    refMarkers = tasks[0].getTgtTamMarkers();
	
	for (int i = 0; i < refMarkers.getSize(); i++)
	{
	    if(i >= markerListComparison[lang].getRowCount())
		markerListComparison[lang].addRow();
	    
	    markerListComparison[lang].setValueAt(refMarkers.getElementAt(i), i, 0);
	}
	
	for (int i = 1; i < tasks.length; i++)
	{
	    Vector otherMarkers = tasks[0].getDifferentMarkers(tasks[i], lang);
	    
	    int count = otherMarkers.size();
	    
	    for (int j = 0; j < count; j++)
	    {
		if(j >= markerListComparison[lang].getRowCount())
		    markerListComparison[lang].addRow();
		
		markerListComparison[lang].setValueAt(otherMarkers.get(i), j, i);
	    }
	}	
	
	compared[MARKER_LIST][lang] = true;
	return markerListComparison[lang];
    }
    
    public SanchayTableModel compareComments()
    {
	if(compared[COMMENTS][0] == true)
	    return comments;

	comments = newComparisonTableModel();
	comments.removeColumn(0);
	
	int maxSenCount = 0;
	
	for (int i = 0; i < tasks.length; i++)
	{
	    int scount = tasks[i].getSentenceCount();
	    
	    if(maxSenCount < scount)
		maxSenCount = scount;
	}	
	
	int diffCount = 0;
	
	for (int i = 0; i < maxSenCount; i++)
	{
	    boolean isDiff = false;
	    Vector otherComments = new Vector();
	    
	    for (int j = 0; j < tasks.length; j++)
	    {
		String cmt = tasks[0].areDifferentComments(tasks[j], i);
		    
		if(cmt == null)
		    otherComments.add("");
		else
		{
		    isDiff = true;
		    otherComments.add(cmt);
		}
	    }
		
	    if(isDiff)
	    {
		if(diffCount >= comments.getRowCount())
		    comments.addRow();
		
		if(i < tasks[0].getSentenceCount())
		    comments.setValueAt(tasks[0].getCommentsPT().getToken(i), diffCount, 0);
		    
		int ocount = otherComments.size();
		
		for (int j = 0; j < ocount; j++)
		{
		    comments.setValueAt(otherComments.get(j), diffCount, j);
		}
	    }
	}
	
	compared[COMMENTS][0] = true;
	return comments;
    }
    
    // Listing
    public void listOverall()
    {
	if(listed[OVERALL_LISTING] == true)
	    return;

	for(int i = 1; i < _LISTING_LAST_; i++)
	{
	    list(i);
	}
    }

    public SanchayTableModel listUsedMarkers()
    {
	if(listed[USED_MARKER_LIST] == true)
	    return usedMarkerList;

	usedMarkerList = newListTableModel();

	for (int l = 0; l < numLanguages; l++)
	{
	    LinkedHashMap markerHT = new LinkedHashMap(0, 10);
	    
	    for(int i = 0; i < tasks.length; i++)
	    {
		LinkedHashMap markup = null;
		DefaultComboBoxModel tamMarkers = null;

		if(l == SL)
		{
		    markup = tasks[i].getSrcSenMarkups();
		    tamMarkers = tasks[i].getSrcTamMarkers();
		}
		else if(l == TL)
		{
		    markup = tasks[i].getTgtSenMarkups();
		    tamMarkers = tasks[i].getTgtTamMarkers();
		}

		Iterator itr = markup.keySet().iterator();

		while(itr.hasNext())
		{
		    String pos = (String) itr.next();
		    SanchayTableModel senMarkup = (SanchayTableModel) markup.get(pos);

		    int rcount = senMarkup.getRowCount();

		    for (int j = 0; j < rcount; j++)
		    {
			int marker = Integer.parseInt((String) senMarkup.getValueAt(j, 2));
			String smarker = (String) tamMarkers.getElementAt(marker);
			
//			System.out.println(smarker);
			
			if(smarker != null)
			{
			    Integer freq = (Integer) markerHT.get(smarker);
			    if(freq == null)
				markerHT.put(smarker, new Integer(1));
			    else
				markerHT.put(smarker, new Integer(freq.intValue() + 1));
			}
		    }
		}
	    }	

	    Object omarkers[] = markerHT.keySet().toArray();

	    Arrays.sort(omarkers);

	    for (int i = 0; i < omarkers.length; i++)
	    {
		if(i >= usedMarkerList.getRowCount())
		    usedMarkerList.addRow();

		usedMarkerList.setValueAt(omarkers[i], i, 2 * l);
		
		Integer freq = (Integer) markerHT.get(omarkers[i]);
		usedMarkerList.setValueAt(freq, i, 2 * l + 1);
	    }
	}
	
	return usedMarkerList;
    }

    public SanchayTableModel listUnusedMarkers()
    {
	if(listed[UNUSED_MARKER_LIST] == true)
	    return unusedMarkerList;
	
	listUsedMarkers();

	unusedMarkerList = newListTableModel();

	for (int l = 0; l < numLanguages; l++)
	{
	    if(l + 1 < unusedMarkerList.getColumnCount())
		unusedMarkerList.removeColumn(l + 1);
	}

	for (int l = 0; l < numLanguages; l++)
	{
	    // Collect all markers
	    Hashtable markerHT = new Hashtable(0, 10);
	    for(int i = 0; i < tasks.length; i++)
	    {
		DefaultComboBoxModel tamMarkers = null;

		if(l == SL)
		{
		    tamMarkers = tasks[i].getSrcTamMarkers();
		}
		else if(l == TL)
		{
		    tamMarkers = tasks[i].getTgtTamMarkers();
		}

		int mcount = tamMarkers.getSize();

		for (int j = 0; j < mcount; j++)
		{
		    String smarker = (String) tamMarkers.getElementAt(j);
		    markerHT.put(smarker, new Boolean(true));
		}
	    }	

	    // Remove used markers
	    int ucount = usedMarkerList.getRowCount();
	    for (int i = 0; i < ucount; i++)
	    {
		String umarker = (String) usedMarkerList.getValueAt(i, 2 * l);

		if(markerHT.get(umarker) != null)
		    markerHT.remove(umarker);
	    }

	    // Return unused markers
	    Object omarkers[] = markerHT.keySet().toArray();

	    Arrays.sort(omarkers);

	    for (int i = 0; i < omarkers.length; i++)
	    {
		if(i >= unusedMarkerList.getRowCount())
		    unusedMarkerList.addRow();

		unusedMarkerList.setValueAt(omarkers[i], i, l);
	    }
	}
	
	return unusedMarkerList;
    }
    
    public SanchayTableModel listWords()
    {
	return null;
    }
    
    public SanchayTableModel listMarkups()
    {
	if(listed[MARKUP_LIST] == true)
	    return markupList;

	markupList = newListTableModel();

	int k = 1;
	for (int l = 0; l < numLanguages; l++)
	{
	    if(k < markupList.getColumnCount())
		markupList.insertColumn(k, GlobalProperties.getIntlString("Marker_(") + tasks[0].getLanguages()[l] + ")");
	    else
		markupList.addColumn(GlobalProperties.getIntlString("Marker_(") + tasks[0].getLanguages()[l] + ")");
	    
	    k += 3;
	}

	for (int l = 0; l < numLanguages; l++)
	{
	    LinkedHashMap markupHT = new LinkedHashMap(0, 10);

	    for(int i = 0; i < tasks.length; i++)
	    {
		LinkedHashMap markup = null;
		DefaultComboBoxModel tamMarkers = null;
		PropertyTokens senPT = null;

		if(l == SL)
		{
		    markup = tasks[i].getSrcSenMarkups();
		    tamMarkers = tasks[i].getSrcTamMarkers();
		    senPT = tasks[i].getSrcCorpusPT();
		}
		else if(l == TL)
		{
		    markup = tasks[i].getTgtSenMarkups();
		    tamMarkers = tasks[i].getTgtTamMarkers();
		    senPT = tasks[i].getTgtCorpusPT();
		}

		Iterator itr = markup.keySet().iterator();

		while(itr.hasNext())
		{
		    String pos = (String) itr.next();
		    int ipos = Integer.parseInt(pos) - 1;
		    SanchayTableModel senMarkup = (SanchayTableModel) markup.get(pos);

		    int rcount = senMarkup.getRowCount();

		    for (int j = 0; j < rcount; j++)
		    {
			int start = Integer.parseInt((String) senMarkup.getValueAt(j, 0));
			int end = Integer.parseInt((String) senMarkup.getValueAt(j, 1));
			int marker = Integer.parseInt((String) senMarkup.getValueAt(j, 2));
			
			String sen = senPT.getToken(ipos);
			String marked = sen.substring(start, end + 1);
			String smarker = (String) tamMarkers.getElementAt(marker);
			
			String markupKey = marked + "::" + smarker;
			
			Integer freq = (Integer) markupHT.get(markupKey);
			if(freq == null)
			    markupHT.put(markupKey, new Integer(1));
			else
			    markupHT.put(markupKey, new Integer(freq.intValue() + 1));
		    }
		}
	    }	

	    Object omarkers[] = markupHT.keySet().toArray();

	    Arrays.sort(omarkers);

	    for (int i = 0; i < omarkers.length; i++)
	    {
		if(i >= markupList.getRowCount())
		    markupList.addRow();

		Integer freq = (Integer) markupHT.get(omarkers[i]);

//		System.out.println(markupList.getRowCount() + " " + markupList.getColumnCount() + " " + i + " " + l);

		String parts[] = ((String) omarkers[i]).split("::");
		
		markupList.setValueAt(parts[0], i, 3 * l);
		markupList.setValueAt(parts[1], i, 3 * l + 1);
		markupList.setValueAt(freq, i, 3 * l + 2);
	    }
	}

	return markupList;
    }
    
    public SanchayTableModel listMarkupMappings()
    {
	if(listed[MARKUP_MAPPING_LIST] == true)
	    return markupMappingList;

	markupMappingList = newListTableModel();
	markupMappingList.addColumn("Frequency");

	for (int l = 0; l < numLanguages; l++)
	{
	    markupMappingList.setColumnIdentifier(2 * l + 1, GlobalProperties.getIntlString("Marker_(") + tasks[0].getLanguages()[l] + ")");
	}

	LinkedHashMap markerMappingHT = new LinkedHashMap(0, 10);

	for(int i = 0; i < tasks.length; i++)
	{
	    LinkedHashMap markerMapping = tasks[i].getMarkerMappingTables();

	    LinkedHashMap srcMarkup = tasks[i].getSrcSenMarkups();
	    LinkedHashMap tgtMarkup = tasks[i].getTgtSenMarkups();
	    
	    DefaultComboBoxModel srcTamMarkers = tasks[i].getSrcTamMarkers();
	    DefaultComboBoxModel tgtTamMarkers = tasks[i].getTgtTamMarkers();

	    PropertyTokens srcSenPT = tasks[i].getSrcCorpusPT();
	    PropertyTokens tgtSenPT = tasks[i].getTgtCorpusPT();

	    Iterator itr = markerMapping.keySet().iterator();

	    while(itr.hasNext())
	    {
		String pos = (String) itr.next();
		int ipos = Integer.parseInt(pos) - 1;
		
		SanchayTableModel senMarkerMapping = (SanchayTableModel) markerMapping.get(pos);

		SanchayTableModel srcSenMarkup = (SanchayTableModel) srcMarkup.get(pos);
		SanchayTableModel tgtSenMarkup = (SanchayTableModel) tgtMarkup.get(pos);

		int rcount = senMarkerMapping.getRowCount();

		for (int j = 0; j < rcount; j++)
		{
		    int srcExtent = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 0)) - 1;
		    int srcMarkerIndex = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 1));
		    int tgtExtent = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 2)) - 1;
		    int tgtMarkerIndex = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 3));

		    int srcStart = -1;
		    int srcEnd = -1;
		    int srcMarker = -1;

		    int tgtStart = -1;
		    int tgtEnd = -1;
		    int tgtMarker = -1;

		    if(srcMarkerIndex != -1)
		    {
			srcStart = Integer.parseInt((String) srcSenMarkup.getValueAt(srcExtent, 0));
			srcEnd = Integer.parseInt((String) srcSenMarkup.getValueAt(srcExtent, 1));
			srcMarker = Integer.parseInt((String) srcSenMarkup.getValueAt(srcExtent, 2));
		    }

		    if(tgtMarkerIndex != -1)
		    {
			tgtStart = Integer.parseInt((String) tgtSenMarkup.getValueAt(tgtExtent, 0));
			tgtEnd = Integer.parseInt((String) tgtSenMarkup.getValueAt(tgtExtent, 1));
			tgtMarker = Integer.parseInt((String) tgtSenMarkup.getValueAt(tgtExtent, 2));
		    }

		    String srcSen = srcSenPT.getToken(ipos);
		    String tgtSen = tgtSenPT.getToken(ipos);

		    String srcMarked = " ";
		    String sSrcMarker = " ";
		    String tgtMarked = " ";
		    String sTgtMarker = " ";
		    
		    if(srcMarkerIndex != -1)
		    {
			srcMarked = srcSen.substring(srcStart, srcEnd + 1);
			sSrcMarker = (String) srcTamMarkers.getElementAt(srcMarker);
		    }

		    if(tgtMarkerIndex != -1)
		    {
			tgtMarked = tgtSen.substring(tgtStart, tgtEnd + 1);
			sTgtMarker = (String) tgtTamMarkers.getElementAt(tgtMarker);
		    }

		    String mappingKey = srcMarked + "::" + sSrcMarker + "::" + tgtMarked + "::" + sTgtMarker;

		    Integer freq = (Integer) markerMappingHT.get(mappingKey);
		    if(freq == null)
			markerMappingHT.put(mappingKey, new Integer(1));
		    else
			markerMappingHT.put(mappingKey, new Integer(freq.intValue() + 1));
		}
	    }
	}	

	Object omarkers[] = markerMappingHT.keySet().toArray();

	Arrays.sort(omarkers);

	for (int i = 0; i < omarkers.length; i++)
	{
	    if(i >= markupMappingList.getRowCount())
		markupMappingList.addRow();

	    Integer freq = (Integer) markerMappingHT.get(omarkers[i]);

//	    System.out.println(markupMappingList.getRowCount() + " " + markupMappingList.getColumnCount() + " " + i + " " + l);

	    String parts[] = ((String) omarkers[i]).split("::");

	    markupMappingList.setValueAt(parts[0].trim(), i, 0);
	    markupMappingList.setValueAt(parts[1].trim(), i, 1);
	    markupMappingList.setValueAt(parts[2].trim(), i, 2);
	    markupMappingList.setValueAt(parts[3].trim(), i, 3);
	    markupMappingList.setValueAt(freq, i, 4);
	}

	return markupMappingList;
    }
    
    public SanchayTableModel listMarkerMappings()
    {
	if(listed[MARKER_MAPPING_LIST] == true)
	    return markerMappingList;

	markerMappingList = newListTableModel();
	
	markerMappingList.addColumn("Frequency");

	for (int l = 0; l < numLanguages; l++)
	{
	    markerMappingList.setColumnIdentifier(2 * l + 1, GlobalProperties.getIntlString("Marker_(") + tasks[0].getLanguages()[l] + ")");
	}

	for (int l = 0; l < numLanguages; l++)
	    markerMappingList.removeColumn(l + 1);

	LinkedHashMap markerMappingHT = new LinkedHashMap(0, 10);

	for(int i = 0; i < tasks.length; i++)
	{
	    LinkedHashMap markerMapping = tasks[i].getMarkerMappingTables();

	    LinkedHashMap srcMarkup = tasks[i].getSrcSenMarkups();
	    LinkedHashMap tgtMarkup = tasks[i].getTgtSenMarkups();
	    
	    DefaultComboBoxModel srcTamMarkers = tasks[i].getSrcTamMarkers();
	    DefaultComboBoxModel tgtTamMarkers = tasks[i].getTgtTamMarkers();

	    Iterator itr = markerMapping.keySet().iterator();

	    while(itr.hasNext())
	    {
		String pos = (String) itr.next();
		int ipos = Integer.parseInt(pos) - 1;
		
		SanchayTableModel senMarkerMapping = (SanchayTableModel) markerMapping.get(pos);

		SanchayTableModel srcSenMarkup = (SanchayTableModel) srcMarkup.get(pos);
		SanchayTableModel tgtSenMarkup = (SanchayTableModel) tgtMarkup.get(pos);

		int rcount = senMarkerMapping.getRowCount();

		for (int j = 0; j < rcount; j++)
		{
		    int srcExtent = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 0)) - 1;
		    int srcMarkerIndex = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 1));
		    int tgtExtent = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 2)) - 1;
		    int tgtMarkerIndex = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 3));

		    int srcMarker = -1;
		    int tgtMarker = -1;

		    if(srcMarkerIndex != -1)
			srcMarker = Integer.parseInt((String) srcSenMarkup.getValueAt(srcExtent, 2));

		    if(tgtMarkerIndex != -1)
			tgtMarker = Integer.parseInt((String) tgtSenMarkup.getValueAt(tgtExtent, 2));

		    String sSrcMarker = " ";
		    String sTgtMarker = " ";
		    
		    if(srcMarkerIndex != -1)
			sSrcMarker = (String) srcTamMarkers.getElementAt(srcMarker);

		    if(tgtMarkerIndex != -1)
			sTgtMarker = (String) tgtTamMarkers.getElementAt(tgtMarker);

		    String mappingKey = sSrcMarker + "::" + sTgtMarker;

		    Integer freq = (Integer) markerMappingHT.get(mappingKey);
		    if(freq == null)
			markerMappingHT.put(mappingKey, new Integer(1));
		    else
			markerMappingHT.put(mappingKey, new Integer(freq.intValue() + 1));
		}
	    }
	}	

	Object omarkers[] = markerMappingHT.keySet().toArray();

	Arrays.sort(omarkers);

	for (int i = 0; i < omarkers.length; i++)
	{
	    if(i >= markerMappingList.getRowCount())
		markerMappingList.addRow();

	    Integer freq = (Integer) markerMappingHT.get(omarkers[i]);

	    String parts[] = ((String) omarkers[i]).split("::");

	    markerMappingList.setValueAt(parts[0].trim(), i, 0);
	    markerMappingList.setValueAt(parts[1].trim(), i, 1);
	    markerMappingList.setValueAt(freq, i, 2);
	}

	return markerMappingList;
    }
    
    public SanchayTableModel listSentences()
    {
	if(listed[SENTENCE_LIST] == true)
	    return sentenceList;

	sentenceList = newListTableModel();

	for (int l = 0; l < numLanguages; l++)
	{
	    if(l + 1 < sentenceList.getColumnCount())
		sentenceList.removeColumn(l + 1);
	}
	
	sentenceList.insertColumn(0, GlobalProperties.getIntlString("S._No."));

	for (int l = 0; l < numLanguages; l++)
	{
	    for(int i = 0; i < tasks.length; i++)
	    {
		PropertyTokens senPT = null;

		if(l == SL)
		{
		    senPT = tasks[i].getSrcCorpusPT();
		}
		else if(l == TL)
		{
		    senPT = tasks[i].getTgtCorpusPT();
		}

		int scount = senPT.countTokens();

		for (int j = 0; j < scount; j++)
		{
		    if(j >= sentenceList.getRowCount())
			sentenceList.addRow();
		    
		    String sen = senPT.getToken(j);
		    sentenceList.setValueAt(sen, j, l + 1);

		    sentenceList.setValueAt("" + (j + 1), j, 0);
		}
	    }
	}

	return sentenceList;
    }
    
    // Querying

    // For a sequence of words		
    public SanchayTableModel queryMarkers(String wrds, int lang, int minFreq, int maxFreq)
    {
	list(MARKUP_LIST);
	
	int count = markupList.getRowCount();
	
	Hashtable matchedHT = new Hashtable(0, 3);
	SanchayTableModel matchedMarkerTable = new SanchayTableModel(0, 2);
	
	for (int i = 0; i < count; i++)
	{
	    String words = (String) markupList.getValueAt(i, 3 * lang + 0);
	    String marker = (String) markupList.getValueAt(i, 3 * lang + 1);
	    
	    if(words.equals("") == false && marker.equals("") == false)
	    {
		Integer freq = (Integer) markupList.getValueAt(i, 3 * lang + 2);

		int ifreq = freq.intValue();

		if(words.equals(wrds) || wrds.equals("_ANY_"))
		{
		    Integer prevFreq = (Integer) matchedHT.get(marker);
		    if(prevFreq == null)
			matchedHT.put(marker, new Integer(ifreq));
		    else
			matchedHT.put(marker, new Integer(prevFreq.intValue() + ifreq));
		}
	    }
	}
	
	Object omarkers[] = matchedHT.keySet().toArray();

	Arrays.sort(omarkers);

	for (int i = 0; i < omarkers.length; i++)
	{
	    Integer freq = (Integer) matchedHT.get(omarkers[i]);
	    int ifreq = freq.intValue();
	    
	    if(ifreq >= minFreq && ifreq <= maxFreq)
	    {
		matchedMarkerTable.addRow();

		matchedMarkerTable.setValueAt(omarkers[i], matchedMarkerTable.getRowCount() - 1, 0);
		matchedMarkerTable.setValueAt(freq, matchedMarkerTable.getRowCount() - 1, 1);
	    }
	}

	return matchedMarkerTable;
    }

    // For a marker
    public SanchayTableModel queryMarkedExtents(String mrk, int lang, int minFreq, int maxFreq)
    {
	return null;
    }

    // For a marker
    public SanchayTableModel queryMarkerContexts(String mrk, int lang, int window, int minFreq, int maxFreq)
    {
	return null;
    }

    // For a marker mapping
    public SanchayTableModel queryMappingContexts(Vector markers, int window, int minFreq, int maxFreq)
    {
	list(MARKER_MAPPING_LIST);
	
	int count = markerMappingList.getRowCount();
	
	SanchayTableModel relevantMappingTable = new SanchayTableModel(0, 2);
	SanchayTableModel matchedContextTable = new SanchayTableModel(0, 5);
	
	for (int i = 0; i < count; i++)
	{
	    String srcMarker = (String) markerMappingList.getValueAt(i, 0);
	    String tgtMarker = (String) markerMappingList.getValueAt(i, 1);
	    Integer sfreq = (Integer) markerMappingList.getValueAt(i, 2);

	    int ifreq = sfreq.intValue();
	    
	    if
	    (
		(
		    (srcMarker.equals(markers.get(0)) && tgtMarker.equals(markers.get(1)))
			|| (srcMarker.equals(markers.get(0)) && ((String) markers.get(1)).equals("_ANY_"))
			|| (((String) markers.get(0)).equals("_ANY_") && tgtMarker.equals(markers.get(1)))
		)
		    && ifreq >= minFreq
		    && ifreq <= maxFreq
	    )
	    {
		try {
		    relevantMappingTable.addRowUnique(new int[]{0, 1}, new String[]{srcMarker, tgtMarker});
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	
	for(int i = 0; i < tasks.length; i++)
	{
	    LinkedHashMap markerMapping = tasks[i].getMarkerMappingTables();

	    LinkedHashMap srcMarkup = tasks[i].getSrcSenMarkups();
	    LinkedHashMap tgtMarkup = tasks[i].getTgtSenMarkups();
	    
	    DefaultComboBoxModel srcTamMarkers = tasks[i].getSrcTamMarkers();
	    DefaultComboBoxModel tgtTamMarkers = tasks[i].getTgtTamMarkers();

	    PropertyTokens srcSenPT = tasks[i].getSrcCorpusPT();
	    PropertyTokens tgtSenPT = tasks[i].getTgtCorpusPT();
	    PropertyTokens tgtUTF8SenPT = tasks[i].getTgtCorpusUTF8PT();

	    Iterator itr = markerMapping.keySet().iterator();

	    while(itr.hasNext())
	    {
		String pos = (String) itr.next();
		int ipos = Integer.parseInt(pos) - 1;
		
		SanchayTableModel senMarkerMapping = (SanchayTableModel) markerMapping.get(pos);

		SanchayTableModel srcSenMarkup = (SanchayTableModel) srcMarkup.get(pos);
		SanchayTableModel tgtSenMarkup = (SanchayTableModel) tgtMarkup.get(pos);

		int rcount = senMarkerMapping.getRowCount();

		for (int j = 0; j < rcount; j++)
		{
		    int srcExtent = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 0)) - 1;
		    int srcMarkerIndex = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 1));
		    int tgtExtent = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 2)) - 1;
		    int tgtMarkerIndex = Integer.parseInt((String) senMarkerMapping.getValueAt(j, 3));

		    int srcMarker = -1;
		    int tgtMarker = -1;

		    if(srcMarkerIndex != -1)
			srcMarker = Integer.parseInt((String) srcSenMarkup.getValueAt(srcExtent, 2));

		    if(tgtMarkerIndex != -1)
			tgtMarker = Integer.parseInt((String) tgtSenMarkup.getValueAt(tgtExtent, 2));

		    String sSrcMarker = "";
		    String sTgtMarker = "";
		    
		    if(srcMarkerIndex != -1)
			sSrcMarker = (String) srcTamMarkers.getElementAt(srcMarker);

		    if(tgtMarkerIndex != -1)
			sTgtMarker = (String) tgtTamMarkers.getElementAt(tgtMarker);

		    Vector matchedRows = null;
		    try {
			matchedRows = relevantMappingTable.getRowsAnd(new int[]{0, 1}, new String[]{sSrcMarker, sTgtMarker});
		    } catch (Exception ex) {
			ex.printStackTrace();
		    }
		    
		    if(matchedRows != null && matchedRows.size() > 0)
		    {
			String srcSen = srcSenPT.getToken(ipos);
			String tgtSen = tgtSenPT.getToken(ipos);
			String tgtUTF8Sen = tgtUTF8SenPT.getToken(ipos);
			
			matchedContextTable.addRowUnique(1, new String[]{sSrcMarker, srcSen, sTgtMarker, tgtUTF8Sen, tgtSen});
		    }
		}
	    }
	}	
	
	return matchedContextTable;
    }

    // For a marker
    public SanchayTableModel queryMarkerTranslations(String mrk, int slang, int tlang, int minFreq, int maxFreq)
    {
	list(MARKER_MAPPING_LIST);
	
	int count = markerMappingList.getRowCount();
	
	Hashtable matchedTransHT = new Hashtable(0, 3);
	SanchayTableModel matchedMarkerTransTable = new SanchayTableModel(0, 2);
	
	for (int i = 0; i < count; i++)
	{
	    String srcMarker = (String) markerMappingList.getValueAt(i, slang);
	    String tgtMarker = (String) markerMappingList.getValueAt(i, tlang);

	    if(srcMarker.equals("") == false && tgtMarker.equals("") == false)
	    {
		Integer freq = (Integer) markerMappingList.getValueAt(i, numLanguages);

		int ifreq = freq.intValue();

		if(srcMarker.equals(mrk) || mrk.equals("_ANY_"))
		{
		    String key = srcMarker + "::" + tgtMarker;
		    Integer prevFreq = (Integer) matchedTransHT.get(key);
		    if(prevFreq == null)
			matchedTransHT.put(key, new Integer(ifreq));
		    else
			matchedTransHT.put(key, new Integer(prevFreq.intValue() + ifreq));
		}
	    }
	}
	
	Object omarkers[] = matchedTransHT.keySet().toArray();

	Arrays.sort(omarkers);

	for (int i = 0; i < omarkers.length; i++)
	{
	    Integer freq = (Integer) matchedTransHT.get(omarkers[i]);
	    int ifreq = freq.intValue();
	    
	    if(ifreq >= minFreq && ifreq <= maxFreq)
	    {
		matchedMarkerTransTable.addRow();

		matchedMarkerTransTable.setValueAt(omarkers[i], matchedMarkerTransTable.getRowCount() - 1, 0);
		matchedMarkerTransTable.setValueAt(freq, matchedMarkerTransTable.getRowCount() - 1, 1);
	    }
	}

	return matchedMarkerTransTable;
    }

    // Other operations
    
    // From dictionary
    public String getDefaultTranslation(String mrk, int slang, int tlang)
    {
	return null;
    }

    // Based on some learning and/or rule based algorithm
    public String getBestTranslation(String mrk, int slang, int tlang)
    {
	return null;
    }
}
