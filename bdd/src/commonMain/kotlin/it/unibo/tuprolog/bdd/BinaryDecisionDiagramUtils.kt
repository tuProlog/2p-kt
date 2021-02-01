/**
 * @author Jason Dellaluce
 */

@file:JvmName("BinaryDecisionDiagramUtils")

package it.unibo.tuprolog.bdd

import it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
import it.unibo.tuprolog.bdd.impl.AnyBinaryDecisionDiagramVisitor
import it.unibo.tuprolog.bdd.impl.ApplyBinaryDecisionDiagramVisitor
import it.unibo.tuprolog.bdd.impl.ExpansionBinaryDecisionDiagramVisitor
import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Shortcut for the [BinaryDecisionDiagram.ofVariable] method.
 */
@JsName("bddOf")
fun <T : Comparable<T>> bddOf(value: T): BinaryDecisionDiagram<T> = BinaryDecisionDiagram.ofVariable(value)

/**
 * Shortcut for the [BinaryDecisionDiagram.ofTerminal] method.
 */
@JsName("bddTerminalOf")
fun <T : Comparable<T>> bddTerminalOf(value: Boolean): BinaryDecisionDiagram<T> =
    BinaryDecisionDiagram.ofTerminal(value)

/** Internal helper function to catch all exceptions and wrap them into BBD-specific ones. */
private fun <T> runOperationAndCatchErrors(action: () -> T): T {
    try {
        return action()
    } catch (e: Throwable) {
        throw BinaryDecisionDiagramOperationException("BinaryDecisionDiagram operation failure", e)
    }
}

/**
 * Formats a [BinaryDecisionDiagram] using Graphviz notation (https://graphviz.org/).
 * This provides a fast and widely supported solution to visualize the contents of a BDD.
 */
@JsName("toGraphvizString")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.toGraphvizString(): String {
    return runOperationAndCatchErrors {
        val checkSet = mutableSetOf<Int>()
        val labelBuilder = StringBuilder()
        val graphBuilder = StringBuilder()

        val falseValue = false.hashCode()
        val trueValue = true.hashCode()
        labelBuilder.append("$falseValue [shape=circle, label=\"0\"]\n")
        labelBuilder.append("$trueValue [shape=circle, label=\"1\"]\n")
        this.expansion(falseValue, trueValue) { node, low, high ->
            val nodeValue = node.hashCode() * 31 + low * 31 + high * 31
            if (nodeValue !in checkSet) {
                labelBuilder.append("$nodeValue [shape=record, label=\"$node\"]\n")
                graphBuilder.append("$nodeValue -> $low [style=dashed]\n")
                graphBuilder.append("$nodeValue -> $high\n")
                checkSet.add(nodeValue)
            }
            nodeValue
        }
        "digraph  {\n$labelBuilder$graphBuilder}"
    }
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
    return runOperationAndCatchErrors {
        this.accept(ExpansionBinaryDecisionDiagramVisitor(operator, falseTerminal, trueTerminal))
    }
}

/**
 * Returns true if the [BinaryDecisionDiagram] has at least one variable element matching the given predicate.
 */
@JsName("anyWhere")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.any(predicate: (T) -> Boolean): Boolean {
    return runOperationAndCatchErrors {
        this.accept(AnyBinaryDecisionDiagramVisitor(predicate))
    }
}

/**
 * Returns true if the [BinaryDecisionDiagram] has at least one variable element.
 */
@JsName("any")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.any(): Boolean {
    return runOperationAndCatchErrors {
        this.any { true }
    }
}

/**
 * Returns a [BinaryDecisionDiagram] containing nodes of applying the given transform function to each
 * element in the original [BinaryDecisionDiagram]. The internal structure of the diagram is maintained.
 */
@JsName("map")
fun <T : Comparable<T>, E : Comparable<E>> BinaryDecisionDiagram<T>.map(
    mapper: (T) -> E
): BinaryDecisionDiagram<E> {
    return runOperationAndCatchErrors {
        this.expansion(
            BinaryDecisionDiagram.ofTerminal(false),
            BinaryDecisionDiagram.ofTerminal(true)
        ) { node, low, high -> BinaryDecisionDiagram.ofVariable(mapper(node), low, high) }
    }
}

/**
 * Applies the "Apply" construction algorithm over [BinaryDecisionDiagram]s using
 * a given boolean operator. The result is a Reduced Ordered Binary Decision Diagram (ROBDD). */
@JsName("applyUnary")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.apply(
    unaryOp: (Boolean) -> Boolean
): BinaryDecisionDiagram<T> {
    return runOperationAndCatchErrors {
        this.accept(ApplyBinaryDecisionDiagramVisitor.Unary(unaryOp))
    }
}

/**
 * Applies the "Apply" construction algorithm over two [BinaryDecisionDiagram]s using a given boolean operator.
 * The result is a Reduced Ordered Binary Decision Diagram (ROBDD).
 * */
@JsName("applyBinary")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.apply(
    that: BinaryDecisionDiagram<T>,
    binaryOp: (Boolean, Boolean) -> Boolean
): BinaryDecisionDiagram<T> {
    return runOperationAndCatchErrors {
        this.accept(ApplyBinaryDecisionDiagramVisitor.Binary(that, binaryOp))
    }
}

/**
 * Performs the "Not" unary boolean operation over a [BinaryDecisionDiagram].
 * The result is a Reduced Ordered Binary Decision Diagram (ROBDD).
 */
@JsName("not")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.not(): BinaryDecisionDiagram<T> {
    return runOperationAndCatchErrors {
        this.apply { a -> !a }
    }
}

/**
 * Performs the "And" unary boolean operation over two [BinaryDecisionDiagram]s.
 * The result is a Reduced Ordered Binary Decision Diagram (ROBDD).
 */
@JsName("and")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.and(that: BinaryDecisionDiagram<T>):
    BinaryDecisionDiagram<T> {
        return runOperationAndCatchErrors {
            this.apply(that) { a, b -> a && b }
        }
    }

/**
 * Performs the "Or" unary boolean operation over two [BinaryDecisionDiagram]s.
 * The result is a Reduced Ordered Binary Decision Diagram (ROBDD).
 */
@JsName("or")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.or(that: BinaryDecisionDiagram<T>):
    BinaryDecisionDiagram<T> {
        return runOperationAndCatchErrors {
            this.apply(that) { a, b -> a || b }
        }
    }
