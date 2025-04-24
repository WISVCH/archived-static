(*
 * GeldTrans
 *
 * Descripton: Example Solution for the Geldtransport Task (DAPC/BAPC 2005)
 * Author:     Bram Kuijvenhoven
 *
 *)

program geldtrans;

{$mode objfpc}{$H+}
{ $DEFINE DEBUG}

uses
  Classes, SysUtils
  { add your units here };

const
  MAX_N = 100;
  MAX_P =  50;
  UNSET = -1;

type
  TSolutionElement = record
    p:double;      // chance of survival
    known:boolean; // node is known to be optimal at this moment
    {$IFDEF DEBUG}
    prev:integer;  // previous node in route
    post:boolean;  // a post has been placed at the road to previous node
    {$ENDIF}
  end;
  TSolution = array[1..MAX_N] of TSolutionElement;
  PSolution = ^TSolution;

var
  n,w,m,a,b:Integer;
  map:array[1..MAX_N,1..MAX_N] of double;

{$IFDEF DEBUG}
procedure PrintSolution(solution:PSolution);
  var
    c:integer;
  begin
    for c:=1 to n do
      writeln(Format('node %d: p: %.6f, prev: %d, post: ',[c,solution^[c].p,solution^[c].prev]),solution^[c].post);
  end;
{$ENDIF}

procedure Go;

var
  numCases,c:integer;
  s1,s2:integer;
  p:double;
  solution1,solution2:TSolution;
  current,next,temp:PSolution;
  i,j,k:integer;
  bestk:integer;
  bestp:double;
  overallbestp:double;

begin

  readln(numCases);
  for c:=1 to numCases do
    begin
      readln(n,w,m,a,b);
      
      for i:=1 to n do
        for j:=1 to n do
          map[i,j]:=UNSET;

      for i:=1 to w do
        begin
          readln(s1,s2,p);
          map[s1,s2]:=p;
          map[s2,s1]:=p;
        end;
        
      current:=@solution1;
      next:=@solution2;
      // clear & init current
      for i:=1 to n do
        begin
          current^[i].known:=false;
          current^[i].p:=-1;
          {$IFDEF DEBUG}
          current^[i].post:=false;
          current^[i].prev:=0;
          {$ENDIF}
        end;
      current^[a].p:=1;
      overallbestp:=-1;
      // iterate over number of police posts
      for i:=0 to m do
        begin
          // clear next
          for j:=1 to n do
            begin
              next^[j].known:=false;
              next^[j].p:=-1;
              {$IFDEF DEBUG}
              next^[j].post:=false;
              next^[j].prev:=0;
              {$ENDIF}
            end;
          // Dijkstra
          for j:=1 to n do
            begin
              // find largest p in current
              bestk:=0;
              bestp:=-1.0;
              for k:=1 to n do
                if not current^[k].known then
                  if current^[k].p>bestp then
                    begin
                      bestp:=current^[k].p;
                      bestk:=k;
                    end;
              // check whether a new reachable node could be found
              if bestk=0 then
                Break;
              // add node
              current^[bestk].known:=true;
              // update neighbours
              for k:=1 to n do
                if map[bestk,k]<>UNSET then
                  begin
                    // nodes in current
                    if not current^[k].known then
                      if bestp*(1-map[bestk,k])>current^[k].p then
                        begin
                          current^[k].p:=bestp*(1-map[bestk,k]);
                          {$IFDEF DEBUG}
                          current^[k].prev:=bestk;
                          current^[k].post:=false;
                          {$ENDIF}
                        end;
                    // nodes in next
                    if bestp*(1-map[bestk,k]/2)>next^[k].p then
                      begin
                        next^[k].p:=bestp*(1-map[bestk,k]/2);
                        {$IFDEF DEBUG}
                        next^[k].prev:=bestk;
                        next^[k].post:=true;
                        {$ENDIF}
                      end;
                  end;
            end;
          // update overallbestp
          if current^[b].p>overallbestp then
            overallbestp:=current^[b].p;
          // debug output
          {$IFDEF DEBUG}
          writeln('posts: ',i);
          PrintSolution(current);
          //PrintRoute(current);
          {$ENDIF}
          // swap current, next
          temp:=current;
          current:=next;
          next:=temp;
        end;

      SysUtils.DecimalSeparator:='.';
      writeln(Format('%.4f',[1-overallbestp]));
    end;
    
end;

begin

  Go;

end.

