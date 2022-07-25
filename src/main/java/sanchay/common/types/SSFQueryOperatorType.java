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
public  final class SSFQueryOperatorType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected SSFQueryOperatorType(String id, String pk) {
        super(id, pk);

        if (SSFQueryOperatorType.last() != null) {
            this.prev = SSFQueryOperatorType.last();
            SSFQueryOperatorType.last().next = this;
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
        return new TypeEnumerator(SSFQueryOperatorType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = SSFQueryOperatorType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = SSFQueryOperatorType.elements();
        return SanchayType.findFromId(enm, i);
    }

//    public static boolean isAtom(String queryString)
//    {
//        if(queryString == null || queryString.equals(""))
//            return false;
//
//        Enumeration<SSFQueryOperatorType> enm = SSFQueryOperatorType.elements();
//
//        while(enm.hasMoreElements())
//        {
//            SSFQueryOperatorType otype = enm.nextElement();
//
//            if(otype.id.equals(MATCH.id)) || otype.id.equals(EQUAL.id))
//
//            if(queryString.contains(otype.getId()))
//                return false;
//        }
//
//        return true;
//    }
//
    public static final SSFQueryOperatorType ON_DS = new SSFQueryOperatorType("ON_DS", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType PARENTHESIS = new SSFQueryOperatorType("PARENTHESIS", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType AND = new SSFQueryOperatorType("AND", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType OR = new SSFQueryOperatorType("OR", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType NOT = new SSFQueryOperatorType("NOT", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType EQUAL = new SSFQueryOperatorType("EQUAL", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType NOT_EQUAL = new SSFQueryOperatorType("NOT_EQUAL", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType LIKE = new SSFQueryOperatorType("LIKE", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType NOT_LIKE = new SSFQueryOperatorType("NOT_LIKE", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType ASSIGN = new SSFQueryOperatorType("ASSIGN", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType VARIABLE = new SSFQueryOperatorType("VARIABLE", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType VALUE = new SSFQueryOperatorType("VALUE", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType LEAF = new SSFQueryOperatorType("LEAF", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType LEVEL = new SSFQueryOperatorType("LEVEL", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType COMMAND = new SSFQueryOperatorType("COMMAND", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType RETURN = new SSFQueryOperatorType("RETURN", "sanchay.corpus.ssf.query");
    public static final SSFQueryOperatorType DESTINATION = new SSFQueryOperatorType("DESTINATION", "sanchay.corpus.ssf.query");
}
