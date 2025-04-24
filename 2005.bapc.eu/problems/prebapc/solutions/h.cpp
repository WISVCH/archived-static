/*
  [NKP'05] Venus Rover
  by: Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>

int main () {

  int runs;
  cin >> runs;
  while (runs--) {
    int N,T,M;
    cin >>N>>T>>M;
    vector<int> t(N),m(N),val(N);
    for (int i=0; i<N; i++) cin>>t[i]>>m[i]>>val[i];

    vector<vector<int> > best(T+1, vector<int>(M+1, 0));
    int res=0;

    for (int i=0; i<N; i++)
      for (int tt=T; tt>=0; tt--)
	for (int mm=M; mm>=0; mm--)
	  if (tt-t[i]>=0 && mm-m[i]>=0)
	    res >?= best[tt][mm] >?= best[tt-t[i]][mm-m[i]] + val[i];

    cout << res << endl;
  }

  return 0;
}
