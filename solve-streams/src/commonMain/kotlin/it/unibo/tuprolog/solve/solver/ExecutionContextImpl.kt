package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * The execution context implementation for [StreamsSolver]
 *
 * @author Enrico
 */
internal data class ExecutionContextImpl(
    override val libraries: Libraries = Libraries(),
    override val flags: PrologFlags = emptyMap(),
    override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
    override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
    override val inputChannels: Map<String, InputChannel<String>> = ExecutionContextAware.defaultInputChannels(),
    override val outputChannels: Map<String, OutputChannel<String>> = ExecutionContextAware.defaultOutputChannels(),
    override val substitution: Substitution.Unifier = Substitution.empty(),
    /** The key strategies that a solver should use during resolution process */
    val solverStrategies: SolverStrategies = SolverStrategies.prologStandard,
    /** The side effects manager to be used during resolution process */
    val sideEffectManager: SideEffectManagerImpl = SideEffectManagerImpl()
) : ExecutionContext {

    override val procedure: Struct?
        get() = sideEffectManager.logicalParentRequests.map { it.query }.firstOrNull()

    override val prologStackTrace: Sequence<Struct> by lazy { sideEffectManager.logicalParentRequests.map { it.query } }

}

/** Extension method to get [SideEffectManagerImpl], if this context is of right type*/
internal fun ExecutionContext.getSideEffectManager(): SideEffectManagerImpl? =
    (this as? ExecutionContextImpl)?.sideEffectManager
