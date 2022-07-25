/*
 * Created on Sep 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sanchay.common.types;

import java.io.*;
import java.util.*;
import sanchay.common.types.SanchayType;

/**
 *  @author Anil Kumar Singh Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class BlogType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected BlogType(String id, String pk) {
        super(id, pk);

        if (BlogType.last() != null) {
            this.prev = BlogType.last();
            BlogType.last().next = this;
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
        return new TypeEnumerator(BlogType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = BlogType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = BlogType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final BlogType WORD_PRESS = new BlogType("WordPress", "sanchay.corpus.blog");
    public static final BlogType BLOGGER = new BlogType("Blogger", "sanchay.corpus.blog");
    public static final BlogType HUFFINGTON_POST = new BlogType("HuffingtonPost", "sanchay.corpus.blog");
}
