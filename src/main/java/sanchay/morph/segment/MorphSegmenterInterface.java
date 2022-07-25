/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.morph.segment;

import java.io.FileNotFoundException;
import java.io.IOException;
/**
 *
 * @author ram
 */
public interface MorphSegmenterInterface extends MorphSegmenter{
    /**
     * @param dataFile
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void createModel(String dataFile)  throws FileNotFoundException, IOException ;
    public void loadModel(String modelFile)  throws FileNotFoundException, IOException;
}
