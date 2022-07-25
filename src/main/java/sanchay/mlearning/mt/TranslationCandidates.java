/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.mt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sanchay.GlobalProperties;
import sanchay.mlearning.common.ModelScore;
import sanchay.propbank.FramesetRoleset;
import sanchay.text.TextNormalizer;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *
 * @author anil
 */
public class TranslationCandidates implements SanchayDOMElement {
    protected String langEnc = GlobalProperties.getIntlString("hin::utf8");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected String srcWrd;
    protected LinkedHashMap translationCandidates;

    protected int pruneSize = 10;
    
    protected double minScore = 0.0;
    protected double maxScore = 0.0;

    public TranslationCandidates()
    {
        translationCandidates = new LinkedHashMap(0, 100);
    }

    /**
     * @return the srcWrd
     */
    public String getSrcWrd() {
        return srcWrd;
    }

    /**
     * @param srcWrd the srcWrd to set
     */
    public void setSrcWrd(String srcWrd) {
        this.srcWrd = srcWrd;
    }

    /**
     * @return the pruneSize
     */
    public int getPruneSize()
    {
        return pruneSize;
    }

    /**
     * @param pruneSize the pruneSize to set
     */
    public void setPruneSize(int pruneSize)
    {
        this.pruneSize = pruneSize;
    }

    /**
     * @return the maxScore
     */
    public double getMaxScore() {
        return maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

    /**
     * 
     */
    public void calcMaxScore() {
        maxScore = 0.0;
        minScore = 0.0;

        Iterator cndItr = getTranslationCandidates();

        while (cndItr.hasNext()) {
            String candidate = (String) cndItr.next();
            TranslationCandidateScores candidateScores = getTranslationCandidateScores(candidate);

            double d = candidateScores.getTranslationScore();

            if(maxScore < d)
                maxScore = d;

            if(minScore > d)
                minScore = d;
        }
    }

    public void normalizeScores(boolean calcMax, boolean onRange) {

        if(calcMax)
            calcMaxScore();

        Iterator cndItr = getTranslationCandidates();

        while (cndItr.hasNext()) {
            String candidate = (String) cndItr.next();
            TranslationCandidateScores candidateScores = getTranslationCandidateScores(candidate);

            double d = candidateScores.getTranslationScore();

            if(onRange)
            {
                candidateScores.setTranslationScore( (d - minScore) / (maxScore - minScore) );
            }
            else
                candidateScores.setTranslationScore(d / maxScore);
        }
    }

    public void removeInvalidWords(TextNormalizer textNormalizer)
    {
        Iterator cndItr = getTranslationCandidates();

        Vector remove = new Vector(0, 100);

        while (cndItr.hasNext()) {
            String candidate = (String) cndItr.next();

            if(textNormalizer.isPossiblyValidWord(candidate) == false)
                remove.add(candidate);
        }

        int rcount = remove.size();

        for (int i = 0; i < rcount; i++) {
            String candidate = (String) remove.get(i);
            removeTranslationCandidateScores(candidate);
        }
    }

    public int countTranslationCandidates()
    {
        if(translationCandidates == null)
            return 0;

        return translationCandidates.size();
    }

    public Iterator getTranslationCandidates()
    {
        if(translationCandidates == null)
            return null;

        return translationCandidates.keySet().iterator();
    }

    public TranslationCandidateScores getTranslationCandidateScores(String candidate)
    {
        if(translationCandidates == null)
            return null;

        return (TranslationCandidateScores) translationCandidates.get(candidate);
    }

    public void addTranslationCandidateScores(String candidate, TranslationCandidateScores scores)
    {
        if(translationCandidates == null)
            translationCandidates = new LinkedHashMap(0, 100);

        translationCandidates.put(candidate, scores);
    }

    public void removeTranslationCandidateScores(String candidate)
    {
        if(translationCandidates == null)
            return;

        translationCandidates.remove(candidate);
    }

    public void readTranslationCandidates(String path, String charset) throws FileNotFoundException, IOException
    {
        BufferedReader inReader = null;

        if(charset != null && charset.equals("") == false)
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
        else
            inReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

        String line = "";

        while((line = inReader.readLine()) != null)
        {
            if(line.equals("") == false)
            {
                TranslationCandidateScores translationScores = new TranslationCandidateScores();

                addTranslationCandidateScores(line, translationScores);
            }
        }
    }

    public void printTranslationCandidates(PrintStream ps, boolean onlyTheFirst, boolean printScores)
    {
        Iterator cndItr = getTranslationCandidates();

        while(cndItr.hasNext())
        {
            String candidate = (String) cndItr.next();

            TranslationCandidateScores scores = getTranslationCandidateScores(candidate);

            if(printScores)
                ps.println(candidate + " ||| " + scores);
            else
                ps.println(candidate);

            if(onlyTheFirst)
                break;
        }
    }

    public void printTranslationCandidates(PrintStream ps, boolean onlyTheFirst)
    {
        printTranslationCandidates(ps, onlyTheFirst, true);
    }
    
    public Vector pruneAndSortTranslationCandidates(boolean prune)
    {
        Vector<ModelScore<String, Double>> sortedScores = new Vector<ModelScore<String, Double>>(100, 100);

        Iterator cndItr = getTranslationCandidates();

        while (cndItr.hasNext()) {
            String candidate = (String) cndItr.next();
            TranslationCandidateScores candidateScores = getTranslationCandidateScores(candidate);

            double s = candidateScores.getTranslationScore();

            ModelScore<String, Double> ms = new ModelScore<String, Double>(candidate, new Double(s));
            sortedScores.add(ms);
        }

        Collections.sort(sortedScores, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Comparable) o2).compareTo((Comparable) o1);
            }
        });

        int count = sortedScores.size();

        if(prune)
            count = Math.min(sortedScores.size(),getPruneSize());

        translationCandidates.clear();

        for (int i = 0; i < count; i++) {
            ModelScore<String, Double> ms = sortedScores.get(i);

            TranslationCandidateScores s = new TranslationCandidateScores();
            s.setTranslationScore(ms.modelScore);

            addTranslationCandidateScores((String) ms.modelKey, s);
        }

        return sortedScores;
    }

    public void addTranslationCandidates(TranslationCandidates moreTranslationCandidates, boolean replace)
    {
        if(moreTranslationCandidates == null)
            return;

        Iterator cndItr = moreTranslationCandidates.getTranslationCandidates();

        while (cndItr.hasNext()) {
            String candidate = (String) cndItr.next();
            TranslationCandidateScores candidateScores = moreTranslationCandidates.getTranslationCandidateScores(candidate);

            TranslationCandidateScores existingCandidateScores = getTranslationCandidateScores(candidate);

            if(existingCandidateScores != null && replace == false)
            {
                existingCandidateScores.setTranslationScore((candidateScores.getTranslationScore() + existingCandidateScores.getTranslationScore()) / 2.0);
                addTranslationCandidateScores(candidate, existingCandidateScores);
            }
            else
            {
                addTranslationCandidateScores(candidate, candidateScores);
            }
        }   
    }

    public Vector getCandidates()
    {
        Vector candidates = new Vector(countTranslationCandidates());

        Iterator cndItr = getTranslationCandidates();

        while(cndItr.hasNext())
        {
            String candidate = (String) cndItr.next();
            candidates.add(candidate);
        }

        return candidates;
    }

    public Vector getCandidateScores()
    {
        Vector candidateScores = new Vector(countTranslationCandidates());

        Iterator cndItr = getTranslationCandidates();

        while(cndItr.hasNext())
        {
            String candidate = (String) cndItr.next();
            TranslationCandidateScores candidateScore = getTranslationCandidateScores(candidate);
            candidateScores.add(candidateScore);
        }

        return candidateScores;
    }

    public String getFirstCandidate()
    {
        String candidate = "";

        Iterator cndItr = getTranslationCandidates();

        if(cndItr.hasNext())
        {
            candidate = (String) cndItr.next();
            return candidate;
        }

        return candidate;
    }

    public void mergeCandidates(TranslationCandidates moreTranslationCandidates, int thisMany)
    {
        if(moreTranslationCandidates == null)
            return;

        Iterator cndItr = moreTranslationCandidates.getTranslationCandidates();

        while (cndItr.hasNext() && countTranslationCandidates() <= thisMany) {
            String candidate = (String) cndItr.next();
            TranslationCandidateScores candidateScores = moreTranslationCandidates.getTranslationCandidateScores(candidate);

            TranslationCandidateScores existingCandidateScores = getTranslationCandidateScores(candidate);

            if(existingCandidateScores == null)
                addTranslationCandidateScores(candidate, candidateScores);
        }
    }

    public TranslationCandidates getCandidatesInReverseOrder()
    {
        TranslationCandidates reverseOrderCandidates = new TranslationCandidates();

        Vector candidates = getCandidates();
        Vector candidateScores = getCandidateScores();

        Collections.reverse(candidates);
        Collections.reverse(candidateScores);

        int count = candidates.size();

        for (int i = 0; i < count; i++) {
            reverseOrderCandidates.addTranslationCandidateScores((String) candidates.get(i), (TranslationCandidateScores) candidateScores.get(i));
        }

        return reverseOrderCandidates;
    }

    @Override
    public org.dom4j.dom.DOMElement getDOMElement()
    {
        DOMElement domElement = new DOMElement("Name");

        DOMElement sdomElement = new DOMElement("SourceName");

        sdomElement.setText(srcWrd);

        domElement.add(sdomElement);

        Iterator cndItr = getTranslationCandidates();

        int id = 0;

        while(cndItr.hasNext())
        {
            String candidate = (String) cndItr.next();

            DOMElement tdomElement = new DOMElement("TargetName");

            DOMAttribute attribDOM = new DOMAttribute(new org.dom4j.QName("ID"), "" + id);

            tdomElement.add(attribDOM);

            tdomElement.setText(candidate);

            sdomElement.add(tdomElement);

            id++;
        }

        return domElement;
    }

    @Override
    public void readXML(Element domElement)
    {
        Node node = domElement.getFirstChild();

        while(node != null)
        {
            if(node instanceof Element)
            {
                Element element = (Element) node;

                if(element.getTagName().equals("SourceName"))
                {
                    srcWrd = element.getTextContent();
                }
                else if(element.getTagName().equals("TargetName"))
                {
                    String candidate = element.getTextContent();

                    addTranslationCandidateScores(candidate, new TranslationCandidateScores());
                }
            }

            node = node.getNextSibling();
        }
    }

    @Override
    public String getXML()
    {
        String xmlString = "";

        org.dom4j.dom.DOMElement element = getDOMElement();
        xmlString = element.asXML();

        return "\n" + xmlString + "\n";
    }

    @Override
    public void printXML(PrintStream ps)
    {
        ps.println(getXML());
    }

    public static void main(String args[])
    {
    }
}
