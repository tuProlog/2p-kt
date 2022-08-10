@file:JvmName("PrologExtensions")

package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.solve.SolverFactory
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("logicProgrammingFromSolverFactory")
fun <R> logicProgramming(solverFactory: SolverFactory, function: LogicProgrammingScopeWithResolution.() -> R): R {
    return LogicProgrammingScopeWithResolution.of(solverFactory).function()
}

@JsName("logicProgramming")
fun <R> logicProgramming(function: LogicProgrammingScopeWithResolution.() -> R): R {
    return LogicProgrammingScopeWithResolution.empty().function()
}

@JsName("lp")
fun <R> lp(function: LogicProgrammingScopeWithResolution.() -> R): R = logicProgramming(function)

@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(function: LogicProgrammingScopeWithResolution.() -> R): R = logicProgramming(function)
