package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.format
import kotlin.test.assertEquals

object TermFormatterUtils {
    /** A named [term] along with its [expected] formatting */
    data class FormatCase(
        val id: String,
        val term: Term,
        val expected: String,
    )

    private val common =
        listOf(
            FormatCase("atom", Atom.of("a"), "a"),
            FormatCase("quoted-atom", Atom.of("a b"), "'a b'"),
            FormatCase("integer", Integer.of(1), "1"),
            FormatCase("real", Real.of("3.2"), "3.2"),
            FormatCase("variable", Var.of("A"), "A"),
            FormatCase("anonymous-variable", Var.anonymous(), Var.ANONYMOUS_NAME),
            FormatCase("escaped-variable", Var.of("A b"), Var.escapeName("A b")),
            FormatCase("struct", Struct.of("f", Var.of("A"), Atom.of("b")), "f(A, b)"),
            FormatCase(
                "struct-with-homonym-variables",
                Struct.of("f", Var.of("A"), Var.of("B"), Var.of("A")),
                "f(A, B, A1)",
            ),
            FormatCase(
                "struct-with-repeated-variable",
                Var.of("A").let { Struct.of("f", it, Var.of("B"), it) },
                "f(A, B, A)",
            ),
            FormatCase(
                "struct-with-quoted-functor",
                Struct.of("F", Atom.of("a"), Integer.of(1), Real.of("3.2")),
                "'F'(a, 1, 3.2)",
            ),
            FormatCase("empty-list", List.empty(), EmptyList.FUNCTOR),
            FormatCase("list-with-homonym-variables", List.of(Var.of("A"), Var.of("A"), Var.of("A")), "[A, A1, A2]"),
            FormatCase("list-with-repeated-variable", Var.of("A").let { List.of(it, it, it) }, "[A, A, A]"),
            FormatCase(
                "partial-list-with-homonym-variables",
                List.from(items = listOf(Var.of("A"), Var.of("A"), Var.of("A")), last = Var.of("A")),
                "[A, A1, A2 | A3]",
            ),
            FormatCase(
                "partial-list-with-repeated-variable",
                Var.of("A").let {
                    List.from(items = listOf(it, it, it), last = it)
                },
                "[A, A, A | A]",
            ),
            FormatCase("empty-block", Block.empty(), EmptyBlock.FUNCTOR),
            FormatCase("block-with-homonym-variables", Block.of(Var.of("A"), Var.of("A"), Var.of("A")), "{A, A1, A2}"),
            FormatCase("block-with-repeated-variable", Var.of("A").let { Block.of(it, it, it) }, "{A, A, A}"),
        )

    val expectedFormatsWithPrettyVariables =
        common +
            listOf(
                FormatCase(
                    "tuple-with-homonym-variables",
                    Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")),
                    "(A, A1, A2)",
                ),
                FormatCase("tuple-with-repeated-variable", Var.of("A").let { Tuple.of(it, it, it) }, "(A, A, A)"),
                FormatCase("indicator", Indicator.of(Var.of("A"), Var.of("B")), "'/'(A, B)"),
                FormatCase(
                    "rule",
                    Rule.of(
                        Tuple.of(Var.of("A"), Var.of("B")),
                        Tuple.of(Var.of("C"), Var.of("D")),
                    ),
                    "':-'((A, B), (C, D))",
                ),
                FormatCase(
                    "fact",
                    Fact.of(
                        Tuple.of(Var.of("A"), Var.of("B"), Var.of("A"), Var.of("B")),
                    ),
                    "':-'((A, B, A1, B1), true)",
                ),
                FormatCase(
                    "directive",
                    Directive.of(
                        Tuple.of(Var.of("A"), Var.of("B"), Var.of("A"), Var.of("B")),
                    ),
                    "':-'((A, B, A1, B1))",
                ),
            )

    val expectedFormatsWithPrettyExpressions =
        common +
            listOf(
                FormatCase(
                    "tuple-with-homonym-variables",
                    Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")),
                    "A, A1, A2",
                ),
                FormatCase("tuple-with-repeated-variable", Var.of("A").let { Tuple.of(it, it, it) }, "A, A, A"),
                FormatCase(
                    "struct-with-tuple-first",
                    Struct.of("f", Tuple.of(Var.of("A"), Var.of("B")), Var.of("A")),
                    "f((A, B), A1)",
                ),
                FormatCase(
                    "struct-with-tuple-last",
                    Struct.of("f", Var.of("A"), Tuple.of(Var.of("B"), Var.of("A"))),
                    "f(A, (B, A1))",
                ),
                FormatCase(
                    "tuple-of-tuples",
                    Tuple.of(Tuple.of(Var.of("A"), Var.of("B")), Tuple.of(Var.of("A"), Var.of("B"))),
                    "(A, B), A1, B1",
                ),
                FormatCase(
                    "block-of-tuples",
                    Block.of(Tuple.of(Var.of("A"), Var.of("B")), Tuple.of(Var.of("A"), Var.of("B"))),
                    "{(A, B), A1, B1}",
                ),
                FormatCase(
                    "list-of-tuples",
                    List.of(Tuple.of(Var.of("A"), Var.of("B")), Tuple.of(Var.of("A"), Var.of("B"))),
                    "[(A, B), (A1, B1)]",
                ),
                FormatCase(
                    "list-of-tuple-and-variables",
                    List.of(Tuple.of(Var.of("A"), Var.of("B")), Var.of("A"), Var.of("B")),
                    "[(A, B), A1, B1]",
                ),
                FormatCase(
                    "partial-list-of-tuples",
                    List.from(
                        items = listOf(Tuple.of(Var.of("A"), Var.of("B")), Tuple.of(Var.of("A"), Var.of("B"))),
                        last = Tuple.of(Var.of("A"), Var.of("B")),
                    ),
                    "[(A, B), (A1, B1) | (A2, B2)]",
                ),
                FormatCase(
                    "partial-list-of-tuple-and-variables",
                    List.from(
                        items = listOf(Tuple.of(Var.of("A"), Var.of("B")), Var.of("A"), Var.of("B")),
                        last = Tuple.of(Var.of("A"), Var.of("B")),
                    ),
                    "[(A, B), A1, B1 | (A2, B2)]",
                ),
                FormatCase(
                    "sum-of-sums",
                    Struct.of(
                        "+",
                        Struct.of("+", Var.of("A"), Var.of("B")),
                        Struct.of("+", Var.of("C"), Var.of("D")),
                    ),
                    "A + B + C + D",
                ),
                FormatCase(
                    "sum-of-differences",
                    Struct.of(
                        "+",
                        Struct.of("-", Var.of("A"), Var.of("B")),
                        Struct.of("-", Var.of("C"), Var.of("D")),
                    ),
                    "A - B + C - D",
                ),
                FormatCase("indicator", Indicator.of(Var.of("A"), Var.of("B")), "A / B"),
                FormatCase(
                    "rule",
                    Rule.of(
                        Tuple.of(Var.of("A"), Var.of("B")),
                        Tuple.of(Var.of("C"), Var.of("D")),
                    ),
                    "A, B :- C, D",
                ),
                FormatCase(
                    "fact",
                    Fact.of(
                        Tuple.of(Var.of("A"), Var.of("B"), Var.of("A"), Var.of("B")),
                    ),
                    "A, B, A1, B1 :- true",
                ),
                FormatCase(
                    "directive",
                    Directive.of(
                        Tuple.of(Var.of("A"), Var.of("B"), Var.of("A"), Var.of("B")),
                    ),
                    ":- A, B, A1, B1",
                ),
            )

    fun TermFormatter.assertProperlyFormats(case: FormatCase) {
        this.assertProperlyFormats(case.expected, case.term)
    }

    fun TermFormatter.assertProperlyFormats(
        expected: String,
        actual: Term,
    ) {
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
                """.trimMargin(),
        )
    }
}
