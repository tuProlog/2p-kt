package it.unibo.tuprolog.solve.problogimpl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor

class ProblogBinaryDecisionDiagramVisitor(
    private val table: MutableMap<String, Double> = mutableMapOf()
): BinaryDecisionDiagramVisitor<ProblogFact> {
    var prob = 0.0

    override fun visit(value: Boolean) {
        prob = if (value) 1.0 else 0.0
    }

    override fun visit(
            value: ProblogFact,
            low: BinaryDecisionDiagram<ProblogFact>,
            high: BinaryDecisionDiagram<ProblogFact>
    ) {
        val valueKey = "${value.id}_${value.head}"
        val tableProb = table[valueKey]

        /* Grab value from table if present */
        if (tableProb != null) {
            prob = tableProb
            return
        }

        /* Calculate probability and store it in table for later */
        prob = value.probability * high.probability(table)
            + (1.0 - value.probability) * low.probability(table)
        table[valueKey] = prob
    }
}

fun BinaryDecisionDiagram<ProblogFact>.probability(): Double {
    val visitor = ProblogBinaryDecisionDiagramVisitor()
    this.accept(visitor)
    return visitor.prob
}

private fun BinaryDecisionDiagram<ProblogFact>.probability(table: MutableMap<String, Double>): Double {
    val visitor = ProblogBinaryDecisionDiagramVisitor(table)
    this.accept(visitor)
    return visitor.prob
}