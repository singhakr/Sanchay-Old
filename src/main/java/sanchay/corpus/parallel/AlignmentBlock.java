/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.parallel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import sanchay.GlobalProperties;
import sanchay.corpus.ssf.SSFCorpus;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.table.SanchayTableModel;
import sanchay.tree.SanchayEdge;
import sanchay.tree.SanchayEdges;

/**
 *
 * @author anil
 */
public class AlignmentBlock<T> implements Serializable {

    protected List<AlignmentUnit<T>> srcAlignedUnits;
    protected List<AlignmentUnit<T>> tgtAlignedUnits;

    protected SanchayEdges edges;
    protected LinkedHashMap<String,SanchayEdge> edgeMap;

    protected String langEnc = GlobalProperties.getIntlString("hin::utf8");
    protected String charset = GlobalProperties.getIntlString("UTF-8");

    protected int mode = 0;

    public static final int DOCUMENT_ALIGNMENT_MODE = 0;
    public static final int PARAGRAPH_ALIGNMENT_MODE = 1;
    public static final int SENTENCE_ALIGNMENT_MODE = 2;
    public static final int PHRASE_ALIGNMENT_MODE = 3;

    public AlignmentBlock(int mode) {

        this.mode = mode;
    }

    public int getMode()
    {
        return mode;
    }

    public int countSrcAlignedUnits()
    {
        return srcAlignedUnits.size();
    }

    public AlignmentUnit<T> getSrcAlignedUnits(int num)
    {
        return srcAlignedUnits.get(num);
    }

    public int addSrcAlignedUnit(AlignmentUnit<T> p)
    {
        srcAlignedUnits.add(p);
        return srcAlignedUnits.size();
    }

    public AlignmentUnit<T> removeSrcAlignedUnit(int num)
    {
        return srcAlignedUnits.remove(num);
    }

    public void removeSrcAlignedUnit(AlignmentUnit<T> p)
    {
        int ind = srcAlignedUnits.indexOf(p);

        if(ind != -1) {
            srcAlignedUnits.remove(ind);
        }
    }

    public int countTgtAlignedUnits()
    {
        return tgtAlignedUnits.size();
    }

    public AlignmentUnit<T> getTgtAlignedUnits(int num)
    {
        return tgtAlignedUnits.get(num);
    }

    public int addTgtAlignedUnit(AlignmentUnit<T> p)
    {
        tgtAlignedUnits.add(p);
        return tgtAlignedUnits.size();
    }

    public AlignmentUnit<T> removeTgtAlignedUnit(int num)
    {
        return tgtAlignedUnits.remove(num);
    }

    public void removeTgtAlignedUnit(AlignmentUnit<T> p)
    {
        int ind = tgtAlignedUnits.indexOf(p);

        if(ind != -1) {
            tgtAlignedUnits.remove(ind);
        }
    }

    /**
     * @return the edges
     */
    public SanchayEdges getEdges()
    {
        return edges;
    }

    /**
     * @param edges the edges to set
     */
    public void setEdges(SanchayEdges edges)
    {
        this.edges = edges;
    }

    /**
     * @return the langEnc
     */
    public String getLangEnc()
    {
        return langEnc;
    }

    /**
     * @param langEnc the langEnc to set
     */
    public void setLangEnc(String langEnc)
    {
        this.langEnc = langEnc;
    }

    /**
     * @return the charset
     */
    public String getCharset()
    {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    public void prepareAlignment(int mode, Alignable<T> srcAlignable, Alignable<T> tgtAlignable)
    {
        edges = new SanchayEdges();
        edgeMap = new LinkedHashMap<String,SanchayEdge>();

        srcAlignedUnits = new ArrayList<AlignmentUnit<T>>();
        tgtAlignedUnits = new ArrayList<AlignmentUnit<T>>();

        if(mode == DOCUMENT_ALIGNMENT_MODE)
        {
            SSFCorpus srcCorpus = (SSFCorpus) srcAlignable;
            SSFCorpus tgtCorpus = (SSFCorpus) tgtAlignable;
        }
        else if(mode == PARAGRAPH_ALIGNMENT_MODE)
        {
            SSFStory srcStory = (SSFStory) srcAlignable;
            SSFStory tgtStory = (SSFStory) tgtAlignable;
        }
        else if(mode == SENTENCE_ALIGNMENT_MODE)
        {
            SSFStory srcStory = (SSFStory) srcAlignable;
            SSFStory tgtStory = (SSFStory) tgtAlignable;

            srcStory.reallocateSentenceIDs();
            tgtStory.reallocateSentenceIDs();

            int scount = srcStory.countSentences();
            int tcount = tgtStory.countSentences();

            for (int i = 0; i < scount; i++)
            {
                SSFSentence sentence = srcStory.getSentence(i);

                AlignmentUnit alignableUnit = sentence.getAlignmentUnit();
//                addEdges(alignableUnit, i, 0, false);

                alignableUnit.setAlignmentObject(sentence);

                alignableUnit.setIndex(i);
                alignableUnit.setParallelIndex(0);
                addSrcAlignedUnit(alignableUnit);
            }

            for (int i = 0; i < tcount; i++)
            {
                SSFSentence sentence = tgtStory.getSentence(i);

                AlignmentUnit alignableUnit = sentence.getAlignmentUnit();
//                addEdges(alignableUnit, i, 2, true);

                alignableUnit.setAlignmentObject(sentence);

                alignableUnit.setIndex(i);
                alignableUnit.setParallelIndex(2);
                addTgtAlignedUnit(alignableUnit);
            }
        }
        else if(mode == PHRASE_ALIGNMENT_MODE)
        {
            SSFSentence srcSentence = (SSFSentence) srcAlignable;
            SSFSentence tgtSentence = (SSFSentence) tgtAlignable;

            SSFPhrase srcRoot = srcSentence.getRoot();
            SSFPhrase tgtRoot = tgtSentence.getRoot();

            if(srcRoot == null) {
                srcRoot = new SSFPhrase();
            }

            if(tgtRoot == null) {
                tgtRoot = new SSFPhrase();
            }

            srcRoot.reallocateNames(null, null);
            tgtRoot.reallocateNames(null, null);

            List<SSFNode> srcChildren = srcRoot.getAllChildren();
            List<SSFNode> tgtChildren = tgtRoot.getAllChildren();

            int scount = srcChildren.size();
            int tcount = tgtChildren.size();

            for (int i = 0; i < scount; i++)
            {
                SSFNode node = (SSFNode) srcChildren.get(i);

                AlignmentUnit alignableUnit = node.getAlignmentUnit();
//                addEdges(alignableUnit, 0, i, false);

                alignableUnit.setAlignmentObject(node);

                alignableUnit.setIndex(i);
                alignableUnit.setParallelIndex(0);
                addSrcAlignedUnit(alignableUnit);
            }

            for (int i = 0; i < tcount; i++)
            {
                SSFNode node = (SSFNode) tgtChildren.get(i);

                AlignmentUnit alignableUnit = node.getAlignmentUnit();
//                addEdges(alignableUnit, 2, i, true);

                alignableUnit.setAlignmentObject(node);

                alignableUnit.setIndex(i);
                alignableUnit.setParallelIndex(2);
                addTgtAlignedUnit(alignableUnit);
            }
        }

        assignAlignmentReferences();
        synchronizeIndices(false);
    }

    protected void addEdges()
    {
        edges.removeAllEdges();
        edgeMap.clear();

        int scount = srcAlignedUnits.size();

        for (int i = 0; i < scount; i++)
        {
            AlignmentUnit srcAlignmentUnit = srcAlignedUnits.get(i);

            addEdges(srcAlignmentUnit);
        }
    }

    public void assignAlignmentReferences()
    {    
        int scount = srcAlignedUnits.size();

        for (int i = 0; i < scount; i++)
        {
            AlignmentUnit srcAlignmentUnit = srcAlignedUnits.get(i);

            Iterator<String> keys = srcAlignmentUnit.getAlignedUnitKeys();

            while(keys.hasNext())
            {
                String key = keys.next();
                AlignmentUnit tgtAlignmentUnit = srcAlignmentUnit.getAlignedUnit(key);

                int tgtIndex = tgtAlignmentUnit.getIndex();

                tgtAlignmentUnit = getTgtAlignedUnits(tgtIndex);

                srcAlignmentUnit.addAlignedUnit(tgtAlignmentUnit);
            }
        }

        int tcount = tgtAlignedUnits.size();

        for (int i = 0; i < tcount; i++)
        {
            AlignmentUnit tgtAlignmentUnit = tgtAlignedUnits.get(i);

            Iterator<String> keys = tgtAlignmentUnit.getAlignedUnitKeys();

            while(keys.hasNext())
            {
                String key = keys.next();
                AlignmentUnit srcAlignmentUnit = tgtAlignmentUnit.getAlignedUnit(key);

                int srcIndex = srcAlignmentUnit.getIndex();

                srcAlignmentUnit = getSrcAlignedUnits(srcIndex);

                tgtAlignmentUnit.addAlignedUnit(srcAlignmentUnit);
            }
        }
    }

    public void synchronizeIndices(boolean clear)
    {
        if(mode == PHRASE_ALIGNMENT_MODE)
        {
            addEdges();
            return;
        }

//        if(clear)
//            clearIndices();

        int scount = srcAlignedUnits.size();

        int prevSrcIndex = 0;
        int prevTgtIndex = 0;

        for (int i = 0; i < scount; i++)
        {
            AlignmentUnit srcAlignmentUnit = srcAlignedUnits.get(i);
            int srcIndex = srcAlignmentUnit.getIndex();

            if(srcAlignmentUnit.countAlignedUnits() > 1)
            {
                prevSrcIndex = srcIndex;
                continue;
            }

            Iterator<String> keys = srcAlignmentUnit.getAlignedUnitKeys();

            int j = 0;

            while(keys.hasNext() && j == 0)
            {
                String key = keys.next();
                AlignmentUnit tgtAlignmentUnit = srcAlignmentUnit.getAlignedUnit(key);

                int tgtIndex = tgtAlignmentUnit.getIndex();

                if(tgtAlignmentUnit.countAlignedUnits() > 1)
                {
                    prevTgtIndex = tgtIndex;
                    continue;
                }

                if(srcIndex > prevSrcIndex + 1 && tgtIndex > prevTgtIndex + 1)
                {
                    int by = Math.min(srcIndex - (prevSrcIndex + 1), (tgtIndex - (prevTgtIndex + 1)));

                    decrementIndices(i, by, false);
                    decrementIndices(tgtAlignedUnits.indexOf(tgtAlignmentUnit), by, true);
                }
//                if(srcIndex < tgtIndex && srcIndex > 0)
                else if(srcIndex < tgtIndex)
                {
                    incrementIndices(i, (tgtIndex - srcIndex), false);
                    srcIndex = srcAlignmentUnit.getIndex();
                }
//                else if(srcIndex > tgtIndex && tgtIndex > 0)
                else if(srcIndex > tgtIndex)
                {
                    incrementIndices(tgtAlignedUnits.indexOf(tgtAlignmentUnit), (srcIndex - tgtIndex), true);
                }

                prevTgtIndex = tgtIndex;
                j++;
            }
            
            prevSrcIndex = srcIndex;
        }

//        int tcount = tgtAlignedUnits.size();
//
//        for (int i = 0; i < tcount; i++)
//        {
//            AlignmentUnit tgtAlignmentUnit = tgtAlignedUnits.get(i);
//            int tgtIndex = tgtAlignmentUnit.getIndex();
//
//            Iterator<String> keys = tgtAlignmentUnit.getAlignedUnitKeys();
//
//            while(keys.hasNext())
//            {
//                String key = keys.next();
//                AlignmentUnit srcAlignmentUnit = tgtAlignmentUnit.getAlignedUnit(key);
//
//                int srcIndex = srcAlignmentUnit.getIndex();
//
//                if(srcIndex < tgtIndex)
//                {
//                    incrementIndices(srcIndex, (tgtIndex - srcIndex), false);
//                }
//                else if(tgtIndex > srcIndex)
//                {
//                    incrementIndices(tgtIndex, (srcIndex - tgtIndex), true);
//                    tgtIndex = tgtAlignmentUnit.getIndex();
//                }
//            }
//        }

        addEdges();
    }

//    public void clearIndices()
//    {
//        int scount = srcAlignedUnits.size();
//
//        for (int i = 0; i < scount; i++)
//        {
//            AlignmentUnit srcAlignmentUnit = srcAlignedUnits.get(i);
//        }
//    }

    public void incrementIndices(int from, int by, boolean tgt)
    {
        if(tgt == false)
        {
            int scount = srcAlignedUnits.size();

            for (int i = from; i < scount; i++)
            {
                AlignmentUnit aunit = srcAlignedUnits.get(i);
                aunit.setIndex(aunit.getIndex() + by);
            }
        }
        else
        {
            int tcount = tgtAlignedUnits.size();

            for (int i = from; i < tcount; i++)
            {
                AlignmentUnit aunit = tgtAlignedUnits.get(i);
                aunit.setIndex(aunit.getIndex() + by);
            }
        }
    }

    public void decrementIndices(int from, int by, boolean tgt)
    {
        if(tgt == false)
        {
            int scount = srcAlignedUnits.size();

            for (int i = from; i < scount; i++)
            {
                AlignmentUnit aunit = srcAlignedUnits.get(i);
                aunit.setIndex(aunit.getIndex() - by);
            }
        }
        else
        {
            int tcount = tgtAlignedUnits.size();

            for (int i = from; i < tcount; i++)
            {
                AlignmentUnit aunit = tgtAlignedUnits.get(i);
                aunit.setIndex(aunit.getIndex() - by);
            }
        }
    }

    public boolean isValidAlignment(AlignmentUnit srcAUnit, AlignmentUnit tgtAUnit)
    {
        boolean valid = true;

        Iterator<String> itr = edgeMap.keySet().iterator();

        while(itr.hasNext())
        {
            String ekey = itr.next();

            SanchayEdge e = edgeMap.get(ekey);

            if(mode == SENTENCE_ALIGNMENT_MODE || mode == PARAGRAPH_ALIGNMENT_MODE)
            {
                if((e.row1 > srcAUnit.getIndex() && e.row2 < tgtAUnit.getIndex())
                        || (e.row1 < srcAUnit.getIndex() && e.row2 > tgtAUnit.getIndex())) {
                    return false;
                }
            }
        }

        return valid;
    }

    public boolean areAlreadyAligned(AlignmentUnit<T> srcAUnit, AlignmentUnit<T> tgtAUnit)
    {
        Object srcAlignmentObject = srcAUnit.getAlignmentObject();

        if(srcAlignmentObject instanceof SSFNode)
        {
            Iterator<String> itr = srcAUnit.getAlignedUnitKeys();

            while(itr.hasNext())
            {
                String key = itr.next();

                AlignmentUnit alignedUnit = srcAUnit.getAlignedUnit(key);

                if(alignedUnit.equals(tgtAUnit)) {
                    return true;
                }
            }
        }
        else if(srcAlignmentObject instanceof SSFSentence)
        {
            Iterator<String> itr = srcAUnit.getAlignedUnitKeys();

            while(itr.hasNext())
            {
                String key = itr.next();

                AlignmentUnit alignedUnit = srcAUnit.getAlignedUnit(key);

                if(alignedUnit.equals(tgtAUnit)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void addEdge(AlignmentUnit<T> srcAUnit, AlignmentUnit<T> tgtAUnit)
    {
        SanchayEdge e = null;

        if(mode == SENTENCE_ALIGNMENT_MODE || mode == PARAGRAPH_ALIGNMENT_MODE) {
            e = new SanchayEdge(srcAUnit, srcAUnit.getIndex(), srcAUnit.getParallelIndex(), tgtAUnit, tgtAUnit.getIndex(), tgtAUnit.getParallelIndex());
        }
        else if(mode == PHRASE_ALIGNMENT_MODE) {
            e = new SanchayEdge(srcAUnit, srcAUnit.getParallelIndex(), srcAUnit.getIndex(), tgtAUnit, tgtAUnit.getParallelIndex(), tgtAUnit.getIndex());
        }

        edges.addEdge(e);

        String ekey = getEdgeMapKey(srcAUnit, tgtAUnit);

        edgeMap.put(ekey, e);
    }

    public void removeEdge(AlignmentUnit<T> srcAUnit, AlignmentUnit<T> tgtAUnit)
    {
        if(mode == SENTENCE_ALIGNMENT_MODE || mode == PARAGRAPH_ALIGNMENT_MODE) {
            edges.removeEdge(srcAUnit.getIndex(), srcAUnit.getParallelIndex(), tgtAUnit.getIndex(), tgtAUnit.getParallelIndex());
        }
        else if(mode == PHRASE_ALIGNMENT_MODE) {
            edges.removeEdge(srcAUnit.getParallelIndex(), srcAUnit.getIndex(), tgtAUnit.getParallelIndex(), tgtAUnit.getIndex());
        }

        String ekey = getEdgeMapKey(srcAUnit, tgtAUnit);

        edgeMap.remove(ekey);
    }

    protected String getEdgeMapKey(AlignmentUnit<T> srcAUnit, AlignmentUnit<T> tgtAUnit)
    {
        if(srcAUnit == null || tgtAUnit == null)
            return null;

        return (srcAUnit.hashCode() + ":" + tgtAUnit.hashCode());
    }

    public int getMaxSrcIndex()
    {
        if(srcAlignedUnits == null || srcAlignedUnits.size() == 0) {
            return -1;
        }

        return srcAlignedUnits.get(srcAlignedUnits.size() - 1).getIndex();
    }

    public int getMaxTgtIndex()
    {
        if(tgtAlignedUnits == null || tgtAlignedUnits.size() == 0) {
            return -1;
        }

        return tgtAlignedUnits.get(tgtAlignedUnits.size() - 1).getIndex();
    }

    public int getMaxIndex()
    {
        return Math.max(getMaxSrcIndex(), getMaxTgtIndex());
    }

    public void addEdges(AlignmentUnit<T> aunit)
    {
        Iterator<String> itr = aunit.getAlignedUnitKeys();

        while(itr.hasNext())
        {
            String key = itr.next();
            AlignmentUnit alignedUnit = aunit.getAlignedUnit(key);

            String ekey = getEdgeMapKey(aunit, alignedUnit);

            if(edgeMap.get(ekey) == null)
            {
                addEdge(aunit, alignedUnit);
            }
        }
    }

    public SanchayTableModel getAlignmentTable()
    {
        SanchayTableModel alignmentTable = new SanchayTableModel(1, 1);
        alignmentTable.addRow();
        
        int scount = countSrcAlignedUnits();
        int tcount = countTgtAlignedUnits();

        if(scount > 0 && tcount > 0)
        {
            if(mode == SENTENCE_ALIGNMENT_MODE)
            {
                alignmentTable = new SanchayTableModel(new String[]{"Source Language", "Quality Estimate", "Target Language"}, 0);
                alignmentTable.addRows(getMaxIndex() + 1);

                for (int i = 0; i < scount; i++)
                {
                    AlignmentUnit alignmentUnit = getSrcAlignedUnits(i);
                    alignmentTable.setValueAt(alignmentUnit, alignmentUnit.getIndex(), 0);
                }

                for (int i = 0; i < scount; i++)
                {
                    AlignmentUnit alignmentUnit = getSrcAlignedUnits(i);
                    alignmentTable.setValueAt(2.5, alignmentUnit.getIndex(), 1);
                }

                for (int i = 0; i < tcount; i++)
                {
                    AlignmentUnit alignmentUnit = getTgtAlignedUnits(i);
                    alignmentTable.setValueAt(alignmentUnit, alignmentUnit.getIndex(), 2);
                }
            }
            else if(mode == PHRASE_ALIGNMENT_MODE)
            {
                alignmentTable = new SanchayTableModel(0, 0);

                alignmentTable.addRow();
                alignmentTable.addRow();
                alignmentTable.addRow();

                alignmentTable.addColumns(getMaxIndex() + 1, "");

                for (int i = 0; i < scount; i++)
                {
                    AlignmentUnit alignmentUnit = getSrcAlignedUnits(i);
                    alignmentTable.setValueAt(alignmentUnit, 0, alignmentUnit.getIndex());
                }

                for (int i = 0; i < tcount; i++)
                {
                    AlignmentUnit alignmentUnit = getTgtAlignedUnits(i);
                    alignmentTable.setValueAt(alignmentUnit, 2, alignmentUnit.getIndex());
                }
            }
        }

        return alignmentTable;
    }
}
