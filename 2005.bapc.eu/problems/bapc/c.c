#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define MAXFLOORS 1000
#define true 1
#define false 0
#define INFINITY 1000000000
#define MAX(a, b) ((a) > (b) ? (a) : (b))
#define MIN(a, b) ((a) < (b) ? (a) : (b))

	char isWaiting[MAXFLOORS+1];
	int totalTime[MAXFLOORS+1][MAXFLOORS+2];
	int totalWaitTime[MAXFLOORS+1][MAXFLOORS+2];
	char willNotWait[MAXFLOORS+1][MAXFLOORS+2];

void solveTest() {
	int M, S, W;
	int Nf, Nw;
	int i, f, p;
	int maxfloor = 0;

	scanf("%d %d %d", &M, &S, &W);
	scanf("%d %d", &Nf, &Nw);

	memset(isWaiting, 0, (MAXFLOORS+1)*sizeof(char));
	for (i = 0; i < Nw; i++) {
		int tmp;
		scanf("%d", &tmp);
		maxfloor = MAX(maxfloor, tmp);
		if (tmp)
			isWaiting[tmp] = true;
	}

	if (maxfloor == 0) {
		printf("%d\n", 0);
		return;
	}

	// reset top row
	for (i = 0; i <= Nf; i++) {
		totalTime[i][Nf+1] = (Nf-i) * M;
		totalWaitTime[i][Nf+1] = (Nf-i) * M + S;
		willNotWait[i][Nf+1] = true;
	}

	// fill left colum
	for (i = Nf; i >= 0; i--) {
		if (isWaiting[i]) {
			totalWaitTime[Nf][i] = MAX(totalWaitTime[Nf][i+1], W*(Nf-i) + S);
			willNotWait[Nf][i] = false;
		} else {
			if (willNotWait[Nf][i+1]) {
				totalTime[Nf][i] = totalTime[Nf][i+1];
				totalWaitTime[Nf][i] = totalWaitTime[Nf][i+1];
				willNotWait[Nf][i] = true;
			} else {
				totalWaitTime[Nf][i] = totalWaitTime[Nf][i+1];
				willNotWait[Nf][i] = false;
			}
		}
	}


	for (p = Nf; p >= 0; p--) {
		for (f = Nf-1; f >= 0; f--) {
			int minWait = INFINITY, minNotWait = INFINITY;
			minNotWait = totalWaitTime[f+1][p] + M;
			if (willNotWait[f+1][p]) {
				int t = totalTime[f+1][p] + M;
				if (t < minNotWait) {
					minNotWait = t;
				}
			}
			if (willNotWait[f][p+1] && !isWaiting[p]) {
				if (totalTime[f][p+1] < minNotWait) {
					minNotWait = totalTime[f][p+1];
				}
			}
			willNotWait[f][p] = true;
			totalTime[f][p] = minNotWait;
			if (isWaiting[p]) {
				minWait = MAX(minNotWait, abs(f-p)*W)+S;
 			} else {
				minWait = minNotWait + S;
			}
			if (willNotWait[f][p+1]) {
				int t;
				if (isWaiting[p]) {
					t = MAX(totalTime[f][p+1], abs(f-p)*W)+S;
				} else {
					t = totalTime[f][p+1] + S;
				}
				if (t < minWait) {
					minWait = t; 
				}
			}
			int t = MAX(totalWaitTime[f][p+1], abs(f-p)*W+S);
			if (t < minWait) {
				minWait = t;
			}
			totalWaitTime[f][p] = minWait;
		}
	}

	int ans = maxfloor*W;
	for (f = 0; f <= Nf; f++) {
		if (willNotWait[0][f]) ans = MIN(ans, MAX(totalTime[0][f], ((f-1)*W)));
		ans = MIN(ans, MAX(totalWaitTime[0][f]-S, ((f-1)*W)));
	}
	
	printf("%d\n", ans);
}

int main() {
	int N;
	scanf("%d", &N);
	while (N--) {
		solveTest();
	}
	return 0;
}
