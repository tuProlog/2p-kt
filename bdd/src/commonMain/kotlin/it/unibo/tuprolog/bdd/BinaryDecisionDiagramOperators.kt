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
 * using a given boolean operator. The result is a Reduced Ordered Binary
 * Decision Diagram (ROBDD).
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
 * using a given boolean operator. The result is a Reduced Ordered Binary
 * Decision Diagram (ROBDD).
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
 * using a given boolean operator, and computes a value using the Shannon
 * Expansion over the result. The result is an instance of [Pair] of which
 * [Pair.first] is the Reduced Ordered Binary Decision Diagram (ROBDD) produced
 * by the operation, and [Pair.second] is the value of type [T] computed with
 * the Shannon Expansion.
 *
 * By definition, invoking [apply] and then [expansion] should produce the same
 * result.
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
 * using a given boolean operator, and computes a value using the Shannon
 * Expansion over the result. The result is an instance of [Pair] of which
 * [Pair.first] is the Reduced Ordered Binary Decision Diagram (ROBDD) produced
 * by the operation, and [Pair.second] is the value of type [T] computed with
 * the Shannon Expansion.
 *
 * By definition, invoking [apply] and then [expansion] should produce the same
 * result.
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
 * The result is a Reduced Ordered Binary Decision Diagram (ROBDD).
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
 * and [Pair.second] is the value of type [T] computed with the
 * Shannon Expansion.
 *
 * By definition, invoking [apply] and then [expansion] should produce the same
 * result.
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
 * Performs the "And" unary boolean operation over two
 * [BinaryDecisionDiagram]s. The result is a Reduced Ordered Binary
 * Decision Diagram (ROBDD).
 */
@JsName("and")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.and(that: BinaryDecisionDiagram<T>): BinaryDecisionDiagram<T> =
    runOperationAndCatchErrors {
        this.apply(that) { a, b -> a && b }
    }

/**
 * Performs the "And" unary boolean operation over two [BinaryDecisionDiagram]s
 * and computes a value using the Shannon Expansion over the result.
 * The result is an instance of [Pair] of which [Pair.first] is the
 * Reduced Ordered Binary Decision Diagram (ROBDD) produced by the operation,
 * and [Pair.second] is the value of type [T] computed with the
 * Shannon Expansion.
 *
 * By definition, invoking [apply] and then [expansion] should produce the same
 * result.
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
 * Performs the "Or" unary boolean operation over two [BinaryDecisionDiagram]s.
 * The result is a Reduced Ordered Binary Decision Diagram (ROBDD).
 */
@JsName("or")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.or(that: BinaryDecisionDiagram<T>): BinaryDecisionDiagram<T> =
    runOperationAndCatchErrors {
        this.apply(that) { a, b -> a || b }
    }

/**
 * Performs the "Or" unary boolean operation over two [BinaryDecisionDiagram]s
 * and computes a value using the Shannon Expansion over the result.
 * The result is an instance of [Pair] of which [Pair.first] is the
 * Reduced Ordered Binary Decision Diagram (ROBDD) produced by the operation,
 * and [Pair.second] is the value of type [T] computed with the
 * Shannon Expansion.
 *
 * By definition, invoking [apply] and then [expansion] should produce the same
 * result.
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
