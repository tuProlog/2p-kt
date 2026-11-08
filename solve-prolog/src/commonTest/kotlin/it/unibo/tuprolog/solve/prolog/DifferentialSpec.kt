package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.answers

/**
 * The machine gives the same answers as the FSM (#1010): same order, same bindings, same errors, on programs mixing
 * recursion, backtracking, cut, exceptions, meta-calls, all-solutions predicates and database updates. Variable names
 * made up by renaming differ between engines, so they are normalised.
 */
class DifferentialSpec :
    FunSpec({
        data class Program(
            val id: String,
            val query: String,
            val clauses: List<String> = emptyList(),
        )

        val programs =
            listOf(
                Program(
                    "naive-reverse",
                    "nrev([1, 2, 3, 4, 5, 6, 7, 8], R)",
                    listOf(
                        "app([], L, L).",
                        "app([H | T], L, [H | R]) :- app(T, L, R).",
                        "nrev([], []).",
                        "nrev([H | T], R) :- nrev(T, RT), app(RT, [H], R).",
                    ),
                ),
                Program(
                    "append-splits",
                    "app(X, Y, [a, b, c])",
                    listOf("app([], L, L).", "app([H | T], L, [H | R]) :- app(T, L, R)."),
                ),
                Program(
                    "queens-5",
                    "queens(5, Qs)",
                    listOf(
                        "queens(N, Qs) :- numlist(1, N, Ns), perm(Ns, Qs), safe(Qs).",
                        "numlist(N, N, [N]) :- !.",
                        "numlist(L, H, [L | T]) :- L < H, L1 is L + 1, numlist(L1, H, T).",
                        "perm([], []).",
                        "perm(L, [H | T]) :- sel(H, L, R), perm(R, T).",
                        "sel(X, [X | T], T).",
                        "sel(X, [H | T], [H | R]) :- sel(X, T, R).",
                        "safe([]).",
                        "safe([Q | Qs]) :- noattack(Q, Qs, 1), safe(Qs).",
                        "noattack(_, [], _).",
                        "noattack(Q, [Q1 | Qs], D) :- Q =\\= Q1 + D, Q =\\= Q1 - D, D1 is D + 1, noattack(Q, Qs, D1).",
                    ),
                ),
                Program("cut-in-clause", "max(3, 7, M)", listOf("max(X, Y, Y) :- X =< Y, !.", "max(X, _, X).")),
                Program("cut-through-disjunction", "t(X)", listOf("t(X) :- (X = 1 ; X = 2), !.", "t(3).")),
                Program(
                    "if-then-else",
                    "classify(-2, A), classify(0, B), classify(5, C)",
                    listOf("classify(X, C) :- (X < 0 -> C = neg ; X =:= 0 -> C = zero ; C = pos)."),
                ),
                Program("negation", "p(X), \\+ q(X)", listOf("p(1).", "p(2).", "p(3).", "q(2).")),
                Program(
                    "catch-and-recover",
                    "catch(r(X), E, (X = caught(E)))",
                    listOf("r(X) :- s(X).", "s(1).", "s(_) :- throw(oops)."),
                ),
                Program("uncaught-error", "X is foo + 1"),
                Program("existence-error", "undefined_predicate(1)"),
                Program(
                    "findall-bagof-setof",
                    "findall(X, m(X), L), bagof(Y, m(Y), B), setof(Z, m(Z), S)",
                    listOf("m(3).", "m(1).", "m(2).", "m(1)."),
                ),
                Program("meta-call", "G = member(X, [a, b]), call(G), once(member(Y, [c, d]))"),
                Program(
                    "database-updates",
                    "assertz(c(1)), assertz(c(2)), retract(c(1)), findall(X, c(X), L), c(Y)",
                ),
                Program("between-and-arith", "between(1, 4, X), Y is X * X, Y > 3"),
                Program(
                    "deep-recursion",
                    "count(0, 200)",
                    listOf("count(N, N) :- !.", "count(I, N) :- I1 is I + 1, count(I1, N)."),
                ),
                Program("free-variables-in-answers", "f(X, Y) = f(Z, g(Z)), W = Y"),
            )

        val normalised = Regex("""\b_?[A-Z][A-Za-z0-9]*_[0-9]+\b|\b_[0-9]+\b|\b_G[0-9]+\b""")

        fun Program.answersOn(factory: SolverFactory): List<String> =
            factory.answers(query, *clauses.toTypedArray()).map { normalised.replace(it, "_") }

        withTests(nameFn = { "same-answers/${it.id}" }, programs) { program ->
            program.answersOn(MachinePrologSolverFactory) shouldBe program.answersOn(PrologSolverFactory)
        }
    })
