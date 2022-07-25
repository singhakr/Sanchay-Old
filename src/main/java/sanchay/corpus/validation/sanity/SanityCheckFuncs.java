/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.validation.sanity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.FeatureStructure;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.features.FeatureValue;
import sanchay.corpus.ssf.features.impl.FSProperties;
import sanchay.corpus.ssf.features.impl.FeatureStructuresImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;

/**
 *
 * @author ambati
 */
public class SanityCheckFuncs {

    private File errFile;   // Input Directory
    private File usefulDir;  // Tools program Directory
    private String lang;
    private List POSTags;
    private List ChunkTags;
    private SortedMap<String, List<String>> MorphTags;

    public SanityCheckFuncs()
    {
        
    }

    public void setLanguage(String language)
    {
        lang = language;
    }

    public void setToolsDir(String dir) throws FileNotFoundException, IOException
    {
        usefulDir = new File(dir);
    }

    public void intPOSCheck() throws FileNotFoundException, IOException
    {
        POSTags = new ArrayList();
        File poslist = new File(usefulDir.getAbsoluteFile()+"/POS_tags.txt");
        fillPOSTags(poslist);
    }

    public void fillPOSTags(File posFile) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = new BufferedReader(new FileReader(posFile));

        String line="";
        while ((line = inReader.readLine()) !=null)
        {
            line=line.trim();
            POSTags.add(line);
        }
    }

    //Check POS Errors
    public void checkPOSErrors(File ifile, File ofile) throws IOException, InterruptedException, Exception
    {
        String inFile = ifile.getAbsolutePath();
        SSFStory story = new SSFStoryImpl();
        story.readFile(inFile);
        int scount = story.countSentences();
        BufferedWriter out = new BufferedWriter(new FileWriter(ofile,true));

        for(int i = 0; i < scount; i++)
        {
            SSFSentence sen = story.getSentence(i);
            //System.out.println("Sentence id is::"+Integer.toString(i+1)+"\n");
            SSFPhrase root = sen.getRoot();
            List leaves = root.getAllLeaves();
            int lcount = root.getLeafCount();
            for (int j = 0; j < leaves.size(); j++)
            {
                SSFNode node = (SSFNode) leaves.get(j);
                //System.out.println("node is::"+node.makeString());
                String senID= String.valueOf(i+1);//String senID= sen.getId();
                String pos = node.getName();
                String nodeID="";
                FeatureStructure fs = null;
                FeatureStructures fss = null;

                fss = node.getFeatureStructures();
                if(fss!=null && !fss.makeString().equals(""))
                {
                    fs = fss.getAltFSValue(0);
                    FeatureValue fv = fs.getAttributeValue("name");
                    if(fv!=null)
                    {
                        nodeID=fv.makeString();
                    }
                }
                
                if(!POSTags.contains(pos))
                {
                    String context=node.getLexData();
                    //String ErrID=nodeID+"::"+senID+"::"+ifile.getName();
                    String ErrID=senID+"::"+nodeID;
                    String Errmsg="POS Error: POS tag not in the list of pre-defined tags";
                    //System.out.println(pos+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                    //out.write(pos+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                    out.write(ErrID+"\t"+pos+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                }
            }
        }
        out.close();
    }

    public void intChunkCheck() throws FileNotFoundException, IOException
    {
        ChunkTags = new ArrayList();
        File chunklist = new File(usefulDir.getAbsoluteFile()+"/Chunk_tags.txt");
        fillChunkTags(chunklist);
    }

    public void fillChunkTags(File chunkFile) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = new BufferedReader(new FileReader(chunkFile));

        String line="";
        while ((line = inReader.readLine()) !=null)
        {
            line=line.trim();
            ChunkTags.add(line);
        }
    }


    public void intMorphCheck() throws FileNotFoundException, IOException
    {
        MorphTags = new TreeMap();
        File chunklist = new File(usefulDir.getAbsoluteFile()+"/Morph_tags.txt");
        fillMorphTags(chunklist);
    }

    public void fillMorphTags(File chunkFile) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = new BufferedReader(new FileReader(chunkFile));

        String line="";
        while ((line = inReader.readLine()) !=null)
        {
            line=line.trim();
            String[] parts=line.split("\t");
            String[] slist = parts[1].split("::");
            List<String> list = new ArrayList<String>();
            for(int i=0;i<slist.length;i++)
            {
                list.add(slist[i]);
            }
            
            MorphTags.put(parts[0],list);
            //System.out.println(parts[0]+"\t"+list.toString());
        }
    }

    public void checkMorphATV(SSFNode node, String senID, File ifile, BufferedWriter out) throws IOException
    {
        String nName = node.getName();
                String nodeID="";
                FeatureStructure fs = null;
                FeatureStructures fss = null;
                fss = node.getFeatureStructures();
                if(fss!=null && !fss.makeString().equals(""))
                {
                    fs = fss.getAltFSValue(0);
                    FeatureValue fv = fs.getAttributeValue("name");
                    if(fv!=null)
                    {
                        nodeID=fv.makeString();
                    }
                }

                fss = node.getFeatureStructures();
                if(fss!=null && !fss.makeString().equals(""))
                {
                    fs = fss.getAltFSValue(0);
                    List atVector = fs.getAttributeNames();
                    //System.out.println("atList:\t"+atVector.toString());
                    for(int k=0;k<atVector.size();k++)
                    {
                        String attr = (String) atVector.get(k);
                        String val = fs.getAttributeValue(attr).makeString();
                        if(attr.equals("drel") || attr.equals("dmrel"))
                        {
                            int ind=val.length();
                            ind=val.indexOf(":");
                            if(ind>0)
                                val=val.substring(0, ind);
                        }

                        //System.out.println(attr+"\t"+MorphTags.toString());
                        if(MorphTags.containsKey(attr))
                        {
                            List mList = MorphTags.get(attr);
                            //System.out.println(attr+"\t"+mList.toString());
                            //if(!mList.contains(val) && !mList.contains("ANY"))
                            if(!mList.contains(val) && !mList.contains("ANY"))
                            {
                                String context=attr+"--"+val;

                                String ErrID=senID+"::"+nodeID;
                                String Errmsg="Morph Error: Value of attribute "+attr+" not in the predefined list";
                                //System.out.println(nName+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                                //System.out.println("ATTR: "+attr+mList.toString()+" value is "+val);
                                out.write(ErrID+"\t"+nName+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                            }
                        }
                        else
                        {
                            String context=attr+"--"+val;
                            String ErrID=senID+"::"+nodeID;
                            String Errmsg="Morph Error: Attribute "+attr+" not in the predefined list";
                            //System.out.println(nName+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                            out.write(ErrID+"\t"+nName+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                            //out.write(nName+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                        }
                    }

                }
    }

    //Check Morph Attr Errors
    public void checkMorphATVErrors(File ifile, File ofile) throws IOException, InterruptedException, Exception
    {
        String inFile = ifile.getAbsolutePath();
        SSFStory story = new SSFStoryImpl();
        story.readFile(inFile);
        int scount = story.countSentences();
        BufferedWriter out = new BufferedWriter(new FileWriter(ofile,true));

        for(int i = 0; i < scount; i++)
        {
            SSFSentence sen = story.getSentence(i);
            //System.out.println("Sentence id is::"+Integer.toString(i+1)+"\n");
            SSFPhrase root = sen.getRoot();
            String senID= String.valueOf(i+1);//String senID= sen.getId();

            List children = root.getAllChildren();
            for (int j = 0; j < children.size(); j++)
            {
                SSFNode node = (SSFNode) children.get(j);

                checkMorphATV(node, senID, ifile, out);
                for(int k=0;k<node.getChildCount();k++)
                {
                    SSFNode leaf = (SSFNode) node.getChildAt(k);
                    checkMorphATV(leaf, senID, ifile, out);
                }
            }
        }
        out.close();
    }
    
    //Check Chunk Errors
    public void checkChunkErrors(File ifile, File ofile) throws IOException, InterruptedException, Exception
    {
        String inFile = ifile.getAbsolutePath();
        SSFStory story = new SSFStoryImpl();
        story.readFile(inFile);
        int scount = story.countSentences();
        BufferedWriter out = new BufferedWriter(new FileWriter(ofile,true));

        for(int i = 0; i < scount; i++)
        {
            SSFSentence sen = story.getSentence(i);
            //System.out.println("Sentence id is::"+Integer.toString(i+1)+"\n");
            SSFPhrase root = sen.getRoot();

            List children = root.getAllChildren();
            for (int j = 0; j < children.size(); j++)
            {
                SSFNode node = (SSFNode) children.get(j);
                //System.out.println("node is::"+node.makeString());
                String senID= String.valueOf(i+1);//String senID= sen.getId();
                String chunk = node.getName();
                String nodeID="";
                FeatureStructure fs = null;
                FeatureStructures fss = null;
                
                fss = node.getFeatureStructures();
                if(fss!=null && !fss.makeString().equals(""))
                {
                    fs = fss.getAltFSValue(0);
                    FeatureValue fv = fs.getAttributeValue("name");
                    if(fv!=null)
                    {
                        nodeID=fv.makeString();
                    }
                }
                if(!ChunkTags.contains(chunk))
                {
                    String context="";
                    for(int k=0;k<node.getChildCount();k++)
                    {
                        SSFNode leaf = (SSFNode) node.getChildAt(k);
                        context+=leaf.getLexData()+"_"+leaf.getName()+" ";
                    }
                    //String ErrID=nodeID+"::"+senID+"::"+ifile.getName();
                    String ErrID=senID+"::"+nodeID;
                    String Errmsg="Chunk Error: Chunk tag not in the list of pre-defined tags";
                    //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                    //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                    out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                }
            }
        }
        out.close();
    }

    public void chunkBoundaryCheck(File ifile, File ofile) throws FileNotFoundException, IOException
    {   
        BufferedReader inReader = new BufferedReader(new FileReader(ifile));
        BufferedWriter out = new BufferedWriter(new FileWriter(ofile,true));
        String line="";
        int senStart=0,cStart=0,cID=0,senID=0;
        while ((line = inReader.readLine()) !=null)
        {
            if(line.startsWith("<Sentence"))
            {
                senStart=1;
                cID=0;
                cStart=0;
                senID++;
            }
            else if(line.startsWith("</Sentence"))
            {
                senStart=0;
            }
            else if(senStart==1)
            {
                String[] parts=line.split("\t");
                if(parts.length<2)
                {
                    
                }
                else if(parts[1].equals("(("))
                {
                    cID++;
                    if(cStart==0)
                    {
                        cStart=1;
                    }
                    else
                    {
                        String context=line.replace("\t", " ");
                        String nodeID="";
                        String regex = "name='.*?'";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(line);
                        if(m.find())
                        {
                            nodeID=m.group();
                            nodeID=nodeID.replace("name='","");
                            nodeID=nodeID.substring(0, nodeID.length()-1);
                        }
                        String chunk=parts[2];
                        
                        //String ErrID=nodeID+"::"+senID+"::"+ifile.getName();
                        String ErrID=senID+"::"+nodeID;
                        String Errmsg="Chunk Error: Nested Chunks";
                        //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                        //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                        out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                        senStart=0;
                    }
                }
                else if(parts[1].equals("))"))
                {
                    cStart=0;
                }
                else
                {
                    if(cStart!=1)
                    {

                        String context=line.replace("\t", " ");
                        String nodeID="";
                        String regex = "name='.*?'";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(line);
                        if(m.find())
                        {
                            nodeID=m.group();
                            nodeID=nodeID.replace("name='","");
                            nodeID=nodeID.substring(0, nodeID.length()-1);
                        }
                        String chunk=parts[2];

                        //String ErrID=nodeID+"::"+senID+"::"+ifile.getName();
                        String ErrID=senID+"::"+nodeID;
                        String Errmsg="Chunk Error: Nodes outside chunk";
                        //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                        //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                        out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                        senStart=0;
                    }
                }
            }
        }
        out.close();
    }


    public int checkMorphCount(SSFNode node)
    {
        //System.out.println("node is::"+node.makeString());
        
        FeatureStructure fs = null;
        FeatureStructures fss = null;

        fss = node.getFeatureStructures();
        if(fss.makeString().equals(""))
        {
            return 0;
        }
        else
        {
            return fss.countAltFSValues();
        }        
    }
    
    //Check Morph Errors
    public void checkMorphErrors(File ifile, File ofile) throws IOException, InterruptedException, Exception
    {
        String inFile = ifile.getAbsolutePath();
        SSFStory story = new SSFStoryImpl();
        story.readFile(inFile);
        int scount = story.countSentences();
        BufferedWriter out = new BufferedWriter(new FileWriter(ofile,true));

        for(int i = 0; i < scount; i++)
        {
            SSFSentence sen = story.getSentence(i);
            //System.out.println("Sentence id is::"+Integer.toString(i+1)+"\n");
            SSFPhrase root = sen.getRoot();

            String senID= String.valueOf(i+1);//String senID= sen.getId();
            
            String nodeID="";

            List leaves = root.getAllChildren();
            for (int j = 0; j < leaves.size(); j++)
            {
                SSFNode node = (SSFNode) leaves.get(j);
                String chunk = node.getName();
                int mcount = checkMorphCount(node);
                if(mcount!=1)
                {
                    String context="";
                    for(int k=0;k<node.getChildCount();k++)
                    {
                        SSFNode leaf = (SSFNode) node.getChildAt(k);
                        context+=leaf.getLexData()+"_"+leaf.getName()+" ";
                    }
                    context.trim();
                    
                    //String ErrID=nodeID+"::"+senID+"::"+ifile.getName();
                    String ErrID=senID+"::"+nodeID;
                    String Errmsg="Morph Error: Node has "+mcount+"morphs";
                    //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                    //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                    out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");

                }
            }

            List children = root.getAllChildren();
            for (int j = 0; j < children.size(); j++)
            {
                SSFNode node = (SSFNode) children.get(j);
                String chunk = node.getName();
                int mcount = checkMorphCount(node);
                if(mcount!=1)
                {
                    String context="";
                    for(int k=0;k<node.getChildCount();k++)
                    {
                        SSFNode leaf = (SSFNode) node.getChildAt(k);
                        context+=leaf.getLexData()+"_"+leaf.getName()+" ";
                    }
                    //String ErrID=nodeID+"::"+senID+"::"+ifile.getName();
                    String ErrID=senID+"::"+nodeID;
                    String Errmsg="Morph Error: Node has "+mcount+"morphs";
                    //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                    //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                    out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                }
            }
        }
        out.close();
    }

    public List createList(int i,SSFPhrase root,File ifile, BufferedWriter out) throws IOException
    {
        List nList = new ArrayList();
        List children = root.getAllChildren();
        for (int j = 0; j < children.size(); j++)
        {
            SSFNode node = (SSFNode) children.get(j);
            //System.out.println("node is::"+node.makeString());
            String senID= String.valueOf(i+1);//String senID= sen.getId();
            String chunk = node.getName();
            String nodeID="";

            String context="";
            for(int k=0;k<node.getChildCount();k++)
            {
                SSFNode leaf = (SSFNode) node.getChildAt(k);
                context+=leaf.getLexData()+"_"+leaf.getName()+" ";
            }
            context=context.trim();
            //String ErrID=nodeID+"::"+senID+"::"+ifile.getName();
            String ErrID=senID+"::"+nodeID;
            String Errmsg="";

            FeatureStructure fs = null;
            FeatureStructures fss = null;

            fss = node.getFeatureStructures();
            if(fss!=null && !fss.makeString().equals(""))
            {
                fs = fss.getAltFSValue(0);
                FeatureValue fv = fs.getAttributeValue("name");
                if(fv!=null)
                {
                    nodeID=fv.makeString();
                    //ErrID=nodeID+"::"+senID+"::"+ifile.getName();
                    ErrID=senID+"::"+nodeID;
                    if(nList.contains(nodeID))
                    {
                        Errmsg="General Error: Multiple nodes have same name attribute";
                        //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                        //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                        out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                        //out.write(fs.makeString()+"\n");
                    }
                    else
                    {
                        nList.add(nodeID);
                    }
                }
                else
                {
                    Errmsg="Morph Error: Node doesn't have name attribute";
                    //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                    out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                    //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                }
            }
            else
            {
                Errmsg="Morph Error: Node has no morph analysis";
                //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
            }
        }
        return nList;
    }

    //Check Tree Errors
    public void checkTreeErrors(File ifile, File ofile) throws IOException, InterruptedException, Exception
    {
        List nList = new ArrayList();
        String inFile = ifile.getAbsolutePath();
        SSFStory story = new SSFStoryImpl();
        story.readFile(inFile);
        int scount = story.countSentences();
        BufferedWriter out = new BufferedWriter(new FileWriter(ofile,true));

        for(int i = 0; i < scount; i++)
        {
            SSFSentence sen = story.getSentence(i);
            //System.out.println("Sentence id is::"+Integer.toString(i+1)+"\n");
            SSFPhrase root = sen.getRoot();
            int rCount=0;
            String senID= String.valueOf(i+1);//String senID= sen.getId();
            String nodeID="";
            String context="";
            String ErrID=nodeID+"::"+senID+"::"+ifile.getName();
            String Errmsg="";

            nList = createList(i,root,ifile,out);

            List children = root.getAllChildren();
            for (int j = 0; j < children.size(); j++)
            {
                SSFNode node = (SSFNode) children.get(j);
                //System.out.println("node is::"+node.makeString());
                
                String chunk = node.getName();
                context="";
                for(int k=0;k<node.getChildCount();k++)
                {
                    SSFNode leaf = (SSFNode) node.getChildAt(k);
                    context+=leaf.getLexData()+"_"+leaf.getName()+" ";
                }
                context=context.trim();

                FeatureStructure fs = null;
                FeatureStructures fss = null;

                fss = node.getFeatureStructures();
                if(fss!=null && !fss.makeString().equals(""))
                {
                    fs = fss.getAltFSValue(0);
                    
                    FeatureValue fv = fs.getAttributeValue("name");
                    
                    if(fv!=null)
                    {
                        nodeID=fv.makeString();
                    }
                    //ErrID=nodeID+"::"+senID+"::"+ifile.getName();
                    ErrID=senID+"::"+nodeID;
                    
                    fv = fs.getAttributeValue("drel");
                    if(fv==null) {
                        fv = fs.getAttributeValue("dmrel");
                    }
                    if(fv!=null)
                    {
                        String depHead=fv.makeString();
                        int ind=depHead.indexOf(":");
                        if(ind>0)
                        {
                            String depLabel=depHead.substring(0,ind);
                            String head=depHead.substring(ind+1);

                            if(!nList.contains(head))
                            {
                                Errmsg="Dependency Error: Node doesn't have a valid head";
                                //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                                //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                                out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                            }
                        }
                        else
                        {
                            Errmsg="Dependency Error: Node doesn't have a head";
                            //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                            //out.write(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                            out.write(ErrID+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                            
                        }
                    }
                    else
                    {
                            rCount+=1;
                    }
                }
            }
            if(rCount!=1)
            {
                Errmsg="Dependency Error: Tree has non-single ("+rCount+") roots";
                //System.out.println(chunk+"\t"+context+"\t"+ErrID+"\t"+Errmsg);
                //out.write("\t"+context+"\t"+ErrID+"\t"+Errmsg+"\n");
                out.write(ErrID+"\t"+" "+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
            }
        }
        out.close();
    }

    public void inits()
    {
        FSProperties fsp = new FSProperties();
        SSFProperties ssfp = new SSFProperties();

        try {
            fsp.read("./validation_tool/useful/data/props/fs-mandatory-attribs.txt",
                    "./validation_tool/useful/data/props/fs-other-attribs.txt",
                    "./validation_tool/useful/data/props/fs-props.txt",
                    "./validation_tool/useful/data/props/ps-attribs.txt",
                    "./validation_tool/useful/data/props/dep-attribs.txt",
                    "./validation_tool/useful/data/props/sem-attribs.txt",
                    "UTF-8"); //throws java.io.FileNotFoundException;
            ssfp.read("./validation_tool/useful/data/props/ssf-props.txt", "UTF-8"); //throws java.io.FileNotFoundException;

            FeatureStructuresImpl.setFSProperties(fsp);
            SSFNode.setSSFProperties(ssfp);


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