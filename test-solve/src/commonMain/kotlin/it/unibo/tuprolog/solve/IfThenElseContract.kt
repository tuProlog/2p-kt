package it.unibo.tuprolog.solve

/** Conformance tests for the combined `->`/2 `;`/2 construct (if-then-else). */
fun ifThenElseContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("if-then-else/if-true-else-fail") {
        ";"("->"(true, true), fail) yields {
            kotlin.collections.listOf(it.yes())
        }
    }

    case("if-then-else/if-fail-else-true") {
        ";"("->"(fail, true), true) yields {
            kotlin.collections.listOf(it.yes())
        }
    }

    case("if-then-else/if-true-then-else-fail") {
        ";"("->"(true, fail), fail) yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("if-then-else/if-fail-else-fail") {
        ";"("->"(fail, true), fail) yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("if-then-else/if-x-true-else-x") {
        ";"("->"(true, ("X" eq 1)), "X" eq 2) yields {
            kotlin.collections.listOf(it.yes("X" to 1))
        }
    }

    case("if-then-else/if-fail-else-x") {
        ";"("->"(fail, ("X" eq 1)), "X" eq 2) yields {
            kotlin.collections.listOf(it.yes("X" to 2))
        }
    }

    case("if-then-else/if-then-else-or-with-double-sub") {
        ";"("->"(true, ("X" eq 1) or ("X" eq 2)), true) yields {
            with(it) {
                kotlin.collections.listOf(
                    yes("X" to 1),
                    yes("X" to 2),
                )
            }
        }
    }

    case("if-then-else/if-or-else-true") {
        ";"("->"(("X" eq 1) or ("X" eq 2), true), true) yields {
            kotlin.collections.listOf(it.yes("X" to 1))
        }
    }
}
