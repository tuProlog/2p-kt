package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.dsl.theory.PrologWithTheories
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.unify.Unificator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class TestClausesParser {

    companion object {
        fun assertMatch(expected: Term, actual: Term) {
            assertTrue("Term `$actual` does not unify with `$expected`"){
                Unificator.default.match(expected, actual)
            }
        }

        fun assertMatch(expected: Term, actual: PrologWithTheories.() -> Term) {
            assertMatch(expected, PrologWithTheories.empty().actual())
        }
    }

    @Test
    fun testClauseDbParsing() {
        val db = with(ClausesParser.withStandardOperators) {
            parseTheory("f(1).\nf(2).\n:- f(X), g(X).\nf(X) :- g(X).")
        }

        val rules = db.rules.toList()
        val directives = db.directives.toList()

        assertEquals(3, rules.size)
        assertEquals(2, rules.filterIsInstance<Fact>().count())
        assertEquals(rules[0], prolog { factOf("f"(1)) })
        assertEquals(rules[1], prolog { factOf("f"(2)) })
        assertMatch(rules[2]) {
            "f"("X") impliedBy "g"("X")
        }
        assertMatch(directives[0]) {
            directiveOf("f"("X"), "g"("X"))
        }
    }

    @Test
    fun testClauseDbParsingError() {
        val input = "f(a).\nf(b)"
        try {
            with(ClausesParser.withStandardOperators) {
                parseTheory(input)
            }
            fail("${ParseException::class} should be thrown")
        } catch (e: ParseException) {
            assertEquals(2, e.line)
            assertEquals(5, e.column)
            assertEquals(input, e.input)
        } catch (e: Throwable) {
            fail("Unexpected exception of type ${e::class}: $e")
        }
    }
}