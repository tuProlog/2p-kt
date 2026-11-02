package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe

private val univCases =
    listOf(
        "univ/builds-a-compound" to ("X =.. [foo, a]" to "yes: X = foo(a)"),
        "univ/builds-an-atomic-term" to ("X =.. [1]" to "yes: X = 1"),
        "univ/decomposes-a-number" to ("42 =.. L" to "yes: L = [42]"),
        "univ/compound-head-with-arguments" to ("X =.. [f(a), b]" to "halt: type_error(atomic, f(a))"),
        "univ/compound-head-alone" to ("X =.. [f(a)]" to "halt: type_error(atomic, f(a))"),
        "univ/number-head-with-arguments" to ("X =.. [1, b]" to "halt: type_error(atom, 1)"),
        "univ/empty-list" to ("X =.. []" to "halt: domain_error(non_empty_list, [])"),
        "univ/partial-list" to ("X =.. [foo|_]" to "halt: instantiation_error"),
    )

/** Conformance tests for the ISO `=../2` built-in. */
fun univContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    univCases.forEach { (id, case) ->
        val (query, answer) = case
        scenario(id) { solverFactory.answers(query) shouldBe listOf(answer) }
    }
}
