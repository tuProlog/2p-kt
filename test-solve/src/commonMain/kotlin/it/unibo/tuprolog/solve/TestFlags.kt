package it.unibo.tuprolog.solve

interface TestFlags : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestFlags =
            TestFlagsImpl(solverFactory)
    }

    fun defaultLastCallOptimizationIsOn()

    fun defaultUnknownIsWarning()

    fun settingUnknownToAdmissibleValueSucceeds()

    fun flagsNamesMustBeAtoms()

    fun gettingMissingFlagsFails()

    fun gettingFlagsByVariableEnumeratesFlags()

    fun settingFlagsByVariableGeneratesInstantiationError()

    fun settingWrongValueToLastCallOptimizationProvokesDomainError()

    fun attemptingToEditMaxArityFlagProvokesPermissionError()
    fun settingMissingFlagsSucceeds()
}
