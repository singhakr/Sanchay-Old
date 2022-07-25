package sanchay.speech.common;

import sanchay.corpus.parallel.APCProperties;
import sanchay.corpus.parallel.aligner.AlignmentFeature;
import sanchay.corpus.simple.SimpleSentence;
import sanchay.matrix.SparseMatrixDouble;
import sanchay.text.spell.*;

public class RecogUtils {

	public static double getDistance(Feature v1, Feature v2)
	{
		double distance = 0.0;
				
		if(v1 != null && v2 != null && ( !(v1 instanceof AlignmentFeature) && !(v2 instanceof AlignmentFeature)))
        {
            Object fdata1 = v1.getFeatures();
            Object fdata2 = v2.getFeatures();

            if(fdata1 instanceof Object[])
            {
                if(((Object[]) fdata1).length != ((Object[]) fdata2).length
                    || ((Object[]) fdata1).length == 0 || ((Object[]) fdata2).length == 0)
                return distance;
            }

            if(fdata1 instanceof Character[] && fdata2 instanceof Character[])
            {
                Character[] a1 = (Character[]) fdata1;
                Character[] a2 = (Character[]) fdata2;

                for (int i = 0; i < a1.length; i++)
                {
                if(a1[i] != a2[i])
                    distance += 1;
                }
            }
            else if(fdata1 instanceof Byte[] && fdata2 instanceof Byte[])
            {
                Byte[] a1 = (Byte[]) fdata1;
                Byte[] a2 = (Byte[]) fdata2;

                for (int i = 0; i < a1.length; i++)
                    distance += Math.abs( a1[i].byteValue() - a2[i].byteValue()) * Math.abs( a1[i].byteValue() - a2[i].byteValue());
            }
            else if(fdata1 instanceof Short[] && fdata2 instanceof Short[])
            {
                Short[] a1 = (Short[]) fdata1;
                Short[] a2 = (Short[]) fdata2;

                for (int i = 0; i < a1.length; i++)
                    distance += Math.abs( a1[i].shortValue() - a2[i].shortValue()) * Math.abs( a1[i].shortValue() - a2[i].shortValue());
            }
            else if(fdata1 instanceof Integer[] && fdata2 instanceof Integer[])
            {
                Integer[] a1 = (Integer[]) fdata1;
                Integer[] a2 = (Integer[]) fdata2;

                for (int i = 0; i < a1.length; i++)
                    distance += Math.abs( a1[i].intValue() - a2[i].intValue()) * Math.abs( a1[i].intValue() - a2[i].intValue());
            }
            else if(fdata1 instanceof Float[] && fdata2 instanceof Float[])
            {
                Float[] a1 = (Float[]) fdata1;
                Float[] a2 = (Float[]) fdata2;

                for (int i = 0; i < a1.length; i++)
                    distance += Math.abs( a1[i].floatValue() - a2[i].floatValue()) * Math.abs( a1[i].floatValue() - a2[i].floatValue());
            }
            else if(fdata1 instanceof Long[] && fdata2 instanceof Long[])
            {
                Long[] a1 = (Long[]) fdata1;
                Long[] a2 = (Long[]) fdata2;

                for (int i = 0; i < a1.length; i++)
                    distance += Math.abs( a1[i].longValue() - a2[i].longValue()) * Math.abs( a1[i].longValue() - a2[i].longValue());
            }
            else if(fdata1 instanceof Double[] && fdata2 instanceof Double[])
            {
                Double[] a1 = (Double[]) fdata1;
                Double[] a2 = (Double[]) fdata2;

                for (int i = 0; i < a1.length; i++)
                    distance += Math.abs( a1[i].doubleValue() - a2[i].doubleValue()) * Math.abs( a1[i].byteValue() - a2[i].byteValue());
            }
            else if(fdata1 instanceof PhoneticCharacter && fdata2 instanceof PhoneticCharacter)
            {
                PhoneticCharacter a1 = (PhoneticCharacter) fdata1;
                PhoneticCharacter a2 = (PhoneticCharacter) fdata2;

                distance = a1.getCharPhoneticFeatures().getDistance(a1.getCharacter(), a2.getCharacter());

    //		    a1.getLogPrintStream().println(a1.getCharacter() + "\t" + a2.getCharacter() + "\t" + sim);

                return distance;
            }
        }
        else
        {
            AlignmentFeature a1 = (v1 == null ? null : (AlignmentFeature) v1);
            AlignmentFeature a2 = (v2 == null ? null : (AlignmentFeature) v2);

            if(a1 == null && a2 == null)
                return Double.MAX_VALUE;

            SimpleSentence s1 = (a1 == null ? null : (SimpleSentence) a1.getFeatures());
            SimpleSentence s2 = (a2 == null ? null : (SimpleSentence) a2.getFeatures());

            // Normalized to a low enough value for factorial calculation
//            double slCharCount = s1.getSentenceLength() * 15.0 / a1.getAPCProperties().getSMaxCharcnt();
//            double slCharCount = s1.getSentenceLength();
//            double slWrdCount = s1.countWords();
//            double slSignature = s1.getSignature();
//            double slWSL = s1.getWeightedLength() * 15.0 / a1.getAPCProperties().getSMaxWSL();

//            double tlCharCount = s2.getSentenceLength() * 15.0 / a1.getAPCProperties().getTMaxCharcnt();
//            double tlCharCount = s2.getSentenceLength();
//            double tlWrdCount = s2.countWords();
//            double tlSignature = s2.getSignature();
//            double tlWSL = s2.getWeightedLength() * 15.0 / a1.getAPCProperties().getTMaxWSL();

//            double charCountRatio = slCharCount / tlCharCount;
//            double wrdCountRatio = slWrdCount / tlWrdCount;
//            double signatureRatio = slSignature / tlSignature;
//            double wslRatio = slWSL / tlWSL;

            double sl = (s1 == null ? 0.0 : s1.countWords());
            double tl = (s2 == null ? 0.0 : s2.countWords());
//            double sl = (s1 == null ? 0.0 : s1.getWeightedLength());
//            double tl = (s2 == null ? 0.0 : s2.getWeightedLength());

//            double r = 0.0;

            APCProperties apcProperties;

            if(a1 == null)
                apcProperties = a2.getAPCProperties();
//                r = a2.getAPCProperties().getMeanWrdcntRatio();
//                r = a2.getAPCProperties().getMeanWSLRatio();
            else
                apcProperties = a1.getAPCProperties();
//                r = a1.getAPCProperties().getMeanWrdcntRatio();
//                r = a1.getAPCProperties().getMeanWSLRatio();

//            r = apcProperties.getWrdcntWeight();
//
//            double mean = sl * r;
//            double logNumer = Math.log(mean) * tl;
//            double logDenom = (double) MathUtilFunctions.logFactorial(tl);
//
//            double pLogTgtGivenSrc = logNumer - logDenom - mean;
//
//            distance = Math.exp(pLogTgtGivenSrc);

//            System.out.println("" + (a1.getIndex() + 1) + " : "  + (a2.getIndex() + 1) + " : " + sim);
//            System.out.println("" + sl + " : "  + tl + " : " + sim);

//		    sim = a1.getCharPhoneticFeatures().getDistance(a1.getCharacter(), a2.getCharacter());

//		    a1.getLogPrintStream().println(a1.getCharacter() + "\t" + a2.getCharacter() + "\t" + sim);

            SparseMatrixDouble lengthScores = apcProperties.getLengthScores();
            SparseMatrixDouble surfaceMatchCounts = apcProperties.getSurfaceMatchCounts();

            distance = lengthScores.get((int) sl, (int) tl);

            if(distance == 0.0)
                return Double.MAX_VALUE;

//            distance = 1.0/distance;

//            double surfaceMatchCount = 0;
//
//            if(s1 != null && s2 != null)
//            {
//                int scount = s1.countWords();
//                int tcount = s2.countWords();
//
//                for (int i = 0; i < scount; i++)
//                {
//                    int srcIndex = s1.getWord(i);
//
//                    for (int j = 0; j < tcount; j++)
//                    {
//                        int tgtIndex = s2.getWord(j);
//
//                        if(surfaceMatchCounts.get(srcIndex, tgtIndex) != 0.0)
//                            surfaceMatchCount++;
//                    }
//                }
//
//                distance *= (Math.max(scount, tcount) - surfaceMatchCount) / Math.max(scount, tcount);
//            }

            return distance;
        }

		return distance;
    }

    public static double getDistance(Feature v1, Feature v21, Feature v22, boolean reverse)
	{
		double distance = 0.0;

        AlignmentFeature a1 = (AlignmentFeature) v1;
        AlignmentFeature a21 = (AlignmentFeature) v21;
        AlignmentFeature a22 = (AlignmentFeature) v22;

        SimpleSentence s1 = (SimpleSentence) a1.getFeatures();
        SimpleSentence s21 = (SimpleSentence) a21.getFeatures();
        SimpleSentence s22 = (SimpleSentence) a22.getFeatures();

//        double sl = s1.getWeightedLength();
        double sl = s1.countWords();

//        double tl = s21.getWeightedLength() + s22.getWeightedLength();
        double tl = s21.countWords() + s22.countWords();

        if(reverse)
        {
//            sl = s21.getWeightedLength() + s22.getWeightedLength();
            sl = s21.countWords() + s22.countWords();
//            tl = s1.getWeightedLength();
            tl = s1.countWords();
        }

        APCProperties apcProperties = a1.getAPCProperties();

        SparseMatrixDouble lengthScores = apcProperties.getLengthScores();
        SparseMatrixDouble surfaceMatchCounts = apcProperties.getSurfaceMatchCounts();

        distance = lengthScores.get((int) sl, (int) tl);

        if(distance == 0.0)
            return Double.MAX_VALUE;

//        double surfaceMatchCount = 0;
//
//        int scount = s1.countWords();
//        int t1count = s21.countWords();
//        int t2count = s22.countWords();
//
//        for (int i = 0; i < scount; i++)
//        {
//            int srcIndex = s1.getWord(i);
//
//            for (int j = 0; j < t1count; j++)
//            {
//                int tgtIndex = s21.getWord(j);
//
//                if(reverse)
//                {
//                    if(surfaceMatchCounts.get(tgtIndex, srcIndex) != 0.0)
//                        surfaceMatchCount++;
//                }
//                else
//                {
//                    if(surfaceMatchCounts.get(srcIndex, tgtIndex) != 0.0)
//                        surfaceMatchCount++;
//                }
//            }
//
//            for (int j = 0; j < t2count; j++)
//            {
//                int tgtIndex = s22.getWord(j);
//
//                if(reverse)
//                {
//                    if(surfaceMatchCounts.get(tgtIndex, srcIndex) != 0.0)
//                        surfaceMatchCount++;
//                }
//                else
//                {
//                    if(surfaceMatchCounts.get(srcIndex, tgtIndex) != 0.0)
//                        surfaceMatchCount++;
//                }
//            }
//        }
//
//        distance *= (Math.max(sl, tl) - surfaceMatchCount) / Math.max(sl, tl);

        return distance;
    }

    public static double getDistance(Feature v11, Feature v12, Feature v21, Feature v22)
	{
		double distance = 0.0;

        AlignmentFeature a11 = (AlignmentFeature) v11;
        AlignmentFeature a12 = (AlignmentFeature) v12;
        AlignmentFeature a21 = (AlignmentFeature) v21;
        AlignmentFeature a22 = (AlignmentFeature) v22;

        SimpleSentence s11 = (SimpleSentence) a11.getFeatures();
        SimpleSentence s12 = (SimpleSentence) a12.getFeatures();
        SimpleSentence s21 = (SimpleSentence) a21.getFeatures();
        SimpleSentence s22 = (SimpleSentence) a22.getFeatures();

//        double sl = s11.getWeightedLength() + s12.getWeightedLength();
        double sl = s11.countWords() + s12.countWords();

//        double tl = s21.getWeightedLength() + s22.getWeightedLength();
        double tl = s21.countWords() + s22.countWords();

        APCProperties apcProperties = a11.getAPCProperties();

        SparseMatrixDouble lengthScores = apcProperties.getLengthScores();
        SparseMatrixDouble surfaceMatchCounts = apcProperties.getSurfaceMatchCounts();

        distance = lengthScores.get((int) sl, (int) tl);

        if(distance == 0.0)
            return Double.MAX_VALUE;

//        double surfaceMatchCount = 0;
//
//        int s1count = s11.countWords();
//        int s2count = s12.countWords();
//        int t1count = s21.countWords();
//        int t2count = s22.countWords();
//
//        for (int i = 0; i < s1count; i++)
//        {
//            int srcIndex = s11.getWord(i);
//
//            for (int j = 0; j < t1count; j++)
//            {
//                int tgtIndex = s21.getWord(j);
//
//                if(surfaceMatchCounts.get(srcIndex, tgtIndex) != 0.0)
//                    surfaceMatchCount++;
//            }
//
//            for (int j = 0; j < t2count; j++)
//            {
//                int tgtIndex = s22.getWord(j);
//
//                if(surfaceMatchCounts.get(srcIndex, tgtIndex) != 0.0)
//                    surfaceMatchCount++;
//            }
//        }
//
//        for (int i = 0; i < s2count; i++)
//        {
//            int srcIndex = s12.getWord(i);
//
//            for (int j = 0; j < t1count; j++)
//            {
//                int tgtIndex = s21.getWord(j);
//
//                if(surfaceMatchCounts.get(srcIndex, tgtIndex) != 0.0)
//                    surfaceMatchCount++;
//            }
//
//            for (int j = 0; j < t2count; j++)
//            {
//                int tgtIndex = s22.getWord(j);
//
//                if(surfaceMatchCounts.get(srcIndex, tgtIndex) != 0.0)
//                    surfaceMatchCount++;
//            }
//        }
//
//        distance *= (Math.max(sl, tl) - surfaceMatchCount) / Math.max(sl, tl);

        return distance;
    }
}
