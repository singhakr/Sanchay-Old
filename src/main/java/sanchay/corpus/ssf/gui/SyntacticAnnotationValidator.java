/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.gui;

import java.util.Vector;
import sanchay.corpus.ssf.SSFProperties;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.query.SSFQuery;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;
import sanchay.table.SanchayTableModel;

/**
 *
 * @author anil
 */
public class SyntacticAnnotationValidator {

    protected PropertyTokens posTagsPT;
    protected KeyValueProperties morphTagsKVP;
    protected PropertyTokens phraseNamesPT;

    public SyntacticAnnotationValidator(PropertyTokens posTagsPT, KeyValueProperties morphTagsKVP,
            PropertyTokens phraseNamesPT)
    {
        this.posTagsPT = posTagsPT;
        this.morphTagsKVP = morphTagsKVP;
        this.phraseNamesPT  =phraseNamesPT;
    }

    public SanchayTableModel validate(SSFStory story, String option)
    {
        if(option.equals("POS Tags"))
            return validatePOSTags(story);
        else if(option.equals("Chunk Tags"))
            return validateChunkTags(story);

        return null;
    }

    public SanchayTableModel runQueries(SSFStory story, String queries[])
    {
        SanchayTableModel allMatches = null;

        for (int i = 0; i < queries.length; i++) {
            SSFQuery ssfQuery = new SSFQuery(queries[i]);

            try
            {
                ssfQuery.parseQuery();
            } catch (Exception ex)
            {
    //            JOptionPane.showMessageDialog(this, "Error in parsing the query: " + ssfQuery, sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
                return null;
            }

            SanchayTableModel matches = ssfQuery.query(story, ssfQuery);

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

    public SanchayTableModel validatePOSTags(SSFStory story)
    {
        String validTags = "^NULL$|";

        int count = posTagsPT.countTokens();

        for (int i = 0; i < count; i++)
        {
            if(i < count - 1)
                validTags += "^" + posTagsPT.getToken(i) + "$|";
            else
                validTags += "^" + posTagsPT.getToken(i) + "$";
        }

        for (int i = 0; i < count; i++)
        {
            if(i < count - 1)
                validTags += "^NULL__" + posTagsPT.getToken(i) + "$|";
            else
                validTags += "^NULL__" + posTagsPT.getToken(i) + "$";
        }

        validTags = "C.t!~\'" + validTags + "\' and C.f=\'t\'";

        SSFQuery ssfQuery = new SSFQuery(validTags);

        try
        {
            ssfQuery.parseQuery();
        } catch (Exception ex)
        {
//            JOptionPane.showMessageDialog(this, "Error in parsing the query: " + ssfQuery, sanchay.GlobalProperties.getIntlString("Search_Results"), JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        SanchayTableModel matches = ssfQuery.query(story, ssfQuery);

        ssfQuery.send();

        return matches;
    }

    public SanchayTableModel validateChunkTags(SSFStory story)
    {
        SSFProperties ssfp = SSFNode.getSSFProperties();

        String rootName = ssfp.getProperties().getPropertyValue("rootName");
        
        String validTags = "^NULL$|^" + rootName + "$|";

        int count = phraseNamesPT.countTokens();

        for (int i = 0; i < count; i++)
        {
            if(i < count - 1)
                validTags += "^" + phraseNamesPT.getToken(i) + "$|";
            else
                validTags += "^" + phraseNamesPT.getToken(i) + "$";
        }

        for (int i = 0; i < count; i++)
        {
            if(i < count - 1)
                validTags += "^NULL__" + phraseNamesPT.getToken(i) + "$|";
            else
                validTags += "^NULL__" + phraseNamesPT.getToken(i) + "$";
        }

        validTags = "C.t!~\'" + validTags + "\' and C.f=\'f\'";

        String karaVGNF = "C.t!=\'VGNF\' and C.f=\'f\' and (C.a[\'tam\']=\'कर\' or C.a[\'tam\']=\'ता_हो\\+या\')";
        String nAVGNN = "C.t!=\'VGNN\' and C.f=\'f\' and C.a[\'tam\']=\'ना\'";
        String pspVG = "A.t!=\'VGNN\' and A.t~\'VG\' and C.f=\'t\' and C.t=\'PSP\'";
        String pspVGF = "A.t=\'VGF\' and C.f=\'t\' and C.t=\'PSP\'";
        String cc = "(A.t!=\'NULL__CCP\' and A.t!=\'CCP\') and C.f=\'t\' and C.t=\'CC\'";

        String NP = "C.t=\'NP\' and D[?].t!~\'^NN$|^NNP$|^NST$|^PRP$|^DEM$|^WQ$|^QF$|^QC$\'";
        String VG = "C.t~\'^VGF$|^VGNF$|^VGNN$|^VGINF$\' and D[?].t!=~\'VM\'";
        String JJP = "C.t=\'JJP\' and D[?].t!~\'^JJ$|^QF$\'";
        String CCP = "C.t=\'CCP\' and D[?].t!~\'^CC$|^SYM$\'";
        String RBP = "C.t=\'RBP\' and D[?].t!~\'^RB$|^WQ$\'";
        String NEGP = "C.t=\'NEGP\' and D[?].t!=\'NEG\'";

        String[] queries = new String[] {validTags, karaVGNF, nAVGNN, pspVG, pspVGF, cc};

        return runQueries(story, queries);
    }
}
