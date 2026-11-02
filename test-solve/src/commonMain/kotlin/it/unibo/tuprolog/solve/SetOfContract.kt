package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe

/** Conformance tests for the ISO `setof/3` built-in. */
fun setOfContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("set-of/set-of-basic") {
        setof("X", ("X" eq 1) or ("X" eq 2), "S") yields {
            listOf(it.yes("S" to logicListOf(1, 2)))
        }
    }

    case("set-of/set-of-x") {
        setof("X", ("X" eq 1) or ("X" eq 2), "X") yields {
            listOf(it.yes("X" to logicListOf(1, 2)))
        }
    }

    case("set-of/set-of-sorted") {
        setof("X", ("X" eq 2) or ("X" eq 1), "X") yields {
            listOf(it.yes("X" to logicListOf(1, 2)))
        }
    }

    case("set-of/set-of-doubled") {
        setof("X", ("X" eq 2) or ("X" eq 2), "L") yields {
            listOf(it.yes("L" to logicListOf(2)))
        }
    }

    case("set-of/set-of-fail") { bagof("X", fail, "L") yields { listOf(it.no()) } }

    case("set-of/set-of-as-find-all") {
        setof("X", "Y" sup ((("X" eq 1) or ("Y" eq 1)) or (("X" eq 2) or ("Y" eq 2))), "S") yields {
            listOf(it.yes("S" to logicListOf(1, 2)))
        }
    }

    scenario("set-of/sorts-by-the-standard-order-of-terms") {
        solverFactory.answers("setof(X, member(X, [c, f(a), 2.0, a, 1, b, a]), S)") shouldBe
            listOf("yes: S = [1, 2.0, a, b, c, f(a)]")
    }
}
