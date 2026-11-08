package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe

/**
 * Which constructs a cut sees through (#1010, step 0). Each predicate's first clause cuts inside the construct and then
 * fails: if the cut is opaque (local to the construct) the second clause still answers `X = 2`, if it is transparent
 * (it cuts the clause) the query fails.
 */
fun controlScopeContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    fun cutIn(
        body: String,
        transparent: Boolean,
    ) = solverFactory.answers("t(X)", "t(_) :- $body, fail.", "t(2).") shouldBe
        if (transparent) listOf("no") else listOf("yes: X = 2")

    scenario("control-scope/if-then-condition-is-opaque") { cutIn("((true, !) -> true ; true)", transparent = false) }
    scenario("control-scope/if-then-branch-is-transparent") { cutIn("(true -> ! ; true)", transparent = true) }
    scenario("control-scope/else-branch-is-transparent") { cutIn("(fail -> true ; !)", transparent = true) }
    scenario("control-scope/if-then-without-else-branch-is-transparent") { cutIn("(true -> !)", transparent = true) }
    scenario("control-scope/disjunction-is-transparent") { cutIn("(! ; true)", transparent = true) }
    scenario("control-scope/conjunction-is-transparent") { cutIn("(true, !)", transparent = true) }
    scenario("control-scope/call-is-opaque") { cutIn("call(!)", transparent = false) }
    scenario("control-scope/variable-goal-is-opaque") { cutIn("G = !, G", transparent = false) }
    scenario("control-scope/once-is-opaque") { cutIn("once(!)", transparent = false) }
    scenario("control-scope/negation-is-opaque") { cutIn("\\+ (!, fail)", transparent = false) }
    scenario("control-scope/catch-is-opaque") { cutIn("catch(!, _, true)", transparent = false) }
    scenario("control-scope/findall-is-opaque") { cutIn("findall(_, !, _)", transparent = false) }

    scenario("control-scope/if-then-condition-runs-once") {
        solverFactory.answers("(member(X, [1, 2]) -> member(Y, [a, b]) ; true)") shouldBe
            listOf("yes: X = 1, Y = a", "yes: X = 1, Y = b", "no")
    }

    scenario("control-scope/cut-in-if-then-condition-keeps-later-goals") {
        solverFactory.answers("((member(X, [1, 2]), !) -> true ; true), member(Y, [a, b])") shouldBe
            listOf("yes: X = 1, Y = a", "yes: X = 1, Y = b", "no")
    }

    scenario("control-scope/cut-only-prunes-its-own-call") {
        // the cut inside first/1 must not prune the alternatives of the caller's member/2
        solverFactory.answers("member(X, [a, b]), first(Y)", "first(Y) :- member(Y, [1, 2]), !.") shouldBe
            listOf("yes: X = a, Y = 1", "yes: X = b, Y = 1", "no")
    }

    scenario("control-scope/cut-after-redo-into-returned-callee") {
        solverFactory.answers("t(X)", "p(1).", "p(2).", "p(3).", "t(X) :- p(X), X >= 2, !.") shouldBe
            listOf("yes: X = 2")
    }

    scenario("control-scope/double-negation-does-not-bind") {
        solverFactory.answers("\\+ \\+ X = 1, var(X)") shouldBe listOf("yes")
    }
}
