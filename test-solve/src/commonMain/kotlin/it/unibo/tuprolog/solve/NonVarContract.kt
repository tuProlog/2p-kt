package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `nonvar/1` type-checking built-in. */
fun nonVarContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("non-var/non-var-number") { nonvar(33.3) yields { listOf(it.yes()) } }
    // foo
    case("non-var/non-var-foo") { nonvar("foo") yields { listOf(it.yes()) } }
    // Foo (CapsLock)
    case("non-var/non-var-foo-cl") { nonvar("Foo") yields { listOf(it.no()) } }

    case("non-var/non-var-foo-assignment") {
        ("foo" eq "Foo") and nonvar("Foo") yields {
            listOf(it.yes("Foo" to "foo"))
        }
    }

    case("non-var/non-var-any-term") { nonvar(`_`) yields { listOf(it.no()) } }
    case("non-var/non-var") { nonvar("a"("b")) yields { listOf(it.yes()) } }
}
