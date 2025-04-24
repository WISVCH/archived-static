/*
  [NKP'05] INSECURITY
  by: Jan Kuipers
*/

using namespace std;

#include <cstdio>
#include <vector>
#include <string>
#include <algorithm>

string encode (string w) {

  string zero(1, char(0));
  string c=zero;

  for (int i=0; i<w.size(); i++) {
    c += zero;
    c[c.size()-2] ^= w[i]/16;
    c[c.size()-1] ^= w[i]%16;
  }

  for (int i=0; i<c.size(); i++)
    c[i] = "0123456789ABCDEF"[c[i]];

  return c;
}

int main () {

  int runs;
  scanf ("%i\n",&runs);

  while (runs--) {
    string key;
    char tmp[100];
    scanf ("%s",tmp);
    key=string(tmp);

    int N;
    scanf ("%i\n",&N);

    vector<string> x(N), y(N);
    for (int i=0; i<N; i++) { scanf("%s\n",tmp); x[i]=string(tmp); }
    for (int i=0; i<N; i++) { scanf("%s\n",tmp); y[i]=string(tmp); }

    vector<string> posx, posy;
    for (int i=0; i<N; i++) {
      string tmp = encode(x[i]);
      if (key.size() < tmp.size()-1) continue;
      if (key.substr(0,tmp.size()-1) == tmp.substr(0,tmp.size()-1))
	posx.push_back(x[i]);
    }

    for (int i=0; i<N; i++) {
      string tmp = encode(y[i]);
      if (key.size() < tmp.size()-1) continue;
      if (key.substr(key.size()-tmp.size()+1) == tmp.substr(1))
        posy.push_back(y[i]);
    }

    for (int i=0; i<posx.size(); i++)
      for (int j=0; j<posy.size(); j++)
	if (encode(posx[i]+posy[j]) == key)
	  printf ("%s\n%s\n", posx[i].c_str(), posy[j].c_str());
  }

  return 0;
}
