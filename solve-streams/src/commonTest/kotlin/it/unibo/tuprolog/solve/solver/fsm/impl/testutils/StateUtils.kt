package it.unibo.tuprolog.solve.solver.fsm.impl.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.SideEffectManager
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.getSideEffectManager
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
    internal inline fun <reified S : State> assertOnlyOneNextState(actualNextStateSequence: Sequence<State>) {
        assertEquals(
            1, actualNextStateSequence.count(),
            "Expected only one state, but ${actualNextStateSequence.toList()}"
        )
        assertTrue { actualNextStateSequence.single() is S }
    }

    /** Utility function to assert over a State instance after casting it to expected type */
    internal inline fun <reified S : State> assertOverState(state: State, assertion: (S) -> Unit) {
        assertTrue("Expected state type to be ${S::class} but was ${state::class}") { state is S }
        assertion(state as S)
    }

    /** Utility function to assert over filtered [S] instances among those in provided sequence */
    internal inline fun <reified S : State> assertOverFilteredStateInstances(
        states: Sequence<State>,
        noinline filteringPredicate: (state: S) -> Boolean = { true },
        assertion: (index: Int, state: S) -> Unit
    ) = states.filterIsInstance<S>().filter(filteringPredicate).forEachIndexed(assertion)

    /** Utility function to assert that receiver solution contains expected query and substitution */
    internal fun Solution.assertCorrectQueryAndSubstitution(query: Struct, substitution: Substitution) {
        assertEquals(query, this.query)
        assertEquals(substitution, this.substitution)
    }

    /** Utility function to extract the SideEffectsManager from a [Solve] either Request or Response */
    internal fun Solve.getSideEffectsManager(): SideEffectManager = when (this) {
        is Solve.Response -> sideEffectManager
        is Solve.Request<*> -> context.getSideEffectManager()
    } ?: fail("SideEffectManager is not present in ${this}")

}
