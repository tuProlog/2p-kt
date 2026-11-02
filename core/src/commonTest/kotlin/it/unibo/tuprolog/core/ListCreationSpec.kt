package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.utils.cursor
import it.unibo.tuprolog.utils.itemWiseEquals

class ListCreationSpec :
    FunSpec({
        fun IntRange.toTerms(): Sequence<Term> = asSequence().map { Integer.of(it) }

        test("list-from-with-list-as-last-is-like-append") {
            val list = List.from((1..5).toTerms(), last = List.of((6..10).toTerms()))
            list.last shouldBe EmptyList()
            list.isWellFormed.shouldBeTrue()
            list.size shouldBe 10
            list.toString() shouldBe (1..10).joinToString(", ", "[", "]")
            itemWiseEquals((1..10).toTerms(), list.toSequence()).shouldBeTrue()
            itemWiseEquals((1..10).toTerms() + EmptyList(), list.unfoldedSequence).shouldBeTrue()
            list.toList() shouldBe (1..10).toTerms().toList()
            list.unfoldedList shouldBe ((1..10).toTerms().toList() + EmptyList())
            list shouldBe List.of((1..10).toTerms())
        }

        test("from/vararg-ends-with-the-last-item") {
            val (a, b) = listOf(Atom.of("a"), Atom.of("b"))
            List.from(a, b) shouldBe Cons.of(a, b)
        }

        test("from/non-list-iterable") {
            val (a, b) = listOf(Atom.of("a"), Atom.of("b"))
            List.from(setOf(a, b), EmptyList()) shouldBe List.of(a, b)
        }

        test("from/cursor-ends-with-the-last-item") {
            val (a, b) = listOf(Atom.of("a"), Atom.of("b"))
            List.from(listOf(a, b).cursor()) shouldBe Cons.of(a, b)
        }
    })
