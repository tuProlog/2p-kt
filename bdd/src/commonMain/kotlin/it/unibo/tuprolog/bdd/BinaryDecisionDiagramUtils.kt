/**
 * @author Jason Dellaluce
 */

@file:JvmName("BinaryDecisionDiagramUtils")

package it.unibo.tuprolog.bdd

import it.unibo.tuprolog.bdd.exception.DataStructureOperationException
import it.unibo.tuprolog.bdd.impl.AnyBinaryDecisionDiagramVisitor
import it.unibo.tuprolog.bdd.impl.ApplyBinaryDecisionDiagramVisitor
import it.unibo.tuprolog.bdd.impl.ExpansionBinaryDecisionDiagramVisitor
import it.unibo.tuprolog.bdd.impl.TreeStringBinaryDecisionDiagramVisitor
import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Formats a [BinaryDecisionDiagram] as a tree-like string.
 */
@JsName("toTreeString")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.toTreeString(): String {
    val visitor = TreeStringBinaryDecisionDiagramVisitor<T>()
    this.accept(visitor)
    return visitor.stringBuilder.toString()
}

/**
 * Applies a given operation over a [BinaryDecisionDiagram] using the Shannon Expansion.
 * The result if a reduction determined by the operation.
 */
@JsName("expansion")
fun <T : Comparable<T>, E> BinaryDecisionDiagram<T>.expansion(
    falseTerminal: E,
    trueTerminal: E,
    operator: (node: T, low: E, high: E) -> E,
): E {
    val visitor = ExpansionBinaryDecisionDiagramVisitor(operator, falseTerminal, trueTerminal)
    this.accept(visitor)
    return visitor.result!!
}

/**
 * Returns true if the [BinaryDecisionDiagram] has at least one variable element matching the given predicate.
 */
@JsName("anyWhere")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.any(predicate: (T) -> Boolean): Boolean {
    val visitor = AnyBinaryDecisionDiagramVisitor(predicate)
    this.accept(visitor)
    return visitor.result
}

/**
 * Returns true if the [BinaryDecisionDiagram] has at least one variable element.
 */
@JsName("any")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.any(): Boolean {
    return this.any { true }
}

/**
 * Returns a [BinaryDecisionDiagram] containing nodes of applying the given transform function to each
 * element in the original [BinaryDecisionDiagram]. The internal structure of the diagram is maintained.
 */
@JsName("map")
fun <T : Comparable<T>, E: Comparable<E>> BinaryDecisionDiagram<T>.map(
    mapper: (T) -> E
): BinaryDecisionDiagram<E> {
    return this.expansion(
        BinaryDecisionDiagram.Terminal<E>(false) as BinaryDecisionDiagram<E>,
        BinaryDecisionDiagram.Terminal(true)
    ) { node, low, high -> BinaryDecisionDiagram.Var(mapper(node), low, high) }
}

/**
 * Uses the "Apply" [BinaryDecisionDiagram] construction algorithm to perform the "Not" unary boolean operation.
 */
@JsName("applyNot")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.applyNot(): BinaryDecisionDiagram<T> {
    return this.apply { a -> !a }
}

/**
 * Uses the "Apply" [BinaryDecisionDiagram] construction algorithm to perform the "And" binary boolean operation over
 * [this] BDD and another one, returning a newly constructed BDD as result.
 */
@JsName("applyAnd")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.applyAnd(that: BinaryDecisionDiagram<T>):
    BinaryDecisionDiagram<T> {
        return this.apply(that) { a, b -> a && b }
    }

/**
 * Uses the "Apply" [BinaryDecisionDiagram] construction algorithm to perform the "Or" binary boolean operation over
 * [this] BDD and another one, returning a newly constructed BDD as result.
 */
@JsName("applyOr")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.applyOr(that: BinaryDecisionDiagram<T>):
    BinaryDecisionDiagram<T> {
        return this.apply(that) { a, b -> a || b }
    }

/**
 * Meant for internal use only.
 * Applies the "Apply" construction algorithm over [BinaryDecisionDiagram]s using
 * a given boolean operator.
 * */
internal fun <T : Comparable<T>> BinaryDecisionDiagram<T>.apply(
    unaryOp: (Boolean) -> Boolean
): BinaryDecisionDiagram<T> {
    val visitor = ApplyBinaryDecisionDiagramVisitor.Unary<T>(unaryOp)
    this.accept(visitor)
    if (visitor.result != null) {
        return visitor.result!!
    }
    throw DataStructureOperationException("Null result on BDD apply unary operation")
}

/**
 * Meant for internal use only.
 * Applies the "Apply" construction algorithm over [BinaryDecisionDiagram]s using
 * a given boolean operator.
 * */
internal fun <T : Comparable<T>> BinaryDecisionDiagram<T>.apply(
    that: BinaryDecisionDiagram<T>,
    binaryOp: (Boolean, Boolean) -> Boolean
): BinaryDecisionDiagram<T> {
    val visitor = ApplyBinaryDecisionDiagramVisitor.Binary(that, binaryOp)
    this.accept(visitor)
    if (visitor.result != null) {
        return visitor.result!!
    }
    throw DataStructureOperationException("Null result on BDD apply binary operation")
}