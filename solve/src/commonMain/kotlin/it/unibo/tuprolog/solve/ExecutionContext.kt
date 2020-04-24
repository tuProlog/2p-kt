package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
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

    @JsName("createSolver")
    fun createSolver(
        libraries: Libraries = this.libraries,
        flags: PrologFlags = this.flags,
        staticKb: ClauseDatabase = this.staticKb,
        dynamicKb: ClauseDatabase = this.dynamicKb,
        stdIn: InputChannel<String> = this.standardInput ?: InputChannel.stdIn(),
        stdOut: OutputChannel<String> = this.standardOutput ?: OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = this.standardError ?: OutputChannel.stdErr(),
        warnings: OutputChannel<PrologWarning> = this.warnings ?: OutputChannel.stdErr()
    ): Solver

    // TODO: 25/09/2019 solverStrategies should go here... in common with other implementations, if the idea is approved
}