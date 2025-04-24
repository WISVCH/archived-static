import java.io.*;
import java.util.ArrayList;

public class b
{
	public static String testFile = "b.in";
	public static PushbackReader invoer = null;
	public static int conn[][];
	public static int hops[];
	public static int hosts = 0;
	public static ArrayList lhosts = new ArrayList();
	public static ArrayList llevels = new ArrayList();
	
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
		hosts = readInt();	
		conn = new int[hosts][hosts];
		for (int i = 0; i < hosts; i++)
			for (int j = 0; j < hosts; j++)
				conn[i][j] = 0;
		hops = new int[hosts];
		for (int i = 0; i < hosts; i++)
				hops[i] = 0;
		
		int conns = readInt();
		for (int i = 0; i < conns; i++)
		{
				int hostA = readInt();
				int hostB = readInt();
				conn[hostA-1][hostB-1] = 1;
				conn[hostB-1][hostA-1] = 1;
		}
		int flips = readInt();
		for (int i = 0; i < flips; i++)
		{
				int hostA = readInt();
				int hostB = readInt();
				conn[hostA-1][hostB-1] = 1 - conn[hostA-1][hostB-1];
				conn[hostB-1][hostA-1] = 1 - conn[hostB-1][hostA-1];
		}		
		// start tracing at host 1 level 1
		lhosts.add(new Integer(0));
		llevels.add(new Integer(1));
		
		int lhost, llevel;
		while (lhosts.size() > 0)
		{
			lhost = ((Integer)lhosts.get(0)).intValue();
			llevel = ((Integer)llevels.get(0)).intValue();
			lhosts.remove(0);
			llevels.remove(0);
			trace(lhost, llevel);
		}
		int ans = 0;
		for (int i = 0; i < hosts; i++)
		{
				if (hops[i] > 10) ans++;
		}
		System.out.println(ans);
	}
	
	public static void trace(int host, int level)
	{
		for (int i = 0; i < hosts; i++)
		{
			if (conn[host][i] == 1 && (hops[i] == 0 || hops[i] > level))
			{
				hops[i] = level;
				lhosts.add(new Integer(i));
				llevels.add(new Integer(level+1));
			}
		}
	}
}

