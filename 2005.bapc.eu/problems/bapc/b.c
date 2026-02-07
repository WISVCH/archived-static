#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#define RANGE 1000
#define EPS 1e-6
#define max(a, b) ((a) > (b) ? (a) : (b))
#define sqr(a) ((a)*(a))

typedef struct change {
	double pos;
	int inhab;
} change;

change changes[2000];

int compChange(const void *a, const void *b) {
	change *c1 = (change*)a;
	change *c2 = (change*)b;
	if (c1->pos < c2->pos) return -1;
	else return 1;
}

void solve() {
	int nh, nc = 0, i;
	int ans = 0, val = 0;
	scanf("%d", &nh);
	for (i = 0; i < nh; i++) {
		int x, y, inhab;
		scanf("%d %d %d", &x, &y, &inhab);
		if (abs(y) <= RANGE) {
			double d = sqrt(sqr(RANGE) - sqr(y)) + EPS;
			changes[nc].inhab = inhab;
			changes[nc++].pos = x - d;
			changes[nc].inhab = -inhab;
			changes[nc++].pos = x + d;
		}
	}
	qsort(changes, nc, sizeof(change), compChange);
	for (i = 0; i < nc; i++) {
		val += changes[i].inhab;
		ans = max(ans, val);
	}
	printf("%d\n", ans);
}

int main() {
	int n;
	scanf("%d\n", &n);
	while (n--) {
		solve();
	}
	return 0;
}
	
