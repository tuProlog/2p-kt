package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFlags
import kotlin.test.Test

class TestClassicFlags :
    TestFlags,
    SolverFactory by ClassicSolverFactory {
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
    override fun flagsNamesMustBeAtoms() {
        prototype.flagsNamesMustBeAtoms()
    }

    @Test
    override fun gettingMissingFlagsFails() {
        prototype.gettingMissingFlagsFails()
    }

    @Test
    override fun gettingFlagsByVariableEnumeratesFlags() {
        prototype.gettingFlagsByVariableEnumeratesFlags()
    }

    @Test
    override fun settingFlagsByVariableGeneratesInstantiationError() {
        prototype.settingFlagsByVariableGeneratesInstantiationError()
    }

    @Test
    override fun settingWrongValueToLastCallOptimizationProvokesDomainError() {
        prototype.settingWrongValueToLastCallOptimizationProvokesDomainError()
    }

    @Test
    override fun attemptingToEditMaxArityFlagProvokesPermissionError() {
        prototype.attemptingToEditMaxArityFlagProvokesPermissionError()
    }

    @Test
    override fun settingMissingFlagsSucceeds() {
        prototype.settingMissingFlagsSucceeds()
    }
}
