@file:JvmName("PrologExtensions")

package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.solve.SolverFactory
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("prologFromSolverFactory")
fun <R> prolog(solverFactory: SolverFactory, function: PrologScopeWithResolution.() -> R): R {
    return PrologScopeWithResolution.of(solverFactory).function()
}

@JsName("prolog")
fun <R> prolog(function: PrologScopeWithResolution.() -> R): R {
    return PrologScopeWithResolution.empty().function()
}
