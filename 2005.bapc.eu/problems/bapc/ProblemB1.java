/*
  Java solution of problem Range
  for ICPC2005
  author: P.G.Kluit
  date  : August 2004
  11 Oktober 2005: input check added. PGK
 */

import java.io.*;
import java.util.*;

public class ProblemB1{
    private static String infile = "b.in";

    public static void main (String [] args){
        try{
           if (args.length > 0) // another inputfile can be given
              infile = args[0]; // as command line argument
           InputStream is = new FileInputStream(infile) ;
           IntReader ir = new IntReader(is);
           int no = ir.read();
           for (int k = 0; k < no; k++){
                Problem p = Problem.read(ir);
                p.solve();
                System.out.println(p.getSolution() + "");

           }
        }
        catch (IOException iox){
            System.err.println(iox.toString());
        }
    }
}

class Problem{

    private int answer;
    private Vector intersections;

    public void solve(){

       int champ = 0;
       int present = 0;
       for (Enumeration sects = intersections.elements(); sects.hasMoreElements();){
          Intersection inter = (Intersection) sects.nextElement();
          present += inter.getDiff();
          if (champ < present)
             champ = present;
       }
       answer = champ;
    }

    public int getSolution(){
       return answer;
    }

    static Problem read(IntReader ir){
        int houseNo = ir.read();
           sure (0 < houseNo && houseNo <= 1000, "houseNo: " + houseNo);
        Problem problem = new Problem();
        problem.intersections = new Vector(2 * houseNo);
        for (int k = 0; k < houseNo; k++){
           House house = House.read(ir);
           problem.insert(house.getEnter());
           problem.insert(house.getExit());
        }
        return problem;
    }
    
    static void sure (boolean b, String mess){
       if (!b){
           System.out.println("something wrong in input: " + mess);
           System.exit(100);
       }
    }

    private void insert(Intersection i){
       if (i != null){
          int index = 0;
          while (index < intersections.size() &&
                  ((Intersection) intersections.elementAt(index)).before(i))
            index++;
          intersections.add(index,i);
       }
    }
}

class House{
   static final int RANGE = 1000;
   private int x, y, inhabitants;

   public static House read(IntReader ir){
      House house = new House();
      house.x = ir.read();
          Problem.sure (-10000 < house.x && house.x <= 10000, "house.x: " + house.x);
      house.y = ir.read();
          Problem.sure (-10000 < house.y && house.y <= 10000, "house.y: " + house.y);
      house.inhabitants = ir.read();
         Problem.sure (0 <= house.inhabitants && house.inhabitants <= 100, "house.inhabitants: " + house.inhabitants);
      return house;
   }

   public Intersection getEnter(){
      if (y > RANGE || y < - RANGE || inhabitants == 0)
         return null;
      double sect = x - Math. sqrt(RANGE * RANGE - y * y);
      return new Intersection (sect, inhabitants);
   }


   public Intersection getExit(){
      if (y > RANGE || y < - RANGE || inhabitants == 0)
         return null;
      double sect = x + Math. sqrt(RANGE * RANGE - y * y);
      return new Intersection (sect, -inhabitants);
   }
}

class Intersection{
   private double x;
   private int diff;

   public Intersection(double x, int diff){
      this.x = x;
      this.diff = diff;
   }

   public int getDiff(){
      return diff;
   }

   public double getX(){
     return x;
   }

   public boolean before (Intersection other){

      if (Math.abs(this.x - other.x) < 1.0E-6 && this.diff * other.diff < 0){ // this should not occur!
         //System.out.println(".......... " + this);
         //System.out.println(".......... " + other);
         return diff > 0;
      }

      return this.x < other.x;
   }
   
   public String toString(){
      return "intersection : " + x + " " + diff;
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
