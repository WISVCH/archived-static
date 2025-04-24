/*
  [NKP'05] Nim/3
  by Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>

typedef vector<int> VI;
typedef vector<VI> VVI;
typedef vector<VVI> VVVI;
typedef vector<VVVI> VVVVI;
typedef vector<VVVVI> VVVVVI;

int cachenum;
VVI value;
VVVVVI cache;

int winner (VI n, int p) {
  
  int &res = cache[cachenum][n[0]][n[1]][n[2]][p];
  if (res!=-1) return res;
  if (n[0]+n[1]+n[2] == 0) return res=(p+2)%3;
 
  int bestvalue=-1;
    
  for (int stak=0; stak<3; stak++)
    for (int num=1; num<=n[stak]; num++) {
      n[stak]-=num;
      int win = winner(n,(p+1)%3);
      if (value[p][win] > bestvalue) {
	res=win;
	bestvalue=value[p][win];
      }
      n[stak]+=num;
    }
  
  return res;
}

int main () {

  cache=VVVVVI(27,VVVVI(21,VVVI(21,VVI(21,VI(3,-1)))));

  int runs;
  cin >> runs;
  while (runs--) {

    VI n(3);
    cin >> n[0] >> n[1] >> n[2];

    value = VVI(3, VI(3,0));
    value[0][0]=2;
    value[1][1]=2;
    value[2][2]=2;

    int f0,f1,f2;
    cin >> f0 >> f1 >> f2;
    
    value[0][--f0]=1;
    value[1][--f1]=1;
    value[2][--f2]=1;

    cachenum = 9*f0+3*f1+f2;

    int win = winner(n,0);
    
    bool done=false;
    for (int stak=0; stak<3 && !done; stak++)
      for (int num=1; num<=n[stak] && !done; num++) {
	n[stak]-=num;
	if (winner(n,1)==win) {
	  cout << stak+1 << " " << num << endl;
	  done=true;
	}
	n[stak]+=num;
      }
  }
  
  return 0;  
}
