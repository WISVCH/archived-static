/* coneasoup.c */

#include <stdio.h>
#define PI 3.141593

int calc(int h, int d)
{
	double r;
	r = 1./12. * PI * d*d * h;
	return (int)r;
}

int main (void)
{
	FILE *fp;
	if (!(fp = fopen("a.in", "r")))
		exit(1);
	
	int n, h, d, i;
	
	fscanf(fp, "%d", &n);
	for (i = 0; i < n; i++)
	{
		fscanf (fp, "%d%d", &h, &d);
		printf("%d\n", calc(h, d));
	}
	return 0;
}
