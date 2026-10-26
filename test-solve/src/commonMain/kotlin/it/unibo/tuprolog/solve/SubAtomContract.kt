package it.unibo.tuprolog.solve

/** Kotest registration of the [TestSubAtom] cases. */
fun subAtomContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestSubAtom.prototype(solverFactory) }) {
    case("sub-atom/sub-atom-sub-is-var") { testSubAtomSubIsVar() }
    case("sub-atom/sub-atom-sub-is-var2") { testSubAtomSubIsVar2() }
    case("sub-atom/sub-atom-sub-is-var3") { testSubAtomSubIsVar3() }
    case("sub-atom/sub-atom-double-var4") { testSubAtomDoubleVar4() }
    case("sub-atom/sub-atom-instantiation-error") { testSubAtomInstantiationError() }
    case("sub-atom/sub-atom-type-error-atom-is-integer") { testSubAtomTypeErrorAtomIsInteger() }
    case("sub-atom/sub-atom-type-error-sub-is-integer") { testSubAtomTypeErrorSubIsInteger() }
    case("sub-atom/sub-atom-type-error-before-is-not-integer") { testSubAtomTypeErrorBeforeIsNotInteger() }
    case("sub-atom/sub-atom-type-error-length-is-not-integer") { testSubAtomTypeErrorLengthIsNotInteger() }
    case("sub-atom/sub-atom-type-error-after-is-not-integer") { testSubAtomTypeErrorAfterIsNotInteger() }
}
