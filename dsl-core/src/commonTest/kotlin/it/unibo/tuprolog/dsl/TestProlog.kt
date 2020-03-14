package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.dsl.PlatformSpecificValues.ONE_POINT_ZERO
import it.unibo.tuprolog.dsl.PlatformSpecificValues.THREE_POINT_ONE_FLOAT
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class TestProlog {

    companion object {
        fun assertDSLCreationIsCorrect(expected: Term, actualCreator: Prolog.() -> Term) {
            assertEquals(expected, Prolog.empty().actualCreator())
        }

        fun assertDSLCreationIsCorrect(expectedCreator: Prolog.() -> Term, actualCreator: Prolog.() -> Term) {
            val prolog = Prolog.empty()
            assertEquals(prolog.expectedCreator(), prolog.actualCreator())
        }
    }

    @Test
    fun testNumOf() {
        assertDSLCreationIsCorrect(Integer.of(1)) {
            numOf(1)
        }
        assertDSLCreationIsCorrect(Integer.of(1)) {
            numOf(1L)
        }
        assertDSLCreationIsCorrect(Integer.of(1)) {
            numOf(1.toShort())
        }
        assertDSLCreationIsCorrect(Integer.of(1)) {
            numOf(1.toByte())
        }
        assertDSLCreationIsCorrect(Integer.of(1)) {
            numOf("1")
        }
        assertDSLCreationIsCorrect(Real.of("1.0")) {
            numOf(1.0)
        }
        assertDSLCreationIsCorrect(Real.of("1.0")) {
            numOf(1.0f)
        }
        assertDSLCreationIsCorrect(Real.of("1.0")) {
            numOf("1.0")
        }
        assertDSLCreationIsCorrect(Real.of("3.1")) {
            numOf(3.1)
        }
        assertDSLCreationIsCorrect(THREE_POINT_ONE_FLOAT) {
            numOf(3.1f)
        }
        assertDSLCreationIsCorrect(Real.of("3.1")) {
            numOf("3.1")
        }
    }

    @Test
    fun testUnderscore() {
        prolog {
            assertNotEquals(`_`, `_`)
            assertEquals(varOf("X"), varOf("X"))
            assertEquals(varOf("_"), varOf("_"))
        }
    }

    @Test
    fun testToTerm() {
        assertDSLCreationIsCorrect(Integer.of(1)) {
            1.toTerm()
        }
        assertDSLCreationIsCorrect(ONE_POINT_ZERO) {
            1.0.toTerm()
        }
        assertDSLCreationIsCorrect(Real.of("3.1")) {
            3.1.toTerm()
        }
        assertDSLCreationIsCorrect(Atom.of("a")) {
            "a".toTerm()
        }
        assertDSLCreationIsCorrect({ varOf("A") }) {
            "A".toTerm()
        }
        prolog {
            atomOf("a").let {
                assertSame(it, it.toTerm())
            }
        }
        assertDSLCreationIsCorrect(Cons.singleton(Atom.of("a"))) {
            kotlin.collections.listOf("a").toTerm()
        }
        assertDSLCreationIsCorrect(Cons.singleton(Atom.of("a"))) {
            listOf("a")
        }
        assertDSLCreationIsCorrect(List.of(Atom.of("a"))) {
            kotlin.collections.setOf("a").toTerm()
        }
        assertDSLCreationIsCorrect(List.of(Atom.of("a"))) {
            arrayOf("a").toTerm()
        }
        assertDSLCreationIsCorrect(List.of(Atom.of("a"))) {
            sequenceOf("a").toTerm()
        }
        assertDSLCreationIsCorrect(Set.of(Atom.of("a"))) {
            setOf("a")
        }
        assertDSLCreationIsCorrect(Tuple.of(Atom.of("a"), Atom.of("b"))) {
            tupleOf("a", "b")
        }
    }
}