package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.data.CustomDataStore
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

object Dummy {
    object Context : ExecutionContext {
        override val procedure: Struct? = null

        override val substitution: Substitution.Unifier = Substitution.empty()

        override val logicStackTrace: List<Struct> = emptyList()

        override val customData: CustomDataStore = CustomDataStore.empty()

        override fun createSolver(
            libraries: Runtime,
            flags: FlagStore,
            staticKb: Theory,
            dynamicKb: Theory,
            inputChannels: InputStore,
            outputChannels: OutputStore
        ): Solver {
            TODO("Not yet implemented")
        }

        override fun createMutableSolver(
            libraries: Runtime,
            flags: FlagStore,
            staticKb: Theory,
            dynamicKb: Theory,
            inputChannels: InputStore,
            outputChannels: OutputStore
        ): MutableSolver {
            TODO("Not yet implemented")
        }

        override fun update(
            libraries: Runtime,
            flags: FlagStore,
            staticKb: Theory,
            dynamicKb: Theory,
            operators: OperatorSet,
            inputChannels: InputStore,
            outputChannels: OutputStore,
            customData: CustomDataStore
        ): ExecutionContext {
            TODO("Not yet implemented")
        }

        override val libraries: Runtime = Runtime.empty()

        override val flags: FlagStore = FlagStore.empty()

        override val staticKb: Theory = Theory.empty()

        override val dynamicKb: Theory = MutableTheory.empty()

        override val operators: OperatorSet = OperatorSet.EMPTY

        override val inputChannels: InputStore = InputStore.fromStandard()

        override val outputChannels: OutputStore = OutputStore.fromStandard()
    }

    object RuntimeException : ResolutionException(null, Context)
}
