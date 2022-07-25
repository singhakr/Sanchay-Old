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
public interface MorphSegmenter {

    String[] getSegmentStrings(String string);

    Vector getSegments(String string);

    String[] getSegmentStrings(String string, int granularity);

    Vector getSegments(String string, int granularity);

}
