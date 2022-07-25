package sanchay.text.adhoc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.io.BufferedWriter;
import java.io.*;
        
@Deprecated
public class DistantWordPairMI
{
    Hashtable word_table;
    Hashtable wp_table;
    int linenum = 0;

    int distance = 8;

    public DistantWordPairMI()
    {
        super();

        word_table= new Hashtable(0,10);
        wp_table= new Hashtable(0,10);
    }

    private void init() throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        /* Word1\tWord2\tDistance is the key
        Value is an array with two elements: frequency, MI*/
        //System.out.println("Starting to prepare frequency table...");
    
        int N = 0;
        int P = 0;
        
        BufferedReader lnReader = null;
                
        //String f = "D:\\data\\Misc_utf8\\gandhi_utf8\\1.isc.utf8";
                String f = "C:\\Documents and Settings\\print\\Desktop\\sent-mix.txt";
                
        //lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                 
                
        String line;
        int linenum=0;
        String words[] = null;
                
                Vector vec = null;
                
        while((line = lnReader.readLine()) != null)
        {
            if(linenum > 0 && linenum % 10 == 0)
            {
                //System.out.println( "\rProcessed " + linenum + " sentences.");
            }
    
            linenum++;
            //chomp($line);
            
        //    $line = lc($line);
            
            words = line.split("[ \\(\\_\\-:]");
            
            for(int i = 0; i < words.length; i++)
            {
                N++;
                
                vec =(Vector)word_table.get(words[i]);
                
                
                if(vec != null)
                {
                    //Integer intFreq = (Integer) ((Vector) word_table.get(words[i])).get(0);
                    Integer intFreq = (Integer) vec.get(0);
                    vec.setElementAt(new Integer(intFreq.intValue() + 1), 0);
//                    ((Vector) word_table.get(words[i])).setElementAt(0, new Integer(intFreq.intValue() + 1));
                }
                else
                {
                                        vec = new Vector(0,10);
                    word_table.put(words[i], vec);
                                     vec.add(new Integer(1));
                                 }
                
                for(int j = i + 1; j <= i + distance && j < words.length; j++)
                {
                    P++;
                    
                    int d = j - i;
                    String key = words[i] + "\t" + words[j] + "\t" + d;
                    
                                        vec =(Vector) wp_table.get(key);
                    if(vec !=null)
                    {
                        Integer intFreq = (Integer) vec.get(0);
                        vec.setElementAt(new Integer(intFreq.intValue() + 1),0);
                    }
                    else
                    {
                                            vec = new Vector(0,10);
                                            wp_table.put(key, vec);
                                            vec.add(new Integer(1));
                    }
                }
            }
        }
    
        //System.out.println( "\nEnded preparing frequency table...\n");
    
        //System.out.println( "Calculating word probailities...\n");
                System.out.println(P);
                System.out.println(N);
        
        Enumeration enm = word_table.keys();
        
        String key = "";
    
        while(enm.hasMoreElements())
        {
            key = (String) enm.nextElement();
            vec =(Vector) word_table.get(key);
            double p = ((double) ((Integer)vec.get(0)).intValue())/((double) N);
            vec.add(new Double(p));
        }
    
        //&PrintWordProbs();
    
        //System.out.println( "Preparing mutual information table...\n");
    
        int count = 0;
        int multiply = 100;
                double mi;
                double new_mi;
        Enumeration word_pairs=wp_table.keys();
        while(word_pairs.hasMoreElements())
        {
            key = (String) word_pairs.nextElement();
            if(count > 0 && count % 50 == 0)
            {
                //System.out.println( "\rProcessed " + count + " triples.");
            }
            
            count++;
            String parts[]= null;
            parts =key.split("\t", 100);
            double p_a = (double) ((Double) ((Vector) word_table.get(parts[0])).get(1)).doubleValue();
            double p_b = (double) ((Double) ((Vector) word_table.get(parts[1])).get(1)).doubleValue();
            double p_ab = ((double) ((Integer) ((Vector) wp_table.get(key)).get(0)).intValue()) / (double) P;
            mi = Math.log(p_ab / (p_a * p_b));
                        new_mi = p_ab * mi;
                        //System.out.println(mi);
            vec =(Vector) wp_table.get(key);
                        vec.add(new Double(mi));
                        vec.add(new Double(new_mi));
                 //       vec.add(new Double(p_ab));
        }
        normalizeWordPairMI(1);    
                normalizeWordPairMI(0);    
        //System.out.println( "\nNormalizing mutual information...\n");
    
        
    }
        
        public void printWord(PrintStream ps)
        {
            ps.println("Word Frequencies: ");
            
            Enumeration enm = word_table.keys();

            while(enm.hasMoreElements())
            {
                    String key = (String) enm.nextElement();
                    ps.println(key + "\t" + ((Vector) word_table.get(key)).get(0) + "\t" + ((Vector) word_table.get(key)).get(1));
            }
    }
        public void printWordPair(PrintStream ps)
    {
            ps.println("Word Pair Frequencies: ");
            
            Enumeration enm = wp_table.keys();
            int Freq_WP=0;
            Vector vec = null;
            while(enm.hasMoreElements())
            {
                    String key = (String) enm.nextElement();
                    vec =(Vector)wp_table.get(key);
                    Freq_WP= ((int) ((Integer)vec.get(0)).intValue());
                    //if (Freq_WP > 50)
                    //{
                        ps.println(key + "\t" + ((Vector) wp_table.get(key)).get(0)+ "\t" + ((Vector) wp_table.get(key)).get(1) + "\t" + ((Vector) wp_table.get(key)).get(2) + "\t" + ((Vector) wp_table.get(key)).get(3) + "\t" + ((Vector) wp_table.get(key)).get(4));
                    //}
            }
    }

        

    public void printWordFreq(PrintStream ps)
    {
            ps.println("Word Frequencies: ");
            
            Enumeration enm = word_table.keys();

            while(enm.hasMoreElements())
            {
                    String key = (String) enm.nextElement();
                    ps.println(key + " = " + ((Vector) word_table.get(key)).get(0));
            }
    }

    public void printWordProbs(PrintStream ps)
    {
            ps.println("Word Probabilities: ");
            
            Enumeration enm = word_table.keys();

            while(enm.hasMoreElements())
            {
                    String key = (String) enm.nextElement();
                    ps.println(key + " = " + ((Vector) word_table.get(key)).get(1));
            }
    }

    public void printWordPairFreq(PrintStream ps)
    {
            ps.println("Word Pair Frequencies: ");
            
            Enumeration enm = wp_table.keys();

            while(enm.hasMoreElements())
            {
                    String key = (String) enm.nextElement();
                    ps.println(key + " = " + ((Vector) wp_table.get(key)).get(0));
            }
    }

    public void printWordPairMI(PrintStream ps)
    {
            ps.println("Word Pair MI: ");

            Enumeration enm = wp_table.keys();

            while(enm.hasMoreElements())
            {
                    String key = (String) enm.nextElement();
                    ps.println(key + " = " + ((Vector) wp_table.get(key)).get(1));
            }
    }

    public void printNormalizedWordPairMI(PrintStream ps)
    {
            ps.println("Word Pair Normalized MI: ");
            
            Enumeration enm = wp_table.keys();

            while(enm.hasMoreElements())
            {
                    String key = (String) enm.nextElement();
                    ps.println(key + " = " + ((Vector) wp_table.get(key)).get(2));
            }
    }

    public void normalizeWordPairMI(int flag)
    {
        Enumeration enm = wp_table.keys();
        int count = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double min_new = 0.0;
        
        String key;
        while(enm.hasMoreElements())
        {
            key = (String) enm.nextElement();
             if(flag==1)
                        {
                             min_new = (double) ((Double) ((Vector) wp_table.get(key)).get(1)).doubleValue();
                        }
                        else
                        {
                             min_new = (double) ((Double) ((Vector) wp_table.get(key)).get(2)).doubleValue();
                        }
            if(min_new < min)
            {
                min = min_new;            
            }
            if(min_new > max)
            {
                max = min_new ;                             
            }
        }
    
        double range = max - min;  
        System.out.println(max + " " + min + "  " + range) ;    
    //    System.out.println( "Min: ".$min."\n");
    //    System.out.println( "Max: ".$max."\n");
    //    System.out.println( "Range: ".$range."\n");
                
        enm = wp_table.keys();
                Vector vec = null;
        
        while(enm.hasMoreElements())
        {
            key = (String) enm.nextElement();
                        if(flag==1)
                        {
                             min_new = (double) ((Double) ((Vector) wp_table.get(key)).get(1)).doubleValue();
                        }
                        else
                        {
                             min_new = (double) ((Double) ((Vector) wp_table.get(key)).get(2)).doubleValue();
                        }
            if(count > 0 && count % 50 == 0)
            {
            //    System.out.println( "\rProcessed " + count + " triples.");
            }
            
            count++;
            min_new = (min_new - min)/range;
                        System.out.println(min_new) ;    
                        vec = (Vector) wp_table.get(key);
                        vec.add(new Double(min_new));
        }
        
    //    System.out.println( "\n");
    }

    public static void main(String[] args)
    {
            DistantWordPairMI dwmi = new DistantWordPairMI();
//            dwmi=null;
            
            String f1 = "C:\\Documents and Settings\\print\\Desktop\\output.txt";
            
            try
             {
                dwmi.init();
                
//        printWordFreq(System.out);
//        printWordProbs(System.out);
//        printWordPairFreq(System.out);
//        printWordPairMI(System.out);
                
                PrintStream ps = new PrintStream(f1, "UTF-8");
                //dwmi.printWord(ps);
                dwmi.printWordPair(ps);
               // dwmi.printWord(ps);
        //dwmi.printNormalizedWordPairMI(ps);
            }
            catch(FileNotFoundException ex)
            {
                ex.printStackTrace();
            }
            catch(UnsupportedEncodingException ex)
            {
                ex.printStackTrace();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
            
            //dwmi.printWordPairFreq();
//            System.out.println("dsgfwegf");
            
          
    }
        
//        private class FreqComparator<T> implements Comparator<T>
//        {
//            Hashtable hashtable;
//            int index;
//            
//            public FreqComparator(Hashtable ht, int ind)
//            {
//                hashtable = ht;
//                index = ind;
//            }
//            
//            public int compare(T o1, T o2)
//            {
//                if(index == 0)
//                {
//                    return ((Integer) o1).compare((Integer) o2);
//                }
//                
//                return -1;
//            }            
//        }                
        
}
