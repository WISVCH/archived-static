/*
  [NKP '05] CELLPHONES
  by Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>
#include <cmath>

const double eps=1E-12;

int main () {

  int runs;
  cin >> runs;

  while (runs--) {
    int N;
    cin >> N;

    if (N==0) break;

    vector<pair<double,int> > add;
    
    for (int n=0; n<N; n++) {
      int x,y,num;
      cin >>x>>y>>num;
      if (abs(y)>1000) continue;
      add.push_back(make_pair(x-sqrt(1E6-y*y)-eps, num));
      add.push_back(make_pair(x+sqrt(1E6-y*y)+eps, -num));
    }

    sort(add.begin(), add.end());
    
    int best=0, now=0;
    for (int n=0; n<add.size(); n++)
      best >?= now += add[n].second;

    cout << best << endl;    
  }

  return 0;
}
