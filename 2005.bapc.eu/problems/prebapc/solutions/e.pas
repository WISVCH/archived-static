(*
 * Minotaur
 *
 * Descripton: Example Solution for the Minotaur Task (DAPC/BAPC 2005)
 * Author:     Bram Kuijvenhoven
 *
 *)

program minotaur;

{$mode objfpc}{$H+}
{$DEFINE USEHASHTABLE}

uses
  Classes, SysUtils, Getopts;

var
  Debug:boolean;
  
{$IFDEF USEHASHTABLE}
const
  MAX_B = 50;
  MAX_H = 50;
{$ELSE}
const
  MAX_B = 20;
  MAX_H = 20;
{$ENDIF}

type
  TDirection = (diWest, diEast, diNorth, diSouth, diNone);
  TPosition = record
    X,Y:integer;
  end;
  
const
  DeltaX:array[diWest..diNone] of integer = (-1,1,0,0,0); // west/east
  DeltaY:array[diWest..diNone] of integer = (0,0,-1,1,0); // north/south

var
  numTestCases,testCase:integer;
  b,h:integer;
  square:array[0..MAX_B+1,0..MAX_H+1] of char;
  
// how is Pos2 related to Pos1?
function IsAtDirection(const Pos1,Pos2:TPosition; direction:TDirection):boolean;
  begin
    case direction of
      diWest: Exit(Pos1.X>Pos2.X);
      diEast: Exit(Pos1.X<Pos2.X);
      diNorth:Exit(Pos1.Y>Pos2.Y);
      diSouth:Exit(Pos1.Y<Pos2.Y);
    else
      raise Exception.CreateFmt('IsAtDirection called with invalid direction parameter (probably diNone); Pos1 = (%d, %d); Pos = (%d, %d)',[Pos1.X,Pos1.Y,Pos2.X,Pos2.Y]);
    end
  end;
  
function MinoDirection(const MinoPos,TheseusPos:TPosition):TDirection;
  var
    dir:TDirection;
  begin
    for dir:=diWest to diSouth do
      if (Square[MinoPos.X+DeltaX[dir],MinoPos.Y+DeltaY[dir]]<>'#') and IsAtDirection(MinoPos,TheseusPos,dir) then Exit(dir);
    Result:=diNone;
  end;
  
operator = (const Pos1,Pos2:TPosition):boolean;
  begin
    Result:=(Pos1.X=Pos2.X) and (Pos1.Y=Pos2.Y);
  end;
  
// BEGIN Queue code
type
  PNode = ^TNode;
  TNode = record
    Turn:integer; // Theseus turn number
    TheseusPos,MinoPos:TPosition;
    Next:PNode;
  end;
  
var
  Head,Tail:PNode; // new nodes are appended to the tail; old nodes are removed from the head; Next points to the Tail
  
// client must allocate the node
procedure Enqueue(Node:PNode);
  begin
    Node^.Next:=nil;
    if Tail<>nil then Tail^.Next:=Node;
    Tail:=Node;
    if Head=nil then Head:=Node;
  end;
  
// client must free the retrieved node
function Dequeue:PNode;
  begin
    if Head=nil then raise Exception.Create('Calling Dequeue() while queue is empty');
    Result:=Head;
    Head:=Head^.Next;
    if Head=nil then Tail:=nil;
  end;
  
function QueueIsEmpty:boolean;
  begin
    Result:=Head=nil;
  end;
  
procedure ClearQueue;
  var
    Node:PNode;
  begin
    while not QueueIsEmpty do
      begin
        Node:=Dequeue;
        dispose(Node);
      end;
  end;
  
procedure InitQueue;
  begin
    Head:=nil;
    Tail:=nil;
  end;

// END Queue code

// BEGIN DP table code

{$IFDEF USEHASHTABLE}
type
  PHashNode = ^THashNode;
  THashNode = record
    MinoPos,TheseusPos:TPosition;
    Next:PHashNode;
  end;

const
  HashTableSize = 2000003; // is prime
  
var
  Table:array[0..HashTableSize-1] of PHashNode;
  
procedure InitSeen;
  var
    i:integer;
  begin
    for i:=0 to HashTableSize-1 do
      Table[i]:=nil;
  end;

procedure ClearSeen;
  var
    Node,BackupNode:PHashNode;
    i:integer;
  begin
    for i:=0 to HashTableSize-1 do
      begin
        Node:=Table[i];
        Table[i]:=nil;
        while Node<>nil do
          begin
            BackupNode:=Node;
            Node:=Node^.Next;
            dispose(BackupNode);
          end;
      end;
  end;
  
function Hash(const MinoPos,TheseusPos:TPosition):integer;
  begin
    Result:=(MinoPos.X+31*MinoPos.Y+1005*TheseusPos.X+30011*TheseusPos.Y) mod HashTableSize;
  end;
  
function AlreadySeen(const MinoPos,TheseusPos:TPosition):boolean;
  var
    HashIndex:integer;
    Node:PHashNode;
  begin
    HashIndex:=Hash(MinoPos,TheseusPos);
    Node:=Table[HashIndex];
    while Node<>nil do
      begin
        if (Node^.MinoPos=MinoPos) and (Node^.TheseusPos=TheseusPos) then
          Exit(true);
        Node:=Node^.Next;
      end;
    Result:=false;
  end;

procedure MarkAsSeen(const MinoPos,TheseusPos:TPosition);
  var
    HashIndex:integer;
    Node:PHashNode;
  begin
    HashIndex:=Hash(MinoPos,TheseusPos);
    new(Node);
    Node^.MinoPos:=MinoPos;
    Node^.TheseusPos:=TheseusPos;
    Node^.Next:=Table[HashIndex];
    Table[HashIndex]:=Node;
  end;
{$ELSE}
var
  Table:array[1..MAX_B,1..MAX_H,1..MAX_B,1..MAX_H] of ByteBool;
  
procedure ClearSeen;
  var
    i,j,k,l:integer;
  begin
    // init entire table to false
    for i:=1 to b do for j:=1 to h do for k:=1 to b do for l:=1 to h do
      Table[i,j,k,l]:=false;
  end;
  
function AlreadySeen(const MinoPos,TheseusPos:TPosition):boolean;
  begin
    Result:=Table[MinoPos.X, MinoPos.Y, TheseusPos.X, TheseusPos.Y];
  end;

procedure MarkAsSeen(const MinoPos,TheseusPos:TPosition);
  begin
    Table[MinoPos.X, MinoPos.Y, TheseusPos.X, TheseusPos.Y]:=true;
  end;

procedure InitSeen;
  begin
    // do nothing
  end;
{$ENDIF}

// END DP table code

var
  Theseus, Mino, TheExit:TPosition;
  i,j:integer;
  Node,NewNode:PNode;
  Dir:TDirection;
  NewTheseusPos:TPosition;
  Solution:integer;
  c:char;

begin

  Debug:=false;

  // handle commandline options
  repeat
    c:=GetOpt('d');
    case c of
      'd':debug:=true;
    end;
  until c=EndOfOptions;

  InitQueue;
  InitSeen;

  readln(numTestCases);
  for testCase:=1 to numTestCases do
    begin
      // read maze
      readln(b,h);
      Theseus.X:=0; Mino.X:=0; TheExit.X:=0;
      for i:=1 to h do
        begin
          for j:=1 to b do
            begin
              read(square[j,i]);
              case square[j,i] of
                'T':begin
                      if Theseus.X>0 then
                        raise Exception.CreateFmt('Duplicate Theseus found at (%d, %d) and (%d, %d)',[Theseus.X, Theseus.Y, j, i]);
                      Theseus.X:=j; Theseus.Y:=i;
                    end;
                'M':begin
                      if Mino.X>0 then
                        raise Exception.CreateFmt('Duplicate Minotaur found at (%d, %d) and (%d, %d)',[Mino.X, Mino.Y, j, i]);
                      Mino.X:=j;    Mino.Y:=i;
                    end;
                'X':begin
                      if TheExit.X>0 then
                        raise Exception.CreateFmt('Duplicate Exit found at (%d, %d) and (%d, %d)',[TheExit.X, TheExit.Y, j, i]);
                      TheExit.X:=j; TheExit.Y:=i;
                    end;
                '.':begin end;
                '#':begin end;
              else
                raise Exception.CreateFmt('Invalid maze character %c found at (%d, %d)',[square[j,i],j,i]);
              end;
            end;
          readln;
        end;

      // init border to all #
      for i:=1 to b do
        begin
          square[i,0]:='#';
          square[i,h+1]:='#';
        end;
      for i:=0 to h+1 do
        begin
          square[0,i]:='#';
          square[b+1,i]:='#';
        end;
        
      if Debug then
        begin
          for i:=0 to h+1 do
            begin
              for j:=0 to b+1 do
                write(square[j,i]);
              writeln;
            end;
        end;
        
      // Solve test case
      ClearSeen;
      Solution:=0;
      new(Node);
      Node^.Turn:=0;
      Node^.MinoPos:=Mino;
      Node^.TheseusPos:=Theseus;
      Enqueue(Node);
      while (Solution=0) and not QueueIsEmpty do
        begin
          Node:=Dequeue;
          
          if Debug then with Node^ do
            writeln(Format('Processing Turn: %d; Minotaur: (%d, %d); Theseus: (%d, %d)',[Turn, MinoPos.X, MinoPos.Y, TheseusPos.X, TheseusPos.Y]));
          
          // do the two Minotaur moves
          for i:=0 to 1 do
            begin
              Dir:=MinoDirection(Node^.MinoPos,Node^.TheseusPos);
              Inc(Node^.MinoPos.X,DeltaX[Dir]);
              Inc(Node^.MinoPos.Y,DeltaY[Dir]);
            end;
            
          // Try Theseus moves
          if Node^.MinoPos<>Node^.TheseusPos then
            begin // Theseus survived until now
              for Dir:=diWest to diNone do
                begin
                  NewTheseusPos.X:=Node^.TheseusPos.X+DeltaX[Dir];
                  NewTheseusPos.Y:=Node^.TheseusPos.Y+DeltaY[Dir];
                  if square[NewTheseusPos.X,NewTheseusPos.Y]<>'#' then // square is no Wall
                    if NewTheseusPos<>Node^.MinoPos then // no Minotaur on square
                      begin
                        if NewTheseusPos=TheExit then
                          begin // Theseus gets to the exit
                            Solution:=Node^.Turn+1;
                            Break; // break out of Dir-loop
                          end;
                        if AlreadySeen(Node^.MinoPos,NewTheseusPos) then
                          Continue // skip this NewTheseusPos
                        else
                          MarkAsSeen(Node^.MinoPos,NewTheseusPos);
                        // add to queue
                        new(NewNode);
                        NewNode^.Turn:=Node^.Turn+1;
                        NewNode^.MinoPos:=Node^.MinoPos;
                        NewNode^.TheseusPos:=NewTheseusPos;
                        Enqueue(NewNode);
                      end;
                end;
            end;

          dispose(Node);
        end;
        
      ClearQueue;

      writeln(Solution);
    end;

  ClearSeen;

end.

