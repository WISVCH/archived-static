/*
  [NKP'05] North-western wind
  by: Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>

class point { 
  public: int x,y; 
  point () {}
  point (int _x, int _y) { x=_x, y=_y; }
};

bool smaller1 (point a, point b) { return a.x!=b.x ? a.x<b.x : a.y>b.y; }
bool smaller2 (point a, point b) { return a.y!=b.y ? a.y>b.y : a.x<b.x; }

template <class smallerfunction>
long long sort (vector<point> &p, smallerfunction smaller) {
  
  if (p.size()==1) return 0;

  vector<point> p1(p.begin(), p.begin()+p.size()/2);
  vector<point> p2(p.begin()+p.size()/2, p.end());

  long long res=sort(p1,smaller)+sort(p2,smaller), tmp=0;
  int i1=0, i2=0, i=0;

  while (i1<p1.size() || i2<p2.size())
    if (i2==p2.size() || (i1<p1.size() && smaller(p1[i1],p2[i2])))
      p[i++] = p1[i1++], res+=tmp;
    else 
      p[i++] = p2[i2++], tmp++;
  
  return res;
}

int main () {

  int runs;
  cin >> runs;
  while (runs--) {
    int N,x,y;
    cin >> N;
    vector<point> p(N);
    for (int i=0; i<N; i++) {
      cin >> x >> y;
      p[i] = point(x,y);
    }

    sort(p, smaller1);
    cout << (long long) N*(N-1)/2 - sort(p, smaller2) << endl;
   }

  return 0;
}
