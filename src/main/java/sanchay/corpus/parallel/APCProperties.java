package sanchay.corpus.parallel;

import java.io.*;
import java.util.*;
import sanchay.GlobalProperties;
import sanchay.corpus.simple.SimpleCorpus;
import sanchay.matrix.SparseMatrixDouble;
import sanchay.mlearning.mt.TransliterationCandidateGenerator;
import sanchay.text.spell.PhoneticModelOfScripts;

public class APCProperties
{
	/*protected String srclang;
	protected String tgtlang;

	protected String srcfile;
	protected String tgtfile;
	protected String dictpath;*/

	protected double signature_weight;
	protected double charcnt_weight;
	protected double wrdcnt_weight;
	protected float prob_threshold;

	protected int smax_signature;
	protected int smax_charcnt;
	protected int smax_wrdcnt;
	protected int smax_wsl;
	protected int tmax_signature;
	protected int tmax_charcnt;
	protected int tmax_wrdcnt;
	protected int tmax_wsl;

	protected double mean_signature_ratio;
	protected double mean_charcnt_ratio;
	protected double mean_wrdcnt_ratio;
	protected double mean_wsl_ratio;

	protected boolean minimal;


	//protected int lex_size_cutoff;

	protected Hashtable hash;
	protected Vector sent_wt;

    protected double surfaceSimilarityThreshold = Double.MAX_VALUE;
    protected double surfaceSimilarityWordLengthRatio = 0.7;
    protected TransliterationCandidateGenerator transliterationCandidatesGenerator;
    protected PhoneticModelOfScripts phoneticModelOfScripts;

    protected SimpleCorpus srcCorpus;
    protected SimpleCorpus tgtCorpus;

    protected SparseMatrixDouble lengthScores;
    protected SparseMatrixDouble surfaceMatchCounts;

	public APCProperties()
	{
		hash = new Hashtable(50, (float)0.05);
		sent_wt = new Vector(7, 5);
	}

	public APCProperties(String f1, String f2)
	{
		hash = new Hashtable(50, (float)0.05);
		sent_wt = new Vector(7, 5);
		add("srcfile", f1);
		add("tgtfile", f2);
		/*add("srclang", "english");
		add("tgtlang", "hindi");

		add("srcfile", f1);
		add("tgtfile", f2);
		add("dictpath", "/home/samar/sanchay/java/input/dict.dat");
		add("first_before_filter_map_file", "/home/samar/sanchay/java/output/algo_mapping_fbf");
		add("first_after_filter_map_file", "/home/samar/sanchay/java/output/algo_mapping_faf");
		add("second_before_filter_map_file", "/home/samar/sanchay/java/output/algo_mapping_sbf");
		add("second_after_filter_map_file", "/home/samar/sanchay/java/output/algo_mapping_saf");


		add("first_num_match", "/home/samar/sanchay/java/output/first_num_match");
		add("second_num_match", "/home/samar/sanchay/java/output/second_num_match");

		add("first_phntc_match", "/home/samar/sanchay/java/output/first_phntc_match");
		add("second_phntc_match", "/home/samar/sanchay/java/output/second_phntc_match");

		add("first_commwrd", "/home/samar/sanchay/java/output/first_commwrd");
		add("second_commwrd", "/home/samar/sanchay/java/output/second_commwrd");

		add("first_commhyp", "/home/samar/sanchay/java/output/first_commhyp");
		add("second_commhyp", "/home/samar/sanchay/java/output/second_commhyp");

		add("first_commsyn", "/home/samar/sanchay/java/output/first_commsyn");
		add("second_commsyn", "/home/samar/sanchay/java/output/second_commsyn");

		add("second_dwcorr", "/home/samar/sanchay/java/output/second_dwcorr");


		add("default_out_dir", "/home/samar/sanchay/java/output/");
		add("eng_morph_info", "/home/samar/sanchay/java/input/morph_flat.data");
		add("hnd_morph_info", "/home/samar/sanchay/java/input/hndtokens.dat.mo");
		add("eng_tokens", "/home/samar/sanchay/java/input/engtokens");
		add("hnd_tokens", "/home/samar/sanchay/java/input/hndtokens.dat");

		add("eng_mi_cutoff", "/home/samar/sanchay/java/input/corpus/MI/word_pair_mi.eng.out");
		add("hnd_mi_cutoff", "/home/samar/sanchay/java/input/corpus/MI/word_pair_mi.hnd.out");

		add("bi_pair_file", "/home/samar/sanchay/java/output/bi_pair_file.out");
		add("eng_wt", "/home/samar/sanchay/java/output/eng_wtype.out");
		add("hnd_wt", "/home/samar/sanchay/java/output/hnd_wtype.out");*/

/*
		add("signature_weight", String.valueOf(0.1));
		add("charcnt_weight", String.valueOf(0.8));
		add("wrdcnt_weight", String.valueOf(0.1));

		add("prob_threshold", String.valueOf(0.9));
*/

		//signature_weight = 0.8;
		//charcnt_weight = 0.1;
		//wrdcnt_weight = 0.1;

		//prob_threshold = (float)0.9;

		//minimal = false;
	}

	public void add(String prop, String val)
	{
		hash.put(prop, val);
	}

	public String get(String prop)
	{
		return (String) hash.get(prop);
	}

	public boolean containsKey(String key)
	{
		if(hash.containsKey(key) == true)
			return true;
		else
			return false;
	}

	public void add_Sent_Wt(String val)
	{
		sent_wt.add(val);
	}

	public String get_Sent_Wt(int index)
	{
		return (String) sent_wt.get(index);
	}

	public int get_Sent_Wt_Count()
	{
		return (int) sent_wt.size();
	}

	public boolean getMinimal()
	{
		return minimal;
	}

	public void setMinimal(boolean m)
	{
		minimal = m;
	}

	/*public String getTgtLang()
	{
		return tgtlang;
	}

	public void setTgtLang(String l)
	{
		tgtlang = l;
	}

	public String getSrcFile()
	{
		return srcfile;
	}

	public void setSrcFile(String l)
	{
		srcfile = l;
	}

	public String getTgtFile()
	{
		return tgtfile;
	}

	public void setTgtFile(String l)
	{
		tgtfile = l;
	}

	public String getDictPath()
	{
		return dictpath;
	}

	public void setDictPath(String dp)
	{
		dictpath = dp;
	}*/

	public void readProperties(String f)throws FileNotFoundException, IOException
	{
		if(f == null)
			f = GlobalProperties.resolveRelativePath("props/sen-align-properties.txt");

		BufferedReader lnReader = new BufferedReader(
	            new InputStreamReader(new FileInputStream(f), GlobalProperties.getIntlString("UTF-8")));
		
		String line;

		while( (line = lnReader.readLine()) != null )
		{
			if(line.contains("<Sentence Weights>") == true)
			{
				String line1 = null;
				while( (line1 = lnReader.readLine()) != null )
				{
					if(line1.contains("</Sentence Weights>") == true)
					{
						line = lnReader.readLine();
						break;
					}
					add_Sent_Wt(line1);
				}
			}

			if(line.contains("##") == false && line.contains("</") == false)
			{
				String [] splitstr = line.split("\t");
				add(splitstr[0], splitstr[1]);
			}
		}

		if(containsKey("minimal") == true)
		{
			String val = get("minimal");
			if(Integer.parseInt(val) == 0)
				setMinimal(false);
			else
				setMinimal(true);
		}

        String wstr = get_Sent_Wt(0);

        String [] parts = wstr.split("\t");
        String [] weights = parts[1].split("::");
        
        setSignatureWeight(Double.parseDouble(weights[2]));
        setCharcntWeight(Double.parseDouble(weights[1]));
        setWrdcntWeight(Double.parseDouble(weights[0]));

	}

	public void setSignatureWeight(double wt)
	{
		signature_weight = wt;
	}

	public void setCharcntWeight(double wt)
	{
		charcnt_weight = wt;
	}

	public void setWrdcntWeight(double wt)
	{
		wrdcnt_weight = wt;
	}

	public double getSignatureWeight()
	{
		return signature_weight;
	}

	public double getCharcntWeight()
	{
		return charcnt_weight;
	}

	public double getWrdcntWeight()
	{
		return wrdcnt_weight;
	}

	public void setSMaxSignature(int maxsig)
	{
		//add("smax_signature", String.valueOf(maxsig));
		smax_signature = maxsig;
	}

	public int getSMaxWSL()
	{
		return smax_wsl;
	}

	public void setSMaxWSL(int maxwsl)
	{
		smax_wsl = maxwsl;
	}

	public int getTMaxWSL()
	{
		return tmax_wsl;
	}

	public void setTMaxWSL(int maxwsl)
	{
		tmax_wsl = maxwsl;
	}

	public void setSMaxCharcnt(int maxchrcnt)
	{
		//add("smax_charcnt", String.valueOf(maxchrcnt));
		smax_charcnt = maxchrcnt;
	}

	public void setSMaxWrdcnt(int maxwrdcnt)
	{
		//add("smax_wrdcnt", String.valueOf(maxwrdcnt));
		smax_wrdcnt = maxwrdcnt;
	}

	public int getSMaxSignature()
	{
		return smax_signature;
	}

	public int getSMaxCharcnt()
	{
		return smax_charcnt;
	}

	public int getSMaxWrdcnt()
	{
		return smax_wrdcnt;
	}

	public void setTMaxSignature(int maxsig)
	{
		//add("tmax_signature", String.valueOf(maxsig));
		tmax_signature = maxsig;
	}

	public void setTMaxCharcnt(int maxchrcnt)
	{
		//add("tmax_charcnt", String.valueOf(maxchrcnt));
		tmax_charcnt = maxchrcnt;
	}

	public void setTMaxWrdcnt(int maxwrdcnt)
	{
		//add("tmax_wrdcnt", String.valueOf(maxwrdcnt));;
		tmax_wrdcnt = maxwrdcnt;
	}

	public int getTMaxSignature()
	{
		return tmax_signature;
	}

	public int getTMaxCharcnt()
	{
		return tmax_charcnt;
	}

	public int getTMaxWrdcnt()
	{
		return tmax_wrdcnt;
	}

	public double getMeanSignatureRatio()
	{
		return mean_signature_ratio;
	}

	public double getMeanCharcntRatio()
	{
		return mean_charcnt_ratio;
	}

	public double getMeanWrdcntRatio()
	{
		return mean_wrdcnt_ratio;
	}

	public double getMeanWSLRatio()
	{
		return mean_wsl_ratio;
	}

	public void setMeanCharcntRatio(double m)
	{
		mean_charcnt_ratio = m;
	}

	public void setMeanWrdcntRatio(double m)
	{
		mean_wrdcnt_ratio = m;
	}

	public void setMeanSignatureRatio(double m)
	{
		mean_signature_ratio = m;
	}

	public void setMeanWSLRatio(double m)
	{
		mean_wsl_ratio = m;
	}

	public float getProbThreshold()
	{
		return Float.parseFloat(get("prob_threshold"));
	}

	public void setProbThreshold(float t)
	{
		add("prob_threshold", String.valueOf(t));
	}

	/*public int getLexSizeCutoff()
	{
		return lex_size_cutoff;
	}*/

	public void setLexSizeCutoff(int l)
	{
		add("lex_size_cutoff", String.valueOf(l));
	}

    /**
     * @return the surfaceSimilarityThreshold
     */
    public double getSurfaceSimilarityThreshold()
    {
        return surfaceSimilarityThreshold;
    }

    /**
     * @param aSurfaceSimilarityThreshold the surfaceSimilarityThreshold to set
     */
    public void setSurfaceSimilarityThreshold(double aSurfaceSimilarityThreshold)
    {
        surfaceSimilarityThreshold = aSurfaceSimilarityThreshold;
    }

    /**
     * @return the surfaceSimilarityWordLengthRatio
     */
    public double getSurfaceSimilarityWordLengthRatio()
    {
        return surfaceSimilarityWordLengthRatio;
    }

    /**
     * @param aSurfaceSimilarityWordLengthRatio the surfaceSimilarityWordLengthRatio to set
     */
    public void setSurfaceSimilarityWordLengthRatio(double aSurfaceSimilarityWordLengthRatio)
    {
        surfaceSimilarityWordLengthRatio = aSurfaceSimilarityWordLengthRatio;
    }

    /**
     * @return the transliterationCandidatesGenerator
     */
    public TransliterationCandidateGenerator getTransliterationCandidatesGenerator()
    {
        return transliterationCandidatesGenerator;
    }

    /**
     * @param aTransliterationCandidatesGenerator the transliterationCandidatesGenerator to set
     */
    public void setTransliterationCandidatesGenerator(TransliterationCandidateGenerator aTransliterationCandidatesGenerator)
    {
        transliterationCandidatesGenerator = aTransliterationCandidatesGenerator;
    }

    /**
     * @return the phoneticModelOfScripts
     */
    public PhoneticModelOfScripts getPhoneticModelOfScripts()
    {
        return phoneticModelOfScripts;
    }

    /**
     * @param aPhoneticModelOfScripts the phoneticModelOfScripts to set
     */
    public void setPhoneticModelOfScripts(PhoneticModelOfScripts aPhoneticModelOfScripts)
    {
        phoneticModelOfScripts = aPhoneticModelOfScripts;
    }

    /**
     * @return the srcCorpus
     */
    public SimpleCorpus getSrcCorpus()
    {
        return srcCorpus;
    }

    /**
     * @param srcCorpus the srcCorpus to set
     */
    public void setSrcCorpus(SimpleCorpus srcCorpus)
    {
        this.srcCorpus = srcCorpus;
    }

    /**
     * @return the tgtCorpus
     */
    public SimpleCorpus getTgtCorpus()
    {
        return tgtCorpus;
    }

    /**
     * @param tgtCorpus the tgtCorpus to set
     */
    public void setTgtCorpus(SimpleCorpus tgtCorpus)
    {
        this.tgtCorpus = tgtCorpus;
    }

    /**
     * @return the lengthScores
     */
    public SparseMatrixDouble getLengthScores()
    {
        return lengthScores;
    }

    /**
     * @param lengthScores the lengthScores to set
     */
    public void setLengthScores(SparseMatrixDouble lengthScores)
    {
        this.lengthScores = lengthScores;
    }

    /**
     * @return the surfaceMatchCounts
     */
    public SparseMatrixDouble getSurfaceMatchCounts()
    {
        return surfaceMatchCounts;
    }

    /**
     * @param surfaceMatchCounts the surfaceMatchCounts to set
     */
    public void setSurfaceMatchCounts(SparseMatrixDouble surfaceMatchCounts)
    {
        this.surfaceMatchCounts = surfaceMatchCounts;
    }

	public void print()
	{
		Enumeration enm = (Enumeration) hash.keys();

		while( enm.hasMoreElements() )
		{
			String key = (String)enm.nextElement();
			String val = (String)hash.get(key);
			System.out.println(key + "\t" + val);
		}

		System.out.println("Minimal\t" + getMinimal());
		System.out.println("prob_threshold\t" + getProbThreshold());

		Iterator itr = (Iterator) sent_wt.iterator();

		while(itr.hasNext())
		{
			String str =  (String) itr.next();
			System.out.println(str);
		}
	}
}