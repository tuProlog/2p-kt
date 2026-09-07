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
inline fun <T> Iterable<T>.forEachWithLookahead(action: (T, Boolean) -> Unit) = iterator().forEachWithLookahead(action)

/** Performs the given [action] on each element, giving a lookahead hint (i.e. if there's another element to process after). */
inline fun <T> Sequence<T>.forEachWithLookahead(action: (T, Boolean) -> Unit) = iterator().forEachWithLookahead(action)

/** Extracts every `op/3` directive out of this collection of [Clause]s, converting each to an [Operator]. */
fun Iterable<Clause>.getAllOperators(): Sequence<Operator> =
    asSequence()
        .filterIsInstance<Directive>()
        .map { it.body }
        .filterIsInstance<Struct>()
        .filter { it.arity == 3 && it.functor == "op" }
        .map { Operator.fromTerm(it) }
        .filterNotNull()

/** Shorthand for this [Library]'s declared [Library.operators], as a [Sequence]. */
fun Library.getAllOperators(): Sequence<Operator> = operators.asSequence()

/** Shorthand for this [Runtime]'s declared [Runtime.operators], as a [Sequence]. */
fun Runtime.getAllOperators(): Sequence<Operator> = operators.asSequence()

/** Collects every [Operator] declared by [libraries] and by `op/3` directives within [theories]. */
fun getAllOperators(
    libraries: Runtime,
    vararg theories: Theory,
): Sequence<Operator> = libraries.getAllOperators() + sequenceOf(*theories).flatMap { it.getAllOperators() }

/** Collects this [Sequence] of [Operator]s into an [OperatorSet]. */
fun Sequence<Operator>.toOperatorSet(): OperatorSet = OperatorSet(this)

/**
 * Assembles a [Library] out of one or more [AbstractWrapper]s (i.e. [it.unibo.tuprolog.solve.primitive.PrimitiveWrapper]s,
 * [it.unibo.tuprolog.solve.function.FunctionWrapper]s, or [it.unibo.tuprolog.solve.rule.RuleWrapper]s), optionally
 * named [alias]. Useful to declare a small, ad-hoc [Library] without hand-writing an implementation.
 *
 * @throws NotImplementedError if any of [item1]/[items] is an [AbstractWrapper] subtype other than the three above.
 */
@JvmOverloads
@JsName("libraryOf")
fun libraryOf(
    alias: String? = null,
    item1: AbstractWrapper<*>,
    vararg items: AbstractWrapper<*>,
): Library {
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

/** Same as [libraryOf], but wraps the resulting [Library] into a single-library [Runtime]. */
@JvmOverloads
@JsName("runtimeOf")
fun runtimeOf(
    alias: String? = null,
    item1: AbstractWrapper<*>,
    vararg items: AbstractWrapper<*>,
): Runtime = libraryOf(alias, item1, *items).toRuntime()
