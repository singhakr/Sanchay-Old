/*
 * Created on Sep 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.common.types;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import sanchay.GlobalProperties;
import sanchay.annotation.common.AutomaticAnnotationJPanel;
import sanchay.corpus.discourse.DiscourseAnnotationJPanel;
import sanchay.corpus.manager.gui.NGramLMJPanel;
import sanchay.corpus.ssf.gui.SyntacticAnnotationWorkJPanel;
import sanchay.gui.common.ApplicationDescription;
import sanchay.gui.common.IntegratedResourceAccessorJPanel;
import sanchay.langenc.gui.LanguageEncodingIdentifierJPanel;
import sanchay.corpus.parallel.gui.ParallelMarkupWorkJPanel;
import sanchay.corpus.parallel.gui.ParallelSyntacticAnnotationWorkJPanel;
import sanchay.corpus.parallel.gui.SentenceAlignmentInterfaceJPanel;
import sanchay.corpus.parallel.gui.WordAlignmentInterfaceJPanel;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.gui.clients.SanchayRemoteWorkJPanel;
import sanchay.gui.common.SanchayCharmapJPanel;
import sanchay.gui.shell.SanchayShellJPanel;
import sanchay.propbank.Frameset;
import sanchay.propbank.gui.FramesetJPanel;
import sanchay.resources.shabdanjali.DictionaryEditorJPanel;
import sanchay.table.gui.SanchayTableJPanel;
import sanchay.text.editor.gui.RichTextEditorJPanel;
import sanchay.text.editor.gui.TextEditorJPanel;
import sanchay.text.spell.gui.DictionaryFSTJPanel;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.tree.gui.SanchayTreeDrawingJPanel;
import sanchay.util.gui.DocumentConverterJPanel;
import sanchay.util.gui.FileSplitterJPanel;
import sanchay.word.gui.WordListJPanel;
/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class ClientType extends SanchayType implements Serializable {
    
    public final int ord;
    private static Vector types = new Vector();
    private static String java_package;
    
    protected ServerType server;
    protected String title;
    protected String code;
    
    protected ClientType(String title, String code, String id, ServerType st, String pk) {
        super(id, pk);
        this.title = title;
        this.code = code;
        //   this.server = server;
        
        if (ClientType.last() != null) {
            this.prev = ClientType.last();
            //System.out.println("dsjkd");
            ClientType.last().next = this;
        }
        
        types.add(this);
        ord = types.size();
    }
    
    public static int size() {
        return types.size();
    }
    
    public static SanchayType first() {
        return (SanchayType) types.get(0);
    }
    
    public static SanchayType last() {
        if(types.size() > 0)
            return (SanchayType) types.get(types.size() - 1);
        
        return null;
    }
    
    public static SanchayType getType(int i) {
        if(i >=0 && i < types.size())
            return (SanchayType) types.get(i);
        
        return null;
    }
    
    public static Enumeration elements() {
        return new TypeEnumerator(ClientType.first());
    }
    
    public static SanchayType findFromClassName(String className) {
        Enumeration enm = ClientType.elements();
        return SanchayType.findFromClassName(enm, className);
    }
    
    public static SanchayType findFromId(String i) {
        Enumeration enm = ClientType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static ClientType findFromTitle(String t) {
        Enumeration enm = ClientType.elements();
        ClientType dt = null;

        while(enm.hasMoreElements())
        {
            dt = (ClientType) enm.nextElement();

            if(t.equals(dt.toString()))
                return dt;
        }

        return null;
    }

    public static SanchayType findFromCode(String c)
    {
        Enumeration enm = ClientType.elements();
        ClientType dt = null;

        while(enm.hasMoreElements())
        {
            dt = (ClientType) enm.nextElement();

            if(c.equals(dt.getCode()))
                return dt;
        }

        return null;
    }
    
    public static JPanel createSanchayClient(ClientType cl) {
        
        return (JPanel)identifyClient(cl);
    }
    
    
    public String toString() {
        if(title!=null)
            return this.title;
        else
            return " ";
        
    }

    public String getCode() {
        return code;
    }
    
    private static Object identifyClient(ClientType cl) {
        
        int t[] = null;
        
//        if(cl.equals(ClientType.BLOG_LIST_CAPTURE))
//            return new BlogListCaptureJPanel();
//        else if(cl.equals(ClientType.DICTIONARY_FST))
//            return new DictionaryFSTJPanel();
//        else if(cl.equals(ClientType.FILE_EXPLORER)) {
//            FileDisplayer fd = new FileDisplayer() {
//                
//                public boolean closeFile(EventObject e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//                
//                public void displayFile(String path, String charset, EventObject e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//                
//                public void displayFile(File file, String charset, EventObject e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//                
//                public String getDisplayedFile(EventObject e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//                
//                public String getCharset(EventObject e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//            };
//            return new FileExplorerJPanel(fd);
//        }
//        else if(cl.equals(ClientType.FILE_MATCHER))
//            return new FileMatcherJPanel();

        if(cl.equals(ClientType.SANCHAY_SSH_CLIENT))
            return new SanchayRemoteWorkJPanel();
        else if(cl.equals(ClientType.FILE_SPLITTER))
            return new FileSplitterJPanel();
        else if(cl.equals(ClientType.SANCHAY_CHARMAP))
            return new SanchayCharmapJPanel();
        else if(cl.equals(ClientType.INTEGRATED_RESOURCE_ACCESSOR))
            return new IntegratedResourceAccessorJPanel(true, true);
        else if(cl.equals(ClientType.WORD_LIST))
            return new WordListJPanel("hin::utf8");
        else if(cl.equals(ClientType.LANGUAGE_ENCODING_IDENTIFIER))
        {
            LanguageEncodingIdentifierJPanel panel = new LanguageEncodingIdentifierJPanel();
            return panel;
        }
        else if(cl.equals(ClientType.DICTIONARY_FST))
            return new DictionaryFSTJPanel();
//        else if(cl.equals(ClientType.FILTER_TRANSLATOR))
//            return new FilterTranslatorJPanel();
//        else if(cl.equals(ClientType.MNREAD))
//            return new MNReadJPanel();
//        else if(cl.equals(ClientType.PARALLEL_CORPUS_MARKUP))
//        {
//            ParallelSyntacticAnnotationWorkJPanel parallelSyntacticAnnotationWorkJPanel =
//                    new ParallelSyntacticAnnotationWorkJPanel();
//
//            return parallelSyntacticAnnotationWorkJPanel;
//        }
        else if(cl.equals(ClientType.NGRAM_LM))
            return new NGramLMJPanel();
        else if(cl.equals(ClientType.SENTENCE_ALIGNMENT_INTERFACE))
            return new SentenceAlignmentInterfaceJPanel(true);
        else if(cl.equals(ClientType.WORD_ALIGNMENT_INTERFACE))
            return new WordAlignmentInterfaceJPanel();
        else if(cl.equals(ClientType.DICTIONARY_EDITOR))
            return new DictionaryEditorJPanel();
        else if(cl.equals(ClientType.PARALLEL_MARKUP))
            return new ParallelMarkupWorkJPanel();
        else if(cl.equals(ClientType.DISCOURSE_ANNOTATION))
            return new DiscourseAnnotationJPanel("hin::utf8");
        else if(cl.equals(ClientType.DOCUMENT_CONVERTER))
            return new DocumentConverterJPanel();
//        else if(cl.equals(ClientType.PARALLEL_MARKUP_ANALYZER))
//            return new ParallelMarkupAnalyzerJPanel();
//        else if(cl.equals(ClientType.PROPERTIES_MANAGER))
//            return new PropertiesManagerJPanel("workspace/sanchay-components-pm.txt", "UTF-8");
        else if(cl.equals(ClientType.SANCHAY_EDITOR))
        {
            TextEditorJPanel textEditorJPanel = new TextEditorJPanel("hin::utf8", "UTF-8", null, null, TextEditorJPanel.DEFAULT_MODE);
            textEditorJPanel.showCommandButtons(false);
            return textEditorJPanel;
        }
        else if(cl.equals(ClientType.SANCHAY_RTF_EDITOR))
        {
            RichTextEditorJPanel richTextEditorJPanel = new RichTextEditorJPanel("hin::utf8", "UTF-8", null, null, RichTextEditorJPanel.DEFAULT_MODE);
            richTextEditorJPanel.showCommandButtons(false);
            return richTextEditorJPanel;
        }
//        else if(cl.equals(ClientType.SENTENCE_ALIGNER))
//            return new SenAlignWorkJPanel();
//        else if(cl.equals(ClientType.SIMILARITY_MEASURE))
//            return new SimilarityMeasuresJPanel();
//        else if(cl.equals(ClientType.SPELL_CHECKER))
//            return new SpellCheckerJPanel();
//        else if(cl.equals(ClientType.SSF_CORPUS_ANALYZER))
//            return new SSFCorpusAnalyzerJPanel();
        else if(cl.equals(ClientType.SANCHAY_SHELL))
            return new SanchayShellJPanel();
        else if(cl.equals(ClientType.SYNTACTIC_ANNOTATION))
            return new SyntacticAnnotationWorkJPanel();
        else if(cl.equals(ClientType.PROPBANK_ANNOTATION))
            return new SyntacticAnnotationWorkJPanel(true);
        else if(cl.equals(ClientType.FRAMESET_EDITOR))
            return new FramesetJPanel(new Frameset(), "hin::utf8");
        else if(cl.equals(ClientType.SANCHAY_HTML_BROWSER))
        {
//            HTMLBrowserJPanel clientJPanal = new HTMLBrowserJPanel();
//            clientJPanal.init();
//            
//            return clientJPanal;
        }
        else if(cl.equals(ClientType.AUTOMATIC_ANNOTATION))
            return new AutomaticAnnotationJPanel();
        else if(cl.equals(ClientType.TABLE_EDITOR))
            return new SanchayTableJPanel(false, SanchayTableJPanel.DEFAULT_MODE, "hin::utf8");
        else if(cl.equals(ClientType.TREE_EDITOR))
        {
            SSFPhrase root = null;

            try
            {
                root = new SSFPhrase("0", "", "S", "");
            } catch (Exception ex)
            {
                Logger.getLogger(SanchayTreeDrawingJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            return new SanchayTreeDrawingJPanel(root, SanchayMutableTreeNode.PHRASE_STRUCTURE_MODE, "eng::utf8", false);
        }
//        else if(cl.equals(ClientType.TREE_EDITOR))
//            return new SanchayTreeJPanel(t , 1000, "eng::utf8");
//        else if(cl.equals(ClientType.XML_EDITOR))
//            return new SanchayW3CXMLJPanel();
        
        return null;
    }
    
    public String applicationDescription(ClientType cl) {
        String returnThis=null;
//        try{
//            ApplicationDescription appl = (ApplicationDescription)identifyClient(cl);
//            returnThis= appl.applicationDescription();
//            if(returnThis==null) {
//                returnThis="Application description missing";
//            }
//        }catch(java.lang.ClassCastException e) {
//            returnThis= "Application description not found. Please check out the latest version for updates.";
//        } catch (Exception e) {
//            returnThis= "Unknown Exception. If this persists, please report it to sanchay@iiit.ac.in (preferably with detailed description of the sequence of events with led you here)";
//        } finally{
//            return returnThis;
//        }

        if(cl.equals(SANCHAY_SSH_CLIENT))
            returnThis = GlobalProperties.getIntlString("SSH_client_for_working_remotely.");
        else if(cl.equals(SANCHAY_EDITOR))
            returnThis = GlobalProperties.getIntlString("An_NLP_friendly_text_editor_with_customizable_support_for_languages_and_encodings.");
        else if(cl.equals(SANCHAY_RTF_EDITOR))
            returnThis = GlobalProperties.getIntlString("An_NLP_friendly_rich_text_editor_with_customizable_support_for_languages_and_encodings.");
        else if(cl.equals(SANCHAY_HTML_BROWSER))
            returnThis = "A very simple HTML browser for simple web pages.";
        else if(cl.equals(SANCHAY_SHELL))
            returnThis = "An very simple shell for Sanchay applications.";
        else if(cl.equals(TABLE_EDITOR))
            returnThis = GlobalProperties.getIntlString("A_table_editor_with_all_the_usual_facilities_for_editing_a_table.");
        else if(cl.equals(TREE_EDITOR))
            returnThis = "A tool for linguists to create and edit trees representing phrase structures";
        else if(cl.equals(INTEGRATED_RESOURCE_ACCESSOR))
            returnThis = GlobalProperties.getIntlString("Find,_replace_and_extract_text_with_or_without_regular_expressions_in_a_file_or_a_directory.");
        else if(cl.equals(WORD_LIST))
            returnThis = GlobalProperties.getIntlString("A_tool_for_building_a_word_list_from_different_kinds_of_sources_like_other_word_lists_and_corpora.");
        else if(cl.equals(LANGUAGE_ENCODING_IDENTIFIER))
            returnThis = GlobalProperties.getIntlString("One_of_the_most_accurate_tools_for_identifying_the_language-encoding_of_a_document_or_of_some_text._The_current_version_is_trained_for_54_language-encoding_pairs.");
        else if(cl.equals(DICTIONARY_FST))
            returnThis = GlobalProperties.getIntlString("A_tool_for_compiling_word_lists_as_an_FST._It_also_allows_the_listing_and_visualization_of_affixes.");
        else if(cl.equals(ClientType.AUTOMATIC_ANNOTATION))
            returnThis = GlobalProperties.getIntlString("A_CRF_based_automatic_annotation_tool_for_POS_tagging,_chunking_and_named_entity_recognition.");
        else if(cl.equals(SYNTACTIC_ANNOTATION))
            returnThis = GlobalProperties.getIntlString("A_user_friendly_interface_for_syntactic_and_other_kinds_of_annotation_(e.g._POS_tagging,_chunking,_dependency_markup_etc.).");
        else if(cl.equals(PROPBANK_ANNOTATION))
            returnThis = "A user friendly interface for Propbank like annotation.";
//        else if(cl.equals(SSF_CORPUS_ANALYZER))
//            returnThis = "A search, query and comparison tool for syntactically annotated corpus. Some facilities are still under development.";
//        else if(cl.equals(PARALLEL_CORPUS_MARKUP))
//            returnThis = "A user friendly interface for flat annotation (e.g. multi word expressions, named entities etc.) of parallel corpus.";
        else if(cl.equals(FRAMESET_EDITOR))
            returnThis = GlobalProperties.getIntlString("An_editor_for_frameset,_compatible_with_Cornerstone.");
        else if(cl.equals(NGRAM_LM))
            returnThis = GlobalProperties.getIntlString("A_tool_for_compiling_n-gram_models_of_different_kinds,_e.g._words,_characters,_bytes");
        else if(cl.equals(SENTENCE_ALIGNMENT_INTERFACE))
            returnThis = "An interface for paragraph and sentence alignment in parallel corpora.";
        else if(cl.equals(WORD_ALIGNMENT_INTERFACE))
            returnThis = "An interface for word and phrase alignment in parallel corpora.";
        else if(cl.equals(DICTIONARY_EDITOR))
            returnThis = "A simple dictionary editor.";
        else if(cl.equals(PARALLEL_MARKUP))
            returnThis = GlobalProperties.getIntlString("An_interface_for_parallel_markup_of_syntactically_annotated_text.");
//        else if(cl.equals(PARALLEL_MARKUP_ANALYZER))
//            returnThis = "A search, query and comparison tool for annotated parallel corpus. Some facilities are still under development.";
        else if(cl.equals(DISCOURSE_ANNOTATION))
            returnThis = GlobalProperties.getIntlString("An_interface_for_discourse_annotation_based_on_the_Penn_Treebank_annotation_scheme.");
        else if(cl.equals(FILE_SPLITTER))
            returnThis = GlobalProperties.getIntlString("A_useful_tool_for_splitting_files_(including_files_in_the_SSF_format)_according_to_various_criteria.");
        else if(cl.equals(SANCHAY_CHARMAP))
            returnThis = "Character map with the usual facilities, except that it is connected to the Sanchay Langauge Encoding Support facility";
//        else if(cl.equals(SANCHAY_EDITOR))
//            returnThis = "";

        return returnThis;
    }
    
    public String email(ClientType cl) {
        String returnThis=null;
        try{
            ApplicationDescription appl = (ApplicationDescription)identifyClient(cl);
            returnThis= appl.email();
            if(returnThis==null) {
                returnThis="sanchay@sanchay.co.in";
            }
        }
//        }catch(java.lang.ClassCastException e)
//        {
//            returnThis= "sanchay@iiit.ac.in";//"email not found. Please check out the latest version for updates.";
//        }
        catch (Exception e) {
            returnThis= "sanchay@sanchay.co.in";//"Unknown Exception. If this persists, please report it to sanchay@iiit.ac.in ";
        } finally{
            return returnThis;
        }
        
    }

    public static final ClientType SANCHAY_SSH_CLIENT = new ClientType("Sanchay_SSH_Client", "SC", "SanchaySSHClientJPanel", ServerType.RESOURCE_MANAGER, "sanchay.gui.clients");
    public static final ClientType SANCHAY_EDITOR = new ClientType(GlobalProperties.getIntlString("Sanchay_Editor"), "SE", "TextEditorJPanel", ServerType.RESOURCE_MANAGER, "sanchay.text.editor.gui");
    public static final ClientType SANCHAY_RTF_EDITOR = new ClientType(GlobalProperties.getIntlString("Sanchay_Rich_Text_Editor"), "RE", "RichTextEditorJPanel", ServerType.RESOURCE_MANAGER, "sanchay.text.editor.gui");
    public static final ClientType SANCHAY_SHELL = new ClientType("Sanchay Shell", "SS", "SanchayShellJPanel", ServerType.RESOURCE_MANAGER, "sanchay.gui.shell");
    public static final ClientType SANCHAY_HTML_BROWSER = new ClientType("Sanchay HTML Browser", "SB", "HTMLBrowserJPanel", ServerType.RESOURCE_MANAGER, "sanchay.html.gui");
    public static final ClientType TABLE_EDITOR = new ClientType(GlobalProperties.getIntlString("Table_Editor"), "TE", "SanchayTableJPanel", ServerType.RESOURCE_MANAGER, "sanchay.table.gui");
    public static final ClientType TREE_EDITOR = new ClientType("Tree Creator", "TC", "SanchayTreeDrawingJPanel", ServerType.RESOURCE_MANAGER, "sanchay.tree.gui");
    public static final ClientType INTEGRATED_RESOURCE_ACCESSOR = new ClientType(GlobalProperties.getIntlString("Integrated_Resource_Accessor"), "RA", "IntegratedResourceAccessorJPanel", ServerType.RESOURCE_MANAGER, "sanchay.gui.common");
    public static final ClientType WORD_LIST = new ClientType(GlobalProperties.getIntlString("Word_List_Builder"), "WB", "WordListJPanel", ServerType.RESOURCE_MANAGER, "sanchay.word.gui");
    public static final ClientType DICTIONARY_FST = new ClientType(GlobalProperties.getIntlString("Word_List_FST_Visualizer"), "WV", "DictionaryFSTJPanel", ServerType.RESOURCE_MANAGER, "sanchay.text.spell.gui");
    public static final ClientType DICTIONARY_EDITOR = new ClientType("Dictionary Editor", "DE", "DictionaryEditorJPanel", ServerType.RESOURCE_MANAGER, "sanchay.resources.shabdanjali");
    public static final ClientType LANGUAGE_ENCODING_IDENTIFIER = new ClientType(GlobalProperties.getIntlString("Language_Encoding_Identifier"), "LI", "LanguageEncodingIdentifierJPanel", ServerType.RESOURCE_MANAGER , "sanchay.langenc.gui");
    public static final ClientType NGRAM_LM = new ClientType(GlobalProperties.getIntlString("N-Gram_Language_Model_Compiler"), "LM", "NGramLMJPanel",ServerType.RESOURCE_MANAGER, "sanchay.corpus.manager.gui");
    public static final ClientType SYNTACTIC_ANNOTATION = new ClientType(GlobalProperties.getIntlString("Syntactic_Annotation"), "SA", "SyntacticAnnotationWorkJPanel", ServerType.RESOURCE_MANAGER, "sanchay.corpus.ssf.gui");
    public static final ClientType PROPBANK_ANNOTATION = new ClientType("Propbank Annotation", "PB", "PropbankAnnotationWorkJPanel", ServerType.RESOURCE_MANAGER, "sanchay.corpus.ssf.gui");
    public static final ClientType FRAMESET_EDITOR = new ClientType(GlobalProperties.getIntlString("Frameset_Editor"), "FE", "FramesetJPanel", ServerType.RESOURCE_MANAGER, "sanchay.propbank.gui");
//    public static final ClientType SSF_CORPUS_ANALYZER = new ClientType("SSF Corpus Analyzer", "", "SSFCorpusAnalyzerJPanel", ServerType.SANCHAY_SERVER, "sanchay.corpus.ssf.gui");
//    public static final ClientType PARALLEL_CORPUS_MARKUP = new ClientType("Parallel Syntactic Annotation", "PS", "ParallelSyntacticAnnotationWorkJPanel",ServerType.SANCHAY_SERVER, "sanchay.corpus.parallel.gui");
//    public static final ClientType SANCHAY = new ClientType("SanchayMain", "", ServerType.SANCHAY, "sanchay.clients");
//    public static final ClientType USER_MANAGER = new ClientType("User Manager", "", "UserManager", ServerType.USER_MANAGER, "sanchay.clients");

//    public static final ClientType BLOG_LIST_CAPTURE = new ClientType("Blog List Capture", "", "BlogListCaptureJPanel", ServerType.SANCHAY_SERVER, "sanchay.corpus.blog");
//    public static final ClientType FILE_EXPLORER = new ClientType("File Explorer", "", "FileExplorerJPanel", ServerType.SANCHAY_SERVER, "sanchay.gui.common");
//    public static final ClientType FILE_MATCHER = new ClientType("File Matcher", "", "FileMatcherJPanel", ServerType.SANCHAY_SERVER, "sanchay.util.gui");
//    public static final ClientType FILTER_TRANSLATOR = new ClientType("Filter Traslator", "", "FilterTranslatorJPanel", ServerType.SANCHAY_SERVER, "sanchay.filters.gui");
//    public static final ClientType MNREAD = new ClientType("MNRead", "", "MNReadJPanel", ServerType.SANCHAY_SERVER, "sanchay.util.gui");
//    public static final ClientType SENTENCE_ALIGNER = new ClientType("Sentence Aligner", "", "SenAlignWorkJPanel", ServerType.SANCHAY_SERVER, "sanchay.corpus.parallel.aligner.sentence.gui");
    public static final ClientType SENTENCE_ALIGNMENT_INTERFACE = new ClientType("Sentence Alignment Interface", "SI", "SentenceAlignmentInterfaceJPanel", ServerType.RESOURCE_MANAGER, "sanchay.corpus.parallel.gui");
    public static final ClientType WORD_ALIGNMENT_INTERFACE = new ClientType("Word Alignment Interface", "WI", "WordAlignmentInterfaceJPanel", ServerType.RESOURCE_MANAGER, "sanchay.corpus.parallel.gui");
    public static final ClientType PARALLEL_MARKUP = new ClientType(GlobalProperties.getIntlString("Parallel_Corpus_Markup"), "PA", "ParallelMarkupWorkJPanel", ServerType.RESOURCE_MANAGER, "sanchay.marker.gui");
//    public static final ClientType PARALLEL_MARKUP_ANALYZER = new ClientType("Parallel Markup Analyser", "", "ParallelMarkupAnalyzerJPanel", ServerType.SANCHAY_SERVER, "sanchay.marker.gui");
    public static final ClientType DISCOURSE_ANNOTATION = new ClientType(GlobalProperties.getIntlString("Discourse_Annotation"), "DA", "DiscourseAnnotationJPanel", ServerType.RESOURCE_MANAGER, "sanchay.corpus.discourse");
    public static final ClientType AUTOMATIC_ANNOTATION = new ClientType(GlobalProperties.getIntlString("Automatic_Annotation"), "AA", "AutomaticAnnotationJPanel", ServerType.RESOURCE_MANAGER, "sanchay.annotation.common");
//    public static final ClientType PROPERTIES_MANAGER = new ClientType("Properties Manager", "", "PropertiesManagerJPanel", ServerType.SANCHAY_SERVER, "sanchay.properties.gui");
    public static final ClientType FILE_SPLITTER = new ClientType(GlobalProperties.getIntlString("File_Splitter"), "FS", "FileSplitterJPanel", ServerType.RESOURCE_MANAGER, "sanchay.util.gui");
    public static final ClientType SANCHAY_CHARMAP = new ClientType("Sanchay Charmap", "CM", "SanchayCharmapJPanel", ServerType.RESOURCE_MANAGER, "sanchay.gui.common");
//    public static final ClientType SIMILARITY_MEASURE = new ClientType("Similarity Measure", "", "SimilarityMeasuresJPanel", ServerType.SANCHAY_SERVER, "sanchay.corpus.manager.gui");
//    public static final ClientType SPELL_CHECKER = new ClientType("Spell Checker", "", "SpellCheckerJPanel", ServerType.SANCHAY_SERVER, "sanchay.gui.common");
//    public static final ClientType TREE_EDITOR = new ClientType("Tree Editor", "", "SanchayTreeJPanel", ServerType.SANCHAY_SERVER, "sanchay.tree.gui");
//    public static final ClientType XML_EDITOR = new ClientType("XML Editor", "", "SanchayW3CXMLJPanel", ServerType.SANCHAY_SERVER, "sanchay.xml.gui");
    public static final ClientType DATABASe_EDITOR = new ClientType("Database Editor", "DBE", "SanchayDatabaseEditorJPanel", ServerType.RESOURCE_MANAGER, "sanchay.db.gui");
    public static final ClientType DOCUMENT_CONVERTER = new ClientType("Document Converter", "DCT", "DocumentConverterJPanel", ServerType.RESOURCE_MANAGER, "sanchay.util.gui");
    
    
//    public static final ClientType PARALLEL_CORPUS_MARKUP = new ClientType("ParallelCorpusMarkup", ServerType.PARALLEL_CORPUS_MARKUP, "sanchay.corpus.parallel.gui");
//    public static final ClientType UD_MANAGER = new ClientType("UDManagerClient", UDManagerServer, "sanchay.clients");
//    public static final ClientType CORPUS_MANAGER = new ClientType("CorpusManagerClient", CorpusManagerServer, "sanchay.clients");
//    public static final ClientType NGRAMLM = new ClientType("NGramLMClient", NGramLMServer, "sanchay.clients");
//    public static final ClientType IBM_MODEL = new ClientType("IBMModelClient", IBMModelServer, "sanchay.clients");
//    public static final ClientType DSF = new ClientType("DSFClient", DSFServer, "sanchay.clients");
//    public static final ClientType GTAC = new ClientType("GTACClient", GTACServer, "sanchay.clients");
//    public static final ClientType PARALLEL_CORPUS = new ClientType("ParallelCorpusClient", ParallelCorpusServer, "sanchay.clients");
//    public static final ClientType ML_ANNOTATION = new ClientType("MLAnnotationClient", MLAnnotationServer, "sanchay.clients");
//    public static final ClientType TEXT_ENCODING = new ClientType("TextEncodingClient", TextEncodingServer, "sanchay.clients");
//    public static final ClientType NLI = new ClientType("NLIClient", NLIServer, "sanchay.clients");
//    public static final ClientType NS = new ClientType("NSClient", NSServer, "sanchay.clients");
}
