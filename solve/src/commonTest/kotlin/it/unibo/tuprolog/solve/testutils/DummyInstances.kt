package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solve
import kotlinx.coroutines.MainScope

/**
 * Utils singleton that contains dummy instances, to be used when in a test something is not important
 *
 * @author Enrico
 */
internal object DummyInstances {

    /** An empty context to be used where needed to fill parameters */
    internal val executionContext = ExecutionContextImpl() // TODO: 25/09/2019 review tests to see if those fields are still necessary

    /** A "true" solveRequest */
    internal val solveRequest = Solve.Request(Signature("true", 0), emptyList(), Struct.of("ciao"), executionContext)

    /** An execution strategy for states */
    internal val executionStrategy = MainScope()
}
