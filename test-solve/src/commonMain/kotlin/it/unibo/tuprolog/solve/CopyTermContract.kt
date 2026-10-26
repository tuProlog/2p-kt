package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `copy_term/2` built-in. */
fun copyTermContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("copy-term/copy-x-num") { copy_term(X, 3) yields { kotlin.collections.listOf(it.yes()) } }
    case("copy-term/copy-any-a") { copy_term(`_`, "a") yields { kotlin.collections.listOf(it.yes()) } }

    case("copy-term/copy-sum") {
        copy_term(atomOf("a") + X, X + "b") yields {
            kotlin.collections.listOf(it.yes(X to "a"))
        }
    }

    case("copy-term/copy-any-any") { copy_term(`_`, `_`) yields { kotlin.collections.listOf(it.yes()) } }

    case("copy-term/copy-triple-sum") {
        copy_term(X + X + Y, A + B + B) yields {
            kotlin.collections.listOf(it.yes(A to B))
        }
    }

    case("copy-term/copy-aa") { copy_term("a", "a") yields { kotlin.collections.listOf(it.yes()) } }
    case("copy-term/copy-ab") { copy_term("a", "b") yields { kotlin.collections.listOf(it.no()) } }
    case("copy-term/copy-f") { copy_term("f"("a"), "f"(X)) yields { kotlin.collections.listOf(it.yes(X to "a")) } }

    case("copy-term/double-copy") {
        copy_term(atom("a") + X, X + "b") and copy_term(atom("a") + X, X + "b") yields {
            kotlin.collections.listOf(it.no())
        }
    }
}
