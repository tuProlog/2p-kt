@file:JvmName("ClauseExtensions")

package it.unibo.tuprolog.core

import kotlin.jvm.JvmName



/**
 * Prepares the receiver Clause for execution, using the provided visitor
 *
 * For example, the [Clause] `product(A) :- A, A` is transformed, after preparation for execution,
 * as the Term: `product(A) :- call(A), call(A)`
 */
fun Clause.prepareForExecution(): Clause =
    accept(Clause.defaultPreparationForExecutionVisitor) as Clause

fun Clause.prepareForExecution(unifier: Substitution.Unifier): Clause =
    accept(Clause.preparationForExecutionVisitor(unifier)) as Clause