/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.util;

/**
 *
 * @author anil
 */
public class Pair<F extends Object, S extends Object> {
    public F first;
    public S second;

    public Pair()
    {
        
    }
    
    public Pair(F f, S s)
    {
        this.first = f;
        this.second = s;
    }
    
    @Override
    public String toString()
    {
        return first + "=>" + second;
    }
    
    public boolean equals(Pair pair)
    {
        if((first == null && pair.first != null)
                || (first != null && pair.first == null))
        {
            return false;
        }
        else if((second == null && pair.second != null)
                || (second != null && pair.second == null))
        {
            return false;
        }

        if((first != null && pair.first != null)
                && (second != null && pair.second != null))
        {
            if(first.equals(pair.first) && second.equals(pair.second))
            {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public Pair clone() throws CloneNotSupportedException
    {
        Pair pair = (Pair) super.clone();
        
        pair.first = first;
        pair.second = second;
        
        return pair;
    }
}
