package sanchay.langenc;

import java.io.*;
import java.util.*;

public class NormalizeDistance {

    public NormalizeDistance() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try
        {
            MakeGraph grph = new MakeGraph();
            BufferedReader a = null;
            a = new BufferedReader (new InputStreamReader(new FileInputStream("/home/anil/myproj/enc/surana/distance")));
            String line;
            //String [] linesplit;
            line = a.readLine();
            String[] split = line.split("_");
            String[] split1 = split[1].split("::");
            double max = Double.parseDouble(split1[1].toString());
            double norm ;
            String enc1 = split[0];
            while ((line = a.readLine()) != null)
            {
                split = line.split("_");
                if (enc1.equals(split[0]))
                {
                    split1 = split[1].split("::");
                    norm = Double.parseDouble(split1[1].toString());
                    norm = norm / max;
                    System.out.println(enc1 + "_" + split1[0] + "::" + norm);
                }
                else
                {
                    split1 = split[1].split("::");
                    max = Double.parseDouble(split1[1].toString());
                    enc1 = split[0];
                }
            }
            a.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
    }


