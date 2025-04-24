#include <stdio.h>
/* by Michal Koucky */
/* Used linear method - you count the number of expected 'enclosed' 
expressions. After every operator E,... you expect one more expression 
than before, N doesn't change the number of expected expressions, 
p through z decreases the number of expected expressions by one (they 
are enclosed expr). */

int read_sen()
/**
r: 0 ok.
   -1 syntax err
   -3 eof
*/
{
	char c;
	int senc;	/* number of expected (sub)sentences */
	
	senc=1;		/* you expect one enclosed expression */
	
	while(1){

	switch( c= getchar() )
	{
	
	case 'C':
	case 'D':
	case 'E':
	case 'I':
		if(senc)senc++;
	case 'N':
	        if(!senc){	/* consume rest of line */
	        	while(getchar()!='\n');
	        	return -1;
	        }
		break;

	case '\n':
		if(senc) return -1;
		else return 0;
	case EOF:
		return -3;
	default:
		if( c < 'p' || c > 'z' || !senc){	/* consume rest of line */
	        	while(getchar()!='\n');
	        	return -1;
	        }
		senc--;
	}
	}
    return -1;
}

int main()
{
	while(1)
	{
		switch(read_sen())
		{
		case 0:
			printf("YES\n");
			break;
		case -1:
			printf("NO\n");
			break;
		case -3:
			return 0;
		}
	}
}	/* main */
	
