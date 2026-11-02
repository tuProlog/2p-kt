package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe

private val variantCases =
    listOf(
        "term-same/renamed-variable" to ("f(X) =@= f(Y)" to "yes"),
        "term-same/swapped-variables" to ("f(X, Y) =@= f(Y, X)" to "yes"),
        "term-same/shared-variable-vs-distinct-ones" to ("f(X, X) =@= f(Y, Z)" to "no"),
        "term-same/distinct-variables-vs-shared-one" to ("f(X, Y) =@= f(Z, Z)" to "no"),
        "term-same/different-constants" to ("f(a) =@= f(b)" to "no"),
        "term-same/variable-vs-constant" to ("f(X) =@= f(a)" to "no"),
        "term-not-same/renamed-variable" to ("f(X) \\=@= f(Y)" to "no"),
        "term-not-same/shared-variable-vs-distinct-ones" to ("f(X, X) \\=@= f(Y, Z)" to "yes"),
    )

/** Conformance tests for `=@=/2` and `\=@=/2`, which check whether two terms are variants of each other. */
fun termSameContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    variantCases.forEach { (id, case) ->
        val (query, answer) = case
        scenario(id) { solverFactory.answers(query) shouldBe listOf(answer) }
    }
}
