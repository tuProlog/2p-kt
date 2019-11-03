package it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils

import it.unibo.tuprolog.solve.SideEffectManager
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.fsm.state.FinalState
import it.unibo.tuprolog.solve.solver.fsm.state.State
import it.unibo.tuprolog.solve.solver.getSideEffectManager
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Utils singleton to help testing [State] subclasses
 *
 * @author Enrico
 */
internal object StateUtils {

    /** Utility function to assert that there's only one next state of given type */
    internal inline fun <reified N> assertOnlyOneNextState(actualNextStateSequence: Sequence<State>) {
        assertEquals(1, actualNextStateSequence.count(), "Expected only one state, but ${actualNextStateSequence.toList()}")
        assertTrue { actualNextStateSequence.single() is N }
    }

    /** Utility function to extract the SideEffectsManager from a [Solve] either Request or Response */
    internal fun Solve.getSideEffectsManager(): SideEffectManager = when (this) {
        is Solve.Response -> sideEffectManager
        is Solve.Request<*> -> context.getSideEffectManager()
    } ?: fail("SideEffectManager is not present in ${this}")

    /** Utility method to assert a state type and its solve request context */
    internal fun assertStateTypeAndContext(expectedStateType: KClass<out State>, expectedSideEffect: SideEffectManagerImpl, actualState: State) {
        assertEquals(expectedStateType, actualState::class)
        (actualState as? FinalState)?.run { assertEquals(expectedSideEffect, actualState.solve.sideEffectManager!!) }
    } // TODO: 17/09/2019 refactor other state tests considering this

}
