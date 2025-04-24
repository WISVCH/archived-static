/* coneasoup.c */

#include <stdio.h>

int main (void)
{
	FILE *fp;
	if (!(fp = fopen("c.in", "r")))
		exit(1);
	
	int n, i, j;
	char w[255];
	
	fscanf(fp, "%d", &n);
	for (i = 0; i < n; i++)
	{
		fscanf (fp, "%s", &w);
		j = 0;
		while (w[j] != '\0')
		{
			printf("%c", w[j] > 'M' ? w[j] - 13 : w[j] + 13);
			j++;
		}
		printf ("\n");
	}
	return 0;
}
