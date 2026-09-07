/**
 * @author Jason Dellaluce
 */

@file:JvmName("BinaryDecisionDiagramOperators")

package it.unibo.tuprolog.bdd

import it.unibo.tuprolog.bdd.impl.BinaryApplyExpansionVisitor
import it.unibo.tuprolog.bdd.impl.UnaryApplyExpansionVisitor
import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Applies the "Apply" construction algorithm over [BinaryDecisionDiagram]s
 * using a given unary boolean operator. The result is a Reduced Ordered
 * Binary Decision Diagram (ROBDD).
 *
 * @param unaryOp the unary boolean operator to apply to each Terminal value
 * of this diagram (e.g. `{ a -> !a }` for [not]).
 * @return the resulting ROBDD.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails, e.g. because [unaryOp] throws.
 * */
@JsName("applyUnary")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.apply(unaryOp: (Boolean) -> Boolean): BinaryDecisionDiagram<T> =
    runOperationAndCatchErrors {
        this.applyThenExpansion(
            unaryOp,
            0,
            0,
        ) { _, _, _ -> 0 }
    }.first

/**
 * Applies the "Apply" construction algorithm over two [BinaryDecisionDiagram]s
 * using a given binary boolean operator. The result is a Reduced Ordered
 * Binary Decision Diagram (ROBDD).
 *
 * @param that the other diagram to combine this one with.
 * @param binaryOp the binary boolean operator combining a Terminal value from
 * this diagram with one from [that] (e.g. `{ a, b -> a && b }` for [and]).
 * @return the resulting ROBDD.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails, e.g. because [binaryOp] throws.
 * */
@JsName("applyBinary")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.apply(
    that: BinaryDecisionDiagram<T>,
    binaryOp: (Boolean, Boolean) -> Boolean,
): BinaryDecisionDiagram<T> =
    runOperationAndCatchErrors {
        this.applyThenExpansion(
            that,
            binaryOp,
            0,
            0,
        ) { _, _, _ -> 0 }
    }.first

/**
 * Applies the "Apply" construction algorithm over [BinaryDecisionDiagram]s
 * using a given unary boolean operator, and computes a value using the
 * Shannon Expansion over the result. The result is an instance of [Pair] of
 * which [Pair.first] is the Reduced Ordered Binary Decision Diagram (ROBDD)
 * produced by the operation, and [Pair.second] is the value of type [E]
 * computed with the Shannon Expansion.
 *
 * By definition, invoking [apply] and then [expansion] should produce the same
 * result as calling this function directly, but in one bottom-up pass instead
 * of two. This is what the `*ThenExpansion` family of operators
 * (e.g. [andThenExpansion], [notThenExpansion]) is built on, and is used e.g.
 * by `it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation`
 * to combine probabilistic explanations while incrementally caching their
 * probability.
 *
 * @param unaryOp the unary boolean operator applied to Terminal values.
 * @param expansionFalseTerminal the [E] value associated to a `false` Terminal.
 * @param expansionTrueTerminal the [E] value associated to a `true` Terminal.
 * @param expansionOperator combines a [BinaryDecisionDiagram.Variable]'s value with the already
 * computed [E] values of its `low` and `high` sub-diagrams.
 * @return a [Pair] of the resulting ROBDD and the Shannon-Expansion result.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails, e.g. because [unaryOp] or [expansionOperator] throws.
 * */
@JsName("applyUnaryThenExpansion")
fun <T : Comparable<T>, E> BinaryDecisionDiagram<T>.applyThenExpansion(
    unaryOp: (Boolean) -> Boolean,
    expansionFalseTerminal: E,
    expansionTrueTerminal: E,
    expansionOperator: (node: T, low: E, high: E) -> E,
): Pair<BinaryDecisionDiagram<T>, E> =
    runOperationAndCatchErrors {
        this.accept(
            UnaryApplyExpansionVisitor(
                BinaryDecisionDiagramBuilder.reducedOf(),
                unaryOp,
                expansionFalseTerminal,
                expansionTrueTerminal,
                expansionOperator,
            ),
        )
    }

/**
 * Applies the "Apply" construction algorithm over two [BinaryDecisionDiagram]s
 * using a given binary boolean operator, and computes a value using the
 * Shannon Expansion over the result. The result is an instance of [Pair] of
 * which [Pair.first] is the Reduced Ordered Binary Decision Diagram (ROBDD)
 * produced by the operation, and [Pair.second] is the value of type [E]
 * computed with the Shannon Expansion.
 *
 * By definition, invoking [apply] and then [expansion] should produce the same
 * result as calling this function directly, but in one bottom-up pass instead
 * of two. See [andThenExpansion]/[orThenExpansion] for concrete instantiations,
 * and `it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation`
 * for their usage to combine probabilistic explanations.
 *
 * @param that the other diagram to combine this one with.
 * @param binaryOp the binary boolean operator combining Terminal values from
 * this diagram and [that].
 * @param expansionFalseTerminal the [E] value associated to a `false` Terminal.
 * @param expansionTrueTerminal the [E] value associated to a `true` Terminal.
 * @param expansionOperator combines a [BinaryDecisionDiagram.Variable]'s value with the already
 * computed [E] values of its `low` and `high` sub-diagrams.
 * @return a [Pair] of the resulting ROBDD and the Shannon-Expansion result.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails, e.g. because [binaryOp] or [expansionOperator] throws.
 * */
@JsName("applyBinaryThenExpansion")
fun <T : Comparable<T>, E> BinaryDecisionDiagram<T>.applyThenExpansion(
    that: BinaryDecisionDiagram<T>,
    binaryOp: (Boolean, Boolean) -> Boolean,
    expansionFalseTerminal: E,
    expansionTrueTerminal: E,
    expansionOperator: (node: T, low: E, high: E) -> E,
): Pair<BinaryDecisionDiagram<T>, E> =
    runOperationAndCatchErrors {
        this.accept(
            BinaryApplyExpansionVisitor(
                BinaryDecisionDiagramBuilder.reducedOf(),
                that,
                binaryOp,
                expansionFalseTerminal,
                expansionTrueTerminal,
                expansionOperator,
            ),
        )
    }

/**
 * Performs the "Not" unary boolean operation over a [BinaryDecisionDiagram].
 * The result is a Reduced Ordered Binary Decision Diagram (ROBDD) representing
 * the logical negation of the Boolean formula encoded by this diagram.
 *
 * @return the negated ROBDD.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails.
 */
@JsName("not")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.not(): BinaryDecisionDiagram<T> =
    runOperationAndCatchErrors {
        this.apply { a -> !a }
    }

/**
 * Performs the "Not" unary boolean operation over a [BinaryDecisionDiagram]
 * and computes a value using the Shannon Expansion over the result.
 * The result is an instance of [Pair] of which [Pair.first] is the
 * Reduced Ordered Binary Decision Diagram (ROBDD) produced by the operation,
 * and [Pair.second] is the value of type [E] computed with the
 * Shannon Expansion.
 *
 * By definition, invoking [not] and then [expansion] should produce the same
 * result as calling this function directly.
 *
 * @param expansionFalseTerminal the [E] value associated to a `false` Terminal.
 * @param expansionTrueTerminal the [E] value associated to a `true` Terminal.
 * @param expansionOperator combines a [BinaryDecisionDiagram.Variable]'s value with the already
 * computed [E] values of its `low` and `high` sub-diagrams.
 * @return a [Pair] of the negated ROBDD and the Shannon-Expansion result.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails, e.g. because [expansionOperator] throws.
 * */
@JsName("notThenExpansion")
fun <T : Comparable<T>, E> BinaryDecisionDiagram<T>.notThenExpansion(
    expansionFalseTerminal: E,
    expansionTrueTerminal: E,
    expansionOperator: (node: T, low: E, high: E) -> E,
): Pair<BinaryDecisionDiagram<T>, E> =
    runOperationAndCatchErrors {
        this.applyThenExpansion(
            { a -> !a },
            expansionFalseTerminal,
            expansionTrueTerminal,
            expansionOperator,
        )
    }

/**
 * Performs the "And" binary boolean operation over two
 * [BinaryDecisionDiagram]s. The result is a Reduced Ordered Binary
 * Decision Diagram (ROBDD) representing the logical conjunction of the
 * Boolean formulas encoded by the two diagrams.
 *
 * Example (from `:bdd`'s own test suite, modeling probabilistic clauses):
 * ```kotlin
 * val solution = bddOf(someHeadsA) and bddOf(heads1)
 * ```
 *
 * @param that the other diagram to combine this one with.
 * @return the conjunction ROBDD.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails.
 */
@JsName("and")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.and(that: BinaryDecisionDiagram<T>): BinaryDecisionDiagram<T> =
    runOperationAndCatchErrors {
        this.apply(that) { a, b -> a && b }
    }

/**
 * Performs the "And" binary boolean operation over two [BinaryDecisionDiagram]s
 * and computes a value using the Shannon Expansion over the result.
 * The result is an instance of [Pair] of which [Pair.first] is the
 * Reduced Ordered Binary Decision Diagram (ROBDD) produced by the operation,
 * and [Pair.second] is the value of type [E] computed with the
 * Shannon Expansion.
 *
 * By definition, invoking [and] and then [expansion] should produce the same
 * result as calling this function directly. This is used e.g. by
 * `it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation.and`
 * to compute the probability of a conjunction of explanations in one pass.
 *
 * @param that the other diagram to combine this one with.
 * @param expansionFalseTerminal the [E] value associated to a `false` Terminal.
 * @param expansionTrueTerminal the [E] value associated to a `true` Terminal.
 * @param expansionOperator combines a [BinaryDecisionDiagram.Variable]'s value with the already
 * computed [E] values of its `low` and `high` sub-diagrams.
 * @return a [Pair] of the conjunction ROBDD and the Shannon-Expansion result.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails, e.g. because [expansionOperator] throws.
 * */
@JsName("andThenExpansion")
fun <T : Comparable<T>, E> BinaryDecisionDiagram<T>.andThenExpansion(
    that: BinaryDecisionDiagram<T>,
    expansionFalseTerminal: E,
    expansionTrueTerminal: E,
    expansionOperator: (node: T, low: E, high: E) -> E,
): Pair<BinaryDecisionDiagram<T>, E> =
    runOperationAndCatchErrors {
        this.applyThenExpansion(
            that,
            { a, b -> a && b },
            expansionFalseTerminal,
            expansionTrueTerminal,
            expansionOperator,
        )
    }

/**
 * Performs the "Or" binary boolean operation over two [BinaryDecisionDiagram]s.
 * The result is a Reduced Ordered Binary Decision Diagram (ROBDD) representing
 * the logical disjunction of the Boolean formulas encoded by the two diagrams.
 *
 * @param that the other diagram to combine this one with.
 * @return the disjunction ROBDD.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails.
 */
@JsName("or")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.or(that: BinaryDecisionDiagram<T>): BinaryDecisionDiagram<T> =
    runOperationAndCatchErrors {
        this.apply(that) { a, b -> a || b }
    }

/**
 * Performs the "Or" binary boolean operation over two [BinaryDecisionDiagram]s
 * and computes a value using the Shannon Expansion over the result.
 * The result is an instance of [Pair] of which [Pair.first] is the
 * Reduced Ordered Binary Decision Diagram (ROBDD) produced by the operation,
 * and [Pair.second] is the value of type [E] computed with the
 * Shannon Expansion.
 *
 * By definition, invoking [or] and then [expansion] should produce the same
 * result as calling this function directly. This is used e.g. by
 * `it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation.or`
 * to compute the probability of a disjunction of explanations in one pass.
 *
 * @param that the other diagram to combine this one with.
 * @param expansionFalseTerminal the [E] value associated to a `false` Terminal.
 * @param expansionTrueTerminal the [E] value associated to a `true` Terminal.
 * @param expansionOperator combines a [BinaryDecisionDiagram.Variable]'s value with the already
 * computed [E] values of its `low` and `high` sub-diagrams.
 * @return a [Pair] of the disjunction ROBDD and the Shannon-Expansion result.
 * @throws it.unibo.tuprolog.bdd.exception.BinaryDecisionDiagramOperationException
 * if construction fails, e.g. because [expansionOperator] throws.
 * */
@JsName("orThenExpansion")
fun <T : Comparable<T>, E> BinaryDecisionDiagram<T>.orThenExpansion(
    that: BinaryDecisionDiagram<T>,
    expansionFalseTerminal: E,
    expansionTrueTerminal: E,
    expansionOperator: (node: T, low: E, high: E) -> E,
): Pair<BinaryDecisionDiagram<T>, E> =
    runOperationAndCatchErrors {
        this.applyThenExpansion(
            that,
            { a, b -> a || b },
            expansionFalseTerminal,
            expansionTrueTerminal,
            expansionOperator,
        )
    }
