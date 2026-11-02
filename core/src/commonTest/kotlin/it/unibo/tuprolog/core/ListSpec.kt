package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.utils.cursor
import it.unibo.tuprolog.core.List as LogicList

/**
 * Test class for [List] companion object
 *
 * @author Enrico
 */
class ListSpec :
    FunSpec({
        fun emptyTerminated(
            property: String,
            toBeTested: (kotlin.collections.List<Term>) -> Term,
        ) = withTests(nameFn = { "$property/${it.id}" }, ConsUtils.onlyConsEmptyListTerminatedCases) { case ->
            assertEqualities(case.cons(Cons.Companion::of), toBeTested(case.elements))
        }

        fun pipeTerminated(
            property: String,
            toBeTested: (kotlin.collections.List<Term>) -> Term,
        ) = withTests(nameFn = { "$property/${it.id}" }, ConsUtils.onlyConsPipeTerminatedCases) { case ->
            assertEqualities(case.cons(Cons.Companion::of), toBeTested(case.elements))
        }

        test("empty-returns-empty-list") {
            assertEqualities(Empty.list(), LogicList.empty())
            LogicList.empty() shouldBe Empty.list()
        }

        test("of-no-vararg-terms") {
            assertEqualities(Empty.list(), LogicList.of())
            LogicList.of() shouldBe Empty.list()
        }

        emptyTerminated("of-varargs") { LogicList.of(*it.toTypedArray()) }

        test("of-empty-iterable") {
            val toBeTested = LogicList.of(emptyList<Term>().asIterable())

            assertEqualities(Empty.list(), toBeTested)
            toBeTested shouldBe Empty.list()
        }

        emptyTerminated("of-iterable") { LogicList.of(it.asIterable()) }

        test("from-empty-iterable-without-last-specified-behaves-like-of-method") {
            val toBeTested = LogicList.from(emptyList<Term>().asIterable())

            assertEqualities(Empty.list(), toBeTested)
            toBeTested shouldBe Empty.list()
        }

        test("from-empty-iterable-specifying-last-as-empty-list") {
            val toBeTested = LogicList.from(emptyList<Term>().asIterable(), Empty.list())

            assertEqualities(Empty.list(), toBeTested)
            toBeTested shouldBe Empty.list()
        }

        test("from-empty-iterable-specifying-last") {
            shouldThrow<IllegalArgumentException> { LogicList.from(emptyList<Term>().asIterable(), Truth.FALSE) }
        }

        pipeTerminated("from-iterable-without-last") { LogicList.from(it.asIterable()) }

        pipeTerminated("from-iterable-specifying-last") { LogicList.from(it) }

        test("from-empty-sequence-without-last-specified-behaves-like-of-method") {
            val toBeTested = LogicList.from(emptyList<Term>().asSequence())

            assertEqualities(Empty.list(), toBeTested)
            toBeTested shouldBe Empty.list()
        }

        test("from-empty-sequence-specifying-last-as-empty-list") {
            val toBeTested = LogicList.from(emptyList<Term>().asSequence(), Empty.list())

            assertEqualities(Empty.list(), toBeTested)
            toBeTested shouldBe Empty.list()
        }

        test("from-empty-sequence-specifying-last") {
            shouldThrow<IllegalArgumentException> { LogicList.from(emptyList<Term>().asSequence(), Truth.FALSE) }
        }

        pipeTerminated("from-sequence-without-last") { LogicList.from(it.asSequence()) }

        pipeTerminated("from-sequence-specifying-last") { LogicList.from(it) }

        test("substituting-iterator/applies-the-unifier-to-each-item") {
            val x = Var.of("X")
            val a = Atom.of("a")
            val list = LogicList.of(x, x)
            val unifier = Substitution.unifier(x, a)
            (
                ListIterator.Substituting
                    .All(list, unifier)
                    .asSequence()
                    .toList()
            ) shouldBe listOf(a, a, EmptyList())
            (
                ListIterator.Substituting
                    .SkippingLast(list, unifier)
                    .asSequence()
                    .toList()
            ) shouldBe listOf(a, a)
        }

        test("skipping-last-iterator/throws-past-the-end") {
            val iterator = ListIterator.SkippingLast(LogicList.of(Atom.of("a")))
            iterator.next()
            shouldThrow<NoSuchElementException> { iterator.next() }
        }

        test("lazy-partial-list/fresh-copy-renames-consistently") {
            val (x, y, z) = listOf(Var.of("X"), Var.of("Y"), Var.of("Z"))
            val list = LogicList.from(listOf<Term>(x, y, x).cursor(), z)
            val copy = list.freshCopy()
            copy.variables.toSet().size shouldBe 3
            copy.castToList().toList()[2] shouldBe copy[0]
            copy[0] shouldNotBe x
            list.structurallyEquals(copy).shouldBeTrue()
        }

        test("lazy-partial-list/keeps-its-tail-and-tags") {
            val (x, z) = listOf(Var.of("X"), Var.of("Z"))
            val list = LogicList.from(listOf<Term>(x).cursor(), z)
            list.last shouldBe z
            list.replaceTags(mapOf("t" to 1)).tags shouldBe mapOf<String, Any>("t" to 1)
        }
    })
