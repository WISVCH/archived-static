/*
  [NKP'05] Minotaurus
  door Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>
#include <string>
#include <queue>

typedef vector<int> VI;
typedef vector<VI> VVI;
typedef vector<VVI> VVVI;
typedef vector<VVVI> VVVVI;

const int dy[5] = { 0, 0, 0, 1,-1 };
const int dx[5] = { 0, 1,-1, 0, 0 };

int main () {

  int runs;
  cin >> runs;
  while (runs--) {

    int Y,X;
    cin >> X >> Y;
    vector<string> m(Y+2, string(X+2,'#'));
    for (int y=0; y<Y; y++) {
      string tmp;
      cin >> tmp;
      m[y+1] = "#"+tmp+"#";
    }

    Y+=2;
    X+=2;

    int ty=-1,tx=-1,my=-1,mx=-1;
    for (int y=0; y<Y; y++)
      for (int x=0; x<X; x++) {
	if (m[y][x]=='M') { my=y; mx=x; }
	if (m[y][x]=='T') { ty=y; tx=x; }
      }

    for (int turn=0; turn<2; turn++) {
      if (m[my][mx-1]!='#' && tx<mx) { mx--; continue; }
      if (m[my][mx+1]!='#' && tx>mx) { mx++; continue; }
      if (m[my-1][mx]!='#' && ty<my) { my--; continue; }
      if (m[my+1][mx]!='#' && ty>my) { my++; continue; }
    }    

    VVVVI best(Y,VVVI(X,VVI(Y,VI(X,-1))));

    best[ty][tx][my][mx]=0;
    
    queue<int> q;
    q.push(ty);
    q.push(tx);
    q.push(my);
    q.push(mx);

    bool solved=false;
      
    while (!q.empty() && !solved) {
      ty=q.front(); q.pop();
      tx=q.front(); q.pop();
      my=q.front(); q.pop();
      mx=q.front(); q.pop();

      for (int d=0; d<5 && !solved; d++) {
	int nty = ty+dy[d];
	int ntx = tx+dx[d];

	if (m[nty][ntx]=='#') continue;
	if (nty==my && ntx==mx) continue;

	int nmy = my;
	int nmx = mx;

	for (int turn=0; turn<2; turn++) {
	  if (m[nmy][nmx-1]!='#' && ntx<nmx) { nmx--; continue; }
	  if (m[nmy][nmx+1]!='#' && ntx>nmx) { nmx++; continue; }
	  if (m[nmy-1][nmx]!='#' && nty<nmy) { nmy--; continue; }
	  if (m[nmy+1][nmx]!='#' && nty>nmy) { nmy++; continue; }
	}

	if (m[nty][ntx]=='X') {
	  cout << best[ty][tx][my][mx]+1 << endl;
	  solved=true;
	}
	
	if (best[nty][ntx][nmy][nmx] != -1) continue;
	if (nty==nmy && ntx==nmx) continue;

	best[nty][ntx][nmy][nmx] = best[ty][tx][my][mx]+1;
	q.push(nty);
	q.push(ntx);
	q.push(nmy);
	q.push(nmx);
      }     
    }

    if (!solved) cout << "0" << endl;
  }

  return 0;  
}
