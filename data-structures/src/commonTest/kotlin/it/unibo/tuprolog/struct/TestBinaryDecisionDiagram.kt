package it.unibo.tuprolog.struct

import it.unibo.tuprolog.struct.impl.applyAnd
import it.unibo.tuprolog.struct.impl.applyNot
import it.unibo.tuprolog.struct.impl.applyOr
import it.unibo.tuprolog.struct.impl.toTreeString
import kotlin.test.Test
import kotlin.test.assertTrue

class TestBinaryDecisionDiagram {
    private val doubleEpsilon = 0.0001

    private class ComparablePair(val id: Long, val first: String, val second: Double) : Comparable<ComparablePair> {
        override fun compareTo(other: ComparablePair): Int {
            var res = this.id.compareTo(other.id)
            if (res == 0) {
                res = this.first.compareTo(other.first)
            }
            return res
        }

        override fun equals(other: Any?): Boolean {
            return if (other is ComparablePair) this.compareTo(other) == 0 else false
        }

        override fun toString(): String {
            return "[$id] $second::$first"
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + first.hashCode()
            result = 31 * result + second.hashCode()
            return result
        }
    }

    /** Internal visitor to calculate a probability over a probabilistic boolean formula
     * (a.k.a. a boolean formula in which each variable represents a probability value).
     * This helps simulating our specific use case for BDDs right away. */
    private class ProbBinaryDecisionDiagramVisitor : BinaryDecisionDiagramVisitor<ComparablePair> {
        var prob = 0.0

        override fun visit(node: BinaryDecisionDiagram.Terminal<ComparablePair>) {
            prob = if (node.value) 1.0 else 0.0
        }

        override fun visit(node: BinaryDecisionDiagram.Var<ComparablePair>) {
            val subVisitor = ProbBinaryDecisionDiagramVisitor()
            node.low.accept(subVisitor)
            val probLow = subVisitor.prob
            node.high.accept(subVisitor)
            val probHigh = subVisitor.prob
            prob = node.value.second * probHigh + (1.0 - node.value.second) * probLow
        }
    }

    /** Test adapted from: https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#noisy-or-multiple-rules-for-the-same-head
     * Probabilities to base-level "someHeads" predicates have been added to simulate "Probabilistic Clauses". */
    @Test
    fun testApplyWithProbSomeHeads() {
        val bdd = (
            BinaryDecisionDiagram.Var(ComparablePair(0, "someHeads", 0.2)) applyAnd
                BinaryDecisionDiagram.Var(ComparablePair(1, "heads1", 0.5))
            ) applyOr (
            BinaryDecisionDiagram.Var(ComparablePair(2, "someHeads", 0.5)) applyAnd
                BinaryDecisionDiagram.Var(ComparablePair(3, "heads2", 0.6))
            )
        val visitor = ProbBinaryDecisionDiagramVisitor()
        bdd.accept(visitor)
        println(bdd.toTreeString())
        assertTrue(visitor.prob >= 0.37 - doubleEpsilon)
        assertTrue(visitor.prob <= 0.37 + doubleEpsilon)
    }

    /** Test adapted from: https://dtai.cs.kuleuven.be/problog/tutorial/basic/02_bayes.html#probabilistic-clauses
     * In this test evidence predicates are not considered. */
    @Test
    fun testApplyWithProbAlarmNegationsNoEvidence() {
        val burglary = BinaryDecisionDiagram.Var(ComparablePair(0, "burglary", 0.7))
        val earthquake = BinaryDecisionDiagram.Var(ComparablePair(1, "earthquake", 0.2))
        val solution = (
            BinaryDecisionDiagram.Var(ComparablePair(2, "alarm", 0.9))
                applyAnd burglary applyAnd earthquake
            ) applyOr (
            BinaryDecisionDiagram.Var(ComparablePair(3, "alarm", 0.8))
                applyAnd burglary applyAnd earthquake.applyNot()
            ) applyOr (
            BinaryDecisionDiagram.Var(ComparablePair(4, "alarm", 0.1))
                applyAnd burglary.applyNot() applyAnd earthquake
            )
        val visitor = ProbBinaryDecisionDiagramVisitor()
        solution.accept(visitor)
        assertTrue(visitor.prob >= 0.58 - doubleEpsilon)
        assertTrue(visitor.prob <= 0.58 + doubleEpsilon)
    }
}
