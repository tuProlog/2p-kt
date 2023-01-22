package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.ui.gui.impl.SolverEventImpl
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SolverEvent<T> : Event<T>, ExecutionContextAware {
    companion object {

        @JsName("of")
        @JvmStatic
        fun <T> of(
            name: String,
            event: T,
            unificator: Unificator,
            operators: OperatorSet,
            libraries: Runtime,
            flags: FlagStore,
            staticKb: Theory,
            dynamicKb: Theory,
            inputChannels: InputStore,
            outputChannels: OutputStore
        ): SolverEvent<T> = SolverEventImpl(
            name,
            event,
            unificator,
            operators,
            libraries,
            flags,
            staticKb,
            dynamicKb,
            inputChannels,
            outputChannels
        )

        @JsName("copyOf")
        @JvmStatic
        fun <T> copyOf(
            name: String,
            event: T,
            other: ExecutionContextAware
        ): SolverEvent<T> = SolverEventImpl(
            name,
            event,
            other.unificator,
            other.operators,
            other.libraries,
            other.flags,
            other.staticKb.toImmutableTheory(),
            other.dynamicKb.toImmutableTheory(),
            other.inputChannels,
            other.outputChannels
        )
    }
}
