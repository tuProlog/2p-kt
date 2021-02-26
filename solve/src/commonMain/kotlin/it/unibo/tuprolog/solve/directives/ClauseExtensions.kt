@file:JvmName("ClauseExtensions")

package it.unibo.tuprolog.solve.directives

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

@JvmOverloads
@JsName("partitionClauses")
fun <C : Clause> Iterable<C>.partition(staticByDefault: Boolean = true): ClausePartition =
    ClausePartitioner(this, staticByDefault)
