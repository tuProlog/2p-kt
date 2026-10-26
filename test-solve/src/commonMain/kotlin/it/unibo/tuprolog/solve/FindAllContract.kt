package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `findall/3` built-in. */
fun findAllContract(
    solverFactory: SolverFactory,
    errorSignature: Signature = Signature("call", 1),
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("find-all/find-x-in-diff-values") {
        findall("X", ("X" eq 1) or ("X" eq 2), "S") yields {
            listOf(it.yes("S" to logicListOf(1, 2)))
        }
    }

    case("find-all/find-sum-result") {
        findall("+"("X", "Y"), "X" eq 1, "S") yields {
            listOf(it.yes("S" to logicListOf(1 + "Y")))
        }
    }

    case("find-all/find-xin-fail") { findall("X", fail, "L") yields { listOf(it.yes("L" to emptyLogicList)) } }

    case("find-all/find-xin-same-x-values") {
        findall("X", ("X" eq 1) or ("X" eq 1), "S") yields {
            listOf(it.yes("S" to logicListOf(1, 1)))
        }
    }

    case("find-all/result-list-is-correct") {
        findall("X", ("X" eq 2) or ("X" eq 1), logicListOf(1, 2)) yields {
            listOf(it.no())
        }
    }

    case("find-all/find-xto-double-assigment") {
        findall("X", ("X" eq 1) or ("X" eq 2), logicListOf("X", "Y")) yields {
            listOf(it.yes("X" to 1, "Y" to 2))
        }
    }

    case("find-all/find-xin-goal") {
        findall("X", "Goal", "S") yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("findall", 3),
                        varOf("Goal"),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("find-all/find-xin-number") {
        findall("X", 4, "S") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("findall", 3),
                        TypeError.Expected.CALLABLE,
                        numOf(4),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("find-all/find-xin-call") {
        findall("X", call(1), "S") yields {
            listOf(
                it.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        numOf(1),
                    ),
                ),
            )
        }
    }
}
