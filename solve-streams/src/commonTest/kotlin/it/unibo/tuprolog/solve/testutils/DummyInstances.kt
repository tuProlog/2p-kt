package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import kotlinx.coroutines.MainScope

/**
 * Utils singleton that contains dummy instances, to be used when in a test something is not important
 *
 * @author Enrico
 */
internal object DummyInstances {

    // TODO: 25/09/2019 review tests to see if those fields are still necessary

    /** An empty context to be used where needed to fill parameters */
    internal val executionContext = object : ExecutionContext {
        override val libraries: Nothing by lazy { throw NotImplementedError() }
        override val flags: Nothing by lazy { throw NotImplementedError() }
        override val staticKB: Nothing by lazy { throw NotImplementedError() }
        override val dynamicKB: Nothing by lazy { throw NotImplementedError() }
        override val substitution: Nothing by lazy { throw NotImplementedError() }
        override val prologStackTrace: Nothing by lazy { throw NotImplementedError() }
    }

    /** A "true" solveRequest */
    internal val solveRequest = Solve.Request(Signature("true", 0), emptyList(), ExecutionContextImpl())

    /** An execution strategy for states */
    internal val executionStrategy = MainScope()
}
