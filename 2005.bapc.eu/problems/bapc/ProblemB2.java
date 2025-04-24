import java.io.*;
import java.util.ArrayList;

public class ProblemB2
{
	public static String testFile = "b.in";
	public static PushbackReader invoer = null;

	public static double[] xs;
	public static int[] ns;
	
	public static void main( String[] args ) {
		try {
			invoer = new PushbackReader( new FileReader( testFile ) );
			int aantalTestCases = readInt( );
			for( int i = 0; i < aantalTestCases; i++ )
				doTestCase( );
		}
		catch( Exception e ) {
			System.err.println( "Exception (" + e.getClass( ).getName( ) + "):\n" + e.getMessage( ) );
		}
	}

	public static int readInt( ) throws Exception {
		return Integer.parseInt( readWord( ) );
	}

	public static String readWord( ) throws Exception {
		int status = 0;
		int c = 0;
		String woord = "";
		for(;;) {
			c = invoer.read( );
			if( c == -1 ) {
				if( status == 0 )
					throw new Exception( "Einde van het bestand? Hier gaat iets verkeerd. (Gegooid in readWord)" );
				else
					return woord;
			}
			if( Character.isWhitespace( (char) c ) ) {
				if( status == 1 ) status = 2;
			}
			else switch( status ) {
				case 2 : invoer.unread( c );
					return woord;
				case 0 : status = 1;
				case 1 : woord += Character.toString( (char) c );
			}
		}
	}

	public static String readLine( ) throws Exception {
		String regel = "";
		int c = 0;
		c = invoer.read();
		while( c != '\n' ) {
			if( c == -1 )
				return regel;
			regel += Character.toString( (char) c );
			c = invoer.read( );
		}
		c = invoer.read( );
		if( c != '\r' )
			invoer.unread( c );
		return regel;
	}

	public static void doTestCase() throws Exception
	{
		// testcase input
		int houses = readInt();	
		int[] x = new int[houses];
		int[] y = new int[houses];
		int[] n = new int[houses];
		double[] start = new double[houses];
		double[] stop = new double[houses];
		for (int i = 0; i < houses; i++)
		{
			x[i] = readInt();
			y[i] = readInt();
			n[i] = readInt();
		}

		for (int i = 0; i < houses; i++)
		{
			if (y[i] < -1000 || y[i] > 1000)
				n[i] = 0;
			int dy = Math.abs(y[i]);
			start[i] = x[i] - Math.sqrt(1000000 - (dy*dy));
			stop[i] = x[i] + Math.sqrt(1000000 - (dy*dy));
		}

		double[] xs = new double[2*houses];
		int[] ns = new int[2*houses];
		for (int i = 0; i < houses; i++)
		{
			xs[i] = start[i];
			ns[i] = n[i];
			xs[houses+i] = stop[i];
			ns[houses+i] = -n[i];
		}
		for (int i = 0; i < 2*houses; i++)
		{
			for (int j = i; j < 2*houses; j++)
			{
				if (xs[i] > xs[j])
				{
					double t = xs[i];
					xs[i] = xs[j];
					xs[j] = t;
					int u = ns[i];
					ns[i] = ns[j];
					ns[j] = u;
				}
			}
		}

		int cur=0;
		int max=0;
		
		for (int i = 0; i < 2*houses; i++)
		{
			cur += ns[i];
			if (cur > max) max = cur;
		}
				
		System.out.println(max);
	}
	
}

