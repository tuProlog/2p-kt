package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConsUtils
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
            assertEquals(Empty.list(), LogicList.empty())
        }

        test("of-no-vararg-terms") {
            assertEqualities(Empty.list(), LogicList.of())
            assertEquals(Empty.list(), LogicList.of())
        }

        emptyTerminated("of-varargs") { LogicList.of(*it.toTypedArray()) }

        test("of-empty-iterable") {
            val toBeTested = LogicList.of(emptyList<Term>().asIterable())

            assertEqualities(Empty.list(), toBeTested)
            assertEquals(Empty.list(), toBeTested)
        }

        emptyTerminated("of-iterable") { LogicList.of(it.asIterable()) }

        test("from-empty-iterable-without-last-specified-behaves-like-of-method") {
            val toBeTested = LogicList.from(emptyList<Term>().asIterable())

            assertEqualities(Empty.list(), toBeTested)
            assertEquals(Empty.list(), toBeTested)
        }

        test("from-empty-iterable-specifying-last-as-empty-list") {
            val toBeTested = LogicList.from(emptyList<Term>().asIterable(), Empty.list())

            assertEqualities(Empty.list(), toBeTested)
            assertEquals(Empty.list(), toBeTested)
        }

        test("from-empty-iterable-specifying-last") {
            assertFailsWith<IllegalArgumentException> { LogicList.from(emptyList<Term>().asIterable(), Truth.FALSE) }
        }

        pipeTerminated("from-iterable-without-last") { LogicList.from(it.asIterable()) }

        pipeTerminated("from-iterable-specifying-last") { LogicList.from(it) }

        test("from-empty-sequence-without-last-specified-behaves-like-of-method") {
            val toBeTested = LogicList.from(emptyList<Term>().asSequence())

            assertEqualities(Empty.list(), toBeTested)
            assertEquals(Empty.list(), toBeTested)
        }

        test("from-empty-sequence-specifying-last-as-empty-list") {
            val toBeTested = LogicList.from(emptyList<Term>().asSequence(), Empty.list())

            assertEqualities(Empty.list(), toBeTested)
            assertEquals(Empty.list(), toBeTested)
        }

        test("from-empty-sequence-specifying-last") {
            assertFailsWith<IllegalArgumentException> { LogicList.from(emptyList<Term>().asSequence(), Truth.FALSE) }
        }

        pipeTerminated("from-sequence-without-last") { LogicList.from(it.asSequence()) }

        pipeTerminated("from-sequence-specifying-last") { LogicList.from(it) }
    })
