package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.statemachine.state.State
import kotlin.reflect.KClass
import kotlin.test.assertEquals

/**
 * Utils singleton to help testing [State] subclasses
 *
 * @author Enrico
 */
internal object StateUtils {

    /** Utility method to assert a state type and its solve request context */
    internal fun assertStateTypeAndContext(expectedStateType: KClass<out State>, expectedStateContext: ExecutionContextImpl, actualState: State) {
        assertEquals(expectedStateType, actualState::class)
        assertEquals(expectedStateContext, actualState.solveRequest.context)
    } // TODO: 17/09/2019 refactor other state tests considering this

}
