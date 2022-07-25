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
import java.util.Arrays;
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
 * @author Rahul
 */
public class SanityCheckFuncs_1 {

    private File errFile;   // Input Directory
    private File usefulDir;  // Tools program Directory
    private String lang;

    private List POSTags;
    private List ChunkTags;
    private SortedMap MorphTags;

    public SanityCheckFuncs_1()
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
            List list = new ArrayList();
            list.addAll(Arrays.asList(slist));
            
            MorphTags.put(parts[0],list);
            //System.out.println(parts[0]+"\t"+list.toString());
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
                    
                        String fsss=fss.toString();

                        String[] feats=fsss.split("\\|");
                        if(feats.length>=2){
                            String ErrID=senID+"::"+nodeID;
                             String Errmsg="Morph Error: Node has "+feats.length+" feature structures";
                             String context="";
                            out.write(ErrID+"\t"+nName+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
                        }
                    
                    fs = fss.getAltFSValue(0);
                    List atVector = fs.getAttributeNames();
                   // System.out.println("atList:\t"+atVector.toString());
                    for(int k=0;k<atVector.size();k++)
                    {
                        String attr = (String) atVector.get(k);
                        String val = fs.getAttributeValue(attr).makeString();
                        if(attr.equals("drel") || attr.equals("dmrel"))
                        {
                            int ind=val.length();
                            ind=val.indexOf(":");
                            if(ind>0) {
                                val=val.substring(0, ind);
                            }
                        }
                        if(!MorphTags.containsKey(attr))
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
                else{

                      String ErrID1=senID+"::"+nodeID;
                      String Errmsg="";
                      String chunk = node.getName();
                      Errmsg="Morph Error: Node has no morph analysis";
                      String context=node.getLexData()+"_"+node.getName()+" ";
                      out.write(ErrID1+"\t"+chunk+"\t"+context+"\t"+" "+"\t"+ifile.getAbsolutePath()+"\t"+Errmsg+"\n");
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
                        String Errmsg="Chunk Error: Node outside chunk";
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