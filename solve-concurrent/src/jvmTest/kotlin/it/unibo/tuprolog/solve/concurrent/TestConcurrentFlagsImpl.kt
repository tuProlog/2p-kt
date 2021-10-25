package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Ignore
import kotlin.test.Test

class TestConcurrentFlagsImpl :
    TestConcurrentFlags<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun defaultLastCallOptimizationIsOn() = multiRunConcurrentTest { super.defaultLastCallOptimizationIsOn() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun defaultUnknownIsWarning() = multiRunConcurrentTest { super.defaultUnknownIsWarning() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun settingUnknownToAdmissibleValueSucceeds() =
        multiRunConcurrentTest { super.settingUnknownToAdmissibleValueSucceeds() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun flagsNamesMustBeAtoms() = multiRunConcurrentTest { super.flagsNamesMustBeAtoms() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun gettingMissingFlagsFails() = multiRunConcurrentTest { super.gettingMissingFlagsFails() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo handle set flag side effect
    override fun settingMissingFlagsSucceeds() = multiRunConcurrentTest { super.settingMissingFlagsSucceeds() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun gettingFlagsByVariableEnumeratesFlags() =
        multiRunConcurrentTest { super.gettingFlagsByVariableEnumeratesFlags() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun settingFlagsByVariableGeneratesInstantiationError() =
        multiRunConcurrentTest { super.settingFlagsByVariableGeneratesInstantiationError() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun settingWrongValueToLastCallOptimizationProvokesDomainError() =
        multiRunConcurrentTest { super.settingWrongValueToLastCallOptimizationProvokesDomainError() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun attemptingToEditMaxArityFlagProvokesPermissionError() =
        multiRunConcurrentTest { super.attemptingToEditMaxArityFlagProvokesPermissionError() }
}
