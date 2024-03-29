@file:JvmName("DSL")

package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.theory.IndexedTheoryFactory
import it.unibo.tuprolog.theory.TheoryFactory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName
import it.unibo.tuprolog.dsl.LogicProgrammingScope as CoreLogicProgrammingScope
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScope as UnifyLogicProgrammingScope

@JsName("logicProgramming")
fun <R> logicProgramming(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = LogicProgrammingScope.of(unificator).function()

@JsName("lp")
fun <R> lp(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(unificator, function)

@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(unificator, function)

fun CoreLogicProgrammingScope.withTheories(unificator: Unificator = LogicProgrammingScope.defaultUnificator) =
    LogicProgrammingScope.of(unificator, IndexedTheoryFactory(unificator), scope, termificator, variablesProvider)

fun UnifyLogicProgrammingScope.withTheories(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    theoryFactory: TheoryFactory = IndexedTheoryFactory(unificator),
) = LogicProgrammingScope.of(unificator, theoryFactory, scope, termificator, variablesProvider)
