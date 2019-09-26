package it.unibo.tuprolog.solve.solver.statemachine.state.testutils

import it.unibo.tuprolog.solve.solver.DeclarativeImplExecutionContext
import it.unibo.tuprolog.solve.solver.statemachine.state.FinalState
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
    internal fun assertStateTypeAndContext(expectedStateType: KClass<out State>, expectedStateContext: DeclarativeImplExecutionContext, actualState: State) {
        assertEquals(expectedStateType, actualState::class)
        (actualState as? FinalState)?.run { assertEquals(expectedStateContext, actualState.solve.context!!) }
    } // TODO: 17/09/2019 refactor other state tests considering this

}
