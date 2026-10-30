package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `bagof/3` built-in. */
fun bagOfContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("bag-of/bag-x-in-different-values") {
        bagof("X", ("X" eq 1) or ("X" eq 2), "S") yields {
            listOf(it.yes("S" to logicListOf(1, 2)))
        }
    }

    case("bag-of/bag-of-find-x") {
        bagof("X", ("X" eq 1) or ("X" eq 2), "X") yields {
            listOf(it.yes("X" to logicListOf(1, 2)))
        }
    }

    case("bag-of/bag-of-yxz") {
        bagof("X", ("X" eq "Y") or ("X" eq "Z"), "L") yields {
            listOf(it.yes("L" to logicListOf("Y", "Z")))
        }
    }

    case("bag-of/bag-of-fail") { bagof("X", fail, "L") yields { listOf(it.no()) } }

    case("bag-of/bag-of-same-as-findall") {
        bagof("X", "Y" sup (("X" eq 1) or ("Y" eq 2)), "S") yields {
            listOf(it.yes("S" to logicListOf(1)))
        }
    }

    case("bag-of/bag-of-instance-error") {
        bagof("X", "G", "S") yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("bagof", 3),
                        varOf("G"),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("bag-of/bag-of-type-error") {
        bagof("X", 1, "S") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("bagof", 3),
                        TypeError.Expected.CALLABLE,
                        numOf(1),
                        index = 1,
                    ),
                ),
            )
        }
    }
}
