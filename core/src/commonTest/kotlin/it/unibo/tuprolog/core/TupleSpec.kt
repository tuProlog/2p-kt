package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.TupleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.TupleUtils
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Test class for [Tuple] companion object
 *
 * @author Enrico
 */
class TupleSpec :
    FunSpec({
        test("wrap-if-needed-with-no-args") {
            assertSame(Truth.TRUE, Tuple.wrapIfNeeded())
        }

        test("wrap-if-needed-with-one-argument") {
            val something = Atom.of("hey")
            assertSame(something, Tuple.wrapIfNeeded(something))
        }

        test("wrap-if-needed-with-two-or-more-arguments-returns-tuple-instance") {
            val something1 = Atom.of("hello")
            val something2 = Atom.of("world")

            val toBeTested = Tuple.wrapIfNeeded(something1, something2)

            TermTypeAssertionUtils.assertIsTuple(toBeTested)
            assertEquals("(hello, world)", toBeTested.toString())
        }

        test("wrap-if-needed-returns-default-if-no-arguments-provided") {
            val myDefault = Atom.of("myDefault")

            assertSame(myDefault, Tuple.wrapIfNeeded { myDefault })
        }

        withTests(nameFn = { "of-left-right/${it.id}" }, TupleUtils.tupleCases) { case ->
            assertEqualities(case.tuple(::TupleImpl), case.tuple { left, right -> Tuple.of(left, right) })
        }

        test("tuple-of-left-right-others") {
            val first = Var.of("A")
            val second = Atom.of("b")
            val third = Truth.FALSE
            val fourth = Truth.TRUE
            val correctInstance = TupleImpl(first, TupleImpl(second, TupleImpl(third, fourth)))

            val others = Tuple.of(third, fourth)
            val toBeTested = Tuple.of(first, second, others)

            assertEqualities(correctInstance, toBeTested)
        }

        withTests(nameFn = { "of-list/${it.id}" }, TupleUtils.tupleCases) { case ->
            assertEqualities(case.tuple(::TupleImpl), Tuple.of(case.elements.toList()))
        }

        test("tuple-of-list-should-complain-with-lower-than-two-elements") {
            assertFailsWith<IllegalArgumentException> { Tuple.of(listOf()) }
            assertFailsWith<IllegalArgumentException> { Tuple.of(listOf(Atom.of("a"))) }
        }

        withTests(nameFn = { "of-iterable/${it.id}" }, TupleUtils.tupleCases) { case ->
            assertEqualities(case.tuple(::TupleImpl), Tuple.of(case.elements.toList().asIterable()))
        }

        test("big-tuples-do-not-provoke-stack-overflow") {
            val nums = (0..100_000).toList()
            val tuple = Tuple.of(nums.map { Integer.of(it) })

            assertEquals(nums.joinToString(", ", "(", ")"), tuple.toString())

            val otherTuple = tuple.freshCopy()

            assertEquals(tuple.hashCode(), otherTuple.hashCode())
            assertEquals(tuple, otherTuple)
        }
    })
