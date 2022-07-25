/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.mlearning.svm;

/**
 *
 * @author anil
 */
public class SVMFeatureNode
{

    private int featureIndex;
    private int featureValueInt;
    private double featureValueDouble;

    public SVMFeatureNode()
    {
        featureIndex = -1;
        featureValueInt = -1;
        featureValueDouble = -1.0;
    }

    public SVMFeatureNode(int featureIndex, int featureValue)
    {
        this.featureIndex = featureIndex;
        this.featureValueInt = featureValue;
        this.featureValueDouble = -1;
    }

    public SVMFeatureNode(int featureIndex, double featureValue)
    {
        this.featureIndex = featureIndex;
        this.featureValueDouble = featureValue;
        this.featureValueInt = -1;
    }

    public SVMFeatureNode(int featureIndex)
    {
        this.featureIndex = featureIndex;
        featureValueInt = -1;
        featureValueDouble = -1.0;
    }

    public int getFeatureIndex()
    {
        return featureIndex;
    }

    public void setFeatureIndex(int featureIndex)
    {
        this.featureIndex = featureIndex;
    }

    public String getFeatureValue()
    {
        String featureValue = new String();

        if (featureValueInt != -1)
        {
            featureValue = "" + featureValueInt;
        }
        if (featureValueDouble != -1)
        {
            featureValue = "" + featureValueDouble;
        }
        return featureValue;
    }

    public void setFeatureValueInt(int featureValueInt)
    {
        this.featureValueDouble = -1.0;
        this.featureValueInt = featureValueInt;
    }

    public void setFeatureValueDouble(double featureValueDouble)
    {
        this.featureValueInt = -1;
        this.featureValueDouble = featureValueDouble;
    }
}
