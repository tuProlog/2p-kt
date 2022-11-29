@file:JvmName("Utils")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.function.FunctionWrapper
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

/** Performs the given [action] on each element, giving a lookahead hint (i.e. if there's another element to process after). */
inline fun <T> Iterator<T>.forEachWithLookahead(action: (T, Boolean) -> Unit) {
    while (hasNext()) {
        action(next(), hasNext())
    }
}

/** Performs the given [action] on each element, giving a lookahead hint (i.e. if there's another element to process after). */
inline fun <T> Iterable<T>.forEachWithLookahead(action: (T, Boolean) -> Unit) =
    iterator().forEachWithLookahead(action)

/** Performs the given [action] on each element, giving a lookahead hint (i.e. if there's another element to process after). */
inline fun <T> Sequence<T>.forEachWithLookahead(action: (T, Boolean) -> Unit) =
    iterator().forEachWithLookahead(action)

fun Iterable<Clause>.getAllOperators(): Sequence<Operator> {
    return asSequence()
        .filterIsInstance<Directive>()
        .map { it.body }
        .filterIsInstance<Struct>()
        .filter { it.arity == 3 && it.functor == "op" }
        .map { Operator.fromTerm(it) }
        .filterNotNull()
}

fun Library.getAllOperators(): Sequence<Operator> {
    return operators.asSequence()
}

fun Runtime.getAllOperators(): Sequence<Operator> {
    return operators.asSequence()
}

fun getAllOperators(libraries: Runtime, vararg theories: Theory): Sequence<Operator> {
    return libraries.getAllOperators() + sequenceOf(*theories).flatMap { it.getAllOperators() }
}

fun Sequence<Operator>.toOperatorSet(): OperatorSet {
    return OperatorSet(this)
}

@JvmOverloads
@JsName("libraryOf")
fun libraryOf(alias: String? = null, item1: AbstractWrapper<*>, vararg items: AbstractWrapper<*>): Library {
    val clauses = mutableListOf<Clause>()
    val primitives = mutableMapOf<Signature, Primitive>()
    val functions = mutableMapOf<Signature, LogicFunction>()
    for (item in arrayOf(item1, *items)) {
        when (item) {
            is PrimitiveWrapper<*> -> primitives += item.descriptionPair
            is FunctionWrapper<*> -> functions += item.descriptionPair
            is RuleWrapper<*> -> clauses.add(item.implementation)
            else -> throw NotImplementedError("Cannot handle wrappers of type ${item::class}")
        }
    }
    val library = Library.of(primitives, clauses, OperatorSet.EMPTY, functions)
    return alias?.let { Library.of(it, library) } ?: library
}

@JvmOverloads
@JsName("runtimeOf")
fun runtimeOf(alias: String? = null, item1: AbstractWrapper<*>, vararg items: AbstractWrapper<*>): Runtime {
    return libraryOf(alias, item1, *items).toRuntime()
}
