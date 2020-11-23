package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor

internal class VariableBinaryDecisionDiagram<T: Comparable<T>>(
        value: T,
        low: AbstractBinaryDecisionDiagram<T>,
        high: AbstractBinaryDecisionDiagram<T>
): AbstractBinaryDecisionDiagram<T>(
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
        return VariableBinaryDecisionDiagram(value!!, low!!.apply(unaryOp), high!!.apply(unaryOp))
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
        val newValue: T

        if (this.value!! <= other.value!!) {
            newValue = this.value
            firstHigh = this.high!!
            firstLow = this.low!!
        } else {
            newValue = other.value
            firstLow = this
            firstHigh = this
        }
        if (other.value <= this.value) {
            secondHigh = other.high!!
            secondLow = other.low!!
        } else {
            secondLow = other
            secondHigh = other
        }

        return VariableBinaryDecisionDiagram<T>(
            newValue,
            firstLow.apply(secondLow, binaryOp),
            firstHigh.apply(secondHigh, binaryOp)
        )
    }
}

fun <T: Comparable<T>> BinaryDecisionDiagram.Companion.ofVar(value: T): BinaryDecisionDiagram<T> =
    VariableBinaryDecisionDiagram<T>(
        value,
        TerminalBinaryDecisionDiagram<T>(false),
        TerminalBinaryDecisionDiagram<T>(true)
    )
