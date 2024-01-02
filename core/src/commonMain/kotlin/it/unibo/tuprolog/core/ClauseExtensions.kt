@file:JvmName("ClauseExtensions")

package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Prepares the receiver Clause for execution, using the provided visitor
 *
 * For example, the [Clause] `product(A) :- A, A` is transformed, after preparation for execution,
 * as the Term: `product(A) :- call(A), call(A)`
 */
@JsName("prepareForExecution")
fun Clause.prepareForExecution(): Clause = accept(Clause.defaultPreparationForExecutionVisitor).castToClause()

@JsName("prepareForExecutionWithUnifier")
fun Clause.prepareForExecution(unifier: Substitution.Unifier): Clause =
    accept(Clause.preparationForExecutionVisitor(unifier)).castToClause()
