package it.unibo.tuprolog.solve.concurrent

import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.answers

private val retractCases =
    listOf(
        "retract/retracts-an-asserted-clause" to ("assertz(f(1)), retract(f(1)), \\+ f(_)" to "yes"),
        "retract/binds-the-retracted-clause" to ("assertz(f(1)), retract(f(X))" to "yes: X = 1"),
        "retractall/retracts-every-matching-clause" to
            ("assertz(f(1)), assertz(f(2)), retractall(f(_)), \\+ f(_)" to "yes"),
        "abolish/abolishes-a-dynamic-procedure" to ("assertz(foo(1)), abolish(foo/1)" to "yes"),
    )

/** `retract/1`, `retractall/1` and `abolish/1` on the concurrent solver. */
fun concurrentRetractContract() =
    concurrentContract {
        retractCases.forEach { (id, case) ->
            val (query, answer) = case
            scenario(id) { answers(query) shouldBe listOf(answer) }
        }
    }
