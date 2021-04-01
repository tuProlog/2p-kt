package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.format
import kotlin.test.assertEquals

object TermFormatterUtils {
    private val common: Map<Term, String> = mapOf(
        Atom.of("a") to "a",
        Atom.of("a b") to "'a b'",
        Integer.of(1) to "1",
        Real.of("3.2") to "3.2",
        Var.of("A") to "A",
        Var.anonymous() to Var.ANONYMOUS_NAME,
        Var.of("A b") to Var.escapeName("A b"),
        Struct.of("f", Var.of("A"), Atom.of("b")) to "f(A, b)",
        Struct.of("f", Var.of("A"), Var.of("B"), Var.of("A")) to "f(A, B, A1)",
        Var.of("A").let { Struct.of("f", it, Var.of("B"), it) } to "f(A, B, A)",
        Struct.of("F", Atom.of("a"), Integer.of(1), Real.of("3.2")) to "'F'(a, 1, 3.2)",
        List.empty() to EmptyList.FUNCTOR,
        List.of(Var.of("A"), Var.of("A"), Var.of("A")) to "[A, A1, A2]",
        Var.of("A").let { List.of(it, it, it) } to "[A, A, A]",
        List.from(items = listOf(Var.of("A"), Var.of("A"), Var.of("A")), last = Var.of("A")) to "[A, A1, A2 | A3]",
        Var.of("A").let { List.from(items = listOf(it, it, it), last = it) } to "[A, A, A | A]",
        Set.empty() to EmptySet.FUNCTOR,
        Set.of(Var.of("A"), Var.of("A"), Var.of("A")) to "{A, A1, A2}",
        Var.of("A").let { Set.of(it, it, it) } to "{A, A, A}"
    )

    val expectedFormatsWithPrettyVariables: Map<Term, String> = common + mapOf(
        Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")) to "(A, A1, A2)",
        Var.of("A").let { Tuple.of(it, it, it) } to "(A, A, A)",
        Indicator.of(Var.of("A"), Var.of("B")) to "'/'(A, B)",
        Rule.of(
            Tuple.of(Var.of("A"), Var.of("B")),
            Tuple.of(Var.of("C"), Var.of("D"))
        ) to "':-'((A, B), (C, D))",
        Fact.of(
            Tuple.of(Var.of("A"), Var.of("B"), Var.of("A"), Var.of("B"))
        ) to "':-'((A, B, A1, B1), true)",
        Directive.of(
            Tuple.of(Var.of("A"), Var.of("B"), Var.of("A"), Var.of("B"))
        ) to "':-'((A, B, A1, B1))"
    )

    val expectedFormatsWithPrettyExpressions: Map<Term, String> = common + mapOf(
        Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")) to "A, A1, A2",
        Var.of("A").let { Tuple.of(it, it, it) } to "A, A, A",
        Struct.of("f", Tuple.of(Var.of("A"), Var.of("B")), Var.of("A")) to "f((A, B), A1)",
        Struct.of("f", Var.of("A"), Tuple.of(Var.of("B"), Var.of("A"))) to "f(A, (B, A1))",
        Tuple.of(Tuple.of(Var.of("A"), Var.of("B")), Tuple.of(Var.of("A"), Var.of("B"))) to "(A, B), A1, B1",
        Set.of(Tuple.of(Var.of("A"), Var.of("B")), Tuple.of(Var.of("A"), Var.of("B"))) to "{(A, B), A1, B1}",
        List.of(Tuple.of(Var.of("A"), Var.of("B")), Tuple.of(Var.of("A"), Var.of("B"))) to "[(A, B), (A1, B1)]",
        List.of(Tuple.of(Var.of("A"), Var.of("B")), Var.of("A"), Var.of("B")) to "[(A, B), A1, B1]",
        List.from(
            items = listOf(Tuple.of(Var.of("A"), Var.of("B")), Tuple.of(Var.of("A"), Var.of("B"))),
            last = Tuple.of(Var.of("A"), Var.of("B"))
        ) to "[(A, B), (A1, B1) | (A2, B2)]",
        List.from(
            items = listOf(Tuple.of(Var.of("A"), Var.of("B")), Var.of("A"), Var.of("B")),
            last = Tuple.of(Var.of("A"), Var.of("B"))
        ) to "[(A, B), A1, B1 | (A2, B2)]",
        Struct.of(
            "+",
            Struct.of("+", Var.of("A"), Var.of("B")),
            Struct.of("+", Var.of("C"), Var.of("D"))
        ) to "A + B + C + D",
        Struct.of(
            "+",
            Struct.of("-", Var.of("A"), Var.of("B")),
            Struct.of("-", Var.of("C"), Var.of("D"))
        ) to "A - B + C - D",
        Indicator.of(Var.of("A"), Var.of("B")) to "A / B",
        Rule.of(
            Tuple.of(Var.of("A"), Var.of("B")),
            Tuple.of(Var.of("C"), Var.of("D"))
        ) to "A, B :- C, D",
        Fact.of(
            Tuple.of(Var.of("A"), Var.of("B"), Var.of("A"), Var.of("B"))
        ) to "A, B, A1, B1 :- true",
        Directive.of(
            Tuple.of(Var.of("A"), Var.of("B"), Var.of("A"), Var.of("B"))
        ) to ":- A, B, A1, B1"
    )

    fun TermFormatter.assertProperlyFormats(entry: Map.Entry<Term, String>) {
        this.assertProperlyFormats(entry.component2(), entry.component1())
    }

    fun TermFormatter.assertProperlyFormats(expected: String, actual: Term) {
        val formatted = actual.format(this)
        assertEquals(
            expected,
            formatted,
            message =
            """
                |Formatting 
                |   $actual
                |with ${this::class} should result in
                |   $expected
                |while 
                |   $formatted
                |is produced instead
                |
                """.trimMargin()
        )
    }
}
