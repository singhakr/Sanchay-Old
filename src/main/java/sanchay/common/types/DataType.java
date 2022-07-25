/*
 * TableDataTypes.java
 *
 * Created on October 21, 2005, 11:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.common.types;

import java.io.*;
import java.util.*;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public final class DataType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected DataType(String id, String pk) {
        super(id, pk);

        if (DataType.last() != null) {
            this.prev = DataType.last();
            DataType.last().next = this;
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
        return new TypeEnumerator(DataType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = DataType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = DataType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final DataType BOOLEAN = new DataType("Boolean", "java.lang");
    public static final DataType BYTE = new DataType("Byte", "java.lang");
    public static final DataType CHARACTER = new DataType("Character", "java.lang");
    public static final DataType SHORT = new DataType("Short", "java.lang");
    public static final DataType INTEGER = new DataType("Integer", "java.lang");
    public static final DataType LONG = new DataType("Long", "java.lang");
    public static final DataType FLOAT = new DataType("Float", "java.lang");
    public static final DataType DOUBLE = new DataType("Double", "java.lang");

    public static final DataType BIG_INTEGER = new DataType("BigInteger", "java.lang");
    public static final DataType BIG_DECIMAL = new DataType("BigDecimal", "java.lang");

    public static final DataType STRING = new DataType("String", "java.lang");

    public static final DataType CLASS = new DataType("Class", "java.lang");
    public static final DataType OBJECT = new DataType("Object", "java.lang");

    public static final DataType FILE = new DataType("File", "java.lang");
}
