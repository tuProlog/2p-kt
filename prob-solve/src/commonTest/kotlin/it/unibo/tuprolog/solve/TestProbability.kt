package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class TestProbability {
    @Test
    fun example() {
        val aRule = Rule.of(Atom.of("hello"), Atom.of("world"))
        assertEquals(DEFAULT_PROBABILITY, aRule.probability)
        val anotherRule = aRule.setProbability(0.5)
        assertEquals(aRule, anotherRule)
        assertEquals(DEFAULT_PROBABILITY, aRule.probability)
        assertEquals(0.5, anotherRule.probability)
        val yetAnotherRule = anotherRule.setProbability(1.5)
        assertEquals(aRule, yetAnotherRule)
        assertEquals(anotherRule, yetAnotherRule)
        assertEquals(1.0, yetAnotherRule.probability)
    }
}
