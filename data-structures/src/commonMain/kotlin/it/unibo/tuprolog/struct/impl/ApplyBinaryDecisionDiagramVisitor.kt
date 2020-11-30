package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor
import it.unibo.tuprolog.struct.exception.DataStructureOperationException
import kotlin.js.JsName

/**
 * This visitor implements the "Apply" [BinaryDecisionDiagram] construction algorithm. Although it is one of
 * the simplest solutions for BDD construction, it is able to produce Reduced Ordered Binary Decision Diagrams (ROBDD).
 * The "apply" operation can be used to perform any boolean operation over BDDs.
 *
 * @author Jason Dellaluce
 */
internal sealed class ApplyBinaryDecisionDiagramVisitor<T : Comparable<T>> : BinaryDecisionDiagramVisitor<T> {
    var result: BinaryDecisionDiagram<T>? = null

    data class Unary<T : Comparable<T>>(
        private val operator: (first: Boolean) -> Boolean
    ) : ApplyBinaryDecisionDiagramVisitor<T>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
            result = BinaryDecisionDiagram.Terminal(operator(node.value))
        }

        override fun visit(node: BinaryDecisionDiagram.Var<T>) {
            result = BinaryDecisionDiagram.Var(node.value, node.low.apply(operator), node.high.apply(operator))
        }
    }

    data class Binary<T : Comparable<T>>(
        private val thatNode: BinaryDecisionDiagram<T>,
        private val operator: (first: Boolean, second: Boolean) -> Boolean
    ) : ApplyBinaryDecisionDiagramVisitor<T>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
            result = when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    BinaryDecisionDiagram.Terminal(operator(node.value, thatNode.value))
                }
                is BinaryDecisionDiagram.Var -> {
                    BinaryDecisionDiagram.Var(
                        thatNode.value,
                        node.apply(thatNode.low, operator),
                        node.apply(thatNode.high, operator)
                    )
                }
            }
        }

        override fun visit(node: BinaryDecisionDiagram.Var<T>) {
            result = when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    thatNode.apply(node, operator)
                }
                is BinaryDecisionDiagram.Var -> {
                    val newValue: T
                    val firstLow: BinaryDecisionDiagram<T>
                    val firstHigh: BinaryDecisionDiagram<T>
                    val secondLow: BinaryDecisionDiagram<T>
                    val secondHigh: BinaryDecisionDiagram<T>

                    if (node.value <= thatNode.value) {
                        newValue = node.value
                        firstHigh = node.high
                        firstLow = node.low
                    } else {
                        newValue = thatNode.value
                        firstLow = node
                        firstHigh = node
                    }
                    if (thatNode.value <= node.value) {
                        secondHigh = thatNode.high
                        secondLow = thatNode.low
                    } else {
                        secondLow = thatNode
                        secondHigh = thatNode
                    }

                    BinaryDecisionDiagram.Var(
                        newValue,
                        firstLow.apply(secondLow, operator),
                        firstHigh.apply(secondHigh, operator)
                    )
                }
            }
        }
    }
}

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

/**
 * Uses the "Apply" [BinaryDecisionDiagram] construction algorithm to perform the "Not" unary boolean operation.
 * @author Jason Dellaluce
 */
@JsName("applyNot")
fun <T : Comparable<T>> BinaryDecisionDiagram<T>.applyNot(): BinaryDecisionDiagram<T> {
    return this.apply { a -> !a }
}

/**
 * Uses the "Apply" [BinaryDecisionDiagram] construction algorithm to perform the "And" binary boolean operation over
 * [this] BDD and another one, returning a newly constructed BDD as result.
 *
 * @author Jason Dellaluce
 */
@JsName("applyAnd")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.applyAnd(that: BinaryDecisionDiagram<T>):
    BinaryDecisionDiagram<T> {
        return this.apply(that) { a, b -> a && b }
    }

/**
 * Uses the "Apply" [BinaryDecisionDiagram] construction algorithm to perform the "Or" binary boolean operation over
 * [this] BDD and another one, returning a newly constructed BDD as result.
 *
 * @author Jason Dellaluce
 */
@JsName("applyOr")
infix fun <T : Comparable<T>> BinaryDecisionDiagram<T>.applyOr(that: BinaryDecisionDiagram<T>):
    BinaryDecisionDiagram<T> {
        return this.apply(that) { a, b -> a || b }
    }
