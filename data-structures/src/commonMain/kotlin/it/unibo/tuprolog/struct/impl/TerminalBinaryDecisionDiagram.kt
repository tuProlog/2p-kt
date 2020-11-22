package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor

internal class TerminalBinaryDecisionDiagram<T>(
        bVal: Boolean
): AbstractBinaryDecisionDiagram<T>(
        index = Int.MAX_VALUE,
        boolValue = bVal,
        value = null,
        low = null,
        high = null,
), BinaryDecisionDiagram<T> {

    override val isTerminal: Boolean
        get() = true

    override fun accept(visitor: BinaryDecisionDiagramVisitor<T>) {
        visitor.visit(boolValue!!)
    }

    override fun apply(unaryOp: (first: Boolean) -> Boolean): AbstractBinaryDecisionDiagram<T> {
        return TerminalBinaryDecisionDiagram<T>(unaryOp(this.boolValue!!))
    }

    override fun apply(other: AbstractBinaryDecisionDiagram<T>, binaryOp: (first: Boolean, second: Boolean) -> Boolean)
            : AbstractBinaryDecisionDiagram<T> {
        if (other.isTerminal)
            return TerminalBinaryDecisionDiagram<T>(binaryOp(boolValue!!, other.boolValue!!))

        return VariableBinaryDecisionDiagram<T>(
            other.index,
            other.value!!,
            apply(other.low!!, binaryOp),
            apply(other.high!!, binaryOp)
        )
    }
}

fun <T> BinaryDecisionDiagram.Companion.ofTrueTerminal(): BinaryDecisionDiagram<T> =
    TerminalBinaryDecisionDiagram<T>(true)

fun <T> BinaryDecisionDiagram.Companion.ofFalseTerminal(): BinaryDecisionDiagram<T> =
    TerminalBinaryDecisionDiagram<T>(false)