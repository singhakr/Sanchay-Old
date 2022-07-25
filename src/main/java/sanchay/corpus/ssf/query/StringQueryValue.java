/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

/**
 *
 * @author anil
 */
public class StringQueryValue implements QueryValue {

    protected String value;
    protected Object object;

    public StringQueryValue()
    {
        
    }

    public StringQueryValue(String value, Object object)
    {
        this.value = value;
        this.object = object;
    }

    @Override
    public Object getQueryReturnValue() {
        return value;
    }

    @Override
    public void setQueryReturnValue(Object rv) {
        value = (String) rv;
    }

    @Override
    public Object getQueryReturnObject() {
        return object;
    }

    @Override
    public void setQueryReturnObject(Object rv) {
        object = rv;
    }

    public String toString()
    {
        return value;
    }
}
