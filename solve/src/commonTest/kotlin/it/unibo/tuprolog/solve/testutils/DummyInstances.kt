package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.currentTime
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlinx.coroutines.MainScope

/**
 * Utils singleton that contains dummy instances, to be used when in a test something is not important
 *
 * @author Enrico
 */
internal object DummyInstances {

    /** An empty context to be used where needed to fill parameters */
    internal val executionContext = ExecutionContext(Libraries(), emptyMap(), ClauseDatabase.of(), ClauseDatabase.of(), currentTime())

    /** A "true" solveRequest */
    internal val solveRequest = Solve.Request(Signature("true", 0), emptyList(), executionContext)

    /** An execution strategy for states */
    internal val executionStrategy = MainScope()

    /** A solveResponse to [solveRequest] */
    internal val solveResponse = with(solveRequest) { Solve.Response(Solution.Yes(signature, arguments, context.currentSubstitution), context) }
}
