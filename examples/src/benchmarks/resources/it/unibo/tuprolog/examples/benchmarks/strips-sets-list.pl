% Sets as lists, [] being the empty one: adapted from https://github.com/unibo-fc-isi-ise/code-strips (Utils.pl)

set_of(List, List).

set_in(X, [Y | Ys]) :- member(X, [Y | Ys]).

set_member(E, [E | Xs], [], Xs).
set_member(E, [X | Xs], [X | L], R) :- set_member(E, Xs, L, R).
set_member(E, Xs, Xs1) :- set_member(E, Xs, L, R), append(L, R, Xs1).

set_union([], [], []) :- !.
set_union(S, [], S) :- !.
set_union([], S, S) :- !.
set_union(S1, S2, [X | S3]) :-
    set_member(X, S1, S1_2),
    set_member(X, S2, S2_2), !,
    set_union(S1_2, S2_2, S3).
set_union(S1, S2, [X | S3]) :-
    set_member(X, S1, S1_2), !,
    set_union(S1_2, S2, S3).

set_difference(S, [], S) :- !.
set_difference([], _, []) :- !.
set_difference(S1, S2, S3) :-
    set_member(X, S2, S2_2),
    set_member(X, S1, S1_2), !,
    set_difference(S1_2, S2_2, S3).
set_difference(S1, S2, S3) :-
    set_member(_, S2, S2_2), !,
    set_difference(S1, S2_2, S3).

setrel([], [_ | _], neq) :- !.
setrel([], [_ | _], subset) :- !.
setrel([], [_ | _], subseteq) :- !.
setrel([], [], subseteq) :- !.
setrel([], [], eq) :- !.
setrel([], [], superseteq) :- !.
setrel([_ | _], [], neq) :- !.
setrel([_ | _], [], superset) :- !.
setrel([_ | _], [], superseteq) :- !.
setrel(S1, S2, Rel) :-
    set_member(X, S1, S1_2), !,
    set_member(X, S2, S2_2),
    setrel(S1_2, S2_2, Rel).
setrel(S1, S2, Rel) :-
    set_member(X, S2, S2_2),
    set_member(X, S1, S1_2),
    setrel(S1_2, S2_2, Rel).
setrel(_, _, neq) :- !.

subseteq(S1, S2) :- setrel(S1, S2, subseteq).
