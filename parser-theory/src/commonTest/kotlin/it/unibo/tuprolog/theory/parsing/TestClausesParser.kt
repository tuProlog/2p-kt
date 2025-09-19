package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class TestClausesParser {
    companion object {
        fun assertMatch(
            expected: Term,
            actual: Term,
        ) {
            assertTrue("Term `$actual` does not unify with `$expected`") {
                Unificator.default.match(expected, actual)
            }
        }

        fun assertMatch(
            expected: Term,
            actual: LogicProgrammingScope.() -> Term,
        ) {
            assertMatch(expected, LogicProgrammingScope.of().actual())
        }
    }

    @Test
    fun testTheoryParsing() {
        val db =
            with(ClausesParser.withStandardOperators()) {
                parseTheory("f(1).\nf(2).\n:- f(X), g(X).\nf(X) :- g(X).")
            }

        val rules = db.rules.toList()
        val directives = db.directives.toList()

        assertEquals(3, rules.size)
        assertEquals(2, rules.filterIsInstance<Fact>().count())
        assertEquals(rules[0], logicProgramming { factOf("f"(1)) })
        assertEquals(rules[1], logicProgramming { factOf("f"(2)) })
        assertMatch(rules[2]) {
            "f"("X") impliedBy "g"("X")
        }
        assertMatch(directives[0]) {
            directiveOf("f"("X"), "g"("X"))
        }
    }

    @Test
    fun testTheoryParsingError() {
        val input = "f(a).\nf(b)"
        try {
            with(ClausesParser.withStandardOperators()) {
                parseTheory(input)
            }
            fail("${ParseException::class} should be thrown")
        } catch (e: ParseException) {
            assertEquals(2, e.line)
            assertEquals(5, e.column)
            assertEquals(input, e.input)
        }
    }

    @Test
    fun testTheoryWithCustomOperator() {
        val input =
            """
            |:- op(900, xfy, '::').
            |nil.
            |1 :: nil.
            |1 :: 2 :: nil.
            """.trimMargin()

        val th =
            with(ClausesParser.withStandardOperators()) {
                parseTheory(input)
            }

        assertMatch(th.elementAt(0)) {
            directive { "op"(900, "xfy", "::") }
        }

        assertMatch(th.elementAt(1)) {
            fact { "nil" }
        }

        assertMatch(th.elementAt(2)) {
            fact { "::"(1, "nil") }
        }

        assertMatch(th.elementAt(3)) {
            fact { "::"(1, "::"(2, "nil")) }
        }
    }

    @Test
    fun testTheoryWithCustomOperators() {
        val input =
            """
            |:- op(900, yfx, ['++', '--']).
            |1 ++ 2.
            |1 -- 2.
            |1 -- 2 ++ 3.
            |1 ++ 2 -- 3 ++ 4.
            """.trimMargin()

        val th =
            with(ClausesParser.withStandardOperators()) {
                parseTheory(input)
            }

        assertMatch(th.elementAt(0)) {
            directive { "op"(900, "yfx", logicListOf("++", "--")) }
        }

        assertMatch(th.elementAt(1)) {
            fact { "++"(1, 2) }
        }

        assertMatch(th.elementAt(2)) {
            fact { "--"(1, 2) }
        }

        assertMatch(th.elementAt(3)) {
            fact { "++"("--"(1, 2), 3) }
        }

        assertMatch(th.elementAt(4)) {
            fact { "++"("--"("++"(1, 2), 3), 4) }
        }
    }
}
