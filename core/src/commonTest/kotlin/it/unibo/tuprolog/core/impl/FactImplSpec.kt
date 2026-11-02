package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

/**
 * Test class for [FactImpl] and [Fact]
 *
 * @author Enrico
 */
class FactImplSpec :
    FunSpec({
        fun facts(
            property: String,
            cases: List<FactUtils.FactCase> = FactUtils.mixedFactCases,
            test: (FactUtils.FactCase, Fact) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it, FactImpl(it.head)) }

        facts("head") { case, fact -> assertEqualities(case.head, fact.head) }

        facts("args") { case, fact -> fact.args shouldBe listOf(case.head, Truth.TRUE) }

        facts("arity-is-two") { _, fact -> fact.arity shouldBe 2 }

        facts("body-is-true") { _, fact -> fact.body shouldBeSameInstanceAs Truth.TRUE }

        facts("is-well-formed") { _, fact -> fact.isWellFormed.shouldBeTrue() }

        facts("type-checks") { _, fact -> TermTypeAssertionUtils.assertIsFact(fact) }

        facts("is-ground", FactUtils.groundFactCases) { _, fact -> fact.isGround.shouldBeTrue() }

        facts("is-not-ground", FactUtils.nonGroundFactCases) { _, fact -> fact.isGround.shouldBeFalse() }

        facts("fresh-copy-is-itself", FactUtils.groundFactCases) { _, fact ->
            ConstantUtils.assertFreshCopyIsItself(fact)
        }

        facts("fresh-copy-renews-variables", FactUtils.nonGroundFactCases) { _, fact ->
            StructUtils.assertFreshCopyRenewsContainedVariables(fact)
        }
    })
