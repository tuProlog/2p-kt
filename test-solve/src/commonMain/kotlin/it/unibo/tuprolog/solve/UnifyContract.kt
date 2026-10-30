package it.unibo.tuprolog.solve

/** Conformance tests for `=`/2 (unification). */
fun unifyContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("unify/number-with-same-number") { 1 eq 1 yields { listOf(it.yes()) } }
    case("unify/variable-with-number") { "X" eq 1 yields { listOf(it.yes("X" to 1)) } }
    case("unify/variable-with-variable") { "X" eq "Y" yields { listOf(it.yes("X" to "Y")) } }

    case("unify/conjunction-binding-shared-variable") {
        (("X" eq "Y") and ("X" eq "abc")) yields {
            listOf(it.yes("X" to "abc", "Y" to "abc"))
        }
    }

    case("unify/compounds-binding-both-sides") {
        "f"("X", "def") eq "f"("def", "Y") yields {
            listOf(it.yes("X" to "def", "Y" to "def"))
        }
    }

    case("unify/different-numbers-fail") { 1 eq 2 yields { listOf(it.no()) } }
    case("unify/integer-with-real-fails") { 1 eq realOf(1.0) yields { listOf(it.no()) } }
    case("unify/different-functors-fail") { ("g"("X")) eq ("f"("a"("X"))) yields { listOf(it.no()) } }
    case("unify/different-arities-fail") { ("f"("X", 1)) eq ("f"("a"("X"))) yields { listOf(it.no()) } }

    case("unify/different-arities-with-repeated-variables-fail") {
        ("f"("X", "Y", "X")) eq ("f"("a"("X"), "a"("Y"), "Y", 2)) yields {
            listOf(it.no())
        }
    }

    case("unify/chained-variable-bindings") {
        ("f"("A", "B", "C")) eq ("f"("g"("B", "B"), "g"("C", "C"), "g"("D", "D"))) yields {
            listOf(
                it.yes(
                    "A" to "g"("g"("g"("D", "D"), "g"("D", "D")), "g"("g"("D", "D"), "g"("D", "D"))),
                    "B" to "g"("g"("D", "D"), "g"("D", "D")),
                    "C" to "g"("D", "D"),
                ),
            )
        }
    }
}
