/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import javax.swing.tree.MutableTreeNode;
import sanchay.common.types.SSFQueryOperatorType;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author anil
 */
public class SSFQueryNode extends SanchayMutableTreeNode {

    protected SSFQueryOperatorType operator;

    protected SSFQuery ssfQuery;

    public SSFQueryNode(SSFQuery ssfQuery,Object userObject) {
        super(userObject);

        this.ssfQuery = ssfQuery;
    }

    public SSFQueryNode(SSFQuery ssfQuery, Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);

        this.ssfQuery = ssfQuery;
    }

    /**
     * @return the operator
     */
    public SSFQueryOperatorType getOperator()
    {
        return operator;
    }

    /**
     * @return the ssfQuery
     */
    public SSFQuery getSSFQuery()
    {
        return ssfQuery;
    }

    /**
     * @param ssfQuery the ssfQuery to set
     */
    public void setSsfQuery(SSFQuery ssfQuery)
    {
        this.ssfQuery = ssfQuery;
    }

    public int addChildren(Collection c)
    {
        Object ca[] = c.toArray();

        for (int i = 0; i < ca.length; i++)
        {
            add((MutableTreeNode) ca[i]);
        }

        return getChildCount();
    }

    public Vector<SSFQueryNode> getChildren(int from, int count)
    {
        Vector ret = new Vector(count);

        for (int i = 0; i < count; i++)
        {
            ret.add(getChildAt(from + i));
        }

        return ret;
    }

    public Vector<SSFQueryNode> getAllChildren()
    {
        Vector ret = getChildren(0, getChildCount());
        return ret;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(SSFQueryOperatorType operator)
    {
        this.operator = operator;
    }

    public boolean parseQuery() throws Exception
    {
        return false;
    }

    public String makeString()
    {
        if(userObject == null && operator == null)
            return "";

        if(operator == null)
            return (String) userObject;

        String qstring = operator.toString();

        if(qstring != null && !qstring.equals(""))
        {
            qstring += ": " + (String) userObject;
        }
        else
            qstring = operator.toString();

        int ccount = getChildCount();

        qstring += "\n";

        for (int i = 0; i < ccount; i++)
        {
            SSFQueryNode child = (SSFQueryNode) getChildAt(i);
            String indent = UtilityFunctions.getRepeatString(getLevel(), "\t");
            qstring += indent + child.makeString();
        }

        return qstring;
    }

    @Override
    public String toString()
    {
        if(operator == null)
            return "null";

        if(operator.equals(SSFQueryOperatorType.VARIABLE) || operator.equals(SSFQueryOperatorType.VALUE))
            return userObject.toString();

        return operator.toString();
    }
}
