package sanchay.langenc;

import java.io.*;
import java.util.*;

public class MakeGraph {

    public Hashtable dist;
    
    public MakeGraph() 
    {
        super();
        dist = new Hashtable();
    }

    /**
     * @param args
     */
    public static void main(String[] args) 
    {
        // TODO Auto-generated method stub
        try
        {
            MakeGraph grph = new MakeGraph();
            BufferedReader a = null;
            a = new BufferedReader (new InputStreamReader(new FileInputStream("/home/anil/myproj/enc/surana/norm")));
            String line;
            //String [] linesplit;
            while ((line = a.readLine()) != null)
            {
                String[] linesplit = line.split("::");
                if (linesplit.length == 2)
                {
                    grph.dist.put(linesplit[0], linesplit[1]);
                }
            }
            a.close();
            int k=0;
            a = new BufferedReader (new InputStreamReader(new FileInputStream("/home/anil/myproj/enc/surana/enum")));
            while ((line = a.readLine()) != null)
            {
                String[] linesplit = line.split("::");
                String distance;
                if (linesplit.length == 3)
                {
                    if (grph.dist.get(linesplit[0]) != null)
                    {
                        distance = grph.dist.get(linesplit[0]).toString();
                        System.out.println(distance + "\t" + linesplit[2]);
                    }
                    else
                    {
                       // distance = linesplit[0];
                        k++;
                    }
                    
                }
            }
            System.out.println(k);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
    }

}
