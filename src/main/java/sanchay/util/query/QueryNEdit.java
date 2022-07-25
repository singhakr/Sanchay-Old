/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.util.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author ambati
 */
public class QueryNEdit {

    public String inDir;
    public String outDir;
    public String charset = "UTF-8";
    public String query;
    public SyntacticCorpusContextQueryOptions contextOptions;
    public ProcessQuery pQuery;
    public SSFStory ssfStory;

    public QueryNEdit()
    {
        contextOptions = new SyntacticCorpusContextQueryOptions();
        pQuery = new ProcessQuery();
    }

    public void setInDir(String input)
    {
        inDir = input;
    }

    public void setOutDir(String input)
    {
        outDir = input;
    }
            
    public void analyseQuery(String queryFile, boolean exactMatch) throws IOException, Exception
    {
        File qfile = new File(queryFile);

        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(qfile), charset));

        String line="",lhs="",rhs="";
        while ((line = inReader.readLine()) != null)
        {
            if(!line.isEmpty())
            {
                String[] lrhs = line.split("->");
                lhs = lrhs[0];
                rhs = lrhs[1];
                contextOptions = pQuery.analyseLHS(lhs);
//                System.out.println(contextOptions.thisNodeOptions.getLexData());
//                System.out.println(contextOptions.thisNodeOptions.getTag());
//                System.out.println(contextOptions.thisNodeOptions.getDisWin());
                
                File ifile = new File(inDir);
                List nodes = new ArrayList();
                processDir(ifile, nodes, exactMatch);
                pQuery.modifyRHS(rhs,nodes);
                printFiles(ifile);
                //searchInFiles();
            }
        }
    }

    public LinkedHashMap<String, List> findMatches(String queryFile, boolean exactMatch) throws IOException, Exception
    {
        LinkedHashMap<String, List> matches = new LinkedHashMap<String, List>();

        File qfile = new File(queryFile);

        BufferedReader inReader = new BufferedReader(new InputStreamReader(new FileInputStream(qfile), charset));

        String line="";
        String lhs="";

        while ((line = inReader.readLine()) != null)
        {
            if(!line.isEmpty())
            {
                lhs = line;
                contextOptions = pQuery.analyseLHS(lhs);
//                System.out.println(contextOptions.thisNodeOptions.getLexData());
//                System.out.println(contextOptions.thisNodeOptions.getTag());
//                System.out.println(contextOptions.thisNodeOptions.getDisWin());

                File ifile = new File(inDir);
                List nodes = new ArrayList();
                processDir(ifile, nodes, exactMatch);

                if(matches != null)
                    matches.put(lhs, nodes);
            }
        }
        
        return matches;
    }

    public void printFiles(File ofile) throws IOException
    {
        //BufferedWriter out = new BufferedWriter(new FileWriter(ofile));
        
        //ssfStory.printRawText(System.out);
        PrintStream ps = new PrintStream(ofile.getAbsoluteFile(),"UTF-8");
        ssfStory.print(ps);
        ssfStory.print(System.out);
        //out.close();
    }
    
    
    public List searchInFiles(File ifile, boolean exactMatch) throws Exception
    {
        ssfStory = new SSFStoryImpl();
        ssfStory.readFile(ifile.getAbsolutePath());
        int count = ssfStory.countSentences();

        List matchedNodes = new ArrayList();
        List senNums = new ArrayList();
        //System.out.println("AMBATI");

        for (int i = 0; i < count; i++) {
            SSFSentence sen = ssfStory.getSentence(i);

            List<SSFNode> senNodes = null;
            senNodes = sen.findContext(contextOptions, exactMatch);
            
            //System.out.println("AMBATI");
            
            if(senNodes != null)
            {
                for(int j=0;j<senNodes.size();j++)
                {
                    SSFNode node = (SSFNode) senNodes.get(j);
//                    node.print(System.out);
                }
                int senCount = senNodes.size();

                for (int j = 0; j < senCount; j++) {
                    senNums.add(new Integer(i));
                }
                
                matchedNodes.addAll(senNodes);
            }
        }
        return matchedNodes;
    }

    public void processDir(File ifile, List matchVector, boolean exactMatch) throws IOException, Exception
    {
        if(ifile.isDirectory() == true)
        {
            File files[] = ifile.listFiles();
            for(int i = 0; i < files.length; i++)
            {
                processDir(files[i], matchVector, exactMatch);
            }
        }
        else if(ifile.isFile() == true)
        {
            matchVector.addAll(searchInFiles(ifile, exactMatch));
        }
    }

    public void printMatches(LinkedHashMap<String, List> matches, PrintStream ps)
    {
        Iterator<String> itr = matches.keySet().iterator();

        ps.println("");
        ps.println("");

        while(itr.hasNext())
        {
            String queryString = itr.next();

            ps.println("-------------------------------------------");
            ps.println("Query: " + queryString);
            ps.println("-------------------------------------------");

            List qmatches = matches.get(queryString);

            int count = qmatches.size();

            for (int i = 0; i < count; i++)
            {
                SSFNode node = (SSFNode) qmatches.get(i);

                SSFNode prevNode = node.getPrevious();
                SSFNode nextNode = node.getNext();

                if(prevNode != null && nextNode != null && prevNode.getName().equals("NN") && nextNode.getName().equals("NN"))
                    ps.println(prevNode.convertToPOSTagged() + " " + node.convertToPOSTagged() + " " + nextNode.convertToPOSTagged());

//                    ps.println(node.convertToPOSTagged());
            }

            ps.println("*******************************************");
        }

        ps.println("");
        ps.println("");
    }

    public void inits()
    {
        
    }

    public static void main(String[] args) throws IOException, Exception
    {
        QueryNEdit qne = new QueryNEdit();

        //String input = "/home/ambati/ltrc/treebank_parser/data/treebankNew/original/";
        //String output = "/home/ambati/ltrc/treebank_parser/data/treebankNew/nullFiles.ssf";

        //String input = "/home/ambati/Netbeans/LTRC/data/ssfsample/test.ssf";
//        String input = "/home/anil/bharat/sanchay-modifications/data/sample.wx";
//        String input = "/home/anil/bharat/sanchay-modifications/data/story_10_1.final.mod.utf8-1";
        String input = "/home/anil/corpora/ciil/postagged-ver-0.4-utf8";

//        String output = "/home/anil/bharat/sanchay-modifications/data/sampleout.wx";
        String rules = "/home/anil/bharat/sanchay-modifications/data/compound-nouns.txt";


        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

        try {
            fsp.readDefaultProps();
            ssfp.read(GlobalProperties.resolveRelativePath("props/ssf-props.txt"), "UTF-8"); //throws java.io.FileNotFoundException;

            FeatureStructuresImpl.setFSProperties(fsp);
            SSFNode.setSSFProperties(ssfp);
            
            qne.setInDir(input);
//            qne.setOutDir(output);

//            qne.analyseQuery(rules, false);
//            qne.printFiles(new File(output));
            LinkedHashMap<String, List> matches = qne.findMatches(rules, false);
            qne.printMatches(matches, System.out);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
