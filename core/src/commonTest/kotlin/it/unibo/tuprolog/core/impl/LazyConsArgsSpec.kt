package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.cursor
import it.unibo.tuprolog.core.List as LogicList

private val head = Atom.of("a")
private val other = Atom.of("z")

/** A lazily-unfolded cons, whose args are a dedicated two-item list rather than a regular one. */
private fun lazyArgs(): List<Term> = LogicList.from(listOf(head, Atom.of("b")).cursor(), null).castToCons().args

/** The same args as a regular Kotlin list, which every operation must agree with. */
private fun referenceArgs(): List<Term> = lazyArgs().let { listOf(it[0], it[1]) }

private val operations: List<Pair<String, (List<Term>) -> Any?>> =
    listOf(
        "size" to { it.size },
        "is-empty" to { it.isEmpty() },
        "get" to { listOf(it[0], it[1]) },
        "get-out-of-range" to { runCatching { it[2] }.exceptionOrNull() is IndexOutOfBoundsException },
        "contains" to { listOf(head in it, other in it) },
        "contains-all" to { listOf(it.containsAll(listOf(head)), it.containsAll(listOf(head, other))) },
        "index-of" to { listOf(it.indexOf(head), it.indexOf(it[1]), it.indexOf(other)) },
        "last-index-of" to { listOf(it.lastIndexOf(head), it.lastIndexOf(it[1]), it.lastIndexOf(other)) },
        "iterator" to { it.iterator().asSequence().toList() },
        "sub-list" to { listOf(it.subList(0, 1), it.subList(1, 2), it.subList(0, 2)) },
        "list-iterator-forwards" to {
            val iterator = it.listIterator()
            buildList {
                while (iterator.hasNext()) add(listOf(iterator.nextIndex(), iterator.previousIndex(), iterator.next()))
                add(runCatching { iterator.next() }.exceptionOrNull() is NoSuchElementException)
            }
        },
        "list-iterator-backwards" to {
            val iterator = it.listIterator(it.size)
            buildList {
                while (iterator.hasPrevious()) add(listOf(iterator.previousIndex(), iterator.previous()))
                add(runCatching { iterator.previous() }.exceptionOrNull() is NoSuchElementException)
            }
        },
    )

class LazyConsArgsSpec :
    FunSpec({
        withTests(nameFn = { "behaves-like-a-list/${it.first}" }, operations) { (_, operation) ->
            operation(lazyArgs()) shouldBe operation(referenceArgs())
        }
    })
