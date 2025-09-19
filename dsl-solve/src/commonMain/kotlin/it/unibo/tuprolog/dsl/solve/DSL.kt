@file:JvmName("DSL")

package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("logicProgramming")
fun <R> logicProgramming(
    solverFactory: SolverFactory,
    unificator: Unificator = solverFactory.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = LogicProgrammingScope.of(solverFactory, unificator).function()

@JsName("lp")
fun <R> lp(
    solverFactory: SolverFactory,
    unificator: Unificator = solverFactory.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(solverFactory, unificator, function)

@JsName("prolog")
fun <R> prolog(
    unificator: Unificator = Solver.prolog.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(Solver.prolog, unificator, function)
