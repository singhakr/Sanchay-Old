package sanchay.speech.common;

import sanchay.GlobalProperties;

public class Feature {
	private Object features;

	// The factors by which the features will be divided
	private static double normfactors[]; // must be the same size as features

	public Feature()
	{
		features = null;
		normfactors = null;
	}

	public Object getFeatures()
	{
		return features;
	}

	public void setFeatures(Object fs)
	{
		features = fs;
	}

	public static void setNormalizationFactors(double[] nf)
	{
		normfactors = nf;
	}

	public void normalize()
	{
//		if(features.length != normfactors.length)
//			return;
//
//		for(int i = 0; i < features.length; i++)
//		{
//			if(normfactors[i] == 0)
//				normfactors[i] = 1;
//
//			//features[i] = features[i]/normfactors[i];
//		}
	}

	public String toString()
	{
		String str = "";

//		for(int i = 0; i < features.length; i++)
//		{
//			if(i == features.length - 1)
//				str = str + features[i].toString();
//			else
//				str = str + features[i].toString() + " ";
//		}

		return str;
	}
}
