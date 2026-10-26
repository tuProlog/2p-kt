package it.unibo.tuprolog.solve

/** Kotest registration of the [TestFlags] cases. */
fun flagsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestFlags.prototype(solverFactory) }) {
    case("flags/default-last-call-optimization-is-on") { defaultLastCallOptimizationIsOn() }
    case("flags/default-unknown-is-warning") { defaultUnknownIsWarning() }
    case("flags/setting-unknown-to-admissible-value-succeeds") { settingUnknownToAdmissibleValueSucceeds() }
    case("flags/flags-names-must-be-atoms") { flagsNamesMustBeAtoms() }
    case("flags/getting-missing-flags-fails") { gettingMissingFlagsFails() }
    case("flags/getting-flags-by-variable-enumerates-flags") { gettingFlagsByVariableEnumeratesFlags() }
    case("flags/setting-flags-by-variable-generates-instantiation-error") {
        settingFlagsByVariableGeneratesInstantiationError()
    }
    case("flags/setting-wrong-value-to-last-call-optimization-provokes-domain-error") {
        settingWrongValueToLastCallOptimizationProvokesDomainError()
    }
    case("flags/attempting-to-edit-max-arity-flag-provokes-permission-error") {
        attemptingToEditMaxArityFlagProvokesPermissionError()
    }
    case("flags/setting-missing-flags-succeeds") { settingMissingFlagsSucceeds() }
    case(
        "flags/default-show-wild-card-variables-in-solutions-is-on",
    ) { defaultShowWildCardVariablesInSolutionsIsOn() }
    case("flags/default-unique-solutions-is-off") { defaultUniqueSolutionsIsOff() }
    case(
        "flags/default-ground-queries-have-boolean-solution-is-off",
    ) { defaultGroundQueriesHaveBooleanSolutionIsOff() }
    case(
        "flags/by-default-wildcard-variables-appear-in-solutions",
    ) { byDefaultWildcardVariablesAppearInSolutions() }
    case("flags/hiding-wildcard-variables-removes-them-from-solutions") {
        hidingWildcardVariablesRemovesThemFromSolutions()
    }
    case("flags/by-default-duplicate-solutions-are-all-returned") { byDefaultDuplicateSolutionsAreAllReturned() }
    case(
        "flags/unique-solutions-drops-solutions-with-a-repeated-term",
    ) { uniqueSolutionsDropsSolutionsWithARepeatedTerm() }
}
