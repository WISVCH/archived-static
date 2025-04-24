/*
  [NKP'05] Flipping Networks
  by Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>
#include <queue>

int main () {

  int runs;
  cin >> runs;
  while (runs--) {

    int N;
    cin >> N;
    
    vector<vector<int> > c(N,vector<int>(N,0));
    
    int M,a,b;
    cin >> M;

    for (int i=0; i<M; i++) {
      cin >> a >> b;
      a--; b--;
      c[b][a] = c[a][b] = 1;
    }

    cin >> M;

    for (int i=0; i<M; i++) {
      cin >> a >> b;
      a--; b--;
      c[a][b] = c[b][a] = 1-c[a][b];
    }
    
    vector<int> d(N,-1);
    queue<int> q;
    q.push(0);
    d[0]=0;

    while (!q.empty()) {
      int n=q.front(); q.pop();
      for (int i=0; i<N; i++)
	if (c[n][i] && d[i]==-1) {
	  d[i]=d[n]+1;
	  q.push(i);
	}
    }

    int cnt=0;
    for (int i=0; i<N; i++)
      if (d[i]>10) cnt++;

    cout << cnt << endl;    
  }

  return 0;
}
