/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.text.spell;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author anil
 */
public class Probabilities {

    protected LinkedHashMap<Object,Probability> priorProbabilities;

    public Probabilities()
    {
        priorProbabilities = new LinkedHashMap(0, 100);
    }

    public int countPriors()
    {
        if(priorProbabilities == null)
            return 0;

        return priorProbabilities.size();
    }

    public Iterator getPriors()
    {
        if(priorProbabilities == null)
            return null;

        return priorProbabilities.keySet().iterator();
    }

    public Probability getPriorProb(String prior)
    {
        return priorProbabilities.get(prior);
    }

    public void addPriorProb(String prior, Probability Probs)
    {
        priorProbabilities.put(prior, Probs);
    }

    public void removePriorProb(String prior)
    {
        priorProbabilities.remove(prior);
    }
}
