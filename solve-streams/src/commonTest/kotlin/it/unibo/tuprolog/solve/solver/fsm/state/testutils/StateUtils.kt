package it.unibo.tuprolog.solve.solver.fsm.state.testutils

import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.fsm.state.FinalState
import it.unibo.tuprolog.solve.solver.fsm.state.State
import kotlin.reflect.KClass
import kotlin.test.assertEquals

/**
 * Utils singleton to help testing [State] subclasses
 *
 * @author Enrico
 */
internal object StateUtils {

    /** Utility method to assert a state type and its solve request context */
    internal fun assertStateTypeAndContext(expectedStateType: KClass<out State>, expectedSideEffect: SideEffectManagerImpl, actualState: State) {
        assertEquals(expectedStateType, actualState::class)
        (actualState as? FinalState)?.run { assertEquals(expectedSideEffect, actualState.solve.sideEffectManager!!) }
    } // TODO: 17/09/2019 refactor other state tests considering this

}
