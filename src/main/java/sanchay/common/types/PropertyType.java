/*
 * Created on Aug 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.common.types;

import java.io.*;
import java.util.*;


public final class PropertyType extends SanchayType implements Serializable {

    java.util.ResourceBundle bundle = sanchay.GlobalProperties.getResourceBundle(); // NOI18N

    public final int ord;
    private static Vector types = new Vector();

    protected PropertyType(String id, String pk) {
        super(id, pk);

        if (PropertyType.last() != null) {
            this.prev = PropertyType.last();
            PropertyType.last().next = this;
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
        return new TypeEnumerator(PropertyType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = PropertyType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = PropertyType.elements();
        return SanchayType.findFromId(enm, i);
    }

    // The basic property containers
    // MULTI property containers are Hashtables with name of the container as the key and container as the value
    public static final PropertyType PROPERTY_TOKENS = new PropertyType("PropertyTokens", "sanchay.properties");
    public static final PropertyType MULTI_PROPERTY_TOKENS = new PropertyType("MultiPropertyTokens", "sanchay.properties");
    public static final PropertyType KEY_VALUE_PROPERTIES = new PropertyType("KeyValueProperties", "sanchay.properties");
    public static final PropertyType MULTI_KEY_VALUE_PROPERTIES = new PropertyType("MultiKeyValueProperties", "sanchay.properties");
    public static final PropertyType PROPERTY_TABLE = new PropertyType("PropertyTable", "sanchay.properties");
    public static final PropertyType MULTI_PROPERTY_TABLE = new PropertyType("MultiPropertyTable", "sanchay.properties");
    
    public static final PropertyType PROPERTIES_MANAGER = new PropertyType("PropertiesManager", "sanchay.properties");
    public static final PropertyType MULTI_PROPERTIES_MANAGER = new PropertyType("MultiPropertiesManager", "sanchay.properties");
}
