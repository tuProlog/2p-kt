package it.unibo.tuprolog.ui.gui.template

/** A ready-to-load theory example, offered to the user e.g. via a "New from template" menu. */
data class TheoryTemplate(
    val id: String,
    val displayName: String,
    val source: String,
    val description: String = "",
) {
    init {
        require(id.isNotBlank()) { "id cannot be blank" }
        require(displayName.isNotBlank()) { "displayName cannot be blank" }
    }
}

/** Classic, toolkit-independent Prolog examples suitable for any plain-Prolog frontend. */
object ClassicTheoryTemplates {
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

    val FAMILY_TREE =
        TheoryTemplate(
            id = "family-tree",
            displayName = "Family tree",
            description = "Classic parent/sibling/ancestor relations.",
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

    val ALL: List<TheoryTemplate> = listOf(PEANO_ARITHMETIC, N_QUEENS, FAMILY_TREE, LIST_PROCESSING)
}
