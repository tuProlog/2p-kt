package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

private data class HeadIndexCase(
    val id: String,
    val wildcard: Boolean,
    val build: (Unificator, List<Clause>) -> Theory,
)

private val headIndexCases =
    listOf(
        HeadIndexCase("immutable/normalized", false) { u, clauses -> Theory.of(u, clauses) },
        HeadIndexCase("immutable/wildcard", true) { u, clauses -> Theory.of(u, clauses) },
        HeadIndexCase("mutable/normalized", false) { u, clauses -> MutableTheory.of(u, clauses) },
        HeadIndexCase("mutable/wildcard", true) { u, clauses -> MutableTheory.of(u, clauses) },
    )

class HeadIndexingSpec :
    FunSpec({
        withTests(nameFn = { "custom-head-keys/${it.id}" }, headIndexCases) { case ->
            val unificator =
                object : AbstractWorklistUnificator() {
                    override fun checkTermsEquality(
                        first: Term,
                        second: Term,
                    ): Boolean =
                        if (first.isAtom && second.isAtom) {
                            first.castToAtom().value.equals(second.castToAtom().value, ignoreCase = true)
                        } else {
                            first == second
                        }

                    override fun indexKey(term: Term): IndexKey =
                        when {
                            case.wildcard -> IndexKey.Wildcard
                            term.isAtom -> IndexKey.OfAtom(term.castToAtom().value.lowercase())
                            else -> IndexKey.strict(term)
                        }
                }
            val upper = Fact.of(Atom.of("Foo"))
            val lower = Fact.of(Atom.of("foo"))
            val other = Fact.of(Atom.of("bar"))
            val directive = Directive.of(Atom.of("init"))
            var theory = case.build(unificator, listOf(upper, other, directive, lower, upper))
            unificator.match(lower, upper) shouldBe true
            val snapshot = theory[lower]
            snapshot.toList() shouldBe listOf(upper, lower, upper)
            theory.candidates(lower).filter { unificator.match(lower, it) }.toList() shouldBe snapshot.toList()
            theory[upper].toList() shouldBe snapshot.toList()

            val first = theory.retract(lower)
            first.clauses?.toList() shouldBe listOf(upper)
            theory = first.theory
            theory.toList() shouldBe listOf(other, directive, lower, upper)
            theory = theory.assertA(upper).assertZ(upper)
            snapshot.toList() shouldBe listOf(upper, lower, upper)
            val all = theory.retractAll(lower)
            all.clauses?.toList() shouldBe listOf(upper, lower, upper, upper)
            all.theory.toList() shouldBe listOf(other, directive)
            all.theory[lower].toList() shouldBe emptyList()
            val lowercaseOnly = case.build(unificator, listOf(lower))
            lowercaseOnly[upper].toList() shouldBe listOf(lower)
            lowercaseOnly.retract(upper).clauses?.toList() shouldBe listOf(lower)
        }
    })
