package sanchay.common.types;

import java.io.*;
import java.util.*;

import sanchay.common.*;
import sanchay.properties.*;

public final class NLIType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();
    
    protected String alias; // the alias XML tag
    protected String modelIdAttribute; // the attribute name whose value is the modelId

    protected NLIType(String id, String al, String at, String pk) {
        super(id, pk);

        this.alias = al;
        modelIdAttribute = at;

        if (NLIType.last() != null) {
            this.prev = NLIType.last();
            NLIType.last().next = this;
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
        return new TypeEnumerator(NLIType.first());
    }

    public String getAlias() { return this.alias; }
    public String getModelIdAttribute() { return this.modelIdAttribute; }

    public static NLIType findFromAlias(String al)
    {
        Enumeration enm = NLIType.elements();
        NLIType nlic = null;

        while(enm.hasMoreElements())
        {
            nlic = (NLIType) enm.nextElement();

            if(al.equals(nlic.getAlias()))
                return nlic;
        }

        return null;
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = NLIType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = NLIType.elements();
        return SanchayType.findFromId(enm, i);
    }

    // Aliases can later be read from the model files
    public static final NLIType NLIGENERIC = new NLIType("NLIGeneric", null, null, "sanchay.nli.lm");

    public static final NLIType NLIOBJECT = new NLIType("NLIObject", null, null, "sanchay.nli.common");
    public static final NLIType NLIREFERENCE = new NLIType("NLIReference", null, null, "sanchay.nli.common");

    public static final NLIType FEATURES = new NLIType("Features", "Ftrs", "features", "sanchay.nli.lm");
    public static final NLIType SYNONYM = new NLIType("Synonym", "Syn", "synonym", "sanchay.nli.lm.lmodel");
    public static final NLIType LMODELOBJECT = new NLIType("LModelObject", null, null, "sanchay.nli.lm.lmodel");
    public static final NLIType WORD = new NLIType("Word", "Wrd", "word", "sanchay.nli.lm.lmodel");
    public static final NLIType MWEX = new NLIType("MWE", "Mwe", "mwe", "sanchay.nli.lm.lmodel");

    public static final NLIType DMODELOBJECT = new NLIType("DModelObject", null, null, "sanchay.nli.lm.dmodel");
    public static final NLIType UNIT = new NLIType("Unit", "Unt", "unit", "sanchay.nli.lm.dmodel");
    public static final NLIType VALUE = new NLIType("Value", "Val", "value", "sanchay.nli.lm.dmodel");
    public static final NLIType ARGUMENT = new NLIType("Argument", "Arg", "argument", "sanchay.nli.lm.dmodel");
    public static final NLIType INTERACTION = new NLIType("Interaction", "Intrn", "interaction", "sanchay.nli.lm.dmodel");

    public static final NLIType SMODELOBJECT = new NLIType("SModelObject", null, null, "sanchay.nli.lm.smodel");
    public static final NLIType CHUNK = new NLIType("Chunk", "Chnk", "chunk", "sanchay.nli.lm.smodel");
    public static final NLIType TEMPLATE = new NLIType("Template", "Tmplt", "template", "sanchay.nli.lm.smodel");
}
