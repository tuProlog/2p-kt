package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.data.CustomDataStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * Utils singleton that contains dummy instances, to be used when in a test something is not important
 *
 * @author Enrico
 */
object DummyInstances {

    /** An empty context to be used where needed to fill parameters */
    @Suppress("IMPLICIT_NOTHING_AS_TYPE_PARAMETER", "IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
    val executionContext = object : ExecutionContext {
        override val unificator: Unificator get() = Unificator.default
        override val procedure: Struct by lazy { Atom.of("dummyProcedure") }
        override val libraries: Nothing by lazy { throw NotImplementedError() }
        override val flags: Nothing by lazy { throw NotImplementedError() }
        override val staticKb: Nothing by lazy { throw NotImplementedError() }
        override val dynamicKb: Nothing by lazy { throw NotImplementedError() }
        override val operators: OperatorSet by lazy { throw NotImplementedError() }
        override val inputChannels: Nothing by lazy { throw NotImplementedError() }
        override val outputChannels: Nothing by lazy { throw NotImplementedError() }
        override val substitution: Substitution.Unifier = Substitution.empty()
        override val logicStackTrace: Nothing by lazy { throw NotImplementedError() }
        override val customData: Nothing by lazy { throw NotImplementedError() }
        override val startTime: TimeInstant = 0L
        override val maxDuration: TimeDuration = TimeDuration.MAX_VALUE

        override fun createSolver(
            unificator: Unificator,
            libraries: Runtime,
            flags: FlagStore,
            staticKb: Theory,
            dynamicKb: Theory,
            inputChannels: InputStore,
            outputChannels: OutputStore
        ): Solver {
            throw NotImplementedError()
        }

        override fun createMutableSolver(
            unificator: Unificator,
            libraries: Runtime,
            flags: FlagStore,
            staticKb: Theory,
            dynamicKb: Theory,
            inputChannels: InputStore,
            outputChannels: OutputStore
        ): MutableSolver {
            throw NotImplementedError()
        }

        override fun update(
            unificator: Unificator,
            libraries: Runtime,
            flags: FlagStore,
            staticKb: Theory,
            dynamicKb: Theory,
            operators: OperatorSet,
            inputChannels: InputStore,
            outputChannels: OutputStore,
            customData: CustomDataStore
        ): ExecutionContext {
            throw NotImplementedError()
        }
    }
}
