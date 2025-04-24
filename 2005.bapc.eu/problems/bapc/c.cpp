/*
  [NKP'05] EVACUATION
  by Jan Kuipers
*/ 

using namespace std;

#include <iostream>
#include <vector>
#include <algorithm>

int Telav, Twait, Tstair;

int calctime (int Tstart, int Felav, int F1, int Fpick, int F2) {
  
  int pickuptime = 0;
  pickuptime >?= (F1-Fpick) * Tstair;
  pickuptime >?= (Fpick-F2) * Tstair;
  pickuptime >?= Tstart + (Felav-Fpick) * Telav;

  return pickuptime + Twait + (Fpick-F2)*Telav; 
}
	  
int main () {

  int runs;
  cin >> runs;
  while (runs--) {

    int F,N;
    cin >>Telav>>Twait>>Tstair;
    cin >>F>>N;

    N+=2;
    vector<int> f(N);
    for (int i=0; i<N-2; i++) cin >>f[i];
    f[N-2]=F;
    f[N-1]=0;
    
    sort(f.begin(),f.end(),greater<int>());

    vector<int> t(N+1,INT_MAX);
    t[0]=0;
		     
    for (int to=1; to<N; to++) {
      int pick=f[to];

      for (int fr=to; fr>=1; fr--) {
	while (calctime(t[fr-1],f[fr-1],f[fr],pick+1,f[to]) <
	       calctime(t[fr-1],f[fr-1],f[fr],pick,f[to])) pick++;

	t[to] <?= calctime(t[fr-1],f[fr-1],f[fr],pick,f[to]);
      }
    }

    int res = f[1]*Tstair;
    for (int n=1; n<N; n++)
      res <?= max(t[n] + f[n]*Telav, f[n+1]*Tstair);
    cout << res << endl;
  }

  return 0;
}
