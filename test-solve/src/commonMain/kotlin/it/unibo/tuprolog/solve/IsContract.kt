package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `is/2` arithmetic-evaluation built-in. */
fun isContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("is/is-result") {
        "Result" `is` (numOf(3) + realOf(11.0)) yields {
            kotlin.collections.listOf(it.yes("Result" to realOf(14.0)))
        }
    }

    case("is/is-xy") {
        ("X" eq (intOf(1) + intOf(2))) and ("Y" `is` ("X" * intOf(3))) yields {
            kotlin.collections.listOf(it.yes("X" to intOf(1) + intOf(2), "Y" to intOf(9)))
        }
    }

    case("is/is-foo") { "foo" `is` intOf(77) yields { kotlin.collections.listOf(it.no()) } }

    case("is/is-n-number") {
        intOf(77) `is` "N" yields {
            kotlin.collections.listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("is", 2),
                        varOf("N"),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("is/is-number-foo") {
        intOf(77) `is` "foo" yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("is", 2),
                        TypeError.Expected.EVALUABLE,
                        indicatorOf("foo", 0),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("is/is-x-float") {
        "X" `is` float(intOf(3)) yields {
            kotlin.collections.listOf(it.yes("X" to realOf(3.0)))
        }
    }
}
