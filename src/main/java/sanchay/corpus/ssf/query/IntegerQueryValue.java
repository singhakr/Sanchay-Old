/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

/**
 *
 * @author anil
 */
public class IntegerQueryValue {

    protected Integer value;
    protected Object object;


    public Object getQueryReturnValue()
    {
        return value;
    }

    public void setQueryReturnValue(Object rv)
    {
        value = (Integer) rv;
    }

    public Object getQueryReturnObject() {
        return object;
    }

    public void setQueryReturnObject(Object rv) {
        object = rv;
    }

    @Override
    public String toString()
    {
        return "" + value;
    }
}
