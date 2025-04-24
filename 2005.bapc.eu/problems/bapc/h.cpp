/*
  [NKP'05] Tafeltjes
  door: Jan Kuipers
*/

using namespace std;

#include <iostream>
#include <vector>

const int M = 22;
const int dx[2][3] = { { 1,-1, 1 }, { 1,-1,-1 } };
const int dy[2][3] = { { 0, 0,-1 }, { 0, 0, 1 } };

int N,num,cnt;
int isfree[M][M];

bool isok (int x, int y, int x1, int y1, int x2, int y2) {
  if (y1==y2) return false;
  if (max(y1,y2)<y || min(y1,y2)>y) return false;
  return ((y2-y)*x1 + (y-y1)*x2)/(y2-y1) < x;
}

void go (int dep) {

  if (dep == num/3) {
    cnt++;
    return;
  }

  int best=99, bestx=-1, besty=-1;
  
  for (int x1=0; x1<M; x1++)
    for (int y1=0; y1<M; y1++) {
      if (isfree[x1][y1] == 0) continue;
      
      int tmp=0, type=x1%2, numfree=0;
      
      for (int d1=0; d1<3; d1++) {
	int x2=x1+dx[type][d1], y2=y1+dy[type][d1];
	if (isfree[x2][y2]) {
	  numfree++;
	  for (int d2=0; d2<3; d2++) {
	    int x3=x2+dx[1-type][d2], y3=y2+dy[1-type][d2];
	    if (x3==x1 && y3==y1) continue;
	    if (isfree[x3][y3]) tmp++;
	  }
	}
      }

      if (numfree==2) tmp+=1;
      if (numfree==3) tmp+=3;
      
      if (tmp<best) {
	best=tmp;
	bestx=x1;
	besty=y1;
      }
    }
    
  if (best==99) return;
  
  int x1=bestx,y1=besty,type=bestx%2;
  
  for (int d1=0; d1<3; d1++) {
    int x2=x1+dx[type][d1], y2=y1+dy[type][d1];
    if (isfree[x2][y2]) {
      for (int d2=0; d2<3; d2++) {
	int x3=x2+dx[1-type][d2], y3=y2+dy[1-type][d2];
	if (x3==x1 && y3==y1) continue;

	if (isfree[x3][y3]) {
	  isfree[x1][y1]=isfree[x2][y2]=isfree[x3][y3]=0;
	  go(dep+1);
	  isfree[x1][y1]=isfree[x2][y2]=isfree[x3][y3]=1;
	}
      }
    }
  }

  for (int d1=0; d1<3; d1++)
    for (int d2=d1+1; d2<3; d2++) {
      int x2=x1+dx[type][d1], y2=y1+dy[type][d1];
      int x3=x1+dx[type][d2], y3=y1+dy[type][d2];
      if (isfree[x2][y2] && isfree[x3][y3]) {
	isfree[x1][y1]=isfree[x2][y2]=isfree[x3][y3]=0;
	go(dep+1);
	isfree[x1][y1]=isfree[x2][y2]=isfree[x3][y3]=1;
      }
    }
}

int main () {

  int runs;
  cin >> runs;

  while (runs--) {
    
    cin >> N;
    vector<int> x(N), y(N);
    for (int i=0; i<N; i++) {
      int tmp;
      cin >> tmp;
      x[i] = 3*(tmp/100);
      y[i] = 3*(tmp%100/10);
    }
      
    for (int i=0; i<M; i++)
      for (int j=0; j<M; j++)
	isfree[i][j]=0;

    num=0;
    
    for (int i=0; i<27; i++)
      for (int j=0; j<27; j++) {
	if (i%3==0 || j%3==0 || (i+j)%3==0) continue;

	int sect=0;
	for (int k=0; k<N; k++)
	  if (isok(i,j,x[k],y[k],x[(k+1)%N],y[(k+1)%N])) sect++;

	if (sect%2==1) {
	  isfree[i/3*2+i%3-1+2][j/3+2]=1;
	  num++;
	}
      }
    
    cnt=0;
    if (num%3==0) go(0);
    cout << cnt << endl;
  }
  
  return 0;
}
