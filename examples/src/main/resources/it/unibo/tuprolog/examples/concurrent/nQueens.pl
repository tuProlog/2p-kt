%%    queens(+N, -Queens) is nondet.
%
%	@param	Queens is a list of column numbers for placing the queens.
%	@author Richard A. O'Keefe (The Craft of Prolog)
%

queenCountSolution(NQueens, Count) :-
    findall(_, queens(NQueens, _), Bag),
    count(Bag, 0, Count).

count([],Res,Res).
count([_|T],C,Res) :-
    C1 is C+1,
    count(T,C1,Res).

length(List, N) :- length(List, 0, N).
length([_|T], C, N) :-
    C < N,
    C1 is C + 1,
    length(T, C1, N).
length([], N, N).

% length probably missing, implementation needed
queens(N, Queens) :-
    length(Queens, N),
	board(Queens, Board, 0, N, _, _),
	queens(Board, 0, Queens).

board([], [], N, N, _, _).
board([_|Queens], [Col-Vars|Board], Col0, N, [_|VR], VC) :-
	Col is Col0+1,
	functor(Vars, f, N),
	constraints(N, Vars, VR, VC),
	board(Queens, Board, Col, N, VR, [_|VC]).

constraints(N, Row, [R|Rs], [C|Cs]) :-
    N > 0,
	arg(N, Row, R-C),
	M is N-1,
	constraints(M, Row, Rs, Cs).
constraints(0, _, _, _).

sel(E, [E|T1], T1).
sel(E, [L1|T1], [L1|T2]) :- sel(E, T1, T2).

queens([], _, []).
queens([C|Cs], Row0, [Col|Solution]) :-
	Row is Row0+1,
	sel(Col-Vars, [C|Cs], Board),
	arg(Row, Vars, Row-Row),
	queens(Board, Row, Solution).

% ?- queens(8, Queens). count 92
