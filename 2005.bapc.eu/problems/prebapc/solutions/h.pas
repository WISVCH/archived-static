(*
 * VenusRover
 *
 * Descripton: Example Solution for the VenusRover Task (DAPC/BAPC 2005)
 * Author:     Bram Kuijvenhoven
 *
 *)

program venusrover;

{$mode objfpc}{$H+}

uses
  Classes, SysUtils;

const
  MAX_N = 100;
  MAX_T = 100;
  MAX_M = 100;
  
type
  TStone = record
    t,m,v:integer;
  end;
  
var
  numTestCases,testCase:integer;
  n,t,m:integer;
  stone:array[1..MAX_N] of TStone;
  maxValue:array[0..MAX_T,0..MAX_M] of integer;
  i,j,k:integer;
  max:integer;

begin

  readln(numTestCases);
  for testCase:=1 to numTestCases do
    begin
      // read test case
      readln(n,t,m);
      for i:=1 to n do
        readln(stone[i].t,stone[i].m,stone[i].v);

      // init DP table
      for i:=0 to t do
        for j:=0 to m do
          maxValue[i,j]:=-1;
      maxValue[0,0]:=0;
      
      // fill DP table (2D knapsack algorithm)
      for i:=1 to n do
        begin
          for j:=t downto stone[i].t do
            for k:=m downto stone[i].m do
              if maxValue[j-stone[i].t,k-stone[i].m]>=0 then
                if maxValue[j,k]<maxValue[j-stone[i].t,k-stone[i].m]+stone[i].v then
                  maxValue[j,k]:=maxValue[j-stone[i].t,k-stone[i].m]+stone[i].v;
        end;
        
      // find maximum in DP table
      max:=0;
      for i:=0 to t do
        for j:=0 to m do
          if max<maxValue[i,j] then
            max:=maxValue[i,j];
            
      // output
      writeln(max);
    end;

end.

