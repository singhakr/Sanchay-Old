/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.regex.Pattern;
import sanchay.common.types.SSFQueryOperatorType;
import sanchay.common.types.SSFQueryTokenType;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author anil
 */
public class SSFQueryActionNode extends SSFQueryNode {

    public SSFQueryActionNode(SSFQuery ssfQuery, Object userObject) {
        super(ssfQuery, userObject);
    }

    public SSFQueryActionNode(SSFQuery ssfQuery, Object userObject, boolean allowsChildren) {
        super(ssfQuery, userObject, allowsChildren);
    }

    public boolean parseQuery() throws Exception
    {
        boolean parsed = true;

        String qstring = (String) userObject;

        Pattern p = Pattern.compile("AND", Pattern.CASE_INSENSITIVE);

        String args[]  = p.split(qstring);

        if(args.length > 1)
        {
            operator = SSFQueryOperatorType.AND;

            for (int i = 0; i < args.length; i++)
            {
                String arg = args[i].trim();

                SSFQueryActionNode actionNode = new SSFQueryActionNode(ssfQuery, arg);

                parsed = actionNode.parseQuery();

                if(!parsed)
                    return false;

                add(actionNode);
            }

            return true;
        }

        args = qstring.split("=");

        if(args.length == 2)
        {
            operator = SSFQueryOperatorType.ASSIGN;

            String arg = args[0].trim();

            SSFQueryActionNode actionNode = new SSFQueryActionNode(ssfQuery, arg);

            actionNode.setOperator(SSFQueryOperatorType.VARIABLE);

//            parsed = actionNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(actionNode);

            arg = args[1].trim();

            actionNode = new SSFQueryActionNode(ssfQuery, arg);

            actionNode.setOperator(SSFQueryOperatorType.VALUE);

//            parsed = actionNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(actionNode);

            return true;
        }

        if(args.length == 1)
        {
            operator = SSFQueryOperatorType.RETURN;

            ssfQuery.returnsValues(true);

            String arg = args[0].trim();

            SSFQueryActionNode actionNode = new SSFQueryActionNode(ssfQuery, arg);

            actionNode.setOperator(SSFQueryOperatorType.VARIABLE);

//            parsed = actionNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(actionNode);
        }

        return true;
    }

    protected LinkedHashMap execute(SSFNode node) throws Exception
    {
        LinkedHashMap returnMap = new LinkedHashMap();
        
        if(getOperator().equals(SSFQueryOperatorType.AND))
        {
            int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                SSFQueryActionNode mnode = (SSFQueryActionNode) getChildAt(i);

                LinkedHashMap rMap = mnode.execute(node);

                if(rMap != null)
                    returnMap.putAll(rMap);
            }
        }

        if(getOperator().equals(SSFQueryOperatorType.RETURN))
        {
            int count = getChildCount();

            if(count != 1)
            {
                System.err.println("SSF query processing error: the return operator has " + count + " arguments, not one as required.");
            }

            SSFQueryActionNode varNode = (SSFQueryActionNode) getChildAt(0);

            LinkedHashMap rMap = varNode.execute(node);

            if(rMap != null)
                returnMap.putAll(rMap);
        }

        if(getOperator().equals(SSFQueryOperatorType.ASSIGN))
        {
            int count = getChildCount();

            if(count != 2)
            {
                System.err.println("SSF query processing error: the assign operator has " + count + " arguments, not two as required.");
            }

            SSFQueryActionNode varNode = (SSFQueryActionNode) getChildAt(0);

            LinkedHashMap rMap = varNode.execute(node);

            if(rMap != null)
                returnMap.putAll(rMap);
        }

        if(getOperator().equals(SSFQueryOperatorType.VARIABLE))
        {
            int count = getChildCount();

            if(count != 0)
            {
                System.err.println("SSF query processing error: variable operator has " + count + " arguments: it should have none.");
            }

            SSFQueryActionNode valNode = (SSFQueryActionNode) getNextSibling();

            if(valNode == null)
                return SSFQueryActionNode.returnValue(this, node);
            else
            {
                SSFQueryActionNode.setValue(this, valNode, node);
                LinkedHashMap rMap = new LinkedHashMap();
                rMap.put(node, ssfQuery.getQueryString());
                return rMap;
            }
        }

        return returnMap;
    }

    public static LinkedHashMap returnValue(SSFQueryActionNode varNode, SSFNode node) throws Exception
    {
        if(!varNode.operator.equals(SSFQueryOperatorType.VARIABLE))
        {
            System.err.println("SSF query processing error: wrong types of node for return: "
                    + varNode.getOperator().getId());

            return null;
        }

        String varString = (String) varNode.getUserObject();

        if(SSFQueryMatchNode.isLiteralValue(varString) || varString.contains("+") || Character.isLowerCase(varString.charAt(varString.length() - 1)))
            return returnValueString(varString, node, varNode);

        return returnValueNode(varString, node, varNode);
    }

    protected static LinkedHashMap returnValueString(String varString, SSFNode node, SSFQueryNode queryNode) throws Exception
    {
        LinkedHashMap returnMap = new LinkedHashMap();

        returnMap.put(new StringQueryValue(SSFQueryMatchNode.getValue(varString, node, queryNode), node), varString);

        return returnMap;
    }

    protected static LinkedHashMap returnValueNode(String varString, SSFNode node, SSFQueryNode queryNode) throws Exception
    {
        LinkedHashMap returnMap = new LinkedHashMap();

        Vector varSSFNodes = new Vector();
        NodeWildcardInfo nodeWildcardInfo = SSFQueryMatchNode.getSSFNodes(varString, node, varSSFNodes, queryNode);

//        varString = SSFQueryMatchNode.stripMatchAlias(varString);

        int vcount = varSSFNodes.size();

        if(vcount == 0)
            return null;

        if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST))
        {
            nodeWildcardInfo.rangeStart = 1;
            nodeWildcardInfo.rangeEnd = 1;
        }
        else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST))
        {
            nodeWildcardInfo.rangeStart = vcount;
            nodeWildcardInfo.rangeEnd = vcount;
        }

        for (int i = nodeWildcardInfo.rangeStart - 1; i < vcount && i < nodeWildcardInfo.rangeEnd; i++)
        {
            SSFNode varSSFNode = (SSFNode) varSSFNodes.get(i);

            returnMap.put(varSSFNode, varString);

//            LinkedHashMap rMap = returnValueHelper(varString, node, varSSFNode, queryNode);
//
//            if(rMap != null)
//                returnMap.putAll(rMap);
        }

        if(Character.isLowerCase(varString.charAt(varString.length() - 1)))
        {
            Vector<QueryValue> varValues = new Vector<QueryValue>();
            LinkedHashMap stringReturnMap = new LinkedHashMap();

            int count = varSSFNodes.size();

            for (int i = 0; i < count; i++)
            {
                QueryValue value = (QueryValue) varSSFNodes.get(i);

                if(value instanceof SSFNode)
                {
                    SSFNode n = (SSFNode) value;
                    StringQueryValue rvalue = new StringQueryValue(SSFQueryMatchNode.getValue(varString, n, queryNode), n);
                    varValues.add(rvalue);
                    stringReturnMap.put(rvalue, varString);
                }
            }

            queryNode.getSSFQuery().addReturnValues(varString, varValues);

            returnMap = stringReturnMap;
        }
        else
            queryNode.getSSFQuery().addReturnValues(varString, varSSFNodes);

        if(varSSFNodes.size() > 0)
        {
            if(Character.isLowerCase(varString.charAt(varString.length() - 1)))
               queryNode.getSSFQuery().addReturnLevel(varString, SSFQuery.STRING);
            else if(((SSFNode) varSSFNodes.get(0)).getParent() == null)
               queryNode.getSSFQuery().addReturnLevel(varString, SSFQuery.SENTENCE);
            else
               queryNode.getSSFQuery().addReturnLevel(varString, SSFQuery.NODE);
        }

        return returnMap;
    }

    public static void setValue(SSFQueryActionNode varNode, SSFQueryActionNode valNode, SSFNode node) throws Exception
    {
        if(!varNode.operator.equals(SSFQueryOperatorType.VARIABLE) && !valNode.operator.equals(SSFQueryOperatorType.VALUE))
        {
            System.err.println("SSF query processing error: wrong types of nodes for matching: "
                    + varNode.getOperator().getId() + " and " + valNode.getOperator().getId());

            return;
        }

        String varString = (String) varNode.getUserObject();
        String valString = (String) valNode.getUserObject();

        setValue(varString, valString, node, varNode);
    }

    protected static void setValue(String varString, String valString, SSFNode node,
            SSFQueryNode queryNode) throws Exception
    {
        Vector<SSFNode> varSSFNodes = new Vector<SSFNode>();
        NodeWildcardInfo nodeWildcardInfo = SSFQueryMatchNode.getSSFNodes(varString, node, varSSFNodes, queryNode);

//        varString = SSFQueryMatchNode.stripMatchAlias(varString);

        int vcount = varSSFNodes.size();

        if(vcount == 0)
            return;

        if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST))
        {
            nodeWildcardInfo.rangeStart = 1;
            nodeWildcardInfo.rangeEnd = 1;
        }
        else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST))
        {
            nodeWildcardInfo.rangeStart = vcount;
            nodeWildcardInfo.rangeEnd = vcount;
        }

        for (int i = nodeWildcardInfo.rangeStart - 1; i < vcount && i < nodeWildcardInfo.rangeEnd; i++)
        {
            SSFNode varSSFNode = varSSFNodes.get(i);

            setValueHelper(varString, valString, node, varSSFNode, queryNode);
        }
    }

//    protected static LinkedHashMap returnValueHelper(String varString, SSFNode node, SSFNode varSSFNode,
//            SSFQueryNode queryNode) throws Exception
//    {
//        LinkedHashMap returnMap = new LinkedHashMap();
//
//        return returnMap;
//    }

    protected static void setValueHelper(String varString, String valString,
            SSFNode node, SSFNode varSSFNode, SSFQueryNode queryNode) throws Exception
    {
        String varParts[] = varString.split("\\.");

        if(varParts.length == 1)
        {
            System.err.println("SSF query processing error: wrong variable syntax: "
                    + varString);

            return;
        }

        if(varParts.length >= 2)
        {
            varString = varParts[varParts.length - 1];
        }

        if(varString.equalsIgnoreCase("l"))
        {
            valString = SSFQueryMatchNode.getValue(valString, node, queryNode);
            varSSFNode.setLexData(valString);
        }
        else if(varString.equalsIgnoreCase("t"))
        {
            valString = SSFQueryMatchNode.getValue(valString, node, queryNode);
            varSSFNode.setName(valString);
        }
        else if(varString.startsWith("a") || varString.startsWith("A"))
        {
            varString = varString.substring(1);

            varString = SSFQueryMatchNode.stripBoundingStrings(varString, "[", "]");

            if(valString.contains(":"))
            {
                String attribValParts[] = valString.split(":");

                valString = attribValParts[0];
                String reference = attribValParts[1];

                if(SSFQueryMatchNode.isLiteralValue(reference))
                {
                    reference = SSFQueryMatchNode.stripQuotes(reference);
                }
                else
                {
                    Vector<SSFNode> referredNodes = new Vector<SSFNode>();
                    NodeWildcardInfo nodeWildcardInfo = SSFQueryMatchNode.getSSFNodes(reference,
                            node, referredNodes, queryNode);

//                    reference = SSFQueryMatchNode.stripMatchAlias(reference);

                    int vcount = referredNodes.size();

                    if(vcount == 0)
                        return;

                    if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST))
                    {
                        nodeWildcardInfo.rangeStart = 1;
                        nodeWildcardInfo.rangeEnd = 1;
                    }
                    else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST))
                    {
                        nodeWildcardInfo.rangeStart = vcount;
                        nodeWildcardInfo.rangeEnd = vcount;
                    }

                    for (int i = nodeWildcardInfo.rangeStart - 1; i < vcount && i < nodeWildcardInfo.rangeEnd; i++)
                    {
                        SSFNode referredNode = referredNodes.get(i);

                        reference = referredNode.getAttributeValue("name");

                        valString = valString + ":" + reference;

                        varString = SSFQueryMatchNode.getValue(varString, node, queryNode);

                        if(valString.equals(""))
                            varSSFNode.removeAttribute(varString);
                        else
                            varSSFNode.setAttributeValue(varString, valString);
                    }
                }
            }
            else
            {
                varString = SSFQueryMatchNode.getValue(varString, node, queryNode);
                valString = SSFQueryMatchNode.getValue(valString, node, queryNode);
//                if(SSFQueryMatchNode.isLiteralValue(valString))
//                {
//                    valString = SSFQueryMatchNode.stripQuotes(valString);
//                }
//                else
//                {
//                    valString = SSFQueryMatchNode.getValueHelper(valString, node);
//                }
            }

//            if(varString.contains(":"))
//            {
//                String attribValParts[] = valString.split(":");
//
//                varString = attribValParts[0];
//                String reference = attribValParts[1];
//
//                if(SSFQueryMatchNode.isLiteralValue(reference))
//                {
//                    reference = SSFQueryMatchNode.stripQuotes(reference);
//                }
//                else
//                {
//                    SSFNode referredNode = SSFQueryMatchNode.getSSFNode(reference, node);
//                    reference = referredNode.getAttributeValue("name");
//
//                    valString = varString + ":" + reference;
//                }
//            }
//            else
//            {
//                if(SSFQueryMatchNode.isLiteralValue(varString))
//                {
//                    varString = SSFQueryMatchNode.stripQuotes(varString);
//                }
//                else
//                {
//                    varString = SSFQueryMatchNode.getValueHelper(varString, node);
//                }
//            }

//            varString = SSFQueryMatchNode.getValue(varString, node);
//            if(SSFQueryMatchNode.isLiteralValue(varString))
//            {
//                varString = SSFQueryMatchNode.stripQuotes(varString);
//            }
//            else
//            {
//                varString = SSFQueryMatchNode.getValueHelper(varString, node);
//            }


            if(valString.equals(""))
                varSSFNode.removeAttribute(varString);
            else
                varSSFNode.setAttributeValue(varString, valString);
        }
    }
}
