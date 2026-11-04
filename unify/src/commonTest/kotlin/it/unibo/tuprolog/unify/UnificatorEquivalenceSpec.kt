package it.unibo.tuprolog.unify

import io.kotest.assertions.withClue
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.choice
import io.kotest.property.checkAll
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.testutils.TermArbs
import it.unibo.tuprolog.unify.testutils.ReferenceUnificators
import it.unibo.tuprolog.unify.testutils.WorklistUnificators

@OptIn(ExperimentalKotest::class)
private val config = PropTestConfig(seed = 1020L, iterations = 500)

/** Random pairs (which rarely unify) mixed with term/instance pairs (which always do). */
private val pairs = Arb.choice(TermArbs.termPairs(), TermArbs.instancePairs())

/** A worklist strategy and the equation-based one it must agree with. */
private class StrategyCase(
    val id: String,
    val worklist: (Substitution) -> Unificator,
    val reference: (Substitution) -> Unificator,
)

private val strategies =
    listOf(
        StrategyCase("strict", WorklistUnificators::strict, ReferenceUnificators::strict),
        StrategyCase("naive", WorklistUnificators::naive, ReferenceUnificators::naive),
    )

/** An agreement property, checked on pairs drawn from [gen]. */
private class PropertyCase(
    val id: String,
    val gen: Gen<Pair<Term, Term>>,
    val check: StrategyCase.(Pair<Term, Term>, Pair<Term, Term>) -> Unit,
)

private fun agree(
    clue: String,
    actual: Substitution,
    expected: Substitution,
) = withClue(clue) { actual shouldBe expected }

private val properties =
    listOf(
        PropertyCase("mgu-with-occurs-check", pairs) { (t1, t2), _ ->
            agree(
                "mgu($t1, $t2)",
                worklist(Substitution.empty()).mgu(t1, t2),
                reference(Substitution.empty()).mgu(t1, t2),
            )
        },
        // random pairs can make the equation-based engine loop forever on cyclic terms: only instances here
        PropertyCase("mgu-without-occurs-check", TermArbs.instancePairs()) { (t1, t2), _ ->
            agree(
                "mgu($t1, $t2)",
                worklist(Substitution.empty()).mgu(t1, t2, false),
                reference(Substitution.empty()).mgu(t1, t2, false),
            )
        },
        PropertyCase("mgu-in-context", pairs) { (t1, t2), (c1, c2) ->
            val context = reference(Substitution.empty()).mgu(c1, c2)
            agree("mgu($t1, $t2) in $context", worklist(context).mgu(t1, t2), reference(context).mgu(t1, t2))
        },
        PropertyCase("merge", pairs) { (t1, t2), (u1, u2) ->
            val s1 = reference(Substitution.empty()).mgu(t1, t2)
            val s2 = reference(Substitution.empty()).mgu(u1, u2)
            agree(
                "merge($s1, $s2)",
                worklist(Substitution.empty()).merge(s1, s2),
                reference(Substitution.empty()).merge(s1, s2),
            )
        },
    )

private val cases = strategies.flatMap { strategy -> properties.map { strategy to it } }

/** The worklist engine computes exactly the substitutions the equation-based one does, where the latter terminates. */
class UnificatorEquivalenceSpec :
    FunSpec({
        withTests(nameFn = { (strategy, property) -> "${strategy.id}/${property.id}" }, cases) { (strategy, property) ->
            checkAll(config, property.gen, pairs) { pair, other -> strategy.(property.check)(pair, other) }
        }
    })
