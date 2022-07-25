/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;

import java.util.Vector;

/**
 *
 * @author eklavya
 */
public class DefaultMorphSegmenter implements MorphSegmenter {

    protected String langEnc;

    public DefaultMorphSegmenter()
    {

    }

    public DefaultMorphSegmenter(String langEnc)
    {

    }

    public String[] getSegmentStrings(String string)
    {
        return null;
    }

    public Vector getSegments(String string)
    {
        return null;
    }

    public String[] getSegmentStrings(String string, int granularity)
    {
        return null;
    }

    public Vector getSegments(String string, int granularity)
    {
        return null;
    }
}
