/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.common.types;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author anil
 */
public final class SSFQueryTokenType extends SanchayType implements Serializable {
    public final int ord;
    private static Vector types = new Vector();

    protected SSFQueryTokenType(String id, String pk) {
        super(id, pk);

        if (SSFQueryTokenType.last() != null) {
            this.prev = SSFQueryTokenType.last();
            SSFQueryTokenType.last().next = this;
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
        return new TypeEnumerator(SSFQueryTokenType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = SSFQueryTokenType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = SSFQueryTokenType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final SSFQueryTokenType ON_DS = new SSFQueryTokenType("ON_DS", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType PARENTHESIS_START = new SSFQueryTokenType("PARENTHESIS_START", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType PARENTHESIS_END = new SSFQueryTokenType("PARENTHESIS_END", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType CARDINALITY_START = new SSFQueryTokenType("CARDINALITY_START", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType CARDINALITY_END = new SSFQueryTokenType("CARDINALITY_END", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType AND = new SSFQueryTokenType("AND", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType OR = new SSFQueryTokenType("OR", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType NOT = new SSFQueryTokenType("NOT", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType ATOM = new SSFQueryTokenType("ATOM", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType COMMAND = new SSFQueryTokenType("COMMAND", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType ARGUMENT_NAME = new SSFQueryTokenType("ARGUMENT_NAME", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType ARGUMENT_VALUE = new SSFQueryTokenType("ARGUMENT_VALUE", "sanchay.corpus.ssf.query");

    public static final SSFQueryTokenType WILDCARD_FIRST = new SSFQueryTokenType("WILDCARD_FIRST", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType WILDCARD_LAST = new SSFQueryTokenType("WILDCARD_LAST", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType WILDCARD_ALL = new SSFQueryTokenType("WILDCARD_ALL", "sanchay.corpus.ssf.query");
    public static final SSFQueryTokenType WILDCARD_RANGE = new SSFQueryTokenType("WILDCARD_RANGE", "sanchay.corpus.ssf.query");
}
