#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct point {
	int x, y;
} point;
int nIslands;
point islands[75000];

int compIslands(const void *a, const void *b) {
	point i1 = islands[*(int*)a];
	point i2 = islands[*(int*)b];
	if (i1.x != i2.x) return i1.x - i2.x;
	return i1.y - i2.y;
}

int compIslands2(int a, int b) {
	point i1 = islands[a];
	point i2 = islands[b];
	if (i1.y != i2.y) return i1.y - i2.y;
	return i1.x - i2.x;
}

int tmp[75000];

unsigned int mergesort(int *idx, int size) {
	if (size == 1) {
		return 0;
	} else {
		unsigned int ans = 0;
		int half = size/2;
		int half2 = size-half;
		int *h1 = tmp;
		int *h2 = idx+half;
		int l, r, i;
		ans += mergesort(idx, half);
		ans += mergesort(idx+half, half2);
		memcpy(h1, idx, half*sizeof(int));
		for (i = 0, l = 0, r = 0; i < size; i++) {
			if (l == half) {
				idx[i] = h2[r++];
			} else if (r == half2) {
				idx[i] = h1[l++];
			} else {
				if (compIslands2(h1[l], h2[r]) < 0) {
					idx[i] = h1[l++];
					ans += half2-r;
				} else {
					idx[i] = h2[r++];
				}
			}
		}
		return ans;
	}
}

int idx[75000];

void solve() {
	int i;
	
	scanf("%d", &nIslands);
	if (nIslands < 1 || nIslands > 75000) {
		printf("Invalid number of islands!\n");
	}
	for (i = 0; i < nIslands; i++) {
		int x, y;
		idx[i] = i;
		scanf("%d %d\n", &x, &y);
		if (x < -1000000000 || x > 1000000000) {
			printf("Invalid x-coordinate: %d\n", x);
		}
		if (y < -1000000000 || y > 1000000000) {
			printf("Invalid y-coordinate: %d\n", y);
		}
		islands[i].x = x;
		islands[i].y = -y;
	}
	qsort(idx, nIslands, sizeof(idx[0]), compIslands);
	printf("%u\n", mergesort(idx, nIslands));
}

int main() {
	int n;
	scanf("%d", &n);
	while (n--) {
		solve();
	}
	return 0;
}
