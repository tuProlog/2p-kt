package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

/** An interface representing the Solver execution context, containing important information that determines its behaviour */
interface ExecutionContext {

    /** The current procedure being executed */
    val procedure: Struct?

    /** Loaded libraries */
    val libraries: Libraries

    /** Enabled flags */
    val flags: PrologFlags

    /** Static Knowledge-base, that is a KB that *can't* change executing goals */
    val staticKB: ClauseDatabase

    /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
    val dynamicKB: ClauseDatabase

    /** The set of current substitution till this context */
    val substitution: Substitution.Unifier

    /** The Prolog call stacktrace till this ExecutionContext */
    val prologStackTrace: Sequence<Struct>

    // TODO: 25/09/2019 solverStrategies should go here... in common with other implementations, if the idea is approved

}