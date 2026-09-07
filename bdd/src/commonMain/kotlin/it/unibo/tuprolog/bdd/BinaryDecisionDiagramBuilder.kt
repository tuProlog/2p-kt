package it.unibo.tuprolog.bdd

import it.unibo.tuprolog.bdd.impl.builder.ReducedBinaryDecisionDiagramBuilder
import it.unibo.tuprolog.bdd.impl.builder.SimpleBinaryDecisionDiagramBuilder
import kotlin.js.JsName

/**
 * This interfaces hides the strategy with which instances of
 * [BinaryDecisionDiagram] are created. Platform-specific optimized
 * representations of BDDs can be introduced by providing new
 * implementations of this interface.
 *
 * Business logic related to diagram reduction, or node re-usage,
 * should be handled by this entity. Most callers do not need to interact
 * with this interface directly: node creation is normally driven through
 * [BinaryDecisionDiagram.variableOf]/[BinaryDecisionDiagram.terminalOf] (or
 * the [bddOf]/[bddTerminalOf] shortcuts), and diagram combination through
 * the operators in `BinaryDecisionDiagramOperators.kt`, both of which use
 * a builder under the hood. Implementing this interface directly is only
 * needed when introducing a new node-creation/reduction strategy.
 *
 * @author Jason Dellaluce
 */
interface BinaryDecisionDiagramBuilder<T : Comparable<T>> {
    /**
     * Returns an instance of [BinaryDecisionDiagram.Variable] with
     * the provided input.
     *
     * @param value the value of the Boolean variable represented by the node.
     * @param low the sub-diagram reached when [value] is `false`.
     * @param high the sub-diagram reached when [value] is `true`.
     * @return a new [BinaryDecisionDiagram.Variable] node.
     * */
    @JsName("buildVariable")
    fun buildVariable(
        value: T,
        low: BinaryDecisionDiagram<T>,
        high: BinaryDecisionDiagram<T>,
    ): BinaryDecisionDiagram<T>

    /**
     * Returns an instance of [BinaryDecisionDiagram.Terminal] with
     * the provided input.
     *
     * @param truth the boolean value (`true`/`false`) of the terminal.
     * @return a new [BinaryDecisionDiagram.Terminal] node.
     * */
    @JsName("buildTerminal")
    fun buildTerminal(truth: Boolean): BinaryDecisionDiagram<T>

    companion object {
        /**
         * Returns a default [BinaryDecisionDiagramBuilder] instance. Different
         * platforms can return different types of instances, to apply
         * platform-specific optimizations. Note, no reduction optimization
         * must be applied by the returned instance.
         *
         * @return a new, platform-default [BinaryDecisionDiagramBuilder] with
         * no reduction optimizations.
         * */
        @JsName("defaultOf")
        fun <E : Comparable<E>> defaultOf(): BinaryDecisionDiagramBuilder<E> =
            createDefaultBinaryDecisionDiagramBuilder()

        /**
         * Returns a simple [BinaryDecisionDiagramBuilder] instance that does
         * not apply platform-specific or reduction optimizations. This
         * provides basic means to build represent BDDs, and keeps the entire
         * data structure in memory in the form of a directed graph.
         *
         * @return a new, unoptimized [BinaryDecisionDiagramBuilder].
         * */
        @JsName("simpleOf")
        fun <E : Comparable<E>> simpleOf(): BinaryDecisionDiagramBuilder<E> = SimpleBinaryDecisionDiagramBuilder()

        /**
         * Returns a [BinaryDecisionDiagramBuilder] instance that applies
         * reduction optimizations through the `reduce` algorithm, and
         * delegates the actual construction logic of each node to [delegate].
         * By default, [delegate] is set as [defaultOf]. The following
         * reductions are performed:
         * - Removal of duplicate variable nodes
         * - Removal of duplicate terminal nodes
         * - Removal of redundant variable nodes, which are
         * [BinaryDecisionDiagram.Variable] nodes where low and high point
         * to the same node
         *
         * This is what all the operators in `BinaryDecisionDiagramOperators.kt`
         * (e.g. [and], [or], [not], [apply]) use internally to keep the result
         * of each operation a canonical Reduced Ordered BDD (ROBDD).
         *
         * @param delegate the builder actually constructing each node, before
         * reduction is applied on top of it. Defaults to [defaultOf].
         * @return a new, reduction-applying [BinaryDecisionDiagramBuilder].
         * */
        @JsName("reducedOf")
        fun <E : Comparable<E>> reducedOf(
            delegate: BinaryDecisionDiagramBuilder<E> = defaultOf(),
        ): BinaryDecisionDiagramBuilder<E> = ReducedBinaryDecisionDiagramBuilder(delegate)
    }
}

internal fun <E : Comparable<E>> createDefaultBinaryDecisionDiagramBuilder(): BinaryDecisionDiagramBuilder<E> =
    BinaryDecisionDiagramBuilder.simpleOf()
