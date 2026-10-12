package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Truth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestTermObjectifier {
    /**
     * A [Truth] which is none of [Truth.TRUE], [Truth.FALSE], nor [Truth.FAIL], and therefore admits
     * no representation in the plain-object scheme documented by [TermObjectifier].
     *
     * Everything but the functor, the dispatch to [TermVisitor.visitTruth], and [toString] is inherited
     * (via delegation) from [Truth.TRUE]. Equality is deliberately narrowed to reference identity: the
     * delegated members would resolve to the delegate, making this term equal to [Truth.TRUE] itself,
     * which is precisely what must not happen for a value the objectifier is unable to represent.
     */
    private class UnexpectedTruth : Truth by Truth.TRUE {
        override val functor: String = "unexpected"

        override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitTruth(this)

        override fun equals(other: Any?): Boolean = this === other

        override fun hashCode(): Int = System.identityHashCode(this)

        override fun toString(): String = functor
    }

    @Test
    fun testKnownTruthValuesAreObjectified() {
        val objectifier = TermObjectifier.default

        assertEquals(true, objectifier.objectify(Truth.TRUE))
        assertEquals(false, objectifier.objectify(Truth.FALSE))
        assertEquals("fail", objectifier.objectify(Truth.FAIL))
    }

    @Test
    fun testUnexpectedTruthValueIsRejected() {
        val thrown =
            assertFailsWith<NotImplementedError> {
                TermObjectifier.default.objectify(UnexpectedTruth())
            }
        assertEquals("Serialization of Truth value: unexpected", thrown.message)
    }
}
