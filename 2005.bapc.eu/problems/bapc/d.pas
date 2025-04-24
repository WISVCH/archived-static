(*
 * Span
 *
 * Descripton: Example Solution for the Minotaur Task (DAPC/BAPC 2005)
 * Author:     Bram Kuijvenhoven
 *
 *)

program span;

{$mode objfpc}{$H+}
{ $DEFINE DEBUG}

uses
  sysutils,math;

type
  TVector = record
    x,y,z:int64;
  end;
  
function cross(const a,b:TVector):TVector;
  begin
    Result.x:=a.y*b.z-a.z*b.y;
    Result.y:=a.z*b.x-a.x*b.z;
    Result.z:=a.x*b.y-a.y*b.x;
  end;
  
operator -(const a,b:TVector):TVector;
  begin
    Result.x:=a.x-b.x;
    Result.y:=a.y-b.y;
    Result.z:=a.z-b.z;
  end;
  
operator =(const a,b:TVector):boolean;
  begin
    Result:=(a.x=b.x) and (a.y=b.y) and (a.z=b.z);
  end;
  
// inner product
operator *(const a,b:TVector):int64;
  begin
    Result:=a.x*b.x+a.y*b.y+a.z*b.z;
  end;
  
const
  zero:TVector = (x:0; y:0; z:0);
  
function distinct(const a,b,c:TVector):boolean;
  begin
    Result:=(a<>b) and (b<>c) and (a<>c);
  end;
  
function colinear(const a,b,c:TVector):boolean;
  begin
    Result:=cross(a-b,c-b)=zero;
  end;
  
// spatial: on which side of triangle a,b,c is p?
function side(const a,b,c,p:TVector):int64;
  begin
    Result:=sign(cross(b-a,c-a)*(p-a));
  end;
  
// flat: is p on the same side of the line a,b as c? (a,b,c,p should be in the same plane)
function sameside(const a,b,c,p:TVector):int64;
  begin
    Result:=sign(cross(b-a,cross(c-a,b-a))*(p-a));
  end;
  
function sqrlength(const a:TVector):int64;
  begin
    Result:=a.x*a.x+a.y*a.y+a.z*a.z;
  end;

function length(const a:TVector):double;
  begin
    Result:=sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
  end;
  
const
  MAX_N = 400;
  MAX_P = 4*MAX_N+4;
  
(*
 * A few notes on orientation etc.:
 *  1) we store all triangles of the convex hull in clockwise order (when seen from outside the hull)
 *  2) the sideSeen array is not cummutative as sideSeen[a,b] and sideSeen[b,a] indicate whether we've seen the side
 *     between point a and b fom a triangle a,b,c or a triangle b,a,c (there always are two triangles that touch an edge/side)
 *  3) the TTriple.c point in the triQueue are actually never used, but are stored for optional debug/consistency checks now
 *)
  
type
  TTriple = record
    a,b,c:integer;
  end;
  
  TPointState = (psUnseen, psHull);

var
  point:array[1..MAX_P] of TVector; // all (unique) points in the input; the first four will be the vertices of the campus itself
  numPoints:integer;
  pointState:array[1..MAX_P] of TPointState;
  sideSeen:array[1..MAX_P,1..MAX_P] of integer; // see note 2) above
  triQueue:array[1..1000000] of TTriple; // indices in the point array; see also note 1) and 3) above
  triQueueStart,triQueueEnd:integer;
  
procedure AddPoint(x,y,z:integer); // operates on point/numPoints
  var
    i:integer;
  begin
    point[numPoints+1].x:=x;
    point[numPoints+1].y:=y;
    point[numPoints+1].z:=z;
    for i:=1 to numPoints do
      if point[i]=point[numPoints+1] then
        Exit; // don't add any point already in numPoints
    Inc(numPoints);
  end;
  
procedure EnqueueTriple(a,b,c:integer); // operates on triQueue*
  begin
    Inc(triQueueEnd);
    triQueue[triQueueEnd].a:=a;
    triQueue[triQueueEnd].b:=b;
    triQueue[triQueueEnd].c:=c;
  end;
  
function QueueEmpty:boolean; // operates on triQueue*
  begin
    Result:=triQueueStart>triQueueEnd;
  end;
  
function DequeueTriple:TTriple; // operates on triQueue*
  begin
    Result:=triQueue[triQueueStart];
    Inc(triQueueStart);
  end;
  
procedure Solve;
  var
    numTestCases,testCase:integer;
    a,b,c,d,h:int64;
    n,i,j:integer;
    best:integer;
    numTriangles,numSides,numHullPoints:integer;
    triple:TTriple;
    twiceTheArea:double; // does not include the area of the campus itself of course
    sure: Boolean;

  begin

    readln(numTestCases);
    for testCase:=1 to numTestCases do
      begin
        numPoints:=0;

        readln(a,b,c,d);
        AddPoint(a,b,0);
        AddPoint(a,d,0);
        AddPoint(c,b,0);
        AddPoint(c,d,0);

        readln(n);
        for i:=1 to n do
          begin
            readln(a,b,c,d,h);
            AddPoint(a,b,h);
            AddPoint(a,d,h);
            AddPoint(c,b,h);
            AddPoint(c,d,h);
          end;
          
        triQueueStart:=1;
        triQueueEnd:=0;
        for i:=1 to numPoints do
          for j:=1 to numPoints do
            sideSeen[i,j]:=0;
        for i:=1 to numPoints do
          pointState[i]:=psUnseen;

        {$IFDEF DEBUG}writeln('numPoints: ',numPoints);{$ENDIF}

        EnqueueTriple(1,3,2);
        EnqueueTriple(3,4,1);
        EnqueueTriple(4,2,1);
        EnqueueTriple(2,1,3);
        sideSeen[1,3]:=4;
        sideSeen[3,4]:=3;
        sideSeen[4,2]:=2;
        sideSeen[2,1]:=1;
        pointState[1]:=psHull;
        pointState[2]:=psHull;
        pointState[3]:=psHull;
        pointState[4]:=psHull;

        twiceTheArea:=0;
        numTriangles:=2;

        while not QueueEmpty do
          begin
            triple:=DequeueTriple;

            {$IFDEF DEBUG}writeln('handling triangle between ',triple.a,', ',triple.b,', ',triple.c);{$ENDIF}

            if sideSeen[triple.b,triple.a]>0 then Continue; // side already seen from the other side

            best:=0;
            sure:=true;
            for i:=1 to numPoints do begin
              if (i<>triple.a) and (i<>triple.b) and (i<>triple.c) then begin
                if side(point[triple.a],point[triple.b],point[triple.c],point[i])=0 then // if point i is in the plane of points a,b,c
                  if sameSide(point[triple.a],point[triple.b],point[triple.c],point[i])>0 then // if point i is on the wrong side of the line a,b
                    Continue;
                if best=0 then
                  best:=i
                else
                  case side(point[triple.a],point[i],point[triple.b],point[best]) of
                    0:begin
                        Sure:=false;
                        if sideSeen[i,triple.a]>0 then begin
                          if sideSeen[best,triple.a]>0 then begin
                            if sameSide(point[triple.a],point[best],point[triple.b],point[i])>0 then // i.e., if i is to the 'right' of best
                              best:=i;
                          end else
                            best:=i;
                        end else begin
                          if sideSeen[best,triple.a]=0 then
                            case sameSide(point[triple.a],point[best],point[triple.b],point[i]) of
                              0:if sqrlength(point[i]-point[triple.a])>sqrlength(point[best]-point[triple.a]) then
                                  best:=i;
                              -1:best:=i; // i.e., if i is to the 'left' of best
                            end;
                        end;
                      end;
                    1:begin
                        Sure:=true;
                        best:=i;
                      end;
                  end;
              end;
            end;

            if best=0 then
              Continue;

            {$IFDEF DEBUG}writeln('  found triangle between ',triple.a,', ',best,', ',triple.b);{$ENDIF}
            
            // sanity check: are all points in the set on the right side?
            for i:=1 to numPoints do
              if side(point[triple.a],point[best],point[triple.b],point[i])<0 then
                writeln(Format('Sanity check failed: triangle %d, %d, %d found, but point %d is on wrong side',[triple.a,best,triple.b,i]));

            twiceTheArea+=length(cross(point[best]-point[triple.a],point[best]-point[triple.b]));
            Inc(numTriangles);
            if sideSeen[triple.a,best]>0     then writeln(Format('sideSeen[triple.a,best] = sideSeen[%d,%d] = %d < %d, %s',[triple.a,best,sideSeen[triple.a,best],numTriangles,BoolToStr(Sure)]));
            if sideSeen[best,triple.b]>0     then writeln(Format('sideSeen[best,triple.b] = sideSeen[%d,%d] = %d < %d, %s',[best,triple.b,sideSeen[best,triple.b],numTriangles,BoolToStr(Sure)]));
            if sideSeen[triple.b,triple.a]>0 then writeln(Format('sideSeen[triple.b,triple.a] = sideSeen[%d,%d] = %d < %d, %s',[triple.b,triple.a,sideSeen[triple.b,triple.a],numTriangles,BoolToStr(Sure)]));
            sideSeen[triple.a,best]:=numTriangles;
            sideSeen[best,triple.b]:=numTriangles;
            sideSeen[triple.b,triple.a]:=numTriangles;
            pointState[best]:=psHull;
            if sideSeen[best,triple.a]=0 then EnqueueTriple(triple.a,best,triple.b);
            if sideSeen[triple.b,best]=0 then EnqueueTriple(best,triple.b,triple.a);

            {$IFDEF DEBUG}writeln('  enqueued triangle between ',triple.a,', ',best,', ',triple.b);{$ENDIF}
            {$IFDEF DEBUG}writeln('  enqueded triangle between ',best,', ',triple.b,', ',triple.a);{$ENDIF}
          end;

        if n>0 then begin
          numSides:=0;
          for i:=1 to numPoints do
            for j:=1 to numPoints do
              if sideSeen[i,j]>0 then begin
                Inc(numSides);
                if sideSeen[j,i]=0 then writeln(Format('sideSeen[%d,%d] = %d but sideSeen[%d,%d] = %d',[i,j,sideSeen[i,j],j,i,sideSeen[j,i]]));
              end;
          if Odd(numSides) then writeln(Format('numSides = %d is odd',[numSides]));
          numSides:=numSides div 2;
          numSides+=1;

          numHullPoints:=0;
          for i:=1 to numPoints do
            if pointState[i]<>psUnseen then Inc(numHullPoints);

          if numTriangles+numHullPoints-numSides<>2 then writeln('Eulers formula failed');
          if 3*numTriangles<>2*numSides then writeln('Not balanced');
          {$IFDEF DEBUG}writeln(Format('F: %d; V: %d; E: %d; F+V-E: %d; 3F-2E: %d',[numTriangles,numHullPoints,numSides,numTriangles+numHullPoints-numSides,3*numTriangles-2*numSides]));{$ENDIF}
        end else
          twiceTheArea+=2*(c-a)*(d-b);

        if twiceTheArea>2*1200000000 then writeln('Area exceeded theoretical maximum!');
        writeln(twiceTheArea/2:0:4);
      end;

  end;
  
begin
  Solve;
end.

