@file:JvmName("Utils")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.theory.Theory
import kotlin.jvm.JvmName

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

fun Libraries.getAllOperators(): Sequence<Operator> {
    return operators.asSequence()
}

fun getAllOperators(libraries: Libraries, vararg theories: Theory): Sequence<Operator> {
    return libraries.getAllOperators() + sequenceOf(*theories).flatMap { it.getAllOperators() }
}

fun Sequence<Operator>.toOperatorSet(): OperatorSet {
    return OperatorSet(this)
}
