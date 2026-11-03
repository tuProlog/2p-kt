package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe

private const val OPERATORS_DIRECTIVE = ":- op(200, xfx, [foo, bar])"

/** Conformance tests for the ISO `op/3` built-in, as a directive and as a goal. */
fun opContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("op/directive-with-a-list-declares-every-operator") {
        solverFactory.answers("current_op(P, T, foo), current_op(P, T, bar)", OPERATORS_DIRECTIVE) shouldBe
            listOf("yes: P = 200, T = xfx")
    }

    scenario("op/directive-with-a-list-reaches-queries") {
        solverFactory.answers("p(a foo b)", OPERATORS_DIRECTIVE, "p(foo(a, b))") shouldBe listOf("yes")
    }

    scenario("op/goal-with-a-list-declares-every-operator") {
        solverFactory.answers("op(200, xfx, [foo, bar]), current_op(P, T, foo), current_op(P, T, bar)") shouldBe
            listOf("yes: P = 200, T = xfx")
    }

    scenario("op/improper-list-is-a-type-error") {
        solverFactory.answers("op(200, xfx, [foo|bar])") shouldBe listOf("halt: type_error(list, [foo | bar])")
    }

    scenario("op/improper-list-declares-no-operator") {
        solverFactory.answers("catch(op(200, xfx, [foo|bar]), _, true), \\+ current_op(_, _, foo)") shouldBe
            listOf("yes")
    }
}
