/*
  [NKP'05] BANCOPIA
  by Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <cstdio>
#include <vector>
#include <queue>

struct state { int n,p; double prob; };
struct edge { int to; double prob; };

bool operator < (state a, state b) { return a.prob > b.prob; }

int main () {

  int runs;
  cin >> runs;
  for (int run=0; run<runs; run++) {

    int N,M,P,fr,to;
    cin >>N>>M>>P>>fr>>to;
    fr--; to--;

    vector<vector<edge> > e(N);

    for (int m=0; m<M; m++) {
      int a,b;
      double prob;
      cin >>a>>b>>prob;
      a--; b--;
      e[a].push_back((edge){b,prob});
      e[b].push_back((edge){a,prob});
    }

    double result=1.0;
    vector<vector<double> > best(N, vector<double>(P+1, -1));
    priority_queue<state> pq;
    pq.push((state){fr,0,0.0});

    while (!pq.empty()) {
      state s=pq.top(); pq.pop();
      if (best[s.n][s.p]!=-1) continue;
      best[s.n][s.p]=s.prob;

      if (s.n==to) result<?=s.prob;

      for (vector<edge>::iterator it=e[s.n].begin(); it!=e[s.n].end(); it++) {
	if (best[it->to][s.p]==-1)
	  pq.push((state){it->to,s.p, 1.0-(1.0-s.prob)*(1.0-it->prob)});
	if (s.p+1<=P && best[it->to][s.p+1]==-1)
	  pq.push((state){it->to,s.p+1, 1.0-(1.0-s.prob)*(1.0-it->prob/2.0)});
      }
    }

    printf ("%.4lf\n",result);
  }

  return 0;
}


