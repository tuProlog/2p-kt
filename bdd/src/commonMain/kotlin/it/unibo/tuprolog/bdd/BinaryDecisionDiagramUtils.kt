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
 * Shortcut for the [BinaryDecisionDiagram.variableOf] method, creating a
 * single-[Variable][BinaryDecisionDiagram.Variable] diagram out of [value].
 * This is the usual entry point for building up a BDD-encoded formula, e.g.
 * in `it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation.of`,
 * where a probabilistic Prolog term is turned into its single-variable
 * explanation with `bddOf(term)`, ready to be combined with [and]/[or]/[not].
 *
 * @param value the value representing the Boolean variable.
 * @return a new single-variable [BinaryDecisionDiagram].
 */
@JsName("bddOf")
fun <T : Comparable<T>> bddOf(value: T): BinaryDecisionDiagram<T> = BinaryDecisionDiagram.variableOf(value)

/**
 * Shortcut for the [BinaryDecisionDiagram.terminalOf] method, creating a
 * [Terminal][BinaryDecisionDiagram.Terminal] diagram representing the
 * constant [value]. Used e.g. in
 * `it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation` to
 * represent the constant `TRUE`/`FALSE` explanations with `bddTerminalOf(true)`
 * / `bddTerminalOf(false)`.
 *
 * @param value the boolean value (`true`/`false`) of the terminal.
 * @return a new [Terminal][BinaryDecisionDiagram.Terminal] diagram.
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
 * bottom-up order: each [Terminal][BinaryDecisionDiagram.Terminal] is mapped
 * to [falseTerminal]/[trueTerminal], and each
 * [Variable][BinaryDecisionDiagram.Variable] is folded with [operator],
 * receiving the already-computed [E] values of its `low`/`high` sub-diagrams.
 *
 * This is the core mechanism 2P-Kt uses to turn a BDD-encoded probabilistic
 * explanation into a probability value via Weighted Model Counting: see
 * `it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation.probability`,
 * which computes `node.probability * high + (1.0 - node.probability) * low`
 * bottom-up over the diagram.
 *
 * @param falseTerminal the [E] value associated to a `false` Terminal.
 * @param trueTerminal the [E] value associated to a `true` Terminal.
 * @param operator combines a [BinaryDecisionDiagram.Variable]'s value with
 * the already computed [E] values of its `low` and `high` sub-diagrams.
 * @return the [E] value resulting from folding the whole diagram.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if the computation fails, e.g. because [operator] throws.
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
 * Returns true if the [BinaryDecisionDiagram] has at least one
 * [Variable][BinaryDecisionDiagram.Variable] element matching the given
 * [predicate]. Used e.g. by
 * `it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation.containsAnyNotGroundTerm`
 * as `diagram.any { !it.isGround }`, to detect explanations that still
 * contain unbound Prolog terms.
 *
 * @param predicate tested against every variable value in the diagram.
 * @return `true` if at least one variable value satisfies [predicate].
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if the traversal fails, e.g. because [predicate] throws.
 */
@JsName("anyWhere")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.any(predicate: (T) -> Boolean): Boolean =
    runOperationAndCatchErrors {
        this.accept(AnyVisitor(predicate))
    }

/**
 * Returns true if the [BinaryDecisionDiagram] has at least one variable element,
 * i.e. it is not just a [Terminal][BinaryDecisionDiagram.Terminal].
 *
 * @return `true` if the diagram contains at least one variable node.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if the traversal fails.
 */
@JsName("any")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.any(): Boolean =
    runOperationAndCatchErrors {
        this.any { true }
    }

/**
 * Returns a [BinaryDecisionDiagram] containing nodes of applying the given
 * transform function to each element in the original [BinaryDecisionDiagram].
 * The internal structure of the diagram is maintained. Used e.g. by
 * `it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation.apply`
 * to rewrite the Prolog terms held by every variable of an explanation
 * (e.g. after applying a substitution), as `diagram.map { transformation(it) }`.
 *
 * @param mapper transforms each variable value of type [T] into a value of type [E].
 * @return a new diagram with the same shape, whose variable values are the
 * result of applying [mapper] to the original ones.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if the construction fails, e.g. because [mapper] throws.
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
 *
 * @return a Graphviz DOT `digraph` representation of this diagram, with
 * terminal nodes labelled `0`/`1` and every distinct variable node rendered
 * once (nodes sharing the same value/low/high triple are folded together).
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if the traversal fails.
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
 * Returns the number of [Variable][BinaryDecisionDiagram.Variable] nodes
 * contained in a [BinaryDecisionDiagram]. Note that, since a node can be
 * shared by multiple parents (e.g. in a reduced diagram built by
 * [BinaryDecisionDiagramBuilder.reducedOf]), a shared node is counted once
 * for every path that reaches it, not once overall.
 *
 * @return the number of variable-node occurrences reachable from this diagram.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if the traversal fails.
 */
@JsName("countVariableNodes")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.countVariableNodes(): Int =
    runOperationAndCatchErrors {
        this.expansion(0, 0) { _, low, high ->
            1 + low + high
        }
    }
