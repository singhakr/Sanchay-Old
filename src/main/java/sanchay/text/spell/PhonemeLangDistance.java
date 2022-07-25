package sanchay.text.spell;

import java.io.FileNotFoundException;
import java.io.IOException;

import sanchay.GlobalProperties;
import sanchay.langenc.*;

public class PhonemeLangDistance {
	
	public PhonemeLangDistance()
	{
		
	}
        
	public static void main(String[] args) {
		try {
		    int scoreType = LangEncIdentifier.MUTUAL_CROSS_ENTROPY;
		    
		    if(args.length > 0)
			scoreType = LangEncIdentifier.getScoreType(args[0]);
		    
//		    String trainPath = "data/cognate-classification/cognate-classification-hindi-train.txt";
		    String trainPath = "/home/taraka/lang-dist/training-path.txt";

		    if(args.length > 1)
			trainPath = args[1];

//		    String testPath = "data/cognate-classification/cognate-classification-hindi-test.txt";
		    String testPath = GlobalProperties.resolveRelativePath("props/enc-lang-identify-test.txt");

		    if(args.length > 2)
			testPath = args[2];
		    
		    boolean useStoredModels = false;

		    if(args.length > 3)
			useStoredModels = Boolean.parseBoolean(args[3]);
		    
		    boolean useWrdModels = false;

		    if(args.length > 4)
			useWrdModels = Boolean.parseBoolean(args[4]);
		    
		    boolean inMemory = true;

		    if(args.length > 5)
			inMemory = Boolean.parseBoolean(args[5]);
		    
		    int numAmbiguous = 5;

		    if(args.length > 6)
			numAmbiguous = Integer.parseInt(args[6]);
		    
		    int charNGrams = 5000;

		    if(args.length > 7)
			charNGrams = Integer.parseInt(args[7]);
		    
		    int wordNGrams = 500;

		    if(args.length > 8)
			wordNGrams = Integer.parseInt(args[8]);
		    
		    double wordNGramsWeight = 1.0;

		    if(args.length > 9)
			wordNGramsWeight = Integer.parseInt(args[9]);

		    int charNGramOrder = 3;

		    if(args.length > 10)
			charNGramOrder = Integer.parseInt(args[10]);
		    
		    int wordNGramOrder = 3;

		    if(args.length > 11)
			wordNGramOrder = Integer.parseInt(args[11]);
		    
		    boolean featureNGrams = true;

		    if(args.length > 12)
			featureNGrams = Boolean.parseBoolean(args[12]);
		    
		    /*
		     * Best configs (for MCE with all test data):
		     * 1. 100% for byte n-grams 1000, without word unigrams (without Norwegian)
		     * 2. 99.876 for byte n-grams 1000, without word unigrams (with Norwegian)
		     * 2. 99.876 for byte n-grams 1000, with 500 word unigrams, 5 numAmbiguous, weight 2.0) (with Norwegian)
		     *
		     *
		     *
		     */
		    
		    System.err.println(GlobalProperties.getIntlString("Options:"));
		    System.err.println("\tscoreType: " + LangEncIdentifier.getScoreTypeString(scoreType));
		    System.err.println("\ttrainPath: " + trainPath);
		    System.err.println("\ttestPath: " + testPath);
		    System.err.println("\tuseStoredModels: " + useStoredModels);
		    System.err.println("\tuseWrdModels: " + useWrdModels);
		    System.err.println("\tinMemory: " + inMemory);
		    System.err.println("\tnumAmbiguous: " + numAmbiguous);
		    System.err.println("\tcharNGrams: " + charNGrams);
		    System.err.println("\twordNGrams: " + wordNGrams);
		    System.err.println("\twordNGramsWeight: " + wordNGramsWeight);
		    System.err.println("\tcharNGramOrder: " + charNGramOrder);
		    System.err.println("\twordNGramOrder: " + wordNGramOrder);
		    System.err.println("-------------------------------------");
			
	            LangEncIdentifier idfr = new LangEncIdentifier(trainPath,
			    LangEncIdentifier.FREQ_IDENTIFIER, scoreType, charNGrams, useStoredModels,
			    useWrdModels, inMemory, numAmbiguous, wordNGrams, wordNGramsWeight,
			    charNGramOrder, wordNGramOrder,featureNGrams);

	            idfr.train();
	            idfr.calcLangDistance();
//	            for(int i = 0;i < idfr.)
//	            idfr.evaluate(testPath);
//	            idfr.evaluateLineWise(testPath);
	            ////////////////////////////////
	            
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	}
}
