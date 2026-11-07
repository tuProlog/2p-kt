% Sets as blocks, {X1, X2, ...}, {} being the empty one: every step rebuilds a block around the remaining items.
% Adapted from https://github.com/unibo-fc-isi-ise/code-strips (Utils.pl)

set_of([], {}).
set_of([X], {X}) :- !.
set_of([X | Xs], {(X, T)}) :- set_of(Xs, {T}).

set_cons(X, {}, {X}) :- !.
set_cons(X, {T}, {(X, T)}).

set_in(X, {(X, _)}).
set_in(X, {(_, Ys)}) :- set_in(X, {Ys}).
set_in(X, {X}) :- X \= (_, _).

set_member(E, {(E, Xs)}, {}, {Xs}).
set_member(E, {(X, Xs)}, L, R) :- set_member(E, {Xs}, L0, R), set_cons(X, L0, L).
set_member(E, {E}, {}, {}) :- E \= (_, _).
set_member(E, Xs, Xs1) :- set_member(E, Xs, L, R), set_append(L, R, Xs1).

set_append({}, R, R) :- !.
set_append({(X, L)}, R, S) :- !, set_append({L}, R, S0), set_cons(X, S0, S).
set_append({X}, R, S) :- set_cons(X, R, S).

set_union({}, {}, {}) :- !.
set_union(S, {}, S) :- !.
set_union({}, S, S) :- !.
set_union(S1, S2, S) :-
    set_member(X, S1, S1_2),
    set_member(X, S2, S2_2), !,
    set_union(S1_2, S2_2, S3),
    set_cons(X, S3, S).
set_union(S1, S2, S) :-
    set_member(X, S1, S1_2), !,
    set_union(S1_2, S2, S3),
    set_cons(X, S3, S).

set_difference(S, {}, S) :- !.
set_difference({}, _, {}) :- !.
set_difference(S1, S2, S3) :-
    set_member(X, S2, S2_2),
    set_member(X, S1, S1_2), !,
    set_difference(S1_2, S2_2, S3).
set_difference(S1, S2, S3) :-
    set_member(_, S2, S2_2), !,
    set_difference(S1, S2_2, S3).

setrel({}, {_}, neq) :- !.
setrel({}, {_}, subset) :- !.
setrel({}, {_}, subseteq) :- !.
setrel({}, {}, subseteq) :- !.
setrel({}, {}, eq) :- !.
setrel({}, {}, superseteq) :- !.
setrel({_}, {}, neq) :- !.
setrel({_}, {}, superset) :- !.
setrel({_}, {}, superseteq) :- !.
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
