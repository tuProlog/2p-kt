@file:JvmName("PrologExtensions")
package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.solve.SolverFactory
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("prologFromSolverFactory")
fun <R> prolog(solverFactory: SolverFactory, function: PrologWithResolution.() -> R): R {
    return PrologWithResolution.of(solverFactory).function()
}

@JsName("prolog")
fun <R> prolog(function: PrologWithResolution.() -> R): R {
    return PrologWithResolution.empty().function()
}