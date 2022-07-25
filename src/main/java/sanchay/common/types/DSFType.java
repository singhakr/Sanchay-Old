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
import sanchay.resources.dsf.config.*;
/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class DSFType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected DSFConfigType dsfct;

    protected DSFType(String id, DSFConfigType dsfct, String pk) {
        super(id, pk);
        this.dsfct = dsfct;

        if (DSFType.last() != null) {
            this.prev = DSFType.last();
            DSFType.last().next = this;
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
        return new TypeEnumerator(DSFType.first());
    }

    public DSFConfigType getDSFConfigType() { return this.dsfct; }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = DSFType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = DSFType.elements();
        return SanchayType.findFromId(enm, i);
    }
    
    public static final DSFType DICTIONARY = new DSFType("Dictionary", DSFConfigType.DICTIONARY, "sanchay.resources.dsf");
    public static final DSFType ENTRY = new DSFType("Entry", DSFConfigType.ENTRY, "sanchay.resources.dsf");
    public static final DSFType SENSE = new DSFType("Sense", DSFConfigType.SENSE, "sanchay.resources.dsf");
    public static final DSFType FIELDSET = new DSFType("FieldSet", DSFConfigType.FIELDSET, "sanchay.resources.dsf");
    public static final DSFType FIELD = new DSFType("Field", DSFConfigType.FIELD, "sanchay.resources.dsf");
    public static final DSFType SUBFIELD = new DSFType("SubField", DSFConfigType.SUBFIELD, "sanchay.resources.dsf");
    public static final DSFType ALTVALUE = new DSFType("AltValue", DSFConfigType.ALTVALUE, "sanchay.resources.dsf");
    public static final DSFType CWORD = new DSFType("CWord", DSFConfigType.CWORD, "sanchay.resources.dsf");
}
