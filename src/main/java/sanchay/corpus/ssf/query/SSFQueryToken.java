/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

import sanchay.common.types.SSFQueryOperatorType;
import sanchay.common.types.SSFQueryTokenType;

/**
 *
 * @author anil
 */
public class SSFQueryToken {

    protected String tokenString;
    protected SSFQueryTokenType tokenType;

    public SSFQueryToken(String tokenString, SSFQueryTokenType operator) {
        this.tokenString = tokenString;
        this.tokenType = operator;
    }

    /**
     * @return the tokenString
     */
    public String getTokenString()
    {
        return tokenString;
    }

    /**
     * @param tokenString the tokenString to set
     */
    public void setTokenString(String tokenString)
    {
        this.tokenString = tokenString;
    }

    /**
     * @return the tokenType
     */
    public SSFQueryTokenType getTokenType()
    {
        return tokenType;
    }

    /**
     * @param operator the tokenType to set
     */
    public void setTokenType(SSFQueryTokenType tokenType)
    {
        this.tokenType = tokenType;
    }
}
