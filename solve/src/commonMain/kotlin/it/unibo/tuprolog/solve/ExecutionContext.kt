package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlin.js.JsName

/** An interface representing the Solver execution context, containing important information that determines its behaviour */
interface ExecutionContext : ExecutionContextAware {

    /** The current procedure being executed */
    @JsName("procedure")
    val procedure: Struct?

    /** The set of current substitution till this context */
    @JsName("substitution")
    val substitution: Substitution.Unifier

    /** The Prolog call stacktrace till this ExecutionContext */
    @JsName("prologStackTrace")
    val prologStackTrace: List<Struct>

    // TODO: 25/09/2019 solverStrategies should go here... in common with other implementations, if the idea is approved
}