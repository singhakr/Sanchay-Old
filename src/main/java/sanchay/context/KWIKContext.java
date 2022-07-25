/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.context;

import sanchay.context.impl.Context;
import sanchay.context.impl.FunctionalContextElement;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * @author Anil Kumar Singh
 */
public class KWIKContext<K, E, CE extends KWIKContextElement<E>> implements Context<K, E, CE> {

    protected CE leftContext;
    protected CE keyword;
    protected CE rightContext;

    /**
     * @return the leftContext
     */
    public CE getLeftContext() {
        return leftContext;
    }

    /**
     * @param leftContext the leftContext to set
     */
    public void setLeftContext(CE leftContext) {
        this.leftContext = leftContext;
    }

    /**
     * @return the keyword
     */
    public CE getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(CE keyword) {
        this.keyword = keyword;
    }

    /**
     * @return the rightContext
     */
    public CE getRightContext() {
        return rightContext;
    }

    /**
     * @param rightContext the rightContext to set
     */
    public void setRightContext(CE rightContext) {
        this.rightContext = rightContext;
    }

    @Override
    public long countContextElementTokens(boolean recount)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<K> getContextElementKeys()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(PrintStream ps) throws IOException, Exception
    {
        ps.println(getLeftContext() + "\t" + getKeyword() + "\t" + getRightContext());
    }

    @Override
    public void pruneTopN(int n, boolean ascending)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
