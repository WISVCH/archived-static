program asm_bram;

{$mode objfpc}{$H+}

uses
  Classes, SysUtils;
  
const
  MIN_X = 1; MAX_X = 100;
  MIN_Y = 1; MAX_Y = 100;
  MIN_N = 1; MAX_N = 100;
  MIN_H = 3; MAX_H = 9;

var
  numTestCases,testCase:integer;
  x,y,n,h:integer;
  lattice,dropped:array[MIN_X..MAX_X, MIN_Y..MAX_Y] of integer;
  i,j:integer;
  c:char;
  s:string;
  xi,yi:integer;
  toppleCount,totalTopples:integer;

begin

  readln(numTestCases);
  for testCase:=1 to numTestCases do begin
    try
      // read & check input
      readln(y,x,n,h);
      if (x<MIN_X) or (x>MAX_X) then Exception.CreateFmt('x = %d is not the range %d..%d',[x,MIN_X,MAX_X]);
      if (y<MIN_Y) or (y>MAX_Y) then Exception.CreateFmt('y = %d is not the range %d..%d',[y,MIN_Y,MAX_Y]);
      if (n<MIN_N) or (n>MAX_N) then Exception.CreateFmt('n = %d is not the range %d..%d',[n,MIN_N,MAX_N]);
      if (h<MIN_H) or (h>MAX_H) then Exception.CreateFmt('h = %d is not the range %d..%d',[h,MIN_H,MAX_H]);
      for j:=1 to y do begin
        for i:=1 to x do begin
          read(c);
          if not (c in ['0'..'9']) then raise Exception.CreateFmt('Character %c in lattice discription, line %d, column %d (should be in the range ''0''..''9'')',[c,j,i]);
          lattice[i,j]:=Ord(c)-Ord('0');
          if lattice[i,j]>h then raise Exception.CreateFmt('Pile exceeds critical height: %d > %d, line %d, column %d',[lattice[i,j],h,j,i]);
          dropped[i,j]:=0; // inits dropped array
        end;
        readln(s);
        if s<>'' then raise Exception.CreateFmt('Trailing characters at end of lattice description, line %d: %s',[j,s]);
      end;
      for i:=1 to n do begin
        readln(yi,xi);
        if (xi<MIN_X) or (xi>x) then raise Exception.CreateFmt('x position of grain %d is %d, which is outside of the range %d..%d',[i,xi,MIN_X,x]);
        if (yi<MIN_Y) or (yi>y) then raise Exception.CreateFmt('y position of grain %d is %d, which is outside of the range %d..%d',[i,yi,MIN_Y,y]);
        Inc(dropped[xi,yi]);
      end;
      // run iterations
      repeat
        totalTopples:=0;
        //  add dropped to lattice, clear dropped
        for i:=1 to x do for j:=1 to y do begin
          Inc(lattice[i,j],dropped[i,j]);
          dropped[i,j]:=0;
        end;
        //  topple grains from lattice to dropped
        for i:=1 to x do for j:=1 to y do begin
          if lattice[i,j]>h then begin
            toppleCount:=(lattice[i,j]-h+3) div 4;
            if i>MIN_X then Inc(dropped[i-1,j],toppleCount);
            if i<x     then Inc(dropped[i+1,j],toppleCount);
            if j>MIN_Y then Inc(dropped[i,j-1],toppleCount);
            if j<y     then Inc(dropped[i,j+1],toppleCount);
            Dec(lattice[i,j],4*toppleCount);
            Inc(totalTopples,toppleCount);
          end;
        end;
      until totalTopples=0;
      // write solution
      for j:=1 to y do begin
        for i:=1 to x do
          write(Chr(Ord('0')+lattice[i,j]));
        writeln;
      end;
    except
      on E:Exception do begin
        raise Exception.CreateFmt('Error in test case %d: %s',[testCase, E.message]);
      end;
    end;
  end;
  if not Eof then raise Exception.CreateFmt('Not at end of file at end of testcases',[]);

end.

