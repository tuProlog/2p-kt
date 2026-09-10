@file:JvmName("ClauseExtensions")

package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

/** Partitions this collection of clauses into a [ClausePartition], using [unificator] to match directive patterns. */
@JvmOverloads
@JsName("partitionClauses")
fun <C : Clause> Iterable<C>.partition(
    unificator: Unificator = Unificator.default,
    staticByDefault: Boolean = true,
): ClausePartition = ClausePartitioner(unificator, this, staticByDefault)

/** Partitions this [Theory]'s clauses into a [ClausePartition], using its own [Theory.unificator] by default. */
@JvmOverloads
@JsName("partitionTheory")
fun Theory.partition(
    unificator: Unificator = this.unificator,
    staticByDefault: Boolean = true,
): ClausePartition = ClausePartitioner(unificator, this, staticByDefault)
