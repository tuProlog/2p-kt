package it.unibo.tuprolog.ui.gui.template

/** Toolkit-independent Prolog examples suitable for any plain-Prolog frontend. */
object PrologTheoryTemplates {
    /** Natural numbers as z/s(N) terms, with addition and multiplication. */
    val PEANO_ARITHMETIC =
        TheoryTemplate(
            id = "peano-arithmetic",
            displayName = "Peano arithmetic",
            description = "Natural numbers as z/s(N) terms, with addition and multiplication.",
            source =
                """
                % Peano arithmetic: numbers represented as z (zero) and s(N) (successor of N).

                nat(z).
                nat(s(N)) :- nat(N).

                add(z, N, N).
                add(s(M), N, s(R)) :- add(M, N, R).

                mul(z, _, z).
                mul(s(M), N, R) :- mul(M, N, R1), add(R1, N, R).

                % Try:  add(s(s(z)), s(z), R).
                % Try:  mul(s(s(z)), s(s(s(z))), R).
                """.trimIndent(),
        )

    /** Place N non-attacking queens on an N x N board via backtracking. */
    val N_QUEENS =
        TheoryTemplate(
            id = "n-queens",
            displayName = "N-Queens",
            description = "Place N non-attacking queens on an N x N board via backtracking.",
            source =
                """
                % N-Queens: place N non-attacking queens on an N x N board.
                % Each solution is a list Qs where Qs[I] is the column of the queen placed in row I.

                queens(N, Qs) :-
                    range(1, N, Ns),
                    permute(Ns, Qs),
                    safe(Qs).

                range(Low, High, []) :- Low > High, !.
                range(Low, High, [Low|Rest]) :- Low =< High, Next is Low + 1, range(Next, High, Rest).

                permute([], []).
                permute(List, [X|Perm]) :- pick(X, List, Rest), permute(Rest, Perm).

                pick(X, [X|Xs], Xs).
                pick(X, [Y|Xs], [Y|Ys]) :- pick(X, Xs, Ys).

                safe([]).
                safe([Q|Qs]) :- safeFrom(Qs, Q, 1), safe(Qs).

                safeFrom([], _, _).
                safeFrom([Q|Qs], Q0, D0) :-
                    Q0 =\= Q + D0, Q0 =\= Q - D0,
                    D1 is D0 + 1,
                    safeFrom(Qs, Q0, D1).

                % Try:  queens(6, Qs).
                """.trimIndent(),
        )

    /** Prolog parent/sibling/ancestor relations. */
    val FAMILY_TREE =
        TheoryTemplate(
            id = "family-tree",
            displayName = "Family tree",
            description = "Prolog parent/sibling/ancestor relations.",
            source =
                """
                % Family relations.

                parent(tom, bob).
                parent(tom, liz).
                parent(bob, ann).
                parent(bob, pat).
                parent(pat, jim).

                male(tom).
                male(bob).
                male(jim).
                female(liz).
                female(ann).
                female(pat).

                father(F, C) :- parent(F, C), male(F).
                mother(M, C) :- parent(M, C), female(M).

                grandparent(G, C) :- parent(G, P), parent(P, C).

                sibling(X, Y) :- parent(P, X), parent(P, Y), X \== Y.

                ancestor(A, D) :- parent(A, D).
                ancestor(A, D) :- parent(A, P), ancestor(P, D).

                % Try:  grandparent(tom, X).
                % Try:  ancestor(tom, jim).
                """.trimIndent(),
        )

    /** Hand-written append/reverse/length/member over lists. */
    val LIST_PROCESSING =
        TheoryTemplate(
            id = "list-processing",
            displayName = "List processing",
            description = "Hand-written append/reverse/length/member over lists.",
            source =
                """
                % Basic list processing.

                myAppend([], L, L).
                myAppend([H|T], L, [H|R]) :- myAppend(T, L, R).

                myReverse(L, R) :- myReverse(L, [], R).
                myReverse([], Acc, Acc).
                myReverse([H|T], Acc, R) :- myReverse(T, [H|Acc], R).

                myLength([], 0).
                myLength([_|T], N) :- myLength(T, N0), N is N0 + 1.

                myMember(X, [X|_]).
                myMember(X, [_|T]) :- myMember(X, T).

                % Try:  myAppend([1,2], [3,4], L).
                % Try:  myReverse([1,2,3], R).
                """.trimIndent(),
        )

    /** STRIPS planning over the blocks world, with states as lists. */
    val STRIPS =
        TheoryTemplate(
            id = "strips",
            displayName = "STRIPS planning",
            description = "STRIPS planning over the blocks world, with states as lists (e.g. the Sussman anomaly).",
            source =
                """
                % STRIPS planning over the blocks world, with states, goals and action effects as sets (here, lists).
                % Adapted from https://github.com/unibo-fc-isi-ise/code-strips

                max_depth(8).

                % strips(+InitList, +GoalList, -Plan): Plan reaches GoalList from InitList, by iterative deepening
                strips(InitList, GoalList, Plan) :-
                    set_of(InitList, Init),
                    set_of(GoalList, Goal),
                    strips(Init, Goal, 1, 1, Plan).

                strips(_, _, _, Max, _) :- max_depth(Max), !, fail.
                strips(Init, Goal, N, Max, Plan) :-
                    strips_impl(Init, Goal, [], N, Max, _, RevPlan),
                    N =< Max,
                    reverse(RevPlan, Plan).
                strips(Init, Goal, _, Max, Plan) :-
                    NewMax is Max + 1,
                    strips(Init, Goal, 0, NewMax, Plan).

                strips_impl(State, Goal, Plan, N, Max, State, Plan) :-
                    N =< Max,
                    subseteq(Goal, State).
                strips_impl(State, Goal, Plan, N, Max, FinalState, FinalPlan) :-
                    N =< Max,
                    set_in(SubGoal, Goal),
                    \+ set_in(SubGoal, State),
                    action(Action, Pre, Add, _),
                    set_in(SubGoal, Add),
                    N1 is N + 1,
                    strips_impl(State, Pre, Plan, N1, Max, TmpState, TmpPlan),
                    apply_action(TmpState, Action, NewState),
                    strips_impl(NewState, Goal, [Action | TmpPlan], N1, Max, FinalState, FinalPlan).

                apply_action(State, Action, NewState) :-
                    action(Action, Pre, Add, Del),
                    set_difference(State, Del, TmpState),
                    subseteq(Pre, State),
                    set_union(Add, TmpState, NewState).

                % action(Action, Preconditions, Additions, Deletions), as sets
                action(Action, Pre, Add, Del) :-
                    action_def(Action, PreList, AddList, DelList),
                    set_of(PreList, Pre),
                    set_of(AddList, Add),
                    set_of(DelList, Del).

                action_def(stack(X, Y), [clear(Y), holding(X)], [on(X, Y), clear(X), handempty], [clear(Y), holding(X)]).
                action_def(unstack(X, Y), [on(X, Y), clear(X), handempty], [clear(Y), holding(X)], [on(X, Y), clear(X), handempty]).
                action_def(pick(X), [ontable(X), clear(X), handempty], [holding(X)], [ontable(X), clear(X), handempty]).
                action_def(put(X), [holding(X)], [ontable(X), clear(X), handempty], [holding(X)]).

                % the Sussman anomaly: c is on a, which must end up on b, on c
                sussman(Plan) :-
                    strips(
                        [on(c, a), ontable(a), ontable(b), clear(c), clear(b), handempty],
                        [on(a, b), on(b, c)],
                        Plan
                    ).

                % Sets as lists, [] being the empty one.

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

                % Try:  sussman(Plan).
                % Try:  strips([on(a, b), ontable(b), clear(a), handempty], [on(b, a)], Plan).
                """.trimIndent(),
        )

    /** Every template in this object, in the order a "New from template" menu should offer them. */
    val ALL: List<TheoryTemplate> = listOf(PEANO_ARITHMETIC, N_QUEENS, FAMILY_TREE, LIST_PROCESSING, STRIPS)
}
