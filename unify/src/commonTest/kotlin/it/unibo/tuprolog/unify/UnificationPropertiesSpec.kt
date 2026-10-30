package it.unibo.tuprolog.unify

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.choice
import io.kotest.property.checkAll
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.testutils.TermArbs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalKotest::class)
private val config = PropTestConfig(seed = 1017L, iterations = 500)

private val unificator = Unificator.default

/** Random pairs (which rarely unify) mixed with term/instance pairs (which always do). */
private val pairs = Arb.choice(TermArbs.termPairs(), TermArbs.instancePairs())

/** Applying a successful mgu to both terms makes them equal. */
private fun assertSound(
    first: Term,
    second: Term,
) {
    val mgu = unificator.mgu(first, second)
    if (mgu.isSuccess) assertEquals(first[mgu], second[mgu], "mgu($first, $second) = $mgu")
}

/** Applying an mgu twice is the same as applying it once: no bound variable occurs in its range. */
private fun assertIdempotent(
    first: Term,
    second: Term,
) {
    val mgu = unificator.mgu(first, second)
    if (mgu.isFailed) return
    val bound = mgu.keys
    assertTrue(mgu.values.none { value -> value.variables.any { it in bound } }, "mgu($first, $second) = $mgu")
    assertEquals(first[mgu], first[mgu][mgu])
}

private fun assertSymmetric(
    first: Term,
    second: Term,
) = assertEquals(unificator.match(first, second), unificator.match(second, first), "match($first, $second)")

/** A hand-picked pair, built in one [Scope] so that equally named variables are the same variable. */
private class PairCase(
    val id: String,
    private val build: Scope.() -> Pair<Term, Term>,
) {
    fun terms(): Pair<Term, Term> = Scope.empty().build()
}

/** Regression examples kept beside the properties: shared variables, occurs-check, partial lists. */
private val regressions =
    listOf(
        PairCase("shared-variable") { structOf("f", varOf("X"), varOf("X")) to structOf("f", atomOf("a"), varOf("Y")) },
        PairCase("chained-variables") {
            structOf("g", varOf("X"), varOf("Y")) to structOf("g", varOf("Y"), structOf("f", varOf("Z")))
        },
        PairCase("occurs-check") { varOf("X") to structOf("f", varOf("X")) },
        PairCase("partial-list") { consOf(varOf("H"), varOf("T")) to logicListOf(intOf(1), intOf(2), intOf(3)) },
        PairCase("int-vs-real") { structOf("f", intOf(1)) to structOf("f", realOf(1.0)) },
    )

class UnificationPropertiesSpec :
    FunSpec({
        test("a-term-unifies-with-itself-by-the-empty-substitution") {
            checkAll(config, TermArbs.nonGroundTerms()) { term ->
                val mgu = unificator.mgu(term, term)
                assertTrue(mgu.isSuccess && mgu.isEmpty(), "mgu($term, $term) = $mgu")
            }
        }

        test("mgu-is-sound") {
            checkAll(config, pairs) { (first, second) -> assertSound(first, second) }
        }

        test("mgu-is-idempotent") {
            checkAll(config, pairs) { (first, second) -> assertIdempotent(first, second) }
        }

        test("unifiability-is-symmetric") {
            checkAll(config, pairs) { (first, second) -> assertSymmetric(first, second) }
        }

        test("a-term-unifies-with-its-instances") {
            checkAll(config, TermArbs.instancePairs()) { (term, instance) ->
                assertTrue(unificator.match(term, instance), "match($term, $instance)")
            }
        }

        test("ground-terms-unify-iff-equal") {
            checkAll(config, TermArbs.groundTerms(), TermArbs.groundTerms()) { first, second ->
                assertEquals(first == second, unificator.match(first, second), "match($first, $second)")
            }
        }

        test("a-term-unifies-with-its-fresh-copy") {
            checkAll(config, TermArbs.nonGroundTerms()) { term ->
                assertTrue(unificator.match(term, term.freshCopy()), "match($term, ${term.freshCopy()})")
            }
        }

        test("applying-the-empty-substitution-is-the-identity") {
            checkAll(config, TermArbs.nonGroundTerms()) { term ->
                assertEquals(term, term[Substitution.empty()])
            }
        }

        withTests(nameFn = { "regressions/${it.id}" }, regressions) { case ->
            val (first, second) = case.terms()
            assertSound(first, second)
            assertIdempotent(first, second)
            assertSymmetric(first, second)
        }
    })
