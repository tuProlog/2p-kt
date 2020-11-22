package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor
import it.unibo.tuprolog.struct.exception.DataStructureOperationException

internal class VariableBinaryDecisionDiagram<T>(
        index: Int,
        value: T,
        low: AbstractBinaryDecisionDiagram<T>,
        high: AbstractBinaryDecisionDiagram<T>
): AbstractBinaryDecisionDiagram<T>(
        index = index,
        boolValue = null,
        value = value,
        low = low,
        high = high,
), BinaryDecisionDiagram<T> {

    override val isTerminal: Boolean
        get() = false

    override fun accept(visitor: BinaryDecisionDiagramVisitor<T>) {
        visitor.visit(value!!, low!!, high!!)
    }

    override fun apply(unaryOp: (first: Boolean) -> Boolean): AbstractBinaryDecisionDiagram<T> {
        return VariableBinaryDecisionDiagram(index, value!!, low!!.apply(unaryOp), high!!.apply(unaryOp))
    }

    override fun apply(other: AbstractBinaryDecisionDiagram<T>, binaryOp: (first: Boolean, second: Boolean) -> Boolean)
            : AbstractBinaryDecisionDiagram<T> {
        if (other.isTerminal) {
            return other.apply(this, binaryOp)
        }

        val firstLow: AbstractBinaryDecisionDiagram<T>
        val firstHigh: AbstractBinaryDecisionDiagram<T>
        val secondLow: AbstractBinaryDecisionDiagram<T>
        val secondHigh: AbstractBinaryDecisionDiagram<T>
        val newIndex: Int
        val newValue: T

        if (this.index <= other.index) {
            newIndex = this.index
            newValue = this.value!!
            firstHigh = this.high!!
            firstLow = this.low!!
        } else {
            newIndex = other.index
            newValue = other.value!!
            firstLow = this
            firstHigh = this
        }
        if (other.index <= this.index) {
            secondHigh = other.high!!
            secondLow = other.low!!
        } else {
            secondLow = other
            secondHigh = other
        }

        return VariableBinaryDecisionDiagram<T>(
            newIndex,
            newValue,
            firstLow.apply(secondLow, binaryOp),
            firstHigh.apply(secondHigh, binaryOp)
        )
    }
}

fun <T> BinaryDecisionDiagram.Companion.ofVar(index: Int, value: T): BinaryDecisionDiagram<T> =
    VariableBinaryDecisionDiagram<T>(
        index,
        value,
        TerminalBinaryDecisionDiagram<T>(false),
        TerminalBinaryDecisionDiagram<T>(true)
    )
