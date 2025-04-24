/*
  Java solution of problem Shepherds
  for NKP2005
  author: P.G.Kluit
  date  : July 2005
  11 Oktober 2005: input check added. PGK
 */

import java.io.*;

public class ProblemF {
    private static String infile = "f.in";

    public static void main (String [] args){

        try{
           if (args.length > 0) // another inputfile can be given
              infile = args[0]; // as command line argument

           InputStream is = new FileInputStream(infile) ;
           IntReader ir = new IntReader(is);
           int no = ir.read();
           for (int k = 0; k < no; k++){
                int sheep = ir.read();
                int bridges = ir.read();
                ShepherdProblem p = new ShepherdProblem(sheep, bridges);
                p.solve();
                System.out.println(p.getSolution() + "");
           }
        }
        catch (IOException iox){
            System.err.println(iox.toString());
        }
    }

}

class ShepherdProblem{
    int sheep, bridges, answer;

    void sure (boolean b, String mess){
       if (!b){
           System.out.println("something wrong in input: " + mess);
           System.exit(100);
       }
    }

    public ShepherdProblem(int s, int b){
          sure(0 < s && s <= 1000000, "bridges: " + s);
           sure(0 <= b && b <= 1000, "bridges: " + b);
       sheep = s;
       bridges = b;
    }

    public void solve(){
       int target = sheep;
       int source = target;
       for (int k = 0; k < bridges; k++){
          int toll = toll(source);
          while (source - toll < target){
              source++;
              toll = toll(source);
          }
          if (toll > 2) System.out.println(target + " " + source + " " + toll);
          target = source;
       }
       answer = source;
    }

    public int getSolution(){
       return answer;
    }

    static int toll(int n){
       if (n < 3)
           return 0;
       if (n % 3 == 0)
          return n/3;
       if (n % 4 == 0)
          return n / 4;
       int m = n;
       if (m % 2 == 0)
          m = m / 2;
       int d = 5;
       while (m % d > 0)
         if (d * d > m)
            d = m;
         else
            d += 2;
       return n / d;
    }

}

class IntReader extends StreamTokenizer{
 	public IntReader(InputStream is){
	 	super(new BufferedReader(new InputStreamReader(is)));
	}

	public int read(){
		try{
			int tokentype = nextToken();
		}
		catch(IOException iox){
			System.out.println(iox);
		}
		return (int) nval;
	}
}
