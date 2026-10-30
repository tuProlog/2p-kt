package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
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
        val emptyTerminatedInstances = ConsUtils.onlyConsEmptyListTerminated(Cons.Companion::of)

        val pipedListInstances = ConsUtils.onlyConsPipeTerminated(Cons.Companion::of)

        val emptyTerminatedElementLists = ConsUtils.onlyConsEmptyListTerminatedElementLists

        val pipeTerminatedElementLists = ConsUtils.onlyConsPipeTerminatedElementLists

        test("empty-returns-empty-list") {
            assertEqualities(Empty.list(), LogicList.empty())
            assertEquals(Empty.list(), LogicList.empty())
        }

        test("of-no-vararg-terms") {
            assertEqualities(Empty.list(), LogicList.of())
            assertEquals(Empty.list(), LogicList.of())
        }

        test("of-vararg-terms") {
            val toBeTested = emptyTerminatedElementLists.map { LogicList.of(*it.toTypedArray()) }

            onCorrespondingItems(emptyTerminatedInstances, toBeTested, ::assertEqualities)
        }

        test("of-empty-iterable") {
            val toBeTested = LogicList.of(emptyList<Term>().asIterable())

            assertEqualities(Empty.list(), toBeTested)
            assertEquals(Empty.list(), toBeTested)
        }

        test("of-iterable-of-terms") {
            val toBeTested = emptyTerminatedElementLists.map { LogicList.of(it.asIterable()) }
            onCorrespondingItems(emptyTerminatedInstances, toBeTested, ::assertEqualities)
        }

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

        test("from-iterable-without-last-specified-does-not-behave-like-of-method") {
            val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it.asIterable()) }

            onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
        }

        test("from-iterable-specifying-last") {
            val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it) }

            onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
        }

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

        test("from-sequence-without-last-specified-does-not-behave-like-of-method") {
            val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it.asSequence()) }

            onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
        }

        test("from-sequence-specifying-last") {
            val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it) }

            onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
        }
    })
