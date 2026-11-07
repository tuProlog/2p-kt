package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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

        test("last-argument-preserves-atom-ordering") {
            val a = Atom.of("Z")
            val b = Atom.of("a")
            val expected = a.compareTo(b).compareTo(0)
            Struct.of("f", a).compareTo(Struct.of("f", b)).compareTo(0) shouldBe expected
            Struct
                .of("f", Atom.of("x"), a)
                .compareTo(Struct.of("f", Atom.of("x"), b))
                .compareTo(0) shouldBe expected
        }

        test("compare-to") {
            val reordered = unordered.sortedWith { a, b -> a.compareTo(b) }
            reordered shouldBe ordered
        }

        test("default-term-comparator") {
            val reordered = unordered.sortedWith(TermComparator.DefaultComparator)
            reordered shouldBe ordered
        }
    })
