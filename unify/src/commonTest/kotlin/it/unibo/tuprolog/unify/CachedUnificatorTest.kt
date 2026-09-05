package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [CachedUnificator]
 */
internal class CachedUnificatorTest {
    /** A [Unificator] that counts its own invocations and returns a fresh, call-numbered result every time */
    private class CountingUnificator(
        override val context: Substitution = Substitution.empty(),
    ) : Unificator {
        var mguCalls = 0
            private set
        var mergeCalls = 0
            private set

        override fun mgu(
            term1: Term,
            term2: Term,
            occurCheckEnabled: Boolean,
        ): Substitution {
            mguCalls++
            return Substitution.of(Var.of("R"), Atom.of("mgu-$mguCalls"))
        }

        override fun merge(
            substitution1: Substitution,
            substitution2: Substitution,
            occurCheckEnabled: Boolean,
        ): Substitution {
            mergeCalls++
            return Substitution.of(Var.of("R"), Atom.of("merge-$mergeCalls"))
        }
    }

    @Test
    fun contextDelegatesToTheDecoratedUnificator() {
        val context = Substitution.of(Var.of("X"), Atom.of("a"))
        val cached = CachedUnificator(CountingUnificator(context), 10)

        assertEquals(context, cached.context)
    }

    @Test
    fun mguIsComputedOnlyOnceForTheSameRequest() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 10)
        val a = Atom.of("a")
        val b = Atom.of("b")

        val first = cached.mgu(a, b, true)
        val second = cached.mgu(a, b, true)

        assertSame(first, second)
        assertEquals(1, decorated.mguCalls)
    }

    @Test
    fun mguIsRecomputedForDifferentTerms() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 10)
        val a = Atom.of("a")
        val b = Atom.of("b")
        val c = Atom.of("c")

        cached.mgu(a, b, true)
        cached.mgu(a, c, true)

        assertEquals(2, decorated.mguCalls)
    }

    @Test
    fun mguIsRecomputedWhenOccurCheckFlagDiffers() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 10)
        val a = Atom.of("a")
        val b = Atom.of("b")

        cached.mgu(a, b, true)
        cached.mgu(a, b, false)

        assertEquals(2, decorated.mguCalls)
    }

    @Test
    fun twoArgMguOverloadIsCachedTogetherWithTheThreeArgOne() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 10)
        val a = Atom.of("a")
        val b = Atom.of("b")

        cached.mgu(a, b) // occurCheckEnabled defaults to true
        cached.mgu(a, b, true)

        assertEquals(1, decorated.mguCalls)
    }

    @Test
    fun matchDelegatesThroughTheCachedMgu() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 10)
        val a = Atom.of("a")
        val b = Atom.of("b")

        cached.match(a, b, true)
        cached.match(a, b, true)

        assertEquals(1, decorated.mguCalls)
    }

    @Test
    fun unifyDelegatesThroughTheCachedMgu() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 10)
        val a = Atom.of("a")
        val b = Atom.of("b")

        cached.unify(a, b, true)
        cached.unify(a, b, true)

        assertEquals(1, decorated.mguCalls)
    }

    @Test
    fun mergeIsComputedOnlyOnceForTheSameRequest() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 10)
        val s1 = Substitution.of(Var.of("X"), Atom.of("a"))
        val s2 = Substitution.of(Var.of("Y"), Atom.of("b"))

        val first = cached.merge(s1, s2, true)
        val second = cached.merge(s1, s2, true)

        assertSame(first, second)
        assertEquals(1, decorated.mergeCalls)
    }

    @Test
    fun mergeIsRecomputedWhenOccurCheckFlagDiffers() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 10)
        val s1 = Substitution.of(Var.of("X"), Atom.of("a"))
        val s2 = Substitution.of(Var.of("Y"), Atom.of("b"))

        cached.merge(s1, s2, true)
        cached.merge(s1, s2, false)

        assertEquals(2, decorated.mergeCalls)
    }

    @Test
    fun evictedEntriesAreRecomputedOnNextRequest() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 1)
        val a = Atom.of("a")
        val b = Atom.of("b")
        val c = Atom.of("c")

        cached.mgu(a, b, true)
        cached.mgu(a, c, true) // capacity is 1: evicts the (a, b) entry
        cached.mgu(a, b, true) // must be recomputed

        assertEquals(3, decorated.mguCalls)
    }

    @Test
    fun mguAndMergeShareTheSameUnderlyingCacheCapacity() {
        val decorated = CountingUnificator()
        val cached = CachedUnificator(decorated, 1)
        val a = Atom.of("a")
        val b = Atom.of("b")
        val s1 = Substitution.of(Var.of("X"), Atom.of("a"))
        val s2 = Substitution.of(Var.of("Y"), Atom.of("b"))

        cached.mgu(a, b, true)
        cached.merge(s1, s2, true) // capacity is 1, shared with mgu: evicts the mgu entry
        cached.mgu(a, b, true) // must be recomputed

        assertEquals(2, decorated.mguCalls)
    }

    @Test
    fun unificatorCachedFactoryWrapsAPlainUnificatorDirectly() {
        val decorated = CountingUnificator()

        val cached = Unificator.cached(decorated, 5) as CachedUnificator

        assertSame(decorated, cached.decorated)
    }

    @Test
    fun unificatorCachedFactoryUnwrapsAnAlreadyCachedUnificatorInsteadOfDoubleWrapping() {
        val decorated = CountingUnificator()
        val onceCached = Unificator.cached(decorated, 1) as CachedUnificator

        val twiceCached = Unificator.cached(onceCached, 10) as CachedUnificator

        assertSame(decorated, twiceCached.decorated)
    }
}
