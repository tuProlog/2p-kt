package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe

/**
 * What `catch/3` restores when it catches (#1010, step 0): the goal's bindings and alternatives are discarded before the
 * recovery runs, the recovery keeps its own alternatives, and non-matching or rethrown balls reach outer catchers.
 */
fun catchRestorationContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("catch-restoration/bindings-undone-before-recovery") {
        solverFactory.answers("catch((X = 1, throw(e)), e, true), var(X)") shouldBe listOf("yes")
    }

    scenario("catch-restoration/catcher-binds") {
        solverFactory.answers("catch(throw(f(1)), f(Y), true)") shouldBe listOf("yes: Y = 1")
    }

    scenario("catch-restoration/ball-is-a-copy") {
        // the ball is copied when thrown, so undoing X's binding doesn't unbind it
        solverFactory.answers("catch((X = 1, throw(b(X))), b(Y), true)") shouldBe listOf("yes: Y = 1")
    }

    scenario("catch-restoration/goal-alternatives-discarded") {
        solverFactory.answers("catch((member(X, [1, 2, 3]), X >= 2, throw(found(X))), found(Y), true)") shouldBe
            listOf("yes: Y = 2")
    }

    scenario("catch-restoration/goal-alternatives-kept-without-throw") {
        solverFactory.answers("catch(member(X, [1, 2]), _, true)") shouldBe listOf("yes: X = 1", "yes: X = 2", "no")
    }

    scenario("catch-restoration/recovery-alternatives") {
        solverFactory.answers("catch(throw(e), e, member(X, [a, b]))") shouldBe listOf("yes: X = a", "yes: X = b", "no")
    }

    scenario("catch-restoration/inner-catcher-does-not-match") {
        solverFactory.answers("catch(catch(throw(outer), inner, X = in), outer, X = out)") shouldBe
            listOf("yes: X = out")
    }

    scenario("catch-restoration/rethrow-from-recovery") {
        solverFactory.answers("catch(catch(throw(a), a, throw(b)), b, X = ok)") shouldBe listOf("yes: X = ok")
    }

    scenario("catch-restoration/catch-is-reactivated-on-redo") {
        // the throw happens after re-entering catch/3's goal on backtracking, so it is caught
        solverFactory.answers("catch((member(X, [1, 2]), X >= 2, throw(two)), two, Y = caught)") shouldBe
            listOf("yes: Y = caught")
    }

    scenario("catch-restoration/throw-after-catch-exited-is-not-caught") {
        // after X = 1 fails, the redo goes back into member/2 inside catch/3, but throw(e) runs outside it
        solverFactory.answers("catch(member(X, [1, 2]), e, true), X >= 2, throw(e)") shouldBe
            listOf("halt: system_error")
    }

    scenario("catch-restoration/throw-from-deep-callee") {
        solverFactory.answers(
            "catch(a(X), deep(Y), true)",
            "a(X) :- b(X).",
            "b(X) :- p(X), X >= 2, throw(deep(X)).",
            "p(1).",
            "p(2).",
        ) shouldBe listOf("yes: Y = 2")
    }

    scenario("catch-restoration/caller-alternatives-survive-catch") {
        solverFactory.answers("member(Z, [a, b]), catch(throw(e), e, true)") shouldBe
            listOf("yes: Z = a", "yes: Z = b", "no")
    }
}
