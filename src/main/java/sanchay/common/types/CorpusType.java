/*
 * Created on Sep 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.common.types;

import java.io.*;
import java.util.*;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class CorpusType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected CorpusType(String id, String pk) {
        super(id, pk);

        if (CorpusType.last() != null) {
            this.prev = CorpusType.last();
            CorpusType.last().next = this;
        }

        types.add(this);
	ord = types.size();
    }

    public static int size()
    {
        return types.size();
    }
    
    public static SanchayType first()
    {
        return (SanchayType) types.get(0);
    }
    
    public static SanchayType last()
    {
        if(types.size() > 0)
            return (SanchayType) types.get(types.size() - 1);
        
        return null;
    }

    public static SanchayType getType(int i)
    {
        if(i >=0 && i < types.size())
            return (SanchayType) types.get(i);
        
        return null;
    }

    public static Enumeration elements()
    {
        return new TypeEnumerator(CorpusType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = CorpusType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = CorpusType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final CorpusType RAW = new CorpusType("RawCorpus", "sanchay.corpus");
    public static final CorpusType POS_TAGGED = new CorpusType("POSTaggedCorpus", "sanchay.corpus");
    public static final CorpusType VERTICAL_POS_TAGGED = new CorpusType("VerticalPOSTaggedCorpus", "sanchay.corpus");
    public static final CorpusType CHUNKED = new CorpusType("Chunked", "sanchay.corpus");
    public static final CorpusType BI_FORMAT = new CorpusType("BI_FORMAT", "sanchay.corpus");
    public static final CorpusType SSF_FORMAT = new CorpusType("SSFFormat", "sanchay.corpus");
    public static final CorpusType XML_FORMAT = new CorpusType("XMLFormat", "sanchay.corpus");
    public static final CorpusType TYPECRAFT_FORMAT = new CorpusType("TypeCraftFormat", "sanchay.corpus");
    public static final CorpusType GATE_FORMAT = new CorpusType("GATEFormat", "sanchay.corpus");
//    public static final CorpusType XML_SSF_TAGGED = new CorpusType("XMLSSFCorpus", "sanchay.corpus");
    public static final CorpusType NGRAM = new CorpusType("NGram", "sanchay.corpus");
    public static final CorpusType DICTIONARY = new CorpusType("Dictionary", "sanchay.corpus");
    public static final CorpusType HINDENCORP_FORMAT = new CorpusType("HindenCorpFormat", "sanchay.corpus");
}
