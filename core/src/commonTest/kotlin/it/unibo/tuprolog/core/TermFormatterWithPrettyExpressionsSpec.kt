package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.testutils.TermFormatterUtils
import it.unibo.tuprolog.core.testutils.TermFormatterUtils.assertProperlyFormats
import kotlin.test.assertEquals

private class PostfixCase(
    val id: String,
    val specifier: Specifier,
    val term: Term,
    val expected: String,
)

class TermFormatterWithPrettyExpressionsSpec :
    FunSpec({
        withTests(nameFn = { "format/${it.id}" }, TermFormatterUtils.expectedFormatsWithPrettyExpressions) { case ->
            TermFormatter.prettyExpressions().assertProperlyFormats(case)
        }

        withTests(
            nameFn = { "postfix/${it.id}" },
            listOf(
                PostfixCase("xf/simple", Specifier.XF, Struct.of("!", Atom.of("a")), "a !"),
                PostfixCase("xf/nested", Specifier.XF, Struct.of("!", Struct.of("!", Atom.of("a"))), "(a !) !"),
                PostfixCase("xf/of-a-prefix", Specifier.XF, Struct.of("!", Struct.of("-", Atom.of("a"))), "(- a) !"),
                PostfixCase("xf/under-a-prefix", Specifier.XF, Struct.of("-", Struct.of("!", Atom.of("a"))), "- a !"),
                PostfixCase("yf/nested", Specifier.YF, Struct.of("!", Struct.of("!", Atom.of("a"))), "a ! !"),
            ),
        ) { case ->
            val operators = OperatorSet(Operator("!", case.specifier, 200), Operator("-", Specifier.FY, 200))
            assertEquals(case.expected, TermFormatter.prettyExpressions(operators).format(case.term))
        }

        withTests(
            nameFn = { "ignoring-operators/${it.first}" },
            listOf(
                "infix" to (Struct.of("+", Atom.of("a"), Atom.of("b")) to "'+'(a, b)"),
                "list" to (List.of(Atom.of("a")) to "'.'(a, [])"),
                "block" to (Block.of(Atom.of("a"), Atom.of("b")) to "'{}'(','(a, b))"),
            ),
        ) { (_, case) ->
            val formatter =
                TermFormatter.of(
                    TermFormatter.VarFormat.COMPLETE_NAME,
                    TermFormatter.OpFormat.IGNORE_OPERATORS,
                )
            assertEquals(case.second, formatter.format(case.first))
        }
    })
