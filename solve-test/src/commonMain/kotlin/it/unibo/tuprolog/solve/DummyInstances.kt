package it.unibo.tuprolog.solve

/**
 * Utils singleton that contains dummy instances, to be used when in a test something is not important
 *
 * @author Enrico
 */
object DummyInstances {

    /** An empty context to be used where needed to fill parameters */
    val executionContext = object : ExecutionContext {
        override val libraries: Nothing by lazy { throw NotImplementedError() }
        override val flags: Nothing by lazy { throw NotImplementedError() }
        override val staticKB: Nothing by lazy { throw NotImplementedError() }
        override val dynamicKB: Nothing by lazy { throw NotImplementedError() }
        override val substitution: Nothing by lazy { throw NotImplementedError() }
        override val prologStackTrace: Nothing by lazy { throw NotImplementedError() }
    }
}
