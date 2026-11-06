package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.collections.ClauseList
import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.MutableClauseMap
import it.unibo.tuprolog.collections.MutableClauseSet
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

/*
 * The code samples of the documentation site (reference/theory-api.md, how-to/use-theories-and-clause-collections.md,
 * how-to/implement-a-custom-unificator.md): each leaf is quoted between its `--8<--` markers, so the docs only show
 * code that compiles and passes.
 */

private fun parent(
    parent: String,
    child: String,
): Fact = Fact.of(Struct.of("parent", Atom.of(parent), Atom.of(child)))

class TheoryUsageExamplesSpec :
    FunSpec({
        test("build-and-query-a-static-theory") {
            // --8<-- [start:static-theory]
            val ancestor =
                Rule.of(
                    Struct.of("ancestor", Var.of("X"), Var.of("Y")),
                    Struct.of("parent", Var.of("X"), Var.of("Y")),
                )
            val theory: Theory =
                Theory.of(
                    parent("abraham", "isaac"),
                    parent("isaac", "jacob"),
                    parent("jacob", "joseph"),
                    ancestor,
                )

            // lookups unify: the clauses whose head unifies with parent(isaac, Who)
            theory[Struct.of("parent", Atom.of("isaac"), Var.of("Who"))].toList() shouldBe
                listOf(parent("isaac", "jacob"))
            // by indicator: every clause of ancestor/2
            theory[Indicator.of("ancestor", 2)].toList() shouldBe listOf(ancestor)
            (Indicator.of("parent", 2) in theory) shouldBe true
            // --8<-- [end:static-theory]
        }

        test("update-an-immutable-theory") {
            // --8<-- [start:immutable-updates]
            val theory = Theory.of(parent("abraham", "isaac"))
            val extended = theory.assertZ(parent("isaac", "jacob")).assertA(parent("terah", "abraham"))

            theory.size shouldBe 1L // unchanged: updates return new theories
            extended.clauses.toList() shouldBe
                listOf(parent("terah", "abraham"), parent("abraham", "isaac"), parent("isaac", "jacob"))

            when (val result = extended.retract(Struct.of("parent", Var.of("X"), Atom.of("isaac")))) {
                is RetractResult.Success -> {
                    result.firstClause shouldBe parent("abraham", "isaac")
                    result.theory.size shouldBe 2L
                }
                is RetractResult.Failure -> error("parent(X, isaac) is there")
            }
            // nothing to remove: Failure, carrying the very same theory
            extended.retract(parent("nobody", "else")).theory shouldBeSameInstanceAs extended
            // --8<-- [end:immutable-updates]
        }

        test("update-a-mutable-theory-while-iterating") {
            // --8<-- [start:mutable-theory]
            val counters: MutableTheory =
                MutableTheory.of(Unificator.default, List(5) { Fact.of(Struct.of("counter", Integer.of(it))) })
            val anyCounter = Fact.of(Struct.of("counter", Var.anonymous()))

            // the logical update view: a lookup sees the clauses as of when it started,
            // so it is safe to assert and retract while consuming it, without copying
            for (clause in counters[anyCounter]) {
                counters.retract(clause)
                counters.assertZ(
                    Fact.of(
                        Struct.of(
                            "counter",
                            Integer.of(
                                clause.head!!
                                    .getArgAt(0)
                                    .castToInteger()
                                    .intValue + 10,
                            ),
                        ),
                    ),
                )
            }

            counters.clauses.toList() shouldBe List(5) { Fact.of(Struct.of("counter", Integer.of(it + 10))) }
            // --8<-- [end:mutable-theory]
        }

        test("convert-between-flavours") {
            // --8<-- [start:conversions]
            val static = Theory.of(parent("abraham", "isaac"))
            val dynamic = static.toMutableTheory() // a copy: changes to it do not affect `static`
            dynamic.assertZ(parent("isaac", "jacob"))
            val snapshot = dynamic.toImmutableTheory() // a copy: later changes to `dynamic` do not affect it
            dynamic.abolish(Indicator.of("parent", 2))

            static.size shouldBe 1L
            snapshot.size shouldBe 2L
            dynamic.size shouldBe 0L
            static.toImmutableTheory() shouldBeSameInstanceAs static // already immutable: no copy
            // --8<-- [end:conversions]
        }

        test("matching-vs-variants") {
            // --8<-- [start:matching-vs-variants]
            val theory = Theory.of(Fact.of(Struct.of("f", Var.of("X"))), Fact.of(Struct.of("f", Atom.of("a"))))
            val query = Fact.of(Struct.of("f", Atom.of("b")))

            (query in theory) shouldBe true // f(X) unifies with f(b)
            theory.containsVariant(query) shouldBe false // but no stored clause is f(b), up to renaming
            theory.containsVariant(Fact.of(Struct.of("f", Var.of("Y")))) shouldBe true // f(X) is a variant of f(Y)
            theory.countMatching(Fact.of(Struct.of("f", Atom.of("a")))) shouldBe 2L
            // --8<-- [end:matching-vs-variants]
        }

        test("choose-a-collection") {
            // --8<-- [start:collections]
            val x = Var.of("X")
            val clauses: List<Clause> =
                listOf(Fact.of(Struct.of("p", x, x)), Fact.of(Struct.of("p", Atom.of("a"), Atom.of("a"))))

            // ordered, with positional access
            val list = ClauseList.of(Unificator.default, clauses)
            list[1] shouldBe clauses[1]
            // order-insensitive equality
            ClauseMultiSet.of(Unificator.default, clauses) shouldBe
                ClauseMultiSet.of(Unificator.default, clauses.reversed())
            // no two variants: p(Y, Y) is a variant of p(X, X), so it is not added
            val set = MutableClauseSet.of(Unificator.default, clauses)
            val y = Var.of("Y") // each Var.of call makes a distinct variable, whatever its name
            set.add(Fact.of(Struct.of("p", y, y))) shouldBe false
            set.size shouldBe 2L
            // --8<-- [end:collections]
        }

        test("attach-data-to-clauses") {
            // --8<-- [start:clause-map]
            val probabilities = MutableClauseMap.empty<Double>()
            probabilities.put(parent("abraham", "isaac"), 0.9)
            probabilities.put(parent("isaac", "jacob"), 0.7)

            // by key, up to variable renaming
            probabilities[parent("isaac", "jacob")] shouldBe 0.7
            // by unification, through the clause indexes
            probabilities
                .entriesMatching(Fact.of(Struct.of("parent", Var.of("P"), Atom.of("isaac"))))
                .map { it.second }
                .toList() shouldBe listOf(0.9)
            // --8<-- [end:clause-map]
        }

        test("candidates-for-solvers") {
            // --8<-- [start:candidates]
            val theory = Theory.of(List(10) { Fact.of(Struct.of("n", Integer.of(it))) })
            val goal = Fact.of(Struct.of("n", Integer.of(3)))

            // candidates: index-selected, NOT unified yet (a superset of theory[goal], in order)
            val candidates = theory.candidates(goal).toList()
            candidates.filter { Unificator.default.match(it, goal) } shouldBe theory[goal].toList()
            // --8<-- [end:candidates]
        }

        test("index-keys-follow-the-unificator") {
            // --8<-- [start:naive-keys]
            val clauses = List(10) { Fact.of(Struct.of("n", Integer.of(it))) }
            val oneAsReal = Fact.of(Struct.of("n", Real.of(1.0)))

            Theory.of(Unificator.strict(), clauses)[oneAsReal].toList() shouldBe emptyList()
            Theory.of(Unificator.naive(), clauses)[oneAsReal].toList() shouldBe listOf(clauses[1])
            // --8<-- [end:naive-keys]
        }

        test("custom-unificator-with-coarser-keys") {
            // --8<-- [start:case-insensitive]
            val caseInsensitive =
                object : AbstractWorklistUnificator() {
                    override fun checkTermsEquality(
                        first: Term,
                        second: Term,
                    ): Boolean =
                        when {
                            first.isAtom && second.isAtom ->
                                first.castToAtom().value.equals(second.castToAtom().value, ignoreCase = true)
                            else -> first == second
                        }

                    // atoms differing only by case unify, so they must share a key
                    override fun indexKey(term: Term): IndexKey =
                        if (term.isAtom) IndexKey.OfAtom(term.castToAtom().value.lowercase()) else IndexKey.strict(term)
                }
            // --8<-- [end:case-insensitive]
            val names = listOf("Alice", "bob", "Carol", "dave", "Eve").map { Fact.of(Struct.of("name", Atom.of(it))) }
            val theory = Theory.of(caseInsensitive, names)
            theory[Fact.of(Struct.of("name", Atom.of("ALICE")))].toList() shouldBe listOf(names[0])
            theory.retract(Fact.of(Struct.of("name", Atom.of("BOB")))).shouldBeInstanceOf<RetractResult.Success<*>>()
            theory.setUnificator(Unificator.default)[Fact.of(Struct.of("name", Atom.of("ALICE")))].toList() shouldBe
                emptyList()
            theory.rules.toList() shouldContainExactlyInAnyOrder names
        }
    })
