/*
 * UserType.java
 *
 * Created on October 30, 2005, 5:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.common.types;

import java.io.*;
import java.util.*;
import sanchay.GlobalProperties;

/**
 *
 *  @author Anil Kumar Singh Kumar Singh
 */
public final class UserType extends SanchayType implements Serializable {
 
    public final int ord;
    private static Vector types = new Vector();
   
    /** Creates a new instance of UserType */
    protected UserType(String id, String pk) {
        super(id, pk);

        if (UserType.last() != null) {
            this.prev = UserType.last();
            UserType.last().next = this;
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
        return new TypeEnumerator(UserType.first());
    }


    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = UserType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = UserType.elements();
        return SanchayType.findFromId(enm, i);
    }
   
    public static final UserType ANONYMOUS = new UserType(GlobalProperties.getIntlString("Anonymous"), "sanchay.users");
    public static final UserType GUEST = new UserType(GlobalProperties.getIntlString("Guest"), "sanchay.users");
    public static final UserType EDITOR = new UserType(GlobalProperties.getIntlString("Editor"), "sanchay.users");
    public static final UserType ADJUDICATOR = new UserType(GlobalProperties.getIntlString("Adjudicator"), "sanchay.users");
    public static final UserType ADMINISTRATOR = new UserType(GlobalProperties.getIntlString("Administrator"), "sanchay.users");
}
