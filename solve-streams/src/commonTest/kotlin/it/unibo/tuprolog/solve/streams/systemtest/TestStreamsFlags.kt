package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFlags
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsFlags :
    TestFlags,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestFlags.prototype(this)

    @Test
    override fun defaultLastCallOptimizationIsOn() {
        prototype.defaultLastCallOptimizationIsOn()
    }

    @Test
    override fun defaultUnknownIsWarning() {
        prototype.defaultUnknownIsWarning()
    }

    @Test
    override fun settingUnknownToAdmissibleValueSucceeds() {
        prototype.settingUnknownToAdmissibleValueSucceeds()
    }

    @Test
    @Ignore
    override fun flagsNamesMustBeAtoms() {
        prototype.flagsNamesMustBeAtoms()
    }

    @Test
    @Ignore
    override fun gettingMissingFlagsFails() {
        prototype.gettingMissingFlagsFails()
    }

    @Test
    override fun gettingFlagsByVariableEnumeratesFlags() {
        prototype.gettingFlagsByVariableEnumeratesFlags()
    }

    @Test
    @Ignore
    override fun settingFlagsByVariableGeneratesInstantiationError() {
        prototype.settingFlagsByVariableGeneratesInstantiationError()
    }

    @Test
    @Ignore
    override fun settingWrongValueToLastCallOptimizationProvokesDomainError() {
        prototype.settingWrongValueToLastCallOptimizationProvokesDomainError()
    }

    @Test
    @Ignore
    override fun attemptingToEditMaxArityFlagProvokesPermissionError() {
        prototype.attemptingToEditMaxArityFlagProvokesPermissionError()
    }

    @Test
    override fun settingMissingFlagsSucceeds() {
        prototype.settingMissingFlagsSucceeds()
    }

    @Test
    override fun defaultShowWildCardVariablesInSolutionsIsOn() {
        prototype.defaultShowWildCardVariablesInSolutionsIsOn()
    }

    @Test
    override fun defaultUniqueSolutionsIsOff() {
        prototype.defaultUniqueSolutionsIsOff()
    }

    @Test
    override fun defaultGroundQueriesHaveBooleanSolutionIsOff() {
        prototype.defaultGroundQueriesHaveBooleanSolutionIsOff()
    }

    @Test
    override fun byDefaultWildcardVariablesAppearInSolutions() {
        prototype.byDefaultWildcardVariablesAppearInSolutions()
    }

    @Test
    override fun hidingWildcardVariablesRemovesThemFromSolutions() {
        prototype.hidingWildcardVariablesRemovesThemFromSolutions()
    }

    @Test
    override fun byDefaultDuplicateSolutionsAreAllReturned() {
        prototype.byDefaultDuplicateSolutionsAreAllReturned()
    }

    @Test
    override fun uniqueSolutionsDropsSolutionsWithARepeatedTerm() {
        prototype.uniqueSolutionsDropsSolutionsWithARepeatedTerm()
    }
}
