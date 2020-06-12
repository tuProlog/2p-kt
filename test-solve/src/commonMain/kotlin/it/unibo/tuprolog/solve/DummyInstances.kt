package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory

/**
 * Utils singleton that contains dummy instances, to be used when in a test something is not important
 *
 * @author Enrico
 */
object DummyInstances {

    /** An empty context to be used where needed to fill parameters */
    @Suppress("IMPLICIT_NOTHING_AS_TYPE_PARAMETER")
    val executionContext = object : ExecutionContext {
        override val procedure: Struct by lazy { Atom.of("dummyProcedure") }
        override val libraries: Nothing by lazy { throw NotImplementedError() }
        override val flags: Nothing by lazy { throw NotImplementedError() }
        override val staticKb: Nothing by lazy { throw NotImplementedError() }
        override val dynamicKb: Nothing by lazy { throw NotImplementedError() }
        override val operators: OperatorSet by lazy { throw NotImplementedError() }
        override val inputChannels: Nothing by lazy { throw NotImplementedError() }
        override val outputChannels: Nothing by lazy { throw NotImplementedError() }
        override val substitution: Substitution.Unifier = Substitution.empty()
        override val prologStackTrace: Nothing by lazy { throw NotImplementedError() }

        override fun createSolver(
            libraries: Libraries,
            flags: PrologFlags,
            staticKb: Theory,
            dynamicKb: Theory,
            stdIn: InputChannel<String>,
            stdOut: OutputChannel<String>,
            stdErr: OutputChannel<String>,
            warnings: OutputChannel<PrologWarning>
        ): Solver {
            throw NotImplementedError()
        }
    }
}
