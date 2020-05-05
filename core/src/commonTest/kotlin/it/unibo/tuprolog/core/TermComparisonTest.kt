package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals

class TermComparisonTest {
    val ordered = listOf<Term>(
        Var.of("A"),
        Var.of("A"),
        Var.of("B"),
        Var.of("C"),
        Var.anonymous(),
        Var.anonymous(),
        Real.of(1.1),
        Real.of(2.2),
        Integer.of(1),
        Integer.of(2),
        Atom.of(""),
        Atom.of("a"),
        Atom.of("a b"),
        Atom.of("b"),
        Struct.of("f", Var.of("A")),
        Struct.of("f", Var.of("A")),
        Struct.of("f", Var.of("B")),
        Struct.of("f", Var.of("C")),
        Struct.of("f", Var.anonymous()),
        Struct.of("f", Real.of(1.1)),
        Struct.of("f", Real.of(2.2)),
        Struct.of("f", Integer.of(1)),
        Struct.of("f", Integer.of(2)),
        Struct.of("f", Atom.of("")),
        Struct.of("f", Atom.of("a")),
        Struct.of("f", Atom.of("a b")),
        Struct.of("f", Atom.of("b")),
        Struct.of("g", Var.of("A")),
        Struct.of("g", Var.of("A")),
        Struct.of("g", Var.of("B")),
        Struct.of("g", Var.of("C")),
        Struct.of("g", Var.anonymous()),
        Struct.of("g", Real.of(1.1)),
        Struct.of("g", Real.of(2.2)),
        Struct.of("g", Integer.of(1)),
        Struct.of("g", Integer.of(2)),
        Struct.of("g", Atom.of("")),
        Struct.of("g", Atom.of("a")),
        Struct.of("g", Atom.of("a b")),
        Struct.of("g", Atom.of("b"))
    )

    val unordered = ordered.shuffled()

    @Test
    fun testCompareTo() {
        val reordered = unordered.sortedWith(Comparator { a, b -> a.compareTo(b) })
        assertEquals(ordered, reordered)
    }

    @Test
    fun testDefaultTermComparator() {
        val reordered = unordered.sortedWith(TermComparator.DefaultComparator)
        assertEquals(ordered, reordered)
    }
}