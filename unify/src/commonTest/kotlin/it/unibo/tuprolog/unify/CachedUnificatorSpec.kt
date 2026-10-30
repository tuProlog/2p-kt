package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [CachedUnificator]
 */
class CachedUnificatorSpec :
    FunSpec({
        class CountingUnificator(
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

        test("context-delegates-to-the-decorated-unificator") {
            val context = Substitution.of(Var.of("X"), Atom.of("a"))
            val cached = CachedUnificator(CountingUnificator(context), 10)

            assertEquals(context, cached.context)
        }

        test("mgu-is-computed-only-once-for-the-same-request") {
            val decorated = CountingUnificator()
            val cached = CachedUnificator(decorated, 10)
            val a = Atom.of("a")
            val b = Atom.of("b")

            val first = cached.mgu(a, b, true)
            val second = cached.mgu(a, b, true)

            assertSame(first, second)
            assertEquals(1, decorated.mguCalls)
        }

        test("mgu-is-recomputed-for-different-terms") {
            val decorated = CountingUnificator()
            val cached = CachedUnificator(decorated, 10)
            val a = Atom.of("a")
            val b = Atom.of("b")
            val c = Atom.of("c")

            cached.mgu(a, b, true)
            cached.mgu(a, c, true)

            assertEquals(2, decorated.mguCalls)
        }

        test("mgu-is-recomputed-when-occur-check-flag-differs") {
            val decorated = CountingUnificator()
            val cached = CachedUnificator(decorated, 10)
            val a = Atom.of("a")
            val b = Atom.of("b")

            cached.mgu(a, b, true)
            cached.mgu(a, b, false)

            assertEquals(2, decorated.mguCalls)
        }

        test("two-arg-mgu-overload-is-cached-together-with-the-three-arg-one") {
            val decorated = CountingUnificator()
            val cached = CachedUnificator(decorated, 10)
            val a = Atom.of("a")
            val b = Atom.of("b")

            cached.mgu(a, b) // occurCheckEnabled defaults to true
            cached.mgu(a, b, true)

            assertEquals(1, decorated.mguCalls)
        }

        test("match-delegates-through-the-cached-mgu") {
            val decorated = CountingUnificator()
            val cached = CachedUnificator(decorated, 10)
            val a = Atom.of("a")
            val b = Atom.of("b")

            cached.match(a, b, true)
            cached.match(a, b, true)

            assertEquals(1, decorated.mguCalls)
        }

        test("unify-delegates-through-the-cached-mgu") {
            val decorated = CountingUnificator()
            val cached = CachedUnificator(decorated, 10)
            val a = Atom.of("a")
            val b = Atom.of("b")

            cached.unify(a, b, true)
            cached.unify(a, b, true)

            assertEquals(1, decorated.mguCalls)
        }

        test("merge-is-computed-only-once-for-the-same-request") {
            val decorated = CountingUnificator()
            val cached = CachedUnificator(decorated, 10)
            val s1 = Substitution.of(Var.of("X"), Atom.of("a"))
            val s2 = Substitution.of(Var.of("Y"), Atom.of("b"))

            val first = cached.merge(s1, s2, true)
            val second = cached.merge(s1, s2, true)

            assertSame(first, second)
            assertEquals(1, decorated.mergeCalls)
        }

        test("merge-is-recomputed-when-occur-check-flag-differs") {
            val decorated = CountingUnificator()
            val cached = CachedUnificator(decorated, 10)
            val s1 = Substitution.of(Var.of("X"), Atom.of("a"))
            val s2 = Substitution.of(Var.of("Y"), Atom.of("b"))

            cached.merge(s1, s2, true)
            cached.merge(s1, s2, false)

            assertEquals(2, decorated.mergeCalls)
        }

        test("evicted-entries-are-recomputed-on-next-request") {
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

        test("mgu-and-merge-share-the-same-underlying-cache-capacity") {
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

        test("unificator-cached-factory-wraps-a-plain-unificator-directly") {
            val decorated = CountingUnificator()

            val cached = Unificator.cached(decorated, 5) as CachedUnificator

            assertSame(decorated, cached.decorated)
        }

        test("unificator-cached-factory-unwraps-an-already-cached-unificator-instead-of-double-wrapping") {
            val decorated = CountingUnificator()
            val onceCached = Unificator.cached(decorated, 1) as CachedUnificator

            val twiceCached = Unificator.cached(onceCached, 10) as CachedUnificator

            assertSame(decorated, twiceCached.decorated)
        }
    })
