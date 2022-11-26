package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.data.CustomDataStore
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.Test
import kotlin.test.assertEquals

class TestTimeoutException {
    private object DummyContext : ExecutionContext {
        override val unificator: Unificator
            get() = TODO("Not yet implemented")
        override val procedure: Struct?
            get() = TODO("Not yet implemented")
        override val substitution: Substitution.Unifier
            get() = TODO("Not yet implemented")
        override val logicStackTrace: List<Struct>
            get() = TODO("Not yet implemented")

        override val customData: CustomDataStore
            get() = TODO("Not yet implemented")

        override fun createSolver(
            unificator: Unificator,
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
            unificator: Unificator,
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
            TODO("Not yet implemented")
        }

        override val libraries: Runtime
            get() = TODO("Not yet implemented")
        override val flags: FlagStore
            get() = TODO("Not yet implemented")
        override val staticKb: Theory
            get() = TODO("Not yet implemented")
        override val dynamicKb: Theory
            get() = TODO("Not yet implemented")
        override val operators: OperatorSet
            get() = TODO("Not yet implemented")
        override val inputChannels: InputStore
            get() = TODO("Not yet implemented")
        override val outputChannels: OutputStore
            get() = TODO("Not yet implemented")

        override val startTime: TimeInstant = 0

        override val maxDuration: TimeDuration = 10L

        override fun toString(): String = "DummyContext"
    }

    private val exception = TimeOutException(
        message = "dummy message",
        context = DummyContext,
        exceededDuration = DummyContext.maxDuration
    )

    @Test
    fun testToString() {
        assertEquals(
            "${TimeOutException::class.simpleName}(" +
                "message=dummy message, " +
                "exceededDuration=${DummyContext.maxDuration}, " +
                "contexts=[${DummyContext::class.simpleName}]" +
                ")",
            exception.toString()
        )
    }

    @Test
    fun testToPrintStackTrace() {
        exception.printStackTrace()
    }
}
