/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

/**
 *
 * @author anil
 */
public class StringQueryReturnValue implements QueryReturnValue {

    String value;
    Object object;

    public StringQueryReturnValue()
    {
        
    }

    public StringQueryReturnValue(String value, Object object)
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
