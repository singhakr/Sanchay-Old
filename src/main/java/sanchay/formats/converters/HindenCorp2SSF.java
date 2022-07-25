/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.formats.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFPhrase;

/**
 *
 * @author anil
 */
public class HindenCorp2SSF {
    
    public static void convert(String hindenCorpPath, int splitSize) throws UnsupportedEncodingException, FileNotFoundException, IOException
    {
        File hindenCorpFile  = new File(hindenCorpPath);
        
        File srcFile = new File(hindenCorpFile.getParent(), "src");
        File tgtFile = new File(hindenCorpFile.getParent(), "tgt");
        
        if(srcFile.exists() == false)
        {
            srcFile.mkdir();
        }

        if(tgtFile.exists() == false)
        {
            tgtFile.mkdir();
        }
        
        BufferedReader lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(hindenCorpPath), "UTF-8"));

        String line = "";        
	int lineNum = 0;
        int splitNum = 0;

        SSFStory srcStory = new SSFStoryImpl();
        SSFStory tgtStory = new SSFStoryImpl();
        
        SSFSentenceImpl srcSen = null;
        SSFSentenceImpl tgtSen = null;

        while((line = lnReader.readLine()) != null)
        {
            line = line.trim();
            
            if(line.equals("") == false)
            {
                String fields[] = line.split("\\t");
                
                srcSen = new SSFSentenceImpl();
                tgtSen = new SSFSentenceImpl();
                
                srcSen.setAttributeValue("corpSrc", fields[0]);
                tgtSen.setAttributeValue("corpSrc", fields[0]);
                
                srcSen.setAttributeValue("alnTyp", fields[1]);
                tgtSen.setAttributeValue("alnTyp", fields[1]);
                
                srcSen.setAttributeValue("alnQual", fields[2]);
                tgtSen.setAttributeValue("alnQual", fields[2]);
                
                srcSen.setRoot(getRoot(fields[3]));
                tgtSen.setRoot(getRoot(fields[4]));
            }
            
            srcStory.addSentence(srcSen);
            tgtStory.addSentence(tgtSen);
            
            if(lineNum != 0 && lineNum % splitSize == 0)
            {
                srcStory.save(srcFile.getAbsolutePath() + "/" + hindenCorpFile.getName() + "-" + ++splitNum + ".src.txt", "UTF-8");
                tgtStory.save(tgtFile.getAbsolutePath() + "/" + hindenCorpFile.getName() + "-" + splitNum + ".tgt.txt", "UTF-8");

                srcStory = new SSFStoryImpl();
                tgtStory = new SSFStoryImpl();
            }

            if(lineNum != 0 && lineNum % 1000 == 0)
            {
                System.out.println("Lines processed: " + lineNum );
            }
            
            lineNum++;
        }
    }
    
    private static SSFPhrase getRoot(String hindenCorpString)
    {
        SSFPhrase root = new SSFPhrase("0", "", "SSF", (FeatureStructures) null);
        
        String tokens[] = hindenCorpString.split("\\s+");
        
        for (String token : tokens) {
//            System.err.println("token: " + token);
            
            String parts[] = token.split("\\|");
            
            String wrd = parts[0];
            String wrdRoot = parts[1];
            String morphInfo = parts[2];
            String posTag = "";

            SSFLexItem word = new SSFLexItem("0", wrd, "", (FeatureStructures) null);
            
            word.setAttributeValue("lex", wrdRoot);
            
            if(!morphInfo.contains(".") || morphInfo.equals("."))
            {
                posTag = morphInfo;
                
                word.setName(posTag);
            }
            else
            {                
                String morphParts[] = morphInfo.split("\\.");
                
                word.setName(morphParts[0]);

                if(morphParts.length > 1)
                {
                    word.setAttributeValue("cat", morphParts[1]);
                }
                
                if(morphParts.length > 2)
                {
                    word.setAttributeValue("gend", morphParts[2]);
                }

                if(morphParts.length > 3)
                {
                    word.setAttributeValue("num", morphParts[3]);
                }

                if(morphParts.length > 4)
                {
                    word.setAttributeValue("pers", morphParts[4]);
                }

                if(morphParts.length > 5)
                {
                    word.setAttributeValue("case", morphParts[5]);
                }
            }            
            
            root.addChild(word);
        }
        
        root.reallocateId("0");
        
        return root;
    }
    
    public static void main(String args[])
    {
        try {
            HindenCorp2SSF.convert("/home/anil/projects/parallel-corpora-alignment/hindencorp05.export.manual",
                    500);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HindenCorp2SSF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HindenCorp2SSF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HindenCorp2SSF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
