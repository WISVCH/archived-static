/*
  Java solution for Tafeltjes3
  for NKP2005
  author: P.G.Kluit
  date  : May 2005
 */

import java.io.*;
import java.util.*;

public class ProblemH {
    private static String infile = "h.in";

    public static void main (String [] args){
        //Stopwatch watch = new Stopwatch();
        try{
           if (args.length > 0) // another inputfile can be given
              infile = args[0]; // as command line argument

           FileReader fr = new FileReader(infile);
           BufferedReader br = new BufferedReader(fr);
           String caseString = br.readLine();
           int cases = Integer.parseInt(caseString);
           for (int geval = 1; geval <= cases; geval++){
                String edgeString = br.readLine();
                int edges = Integer.parseInt(edgeString);
                String contour = br.readLine();
                TafeltjesProblem p = new TafeltjesProblem(edges, contour.trim());
               // watch.start();
                p.solve();
                //watch.stop();
                System.out.println(p.getSolution() + "");
                //System.out.println("  duur: " + watch.getDuur()/1000 + " seconden");
           }
        }
        catch (IOException iox){
            System.err.println(iox.toString());
        }
    }
}

class TafeltjesProblem {
    private static char NUL = '0';
    private Contour contour = new Contour();
    int telling;

    public TafeltjesProblem(int edges, String s){
        int count = 0;
        int index = 0;

        while (count < edges){
           while (index < s.length() && s.charAt(index) == ' ')
             index++; // skip spaces
           int x = s.charAt(index) - NUL;
           index++;
           int y = s.charAt(index) - NUL;
           index++;
           int z = s.charAt(index) - NUL;
           index++;

           Triple hoek = new Triple(x,y,z);
           contour.add(hoek);
           //System.out.println(hoek);
           count++;
        }
        contour.close();
        //System.out.println(contour);
    }

    public void solve(){
       boolean  [] triangles = contour.solve();
       Vorm vorm = new Vorm(triangles);
       telling = vorm.count();
    }

    public String getSolution(){
      return "" + telling;
   }
}

class Triple{
   int x,y,z;
   public Triple(int x, int y, int z){
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public int getCode(){
     int base = (x + y)/3;
     int antwoord = base * base + 2 * (x + 1) / 3;
     //System.out.print( " -" + antwoord + "- ");
     return antwoord;
   }

   public Triple plus(Triple other){
      return new Triple(this.x + other.x,
                        this.y + other.y,
                        this.z + other.z);
   }

   public Triple minus(Triple other){
      return new Triple(this.x - other.x,
                        this.y - other.y,
                        this.z - other.z);
   }

   public Triple rotate(){
      return new Triple(z,x,y);
   }

   public boolean equals (Object o){
       Triple other = (Triple) o;
       return   this.x == other.x
             && this.y == other.y
             && this.z == other.z;
   }

   public boolean lessThan(Triple that){
     return this.x < that.x ||
            (this.x == that.x && (this.y < that.y ||
                                  (this.y == that.y && this.z < that.z)));
   }

   public String toString(){
     return "(" + x + ", " + y + ", " + z + ")";
   }

    public void reduce(){
       int d = 2;
       while ( d <= Math.max(x,Math.max(y,z))){
          while (x % d == 0 && y % d == 0 && z % d == 0){
            x = x / d;
            y = y / d;
            z = z / d;
          }
          d++;
       }
    }
}

class Edge{
    Triple start, end;

    public Edge(Triple s, Triple e){
      start = s;
      end = e;
    }

    public Triple direction(){
       return end.minus(start);
    }

    public String toString(){
       return "[" + start.toString() + " - " + end.toString() + "]";
    }

    public boolean isInvers(Edge that){
       return this.start.equals(that.end) &&
              that.start.equals(this.end);
    }
}

class Contour{
    Vector edges = new Vector();
    Triple lastTriple;
    //private int [] triangles = new int [100];
    //int trianglenum = 0;
    boolean [] driehoek = new boolean [82];

    public void add (Triple hoek){
      if (lastTriple == null)
         lastTriple = hoek;
      else{
        Triple step = hoek.minus(lastTriple);
        step.reduce();
        Triple next = lastTriple;

        do{
           next = next.plus(step);
           edges.add( new Edge(lastTriple, next));
           lastTriple = next;
        } while (! next.equals(hoek));
      }
    }

    public boolean [] solve(){
       while (!edges.isEmpty()){
         Edge lastEdge = (Edge)edges.lastElement();
         Triple dir = lastEdge.direction();
         Triple newDir = dir.rotate();
         Triple eerste = lastEdge.start;
         Triple tweede = lastEdge.end;
         Triple derde = tweede.plus(newDir);

         Triple drieh = eerste.plus(tweede.plus(derde));
         //System.out.println(drieh);
         int code = drieh.getCode();
         driehoek[code] = true;
         edges.remove (lastEdge);
         addEdge(new Edge (eerste, derde));
         addEdge(new Edge (derde, tweede));
       }
       return driehoek;

    }

    public void close(){
        Triple firstTriple = getEdge(0).start;
        add (firstTriple);
    }

    public String toString(){
       String antwoord = "";
       for (int k = 0; k < edges.size(); k++)
         antwoord += getEdge(k).toString() + "\n";
       return antwoord;
    }

    private void insert(int t){
       driehoek[t] = true;
    }

    private Edge getEdge(int k){
       return (Edge) edges.elementAt(k);
    }

   private void addEdge(Edge e){
      int k = 0;
      while (k < edges.size() &&! getEdge(k).isInvers(e))
        k++;
      if (k < edges.size())
         edges.remove(k);
      else
         edges.add(e);
   }
}

class PreVorm{
  static boolean incident [] [] = new boolean [82][250];
  static {
     for (int d = 1; d <= 81; d++){
         int onder, boven, derde;
         onder = onderbuur(d);
         boven = bovenbuur(d);
         derde = derdebuur(d);
         int tafeltje;

         tafeltje = 3 * d;
         if (onder > 0 && boven > 0){
            incident[d][tafeltje] = true;
            incident[onder][tafeltje] = true;
            incident[boven][tafeltje] = true;
         }

         tafeltje++;
         if (derde > 0 && boven > 0){
            incident[d][tafeltje] = true;
            incident[derde][tafeltje] = true;
            incident[boven][tafeltje] = true;
         }

         tafeltje ++;
         if (onder > 0 && derde > 0){
            incident[d][tafeltje] = true;
            incident[onder][tafeltje] = true;
            incident[derde][tafeltje] = true;
         }
     }
     //dump();
  }

  static int onderbuur(int d){
     if (isSquare(d -1))
        return -1;
      else
        return d - 1;
  }

  static int bovenbuur(int d){
     if (isSquare(d))
        return -1;
     else
        return d + 1;
  }

  static int derdebuur(int d){
     int s = superSquare (d);
     int derde;
     if ( (d - s ) % 2 == 0)
        derde = d + 2 * s;
     else
        derde = d - 2 * ( s-1);
     if (derde <= 81)
       return derde;
     else
       return -1;
  }

  static boolean isSquare(int d){
     int s = superSquare (d);
     return d == s * s;
  }

  static int superSquare(int d){
     int s = (int)Math.sqrt(d-1);
     while (s * s < d) s++;
     return s;
  }

  static void dump(){
     for (int k = 1; k <= 81; k++){
        System.out.print ( k + " ");
        int c = 0;
        for (int t = 3; t <= 245; t++)
          if (incident[k][t])   {
              System.out.print("+");
              c++;
           }
          else
              System.out.print(" ");
        System.out.println( " " + c);
     }
  }

}

class Vorm extends PreVorm{

  private boolean [] driehoek;
  private boolean [] tafeltje;
  private int size = 0;

  public Vorm (boolean [] dh){
     driehoek = dh;
     for (int d = 1; d <= 81; d++)
        if (driehoek[d]) size++;
     tafeltje = new boolean [243];
     for (int t = 0; t < 243; t++)
         tafeltje[t] = true;
  }

  public Vorm (boolean [] dh, boolean [] tf){
     driehoek = dh;
     tafeltje = tf;
     for (int d = 1; d <= 81; d++)
        if (driehoek[d]) size++;
  }

  public int count(){
     if (size % 3 > 0)
       return 0;
     if (size == 0)
        return 1;
     for (int tafel = 0; tafel < 243; tafel++)
       if (tafeltje[tafel]){
         int d = 1;
         while (d <= 81 && (driehoek [d] || !incident [d][tafel]))
           d++;
         if (d <= 81)
           tafeltje [tafel] = false;
     }

     int champval = 10;
     int champ = -1;
     for (int d = 1; d <= 81; d++)
       if (driehoek[d]){
         int telling = 0;
         for (int t = 0; t < 243; t++)
           if (incident[d][t] && tafeltje [t])
              telling++;
         if (driehoek[d] && telling < champval){
             champ = d;
             champval = telling;
       }
     }
    // champ is de driehoek die we gaan bedekken
    // want die heeft de minste keuzemogelijkheden.
     int antwoord = 0;
     for (int t = 0; t < 243; t++)
       if ( incident[champ][t] && tafeltje[t]){
          boolean [] kopie = (boolean []) driehoek.clone();
          // driehoeken schrappen
          for (int d = 1; d <= 81; d++)
             if (incident[d][t])
                kopie[d] = false;
          Vorm vorm = new Vorm (kopie, (boolean []) tafeltje.clone());
          antwoord += vorm.count();
       }
     return antwoord;
  }

  private void consistent(){
      for (int t = 0; t < 243; t++)
         if (tafeltje[t]){
              int tel = 0;
              for (int d = 1; d <= 81; d++)
                if (incident[d][t] && driehoek[d])
                   tel++;
              if (tel % 3 > 0)
                System.out.println("inconsistent " + t + " " + tel);
         }
  }

  public String toString(){
      String antwoord = "";
      for (int k = 1; k <= 81; k++)
         if (driehoek[k])
            antwoord += "1";
         else
            antwoord += "-";
      return antwoord+ "      size = " + size ;
  }
}


