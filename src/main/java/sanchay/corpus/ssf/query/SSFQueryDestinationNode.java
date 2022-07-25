/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import sanchay.SanchayMain;
import sanchay.common.types.SSFQueryOperatorType;
import sanchay.common.types.SSFQueryTokenType;
import sanchay.corpus.ssf.SSFSentence;
import sanchay.corpus.ssf.SSFStory;
import sanchay.corpus.ssf.impl.SSFSentenceImpl;
import sanchay.corpus.ssf.impl.SSFStoryImpl;
import sanchay.corpus.ssf.tree.SSFNode;
import sanchay.corpus.ssf.tree.SSFPhrase;

/**
 *
 * @author anil
 */
public class SSFQueryDestinationNode extends SSFQueryNode {

    protected SSFQueryLexicalAnalyser ssfQueryLexicalAnalyser;

    public SSFQueryDestinationNode(SSFQuery ssfQuery, Object userObject) {
        super(ssfQuery, userObject);
    }

    public SSFQueryDestinationNode(SSFQuery ssfQuery, Object userObject, boolean allowsChildren) {
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

        if(!qstring.contains("(")) {
            parsed = parseQueryHelper(qstring);
        }
        else if(qstring.startsWith("(") && qstring.endsWith(")") && qstring.indexOf("(", 1) == -1  && qstring.indexOf(")", 0) == qstring.length() - 1)
        {
            parsed = parseQueryHelper(qstring);
        }

        return parsed;
    }

    public boolean parseQueryHelper(String qstring) throws Exception
    {
        qstring = qstring.trim();

        boolean parsed = true;

        if(ssfQueryLexicalAnalyser.countTokens() > 0)
        {
            if(ssfQueryLexicalAnalyser.getToken(0).getTokenType().equals(SSFQueryTokenType.COMMAND))
            {
                operator = SSFQueryOperatorType.COMMAND;
                setUserObject(qstring);

                return true;
            }

            if(qstring.startsWith("(") && qstring.startsWith(")"))
            {
                operator = SSFQueryOperatorType.PARENTHESIS;

                String arg = SSFQueryMatchNode.stripBoundingStrings(qstring, "(", ")");

                SSFQueryDestinationNode destNode = new SSFQueryDestinationNode(ssfQuery, arg);

                parsed = destNode.parseQuery();

                if(!parsed) {
                    return false;
                }

                add(destNode);

                return true;
            }

            Pattern p = Pattern.compile("AND", Pattern.CASE_INSENSITIVE);
    //        p = Pattern.compile("(\\(?\\s*[^\\(\\)]+\\)?)\\s+AND\\s+(\\(?\\s*[^\\(\\)]+)", Pattern.CASE_INSENSITIVE);

            String args[] = p.split(qstring);

            if(args.length > 1)
            {
                operator = SSFQueryOperatorType.AND;

                for (int i = 0; i < args.length; i++)
                {
                    String arg = args[i].trim();

                    SSFQueryDestinationNode destNode = new SSFQueryDestinationNode(ssfQuery, arg);

                    parsed = destNode.parseQuery();

                    if(!parsed) {
                        return false;
                    }

                    add(destNode);
                }

                return true;
            }
        }

        QuerySourceDestination destination = new QuerySourceDestination();

        String args[] = qstring.split(":");

        operator = SSFQueryOperatorType.DESTINATION;

        if(args.length == 3)
        {
            destination.setFormat(args[0].trim());
            destination.setLocation(args[1].trim());
            destination.setCharset(args[2].trim());
        }
        else if(args.length == 2)
        {
            destination.setFormat(args[0].trim());
            destination.setLocation(args[1].trim());
        }
        else if(args.length == 1)
        {
            destination.setLocation(args[0].trim());

            if(destination.getLocation().equals(""))
            {
                JFileChooser chooser = new JFileChooser();
                
                int returnVal = chooser.showSaveDialog(SanchayMain.getSanchayMain());

                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    destination.setLocation(chooser.getSelectedFile().getAbsolutePath());

                    destination.setCharset(JOptionPane.showInputDialog(SanchayMain.getSanchayMain(), sanchay.GlobalProperties.getIntlString("Please_enter_the_charset:"), sanchay.GlobalProperties.getIntlString("UTF-8")));
                }
                else {
                    return false;
                }
            }
        }

        return true;
    }

    protected boolean send(SSFQueryOperatorType parentOperatorType) throws Exception
    {
        if(getOperator().equals(SSFQueryOperatorType.PARENTHESIS))
        {
            int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                SSFQueryDestinationNode dnode = (SSFQueryDestinationNode) getChildAt(i);

                if(dnode.send(getOperator()) == false) {
                    return false;
                }
            }

            return true;
        }

        if(getOperator().equals(SSFQueryOperatorType.AND))
        {
            int count = getChildCount();

            for (int i = 0; i < count; i++)
            {
                SSFQueryDestinationNode dnode = (SSFQueryDestinationNode) getChildAt(i);

                if(dnode.send(getOperator()) == false) {
                    return false;
                }
            }
        }

        if(getOperator().equals(SSFQueryOperatorType.DESTINATION))
        {
            Iterator<String> itr = ssfQuery.getReturnKeys();

            while(itr.hasNext())
            {
                String rkey = itr.next();
                
                List<QueryValue> returnValues = ssfQuery.getReturnValues(rkey);

                if(ssfQuery.getReturnLevel(rkey) == SSFQuery.SENTENCE)
                {
                    SSFStory ssfStory = ssfQuery.getDestination(rkey).getDocument();

                    if(ssfStory == null)
                    {
                        ssfStory = new SSFStoryImpl();
                        ssfQuery.getDestination(rkey).setDocument(ssfStory);
                    }
                    
                    int count = returnValues.size();

                    for (int i = 0; i < count; i++)
                    {

                        SSFNode node = (SSFNode) returnValues.get(i);
                        
                        SSFSentence sentence = new SSFSentenceImpl();
                        sentence.setRoot((SSFPhrase) node);

                        ssfStory.addSentence(sentence);
                    }
                }
                else
                {
                    PrintStream destinationStream = ssfQuery.getDestination(rkey).getStream();

                    if(destinationStream == null)
                    {
                        destinationStream = new PrintStream(ssfQuery.getDestination(rkey).getLocation(), ssfQuery.getDestination(rkey).getCharset());
                        ssfQuery.getDestination(rkey).setStream(destinationStream);
                    }
                    
                    int count = returnValues.size();

                    for (int i = 0; i < count; i++)
                    {

                        SSFNode node = (SSFNode) returnValues.get(i);

                        if(ssfQuery.getDestination(rkey).getFormat().equalsIgnoreCase("ssf")) {
                            destinationStream.println(node.makeString());
                        }
                        else if(ssfQuery.getDestination(rkey).getFormat().equalsIgnoreCase("bf")) {
                            destinationStream.println(node.convertToBracketForm(1));
                        }
                        else if(ssfQuery.getDestination(rkey).getFormat().equalsIgnoreCase("pos")) {
                            destinationStream.println(node.convertToPOSTagged());
                        }
                        else if(ssfQuery.getDestination(rkey).getFormat().equalsIgnoreCase("pos")) {
                            destinationStream.println(node.makeRawSentence());
                        }
                    }
                }
            }
        }

        return true;
    }
}
