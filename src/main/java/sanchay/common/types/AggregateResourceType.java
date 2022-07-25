/*
 * TaskType.java
 *
 * Created on November 1, 2005, 5:29 PM
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
public class AggregateResourceType extends SanchayType implements Serializable
{
 
    public final int ord;
    private static Vector types = new Vector();
   
    /** Creates a new instance of TaskType */
    protected AggregateResourceType(String id, String pk) {
        super(id, pk);

        if (AggregateResourceType.last() != null) {
            this.prev = AggregateResourceType.last();
            AggregateResourceType.last().next = this;
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
        return new TypeEnumerator(AggregateResourceType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = AggregateResourceType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = AggregateResourceType.elements();
        return SanchayType.findFromId(enm, i);
    }
   
    public static final AggregateResourceType PARALLEL_MARKUP = new AggregateResourceType("ParallelMarkupTask", "sanchay.tasks");
    public static final AggregateResourceType SYNTACTIC_ANNOTATION = new AggregateResourceType("SyntacticAnnotationTask", "sanchay.tasks");
}
