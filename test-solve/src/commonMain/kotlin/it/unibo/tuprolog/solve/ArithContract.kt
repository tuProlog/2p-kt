package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.EVALUABLE

/** Conformance tests for the ISO arithmetic comparison operators `=\=`/2, `=:=`/2, `>`/2, `>=`/2, `<`/2 and `=<`/2 (each comparing the *evaluated* value of its two arithmetic-expression arguments, unlike the term-order operators in [termContract]). */
fun arithContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("arith/arith-diff-1") { 0 arithNeq 1 yields { listOf(it.yes()) } }
    case("arith/arith-diff-2") { 1.0 arithNeq 1 yields { listOf(it.no()) } }
    case("arith/arith-diff-3") { (numOf(3) * 2) arithNeq (numOf(7) - 1) yields { listOf(it.no()) } }

    case("arith/arith-diff-4") {
        "N" arithNeq 5 yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=\\=", 2),
                        varOf("N"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-diff-5") {
        "floot"(1) arithNeq 5 yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=\\=", 2),
                        EVALUABLE,
                        indicatorOf("floot", 1),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-eq-1") { 0 arithEq 1 yields { listOf(it.no()) } }
    case("arith/arith-eq-2") { 1.0 arithEq 1 yields { listOf(it.yes()) } }
    case("arith/arith-eq-3") { (numOf(3) * 2) arithEq (numOf(7) - 1) yields { listOf(it.yes()) } }

    case("arith/arith-eq-4") {
        "N" arithEq 5 yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=:=", 2),
                        varOf("N"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-eq-5") {
        "floot"(1) arithEq 5 yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=:=", 2),
                        EVALUABLE,
                        indicatorOf("floot", 1),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-eq-6") { 0.333 arithEq (numOf(1) / 3) yields { listOf(it.no()) } }
    case("arith/arith-greater-than-1") { 0 greaterThan 1 yields { listOf(it.no()) } }
    case("arith/arith-greater-than-2") { 1.0 greaterThan 1 yields { listOf(it.no()) } }
    case("arith/arith-greater-than-3") { (numOf(3) * 2) greaterThan (numOf(7) - 1) yields { listOf(it.no()) } }

    case("arith/arith-greater-than-4") {
        "X" greaterThan 5 yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature(">", 2),
                        varOf("X"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-greater-than-5") {
        (2 + "floot"(1)) greaterThan 5 yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature(">", 2),
                        EVALUABLE,
                        indicatorOf("floot", 1),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-greater-than-eq-1") { 0 greaterThanOrEqualsTo 1 yields { listOf(it.no()) } }
    case("arith/arith-greater-than-eq-2") { 1.0 greaterThanOrEqualsTo 1 yields { listOf(it.yes()) } }

    case("arith/arith-greater-than-eq-3") {
        (numOf(3) * 2) greaterThanOrEqualsTo (numOf(7) - 1) yields {
            listOf(it.yes())
        }
    }

    case("arith/arith-greater-than-eq-4") {
        "X" greaterThanOrEqualsTo 5 yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature(">=", 2),
                        varOf("X"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-greater-than-eq-5") {
        (2 + "floot"(1)) greaterThanOrEqualsTo 5 yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature(">=", 2),
                        EVALUABLE,
                        indicatorOf("floot", 1),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-less-than-1") { 0 lowerThan 1 yields { listOf(it.yes()) } }
    case("arith/arith-less-than-2") { 1.0 lowerThan 1 yields { listOf(it.no()) } }
    case("arith/arith-less-than-3") { (numOf(3) * 2) lowerThan (numOf(7) - 1) yields { listOf(it.no()) } }

    case("arith/arith-less-than-4") {
        "X" lowerThan 5 yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("<", 2),
                        varOf("X"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-less-than-5") {
        (2 + "floot"(1)) lowerThan 5 yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("<", 2),
                        EVALUABLE,
                        indicatorOf("floot", 1),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-less-than-eq-1") { 0 lowerThanOrEqualsTo 1 yields { listOf(it.yes()) } }
    case("arith/arith-less-than-eq-2") { 1.0 lowerThanOrEqualsTo 1 yields { listOf(it.yes()) } }

    case("arith/arith-less-than-eq-3") {
        (numOf(3) * 2) lowerThanOrEqualsTo (numOf(7) - 1) yields {
            listOf(it.yes())
        }
    }

    case("arith/arith-less-than-eq-4") {
        "X" lowerThanOrEqualsTo 5 yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=<", 2),
                        varOf("X"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arith/arith-less-than-eq-5") {
        (2 + "floot"(1)) lowerThanOrEqualsTo 5 yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=<", 2),
                        EVALUABLE,
                        indicatorOf("floot", 1),
                        index = 0,
                    ),
                ),
            )
        }
    }
}
