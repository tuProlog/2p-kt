package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `atom_concat/3` built-in. */
fun atomConcatContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("atom-concat/atom-concat-third-is-var") {
        atom_concat("test", "concat", "X") yields {
            kotlin.collections.listOf(it.yes("X" to atomOf("testconcat")))
        }
    }

    case("atom-concat/atom-concat-fails") {
        atom_concat("test", "concat", "test") yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("atom-concat/atom-concat-fails-not-prefix") {
        atom_concat("concat", "X", "testconcat") yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("atom-concat/atom-concat-fails-not-suffix") {
        atom_concat("X", "test", "testconcat") yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("atom-concat/atom-concat-second-is-var") {
        atom_concat("test", "X", "testTest") yields {
            kotlin.collections.listOf(it.yes("X" to atomOf("Test")))
        }
    }

    case("atom-concat/atom-concat-first-is-var") {
        atom_concat("X", "query", "testquery") yields {
            kotlin.collections.listOf(it.yes("X" to atomOf("test")))
        }
    }
}
