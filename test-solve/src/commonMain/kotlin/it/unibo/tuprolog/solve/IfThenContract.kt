package it.unibo.tuprolog.solve

/** Conformance tests for `->`/2 (if-then, without an else branch). */
fun ifThenContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("if-then/if-then-true") { "->"(true, true) yields { kotlin.collections.listOf(it.yes()) } }
    case("if-then/if-then-fail") { "->"(true, fail) yields { kotlin.collections.listOf(it.no()) } }
    case("if-then/if-then-fail-true") { "->"(fail, true) yields { kotlin.collections.listOf(it.no()) } }
    case("if-then/if-then-xto-one") { "->"(true, "X" eq 1) yields { kotlin.collections.listOf(it.yes("X" to 1)) } }

    case("if-then/if-then-x-or") {
        "->"(("X" eq 1) or ("X" eq 2), true) yields {
            kotlin.collections.listOf(it.yes("X" to 1))
        }
    }

    case("if-then/if-then-or-with-double-sub") {
        "->"(true, ("X" eq 1) or ("X" eq 2)) yields {
            with(it) {
                kotlin.collections.listOf(
                    yes("X" to 1),
                    yes("X" to 2),
                )
            }
        }
    }
}
