package it.unibo.tuprolog.testutils

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Utils singleton that contains dummy instances, to be used when in a test something is not important
 *
 * @author Enrico
 */
internal object DummyInstances {

    /** An empty context to be used where needed to fill parameters */
    internal val executionContext = object : ExecutionContext {
        override val libraries: Nothing by lazy { throw NotImplementedError() }
        override val flags: Nothing by lazy { throw NotImplementedError() }
        override val staticKB: Nothing by lazy { throw NotImplementedError() }
        override val dynamicKB: Nothing by lazy { throw NotImplementedError() }
        override val substitution: Nothing by lazy { throw NotImplementedError() }
        override val prologStackTrace: Nothing by lazy { throw NotImplementedError() }
    }
}
