package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe

/**
 * Bindings and control state are restored exactly on backtracking (#1010, step 0): after a partial unification failure,
 * through aliases, and when backtracking into a callee that already returned to its caller.
 */
fun bindingRestorationContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("binding-restoration/partial-unification-failure") {
        // f(Y, Y) = f(1, 2) binds Y to 1 before failing on 2: the binding must not survive
        solverFactory.answers("(X = f(Y, Y), X = f(1, 2) ; true), var(X), var(Y)") shouldBe listOf("yes")
    }

    scenario("binding-restoration/aliases") {
        solverFactory.answers("(X = Y, Y = 1, fail ; true), var(X), var(Y), X \\== Y") shouldBe listOf("yes")
    }

    scenario("binding-restoration/alias-chain-bound-late") {
        solverFactory.answers("(X = Y, Y = Z ; true), Z = 1") shouldBe
            listOf("yes: X = 1, Y = 1, Z = 1", "yes: Z = 1")
    }

    scenario("binding-restoration/redo-into-returned-callee") {
        // q/1 returns to the query before X >= 2 fails, so the redo re-enters q/1's frame
        solverFactory.answers("q(X), X >= 2", "p(1).", "p(2).", "p(3).", "q(X) :- p(X).") shouldBe
            listOf("yes: X = 2", "yes: X = 3")
    }

    scenario("binding-restoration/redo-into-returned-deep-callee") {
        solverFactory.answers(
            "top(X, Y), X >= 2",
            "p(1).",
            "p(2).",
            "p(3).",
            "mid(X) :- p(X).",
            "top(X, Y) :- mid(X), Y = done(X).",
        ) shouldBe listOf("yes: X = 2, Y = done(2)", "yes: X = 3, Y = done(3)")
    }

    scenario("binding-restoration/nested-alternatives") {
        solverFactory.answers("pair(X, Y)", "g(a).", "g(b).", "pair(X, Y) :- g(X), g(Y).") shouldBe
            listOf("yes: X = a, Y = a", "yes: X = a, Y = b", "yes: X = b, Y = a", "yes: X = b, Y = b")
    }

    scenario("binding-restoration/redo-after-deep-deterministic-call") {
        // the deterministic count/1 reuses whatever storage the engine freed, between the two redos into p/1
        solverFactory.answers(
            "p(X), count(200), X >= 2",
            "p(1).",
            "p(2).",
            "p(3).",
            "count(0) :- !.",
            "count(N) :- M is N - 1, count(M).",
        ) shouldBe listOf("yes: X = 2", "yes: X = 3")
    }

    scenario("binding-restoration/bindings-of-one-branch-do-not-leak-into-the-next") {
        solverFactory.answers("p(X), (X = 1 -> Y = one ; true)", "p(1).", "p(2).") shouldBe
            listOf("yes: X = 1, Y = one", "yes: X = 2")
    }

    scenario("binding-restoration/clause-local-bindings-survive-primitives") {
        // B and C appear in no query, only in the clause: their bindings must still reach C > B
        solverFactory.answers("t(0)", "t(_) :- B = 6, C = 7.5, C > B.") shouldBe listOf("yes")
    }

    scenario("binding-restoration/clause-local-bindings-reach-the-caller") {
        // P and Q are bound before A and B are bound to them (#1078)
        solverFactory.answers("t(A, B)", "t(A, B) :- P = p, Q = q, A = P, B = Q.") shouldBe listOf("yes: A = p, B = q")
    }

    scenario("binding-restoration/clause-local-bindings-through-user-predicates") {
        solverFactory.answers(
            "t(A, B)",
            "id(L, L).",
            "d([1], [2]).",
            "t(A, B) :- d(P, Q), id(P, A), id(Q, B).",
        ) shouldBe
            listOf("yes: A = [1], B = [2]")
    }

    scenario("binding-restoration/clause-local-bindings-reach-inequality") {
        // X and Y are bound by the parent/2 calls before \= and \== run (#1073)
        val family = arrayOf("parent(alice, bob).", "parent(alice, charlie).")
        val distinct = listOf("yes: X = bob, Y = charlie", "yes: X = charlie, Y = bob", "no")
        solverFactory.answers("s(X, Y)", *family, "s(X, Y) :- parent(Z, X), parent(Z, Y), X \\= Y.") shouldBe distinct
        solverFactory.answers("s(X, Y)", *family, "s(X, Y) :- parent(Z, X), parent(Z, Y), X \\== Y.") shouldBe distinct
    }

    scenario("binding-restoration/shared-variable-across-clauses") {
        solverFactory.answers("r(A, B), (var(A) -> A = free ; true)", "r(X, X) :- X = 1.", "r(X, Y) :- Y = 2.") shouldBe
            listOf("yes: A = 1, B = 1", "yes: A = free, B = 2")
    }
}
