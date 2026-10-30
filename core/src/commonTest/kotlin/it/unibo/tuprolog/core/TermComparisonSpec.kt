package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class TermComparisonSpec :
    FunSpec({
        val base =
            listOf(
                Var.of("A"),
                Var.of("A"),
                Var.of("B"),
                Var.of("C"),
                Var.anonymous(),
                Var.anonymous(),
            ).sortedWith { x, y -> compareStringsLocaleIndependently(x.completeName, y.completeName) } +
                listOf(
                    Real.of(1.1),
                    Real.of(2.2),
                ).sortedWith { x, y -> x.decimalValue.compareTo(y.decimalValue) } +
                listOf(
                    Integer.of(1),
                    Integer.of(2),
                ).sortedWith { x, y -> x.intValue.compareTo(y.intValue) } +
                listOf(
                    Atom.of(""),
                    Atom.of("a"),
                    Atom.of("a b"),
                    Atom.of("b"),
                ).sortedWith { x, y -> compareStringsLocaleIndependently(x.value, y.value) }

        val ordered =
            base + base.map { Struct.of("f", it) } + base.map { Struct.of("g", it) }

        val unordered = ordered.shuffled()

        test("compare-to") {
            val reordered = unordered.sortedWith { a, b -> a.compareTo(b) }
            assertEquals(ordered, reordered)
        }

        test("default-term-comparator") {
            val reordered = unordered.sortedWith(TermComparator.DefaultComparator)
            assertEquals(ordered, reordered)
        }
    })
