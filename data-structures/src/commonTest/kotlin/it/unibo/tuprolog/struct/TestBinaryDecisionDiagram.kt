package it.unibo.tuprolog.struct

import it.unibo.tuprolog.struct.impl.ofVar
import it.unibo.tuprolog.struct.impl.toTreeString
import kotlin.test.Test
import kotlin.test.assertTrue

class TestBinaryDecisionDiagram {
    private val doubleEpsilon = 0.0001

    private class ComparablePair(val id: Long, val first: String, val second: Double) : Comparable<ComparablePair> {
        override fun compareTo(other: ComparablePair): Int {
            return this.id.compareTo(other.id)
        }

        override fun toString(): String {
            return "[$id] $second::$first"
        }
    }

    /* Internal visitor to calculate a probability over a probabilistic boolean formula
       (a.k.a. a boolean formula in which each variable represents a probability value. */
    private class ProbBinaryDecisionDiagramVisitor : BinaryDecisionDiagramVisitor<ComparablePair> {
        var prob = 0.0

        override fun visit(value: Boolean) {
            prob = if (value) 1.0 else 0.0
        }

        override fun visit(
            value: ComparablePair,
            low: BinaryDecisionDiagram<ComparablePair>,
            high: BinaryDecisionDiagram<ComparablePair>
        ) {
            val subVisitor = ProbBinaryDecisionDiagramVisitor()
            low.accept(subVisitor)
            val probLow = subVisitor.prob
            high.accept(subVisitor)
            val probHigh = subVisitor.prob
            prob = value.second * probHigh + (1.0 - value.second) * probLow
        }
    }

    /* Test adapted from: https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#noisy-or-multiple-rules-for-the-same-head
       Probabilities to base-level "someHeads" predicates have been added to simulate "Probabilistic Clauses". */
    @Test
    fun testProbabilitySomeHeads() {
        val bdd = (
            BinaryDecisionDiagram.ofVar(ComparablePair(0, "someHeads", 0.2)) and
                BinaryDecisionDiagram.ofVar(ComparablePair(1, "heads1", 0.5))
            ) or (
            BinaryDecisionDiagram.ofVar(ComparablePair(2, "someHeads", 0.5)) and
                BinaryDecisionDiagram.ofVar(ComparablePair(3, "heads2", 0.6))
            )
        val visitor = ProbBinaryDecisionDiagramVisitor()
        bdd.accept(visitor)
        println(bdd.toTreeString())
        assertTrue(visitor.prob >= 0.37 - doubleEpsilon)
        assertTrue(visitor.prob <= 0.37 + doubleEpsilon)
    }

    /* Test adapted from: https://dtai.cs.kuleuven.be/problog/tutorial/basic/02_bayes.html#probabilistic-clauses
       In this test evidence predicates are not considered. */
    @Test
    fun testProbabilityAlarmNegationsNoEvidence() {
        val burglary = BinaryDecisionDiagram.ofVar(ComparablePair(0, "burglary", 0.7))
        val earthquake = BinaryDecisionDiagram.ofVar(ComparablePair(1, "earthquake", 0.2))
        val solution = (
            BinaryDecisionDiagram.ofVar(ComparablePair(2, "alarm", 0.9))
                and burglary and earthquake
            ) or (
            BinaryDecisionDiagram.ofVar(ComparablePair(3, "alarm", 0.8))
                and burglary and earthquake.not()
            ) or (
            BinaryDecisionDiagram.ofVar(ComparablePair(4, "alarm", 0.1))
                and burglary.not() and earthquake
            )
        val visitor = ProbBinaryDecisionDiagramVisitor()
        solution.accept(visitor)
        assertTrue(visitor.prob >= 0.58 - doubleEpsilon)
        assertTrue(visitor.prob <= 0.58 + doubleEpsilon)
    }
}
