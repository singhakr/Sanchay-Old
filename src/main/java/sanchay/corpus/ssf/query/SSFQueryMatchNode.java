/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.JFrame;
import sanchay.GlobalProperties;
import sanchay.common.types.SSFQueryOperatorType;
import sanchay.common.types.SSFQueryTokenType;
import sanchay.corpus.ssf.tree.SSFLexItem;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.tree.SanchayMutableTreeNode;
import sanchay.tree.gui.SanchayTreeJPanel;
import sanchay.tree.gui.SanchayTreeViewerJPanel;

/**
 *
 * @author anil
 */
public class SSFQueryMatchNode extends SSFQueryNode {

    protected SSFQueryLexicalAnalyser ssfQueryLexicalAnalyser;

    public SSFQueryMatchNode(SSFQuery ssfQuery, Object userObject) {
        super(ssfQuery, userObject);
    }

    public SSFQueryMatchNode(SSFQuery ssfQuery, Object userObject, boolean allowsChildren) {
        super(ssfQuery, userObject, allowsChildren);
    }
    
    @Override
    public boolean parseQuery() throws Exception
    {
        boolean parsed = false;

        String qstring = (String) userObject;

        qstring = qstring.trim();

        ssfQueryLexicalAnalyser = new SSFQueryLexicalAnalyser();
        ssfQueryLexicalAnalyser.readTokens(qstring);

        if(!qstring.contains(")") || isOnDS(qstring) >= 0) {
            parsed = parseQueryHelper(qstring);
        }
        else if(qstring.startsWith("(") && qstring.endsWith(")") && qstring.indexOf("(", 1) == -1  && qstring.indexOf(")", 0) == qstring.length() - 1)
        {
            parsed = parseQueryHelper(qstring);
        }
        else if(qstring.startsWith("!(") && qstring.endsWith(")") && qstring.indexOf("!(", 2) == -1  && qstring.indexOf(")", 0) == qstring.length() - 1)
        {
            parsed = parseQueryHelper(qstring);
        }
        else
        {
            operator = SSFQueryOperatorType.PARENTHESIS;
            setUserObject("Root");
            
            LinkedList<SSFQueryMatchNode> nodeStack = new LinkedList<SSFQueryMatchNode>();

            SSFQueryMatchNode parentNode = this;
            SSFQueryMatchNode node = null;

            int tcount = ssfQueryLexicalAnalyser.countTokens();

            int level = 0;

            for (int i = 0; i < tcount; i++)
            {
                SSFQueryToken token = ssfQueryLexicalAnalyser.getToken(i);

                System.out.println(token.getTokenString());

                if(token.getTokenType().equals(SSFQueryTokenType.ON_DS))
                {
                    operator = SSFQueryOperatorType.ON_DS;

                    SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, token.getTokenString());

                    parsed = matchNode.parseQuery();

                    if(!parsed) {
                        return false;
                    }

                    add(matchNode);
                }
                else if(token.getTokenType().equals(SSFQueryTokenType.PARENTHESIS_START)
                        || token.getTokenType().equals(SSFQueryTokenType.NOT)
                        || token.getTokenType().equals(SSFQueryTokenType.AND)
                        || token.getTokenType().equals(SSFQueryTokenType.OR))
                {
                    level++;

                    boolean branch = true;

                    if (level == 1)
                    {
                        parentNode = this;
                    }
                    else
                    {
                        if(nodeStack.isEmpty()) {
                            return false;
                        }
                        else
                        {
                            parentNode = nodeStack.getLast();
                        }
                    }

                    if(token.getTokenType().equals(SSFQueryTokenType.AND)
                            && parentNode.getOperator().equals(SSFQueryOperatorType.AND)) {
                        branch = false;
                    }

                    if(token.getTokenType().equals(SSFQueryTokenType.OR)
                            && parentNode.getOperator().equals(SSFQueryOperatorType.OR)) {
                        branch = false;
                    }

                    if(branch)
                    {
                        node = new SSFQueryMatchNode(ssfQuery, "");
                        nodeStack.add(node);

                        if(token.getTokenType().equals(SSFQueryTokenType.PARENTHESIS_START)) {
                            node.setOperator(SSFQueryOperatorType.PARENTHESIS);
                        }
                        else if(token.getTokenType().equals(SSFQueryTokenType.NOT)) {
                            node.setOperator(SSFQueryOperatorType.NOT);
                        }
                        else if(token.getTokenType().equals(SSFQueryTokenType.AND)) {
                            node.setOperator(SSFQueryOperatorType.AND);
                        }
                        else if(token.getTokenType().equals(SSFQueryTokenType.OR)) {
                            node.setOperator(SSFQueryOperatorType.OR);
                        }

                        if (level == 1)
                        {
                            add(node);
                        }
                        else
                        {
                            parentNode.add(node);
                        }

                        if(token.getTokenType().equals(SSFQueryTokenType.AND)
                            || token.getTokenType().equals(SSFQueryTokenType.OR))
                        {
                            int pccount = parentNode.getChildCount();

                            if(pccount > 1)
                            {
                                SSFQueryMatchNode tempNode = (SSFQueryMatchNode) node.getPreviousSibling();

                                if(tempNode != null)
                                {
                                    parentNode.remove(tempNode);
                                    node.add(tempNode);
                                }
                            }
                        }
                    }
                }
                else if(token.getTokenType().equals(SSFQueryTokenType.PARENTHESIS_END))
                {
                    level--;

                    SSFQueryMatchNode tempNode = null;

                    if (nodeStack.isEmpty()) {
                        return false;
                    }
                    else
                    {
                        tempNode = nodeStack.removeLast();

                        if(((SSFQueryMatchNode) tempNode.getParent()).getOperator().equals(SSFQueryOperatorType.NOT))
                        {
                            level--;
                            tempNode = nodeStack.removeLast();
                        }
                    }

                    if(tempNode.getOperator().equals(SSFQueryOperatorType.AND)
                            || tempNode.getOperator().equals(SSFQueryOperatorType.OR))
                    {
                        level--;

                        if (nodeStack.isEmpty()) {
                            return false;
                        }
                        else
                        {
                            nodeStack.removeLast();
                        }
                    }
                }
                else if(token.getTokenType().equals(SSFQueryTokenType.ATOM))
                {
                    if (level == 0)
                    {
                        parentNode = this;
                    }
                    else
                    {
                        if(nodeStack.isEmpty()) {
                            return false;
                        }
                        else
                        {
                            parentNode = nodeStack.peekLast();
                        }
                    }

                    node = new SSFQueryMatchNode(ssfQuery, token.getTokenString());
                    parsed = node.parseQuery();

                    if(!parsed) {
                        return false;
                    }

                    if(level == 0)
                    {
                        add(node);
                    } else
                    {
                        parentNode.add(node);
                    }
                }
            }

            if (nodeStack.size() > 0
                    && !(nodeStack.peekLast().operator.equals(SSFQueryOperatorType.AND)
                        || nodeStack.peekLast().operator.equals(SSFQueryOperatorType.OR))) {
                return false;
            }
        }

        return parsed;
    }

    public boolean parseQuery1() throws Exception
    {
        boolean parsed = false;

        String qstring = (String) userObject;

        qstring = qstring.trim();

        if(!qstring.contains("(")) {
            parsed = parseQueryHelper(qstring);
        }
        else if(qstring.startsWith("(") && qstring.endsWith(")") && qstring.indexOf("(", 1) == -1  && qstring.indexOf(")", 0) == qstring.length() - 1)
        {
            parsed = parseQueryHelper(qstring);
        }
        else
        {
            LinkedList<SSFQueryMatchNode> nodeStack = new LinkedList<SSFQueryMatchNode>();

            boolean parenthesisEnd = false;
            boolean doublePop = false;
            LinkedList<Boolean> parenthesisEndStack = new LinkedList<Boolean>();

            String nodeString = "";
            SSFQueryMatchNode parentMatchNode = this;
            SSFQueryMatchNode matchNode = null;

            while(qstring.length() > 0)
            {
//                System.out.println(makeString() + "\n**********************************\n");

                System.out.println(qstring);

                if(matchNode != null) {
                    System.out.println(matchNode.makeString() + "\n**********************************\n");
                }

                if(parentMatchNode != null) {
                    System.err.println(parentMatchNode.makeString() + "\n**********************************\n");
                }
                
                if(qstring.trim().startsWith("("))
                {
                    qstring = qstring.trim();
                    nodeString = nodeString.trim();

                    if(!nodeString.equals(""))
                    {
                        matchNode = new SSFQueryMatchNode(ssfQuery, nodeString);

                        parsed = matchNode.parseQuery();

                        if(!parsed) {
                            return false;
                        }

                        parentMatchNode.add(matchNode);

                        nodeString = "";
                    }

                    qstring = qstring.substring(1);

                    matchNode = new SSFQueryMatchNode(ssfQuery, "");
                    matchNode.setOperator(SSFQueryOperatorType.PARENTHESIS);

                    if(nodeStack.size() == 0) {
                        parentMatchNode = this;
                    }
                    else if(nodeStack.size() > 0) {
                        parentMatchNode = nodeStack.peekLast();
                    }

                    nodeStack.add(matchNode);
                    parenthesisEndStack.add(false);

                    parentMatchNode.add(matchNode);//
                }
                else if(qstring.trim().startsWith(")") && matchNode != null)
                {
                    qstring = qstring.trim();
                    
                    if(nodeStack.size() == 0) {
                        return false;
                    }
                    
                    qstring = qstring.substring(1);

                    matchNode.setUserObject(nodeString);

                    parsed = matchNode.parseQuery();

                    if(!parsed) {
                        return false;
                    }

                    nodeStack.removeLast();
                    parenthesisEndStack.removeLast();
                    nodeString = "";

//                    parentMatchNode.add(matchNode);

//                    if((parentMatchNode.operator.equals(SSFQueryOperatorType.PARENTHESIS))
//                            && (matchNode.operator.equals(SSFQueryOperatorType.AND) || matchNode.operator.equals(SSFQueryOperatorType.OR)))
                    if(doublePop)
                    {
                        nodeStack.removeLast();
                        parenthesisEndStack.removeLast();
                        doublePop = false;
                    }

//                    if(nodeStack.size() == 0)
//                    {
//                        parentMatchNode = this;
//                        parenthesisEnd = true;
//                    }
//                    else if(nodeStack.size() > 0)
//                    {
//                        parentMatchNode = nodeStack.get(nodeStack.size() - 1);
//                        parenthesisEndStack.setElementAt(true, parenthesisEndStack.size() - 1);
//                    }
                    qstring = qstring.trim();
                }
                else if(qstring.trim().startsWith("AND "))
                {
                    qstring = qstring.trim();
                    qstring = qstring.substring(3);

//                    if(!qstring.trim().startsWith("("))
//                    {
                        qstring = qstring.trim();

                        if(!qstring.trim().startsWith("("))
                        {
                            if((nodeStack.size() == 0 && !parenthesisEnd)
                                    || (nodeStack.size() > 0 && parenthesisEndStack.peekLast().booleanValue() == false))
                            {
                                nodeString += " AND ";
                                matchNode.setOperator(SSFQueryOperatorType.AND);
                            }
                        }
                        else
                        {
                            nodeString = nodeString.trim();

                            if(!nodeString.equals(""))
                            {
                                SSFQueryMatchNode tempMatchNode = new SSFQueryMatchNode(ssfQuery, nodeString);

                                parsed = tempMatchNode.parseQuery();

                                if(!parsed) {
                                    return false;
                                }

                                matchNode.add(tempMatchNode);

                                nodeString = "";
                            }
                        }
//                    }

                    if(matchNode.operator.equals(SSFQueryOperatorType.PARENTHESIS) || nodeStack.isEmpty())
                    {
                        if(matchNode.operator.equals(SSFQueryOperatorType.PARENTHESIS)) {
                            parentMatchNode = matchNode;
                        }
                        else if(nodeStack.isEmpty()) {
                            parentMatchNode = this;
                        }

                        matchNode = new SSFQueryMatchNode(ssfQuery, "");
                        matchNode.setOperator(SSFQueryOperatorType.AND);

                        List<SSFQueryNode> chn = parentMatchNode.getAllChildren();
                        parentMatchNode.removeAllChildren();
                        matchNode.addChildren(chn);

                        nodeStack.add(matchNode);

                        parentMatchNode.add(matchNode);

                        if(parenthesisEndStack.size() == 0) {
                            boolean add = parenthesisEndStack.add(false);
                        }
                        else {
                            parenthesisEndStack.add(parenthesisEndStack.get(parenthesisEndStack.size() - 1));
                        }

                        doublePop = true;
                    }

//                    matchNode.setOperator(SSFQueryOperatorType.AND);
                    qstring = qstring.trim();
                }
                else if(qstring.trim().startsWith("OR "))
                {
                    qstring = qstring.trim();
                    qstring = qstring.substring(3);
                    
//                    if(!parenthesisEnd)
//                    if(!qstring.trim().startsWith("("))
//                    {
                        qstring = qstring.trim();

                        if(!qstring.trim().startsWith("("))
                        {
                            if((nodeStack.size() == 0 && !parenthesisEnd)
                                    || (nodeStack.size() > 0 && parenthesisEndStack.peekLast().booleanValue() == false))
                            {
                                nodeString += " OR ";
                                matchNode.setOperator(SSFQueryOperatorType.OR);
                            }
                        }
                        else
                        {
                            nodeString = nodeString.trim();

                            if(!nodeString.equals(""))
                            {
                                SSFQueryMatchNode tempMatchNode = new SSFQueryMatchNode(ssfQuery, nodeString);

                                parsed = matchNode.parseQuery();

                                if(!parsed) {
                                    return false;
                                }

                                matchNode.add(tempMatchNode);

                                nodeString = "";
                            }
                        }
//                    }

                    if(matchNode.operator.equals(SSFQueryOperatorType.PARENTHESIS) || nodeStack.size() == 0)
                    {
                        if(matchNode.operator.equals(SSFQueryOperatorType.PARENTHESIS)) {
                            parentMatchNode = matchNode;
                        }
                        else if(nodeStack.size() == 0) {
                            parentMatchNode = this;
                        }

                        matchNode = new SSFQueryMatchNode(ssfQuery, "");
                        matchNode.setOperator(SSFQueryOperatorType.OR);

                        List<SSFQueryNode> chn = parentMatchNode.getAllChildren();
                        parentMatchNode.removeAllChildren();
                        matchNode.addChildren(chn);

                        nodeStack.add(matchNode);

                        parentMatchNode.add(matchNode);

                        if(parenthesisEndStack.size() == 0) {
                            parenthesisEndStack.add(false);
                        }
                        else {
                            parenthesisEndStack.add(parenthesisEndStack.get(parenthesisEndStack.size() - 1));
                        }

                        doublePop = true;
                    }

//                    matchNode.setOperator(SSFQueryOperatorType.OR);
                    qstring = qstring.trim();
                }
                else
                {
                    nodeString += qstring.charAt(0);
                    qstring = qstring.substring(1);
                }
            }

            if(operator == null) {
                operator = SSFQueryOperatorType.PARENTHESIS;
            }

            if(nodeStack.size() > 0) {
                return false;
            }

            nodeString = nodeString.trim();

            if(!nodeString.equals(""))
            {
                matchNode = new SSFQueryMatchNode(ssfQuery, nodeString);

                parsed = matchNode.parseQuery();

                if(!parsed) {
                    return false;
                }

                add(matchNode);
            }
        }

        return parsed;
    }

    private int isOnDS(String qstring)
    {
        Pattern p = Pattern.compile("^DS:", Pattern.CASE_INSENSITIVE);

        Matcher m = p.matcher(qstring);

        if(m.find()) {
            return m.group().length();
        }

        return -1;
    }

    public boolean parseQueryHelper(String qstring) throws Exception
    {
        qstring = qstring.trim();

        boolean parsed = true;

        if(ssfQueryLexicalAnalyser.getToken(0).getTokenType().equals(SSFQueryTokenType.COMMAND))
        {
            operator = SSFQueryOperatorType.COMMAND;
            setUserObject(qstring);

            return true;
        }

//        Pattern p = Pattern.compile("^DS:", Pattern.CASE_INSENSITIVE);
//
//        Matcher m = p.matcher(qstring);

//        if(m.find())
        int onDS = isOnDS(qstring);

        if(onDS >= 0)
        {
            operator = SSFQueryOperatorType.ON_DS;

//            String arg = qstring.substring(3);
            String arg = qstring.substring(onDS);

            SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            parsed = matchNode.parseQuery();

            if(!parsed) {
                return false;
            }

            add(matchNode);

            return true;
        }

        if(qstring.startsWith("(") && qstring.endsWith(")"))
        {
            operator = SSFQueryOperatorType.PARENTHESIS;

            String arg = stripBoundingStrings(qstring, "(", ")");

            SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            parsed = matchNode.parseQuery();

            if(!parsed) {
                return false;
            }

            add(matchNode);

            return true;
        }

        if(qstring.startsWith("!(") && qstring.endsWith(")"))
        {
            operator = SSFQueryOperatorType.NOT;

            String arg = stripBoundingStrings(qstring, "!(", ")");

            SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            parsed = matchNode.parseQuery();

            if(!parsed) {
                return false;
            }

            add(matchNode);

            return true;
        }
        
        Pattern p = Pattern.compile("\\s+AND\\s+", Pattern.CASE_INSENSITIVE);
//        p = Pattern.compile("(\\(?\\s*[^\\(\\)]+\\)?)\\s+AND\\s+(\\(?\\s*[^\\(\\)]+)", Pattern.CASE_INSENSITIVE);

        String args[] = p.split(qstring);

        if(args.length > 1)
        {
            operator = SSFQueryOperatorType.AND;

            for (int i = 0; i < args.length; i++)
            {
                String arg = args[i].trim();

                SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

                parsed = matchNode.parseQuery();

                if(!parsed) {
                    return false;
                }

                add(matchNode);
            }

            return true;
        }

        p = Pattern.compile("\\s+OR\\s+", Pattern.CASE_INSENSITIVE);

        args = p.split(qstring);

        if(args.length > 1)
        {
            operator = SSFQueryOperatorType.OR;

            for (int i = 0; i < args.length; i++)
            {
                String arg = args[i].trim();

                SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

                parsed = matchNode.parseQuery();

                if(!parsed) {
                    return false;
                }

                add(matchNode);
            }

            return true;
        }

        args = qstring.split("!=");

        if(args.length == 2)
        {
            operator = SSFQueryOperatorType.NOT_EQUAL;

            String arg = args[0].trim();

            SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            matchNode.setOperator(SSFQueryOperatorType.VARIABLE);

//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(matchNode);

            arg = args[1].trim();

            matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            matchNode.setOperator(SSFQueryOperatorType.VALUE);

//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(matchNode);

            return true;
        }

        args = qstring.split("=");

        if(args.length == 2)
        {
            operator = SSFQueryOperatorType.EQUAL;

            String arg = args[0].trim();

            SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            matchNode.setOperator(SSFQueryOperatorType.VARIABLE);

//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(matchNode);

            arg = args[1].trim();

            matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            matchNode.setOperator(SSFQueryOperatorType.VALUE);

//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(matchNode);

            return true;
        }

        args = qstring.split("!~");

        if(args.length == 2)
        {
            operator = SSFQueryOperatorType.NOT_LIKE;

            String arg = args[0].trim();

            SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            matchNode.setOperator(SSFQueryOperatorType.VARIABLE);

//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(matchNode);

            arg = args[1].trim();

            matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            matchNode.setOperator(SSFQueryOperatorType.VALUE);

//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(matchNode);

            return true;
        }

        args = qstring.split("~");

        if(args.length == 2)
        {
            operator = SSFQueryOperatorType.LIKE;

            String arg = args[0].trim();

            SSFQueryMatchNode matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            matchNode.setOperator(SSFQueryOperatorType.VARIABLE);

//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(matchNode);

            arg = args[1].trim();

            matchNode = new SSFQueryMatchNode(ssfQuery, arg);

            matchNode.setOperator(SSFQueryOperatorType.VALUE);

//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;

            add(matchNode);

            return true;
        }

//        if(args.length == 1)
//        {
//            SSFQueryMatchNode matchNode = new SSFQueryMatchNode(args[0], SSFQueryOperatorType.ATOM);
//
//            parsed = matchNode.parseQuery();
//
//            if(!parsed)
//                return false;
//
//            add(matchNode);
//        }

        return true;
    }

    protected boolean match(SSFNode node, SSFQueryOperatorType parentOperatorType) throws Exception
    {
        if(getOperator().equals(SSFQueryOperatorType.ON_DS))
        {
            int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                SSFQueryMatchNode mnode = (SSFQueryMatchNode) getChildAt(i);

                if(mnode.match(node, getOperator()) == false) {
                    return false;
                }
            }

            return true;
        }

        if(getOperator().equals(SSFQueryOperatorType.PARENTHESIS))
        {
            int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                SSFQueryMatchNode mnode = (SSFQueryMatchNode) getChildAt(i);

                if(mnode.match(node, getOperator()) == false) {
                    return false;
                }
            }

            return true;
        }

        if(getOperator().equals(SSFQueryOperatorType.NOT))
        {
            int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                SSFQueryMatchNode mnode = (SSFQueryMatchNode) getChildAt(i);

                if(mnode.match(node, getOperator()) == false) {
                    return true;
                }
            }

            return false;
        }

        if(getOperator().equals(SSFQueryOperatorType.AND))
        {
            int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                SSFQueryMatchNode mnode = (SSFQueryMatchNode) getChildAt(i);

                if(mnode.match(node, getOperator()) == false) {
                    return false;
                }
            }

            return true;
        }

        if(getOperator().equals(SSFQueryOperatorType.OR))
        {
            int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                SSFQueryMatchNode mnode = (SSFQueryMatchNode) getChildAt(i);

                if(mnode.match(node, getOperator()) == true) {
                    return true;
                }
            }

            return false;
        }

        if(getOperator().equals(SSFQueryOperatorType.EQUAL) || getOperator().equals(SSFQueryOperatorType.LIKE))
        {
            int count = getChildCount();

            if(count != 2)
            {
                System.err.println("SSF query processing error: the comparison operator has " + count + " arguments, not two as required.");
            }

            SSFQueryMatchNode varNode = (SSFQueryMatchNode) getChildAt(0);

            if(varNode.match(node, getOperator()) == true) {
                return true;
            }

            return false;
        }

        if(getOperator().equals(SSFQueryOperatorType.NOT_EQUAL) || getOperator().equals(SSFQueryOperatorType.NOT_LIKE))
        {
            int count = getChildCount();

            if(count != 2)
            {
                System.err.println("SSF query processing error: the comparison operator has " + count + " arguments, not two as required.");
            }

            SSFQueryMatchNode varNode = (SSFQueryMatchNode) getChildAt(0);

            if(varNode.match(node, getOperator()) == false) {
                return true;
            }

            return false;
        }

        if(getOperator().equals(SSFQueryOperatorType.VARIABLE))
        {
            int count = getChildCount();

            if(count != 0)
            {
                System.err.println("SSF query processing error: the variable operator has " + count + " arguments: it should have none.");
            }

            SSFQueryMatchNode valNode = (SSFQueryMatchNode) getNextSibling();

            if(SSFQueryMatchNode.matchValue(this, valNode, node, parentOperatorType) == true) {
                return true;
            }

            return false;
        }

        return false;
    }

    public static boolean matchValue(SSFQueryMatchNode varNode, SSFQueryMatchNode valNode, SSFNode node,
            SSFQueryOperatorType parentOperatorType) throws Exception
    {
        if(!(varNode.operator.equals(SSFQueryOperatorType.VARIABLE) && valNode.operator.equals(SSFQueryOperatorType.VALUE)))
        {
            System.err.println("SSF query processing error: wrong types of nodes for matching: "
                    + varNode.getOperator().getId() + " and " + valNode.getOperator().getId());

            return false;
        }

        String varString = (String) varNode.getUserObject();
        String valString = (String) valNode.getUserObject();

        LinkedHashMap<SSFNode, String> varSSFNodes = new LinkedHashMap<SSFNode, String>();
        List<QueryValue> nodes = new ArrayList<QueryValue>();

        NodeWildcardInfo varNodeWildcardInfo = getValue(varString, node, varSSFNodes, varNode);
        valString = getValue(valString, node, valNode);

        String matchAlias = null;
        String parts[] = varString.split("/");

        if(parts.length == 2)
        {
            matchAlias = parts[1];
        }

        int vcount = varSSFNodes.size();

        if(vcount == 0) {
            return false;
        }
        
        if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST))
        {
            varNodeWildcardInfo.rangeStart = 1;
            varNodeWildcardInfo.rangeEnd = vcount;
        }
        else if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST))
        {
            varNodeWildcardInfo.rangeStart = 1;
            varNodeWildcardInfo.rangeEnd = vcount;
        }

        boolean found = false;

        Iterator<SSFNode> itr = varSSFNodes.keySet().iterator();

//        int i = 1;
        int i = varNodeWildcardInfo.rangeStart;
        
        while(itr.hasNext() && i >= varNodeWildcardInfo.rangeStart && i <= varNodeWildcardInfo.rangeEnd)
        {
            SSFNode varSSFNode = itr.next();
            varString = varSSFNodes.get(varSSFNode);

            if(parentOperatorType.equals(SSFQueryOperatorType.LIKE) || parentOperatorType.equals(SSFQueryOperatorType.NOT_LIKE))
            {
                Pattern p = Pattern.compile(valString);

                Matcher m = p.matcher(varString);

                if(m.find())
                {
                    nodes.add(varSSFNode);

                    if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL)
                            || varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)
                            || varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)) {
                        found = found || true;
                    }
                    else if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                    {
                        if(i == varNodeWildcardInfo.rangeEnd) {
                            found = true;
                        }
                    }
                }
                else
                {
                    if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                    {
                        found = false;
                        break;
                    }
                }

    //            if(found)
    //                System.err.println("Match found: " + node);
            }
            else
            {
                if(varString.equals(valString))
                {
                    nodes.add(varSSFNode);

                    if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL)
                            || varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)
                            || varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)) {
                        found = found || true;
                    }
                    else if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                    {
                        if(i == varNodeWildcardInfo.rangeEnd) {
                            found = true;
                        }
                    }
                }
                else
                {
                    if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                    {
                        found = false;
                        break;
                    }
                }
            }

            i++;
        }

        if(found)
        {
            if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)) {
                varNode.getSSFQuery().addMatchedValue(matchAlias, nodes.get(0));
            }
            else if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)) {
                varNode.getSSFQuery().addMatchedValue(matchAlias, nodes.get(vcount - 1));
            }
            else if(varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL)
                    || varNodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE)) {
                varNode.getSSFQuery().addMatchedValues(matchAlias, nodes);
            }
        }

        return found;
    }

    protected static NodeWildcardInfo getSSFNodes(String varString, SSFNode node,
            List<SSFNode> varSSFNodes, SSFQueryNode queryNode) throws Exception
    {
        SSFNode varSSFNode = node;
        List<QueryValue> matchedNodes = new ArrayList<QueryValue>();
        NodeWildcardInfo nodeWildcardInfo = new NodeWildcardInfo(); // Wildcards etc.

        if(varString.equals("S"))
        {
            varSSFNodes.add((SSFNode) node.getRoot());

            nodeWildcardInfo.rangeStart = 1;
            nodeWildcardInfo.rangeEnd = 1;
            nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_RANGE;

            return nodeWildcardInfo;
        }

        String generation = "C";
        String generationGapStr = "";


        String varParts[] = varString.split("\\.");

        int varPartsLength = varParts.length;
        int minPartsLength = 2;
        int maxPartsLength = varParts.length - 1;

        if(Character.isUpperCase(varString.charAt(varString.length() - 1)))
        {
            minPartsLength = 1;
            maxPartsLength = varParts.length;
        }
//        if(varParts.length == 1)
        else if(varPartsLength == 1)
        {
            System.err.println("SSF query processing error: wrong variable syntax: "
                    + varString);

            return null;
        }

         List<SSFNode> prevStack = new ArrayList<SSFNode>();
         List<SSFNode> currentStack = new ArrayList<SSFNode>();

         prevStack.add(varSSFNode);

//        if(varParts.length >= 2)
        if(varParts.length >= minPartsLength)
        {
//            for (int i = 0; i < varParts.length - 1; i++)
            for (int i = 0; i < maxPartsLength; i++)
            {
                currentStack = new ArrayList<SSFNode>();
                
                int pscount = prevStack.size();

                for (int ps = 0; ps < pscount; ps++)
                {
                    varSSFNode = prevStack.get(ps);

                    generation = "" + varParts[i].charAt(0);

                    if(varParts[i].length() > 1)
                    {
                        generationGapStr = varParts[i].substring(1);

                        generationGapStr = stripBoundingStrings(generationGapStr, "[", "]");

                        if(i == 0 && generation.equals("M"))
                        {
                            if(generationGapStr.equals("")) {
                                matchedNodes = queryNode.getSSFQuery().getMatchedValues("" + 1);
                            }

                            String parts[] = generationGapStr.split("/");

                            if(parts.length == 2)
                            {
                                generationGapStr = parts[1];
                                matchedNodes = queryNode.getSSFQuery().getMatchedValues(parts[0]);
                            }
                            else {
                                matchedNodes = queryNode.getSSFQuery().getMatchedValues(generationGapStr);
                            }
                        }

                        if(i > 0 && generation.equals("M"))
                        {
                            System.err.println("Error in query syntax (matched nodes wrongly specified): "+ varString);
                            return nodeWildcardInfo;
                        }

                        if(generation.equals("R")) {
                            generationGapStr = stripQuotes(generationGapStr);
                        }
                        else
                        {
                            if(generationGapStr.equals("?"))
                            {
                                nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_FIRST;
                            }
                            else if(generationGapStr.equals("."))
                            {
                                nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_LAST;
                            }
                            else if(generationGapStr.equals("*"))
                            {
                                nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_ALL;
                            }
                            else if(generationGapStr.startsWith("-"))
                            {
                                generationGapStr = generationGapStr.substring(1);
                                nodeWildcardInfo.rangeEnd = Integer.parseInt(generationGapStr);
                                nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_RANGE;
                            }
                            else if(generationGapStr.endsWith("-"))
                            {
                                generationGapStr = generationGapStr.substring(0, generationGapStr.length() - 1);
                                nodeWildcardInfo.rangeStart = Integer.parseInt(generationGapStr);
                                nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_RANGE;
                            }
                            else if(generationGapStr.contains("-"))
                            {
                                String gparts[] = generationGapStr.split("\\-");

                                if(gparts.length == 2)
                                {
                                    nodeWildcardInfo.rangeStart = Integer.parseInt(gparts[0]);
                                    nodeWildcardInfo.rangeEnd = Integer.parseInt(gparts[1]);
                                    nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_RANGE;
                                }
                            }
                            else
                            {
                                if(generation.equals("M"))
                                {
                                    nodeWildcardInfo.rangeStart = 1;
                                    nodeWildcardInfo.rangeEnd = 1;
                                    nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_RANGE;
                                }
                                else
                                {
                                    nodeWildcardInfo.rangeStart = Integer.parseInt(generationGapStr);
                                    nodeWildcardInfo.rangeEnd = Integer.parseInt(generationGapStr);
                                    nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_RANGE;
                                }
                            }
                        }

                        nodeWildcardInfo.wildcardString = generationGapStr;
                    }
                    else
                    {
                        if(i == 0 && generation.equals("M"))
                        {
                            matchedNodes = queryNode.getSSFQuery().getMatchedValues("" + 1);
                        }
                        else if(i > 0 && generation.equals("M"))
                        {
                            System.err.println("Error in query syntax (matched nodes wrongly specified): "+ varString);
                            return nodeWildcardInfo;
                        }

                        if(generation.equals("R")) {
                            generationGapStr = "drel";
                        }
                        else
                        {
                            nodeWildcardInfo.rangeStart = 1;
                            nodeWildcardInfo.rangeEnd = 1;
                            nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_RANGE;
                        }
                    }

                    if(generation.equals("C"))
                    {
                        currentStack.add(varSSFNode);
                        nodeWildcardInfo.wildcardTokenType = SSFQueryTokenType.WILDCARD_FIRST;
                    }
                    else if(i == 0 && generation.equals("M"))
                    {
                        if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                        {
                            boolean found = false;

                            int mcount = matchedNodes.size();
                            
                            if(mcount > 0)
                            {
                                for (int j = nodeWildcardInfo.rangeStart; j <= nodeWildcardInfo.rangeEnd; j++)
                                {
                                    if(j <= mcount)
                                    {
                                        varSSFNode = (SSFNode) matchedNodes.get(j - 1);
                                        currentStack.add(varSSFNode);

                                        found = (found || true);
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                        else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL))
                        {
                            boolean found = false;

                            int mcount = matchedNodes.size();

                            for (int j = 0; j < mcount; j++)
                            {
                                varSSFNode = (SSFNode) matchedNodes.get(j);
                                currentStack.add(varSSFNode);

                                found = (found || true);
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                    }
                    else if(generation.equals("A"))
                    {
                        if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                        {
                            boolean found = false;

                            for (int j = 1; j <= nodeWildcardInfo.rangeEnd; j++)
                            {
                                if(varSSFNode.getParent() != null)
                                {
                                    varSSFNode = (SSFNode) varSSFNode.getParent();
                                    
                                    if(j >= nodeWildcardInfo.rangeStart)
                                    {
                                        currentStack.add(varSSFNode);

                                        found = (found || true);
                                    }
                                }
                                else {
                                    break;
                                }
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                        else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL))
                        {
                            boolean found = false;

                            while(varSSFNode.getParent() != null)
                            {
                                varSSFNode = (SSFNode) varSSFNode.getParent();
                                currentStack.add(varSSFNode);

                                found = (found || true);
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                    }
                    else if(generation.equals("D"))
                    {
                        if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                        {
                            boolean found = false;

                            int ccount = varSSFNode.getChildCount();
                            if(ccount > 0)
                            {
                                for (int j = nodeWildcardInfo.rangeStart; j <= nodeWildcardInfo.rangeEnd; j++)
                                {
                                    if(j <= ccount)
                                    {
                                        SSFNode varSSFNodeDesc = (SSFNode) varSSFNode.getChildAt(j - 1);
                                        currentStack.add(varSSFNodeDesc);

                                        found = (found || true);
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                        else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL))
                        {
                            boolean found = false;

                            int ccount = varSSFNode.getChildCount();

                            for (int j = 0; j < ccount; j++)
                            {
                                SSFNode varSSFNodeDesc = (SSFNode) varSSFNode.getChildAt(j);
                                currentStack.add(varSSFNodeDesc);

                                found = (found || true);
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                    }
                    else if(generation.equals("P"))
                    {
                        if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                        {
                            boolean found = false;

                            for (int j = 1; j <= nodeWildcardInfo.rangeEnd; j++)
                            {
                                if(varSSFNode instanceof SSFPhrase)
                                {
                                    if(varSSFNode.getPrevious() != null)
                                    {
                                        varSSFNode = varSSFNode.getPrevious();

                                        if(j >= nodeWildcardInfo.rangeStart)
                                        {
                                            currentStack.add(varSSFNode);

                                            found = (found || true);
                                        }
                                    }
                                    else {
                                        break;
                                    }
                                }
                                else if(varSSFNode instanceof SSFLexItem)
                                {
                                    if(varSSFNode.getPreviousLeaf() != null)
                                    {
                                        varSSFNode = (SSFNode) varSSFNode.getPreviousLeaf();

                                        if(j >= nodeWildcardInfo.rangeStart)
                                        {
                                            currentStack.add(varSSFNode);

                                            found = (found || true);
                                        }
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                        else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL))
                        {
                            boolean found = false;

                            if(varSSFNode instanceof SSFPhrase)
                            {
                                while(varSSFNode.getPrevious() != null)
                                {
                                    varSSFNode = varSSFNode.getPrevious();
                                    currentStack.add(varSSFNode);

                                    found = (found || true);
                                }
                            }
                            else if(varSSFNode instanceof SSFLexItem)
                            {
                                while(varSSFNode.getPreviousLeaf() != null)
                                {
                                    varSSFNode = (SSFNode) varSSFNode.getPreviousLeaf();
                                    currentStack.add(varSSFNode);

                                    found = (found || true);
                                }
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                    }
                    else if(generation.equals("N"))
                    {
                        if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                        {
                            boolean found = false;

                            for (int j = 1; j <= nodeWildcardInfo.rangeEnd; j++)
                            {
                                if(varSSFNode instanceof SSFPhrase)
                                {
                                    if(varSSFNode.getNext() != null)
                                    {
                                        varSSFNode = varSSFNode.getNext();
                                        
                                        if(j >= nodeWildcardInfo.rangeStart)
                                        {
                                            currentStack.add(varSSFNode);

                                            found = (found || true);
                                        }
                                    }
                                    else {
                                        break;
                                    }
                                }
                                else if(varSSFNode instanceof SSFLexItem)
                                {
                                    if(varSSFNode.getNextLeaf() != null)
                                    {
                                        varSSFNode = (SSFNode) varSSFNode.getNextLeaf();
                                        
                                        if(j >= nodeWildcardInfo.rangeStart)
                                        {
                                            currentStack.add(varSSFNode);

                                            found = (found || true);
                                        }
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                        else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL))
                        {
                            boolean found = false;

                            if(varSSFNode instanceof SSFPhrase)
                            {
                                while(varSSFNode.getNext() != null)
                                {
                                    varSSFNode = varSSFNode.getNext();
                                    currentStack.add(varSSFNode);

                                    found = (found || true);
                                }
                            }
                            else if(varSSFNode instanceof SSFLexItem)
                            {
                                while(varSSFNode.getNextLeaf() != null)
                                {
                                    varSSFNode = (SSFNode) varSSFNode.getNextLeaf();
                                    currentStack.add(varSSFNode);

                                    found = (found || true);
                                }
                            }

                            if(!found) {
                                return nodeWildcardInfo;
                            }
                        }
                    }
                    else if(generation.equals("R"))
                    {
                        if(varSSFNode instanceof SSFPhrase)
                        {
                            SSFNode referredNode = ((SSFPhrase) varSSFNode).getReferredNode(generationGapStr);

                            if(referredNode != null)
                            {
                                varSSFNode = referredNode;
                                currentStack.add(varSSFNode);
                            }
                            else {
                                return nodeWildcardInfo;
                            }
                        }
                        else {
                            return nodeWildcardInfo;
                        }
                    }
                    else if(generation.equals("T"))
                    {
                        if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_RANGE))
                        {
                            if(varSSFNode instanceof SSFPhrase)
                            {
                                List<SSFNode> referringNodes = ((SSFPhrase) varSSFNode.getRoot()).getReferringNodes(varSSFNode, SSFPhrase.DEPENDENCY_RELATIONS_MODE);

                                boolean found = false;

                                if(referringNodes != null && referringNodes.size() > 0)
                                {
                                    for (int j = nodeWildcardInfo.rangeStart; j <= nodeWildcardInfo.rangeEnd; j++)
                                    {
                                        if(j <= referringNodes.size())
                                        {
                                            varSSFNode = referringNodes.get(j - 1);
                                            currentStack.add(varSSFNode);

                                            found = (found || true);
                                        }
                                    }
                                }

                                if(!found) {
                                    return nodeWildcardInfo;
                                }
                            }
                            else {
                                return nodeWildcardInfo;
                            }
                        }
                        else if(nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_FIRST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_LAST)
                                || nodeWildcardInfo.wildcardTokenType.equals(SSFQueryTokenType.WILDCARD_ALL))
                        {
                            if(varSSFNode instanceof SSFPhrase)
                            {
                                List<SSFNode> referringNodes = ((SSFPhrase) varSSFNode.getRoot()).getReferringNodes(varSSFNode, SSFPhrase.DEPENDENCY_RELATIONS_MODE);

                                boolean found = false;

                                if(referringNodes != null && referringNodes.size() > 0)
                                {
                                    int rcount = referringNodes.size();

                                    for (int j = 0; j < rcount; j++)
                                    {
                                        varSSFNode = referringNodes.get(j);
                                        currentStack.add(varSSFNode);

                                        found = (found || true);
                                    }
                                }

                                if(!found) {
                                    return nodeWildcardInfo;
                                }
                            }
                            else {
                                return nodeWildcardInfo;
                            }
                        }
                    }
                }

                prevStack = currentStack;
            }
        }
        else {
            System.err.println("Error in query syntax: " + varString);
        }

        varSSFNodes.addAll(currentStack);

        return nodeWildcardInfo;
    }

    public static boolean isLiteralValue(String valString)
    {
        boolean literalValue = true;

        if(!(valString.startsWith("'") && valString.endsWith("'"))
                && !(valString.startsWith("\"") && valString.endsWith("\""))) {
            literalValue = false;
        }

        return literalValue;
    }

    public static String stripQuotes(String valString)
    {
        if(valString.equals("''")) {
            return "";
        }

        if(valString.startsWith("'") && valString.length() > 1) {
            valString = valString.substring(1);
        }

        if(valString.endsWith("'") && valString.length() > 1) {
            valString = valString.substring(0, valString.length() - 1);
        }

        if(valString.startsWith("\"") && valString.length() > 1) {
            valString = valString.substring(1);
        }

        if(valString.endsWith("\"") && valString.length() > 1) {
            valString = valString.substring(0, valString.length() - 1);
        }

        return valString;
    }

    public static String stripBoundingStrings(String varString, String boundingStringStart, String boundingStringEnd)
    {
//        if(varString.startsWith(boundingStringStart) && varString.length() > boundingStringStart.length())
//            varString = varString.substring(boundingStringStart.length());
//
//        if(varString.endsWith(boundingStringEnd) && varString.length() > boundingStringEnd.length())
//            varString = varString.substring(0, varString.length() - boundingStringEnd.length());

        if(varString.startsWith(boundingStringStart) && varString.endsWith(boundingStringEnd) 
                && varString.length() > (boundingStringStart.length() + boundingStringEnd.length()))
        {
            varString = varString.substring(boundingStringStart.length() );
            varString = varString.substring(0, varString.length() - boundingStringEnd.length());
        }

        return varString;
    }

    public static String stripMatchAlias(String valString)
    {
        if(valString.contains("/"))
        {
            String parts[] = valString.split("/");

            if(parts.length == 2)
            {
                return parts[0];
            }
            else {
                System.err.println("Error in query syntax (match alias): " + valString);
            }
        }

        return valString;
    }

    protected static String getValue(String valString, SSFNode node, SSFQueryNode queryNode) throws Exception
    {
        String parts[] = valString.split("\\+");

        Pattern escapePattern = Pattern.compile("\\\\$");

        valString = "";

        boolean prevEscape = false;
        boolean escape = false;

        for (int i = 0; i < parts.length; i++)
        {
            String vstring = parts[i];

            vstring = vstring.trim();

            if(parts.length > 1)
            {
                Matcher escapeMatcher = escapePattern.matcher(parts[i]);

                prevEscape = escape;

                if(escapeMatcher.find())
                {
                    escape = true;
                    vstring = vstring.replaceAll("\\\\", "");
                }
                else {
                    escape = false;
                }
            }

            if(escape || prevEscape)
            {
                    valString += vstring;
            }
            else if(escape == false)
            {
                boolean literalValue = isLiteralValue(vstring);

                if(!literalValue)
                {
                    vstring = getValueHelper(vstring, node, queryNode);
                }
                else
                {
                    vstring = stripQuotes(vstring);
                }

                valString += vstring;
            }
        }

        return valString;
    }

    protected static String getValueHelper(String varString, SSFNode node, SSFQueryNode queryNode) throws Exception
    {
        List<SSFNode> varSSFNodes = new ArrayList<SSFNode>();
        NodeWildcardInfo nodeWildcardInfo = getSSFNodes(varString, node, varSSFNodes, queryNode);

//        varString = SSFQueryMatchNode.stripMatchAlias(varString);

        int vcount = varSSFNodes.size();

        if(vcount == 0) {
            return null;
        }

        SSFNode varSSFNode = varSSFNodes.get(0);
//        String valueString = getValueHelper(varString, node, varSSFNode);
        String valueString = getValueHelper(varString, varSSFNode);

        return valueString;
    }

    protected static NodeWildcardInfo getValue(String varString, SSFNode node,
            LinkedHashMap<SSFNode, String> varSSFNodes, SSFQueryNode queryNode) throws Exception
    {
        List<SSFNode> varSSFNodesVec = new ArrayList<SSFNode>();

        varString = SSFQueryMatchNode.stripMatchAlias(varString);

        NodeWildcardInfo nodeWildcardInfo = getSSFNodes(varString, node, varSSFNodesVec, queryNode);

        int vcount = varSSFNodesVec.size();

        if(vcount == 0) {
            return null;
        }

        for (int i = 0; i < vcount; i++)
        {
            SSFNode varSSFNode = varSSFNodesVec.get(i);
//            String valueString = getValueHelper(varString, node, varSSFNode);
            String valueString = getValueHelper(varString, varSSFNode);

            if(valueString != null) {
                varSSFNodes.put(varSSFNode, valueString);
            }
        }

        return nodeWildcardInfo;
    }

//    protected static String getValueHelper(String varString, SSFNode node, SSFNode varSSFNode) throws Exception
    protected static String getValueHelper(String varString, SSFNode varSSFNode) throws Exception
    {
        if(varSSFNode == null) {
            return null;
        }

        String varParts[] = varString.split("\\.");

        if(varParts.length == 1)
        {
            System.err.println("SSF query processing error: wrong variable syntax: "
                    + varString);

            return null;
        }

        if(varParts.length >= 2)
        {
            varString = varParts[varParts.length - 1];
        }

        if(varString.equals("l"))
        {
            if(varSSFNode instanceof SSFLexItem) {
                return varSSFNode.getLexData();
            }
            else if(varSSFNode instanceof SSFPhrase) {
                return varSSFNode.makeRawSentence();
            }
        }
        else if(varString.equals("t"))
        {
            return varSSFNode.getName();
        }
        else if(varString.equals("f"))
        {
            if(varSSFNode instanceof SSFLexItem) {
                return "t";
            }
            else {
                return "f";
            }
        }
        else if(varString.equals("v"))
        {
                return "" + varSSFNode.getLevel();
        }
        else if(varString.startsWith("a") || varString.startsWith("A"))
        {
            varString = varString.substring(1);

            varString = stripBoundingStrings(varString, "[", "]");
            varString = stripQuotes(varString);

            String attribVal = varSSFNode.getAttributeValue(varString);

            if(attribVal == null) {
                return null;
            }

            if(attribVal.contains(":"))
            {
                String attribVaParts[] = attribVal.split(":");

                varString = attribVaParts[0];
            }
            else {
                varString = attribVal;
            }

            return varString;
        }

        return null;
    }

    public void showTreeJPanel(String langEnc) {
        JDialog realTreeDialog = null;

        JDialog dialog = null;
        JFrame owner = null;

        if(dialog != null) {
            realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Tree_Viewer"), true);
        }
        else {
            realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Tree_Viewer"), true);
        }

        SanchayTreeJPanel realTreeJPanel = null;

        realTreeJPanel = SanchayTreeJPanel.createDefaultTreeJPanel(this, langEnc);

        realTreeJPanel.setDialog(realTreeDialog);

        realTreeDialog.add(realTreeJPanel);
        realTreeDialog.setBounds(80, 30, 900, 700);

        realTreeDialog.setVisible(true);
        realTreeJPanel.treeJTree.collapseRow(0);
    }

    public void showTreeView(String langEnc) {
        JDialog realTreeDialog = null;

        JDialog dialog = null;
        JFrame owner = null;

        if(dialog != null) {
            realTreeDialog = new JDialog(dialog, GlobalProperties.getIntlString("Tree_Viewer"), true);
        }
        else {
            realTreeDialog = new JDialog(owner, GlobalProperties.getIntlString("Tree_Viewer"), true);
        }

        SanchayTreeViewerJPanel realTreeJPanel = null;

        realTreeJPanel = new SanchayTreeViewerJPanel(this, SanchayMutableTreeNode.CHUNK_MODE, langEnc);

        realTreeJPanel.setDialog(realTreeDialog);
        realTreeJPanel.setColumnClasses();

        realTreeDialog.add(realTreeJPanel);
        realTreeJPanel.sizeToFit();

        realTreeDialog.setVisible(true);
    }

    public static void main(String[] args)
    {
        try {
//            SSFQueryMatchNode ssfQueryMatchNode = new SSFQueryMatchNode("(C.t='NN' AND (C.f='f' OR A.t~'V')) AND (C.l='के' OR C.l='तो')");
//            SSFQueryMatchNode ssfQueryMatchNode = new SSFQueryMatchNode("(C.t='NN' AND (C.f='f' OR A.t~'V')) AND C.v = '1' AND (C.l='के' OR C.l='तो')");
//            SSFQueryMatchNode ssfQueryMatchNode = new SSFQueryMatchNode(null, "((C.t~'^N' OR C.t!~'^V') AND C.f='t') AND (C.l~'है' AND C.t~'V')");
//            SSFQueryMatchNode ssfQueryMatchNode = new SSFQueryMatchNode(null, "!(((C.t~'^N' OR C.t!~'^V') AND C.f='t') AND !((C.l~'है' AND C.t~'V')))");
            SSFQueryMatchNode ssfQueryMatchNode = new SSFQueryMatchNode(null, "C.l='ke' and !(C.t~'^N') and C.f='t'");

            System.out.println("Parsed: " + ssfQueryMatchNode.parseQuery());

//            ssfQueryMatchNode.showTreeJPanel("hin:utf8");
            ssfQueryMatchNode.showTreeView("hin:utf8");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
