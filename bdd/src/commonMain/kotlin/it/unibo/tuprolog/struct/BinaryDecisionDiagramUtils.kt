/**
 * @author Jason Dellaluce
 */

@file:JvmName("BinaryDecisionDiagramUtils")

package it.unibo.tuprolog.struct

import it.unibo.tuprolog.struct.exception.DataStructureOperationException
import it.unibo.tuprolog.struct.impl.ApplyBinaryDecisionDiagramVisitor
import it.unibo.tuprolog.struct.impl.TreeStringBinaryDecisionDiagramVisitor
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
