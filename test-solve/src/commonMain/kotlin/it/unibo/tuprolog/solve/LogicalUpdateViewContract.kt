package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe

/**
 * The logical update view (ISO 7.5.4, #1010 step 0): a call to a dynamic predicate sees the clauses that existed when it
 * started, whatever is asserted or retracted while it runs; later calls see the changes.
 */
fun logicalUpdateViewContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("logical-update-view/assert-during-enumeration-is-not-seen") {
        // without the view, each assertion would feed the running c(X) and never terminate
        solverFactory.answers(
            "assertz(c(1)), (c(X), Y is X + 1, assertz(c(Y)), fail ; findall(Z, c(Z), L))",
        ) shouldBe listOf("yes: L = [1, 2]")
    }

    scenario("logical-update-view/retract-during-enumeration-is-not-seen") {
        solverFactory.answers(
            "assertz(d(1)), assertz(d(2)), assertz(d(3)), findall(X, (d(X), retractall(d(_))), L)",
        ) shouldBe listOf("yes: L = [1, 2, 3]")
    }

    scenario("logical-update-view/later-calls-see-changes") {
        solverFactory.answers("assertz(e(1)), e(X), assertz(e(2)), findall(Y, e(Y), L)") shouldBe
            listOf("yes: L = [1, 2], X = 1")
    }

    scenario("logical-update-view/asserta-during-enumeration-is-not-seen") {
        solverFactory.answers("assertz(f(1)), assertz(f(2)), findall(X, (f(X), asserta(f(0))), L)") shouldBe
            listOf("yes: L = [1, 2]")
    }

    scenario("logical-update-view/retract-iterates-its-own-view") {
        solverFactory.answers("assertz(g(1)), assertz(g(2)), retract(g(X)), assertz(g(3))") shouldBe
            listOf("yes: X = 1", "yes: X = 2")
    }

    scenario("logical-update-view/retracted-clauses-still-run") {
        // retract/1 removes both clauses (on first answer and on redo), yet the running findall/3 still sees h(second)
        solverFactory.answers(
            "assertz((h(X) :- retract((h(_) :- _)), X = first)), assertz(h(second)), findall(X, h(X), L)",
        ) shouldBe listOf("yes: L = [first, first, second]")
    }
}
