/*
  Java solution of problem Skiliften
  for NKP2005
  author: P.G.Kluit
  date  : May 2005

 */

import java.io.*;
import java.util.*;

public class ProblemG {
    private static String infile = "g.in";

    public static void main (String [] args){
        //Stopwatch watch = new Stopwatch();
        try{
           if (args.length > 0) // another inputfile can be given
              infile = args[0]; // as command line argument
           InputStream is = new FileInputStream(infile) ;
           IntReader ir = new IntReader(is);
           int no = ir.read(); //System.out.println("cases:" + no);

           for (int k = 0; k < no; k++){
                //watch.start();
                Problem p = Problem.read(ir);
                //Problem p = ProblemQueued.read(ir);
                //Problem p = ProblemListQueued.read(ir);
                //Problem p = ProblemVectorQueued.read(ir);
                p.solve();
                //watch.stop();
                System.out.println(p.getSolution() + "");
                //System.out.println("  duur: " + watch.getDuur() + " ms");
                //p.report();
           }
        }
        catch (IOException iox){
            System.err.println(iox.toString());
        }
    }
}

class Problem{

     public static final byte
      ANYTHING  = 7,
      NOBOTTOM  = 6,
      TOP       = 4,
      NOTOP     = 3,
      MIDDLE    = 2,
      BOTTOM    = 1,
      NOTHING   = 0;

    int answer;
    int breedte, lengte;
    int [][] hoogte;
    byte [][]soort;

    public void solve(){
       for (int x = 0; x < breedte; x++)
         for (int y = 0; y < lengte; y++){
           int minor=0, equal=0, major=0;
           if (x > 0)
             if (hoogte[x-1][y] < hoogte[x][y])
                minor++;
             else if (hoogte[x-1][y] == hoogte[x][y])
                equal++;
             else if (hoogte[x-1][y] > hoogte[x][y])
                major++;
           if (x < breedte - 1)
             if (hoogte[x+1][y] < hoogte[x][y])
                minor++;
             else if (hoogte[x+1][y] == hoogte[x][y])
                equal++;
             else if (hoogte[x+1][y] > hoogte[x][y])
                major++;
           if (y > 0)
             if (hoogte[x][y-1] < hoogte[x][y])
                minor++;
             else if (hoogte[x][y-1] == hoogte[x][y])
                equal++;
             else if (hoogte[x][y-1] > hoogte[x][y])
                major++;
           if (y < lengte - 1)
             if (hoogte[x][y+1] < hoogte[x][y])
                minor++;
             else if (hoogte[x][y+1] == hoogte[x][y])
                equal++;
             else if (hoogte[x][y+1] > hoogte[x][y])
                major++;

           if (equal == 0)
              if (major > 0 && minor > 0)
                 set(x,y, MIDDLE);
              else if (major > 0)
                 set(x,y,  BOTTOM);
              else if (minor > 0)
                 set(x,y,  TOP);
              else // singleton!
                 set (x,y, MIDDLE);
           else
             if (major > 0 && minor > 0)
                 update(x,y,  MIDDLE);
             else if (major > 0)
                update(x,y,  NOTOP);
             else if (minor > 0)
                 update(x,y,  NOBOTTOM);

       }
       //report();
       int toppen = 0;
       int dalen = 0;
       for (int x = 0; x < breedte; x++)
           for (int y = 0; y < lengte; y++){
             if (soort[x][y] == TOP || soort[x][y] == NOBOTTOM){
                 toppen++;
                 update(x,y, NOTHING);
             }
             if (soort[x][y] == BOTTOM|| soort[x][y] == NOTOP){
                 dalen++;
                 update(x,y, NOTHING);
             }
           }
        //report();
        answer = Math.max(toppen, dalen);
        //System.out.println ("toppen : " + toppen + "   " + "dalen : " + dalen);
    }
      boolean set(int x, int y, byte s){
          byte oldsoort = soort[x][y];
          byte newsoort = (byte) (oldsoort & s);
          if (newsoort == oldsoort)
            return false;
          soort[x][y] = newsoort;
          return true;
      }

      private void report(){
         for (int x = 0; x < breedte; x++){
           for (int y = 0; y < lengte; y++)
             System.out.print(soort[x][y]  + " ");
           System.out.println();
         }
         System.out.println("-----------");

     }

       void update(int x, int y, byte s){
        //   if (soort[x][y] > s){
        //      soort[x][y] = s;
        if (set(x,y,s)){
              if (x > 0 && hoogte[x-1][y] == hoogte[x][y])
                    update (x-1,y, s);
              if (x < breedte - 1 && hoogte[x+1][y] == hoogte[x][y])
                    update (x+1,y, s);
              if (y > 0 && hoogte[x][y-1] == hoogte[x][y])
                    update (x,y-1, s);
              if (y < lengte - 1 && hoogte[x][y+1] == hoogte[x][y])
                    update (x,y+1, s);
           }
       }


    public int getSolution(){
       return answer;
    }

    void readMe(IntReader ir){
       breedte = ir.read();
       lengte  = ir.read();
       hoogte = new int [breedte][lengte];
       soort = new byte [breedte][lengte];
       for (int x = 0; x < breedte; x++)
         for (int y = 0; y < lengte; y++){
           hoogte[x][y] = ir.read();
           soort[x][y] = ANYTHING;
         }
    }

    static Problem read(IntReader ir){
       Problem p = new Problem();
       p.readMe(ir);
       return p;
    }
}

class ProblemQueued extends Problem{
   CircularQueue q = new CircularQueue();  

      private void enqueue(int x, int y, byte s){
         int [] parm = new int [3];
         parm[0] = x; parm[1] = y; parm[2] = s;
         q.enqueue(parm);
      }

      void update(int x, int y, byte s){
         //System.out.println("-");
         enqueue(x,y,s);
         while (! q.isEmpty()){
            int [] parm = q.dequeue();
            x = parm[0];
            y = parm[1];
            s = (byte) parm[2];
            //if (soort[x][y] > s){
            //  soort[x][y] = s;
              //   if (soort[x][y] > s){
        //      soort[x][y] = s;
        if (set(x,y,s)){
              if (x > 0 && hoogte[x-1][y] == hoogte[x][y])
                    enqueue (x-1,y, s);
              if (x < breedte - 1 && hoogte[x+1][y] == hoogte[x][y])
                    enqueue (x+1,y, s);
              if (y > 0 && hoogte[x][y-1] == hoogte[x][y])
                    enqueue (x,y-1, s);
              if (y < lengte - 1 && hoogte[x][y+1] == hoogte[x][y])
                    enqueue (x,y+1, s);
           }
       }
      }

     static Problem read(IntReader ir){
       Problem p = new ProblemQueued();
       p.readMe(ir);
       return p;
    }
}
class ProblemListQueued extends ProblemQueued{
   ListQueue q = new ListQueue();

     static Problem read(IntReader ir){
       Problem p = new ProblemListQueued();
       p.readMe(ir);
       return p;
    }
}

class ProblemVectorQueued extends ProblemQueued{
   ListQueue q = new ListQueue();

     static Problem read(IntReader ir){
       Problem p = new ProblemVectorQueued();
       p.readMe(ir);
       return p;
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

class CircularQueue {
   private ListElement last;

   public void enqueue(int[] item){
      if (last != null){
         last.next = new ListElement(item, last.next);
         last = last.next;
      }
      else{
         last = new ListElement(item, null);
         last.next = last;
      }
  }

   public int[] dequeue(){
      int [] antwoord = last.next.value;
      if (last.next == last)
         last = null;
      else
         last.next = last.next.next;
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

class ListQueue {
   private LinkedList list = new LinkedList();

   public void enqueue(int[] item){
      list.addLast(item);
   }

   public int[] dequeue(){
      int [] antwoord = (int [])list.getFirst();
      list.removeFirst();
      return antwoord;
   }

   public boolean isEmpty(){
      return list.isEmpty();
   }

}

class VectorQueue {
   private Vector vector = new Vector();

   public void enqueue(int[] item){
      vector.add(item);
   }

   public int[] dequeue(){
      int [] antwoord = (int [])vector.firstElement();
      vector.remove(0);
      return antwoord;
   }

   public boolean isEmpty(){
      return vector.isEmpty();
   }

}
