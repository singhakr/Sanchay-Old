/*
 * Main.java
 *
 * Created on November 10, 2007, 1:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.corpus.blog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlparser.filters.RegexFilter;
import sanchay.GlobalProperties;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author Anil Kumar Singh
 */
public class BlogCorpusMain {

    org.htmlparser.parserapplications.SiteCapturer siteCapturer;
    
    /** Creates a new instance of Main */
    public BlogCorpusMain() {
        
        Properties systemSettings = System.getProperties();
        systemSettings.put("http.proxyHost", "192.168.36.204");
        systemSettings.put("http.proxyPort", "8080");
        System.setProperties(systemSettings);        
    }
    
    public void rearrangeHuffingtonPostBlogs(File inDir, File outDir) throws Exception, FileNotFoundException, IOException 
    {
        if(inDir.isDirectory() == false)
            return;

        File blogDirs[] = inDir.listFiles();
        
        int newFiles = 0;
        
        for (int i = 0; i < blogDirs.length; i++)
        {
            System.err.println("Processing blog: " + blogDirs[i].getName());

            File blogFiles[] = blogDirs[i].listFiles();            
            
            for (int j = 0; j < blogFiles.length; j++)
            {
                if(blogFiles[j].isFile())
                {
                    org.htmlparser.Parser parser = new org.htmlparser.Parser(blogFiles[j].getAbsolutePath());

                    String regex = "(?dum);nickname=([^;]+);";
                    RegexFilter nickFilter = new RegexFilter(regex);

                    org.htmlparser.util.NodeList elements = null;

                    try
                    {
                        elements = parser.extractAllNodesThatMatch(nickFilter);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    org.htmlparser.Node blogInfoElement = elements.elementAt(0);

                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(blogInfoElement.getText());

                    String blogName = "";

                    if(m.find())
                    {
                        blogName = m.group(1);

                        File outBlogDir = new File(outDir, blogName);

                        if(outBlogDir.exists() == false)
                            outBlogDir.mkdirs();

                        File outBlogFile = new File(outBlogDir, blogFiles[j].getName());

                        if(outBlogFile.exists() == false)
                        {
                            try {
                                UtilityFunctions.copyFile(blogFiles[j], outBlogFile);
                                newFiles++;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        else
                        {
                            System.err.println(GlobalProperties.getIntlString("File_already_exists:_") + outBlogFile.getParentFile().getName() + "/" + outBlogFile.getName());
                        }
                    }
                    else
                    {
                        System.err.println(GlobalProperties.getIntlString("Blog_info_not_found_for_file:_") + blogFiles[j].getParentFile().getName() + "/" + blogFiles[j].getName());
                    }
                }
                else
                {
                    System.err.println(GlobalProperties.getIntlString("Directory_instead_of_file:_") + blogFiles[j].getParentFile().getName() + "/" + blogFiles[j].getName());
                }
            }
        }
        
        System.err.println(GlobalProperties.getIntlString("Number_of_new_files:_") + newFiles);
    }
    
    public void capturePages(File inDir, File outDir, org.htmlparser.NodeFilter outerFilter,
            org.htmlparser.NodeFilter innerFilter, String prefix, boolean captureResources) throws Exception, FileNotFoundException, IOException 
    {
        if(inDir.isFile())
        {
            org.htmlparser.Parser parser = new org.htmlparser.Parser(inDir.getAbsolutePath());
            
            org.htmlparser.util.NodeList elements = null;

            try
            {
                elements = parser.extractAllNodesThatMatch(outerFilter);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            int pcount = elements.size();        
            for(int i = 0; i < pcount; i++)
            {
                org.htmlparser.Node element = (org.htmlparser.Node) elements.elementAt(i);
                
                org.htmlparser.util.NodeList innerElements = element.getChildren().extractAllNodesThatMatch(innerFilter, true);

                if(innerElements == null)
                    continue;
                
                int count = innerElements.size();
                for (int j = 0; j < count; j++)
                {
                    org.htmlparser.Node innerElement = (org.htmlparser.Node) innerElements.elementAt(j);

//                    if(innerElement.getClass().isAssignableFrom(org.htmlparser.Tag.class) == false)
//                        continue;
                    
                    String urlStr = ((org.htmlparser.Tag) innerElement).getAttribute("href");
                    
                    if(urlStr == null || urlStr.equals(""))
                        continue;                        

                    URL url = new URL(urlStr);
                    
                    siteCapturer = new org.htmlparser.parserapplications.SiteCapturer();
                    siteCapturer.setCaptureResources(captureResources);

                    siteCapturer.setSource(urlStr);

//                    String tgtPath = outDir.getAbsolutePath() + prefix + new File(url.getPath()).getName();
                    String tgtPath = outDir.getAbsolutePath() + "/" + new File(url.getPath()).getName();
                    siteCapturer.setTarget(tgtPath);

                    try {
                        siteCapturer.capture();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    File inTgtFile = new File(tgtPath);
                    
                    File files[] = inTgtFile.listFiles();
                
                    if(files != null)
                    {
                        int fcount = files.length;

                        if(fcount == 0)
                        {
                            inTgtFile.delete();
                            break;
                        }
                        else
                        {
                            for (int k = 0; k < fcount; k++)
                            {
                                File newFile = new File(inTgtFile.getParent(), inTgtFile.getName() + "-" + files[k].getName());

                                try {
                                    UtilityFunctions.copyFile(files[k], newFile);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                files[k].delete();
                            }

                            inTgtFile.delete();
                        }
                    }                
                }
            }
        }
        else if(inDir.isDirectory())
        {
            File files[] = inDir.listFiles();
            outDir.mkdirs();
            
//            prefix += inDir.getName();
            
            for (int i = 0; i < files.length; i++)
            {
                capturePages(files[i], outDir, outerFilter, innerFilter, prefix, captureResources);
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BlogCorpusMain main = new BlogCorpusMain();
        
        // TODO code application logic here
//        File inPath = new File("F:\\docs\\corpus-building\\lexmasterclass\\sanchay-site-capture\\huffingtonpost-blogs");
//        File outPath = new File("E:\\blog-corpus\\huffingtonpost-blogs-posts");
//        
//        File files[] = inPath.listFiles();
//        
//        TagNameFilter divTagFilter = new TagNameFilter("div");
//        TagNameFilter innerFilter = new TagNameFilter("a");
// 
//        HasAttributeFilter postFilter = new HasAttributeFilter("class", "read_post");
//        
//        AndFilter outerFilter = new AndFilter(new NodeFilter[] {divTagFilter, postFilter} );
//                
//        for (int i = 0; i < files.length; i++)
//        {
//            System.err.println("Processing blog: " + files[i].getName());
//            File file = files[i];
//            
//            try {                
//                main.capturePages(file, new File(outPath, file.getName()), outerFilter, innerFilter, "", false);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
        
        File inPath = new File("E:\\blog-corpus\\huffingtonpost-blogs-posts");
        File outPath = new File("E:\\blog-corpus\\huffingtonpost-blogs-rearranged");
        
        try {
            main.rearrangeHuffingtonPostBlogs(inPath, outPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
}
