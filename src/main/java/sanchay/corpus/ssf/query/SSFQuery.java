/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.common.types.SSFQueryOperatorType;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.table.SanchayTableModel;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class SSFQuery {

    protected boolean returnsValues;

    protected String queryString;

    protected SSFQueryMatchNode rootMatchNode;
    protected SSFQueryActionNode rootActionNode;
    protected SSFQueryDestinationNode rootDestinationNode;

    protected LinkedHashMap<String, List<QueryValue>> matchedValues;

    protected LinkedHashMap<String, List<QueryValue>> returnValues;
    protected LinkedHashMap<String, Integer> returnLevels;

    protected LinkedHashMap<String, QuerySourceDestination> sources;
    protected LinkedHashMap<String, QuerySourceDestination> destinations;

    public static final int SENTENCE = 0;
    public static final int NODE = 1;
    public static final int STRING = 2;

    public SSFQuery()
    {
        super();
        
        matchedValues = new LinkedHashMap<String, List<QueryValue>>();
        returnValues = new LinkedHashMap<String, List<QueryValue>>();
        returnLevels = new LinkedHashMap<String, Integer>();

        sources = new LinkedHashMap<String, QuerySourceDestination>();
        destinations = new LinkedHashMap<String, QuerySourceDestination>();
    }

    public SSFQuery(String queryString)
    {
        super();
        
        this.queryString = queryString;

        matchedValues = new LinkedHashMap<String, List<QueryValue>>();
        returnValues = new LinkedHashMap<String, List<QueryValue>>();
        returnLevels = new LinkedHashMap<String, Integer>();

        sources = new LinkedHashMap<String, QuerySourceDestination>();
        destinations = new LinkedHashMap<String, QuerySourceDestination>();
    }

    public boolean returnsValues()
    {
        return returnsValues;
    }

    public void returnsValues(boolean d)
    {
        returnsValues = d;
    }

    /**
     * @return the queryString
     */
    public String getQueryString()
    {
        return queryString;
    }

    /**
     * @param queryString the queryString to set
     */
    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }

    /**
     * @return the rootMatchNode
     */
    public SSFQueryMatchNode getRootMatchNode()
    {
        return rootMatchNode;
    }

    /**
     * @param rootMatchNode the rootMatchNode to set
     */
    public void setRootMatchNode(SSFQueryMatchNode rootMatchNode)
    {
        this.rootMatchNode = rootMatchNode;
    }

    /**
     * @return the rootActionNode
     */
    public SSFQueryActionNode getRootActionNode()
    {
        return rootActionNode;
    }

    /**
     * @param rootActionNode the rootActionNode to set
     */
    public void setRootActionNode(SSFQueryActionNode rootActionNode)
    {
        this.rootActionNode = rootActionNode;
    }

    public int countMatches()
    {
        return matchedValues.size();
    }

    public Iterator getMatchKeys()
    {
        return matchedValues.keySet().iterator();
    }

    public List<QueryValue> getMatchedValues(String p /* Property key */)
    {
        return matchedValues.get(p);
    }

    public int addMatchedValue(String m, QueryValue value)
    {
        if(m == null) {
            m = ""  + (matchedValues.size() + 1);
        }

        List<QueryValue> nodes = matchedValues.get(m);

        if(nodes == null)
        {
            nodes = new ArrayList<QueryValue>();
            matchedValues.put(m, nodes);
        }

        nodes.add(value);

        return nodes.size();
    }

    public int addMatchedValues(String m, List<QueryValue> values)
    {
        if(m == null) {
            m = ""  + (matchedValues.size() + 1);
        }

        matchedValues.put(m, values);

        return matchedValues.size();
    }

    public int addAllMatchValues(SSFQuery query)
    {
        Iterator<String> itr = query.getMatchKeys();

        while(itr.hasNext())
        {
            String m = itr.next();
            addMatchedValues(m, query.getMatchedValues(m));
        }

        return matchedValues.size();
    }

    public List<QueryValue> removeMatchedValues(String m)
    {
        return matchedValues.remove(m);
    }

    public void removeAllMatchedValues()
    {
        matchedValues.clear();
    }

    public int countReturns()
    {
        return returnValues.size();
    }

    public Iterator getReturnKeys()
    {
        return returnValues.keySet().iterator();
    }

    public List<QueryValue> getReturnValues(String p /* Property key */)
    {
        return returnValues.get(p);
    }

    public int addReturnNode(String r, QueryValue value)
    {
        if(r == null) {
            r = ""  + (returnValues.size() + 1);
        }

        List<QueryValue> values = returnValues.get(r);

        if(values == null)
        {
            values = new ArrayList<QueryValue>();
            returnValues.put(r, values);
        }

        values.add(value);

        return values.size();
    }

    public int addReturnValues(String r, List<QueryValue> values)
    {
        if(r == null) {
            r = ""  + (returnValues.size() + 1);
        }

        List<QueryValue> rvalues = returnValues.get(r);

        if(rvalues != null)
        {
            rvalues.addAll(values);

            rvalues = (List<QueryValue>) UtilityFunctions.getUnique(rvalues);
            
            returnValues.put(r, rvalues);
        }
        else {
            returnValues.put(r, values);
        }

        return returnValues.size();
    }

    public int addAllReturnValues(SSFQuery query)
    {
        Iterator<String> itr = query.getMatchKeys();

        while(itr.hasNext())
        {
            String r = itr.next();
            addReturnValues(r, query.getMatchedValues(r));
        }

        return returnValues.size();
    }

    public List<QueryValue> removeReturnValues(String r)
    {
        return returnValues.remove(r);
    }

    public void removeAllReturnValues()
    {
        returnValues.clear();
    }

    public int countSources()
    {
        return sources.size();
    }

    public Iterator getSourceKeys()
    {
        return sources.keySet().iterator();
    }

    public QuerySourceDestination getSource(String skey)
    {
        return sources.get(skey);
    }

    public int addSource(String skey, QuerySourceDestination src)
    {
        sources.put(skey, src);

        return sources.size();
    }

    public QuerySourceDestination removeSource(String skey)
    {
        return sources.remove(skey);
    }

    public void removeAllSources()
    {
        sources.clear();
    }

    public int countDestinations()
    {
        return destinations.size();
    }

    public Iterator getDestinationKeys()
    {
        return destinations.keySet().iterator();
    }

    public QuerySourceDestination getDestination(String dkey)
    {
        return destinations.get(dkey);
    }

    public int addDestination(String dkey, QuerySourceDestination src)
    {
        destinations.put(dkey, src);

        return destinations.size();
    }

    public QuerySourceDestination removeDestination(String dkey)
    {
        return destinations.remove(dkey);
    }

    public void removeAllDestinations()
    {
        sources.clear();
    }

    public int getReturnLevel(String p)
    {
        return returnLevels.get(p);
    }

    public int addReturnLevel(String r, int l)
    {
        returnLevels.put(r, l);

        return returnLevels.size();
    }

    public void clear()
    {
        removeAllMatchedValues();
        removeAllReturnValues();

        returnLevels.clear();

        sources.clear();
        destinations.clear();
    }

    public boolean parseQuery() throws Exception
    {
        clear();

        String lrparts[] = queryString.split("->");

        String lhs = lrparts[0].trim();
        String rhs = null;
        String dest = null;

        if(lrparts.length == 2)
        {
            rhs = lrparts[1].trim();

            String rdparts[] = rhs.split(":=");

            if(rdparts.length == 2)
            {
                rhs = rdparts[0].trim();
                dest = rdparts[1].trim();
            }
            else if(rdparts.length == 1)
            {
                rhs = rdparts[0].trim();

                if(lrparts[1].contains(":=")) {
                    dest = "";
                }
            }
        }

//        if(rhs != null && rhs.equals("S"))
//        {
//            // Ad-hoc
//            addReturnLevel("S", SSFQuery.SENTENCE);
//        }

        boolean parsed = parseLHS(lhs);

        if(parsed && rhs != null) {
            parsed = parseRHS(rhs);
        }

        if(parsed && dest != null) {
            parsed = parseDestination(dest);
        }

        return parsed;
    }

    protected boolean parseLHS(String lhs) throws Exception
    {
        rootMatchNode = new SSFQueryMatchNode(this, lhs);
        
        return rootMatchNode.parseQuery();
    }

    protected boolean parseRHS(String rhs) throws Exception
    {
        rootActionNode = new SSFQueryActionNode(this, rhs);

        return rootActionNode.parseQuery();
    }

    protected boolean parseDestination(String dest) throws Exception
    {
        rootDestinationNode = new SSFQueryDestinationNode(this, dest);

        return rootDestinationNode.parseQuery();
    }

    public LinkedHashMap executeQuery(SSFNode node) throws Exception
    {
        boolean match = match(node);

        LinkedHashMap returnMap = null;

        if(match && rootActionNode != null && !rootActionNode.isLeaf()) {
            returnMap = execute(node);
        }

//        if(match && rootDestinationNode != null)
//            send(node, null);

        if(match && rootActionNode != null && !rootActionNode.isLeaf()) {
            return returnMap;
        }

        if(match == false) {
            return null;
        }
        else
        {
            returnMap = new LinkedHashMap();
            returnMap.put(node, queryString);

            return returnMap;
        }
    }

    protected boolean match(SSFNode node) throws Exception
    {
        removeAllMatchedValues();

        return rootMatchNode.match(node, null);
    }

    protected LinkedHashMap execute(SSFNode node) throws Exception
    {
        return rootActionNode.execute(node);
    }

    public void send()
    {
        if(rootDestinationNode != null)
        {
            try {
                send(null);
            } catch (Exception ex) {
                Logger.getLogger(SSFQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected boolean send(SSFQueryOperatorType parentOperatorType) throws Exception
    {
//        removeAllReturnNodes();

        boolean sent = rootDestinationNode.send(parentOperatorType);
        
        if(sent)
        {
            Iterator<String> itr = getReturnKeys();

            while(itr.hasNext())
            {
                String rkey = itr.next();

                if(getReturnLevel(rkey) == SSFQuery.SENTENCE)
                {
                    SSFStory ssfStory = getDestination(rkey).getDocument();

                    ssfStory.clearHighlights();
                    
                    if(getDestination(rkey).getFormat().equalsIgnoreCase("ssf")) {
                        ssfStory.save(getDestination(rkey).getLocation(), getDestination(rkey).getCharset());
                    }
                    else if(getDestination(rkey).getFormat().equalsIgnoreCase("bf")) {
                        ssfStory.saveBracketForm(getDestination(rkey).getLocation(), getDestination(rkey).getCharset(), 1);
                    }
                    else if(getDestination(rkey).getFormat().equalsIgnoreCase("pos")) {
                        ssfStory.savePOSTagged(getDestination(rkey).getLocation(), getDestination(rkey).getCharset());
                    }
                    else if(getDestination(rkey).getFormat().equalsIgnoreCase("pos")) {
                        ssfStory.saveRawText(getDestination(rkey).getLocation(), getDestination(rkey).getCharset());
                    }
                }
                else
                {
                    PrintStream destinationStream = getDestination(rkey).getStream();

                    destinationStream.close();
                }
            }
        }

        return sent;
    }

    public boolean isCommand()
    {
        if(rootMatchNode.getOperator().equals(SSFQueryOperatorType.COMMAND)) {
            return true;
        }

        return false;
    }

    public SanchayTableModel query(SSFStory story, SSFQuery ssfQuery)
    {
        LinkedHashMap<QueryValue, String> matches = story.getMatchingValues(ssfQuery);

        if(matches.size() == 0 && !ssfQuery.isCommand())
        {
//            JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("No_match_found."), sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        SanchayTableModel matchesTable = new SanchayTableModel(new String[]{"Sentence", "Matched Node"}, 2);
        SanchayTableModel.mapToTable(matches, matchesTable, true, false);
        matchesTable.trimRows(false);

        if(matchesTable.getRowCount() == 0)
        {
//            JOptionPane.showMessageDialog(this, sanchay.GlobalProperties.getIntlString("No_match_found."), sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            if(!ssfQuery.isCommand()) {
                return null;
            }
            else
            {
                matchesTable.addRow();
                matchesTable.setValueAt("Command executed.", 0, 0);
            }
        }

        int rcount = matchesTable.getRowCount();

        matchesTable.addColumn("Context");
        matchesTable.addColumn("Referred Node");

        Iterator<QueryValue> itr = matches.keySet().iterator();

        int i = 0;

        while(itr.hasNext())
        {
            QueryValue value = itr.next();

            if(value instanceof SSFNode)
            {
                SSFNode node = (SSFNode) value;

//                if(ssfQuery.getRootMatchNode().getOperator().equals(SSFQueryOperatorType.ON_DS))
//                {
//                    SSFNode mnode = mmNode.findNodeByName(node.getAttributeValue("name"));
//                    mnode.isHighlighted(true);
//                }
//                 else
                    node.isHighlighted(true);

                matchesTable.setValueAt(node.convertToBracketForm(1), i, 1);

                SSFNode contextNode = (SSFNode) node.getParent();

                if(contextNode != null)
                {
                    matchesTable.setValueAt(contextNode.convertToBracketForm(1), i, 2);
                }

                if(node instanceof SSFPhrase)
                {
                    SSFNode referredNode = ((SSFPhrase) node).getReferredNode("drel");

                    if(referredNode != null) {
                        matchesTable.setValueAt(referredNode.convertToBracketForm(1), i, 3);
                    }
                }
            }
            else if(value instanceof StringQueryValue)
            {
                String svalue = (String) value.getQueryReturnValue();
                Object ovalue = value.getQueryReturnValue();

                if(ovalue instanceof SSFNode)
                {
                    SSFNode node = (SSFNode) ovalue;

//                    if(ssfQuery.getRootMatchNode().getOperator().equals(SSFQueryOperatorType.ON_DS))
//                    {
//                        LinkedHashMap cfgToMMTreeMapping = new LinkedHashMap(0, 10);
//                        SSFPhrase mmNode = ((SSFPhrase) node.getRoot()).convertToMMNode(cfgToMMTreeMapping, false);
//
//                        SSFNode mnode = mmNode.findNodeByName(node.getAttributeValue("name"));
//                        mnode.isHighlighted(true);
//                    }
//                    else
                        node.isHighlighted(true);

                    matchesTable.setValueAt(svalue, i, 1);

                    SSFNode contextNode = (SSFNode) node.getParent();

                    if(contextNode != null)
                    {
                        matchesTable.setValueAt(contextNode.convertToBracketForm(1), i, 2);
                    }

                    if(node instanceof SSFPhrase)
                    {
                        SSFNode referredNode = ((SSFPhrase) node).getReferredNode("drel");

                        if(referredNode != null) {
                            matchesTable.setValueAt(referredNode.convertToBracketForm(1), i, 3);
                        }
                    }
                }
            }

            i++;
        }

        return matchesTable;
    }

    public SanchayTableModel queryInFiles(SSFQuery ssfQuery, LinkedHashMap<File, SSFStory> selStories, String comment)
    {
        SanchayTableModel matches = null;

            int ccount = 0;

            Iterator<File> itr = selStories.keySet().iterator();

            int i = 0;

            while(itr.hasNext()) {
                File file = itr.next();

                SSFStory story = selStories.get(file);

                if(i == 0) {
                    matches = query(story, ssfQuery);

                    if(matches == null) {
                        continue;
                    }

                    matches.addColumn(sanchay.GlobalProperties.getIntlString("File"));
                    matches.addColumn(sanchay.GlobalProperties.getIntlString("Comment"));

                    int rcount = matches.getRowCount();
                    ccount = matches.getColumnCount();

                    for (int j = 0; j < rcount; j++) {

                        Object val = matches.getValueAt(j, 0);

                        if(val != null && !val.equals(""))
                        {
                            matches.setValueAt(file, j, ccount - 2);
                            matches.setValueAt(comment, j, ccount - 1);
                        }
                    }
                }
                else
                {
                    SanchayTableModel fileMatches = null;

                    fileMatches = query(story, ssfQuery);

                    if(fileMatches == null) {
                        continue;
                    }

                    if(matches == null)
                    {
                        matches = fileMatches;

                        matches.addColumn(sanchay.GlobalProperties.getIntlString("File"));
                        matches.addColumn(sanchay.GlobalProperties.getIntlString("Comment"));

                        int rcount = matches.getRowCount();
                        ccount = matches.getColumnCount();

                        for (int j = 0; j < rcount; j++)
                        {
                            Object val = matches.getValueAt(j, 0);

                            if(val != null && !val.equals(""))
                            {
                                matches.setValueAt(file, j, ccount - 2);
                                matches.setValueAt(comment, j, ccount - 1);
                            }
                        }
                    }
                    else
                    {
                        int rcount = matches.getRowCount();
                        int frcount = fileMatches.getRowCount();

                        for (int j = 0; j < frcount; j++) {
                            Vector rowData = fileMatches.getRow(j);
                            rowData.add("");

                            matches.addRow(rowData);

                            Object val = matches.getValueAt(rcount + j, 0);

                            if(val != null && !val.equals(""))
                            {
                                matches.setValueAt(file, rcount + j, ccount - 2);
                                matches.setValueAt(comment, rcount + j, ccount - 1);
                            }
                        }

                        matches.insertRow(rcount);
                    }
                }

                i++;

//                if(ssfQuery.getRootActionNode() != null || ssfQuery.isCommand())
//                {
//                    try
//                    {
//                        story.save(file.getAbsolutePath(), cs);
//                    } catch (FileNotFoundException ex)
//                    {
//                        Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (UnsupportedEncodingException ex)
//                    {
//                        Logger.getLogger(SyntacticAnnotationWorkJPanel.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
            }

        return matches;
    }

    public static void main(String args[])
    {
//        String query = "C.l='ne' AND C.t='PSP' -> C.A(1).a('drel')=k1";
//        String query = "C.l='ne' OR C.t='x'";
//        String query = "C.l='ne' AND C.t='x' AND A(1).t='NP'";
//        String query = "C.t='PSP' and N.l='lie' -> C.t='Prep' AND A.t='PP' AND N.l='लिए' AND C.a['drel']=A[1].a['name']";
//        String query = "C.t='XC' -> C.t=N.t+'C'";
//        String query = "C.A.t='NP'";
//        String query = "C.a['name']='NP1'";
//        String query = "C.t='PSP' and M.l='ke'";
//        String query = "C.t/q='PSP' and M[q].l='lie'";
//        String query = "N.t/q='PSP' and M[q].l='lie'";
//        String query = "ReallocateNames";
        String query = "C.t='PSP' -> C.N";

        SSFNode pnode = null;
        SSFNode node = null;
        SSFNode nextNode = null;

        try
        {
            pnode = new SSFPhrase("0", "((", "NP", "");
            pnode.setAttributeValue("name", "NP1");
            node = new SSFLexItem("0", "ke", "PSP", "");
//            node = new SSFLexItem("0", "ke", "XC", "");
            nextNode = new SSFLexItem("0", "lie", "PSP", "");
//            nextNode = new SSFLexItem("0", "lie", "NN", "");

            pnode.add(node);
            pnode.add(nextNode);
        } catch (Exception ex)
        {
            Logger.getLogger(SSFQuery.class.getName()).log(Level.SEVERE, null, ex);
        }

        SSFQuery ssfQuery = new SSFQuery(query);
        
        try
        {
            ssfQuery.parseQuery();

            System.out.println("Match (node): " + ssfQuery.match(node));
            System.out.println("Match (nextNode): " + ssfQuery.match(nextNode));
            System.out.println("Match (pnode): " + ssfQuery.match(pnode));

            System.out.println("Is command: " + ssfQuery.isCommand());

            ssfQuery.executeQuery(node);

            node.print(System.out);
            pnode.print(System.out);
        } catch (Exception ex)
        {
            Logger.getLogger(SSFQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
