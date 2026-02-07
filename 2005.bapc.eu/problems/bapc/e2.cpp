/*
  [NKP'05] North-western wind
  by: Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>

int main () {

  int runs;
  cin >> runs;
  while (runs--) {
    int N;
    cin >> N;
    vector<int> x(N),y(N);
    for (int i=0; i<N; i++)
      cin >> x[i] >> y[i];

    long long sol=0;
    for (int i=0; i<N; i++)
      for (int j=0; j<N; j++)
        if (x[i]>=x[j] && y[i]<=y[j] && i!=j) sol++;

    cout << sol << endl;
  }

  return 0;
}
