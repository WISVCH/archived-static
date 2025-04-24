/*
  Java solution of problem Eilanden
  for NKP2005
  author: P.G.Kluit
  date  : May 2005

 */

import java.io.*;
import java.util.*;

public class ProblemC1 {
    private static String infile = "c.in";

    public static void main (String [] args){
        //Stopwatch watch = new Stopwatch();
        try{
        //   for (int m = 1; m <=5; m++){
          // infile = "pgkTestEilanden" + m + ".txt";
           if (args.length > 0) // another inputfile can be given
              infile = args[0]; // as command line argument



           InputStream is = new FileInputStream(infile) ;
           IntReader ir = new IntReader(is);
           int no = ir.read();

           for (int k = 0; k < no; k++){
                //watch.start();
                Problem p = new FastProblem();
                p.read(ir);
                //System.out.println("grootte: "+  p.aantal);
                //watch.stop();
                //System.out.println("Inlezen duurde: " + watch.getDuur()/1000 + " seconden");
                // System.out.println("case: " + (k + 1));
                p.solve();
                //watch.stop();
                //System.out.println("Rekenen duurde: " + watch.getDuur()/1000 + " seconden");
                //System.out.println(p.aantal + " "+  p.aantal* (p.aantal -1) / 2);
                System.out.println(p.getSolution() + "");
           }
           //}

        }
        catch (IOException iox){
            System.err.println(iox.toString());
        }
    }
}

class Problem{

    long answer;
    int aantal;
    int [][] punten;

    public void solve(){ // quadratic algoritm
       for (int count = 0; count < aantal; count ++){
          int x = punten[count][0];
          int y = punten[count][1];
          for (int punt = 0; punt < count; punt++)
             if (punten[punt][0] >= x && punten [punt][1] >= y)
                answer++;
             else if(punten[punt][0] <= x && punten [punt][1] <= y)
                answer++;
       }
    }

    public long getSolution(){
       return answer;
    }

    public void read(IntReader ir){
       aantal = ir.read();
       punten = new int [aantal] [2];
       for (int count = 0; count < aantal; count ++){
          int x = ir.read();
          punten[count][0] = x;
          int y = ir.read();
          punten[count][1] = y;
       }
    }

    public void dump(){
       for (int k = 0; k < aantal; k++)
          System.out.println(punten[k][0] + " " + punten[k][1]);
   }
     public void dump2(){}
}


class FastProblem extends Problem{
    private Punt [] points;
    int rijen = 0;
    public void solve(){ // nlogn algoritme
       heapsort();
       collect();
       mergesort();
    }

    private void merge(int [] left, int [] right, int [] target){
       int leftindex = 0;
       int rightindex = 0;
       int targetindex = 0;
       while (leftindex < left.length && rightindex < right.length){
          if (left[leftindex] <= right[rightindex]){
             int rs = right.length - rightindex;
             answer += rs ;
             target[targetindex++] = left[leftindex++];
          }
          else
             target[targetindex++] = right[rightindex++];
       }
       while (leftindex < left.length)
           target[targetindex++] = left[leftindex++];
       while (leftindex < left.length && rightindex < right.length)
             target[targetindex++] = right[rightindex++];
    }

    private void mergesort(){
       while (rijen > 1){
          int naar = 0;
          int from = 0;
          while (from < rijen){
              int other = from + 1;
              int [] target;
              if (other < rijen){
                 target = new int [punten[from].length + punten[other].length];
                 merge(punten[from], punten[other], target);
                 punten[from] = null;
                 punten[other] = null;
              }
              else
                 target = punten[from];
              punten[naar++] = target;
              from +=2;
          }
          rijen = naar;
       }
    }

    private void collect(){
      punten = new int [aantal][];
      int begin = aantal;
      int eind ;
      while (begin > 0){
         eind = begin - 1;
         while (eind > 0  && points[eind].y > points[eind + 1].y)
            eind--;
         eind++;
         int lengte = begin - eind +1;
         answer += (long) lengte *(lengte -1) / 2;
         int [] target = new int [lengte];
         int t = 0;
         for (int k = begin; k >= eind; k--)
            target[t++] = points[k].y;
         punten[rijen++] = target;
         begin = eind - 1;
      }
      points = null;
    }

    private void sift(int k, int eind){
       //System.out.println(" sift  " + k + " " + eind);
       int l;
       if (2 * k + 1 <= eind && points[2 * k + 1].before(points[2 * k]))
         l = 2 * k + 1;
       else
         l = 2 * k;
       if (l <= eind && points[l].before(points[k])){
          swap(k, l);
          sift(l,eind);
       }
    }

    private void swap(int k, int l){
        Punt hulp = points[k];
        points[k] = points[l];
        points[l] = hulp;
    }

    private void heapsort(){
       for (int k = aantal/2 + 1 ; k >= 1; k--)
          sift(k,aantal);
       //dump();
       for (int eind = aantal; eind > 1; eind--){
          swap(1,eind);
          sift(1,eind - 1);
          //dump();
       }
    }

    public void read(IntReader ir){
       aantal = ir.read();
       points = new Punt [aantal + 1] ;
       for (int count = 1; count <= aantal; count ++){
          int x = ir.read();
          int y = ir.read();
          points[count] = new Punt (x,y);
       }
    }

    public void dump(){
       System.out.println("---");
       for (int k = 1; k <= aantal; k++)
          System.out.print(points[k] + " ");
       System.out.println();
       System.out.println("answer = : " + answer);
       System.out.println("----");
   }

   public void dump2(){
      int m = 0;
      while (m < aantal && punten[m] != null){
        for (int k = 0; k < punten[m].length; k++)
          System.out.print(punten[m][k] + " ");
        System.out.println();
        m ++;
      }
      System.out.println("answer = : " + answer);
   }
}

class Punt{
   int x, y;
   public Punt(int x, int y){
      this.x = x;
      this.y = y;
   }

   public boolean before(Punt that){
      return this.x < that.x ||
             (this.x == that.x && this.y < that.y);
   }

   public String toString(){
       return "(" + x + ", " + y + ")";
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
