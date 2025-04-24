/*
  [NKP'05] Mandala
  by: Jan Kuipers
*/

using namespace std;

#include <cstdio>
#include <iostream>
#include <vector>
#include <queue>
#include <set>
#include <cmath>

double eps = 1e-8;

struct point {
  double x,y;
  point() {}
  point(double _x, double _y) { x=_x; y=_y; }
};

struct circle {
  int x,y,r;
  circle() {}
  circle(int _x, int _y, int _r) { x=_x; y=_y; r=_r; }
};

bool operator == (circle a, circle b) {
  return a.x==b.x && a.y==b.y && a.r==b.r;
}

bool operator < (point a, point b) {
  if (abs(a.x-b.x) > eps) return a.x<b.x;
  if (abs(a.y-b.y) > eps) return a.y<b.y;
  return false;
}

double sqr (double x) { return x*x; }

int main () {

  int runs;
  cin >> runs;
  while (runs--) {
    
    int N;
    cin >> N;
    vector<circle> c(N);

    for (int i=0; i<N; i++) {
      int x,y,r;
      cin >> x >> y >> r;
      c[i] = circle(x,y,r);
    }

    for (int i=0; i<N; i++)
      for (int j=i+1; j<N; j++)
	if (c[i]==c[j]) {
	  c.erase(c.begin()+j);
	  j--;
	  N--;
	}

    set<point> points;
    vector<set<point> > pointsoncircle(N);
    vector<vector<bool> > intersect(N, vector<bool>(N));

    for (int i=0; i<N; i++)
      for (int j=0; j<N; j++) {
	if (i==j) continue;
	
	double x0 = c[i].x;
	double y0 = c[i].y;
	double dx = c[j].x-x0;
	double dy = c[j].y-y0;
	double r = c[i].r;
	double s = c[j].r;

	if (dx*dx+dy*dy < (r-s)*(r-s) - eps) continue;
	if (dx*dx+dy*dy > (r+s)*(r+s) + eps) continue;
	
	double t = dx*dx+dy*dy-s*s+r*r;
	double A = 4*(dy*dy+dx*dx);
	double B = -4*dx*t;
	double C = t*t-4*r*r*dy*dy;
	double D = B*B - 4*A*C;

	point I1, I2;
	I1.x = x0 + (-B-sqrt(D)) / (2*A);
	I2.x = x0 + (-B+sqrt(D)) / (2*A);
	
	B = -4*dy*t;
	C = t*t-4*r*r*dx*dx;
	D = B*B - 4*A*C;

	I1.y = y0 + (-B-sqrt(D)) / (2*A);
	I2.y = y0 + (-B+sqrt(D)) / (2*A);

	if (abs(sqr(I1.x-x0)+sqr(I1.y-y0) - r*r) > eps) swap(I1.y, I2.y);

	intersect[i][j] = true;

 	points.insert(I1);
 	points.insert(I2);

 	pointsoncircle[i].insert(I1);
 	pointsoncircle[i].insert(I2);
      }

    int V=0,E=0,C=0;
    
    V=points.size();

    for (int i=0; i<N; i++) 
      E += pointsoncircle[i].size();

    vector<bool> used(N,false);
    for (int i=0; i<N; i++)
      if (!used[i]) {
	C++;
	queue<int> q;
	q.push(i);
	used[i]=true;
	while (!q.empty()) {
	  int n=q.front(); q.pop();
	  for (int j=0; j<N; j++)
	    if (intersect[j][n] && !used[j]) {
	      used[j]=true;
	      q.push(j);
	    }
	}
      }

    cout << E-V+C+1 << endl;
  }

  return 0;
}

