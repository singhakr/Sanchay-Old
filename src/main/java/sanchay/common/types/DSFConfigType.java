/*
 * Created on Sep 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.common.types;

import java.io.*;
import java.util.*;

import sanchay.common.*;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class DSFConfigType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected DSFConfigType(String id, String pk) {
        super(id, pk);

        if (DSFConfigType.last() != null) {
            this.prev = DSFConfigType.last();
            DSFConfigType.last().next = this;
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
        return new TypeEnumerator(DSFConfigType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = DSFConfigType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = DSFConfigType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final DSFConfigType DICTIONARY = new DSFConfigType("DictionaryConfig", "sanchay.resources.dsf.config");
    public static final DSFConfigType ENTRY = new DSFConfigType("EntryConfig", "sanchay.resources.dsf.config");
    public static final DSFConfigType SENSE = new DSFConfigType("SenseConfig", "sanchay.resources.dsf.config");
    public static final DSFConfigType FIELDSET = new DSFConfigType("FieldSetConfig", "sanchay.resources.dsf.config");
    public static final DSFConfigType FIELD = new DSFConfigType("FieldConfig", "sanchay.resources.dsf.config");
    public static final DSFConfigType SUBFIELD = new DSFConfigType("SubFieldConfig", "sanchay.resources.dsf.config");
    public static final DSFConfigType ALTVALUE = new DSFConfigType("AltValueConfig", "sanchay.resources.dsf.config");
    public static final DSFConfigType CWORD = new DSFConfigType("CWordConfig", "sanchay.resources.dsf.config");

    public static final DSFConfigType ADDINFO_CONFIG = new DSFConfigType("AddInfoConfig", "sanchay.resources.dsf.config");
    public static final DSFConfigType CONFIG_CONDITIONS = new DSFConfigType("ConfigConditions", "sanchay.resources.dsf.config");
    public static final DSFConfigType GLOBAL_SEPARATORS = new DSFConfigType("GlobalSeparators", "sanchay.resources.dsf.config");
}
