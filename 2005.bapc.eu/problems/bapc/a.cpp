/*
  [NKP'05] ASM
  by Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>
#include <string>
#include <queue>

const int dy[4] = { 1,-1, 0, 0 };
const int dx[4] = { 0, 0, 1,-1 };

int main () {

  int runs;
  cin >> runs;

  while (runs--) {
    int Y,X,N,H;
    cin >>Y>>X>>N>>H;
    
    vector<string> s(Y);
    for (int y=0; y<Y; y++) cin >>s[y];

    vector<vector<int> > t(Y, vector<int>(X, 0));
    for (int y=0; y<Y; y++)
      for (int x=0; x<X; x++)
        t[y][x] = s[y][x]-'0';
    
    for (int i=0; i<N; i++) {
      int y,x;
      cin >>y>>x;
      t[y-1][x-1]++;
    }

    bool again = true;
    while (again) {
      again = false;
      for (int y=0; y<Y; y++)
        for (int x=0; x<X; x++)
          if (t[y][x] > H) {
    	    t[y][x]-=4;
            again=true;
 	  
  	    for (int d=0; d<4; d++) {
	      int ny=y+dy[d], nx=x+dx[d];
  	      if (nx<0 || nx>=X || ny<0 || ny>=Y) continue;
	      t[ny][nx]++;
	    }
          }
    }

    for (int y=0; y<Y; y++) {
      for (int x=0; x<X; x++)
        cout << char('0'+t[y][x]);
      cout << endl;
    }
  }

  return 0;
}

  
