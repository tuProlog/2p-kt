package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.utils.setTag

private class NormalizationCase(
    val id: String,
    /** Builds the original clause and the expected normalized one, sharing their variables. */
    val clauses: () -> Pair<Clause, Clause>,
)

private class UnchangedCase(
    val id: String,
    val clause: () -> Clause,
)

class ClauseNormalizationSpec :
    FunSpec({
        fun call(goal: Term) = Struct.of("call", goal)

        withTests(
            nameFn = { "rewrites-goal-positions/${it.id}" },
            NormalizationCase("variable-body") {
                Scope.empty { ruleOf(structOf("p", varOf("X")), varOf("X")) to ruleOf(structOf("p", varOf("X")), call(varOf("X"))) }
            },
            NormalizationCase("conjunction") {
                Scope.empty {
                    val q = structOf("q", varOf("X"))
                    ruleOf(structOf("p", varOf("X")), varOf("X"), q) to ruleOf(structOf("p", varOf("X")), call(varOf("X")), q)
                }
            },
            NormalizationCase("disjunction") {
                Scope.empty {
                    val q = structOf("q", varOf("X"))
                    ruleOf(structOf("p", varOf("X")), structOf(";", varOf("X"), q)) to
                        ruleOf(structOf("p", varOf("X")), structOf(";", call(varOf("X")), q))
                }
            },
            NormalizationCase("if-then-else") {
                Scope.empty {
                    val q = structOf("q", varOf("X"))
                    ruleOf(structOf("p", varOf("X")), structOf(";", structOf("->", varOf("X"), q), varOf("Y"))) to
                        ruleOf(
                            structOf("p", varOf("X")),
                            structOf(";", structOf("->", call(varOf("X")), q), call(varOf("Y"))),
                        )
                }
            },
            NormalizationCase("nested-on-the-left") {
                Scope.empty {
                    ruleOf(atomOf("p"), structOf(";", tupleOf(varOf("X"), varOf("Y")), varOf("Z"))) to
                        ruleOf(atomOf("p"), structOf(";", tupleOf(call(varOf("X")), call(varOf("Y"))), call(varOf("Z"))))
                }
            },
            NormalizationCase("goal") {
                Scope.empty { goalOf(varOf("X")) to goalOf(call(varOf("X"))) }
            },
            NormalizationCase("goal-conjunction") {
                Scope.empty { goalOf(atomOf("a"), varOf("X")) to goalOf(atomOf("a"), call(varOf("X"))) }
            },
            NormalizationCase("pattern-head") {
                Scope.empty {
                    val head = patternOf(varOf("P"), varOf("X"))
                    ruleOf(head, varOf("X")) to ruleOf(head, call(varOf("X")))
                }
            },
        ) { case ->
            val (original, expected) = case.clauses()
            assertEqualities(expected, original.normalized())
        }

        withTests(
            nameFn = { "leaves-unchanged/${it.id}" },
            UnchangedCase("fact") { Fact.of(Struct.of("p", Var.of("X"))) },
            UnchangedCase("ground-body") { Rule.of(Atom.of("p"), Atom.of("q"), Atom.of("r")) },
            UnchangedCase("data-argument") { Scope.empty { ruleOf(atomOf("p"), structOf("q", structOf("f", varOf("X")))) } },
            UnchangedCase("unification") { Scope.empty { ruleOf(atomOf("p"), structOf("=", varOf("X"), varOf("Y"))) } },
            UnchangedCase("list-of-variables") { Scope.empty { ruleOf(atomOf("p"), structOf("r", logicListOf(varOf("X")))) } },
            UnchangedCase("call") { Scope.empty { ruleOf(atomOf("p"), structOf("call", varOf("X"))) } },
            UnchangedCase("negation") { Scope.empty { ruleOf(atomOf("p"), structOf("\\+", varOf("X"))) } },
            UnchangedCase("findall") {
                Scope.empty { ruleOf(atomOf("p"), structOf("findall", varOf("X"), varOf("G"), varOf("L"))) }
            },
            UnchangedCase("pattern-goal") {
                Scope.empty { ruleOf(patternOf(varOf("P"), varOf("X")), patternOf(varOf("Q"), varOf("X"))) }
            },
            UnchangedCase("normalized-already") { Scope.empty { ruleOf(atomOf("p"), call(varOf("X")), atomOf("q")) } },
            UnchangedCase("numeric-goal") { Rule.of(Atom.of("p"), Integer.ONE) },
        ) { case ->
            val clause = case.clause()
            clause.normalized() shouldBeSameInstanceAs clause
        }

        test("preserves-variable-identity") {
            val x = Var.of("X")
            val normalized = Rule.of(Struct.of("p", x), x).normalized()
            normalized.head!![0] shouldBeSameInstanceAs x
            normalized.body.castToStruct()[0] shouldBeSameInstanceAs x
        }

        test("preserves-tags") {
            val x = Var.of("X")
            val conjunction = Tuple.of(x, Atom.of("q")).setTag("source", "conjunction")
            val clause = Rule.of(Atom.of("p"), conjunction).setTag("source", "clause")
            val normalized = clause.normalized()
            normalized.tags shouldBe mapOf("source" to "clause")
            normalized.body.tags shouldBe mapOf("source" to "conjunction")
        }

        test("long-conjunction-is-stack-safe") {
            val goals = (0 until 100_000).map { Var.of("X$it") }
            val normalized = Goal.of(goals).normalized()
            normalized.bodySize shouldBe goals.size
            normalized.bodyItems.first() shouldBe call(goals.first())
            normalized.bodyItems.last() shouldBe call(goals.last())
        }

        test("references-in-goal-position-are-not-well-formed") {
            Rule.of(Atom.of("p"), ObjectRef.of(Any())).isWellFormed.shouldBeFalse()
            Rule.of(Atom.of("p"), Atom.of("q"), NullRef()).isWellFormed.shouldBeFalse()
        }
    })
