package it.unibo.tuprolog.solve

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
            listOf(it.yes("X" to logicListOf(2, 1)))
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
}
