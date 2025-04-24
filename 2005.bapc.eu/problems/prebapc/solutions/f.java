import java.io.*;
import java.util.ArrayList;

public class f {
	public static String testFile = "f.in";
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
			e.printStackTrace();
		}
	}
	
	public static int readInt( ) throws Exception {
		return Integer.parseInt( readWord( ) );
	}
	
	public static double readDouble( ) throws Exception {
		return Double.parseDouble( readWord( ) );
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
	
	public static int N;
	public static int[] x1;
	public static int[] x2;
	public static int[] y1;
	public static int[] y2;
	public static boolean[][] buur;
	public static int[] vakKleur;
	public static int mogelijk;
	public static int AANTAL_KLEUREN = 4;
	
	/* Kleur vak in met een mogelijke kleur en zoek evt. 
	 * verder naar volgend vak dat te kleuren is.
	 */
	public static void paint(int vak) {
		// Probeer dit vak in alle mogelijke kleuren te kleuren
		for (int i = 0; i < AANTAL_KLEUREN; i++) {
			vakKleur[vak] = i; // Kleur vak in
			
			// Zoek of er conflicten zijn omdat buren dezelfde kleur hebben
			boolean conflict = false;
			if (i != 0) { // Wit telt niet als kleur, dus kan geen conflicten hebben
				for (int j = 0; j < vak && !conflict; j++) {
					if (buur[vak][j] && vakKleur[vak] == vakKleur[j]) {
//						System.out.println("Kleur van vak " + vak + " was dezelfde als van vak " + j + ", kleur was " + i);
						conflict = true;
					}
				}
			}
			// Dit vak kon niet met deze kleur geverfd worden, probeer een andere kleur
			if (conflict) {
				continue;
			}
			// Dit vak kon met deze kleur geverfd worden, ga een volgende vak verfen
			else {
				if (vak == N - 1) {
					// Dit was al de laatste vak, dus niet verder zoeken
					mogelijk++; // Moet geteld worden als geldige oplossing
				}
				else paint(vak + 1); // Niet alle vakken nog gekleurd, moet volgende verfen!
			}
		}
		
	}
	
	public static void doTestCase() throws Exception {
		// Lees alle vakken in
		N = readInt();
		x1 = new int[N];
		x2 = new int[N];
		y1 = new int[N];
		y2 = new int[N];
		buur = new boolean[N][N];
		vakKleur = new int[N];
		
		for (int i = 0; i < N; i++) {
			x1[i] = readInt();
			y1[i] = readInt();
			x2[i] = readInt();
			y2[i] = readInt();
			
			if (x1[i] > x2[i]) {
				int _x = x1[i];
				x1[i] = x2[i];
				x2[i] = _x;
			}
			if (y1[i] > y2[i]) {
				int _y = y1[i];
				y1[i] = y2[i];
				y2[i] = _y;
			}
		}
		
		// Vindt alle buren van alle vakken
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				if ((x1[i] == x2[j] || x1[j] == x2[i]) && !(y2[i] <= y1[j] || y2[j] <= y1[i])) buur[i][j] = true;
				else if ((y1[i] == y2[j] || y1[j] == y2[i]) && !(x2[i] <= x1[j] || x2[j] <= x1[i])) buur[i][j] = true;
				else buur[i][j] = false;
			}
		
		mogelijk = 0;
		// Kleur eerste vak in en ga vanaf daar verder zoeken
		paint(0);
		
		System.out.println("" + mogelijk);
	}
}
