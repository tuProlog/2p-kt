/**
 * @author Jason Dellaluce
 */

@file:JvmName("BinaryDecisionDiagramUtils")

package it.unibo.tuprolog.bdd

import it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
import it.unibo.tuprolog.bdd.impl.AnyVisitor
import it.unibo.tuprolog.bdd.impl.ExpansionVisitor
import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Shortcut for the [BinaryDecisionDiagram.variableOf] method.
 */
@JsName("bddOf")
fun <T : Comparable<T>> bddOf(value: T): BinaryDecisionDiagram<T> = BinaryDecisionDiagram.variableOf(value)

/**
 * Shortcut for the [BinaryDecisionDiagram.terminalOf] method.
 */
@JsName("bddTerminalOf")
fun <T : Comparable<T>> bddTerminalOf(value: Boolean): BinaryDecisionDiagram<T> =
    BinaryDecisionDiagram.terminalOf(value)

/** Internal helper function to catch all exceptions and wrap them into
 * BBD-specific ones. */
internal fun <T> runOperationAndCatchErrors(action: () -> T): T {
    try {
        return action()
    } catch (e: Throwable) {
        throw BinaryDecisionDiagramOperationException(
            "BinaryDecisionDiagram operation failure",
            e,
        )
    }
}

/**
 * Applies a given operation over a [BinaryDecisionDiagram] using
 * the Shannon Expansion. The result is a reduction of a given diagram,
 * determined by applying an operation recursively over a BDD with
 * bottom-up order.
 */
@JsName("expansion")
fun <T : Comparable<T>, E> BinaryDecisionDiagram<T>.expansion(
    falseTerminal: E,
    trueTerminal: E,
    operator: (node: T, low: E, high: E) -> E,
): E =
    runOperationAndCatchErrors {
        this.accept(
            ExpansionVisitor(
                operator,
                falseTerminal,
                trueTerminal,
            ),
        )
    }

/**
 * Returns true if the [BinaryDecisionDiagram] has at least one variable
 * element matching the given predicate.
 */
@JsName("anyWhere")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.any(predicate: (T) -> Boolean): Boolean =
    runOperationAndCatchErrors {
        this.accept(AnyVisitor(predicate))
    }

/**
 * Returns true if the [BinaryDecisionDiagram] has at least one variable element.
 */
@JsName("any")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.any(): Boolean =
    runOperationAndCatchErrors {
        this.any { true }
    }

/**
 * Returns a [BinaryDecisionDiagram] containing nodes of applying the given
 * transform function to each element in the original [BinaryDecisionDiagram].
 * The internal structure of the diagram is maintained.
 */
@JsName("map")
fun <T : Comparable<T>, E : Comparable<E>> BinaryDecisionDiagram<T>.map(mapper: (T) -> E): BinaryDecisionDiagram<E> {
    val builder = BinaryDecisionDiagramBuilder.reducedOf<E>()
    return runOperationAndCatchErrors {
        this.expansion(
            builder.buildTerminal(false),
            builder.buildTerminal(true),
        ) { node, low, high -> builder.buildVariable(mapper(node), low, high) }
    }
}

/**
 * Formats a [BinaryDecisionDiagram] using Graphviz DOT notation
 * (https://graphviz.org/). This provides a fast and widely supported solution
 * to visualize the contents of a BDD.
 */
@JsName("toDotString")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.toDotString(): String =
    runOperationAndCatchErrors {
        val checkSet = mutableSetOf<Int>()
        val labelBuilder = StringBuilder()
        val graphBuilder = StringBuilder()

        val falseValue = false.hashCode()
        val trueValue = true.hashCode()
        labelBuilder.append("$falseValue [shape=circle, label=\"0\"]\n")
        labelBuilder.append("$trueValue [shape=circle, label=\"1\"]\n")
        this.expansion(falseValue, trueValue) { node, low, high ->
            val nodeValue = Triple(node, low, high).hashCode()
            if (nodeValue !in checkSet) {
                labelBuilder.append(
                    "$nodeValue [shape=record, label=\"$node\"]\n",
                )
                graphBuilder.append("$nodeValue -> $low [style=dashed]\n")
                graphBuilder.append("$nodeValue -> $high\n")
                checkSet.add(nodeValue)
            }
            nodeValue
        }
        "digraph  {\n$labelBuilder$graphBuilder}"
    }

/**
 * Returns the number of variable nodes contained in a [BinaryDecisionDiagram].
 */
@JsName("countVariableNodes")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.countVariableNodes(): Int =
    runOperationAndCatchErrors {
        this.expansion(0, 0) { _, low, high ->
            1 + low + high
        }
    }
