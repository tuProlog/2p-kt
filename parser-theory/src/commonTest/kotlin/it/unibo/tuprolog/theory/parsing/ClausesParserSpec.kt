package it.unibo.tuprolog.theory.parsing

import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.unify.Unificator

class ClausesParserSpec :
    FunSpec({
        test("theory-parsing") {
            val db =
                with(ClausesParser.withStandardOperators()) {
                    parseTheory("f(1).\nf(2).\n:- f(X), g(X).\nf(X) :- g(X).")
                }

            val rules = db.rules.toList()
            val directives = db.directives.toList()

            rules.size shouldBe 3
            (rules.filterIsInstance<Fact>().count()) shouldBe 2
            (logicProgramming { factOf("f"(1)) }) shouldBe rules[0]
            (logicProgramming { factOf("f"(2)) }) shouldBe rules[1]
            assertMatch(rules[2]) {
                "f"("X") impliedBy "g"("X")
            }
            assertMatch(directives[0]) {
                goalOf("f"("X"), "g"("X"))
            }
        }

        test("theory-parsing-error") {
            val input = "f(a).\nf(b)"
            try {
                with(ClausesParser.withStandardOperators()) {
                    parseTheory(input)
                }
                fail("${ParseException::class} should be thrown")
            } catch (e: ParseException) {
                e.line shouldBe 2
                e.column shouldBe 5
                e.input shouldBe input
            }
        }

        test("theory-with-custom-operator") {
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

        test("theory-with-custom-operators") {
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
    })

private fun assertMatch(
    expected: Term,
    actual: Term,
) {
    withClue(
        "Term `$actual` does not unify with `$expected`",
    ) { Unificator.default.match(expected, actual).shouldBeTrue() }
}

private fun assertMatch(
    expected: Term,
    actual: LogicProgrammingScope.() -> Term,
) {
    assertMatch(expected, LogicProgrammingScope.of().actual())
}
