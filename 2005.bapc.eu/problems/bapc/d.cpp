/*
  [NKP'05] GIANT COVER
  by Jan Kuipers
*/

using namespace std;

#include <cstdio>
#include <vector>
#include <queue>
#include <cmath>

double eps = 1e-9;

class point {
public:
  double x,y,z;
  point() { x=y=z=0; }
  point(double _x, double _y, double _z) { x=_x; y=_y; z=_z; }
};

point operator - (point a, point b) { return point(a.x-b.x, a.y-b.y, a.z-b.z); }
point operator ^ (point a, point b) { return point(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x); }
double operator * (point a, point b) { return a.x*b.x + a.y*b.y + a.z*b.z; }
bool operator == (point a, point b) { return a.x==b.x && a.y==b.y && a.z==b.z; }
double len (point a) { return sqrt(1.0*(a*a)); }
    
int main () {

  int runs;
  scanf ("%i\n",&runs);

  while (runs--) {

    vector<point> p;
    int x1,y1,x2,y2;
    scanf ("%i %i %i %i\n",&x1,&y1,&x2,&y2);
    p.push_back(point(x1,y1,0));
    p.push_back(point(x2,y1,0));
    p.push_back(point(x2,y2,0));
    p.push_back(point(x1,y2,0));
        
    int N;
    scanf ("%i\n",&N);

    for (int i=0; i<N; i++) {
      int bx1,by1,bx2,by2,bh;
      scanf ("%i %i %i %i %i\n",&bx1,&by1,&bx2,&by2,&bh);
      p.push_back(point(bx1,by1,bh));
      p.push_back(point(bx2,by1,bh));
      p.push_back(point(bx2,by2,bh));
      p.push_back(point(bx1,by2,bh));
    }

    N=p.size();
    vector<vector<int> > done(N, vector<int>(N, -1));

    queue<int> q;
    q.push(0);
    q.push(1);
    done[1][0]=2;
    done[0][3]=9999;
    done[3][2]=9999;
    done[2][1]=9999;
    
    double area=0.0;
    
    while (!q.empty()) {
      int A = q.front(); q.pop();
      int B = q.front(); q.pop();

      if (done[A][B]!=-1) continue;
      
      point n = (p[done[B][A]]-p[B]) ^ (p[done[B][A]]-p[A]);
      
      int C = -1;
      double Ccos3D = -1e100;
      double Ccos2D = -1e100;
      double Cdist  = -1e100;
      
      for (int i=0; i<N; i++) {
	if (done[B][i]!=-1 || done[i][A]!=-1) continue;
						
	point m = (p[i]-p[A]) ^ (p[i]-p[B]);
	if (len(m)==0) continue;

	double icos3D = m*n / len(n) / len(m);
	double icos2D = (p[i]-p[B])*(p[B]-p[A]) / len(p[i]-p[B]) / len(p[B]-p[A]);
	double idist  = len(m);
		
	if ((icos3D>Ccos3D+eps) ||
	    (icos3D>Ccos3D-eps && icos2D>Ccos2D+eps) ||
	    (icos3D>Ccos3D-eps && icos2D>Ccos2D-eps && Cdist<idist)) {
	  C = i;
	  Ccos3D = icos3D;
	  Ccos2D = icos2D;
	  Cdist  = idist;
	} 
      }

      done[A][B] = C;

      done[B][C] = A;
      q.push(C); 
      q.push(B);

      done[C][A] = B;
      q.push(A);
      q.push(C);

      area += 0.5 * len((p[C]-p[A]) ^ (p[C]-p[B]));
    }

    printf ("%.4lf\n", area);
  }

  return 0;
}
