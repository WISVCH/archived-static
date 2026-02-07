/*
  [NKP'05] Skiliften
  door: Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>
#include <string>
#include <algorithm>

struct pos {
  int y,x,h;
  pos(int _y, int _x, int _h) { y=_y; x=_x; h=_h; }
};

bool operator < (pos a, pos b) { return a.h>b.h; }

int Y,X;
vector<vector<int> > h,u;

void fill (int y, int x) {
  if (u[y][x]) return;
  u[y][x]=1;
  if (y>0 && h[y-1][x]<=h[y][x]) fill(y-1,x);
  if (x>0 && h[y][x-1]<=h[y][x]) fill(y,x-1);
  if (y<Y-1 && h[y+1][x]<=h[y][x]) fill(y+1,x);
  if (x<X-1 && h[y][x+1]<=h[y][x]) fill(y,x+1);
}

int count () {
  int res=0;
  u = vector<vector<int> >(Y, vector<int>(X,0));

  vector<pos> p;
  for (int y=0; y<Y; y++)    
    for (int x=0; x<X; x++)
      p.push_back(pos(y,x,h[y][x]));

  sort(p.begin(),p.end());

  for (int i=0; i<p.size(); i++)
    if (!u[p[i].y][p[i].x]) {
      res++;
      fill(p[i].y,p[i].x);
    }
  
  return res;
}

int main () {

  int runs;
  cin >> runs;

  for (int run=0; run<runs; run++) {
    cin >> Y >> X;
    h = vector<vector<int> >(Y, vector<int>(X));
    
    bool flat=true;
    for (int y=0; y<Y; y++)
      for (int x=0; x<X; x++) {
	cin >> h[y][x];
	if (h[y][x]!=h[0][0]) flat=false;
      }

    if (flat) {
      cout << "0" << endl;
      continue;
    }

    int a = count();
    for (int y=0; y<Y; y++)
      for (int x=0; x<X; x++) h[y][x]*=-1;
    int b = count();

    cout << max(a,b) << endl;
  }

  return 0;
}
