/* epluribus.c */

#include <stdio.h>

int main (void)
{
	FILE *fp;
	if (!(fp = fopen("b.in", "r")))
		exit(1);
	
	int n, b, i, j, k, l, r;
	
	fscanf(fp, "%d", &n);
	for (i = 0; i < n; i++)
	{
		fscanf (fp, "%d", &b);
		r = 0;
		for (j = 1; j <= b; j++)
			for (k = j; k <= b; k++)
				for (l = k; l <= b; l++)
					if (j*k*l == b) r++;
		printf("%d\n", r);
	}
	return 0;
}
