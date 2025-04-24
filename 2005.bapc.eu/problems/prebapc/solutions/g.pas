(*
 * Nim3
 *
 * Descripton: Solution for the Nim/3 Task (DAPC/BAPC 2005)
 * Author:     Bram Kuijvenhoven
 *
 *)

program nim3;

{$MODE OBJFPC}{$H+}

uses SysUtils;

const
  MAX_N = 20;
  UNKNOWN = -1;
  INVALID = -2;

const
  nextplayer:array[0..2] of integer = (1,2,0); // next player
  prevplayer:array[0..2] of integer = (2,0,1); // previous player

type
  TFavorites = array[0..2] of integer;
  TOptimalInfo = record
    winner,stack,count:integer;
  end;

var
  // useful precalced array
  nextfav:array[0..2,0..2,0..2] of TFavorites;
  // dp table with indices f1,f2,f3,s1,s2,s3
  table:array[0..2,0..2,0..2,0..MAX_N,0..MAX_N,0..MAX_N] of TOptimalInfo;
  
function CalcWinner(const favorite:TFavorites; a,b,c:integer):integer;
  var
    w,best,i:integer;
    st,co:integer;
  begin
    // fetch from buffer iff possible
    Result:=table[favorite[0],favorite[1],favorite[2],a,b,c].winner;
    if Result<>UNKNOWN then exit;

    best:=3-favorite[0]; // the non-preferred other player
    // default action: take 1 match from first non-zero stack
    if a>0 then st:=1 else if b>0 then st:=2 else st:=3;
    co:=1;

    // calculate it
    for i:=a-1 downto 0 do
      begin
        w:=nextplayer[CalcWinner(nextfav[favorite[0],favorite[1],favorite[2]],i,b,c)];
        if w=0 then
          begin
            if best<>w then begin st:=1; co:=a-i; best:=w; end;
            Break;
          end
        else if w=favorite[0] then
          begin
            if best<>w then begin st:=1; co:=a-i; best:=w; end;
          end;
      end;

    if best<>0 then
      for i:=b-1 downto 0 do
        begin
          w:=nextplayer[CalcWinner(nextfav[favorite[0],favorite[1],favorite[2]],a,i,c)];
          if w=0 then
            begin
              if best<>w then begin st:=2; co:=b-i; best:=w; end;
              Break;
            end
          else if w=favorite[0] then
            begin
              if best<>w then begin st:=2; co:=b-i; best:=w; end;
            end;
        end;

    if best<>0 then
      for i:=c-1 downto 0 do
        begin
          w:=nextplayer[CalcWinner(nextfav[favorite[0],favorite[1],favorite[2]],a,b,i)];
          if w=0 then
            begin
              if best<>w then begin st:=3; co:=c-i; best:=w; end;
              Break;
            end
          else if w=favorite[0] then
            begin
              if best<>w then begin st:=3; co:=c-i; best:=w; end;
            end;
        end;

    with table[favorite[0],favorite[1],favorite[2],a,b,c] do
      begin
        winner:=best;
        stack:=st;
        count:=co;
      end;

    Result:=best;
  end;

procedure InitTable;
  var
    f1,f2,f3,a,b,c:integer;
  begin
    for f1:=0 to 2 do
      for f2:=0 to 2 do
        for f3:=0 to 2 do
          begin
            for a:=0 to MAX_N do
              for b:=0 to MAX_N do
                for c:=0 to MAX_N do
                  with table[f1,f2,f3,a,b,c] do
                    begin
                      winner:=UNKNOWN;
                      stack:=UNKNOWN;
                      count:=UNKNOWN;
                    end;
            for a:=1 to MAX_N do
              begin
                with table[f1,f2,f3,a,0,0] do begin winner:=0; stack:=1; count:=a; end;
                with table[f1,f2,f3,0,a,0] do begin winner:=0; stack:=2; count:=a; end;
                with table[f1,f2,f3,0,0,a] do begin winner:=0; stack:=3; count:=a; end;
              end;
            with table[f1,f2,f3,0,0,0] do begin winner:=INVALID; stack:=INVALID; count:=INVALID; end;
            nextfav[f1,f2,f3,0]:=prevplayer[f2];
            nextfav[f1,f2,f3,1]:=prevplayer[f3];
            nextfav[f1,f2,f3,2]:=prevplayer[f1];
          end;
  end;

var
  numTestCases,testCase:integer;
  s1,s2,s3:integer;
  f:TFavorites;

begin

  InitTable;
  readln(numTestCases);
  for testCase:=1 to numTestCases do
    begin
      readln(s1,s2,s3,f[0],f[1],f[2]);
      // convert from 1-based player numbering to 0-based player numbering
      Dec(f[0]);
      Dec(f[1]);
      Dec(f[2]);
      // calc & write solution
      CalcWinner(f,s1,s2,s3);
      with table[f[0],f[1],f[2],s1,s2,s3] do
        writeln(stack,' ',count);
    end;

end.
