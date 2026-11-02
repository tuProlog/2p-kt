package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.impl.TupleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.TupleUtils

/**
 * Test class for [Tuple] companion object
 *
 * @author Enrico
 */
class TupleSpec :
    FunSpec({
        test("wrap-if-needed-with-no-args") {
            Tuple.wrapIfNeeded() shouldBeSameInstanceAs Truth.TRUE
        }

        test("wrap-if-needed-with-one-argument") {
            val something = Atom.of("hey")
            Tuple.wrapIfNeeded(something) shouldBeSameInstanceAs something
        }

        test("wrap-if-needed-with-two-or-more-arguments-returns-tuple-instance") {
            val something1 = Atom.of("hello")
            val something2 = Atom.of("world")

            val toBeTested = Tuple.wrapIfNeeded(something1, something2)

            TermTypeAssertionUtils.assertIsTuple(toBeTested)
            toBeTested.toString() shouldBe "(hello, world)"
        }

        test("wrap-if-needed-returns-default-if-no-arguments-provided") {
            val myDefault = Atom.of("myDefault")

            (Tuple.wrapIfNeeded { myDefault }) shouldBeSameInstanceAs myDefault
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
            shouldThrow<IllegalArgumentException> { Tuple.of(listOf()) }
            shouldThrow<IllegalArgumentException> { Tuple.of(listOf(Atom.of("a"))) }
        }

        withTests(nameFn = { "of-iterable/${it.id}" }, TupleUtils.tupleCases) { case ->
            assertEqualities(case.tuple(::TupleImpl), Tuple.of(case.elements.toList().asIterable()))
        }

        test("big-tuples-do-not-provoke-stack-overflow") {
            val nums = (0..100_000).toList()
            val tuple = Tuple.of(nums.map { Integer.of(it) })

            tuple.toString() shouldBe nums.joinToString(", ", "(", ")")

            val otherTuple = tuple.freshCopy()

            otherTuple.hashCode() shouldBe tuple.hashCode()
            otherTuple shouldBe tuple
        }

        test("wrap-sequence-if-needed/several-terms-are-a-tuple") {
            val (a, b) = listOf(Atom.of("a"), Atom.of("b"))
            Tuple.wrapIfNeeded(sequenceOf(a, b)) shouldBe Tuple.of(a, b)
        }

        test("wrap-sequence-if-needed/one-term-stays-as-is") {
            Tuple.wrapIfNeeded(sequenceOf(Atom.of("a"))) shouldBe Atom.of("a")
        }

        test("wrap-sequence-if-needed/no-terms-is-true") {
            Tuple.wrapIfNeeded(emptySequence()) shouldBe Truth.TRUE
        }
    })
