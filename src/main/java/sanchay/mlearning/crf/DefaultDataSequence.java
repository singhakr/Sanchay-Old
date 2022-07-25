/*
 * DefaultDataSequence.java
 *
 * Created on September 7, 2008, 11:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.mlearning.crf;

import iitb.crf.DataSequence;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Vector;
import sanchay.corpus.ssf.features.FeatureStructures;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author Anil Kumar Singh
 */
public class DefaultDataSequence implements DataSequence, Serializable {
    
    protected int length;    
    protected Vector xvector;
    protected Vector yvector;
    
    /** Creates a new instance of DefaultDataSequence */
    public DefaultDataSequence()
    {
        super();

        xvector = new Vector(0, 10);
        yvector = new Vector(0, 10);
    }

    public int length()
    {
        length = xvector.size();
        
        return length;
    }

    public int y(int i)
    {
        return ((Integer) yvector.get(i)).intValue();
    }

    public Object x(int i)
    {
        return xvector.get(i);
    }

    public void set_y(int i, int i0)
    {
        yvector.setElementAt(new Integer(i0), i);
    }

    public void add_x(Object x0)
    {
        xvector.add(x0);
        yvector.add(new Integer(0));
        length++;
    }
    
    public void printRaw(PrintStream ps)
    {
        for (int i = 0; i < length; i++)
        {
            ps.print(x(i));
            
            if(i < length - 1)
                ps.print(" ");
            else
                ps.println();
        }
    }
    
    public void printTagged(PrintStream ps)
    {
        for (int i = 0; i < length; i++)
        {
            ps.println(x(i) + " |" + (y(i) + 1));
            
            if(i == length - 1)
                ps.println();
        }
    }
}
