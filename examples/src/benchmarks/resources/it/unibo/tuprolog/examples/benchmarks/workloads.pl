% Workloads for the resolution-timing benchmarks (#1012).

% deep-recursion: a deterministic counting loop, one arithmetic primitive per step
count(N, N) :- !.
count(I, N) :- I1 is I + 1, count(I1, N).

% nrev: naive reverse, the classic LIPS benchmark (pure unification, no primitives)
app([], L, L).
app([H | T], L, [H | R]) :- app(T, L, R).
nrev([], []).
nrev([H | T], R) :- nrev(T, RT), app(RT, [H], R).
range(N, N, [N]) :- !.
range(I, N, [I | T]) :- I1 is I + 1, range(I1, N, T).
nrev_list(N, R) :- range(1, N, L), nrev(L, R).

% backtracking: every solution of the N-queens problem, via generate-and-test over permutations
sel(X, [X | T], T).
sel(X, [H | T], [H | R]) :- sel(X, T, R).
perm([], []).
perm(L, [H | T]) :- sel(H, L, R), perm(R, T).
safe([]).
safe([Q | Qs]) :- no_attack(Q, Qs, 1), safe(Qs).
no_attack(_, [], _).
no_attack(Q, [Q1 | Qs], D) :- Q =\= Q1 + D, Q =\= Q1 - D, D1 is D + 1, no_attack(Q, Qs, D1).
queens(N, Qs) :- range(1, N, Ns), perm(Ns, Qs), safe(Qs).

% failing-search: enumerates N candidates, none of which passes the test
gen(I, _, I).
gen(I, N, X) :- I < N, I1 is I + 1, gen(I1, N, X).
failing_search(N) :- gen(0, N, X), X < 0.

% nested-solving: N sub-resolutions, each collecting 10 solutions via findall/3
nested(N, N) :- !.
nested(I, N) :- findall(X, gen(0, 10, X), _), I1 is I + 1, nested(I1, N).

% primitives: N rounds of relatively expensive built-ins
primitives(N, N) :- !.
primitives(I, N) :-
    number_codes(I, Cs), atom_codes(A, Cs), atom_length(A, _), atom_concat(A, x, _),
    I1 is I + 1, primitives(I1, N).

% bind-vars: a long list of variables (hence non-ground), bound one per resolution step, so that every step
% substitutes the (long) remaining suffix of the list
bind_all([]).
bind_all([x | T]) :- bind_all(T).
vars(0, []) :- !.
vars(N, [_ | T]) :- N1 is N - 1, vars(N1, T).
bind_vars(N) :- vars(N, L), bind_all(L).

% cut-catch: N rounds of cuts inside clauses and if-then-else, and of exceptions thrown and caught (#1010)
pick(X, [X | _]) :- !.
pick(X, [_ | T]) :- pick(X, T).
guarded(I, R) :- (I mod 3 =:= 0 -> throw(skip(I)) ; R = I).
cut_catch(N, N) :- !.
cut_catch(I, N) :-
    pick(X, [a, b, c]), X == a,
    catch(guarded(I, _), skip(_), true),
    I1 is I + 1, cut_catch(I1, N).

% dynamic-churn: asserts and retracts while enumerating the same dynamic predicate (logical update view, #1010)
churn(N) :- retractall(item(_)), fill(0, N), (item(X), retract(item(X)), Y is X + N, assertz(item(Y)), fail ; true).
fill(N, N) :- !.
fill(I, N) :- assertz(item(I)), I1 is I + 1, fill(I1, N).

% first-of-many: the first answer out of N candidates, the rest never needed (#1010)
candidate(N, X) :- between(1, N, X).
first_of_many(N) :- candidate(N, X), X > 10, !.

% arithmetic: N rounds of integer and floating-point evaluation (#1010)
arith(N, N) :- !.
arith(I, N) :- A is I * I + 3 * I - 7, B is A mod 13, C is sqrt(A * A + 1.0) + B, C > B, I1 is I + 1, arith(I1, N).

% overshoot: never terminates, so only the timeout can stop it
loop :- loop.
