(*
 * Mandala
 *
 * Descripton: Example Solution for the Mandala Task (DAPC/BAPC 2005)
 * Author:     Bram Kuijvenhoven
 *
 *)

program mandala;

{$mode objfpc}{$H+}

{ $DEFINE DEBUG}
{$DEFINE XFIG}

uses
  Classes, SysUtils
  {$IFDEF XFIG}, xfig, math{$ENDIF}
  { add your units here };
  
const
  MAX_CIRCLES = 500;
  
var
  // circle data
  numCircles:integer;
  circleX,circleY,circleR:array[1..MAX_CIRCLES] of integer;
  // coefficients calculated for circle circle-intersections
  bigA,bigB,bigC:array[1..MAX_CIRCLES,1..MAX_CIRCLES] of int64;
  bigBisSquare:array[1..MAX_CIRCLES,1..MAX_CIRCLES] of boolean;
  // circle-circle intersection info
  numIntersects:array[1..MAX_CIRCLES] of integer; // numIntersects[i] is the number of entries in intersects[i]
//  intersects:array[1..MAX_CIRCLES,1..MAX_CIRCLES] of integer; // intersects[i] lists circles circle i intersects with
  // subgraph number
  subGraph:array[1..MAX_CIRCLES] of integer;
  // duplicate info
  numDuplicates:integer;
  duplicate:array[1..MAX_CIRCLES] of boolean;
  
{$IFDEF XFIG}
procedure WriteXFig(filename:string);
  var
    MyXFig: TXFig;
    MyEllipse: TXFigEllipse;
    i:integer;
    MinX, MinY, MaxX, MaxY: Integer;
  const
    ColorTbl:array[0..6] of integer = (clBlack, clBlue, clGreen, clCyan, clRed, clMagenta, clYellow);
  begin
    MyXFig:=TXFig.Create;
    MinX:=High(MinX);
    MinY:=High(MinY);
    MaxX:=Low(MaxX);
    MaxY:=Low(MaxY);
    for i:=1 to numCircles do
      begin
        MinX:=Min(MinX,circleX[i]-circleR[i]);
        MinY:=Min(MinY,circleY[i]-circleR[i]);
        MaxX:=Max(MaxX,circleX[i]+circleR[i]);
        MaxY:=Max(MaxY,circleY[i]+circleR[i]);
      end;
    for i:=1 to numCircles do
      begin
        MyEllipse:=TXFigEllipse.Create(estCircleByRadius);
        MyEllipse.CenterX:=1200+(circleX[i]-MinX)*15;
        MyEllipse.CenterY:=1200+(MaxY-circleY[i])*15;
        MyEllipse.RadiusX:=circleR[i]*15;
        MyEllipse.PenColor:=ColorTbl[(subGraph[i]-1) mod Length(ColorTbl)];
        MyXFig.AddObject(MyEllipse);
      end;
    MyXFig.WriteToFile(filename);
    MyXFig.Free;
  end;
{$ENDIF}
  
function IsSquare(num:int64; var root:int64):boolean;
  begin
    root:=round(sqrt(num));
    if root*root<num then
      begin
        repeat
          Inc(root);
          if root*root=num then Exit(true);
        until root*root>num;
      end
    else if root*root>num then
      begin
        repeat
          Dec(root);
          if root*root=num then Exit(true);
        until root*root<num;
      end
    else
      Exit(true);
    Result:=false;
  end;
  
procedure TestIsSquare;
  var
    i: Integer;
    root: int64;
  begin
    for i:=1 to 100 do
      writeln(Format('%3d: %s, root: %d',[i,BoolToStr(IsSquare(i,root)),root]));
  end;

procedure FillSubGraph(i,S:integer);
  var
    j:integer;
  begin
    subGraph[i]:=S;
    for j:=1 to numCircles do
      if subGraph[j]=0 then
        if bigB[i,j]>=0 then
          FillSubGraph(j,S);
  end;

function Solve:integer;
  var
    i,j,k:integer;
    a,b,r1,r2,r3,c,d:int64;
    Shift:integer;
    V,E,R,S:integer; // respectively the number of vertices, edges, regions, subgraphs
    BIsSquare: Boolean;
    root:int64;
    Unique, NewForCircleI: Boolean;
    {$IFDEF DEBUG}subGraphTexts:array of string;{$ENDIF}

  begin
    // read testcase from stdin
    readln(numCircles);
    for i:=1 to numCircles do
      readln(circleX[i],circleY[i],circleR[i]);
      
    // solve
    // initialise
    for i:=1 to numCircles do
      begin
        numIntersects[i]:=0;
        for j:=1 to numCircles do
          begin
            bigBisSquare[i,j]:=false;
            bigB[i,j]:=-1;
          end;
        subGraph[i]:=0;
        duplicate[i]:=false;
      end;
    V:=0; E:=0; R:=0; S:=0;
    // remove duplicates
    //   mark duplicates
    numDuplicates:=0;
    for i:=1 to numCircles do
      for j:=1 to i-1 do
        begin
          if (circleX[i]=circleX[j]) and (circleY[i]=circleY[j]) and(circleR[i]=circleR[j]) then
            begin // duplicate circle i and circle j
              duplicate[i]:=true;
              Inc(numDuplicates);
              Break;
            end;
        end;
    //   throw away duplicates
    Shift:=0;
    for i:=1 to numCircles do
      if duplicate[i] then
        Inc(Shift)
      else
        begin
          circleX[i-Shift]:=circleX[i];
          circleY[i-Shift]:=circleY[i];
          circleR[i-Shift]:=circleR[i];
        end;
{$IFDEF DEBUG} writeln(Format('%d duplicates out of %d circles: new numCircles is %d',[numDuplicates,numCircles,numCircles-numDuplicates])); {$ENDIF}
    Dec(numCircles,numDuplicates);
{$IFDEF DEBUG}
    for i:=1 to numCircles do
      begin
        //writeln(Format('Circle %d: center (%d,%d), radius: %d',[i,circleX[i],circleY[i],circleR[i]]));
      end;
{$ENDIF}
    // determine intersections
    for i:=1 to numCircles do
      for j:=1 to i-1 do
        begin // note: we will have k < j < i
          a:=circleX[j]-circleX[i];
          b:=circleY[j]-circleY[i];
          r1:=circleR[i];
          r2:=circleR[j];
          bigC[j,i]:=a*a+b*b;
          if bigC[j,i]=0 then
            begin // same center; cannot intersect because duplicates have already been removed
              Continue;
            end;
          bigA[j,i]:=bigC[j,i]+r1*r1-r2*r2;
          bigB[j,i]:=-(bigC[j,i]-(r1+r2)*(r1+r2))*(bigC[j,i]-(r1-r2)*(r1-r2));
          bigB[i,j]:=bigB[j,i];
          if bigB[j,i]=0 then
            begin // 1 intersection point
              // check for unicity
              Unique:=true;
              for k:=1 to i-1 do
                if k<>j then
                  begin // check whether circle k already intersects our intersection point
                    c:=circleX[k]-circleX[i];
                    d:=circleY[k]-circleY[i];
                    r3:=circleR[k];
                    if (bigA[j,i]*bigA[j,i])-4*(bigA[j,i]*(c*a+d*b))=4*bigC[j,i]*(r3*r3-c*c-d*d) then
                      begin
                        Unique:=false;
                        NewForCircleI:=j<k;
                        Break;
                      end;
                  end;
              // add to intersections, update V
              if Unique then
                begin
                  Inc(numIntersects[j]);
                  Inc(V);
//{$IFDEF DEBUG}    writeln(Format('Unique vertex at only intersection of %d and %d',[i,j])); {$ENDIF}
                end
              else
                begin
//{$IFDEF DEBUG}    writeln(Format('Non-unique vertex at only intersection of %d and %d',[i,j])); {$ENDIF}
                end;
              if Unique or NewForCircleI then
                Inc(numIntersects[i]);
            end
          else if bigB[j,i]>0 then
            begin // 2 intersection point
              BIsSquare:=IsSquare(bigB[j,i],root);
              // check for unicity of first intersection point
              Unique:=true;
              for k:=1 to i-1 do
                if k<>j then
                  begin // check whether circle k already intersects our intersection point
                    c:=circleX[k]-circleX[i];
                    d:=circleY[k]-circleY[i];
                    r3:=circleR[k];
                    if BIsSquare or (c*b-d*a=0) then
                      begin
                        if (bigA[j,i]*bigA[j,i]+bigB[j,i])-4*(bigA[j,i]*(c*a+d*b)+root*(c*b-d*a))=4*bigC[j,i]*(r3*r3-c*c-d*d) then
                          begin
                            Unique:=false;
                            NewForCircleI:=j<k;
                            Break;
                          end;
                      end;
                  end;
              if Unique then
                begin
                  Inc(numIntersects[j]);
                  Inc(V);
//{$IFDEF DEBUG}    writeln(Format('Unique vertex at first intersection of %d and %d',[i,j])); {$ENDIF}
                end
              else
                begin
//{$IFDEF DEBUG}    writeln(Format('Non-unique vertex at first intersection of %d and %d',[i,j])); {$ENDIF}
                end;
              if Unique or NewForCircleI then
                Inc(numIntersects[i]);
              // check for unicity of second intersection point
              Unique:=true;
              for k:=1 to i-1 do
                if k<>j then
                  begin // check whether circle k already intersects our intersection point
                    c:=circleX[k]-circleX[i];
                    d:=circleY[k]-circleY[i];
                    r3:=circleR[k];
                    if BIsSquare or (c*b-d*a=0) then
                      begin
                        if (bigA[j,i]*bigA[j,i]+bigB[j,i])-4*(bigA[j,i]*(c*a+d*b)-root*(c*b-d*a))=4*bigC[j,i]*(r3*r3-c*c-d*d) then
                          begin
                            Unique:=false;
                            NewForCircleI:=j<k;
                            Break;
                          end;
                      end;
                  end;
              if Unique then
                begin
                  Inc(numIntersects[j]);
                  Inc(V);
//{$IFDEF DEBUG}    writeln(Format('Unique vertex at second intersection of %d and %d',[i,j])); {$ENDIF}
                end
              else
                begin
//{$IFDEF DEBUG}    writeln(Format('Non-unique vertex at second intersection of %d and %d',[i,j])); {$ENDIF}
                end;
              if Unique or NewForCircleI then
                Inc(numIntersects[i]);
            end
          else
            begin
//{$IFDEF DEBUG}writeln(Format('No intersections between %d and %d',[i,j]));{$ENDIF}
            end;
        end;
      // determine E
      for i:=1 to numCircles do
        if numIntersects[i]=0 then
          begin
            Inc(E,1);
            Inc(V,1);
          end
        else
          Inc(E,numIntersects[i]);
      // determine S
      for i:=1 to numCircles do
        if subGraph[i]=0 then
          begin
            Inc(S);
            FillSubGraph(i,S);
          end;
{$IFDEF DEBUG}
      SetLength(subGraphTexts,S);
      for i:=1 to S do
        subGraphTexts[i-1]:=Format('SubGraph %2d:',[i]);
      for i:=1 to numCircles do
        subGraphTexts[subGraph[i]-1]+=Format(' %d',[i]);
      for i:=1 to S do
        writeln(subGraphTexts[i-1]);
{$ENDIF}
      // calculate R
      R:=1+S+E-V;
      Result:=R;
{$IFDEF DEBUG}writeln(Format('V: %d,  E: %d,  R: %d,  S: %d',[V,E,R,S]));{$ENDIF}
  end;

var
  numCases,i:integer;

begin

//  TestIsSquare;

  readln(numCases);
  for i:=1 to numCases do
    begin
{$IFDEF DEBUG} writeln(Format('=== Test Case %d ===',[i])); {$ENDIF}
      writeln(Solve);
{$IFDEF XFIG}
      WriteXFig(Format('testcase%d.fig',[i]));
{$ENDIF}
    end;
    
end.

