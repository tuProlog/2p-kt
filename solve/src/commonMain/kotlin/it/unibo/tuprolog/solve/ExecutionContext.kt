package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

/** An interface representing the Solver execution context, containing important information that determines its behaviour */
interface ExecutionContext : ExecutionContextAware {

    /** The current procedure being executed */
    val procedure: Struct?

    /** The set of current substitution till this context */
    val substitution: Substitution.Unifier

    /** The Prolog call stacktrace till this ExecutionContext */
    val prologStackTrace: Sequence<Struct>

    // TODO: 25/09/2019 solverStrategies should go here... in common with other implementations, if the idea is approved

}