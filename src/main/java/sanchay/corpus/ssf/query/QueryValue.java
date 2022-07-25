/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

/**
 *
 * @author anil
 */
public interface QueryValue {

    Object getQueryReturnValue();
    void setQueryReturnValue(Object rv);

    Object getQueryReturnObject();
    void setQueryReturnObject(Object rv);
}
