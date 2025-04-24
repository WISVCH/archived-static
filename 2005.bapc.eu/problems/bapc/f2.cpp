/*
  [NKP'05] SHEPHERDS
  by Jan Kuipers
*/

using namespace std;

#include <vector>
#include <iostream>

int toll (int n) {
  if (n<3) return 0;
  if (n<5) return 1;
  
  for (int i=3; i*i<=n; i++)
    if (n%i==0) return n/i;
  
  if (n%2==0) return 2;
  return 1;			      
}

const int MAX = 1100000;

int main () {

  vector<int> toll(MAX), next(MAX);
  for (int d=1; d<MAX; d++) toll[d]=d;
  
  for (int d=2; d<MAX; d++)
    if (toll[d]==d)
      for (int x=2*d; x<MAX; x+=d) toll[x]<?=d;

  for (int d=3; d<MAX; d++)
    if (toll[d]==2) {
      if (d%3==0) toll[d]=3;
      else if (d%4==0) toll[d]=4;
      else toll[d]=toll[d/2];
    }

  toll[0]=toll[1]=toll[2]=0;
  for (int d=3; d<MAX; d++) toll[d]=d/toll[d];

  for (int n=1; n<MAX; n++) {
    next[n]= next[n-1];
    while (next[n]-toll[next[n]] < n) next[n]++;
  }

  int runs;
  cin >> runs;
  
  while (runs--) {
    int N,K;
    cin >> N >> K;

    for (int i=0; i<K; i++) N=next[N];
    cout << N << endl;
  }

  return 0;
}
