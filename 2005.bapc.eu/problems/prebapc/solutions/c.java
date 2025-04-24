import java.io.*;
import java.util.*;

public class c {
	FileReader in = null;
	
	public static void main( String[] args ) {
		try {
			Insecurity in = new Insecurity( args );
		}
		catch( Throwable t ) {
			System.out.println( "Throwable: " + t );
			t.printStackTrace( );
		}
	}

	public Insecurity( String[] args ) throws Throwable{
		in = new FileReader( args[0] );
		int a = readInt( );
		for( int i = 0; i < a; i++ )
			solve( );
	}

	public int readInt( ) throws Throwable {
		return Integer.parseInt( readWord( ) );
	}
	
	public String readWord( ) throws Throwable {
		String s = "";
		int c = in.read( );
		while( Character.isWhitespace( (char) c ) )
			c = in.read( );
		s = new String( "" + (char) c );
		c = in.read( );
		while( !Character.isWhitespace( (char) c ) ) {
			s += (char) c;
			c = in.read( );
		}
		return s;
	}
	
	public void solve( ) throws Throwable {
		String encrypt = readWord( );
		int n = readInt( );
		int i = 0;
		LinkedList< String > users = new LinkedList< String >( );
		LinkedList< String > passes = new LinkedList< String >( );
		String user, pass;
		for( i = 0; i < n; i++ ) {
			user = readWord( );
			if( canBeUser( user, encrypt ) )
				users.add( user );
		}
		for( i = 0; i < n; i++ ) {
			pass = readWord( );
			if( canBePass( pass, encrypt ) )
				passes.add( pass );
		}
		ListIterator< String > user_it = users.listIterator( 0 );
		while( user_it.hasNext( ) ) {
			user = user_it.next( );
			ListIterator< String > pass_it = passes.listIterator( 0 );
			while( pass_it.hasNext( ) ) {
				pass = pass_it.next( );
				if( isSolution( user, pass, encrypt ) ) {
					System.out.println( user );
					System.out.println( pass );
					// inverse = null;
					// return; -> Check of er meerdere mogelijkheden zijn (kan!) Moeten er uit
				}
			}
		}
		inverse = null;
	}
	
	public boolean canBeUser( String user, String encrypt ) {
		String encuser = encrypt( user );
		encuser = encuser.substring( 0, encuser.length( ) - 1 );
		for( int i = 0; i < encuser.length( ); i++ ) {
			if( encuser.charAt( i ) != encrypt.charAt( i ) )
				return false;
		}
		return true;
	}
	
	public String inverse = null;
	
	public boolean canBePass( String pass, String encrypt ) {
		if( inverse == null ) {
			inverse = new String( );
			for( int i = encrypt.length( ) - 1; i >= 0; i-- )
				inverse += encrypt.charAt( i );
		}
		String encpass = encrypt( pass );
		encpass = encpass.substring( 1, encpass.length( ) );
		for( int i = 0; i < encpass.length( ); i++ ) {
			if( encpass.charAt( encpass.length( ) - ( 1 + i ) ) != inverse.charAt( i ) )
				return false;
		}
		return true;
	}
	
	public boolean isSolution( String user, String pass, String encrypt ) {
		return encrypt( user + pass ).equals( encrypt );
	}
	
	public String encrypt( String s ) {
		char[] ar = null;
		ar = new char[ s.length( ) + 1 ];
		ar[ 0 ] = highhex( (char)s.charAt( 0 ) );
		for( int i = 1; i < ar.length - 1; i++ )
			ar[ i ] = lowhex( (char)(s.charAt( i - 1 ) ^ ( s.charAt( i ) >>> 4 )) );
		ar[ ar.length - 1 ] = lowhex( (char) s.charAt( ar.length - 2 ) );
		return new String( ar );
	}
		
	public char highhex( char a ) {
		a = (char)((char)a >>> (char)4);
		if( a < 10 )
			return (char)((char) '0' + (char) a);
		else
			return (char)((char) 'A' + (char) a - (char) 10 );
	}
	
	public char lowhex( char a ) {
		a = (char)((char) a & (char) 0xF);
		if( a < 10 )
			return (char)((char) '0' + (char) a);
		else
			return (char)((char) 'A' + (char) a - (char) 10 );
	}
}