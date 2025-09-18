package it.unibo.tuprolog.bdd

import kotlin.test.Test
import kotlin.test.assertTrue

class TestBinaryDecisionDiagram {
    private val doubleEpsilon = 0.0001

    private class ComparablePair(
        val id: Long,
        val first: String,
        val second: Double,
    ) : Comparable<ComparablePair> {
        override fun compareTo(other: ComparablePair): Int {
            var res = this.id.compareTo(other.id)
            if (res == 0) {
                res = this.first.compareTo(other.first)
            }
            return res
        }

        override fun equals(other: Any?): Boolean = if (other is ComparablePair) this.compareTo(other) == 0 else false

        override fun toString(): String = "[$id] $second::$first"

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + first.hashCode()
            result = 31 * result + second.hashCode()
            return result
        }
    }

    /** Internal function to calculate a probability over a probabilistic boolean formula
     * (a.k.a. a boolean formula in which each variable represents a probability value) through
     * Weighted Model Counting. This simulates our specific use case for BDDs, which
     * is what we are really interested in. On the other hand, this also effectively verifies
     * the correctness of the data structure itself, as the probability computation would
     * give bad results on badly constructed BDDs. */
    private fun BinaryDecisionDiagram<ComparablePair>.probability(): Double =
        this.expansion(0.0, 1.0) { node, low, high ->
            node.second * high + (1.0 - node.second) * low
        }

    /** Test adapted from: https://dtai.cs.kuleuven.be/problog/tutorial/basic/01_coins.html#noisy-or-multiple-rules-for-the-same-head
     * Probabilities to base-level "someHeads" predicates have been added to simulate "Probabilistic Clauses". */
    @Test
    fun testApplyWithProbSomeHeads() {
        val solution =
            (
                bddOf(ComparablePair(0, "someHeads", 0.2)) and
                    bddOf(ComparablePair(1, "heads1", 0.5))
            ) or (
                bddOf(ComparablePair(2, "someHeads", 0.5)) and
                    bddOf(ComparablePair(3, "heads2", 0.6))
            )
        val prob = solution.probability()
        assertTrue(prob >= 0.37 - doubleEpsilon)
        assertTrue(prob <= 0.37 + doubleEpsilon)
    }

    /** Test adapted from: https://dtai.cs.kuleuven.be/problog/tutorial/basic/02_bayes.html#probabilistic-clauses
     * In this test evidence predicates are not considered. */
    @Test
    fun testApplyWithProbAlarmNegationsNoEvidence() {
        val burglary = bddOf(ComparablePair(0, "burglary", 0.7))
        val earthquake = bddOf(ComparablePair(1, "earthquake", 0.2))
        val solution =
            (
                bddOf(ComparablePair(2, "alarm", 0.9))
                    and burglary and earthquake
            ) or (
                bddOf(ComparablePair(3, "alarm", 0.8))
                    and burglary and earthquake.not()
            ) or (
                bddOf(ComparablePair(4, "alarm", 0.1))
                    and burglary.not() and earthquake
            )
        val prob = solution.probability()
        assertTrue(prob >= 0.58 - doubleEpsilon)
        assertTrue(prob <= 0.58 + doubleEpsilon)
    }
}
