/*
  [NKP'05] Mondriaan
  door: Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>

int N,cnt;
vector<int> col;
vector<vector<int> > adj;

void go (int dep) {

  if (dep==N) {
    cnt++;
    return;
  }
  
  vector<bool> ok(4,true);
  for (int i=0; i<dep; i++)
    if (adj[dep][i] && col[i]>0) ok[col[i]]=false;

  for (int i=0; i<4; i++)
    if (ok[i]) {
      col[dep]=i;
      go(dep+1);
    }   
}

int main () {

  int runs;
  cin >> runs;

  while (runs--) {
    cin >> N;
    vector<int> x1(N),y1(N),x2(N),y2(N);
    
    for (int i=0; i<N; i++) {
      cin >>x1[i]>>y1[i]>>x2[i]>>y2[i];
      if (x1[i]>x2[i]) swap(x1[i],x2[i]);
      if (y1[i]>y2[i]) swap(y1[i],y2[i]);
    }

    adj=vector<vector<int> >(N, vector<int>(N));
    for (int i=0; i<N; i++)
      for (int j=0; j<N; j++) {
	if ((x1[i]==x2[j] || x1[j]==x2[i]) && !(y2[i]<=y1[j] || y2[j]<=y1[i])) adj[i][j]=1;
	if ((y1[i]==y2[j] || y1[j]==y2[i]) && !(x2[i]<=x1[j] || x2[j]<=x1[i])) adj[i][j]=1;
      }

    col=vector<int>(N);
    cnt=0;
    go(0);
    cout << cnt << endl;      
  }
  return 0;
}
