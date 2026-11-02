package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.utils.cursor
import it.unibo.tuprolog.utils.itemWiseEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListCreationSpec :
    FunSpec({
        fun IntRange.toTerms(): Sequence<Term> = asSequence().map { Integer.of(it) }

        test("list-from-with-list-as-last-is-like-append") {
            val list = List.from((1..5).toTerms(), last = List.of((6..10).toTerms()))
            assertTrue { list.last == EmptyList() }
            assertTrue { list.isWellFormed }
            assertEquals(10, list.size)
            assertEquals((1..10).joinToString(", ", "[", "]"), list.toString())
            assertTrue { itemWiseEquals((1..10).toTerms(), list.toSequence()) }
            assertTrue { itemWiseEquals((1..10).toTerms() + EmptyList(), list.unfoldedSequence) }
            assertEquals((1..10).toTerms().toList(), list.toList())
            assertEquals((1..10).toTerms().toList() + EmptyList(), list.unfoldedList)
            assertEquals(List.of((1..10).toTerms()), list)
        }

        test("from/vararg-ends-with-the-last-item") {
            val (a, b) = listOf(Atom.of("a"), Atom.of("b"))
            assertEquals(Cons.of(a, b), List.from(a, b))
        }

        test("from/non-list-iterable") {
            val (a, b) = listOf(Atom.of("a"), Atom.of("b"))
            assertEquals(List.of(a, b), List.from(setOf(a, b), EmptyList()))
        }

        test("from/cursor-ends-with-the-last-item") {
            val (a, b) = listOf(Atom.of("a"), Atom.of("b"))
            assertEquals(Cons.of(a, b), List.from(listOf(a, b).cursor()))
        }
    })
