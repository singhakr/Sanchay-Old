/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

import java.io.PrintStream;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.common.types.SSFQueryTokenType;

/**
 *
 * @author anil
 */
public class SSFQueryLexicalAnalyser {

    protected String queryString;
    protected Vector<SSFQueryToken> tokens;

    public static Pattern dsPattern = Pattern.compile("^DS: ", Pattern.CASE_INSENSITIVE);
    public static Pattern parenthesisStartPattern = Pattern.compile("^\\(", Pattern.CASE_INSENSITIVE);
    public static Pattern parenthesisEndPattern = Pattern.compile("^\\)", Pattern.CASE_INSENSITIVE);
    public static Pattern andPattern = Pattern.compile("^AND ", Pattern.CASE_INSENSITIVE);
    public static Pattern orPattern = Pattern.compile("^OR ", Pattern.CASE_INSENSITIVE);
    public static Pattern notPattern = Pattern.compile("^!\\(", Pattern.CASE_INSENSITIVE);
    public static Pattern atomPattern1 = Pattern.compile("", Pattern.CASE_INSENSITIVE);
    public static Pattern cardinalityPattern = Pattern.compile("^\\|");

    // Command patterns
    public static Pattern cmdReallocateIDs = Pattern.compile("ReallocateIDs", Pattern.CASE_INSENSITIVE);
    public static Pattern cmdReallocateSenIDs = Pattern.compile("ReallocateSenIDs", Pattern.CASE_INSENSITIVE);
    public static Pattern cmdReallocateNames = Pattern.compile("ReallocateNames", Pattern.CASE_INSENSITIVE);
    public static Pattern cmdReallocatePosn = Pattern.compile("ReallocatePosn", Pattern.CASE_INSENSITIVE);

    public SSFQueryLexicalAnalyser() {
        tokens = new Vector<SSFQueryToken>();
    }
    
    public int countTokens()
    {
        return tokens.size();
    }

    public SSFQueryToken getToken(int num)
    {
        return tokens.get(num);
    }

    public int addToken(SSFQueryToken t)
    {
        tokens.add(t);
        return tokens.size();
    }

    public SSFQueryToken removeToken(int num)
    {
        return tokens.remove(num);
    }

    public void removeToken(SSFQueryToken t)
    {
        int ind = tokens.indexOf(t);

        if(ind != -1)
            removeToken(ind);
    }

    protected void addAtom(String atomString)
    {
        atomString = atomString.trim();

        if(atomString.equals("") == false)
        {
            SSFQueryToken token = new SSFQueryToken(atomString, SSFQueryTokenType.ATOM);

            addToken(token);
        }
    }

    public void readTokens(String queryString)
    {
        tokens.clear();
        this.queryString = queryString;

        String qstring = queryString;
        String atomString = "";

        boolean inCardinality = false;

        while(qstring.length() > 0)
        {
            qstring = qstring.trim();

            Matcher dsMatcher = dsPattern.matcher(qstring);
            Matcher parenthesisStartMatcher = parenthesisStartPattern.matcher(qstring);
            Matcher parenthesisEndMatcher = parenthesisEndPattern.matcher(qstring);
            Matcher andMatcher = andPattern.matcher(qstring);
            Matcher orMatcher = orPattern.matcher(qstring);
            Matcher notMatcher = notPattern.matcher(qstring);
            Matcher cardinalityMatcher = cardinalityPattern.matcher(qstring);
 
            // Command matchers
            Matcher cmdReallocateIDsMatcher = cmdReallocateIDs.matcher(qstring);
            Matcher cmdReallocateSenIDsMatcher = cmdReallocateSenIDs.matcher(qstring);
            Matcher cmdReallocateNamesMatcher = cmdReallocateNames.matcher(qstring);
            Matcher cmdReallocatePosMatcher = cmdReallocatePosn.matcher(qstring);

//            if(qstring.startsWith("DS: "))
            if(cmdReallocateIDsMatcher.find())
            {
                qstring = qstring.substring(1);

                SSFQueryToken token = new SSFQueryToken("reallocateIDs", SSFQueryTokenType.COMMAND);

                addToken(token);

                qstring = qstring.trim();
            }
            else if(cmdReallocateSenIDsMatcher.find())
            {
                qstring = qstring.substring(1);

                SSFQueryToken token = new SSFQueryToken("reallocateSenIDs", SSFQueryTokenType.COMMAND);

                addToken(token);

                qstring = qstring.trim();
            }
            else if(cmdReallocateNamesMatcher.find())
            {
                qstring = qstring.substring(1);

                SSFQueryToken token = new SSFQueryToken("reallocateNames", SSFQueryTokenType.COMMAND);

                addToken(token);

                qstring = qstring.trim();
            }
            else if(cmdReallocatePosMatcher.find())
            {
                qstring = qstring.substring(1);

                SSFQueryToken token = new SSFQueryToken("reallocatePosn", SSFQueryTokenType.COMMAND);

                addToken(token);

                qstring = qstring.trim();
            }
//            else if(dsMatcher.find())
//            {
////                qstring = qstring.substring(1);
//                qstring = qstring.substring(dsMatcher.group().length());
//
//                SSFQueryToken token = new SSFQueryToken("DS:", SSFQueryTokenType.ON_DS);
//
//                addToken(token);
//
//                qstring = qstring.trim();
//            }
//            else if(qstring.startsWith("("))
            else if(parenthesisStartMatcher.find())
            {
                addAtom(atomString);
                atomString = "";

                qstring = qstring.substring(1);

                SSFQueryToken token = new SSFQueryToken("(", SSFQueryTokenType.PARENTHESIS_START);

                addToken(token);

                qstring = qstring.trim();
            }
//            else if(cardinalityMatcher.find() && inCardinality == false)
//            {
//                addAtom(atomString);
//                atomString = "";
//
//                qstring = qstring.substring(1);
//
//                SSFQueryToken token = new SSFQueryToken("|", SSFQueryTokenType.CARDINALITY_START);
//
//                addToken(token);
//
//                qstring = qstring.trim();
//
//                inCardinality = true;
//            }
//            else if(cardinalityMatcher.find() && inCardinality == true)
//            {
//                addAtom(atomString);
//                atomString = "";
//
//                qstring = qstring.substring(1);
//
//                SSFQueryToken token = new SSFQueryToken("|", SSFQueryTokenType.CARDINALITY_END);
//
//                addToken(token);
//
//                qstring = qstring.trim();
//
//                inCardinality = false;
//            }
            else if(notMatcher.find())
            {
                addAtom(atomString);
                atomString = "";

                qstring = qstring.substring(2);

                SSFQueryToken token = new SSFQueryToken("NOT", SSFQueryTokenType.NOT);

                addToken(token);

                token = new SSFQueryToken("(", SSFQueryTokenType.PARENTHESIS_START);

                addToken(token);

                qstring = qstring.trim();
            }
//            else if(qstring.startsWith(")"))
            else if(parenthesisEndMatcher.find())
            {
                addAtom(atomString);
                atomString = "";

                qstring = qstring.substring(1);

                SSFQueryToken token = new SSFQueryToken(")", SSFQueryTokenType.PARENTHESIS_END);

                addToken(token);

                qstring = qstring.trim();
            }
//            else if(qstring.startsWith("AND "))
            else if(andMatcher.find())
            {
                addAtom(atomString);
                atomString = "";

                qstring = qstring.substring(3);

                SSFQueryToken token = new SSFQueryToken("AND", SSFQueryTokenType.AND);

                addToken(token);

                qstring = qstring.trim();
            }
//            else if(qstring.startsWith("OR "))
            else if(orMatcher.find())
            {
                addAtom(atomString);
                atomString = "";

//                qstring = qstring.substring(3);
                qstring = qstring.substring(2);

                SSFQueryToken token = new SSFQueryToken("OR", SSFQueryTokenType.OR);

                addToken(token);

                qstring = qstring.trim();
            }
            else
            {
                atomString += qstring.charAt(0);
                qstring = qstring.substring(1);
            }
        }

        if(atomString.equals("") == false)
            addAtom(atomString);
    }

    public String makeString()
    {
        String qstring = "";
        
        int count = countTokens();

        for (int i = 0; i < count; i++)
        {
            SSFQueryToken token = getToken(i);

            qstring += token.getTokenString() + " ";
        }

        return qstring.trim();
    }

    public void printTokens(PrintStream ps)
    {
        ps.println(makeString());
    }

    public static void main(String[] args)
    {
        SSFQueryLexicalAnalyser ssfQueryLexicalAnalyser = new SSFQueryLexicalAnalyser();

//        ssfQueryLexicalAnalyser.readTokens("(C.t='NN' AND (C.f='f' OR A.t~'V')) AND C.v = '1' AND (C.l='के' OR C.l='तो')");
//        ssfQueryLexicalAnalyser.readTokens("D[*].t='NN'");
        ssfQueryLexicalAnalyser.readTokens("!(((C.t~'^N' OR C.t!~'^V') AND C.f='t') AND !((C.l~'है' AND C.t~'V')))");


        ssfQueryLexicalAnalyser.printTokens(System.out);
    }
}
