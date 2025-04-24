/*
  Java solution of problem Sandpiles
  for NKP2005
  author: P.G.Kluit
  date  : Oktober 2005
  11 Oktober 2005: input check added. PGK
 */

import java.io.*;
import java.util.*;

public class ProblemA{
    private static String infile = "a.in";

    public static void main (String [] args){
        //Stopwatch watch = new Stopwatch();
        try{
           if (args.length > 0) // another inputfile can be given
              infile = args[0]; // as command line argument
           Reader r = new FileReader(infile) ;
           BufferedReader br = new BufferedReader(r);
           String regel = br.readLine();
           int no = Integer.parseInt(regel);
           // watch.start();
           for (int k = 0; k < no; k++){

                Problem p = new Problem(br);
                p.solve();
                p.dump();

           }
           //watch.stop();
           //System.out.println("   " + watch.getDuur() + "  msec");
        }
        catch (IOException iox){
            System.err.println(iox.toString());
        }
    }
}

class Problem{
    int drops = 0;
    int loss = 0;
    int topplings = 0;
    CircularQueue q = new CircularQueue();

    int breedte, lengte, maxHoogte;
    int [][] hoogte;

    Problem(BufferedReader ir) throws IOException{
       String regel = ir.readLine();
       StringTokenizer str = new StringTokenizer(regel);
       String token = str.nextToken();
       lengte = Integer.parseInt(token);
         sure (1 <= lengte && lengte <= 100, "lengte: "  + lengte);
       token = str.nextToken();
       breedte = Integer.parseInt(token);
         sure (1 <= breedte && breedte <= 100, "breedte: " + breedte);
       hoogte = new int[breedte][lengte];

       token = str.nextToken();
       int grains = Integer.parseInt(token);
          sure (0 <= grains && grains <= 100, "grains: " + grains);
       token = str.nextToken();
       maxHoogte = Integer.parseInt(token);
           sure (3 <= maxHoogte && maxHoogte <= 9, "maxHoogte: " + maxHoogte);
       for (int k = 0; k < lengte; k++){
          regel = ir.readLine();
            sure (regel.length() == breedte, "regellengte " + regel);
          for (int n = 0; n < breedte; n++){
            hoogte[n][k] = regel.charAt(n) - '0';
               sure (hoogte[n][k] >= 0 && hoogte[n][k] <= 9, "hoogte: " + regel.charAt(n));
          }
       }
       for (int n = 0; n < grains; n++){
          regel = ir.readLine();
          str = new StringTokenizer(regel);
          token = str.nextToken();
          int y = Integer.parseInt(token);
             sure (1 <= y && y <= lengte, "y : " + y);
          token = str.nextToken();
          int x = Integer.parseInt(token);
               sure (1 <= x && x <= breedte, "x : " + x);
          drop(x-1,y-1);
       }
    }


    private void sure (boolean b, String mess){
       if (!b){
           System.out.println("something wrong in input: " + mess);
           System.exit(100);
       }
    }


    public void dump(){
      for (int k =0; k < lengte; k++){
         StringBuffer regel = new StringBuffer();
         for (int n = 0; n < breedte; n++)
            regel.append(hoogte[n][k]);
         System.out.println(regel);
      }
      //System.out.println(" aantal drops: " + drops + " aantal kwijt: " + loss + " topplings : " + topplings +  " langste queue : " + q.maxcount);
      //System.out.println();
    }

    public void solve(){
        while (!q.isEmpty()){
          int [] where = (int []) q.dequeue();
          int x = where[0];
          int y = where[1];
          while(hoogte[x][y] > maxHoogte){
             hoogte[x][y] -= 4;
             drop(x, y+1);
             drop(x+1, y);
             drop(x, y-1);
             drop(x-1, y);
             topplings++;
          }
        }
    }

    void drop (int x, int y){
       drops++;
       if (x >= 0 && x < breedte && y >= 0 && y < lengte){
         hoogte[x][y]++;
         if (hoogte[x][y] == maxHoogte + 1)
           q.enqueue(new int [] {x,y});
       }
       else loss++;
    }
}

class CircularQueue {
   private ListElement last;

   private int count;
   int maxcount;         // voor de statistiek

   public void enqueue(int[] item){
      if (last != null){
         last.next = new ListElement(item, last.next);
         last = last.next;
      }
      else{
         last = new ListElement(item, null);
         last.next = last;
      }
      count++;
      maxcount = Math.max(count, maxcount);
  }

   public int[] dequeue(){
      int [] antwoord = last.next.value;
      if (last.next == last)
         last = null;
      else
         last.next = last.next.next;
      count--;
      return antwoord;
   }

   public boolean isEmpty(){
      return last == null;
   }

   class ListElement{
     int [] value;
     ListElement next;

     public ListElement(int [] ob, ListElement next){
       value = ob;
       this.next = next;
     }
   }
}
