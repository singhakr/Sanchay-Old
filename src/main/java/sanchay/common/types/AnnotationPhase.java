/*
 * AnnotationPhase.java
 *
 * Created on November 11, 2005, 12:18 AM
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
public final class AnnotationPhase extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected AnnotationPhase(String id, String pk) {
        super(id, pk);

        if (AnnotationPhase.last() != null) {
            this.prev = AnnotationPhase.last();
            AnnotationPhase.last().next = this;
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
        return new TypeEnumerator(AnnotationPhase.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = AnnotationPhase.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = AnnotationPhase.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final AnnotationPhase INITIAL = new AnnotationPhase("Initial", "sanchay.annotation.phase");
    public static final AnnotationPhase ANNOTATION = new AnnotationPhase("Annotation", "sanchay.annotation.phase");
    public static final AnnotationPhase ANNOTATED = new AnnotationPhase("Annotated", "sanchay.annotation.phase");
    public static final AnnotationPhase VALIDATION = new AnnotationPhase("Validation", "sanchay.annotation.phase");
    public static final AnnotationPhase VALIDATED = new AnnotationPhase("Validated", "sanchay.annotation.phase");
    public static final AnnotationPhase FINAL = new AnnotationPhase("Final", "sanchay.annotation.phase");
}
