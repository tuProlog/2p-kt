package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

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

        facts("args") { case, fact -> assertEquals(listOf(case.head, Truth.TRUE), fact.args) }

        facts("arity-is-two") { _, fact -> assertEquals(2, fact.arity) }

        facts("body-is-true") { _, fact -> assertSame(Truth.TRUE, fact.body) }

        facts("is-well-formed") { _, fact -> assertTrue { fact.isWellFormed } }

        facts("type-checks") { _, fact -> TermTypeAssertionUtils.assertIsFact(fact) }

        facts("is-ground", FactUtils.groundFactCases) { _, fact -> assertTrue { fact.isGround } }

        facts("is-not-ground", FactUtils.nonGroundFactCases) { _, fact -> assertFalse { fact.isGround } }

        facts("fresh-copy-is-itself", FactUtils.groundFactCases) { _, fact ->
            ConstantUtils.assertFreshCopyIsItself(fact)
        }

        facts("fresh-copy-renews-variables", FactUtils.nonGroundFactCases) { _, fact ->
            StructUtils.assertFreshCopyRenewsContainedVariables(fact)
        }
    })
