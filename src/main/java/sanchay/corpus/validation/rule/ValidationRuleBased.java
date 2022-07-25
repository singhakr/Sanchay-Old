/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.validation.rule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.query.SSFQuery;
import sanchay.properties.KeyValueProperties;
import sanchay.table.SanchayTableModel;

/**
 *
 * @author ambati
 */
public class ValidationRuleBased {

    private File errFile;   // Input Directory
    private File usefulDir;  // Tools program Directory
    private String lang;
    protected List filesList;
    protected LinkedHashMap<File, SSFStory> selStories;
    //protected SanchayTableModel allMatches;

    public ValidationRuleBased()
    {
        filesList = new ArrayList();
        selStories = new LinkedHashMap<File, SSFStory>();
        //allMatches = null;
    }

    public void setFilesList(List list)
    {
        filesList = list;
    }

    public void setSelStories(LinkedHashMap map)
    {
        selStories= map;
    }
    
/*
    public SanchayTableModel getTableModel()
    {
        return allMatches;
    }
 */
    
    public void setLanguage(String language)
    {
        lang = language;
    }

    public void setToolsDir(String dir) throws FileNotFoundException, IOException
    {
        usefulDir = new File(dir);
    }

    public SanchayTableModel ValRulePOS() throws FileNotFoundException, IOException
    {
        File ifile = new File(GlobalProperties.resolveRelativePath("workspace/syn-annotation/validation-rules/pos-rules.txt"));
        KeyValueProperties queriesKVP = null;
        queriesKVP = GetQueriesfromFile(ifile);
        if(queriesKVP.countProperties() > 0)
            return RunQueries(filesList, queriesKVP);
        else
            return null;
    }

    public SanchayTableModel ValRuleChunk() throws FileNotFoundException, IOException
    {
        File ifile = new File(GlobalProperties.resolveRelativePath("workspace/syn-annotation/validation-rules/chunk-rules.txt"));
        KeyValueProperties queriesKVP = null;
        queriesKVP = GetQueriesfromFile(ifile);
        if(queriesKVP.countProperties() > 0)
            return RunQueries(filesList, queriesKVP);
        else
            return null;
    }

    public SanchayTableModel ValRuleMorph() throws FileNotFoundException, IOException
    {
        File ifile = new File(GlobalProperties.resolveRelativePath("workspace/syn-annotation/validation-rules/morph-rules.txt"));
        KeyValueProperties queriesKVP = null;
        queriesKVP = GetQueriesfromFile(ifile);
        if(queriesKVP.countProperties() > 0)
            return RunQueries(filesList, queriesKVP);
        else
            return null;
    }

    public SanchayTableModel ValRuleDep() throws FileNotFoundException, IOException
    {
        File ifile = new File(GlobalProperties.resolveRelativePath("workspace/syn-annotation/validation-rules/dep-rules.txt"));
        KeyValueProperties queriesKVP = null;
        queriesKVP = GetQueriesfromFile(ifile);
        if(queriesKVP.countProperties() > 0)
            return RunQueries(filesList, queriesKVP);
        else
            return null;
    }

    public SanchayTableModel ValRuleOthers() throws FileNotFoundException, IOException
    {
        File ifile = new File(GlobalProperties.resolveRelativePath("workspace/syn-annotation/validation-rules/other-rules.txt"));
        KeyValueProperties queriesKVP = null;
        queriesKVP = GetQueriesfromFile(ifile);
        if(queriesKVP.countProperties() > 0)
            return RunQueries(filesList, queriesKVP);
        else
            return null;
    }

    public SanchayTableModel RunQueries(List files, KeyValueProperties queriesKVP)
    {
        SanchayTableModel allMatches = null;

        Iterator iterator = queriesKVP.getPropertyKeys();

        while (iterator.hasNext()) {
            String query = (String) iterator.next();
            String comment = (String) queriesKVP.getPropertyValue(query);
            SSFQuery ssfQuery = new SSFQuery(query);
            //SSFQuery ssfQuery = new SSFQuery("C.t=\'NN\'");

            try
            {
                ssfQuery.parseQuery();
            } catch (Exception ex)
            {
    //            JOptionPane.showMessageDialog(this, "Error in parsing the query: " + ssfQuery, sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
                return null;
            }

            SanchayTableModel matches = ssfQuery.queryInFiles(ssfQuery, selStories, comment);

            //System.out.println("Query: "+query);
            
            Vector matchesVec = new Vector();
            
            if(allMatches != null && matches != null)
            {
                matchesVec.add(allMatches);
                matchesVec.add(matches);

                allMatches = SanchayTableModel.mergeRows(matchesVec);
            }
            else if(allMatches == null && matches != null)
                allMatches = matches;

            ssfQuery.send();
        }

        return allMatches;
    }

    /*
    private SanchayTableModel RunQueryonFiles(List files, SSFQuery ssfQuery)
    {
        SanchayTableModel allMatches = null;
        for(int i=0;i<files.size();i++)
        {
            String file = (String) files.get(i);
            SSFStory story = new SSFStoryImpl();
            File ifile = new File(file);

            story.setSSFFile(ifile.getAbsolutePath());

                //String cs = kvTaskProps.getPropertyValue("SSFCorpusCharset");
            String cs = charset;

                try {
                    story.readFile(ifile.getAbsolutePath(), cs);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SanchayTableModel matches = RunQueriesOnFile(story,queries);

                Vector matchesVec = new Vector();

                if(allMatches != null && matches != null)
                {
                    matchesVec.add(allMatches);
                    matchesVec.add(matches);

                    allMatches = SanchayTableModel.mergeRows(matchesVec);
                }
                else if(allMatches == null && matches != null)
                    allMatches = matches;
            }
        return allMatches;
    }

     * */

    private KeyValueProperties GetQueriesfromFile(File ifile) throws FileNotFoundException, IOException
    {
        KeyValueProperties queriesKVP = new KeyValueProperties(ifile.getAbsolutePath(), "UTF-8");
        
//        BufferedReader inReader = new BufferedReader(new FileReader(ifile));
//        SortedMap queries = new TreeMap();
//        String line="",key="",value="";
//        while ((line = inReader.readLine()) !=null)
//        {
//            if(!line.isEmpty())
//            {
//                //System.out.println(line);
//                line=line.trim();
//                //System.out.println(line);
//                if(line.startsWith("#"))
//                {
//                    value=line.replace("#", "");
//                }
//                else
//                {
//                    key = line;
//                    queries.put(key, value);
//                }
//            }
//        }
//        return queries;

        return queriesKVP;
    }
}