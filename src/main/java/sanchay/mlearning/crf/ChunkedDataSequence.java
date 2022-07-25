/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.mlearning.crf;

import iitb.crf.DataSequence;
import iitb.crf.SegmentDataSequence;
import java.io.Serializable;
import java.util.Vector;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class ChunkedDataSequence extends DefaultDataSequence implements SegmentDataSequence, Serializable {

    protected Vector chunks;

    public ChunkedDataSequence()
    {
        super();

        xvector = new Vector(0, 10);
        yvector = new Vector(0, 10);

        chunks = new Vector(0, 10);
    }
    /*Constructor Added by Pankaj Soni*/
    public ChunkedDataSequence(DataSequence data)
    {
        length = ((DefaultDataSequence)data).length;
        xvector = ((DefaultDataSequence)data).xvector;
        yvector = ((DefaultDataSequence)data).yvector;
        chunks = new Vector(0, 10);
    }
    
    public void addChunkBoundaries(int startIndex, int endIndex, SSFNode node)
    {
        String fsString = "";

        if(node.getFeatureStructures() != null)
            fsString = node.getFeatureStructures().makeString();

        ChunkedCRFSequence chunk = new ChunkedCRFSequence(startIndex, endIndex, node.getLexData(), node.getName(), fsString);

        chunks.add(chunk);
    }

    public ChunkedCRFSequence removeChunkBoundaries(int index)
    {
        return (ChunkedCRFSequence) chunks.remove(index);
    }

    public ChunkedCRFSequence getChunk(int index)
    {
        return (ChunkedCRFSequence) chunks.get(index);
    }

    public int countChunks()
    {
        return chunks.size();
    }

    public int getSegmentEnd(int segmentStart) {
            if ((segmentStart > 0) && (y(segmentStart) == y(segmentStart-1)))
                    return -1;
            for (int i = segmentStart+1; i < length(); i++) {
                    if (y(i)!= y(segmentStart))
                            return i-1;
            }
            return length()-1;
    }

    /* (non-Javadoc)
     * @see iitb.CRF.SegmentDataSequence#setSegment(int, int, int)
     */
    public void setSegment(int segmentStart, int segmentEnd, int y) {
            for (int i = segmentStart; i <= segmentEnd; i++)
                    set_y(i,y);
    }

    public class ChunkedCRFSequence
    {
        int startSequence;
        int endSequence;

        protected String lexdata; // e.g., fair
        protected String name; // e.g., JJ

        protected String fsString; // Corresponds to stringFS

        public ChunkedCRFSequence(int startIndex, int endIndex, String lexdata, String name, String fsString)
        {
            this.startSequence = startIndex;
            this.endSequence = endIndex;

            this.lexdata = lexdata;
            this.name = name;
            this.fsString = fsString;
        }

        public int getSequenceStart()
        {
            return startSequence;
        }

        public void setSequenceStart(int s)
        {
            startSequence = s;
        }

        public int getSequenceEnd()
        {
            return endSequence;
        }

        public void setSequenceEnd(int e)
        {
            endSequence = e;
        }

        public String getLexData()
        {
            return lexdata;
        }

        public void setLexData(String ld)
        {
            lexdata = ld;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String n)
        {
            name = n;
        }

        public String getFSString()
        {
            return fsString;
        }

        public void setFSString(String fsString)
        {
            this.fsString = fsString;
        }
    }
}
