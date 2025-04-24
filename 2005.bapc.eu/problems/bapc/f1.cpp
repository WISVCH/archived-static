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

int main () {

  int runs;
  cin >> runs;
  while (runs--) {
    int N,K;
    cin >> N >> K;

    int now=N;
    for (int i=0; i<K; i++) {
      int next=now;
      while (next-toll(next) < now) next++;
      now=next;
    }

    cout << now << endl;
  }

  return 0;
}
