/*
 * AccessType.java
 *
 * Created on November 10, 2005, 4:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
public final class AccessType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected AccessType(String id, String pk) {
        super(id, pk);

        if (AccessType.last() != null) {
            this.prev = AccessType.last();
            AccessType.last().next = this;
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
        return new TypeEnumerator(AccessType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = AccessType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = AccessType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final AccessType INVISIBLE = new AccessType("Invisible", "sanchay.access");
    public static final AccessType READABLE = new AccessType("Readable", "sanchay.access");
    public static final AccessType CLONEABLE = new AccessType("Cloneable", "sanchay.access");
    public static final AccessType APPENDABLE = new AccessType("Appendable", "sanchay.access");
    public static final AccessType MODIFIABLE = new AccessType("Modifiable", "sanchay.access");
    public static final AccessType FULL = new AccessType("Full", "sanchay.access");
}
