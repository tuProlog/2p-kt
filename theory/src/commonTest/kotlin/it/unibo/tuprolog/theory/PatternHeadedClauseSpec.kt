package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

private class TheoryKind(
    val id: String,
    val of: (List<Clause>) -> Theory,
)

private val kinds =
    listOf(
        TheoryKind("frozen") { Theory.indexedOf(Unificator.default, it) },
        TheoryKind("mutable") { MutableTheory.indexedOf(Unificator.default, it) },
    )

private fun p(vararg args: Term) = Struct.of("p", *args)

private fun q(vararg args: Term) = Struct.of("q", *args)

private fun metarule(body: String) = Rule.of(Pattern.of(Var.of("P"), Var.of("X")), Atom.of(body))

/** A head-only query: the clauses whose heads unify with [head], whatever their bodies. */
private fun Theory.lookup(head: Term) = get(Rule.of(head.castToCompound(), Var.anonymous())).toList()

/** The clauses of [theory] as their positions in [original]. */
private fun positions(
    original: List<Clause>,
    found: List<Clause>,
) = found.map { clause -> original.indexOfFirst { it === clause } }

private class Scenario(
    val id: String,
    val check: (TheoryKind) -> Unit,
)

/** Pattern-headed clauses (metarules) in theories: stored, indexed and retrieved in their insertion order. */
class PatternHeadedClauseSpec :
    FunSpec({
        val scenarios =
            listOf(
                Scenario("concrete-query-sees-metarules-in-insertion-order") { kind ->
                    val clauses = listOf(Fact.of(p(Integer.of(1))), metarule("m"), Fact.of(p(Integer.of(2))))
                    val theory = kind.of(clauses)
                    positions(clauses, theory.lookup(p(Var.of("Y")))) shouldBe listOf(0, 1, 2)
                    positions(clauses, theory.lookup(p(Integer.of(2)))) shouldBe listOf(1, 2)
                },
                Scenario("concrete-query-sees-metarules-above-the-index-threshold") { kind ->
                    val clauses =
                        (1..3).map { Fact.of(p(Integer.of(it))) } + metarule("m") +
                            (4..8).map { Fact.of(p(Integer.of(it))) }
                    val theory = kind.of(clauses)
                    positions(clauses, theory.lookup(p(Integer.of(2)))) shouldBe listOf(1, 3)
                    positions(clauses, theory.lookup(p(Integer.of(6)))) shouldBe listOf(3, 6)
                },
                Scenario("symbol-without-clauses-sees-metarules") { kind ->
                    val clauses = listOf(Fact.of(p(Integer.of(1))), metarule("m"))
                    positions(clauses, kind.of(clauses).lookup(q(Atom.of("a")))) shouldBe listOf(1)
                },
                Scenario("other-arities-do-not-see-metarules") { kind ->
                    val theory = kind.of(listOf(metarule("m"), Fact.of(p(Atom.of("a"), Atom.of("b")))))
                    theory.lookup(p(Var.of("A"), Var.of("B"))) shouldBe listOf(Fact.of(p(Atom.of("a"), Atom.of("b"))))
                    theory.lookup(Atom.of("p")).shouldBeEmpty()
                },
                Scenario("pattern-query-sees-every-clause-of-its-arity") { kind ->
                    val clauses =
                        listOf(
                            Fact.of(p(Integer.of(1))),
                            Fact.of(q(Integer.of(1), Integer.of(2))),
                            metarule("m"),
                            Fact.of(q(Integer.of(3))),
                        )
                    val theory = kind.of(clauses)
                    positions(clauses, theory.lookup(Pattern.of(Var.of("F"), Var.of("Y")))) shouldBe listOf(0, 2, 3)
                },
                Scenario("pattern-query-above-the-index-threshold") { kind ->
                    val clauses =
                        (1..6).flatMap { listOf(Fact.of(p(Integer.of(it))), Fact.of(q(Integer.of(it)))) } +
                            metarule("m") + Fact.of(q(Integer.of(5)))
                    val theory = kind.of(clauses)
                    positions(clauses, theory.lookup(Pattern.of(Var.of("F"), Integer.of(5)))) shouldBe
                        listOf(8, 9, 12, 13)
                },
                Scenario("pattern-queries-see-later-updates") { kind ->
                    val rule = metarule("m")
                    var theory = kind.of(listOf(Fact.of(p(Integer.of(1))), rule))
                    val query = Pattern.of(Var.of("F"), Var.of("Y"))
                    theory.lookup(query) shouldBe listOf(Fact.of(p(Integer.of(1))), rule)
                    theory = theory.assertZ(Fact.of(q(Integer.of(2)))).assertA(Fact.of(q(Integer.of(0))))
                    theory.lookup(query) shouldBe
                        listOf(Fact.of(q(Integer.of(0))), Fact.of(p(Integer.of(1))), rule, Fact.of(q(Integer.of(2))))
                    theory = theory.retract(p(Var.of("Y"))).theory
                    theory.lookup(query) shouldBe listOf(Fact.of(q(Integer.of(0))), rule, Fact.of(q(Integer.of(2))))
                    theory = theory.retractAll(query).theory
                    theory.lookup(query).shouldBeEmpty()
                    theory.lookup(p(Var.of("Y"))).shouldBeEmpty()
                },
                Scenario("asserted-metarules-keep-their-place") { kind ->
                    val first = Fact.of(p(Integer.of(1)))
                    val last = Fact.of(p(Integer.of(2)))
                    val front = metarule("front")
                    val back = metarule("back")
                    val theory =
                        kind
                            .of(listOf(first))
                            .assertA(front)
                            .assertZ(back)
                            .assertZ(last)
                    theory.lookup(p(Var.of("Y"))) shouldBe listOf(front, first, back, last)
                    theory.clauses.toList() shouldBe listOf(front, first, back, last)
                },
                Scenario("concrete-procedure-created-after-metarules") { kind ->
                    val front = metarule("front")
                    val fact = Fact.of(q(Integer.of(1)))
                    val theory = kind.of(listOf(front)).assertZ(fact).assertA(Fact.of(q(Integer.of(0))))
                    theory.lookup(q(Var.of("Y"))) shouldBe listOf(Fact.of(q(Integer.of(0))), front, fact)
                },
                Scenario("concrete-retract-keeps-metarules") { kind ->
                    val rule = metarule("m")
                    val theory = kind.of(listOf(rule, Fact.of(p(Integer.of(1))), Fact.of(p(Integer.of(2)))))
                    val retracted = theory.retract(p(Var.of("Y")))
                    (retracted as RetractResult.Success).clauses.toList() shouldBe listOf(Fact.of(p(Integer.of(1))))
                    retracted.theory.lookup(p(Var.of("Y"))) shouldBe listOf(rule, Fact.of(p(Integer.of(2))))
                },
                Scenario("concrete-retract-all-and-abolish-keep-metarules") { kind ->
                    val rule = metarule("m")
                    val clauses = listOf(Fact.of(p(Integer.of(1))), rule, Fact.of(p(Integer.of(2))))
                    kind
                        .of(clauses)
                        .retractAll(p(Var.of("Y")))
                        .theory.clauses
                        .toList() shouldBe listOf(rule)
                    kind
                        .of(clauses)
                        .abolish(Indicator.of("p", 1))
                        .clauses
                        .toList() shouldBe listOf(rule)
                },
                Scenario("pattern-retract-removes-whatever-unifies-first") { kind ->
                    val rule = metarule("m")
                    val theory = kind.of(listOf(Fact.of(p(Integer.of(1))), rule))
                    val retracted = theory.retract(Rule.of(Pattern.of(Var.of("F"), Var.of("Y")), Var.of("B")))
                    retracted.theory.clauses.toList() shouldBe listOf(rule)
                },
                Scenario("retracting-a-metarule-removes-it-from-every-procedure") { kind ->
                    val rule = metarule("m")
                    val theory = kind.of(listOf(Fact.of(p(Integer.of(1))), rule, Fact.of(q(Integer.of(1)))))
                    val retracted = theory.retract(rule).theory
                    retracted.lookup(p(Var.of("Y"))) shouldBe listOf(Fact.of(p(Integer.of(1))))
                    retracted.lookup(q(Var.of("Y"))) shouldBe listOf(Fact.of(q(Integer.of(1))))
                    retracted.lookup(Struct.of("r", Atom.of("a"))).shouldBeEmpty()
                },
            )

        withTests(nameFn = {
            "${it.second.id}/${it.first.id}"
        }, scenarios.flatMap { s -> kinds.map { s to it } }) { (scenario, kind) ->
            scenario.check(kind)
        }

        test("mutable/lookups-follow-the-logical-update-view") {
            val theory = MutableTheory.indexedOf(Unificator.default, listOf(Fact.of(p(Integer.of(1)))))
            val seen = mutableListOf<Clause>()
            for (clause in theory[Rule.of(p(Var.of("Y")), Var.anonymous())]) {
                seen.add(clause)
                theory.assertZ(metarule("m"))
            }
            seen shouldBe listOf(Fact.of(p(Integer.of(1))))
            theory.lookup(p(Var.of("Y"))).size shouldBe 2
        }
    })
