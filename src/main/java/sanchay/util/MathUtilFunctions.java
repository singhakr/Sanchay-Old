/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.util;

/**
 *
 * @author anil
 */
public class MathUtilFunctions {

    public static long factorial(long n)
    {
        long f = 1;

        for (long i = n; i > 1; i--)
        {
            f *= i;
        }

        return f;
    }

    public static double logFactorial(double n)
    {
        double f = 0;

        for (double i = n; i > 1; i--)
        {
            f += Math.log(i);
        }

        return f;
    }

    public static double log10Factorial(double n)
    {
        double f = 0;

        for (double i = n; i > 1; i--)
        {
            f += Math.log10(i);
        }

        return f;
    }

    public static double logProb(double prob)
    {
        double logProb = 0.0;

		if ( ( prob < 0.0D )  || ( prob > 1.0 ) )
		{
            return logProb;
		}

		if ( prob == 0.0D )
		{
			prob = Double.NEGATIVE_INFINITY;
		}
		else if ( prob == 1.0D )
		{
			logProb	= 0.0D;
        }
		else
		{
			logProb	= Math.log( prob );
		}

        return logProb;
    }

    public static double log10Prob(double prob)
    {
        double logProb = 0.0;

		if ( ( prob < 0.0D )  || ( prob > 1.0 ) )
		{
            return logProb;
		}

		if ( prob == 0.0D )
		{
			prob = Double.NEGATIVE_INFINITY;
		}
		else if ( prob == 1.0D )
		{
			logProb	= 0.0D;
        }
		else
		{
			logProb	= Math.log10( prob );
		}

        return logProb;
    }

	public static double log1p( double x )
	{
		double result;
		double u;
								//	Use log(), corrected to first order
								//	for truncation loss.
		u	= 1.0D + x;

		if ( u == 1.0D )
		{
			result	= x;
		}
		else
		{
			result	= ( Math.log( u ) * ( x / ( u - 1.0D ) ) );
		}

		return result;
	}

	public static double log1p10( double x )
	{
		double result;
		double u;
								//	Use log(), corrected to first order
								//	for truncation loss.
		u	= 1.0D + x;

		if ( u == 1.0D )
		{
			result	= x;
		}
		else
		{
			result	= ( Math.log10( u ) * ( x / ( u - 1.0D ) ) );
		}

		return result;
	}

	public static double addLogs( double log1 , double log2 )
	{
		return Math.max( log1 , log2 ) +
			log1p( Math.exp( -Math.abs( log1 - log2 ) ) );
	}

	public static double addLogs10( double log1 , double log2 )
	{
		return Math.max( log1 , log2 ) +
			log1p10( Math.pow(10.0, -Math.abs( log1 - log2 ) ) );
	}

	public static double subtractLogs( double log1 , double log2 )
	{
		return log1 + log1p( -Math.exp( log2 - log1 ) );
	}

	public static double subtractLogs10( double log1 , double log2 )
	{
		return log1 + log1p10( -Math.pow(10.0, log2 - log1 ) );
	}

    public static void main(String args[])
    {
        double val = Math.exp(MathUtilFunctions.addLogs(Math.log(0), Math.log(3)));

        System.out.println(val);

        val = Math.log(0.3);

        System.out.println(val);

        val = Math.log(-0.2);

        System.out.println(val);
    }
}
