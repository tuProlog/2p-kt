package it.unibo.tuprolog.core.impl

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

/**
 * Test class for [DirectiveImpl] and [Directive]
 *
 * @author Enrico
 */
class DirectiveImplSpec :
    FunSpec({
        fun directives(
            property: String,
            cases: List<DirectiveUtils.DirectiveCase> = DirectiveUtils.mixedDirectiveCases,
            test: (DirectiveUtils.DirectiveCase, Directive) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it, DirectiveImpl(it.body)) }

        directives("head-null") { _, directive -> directive.head.shouldBeNull() }

        directives("body") { case, directive -> assertEqualities(case.body, directive.body) }

        directives("is-well-formed", DirectiveUtils.wellFormedDirectiveCases) { _, directive ->
            withClue("$directive isWellFormed should be true") { directive.isWellFormed.shouldBeTrue() }
        }

        directives("is-not-well-formed", DirectiveUtils.nonWellFormedDirectiveCases) { _, directive ->
            withClue("$directive isWellFormed should be false") { directive.isWellFormed.shouldBeFalse() }
        }

        directives("type-checks") { _, directive -> TermTypeAssertionUtils.assertIsDirective(directive) }

        directives("is-ground", DirectiveUtils.groundDirectiveCases) { _, directive ->
            withClue("$directive isGround should be true") { directive.isGround.shouldBeTrue() }
        }

        directives("is-not-ground", DirectiveUtils.nonGroundDirectiveCases) { _, directive ->
            withClue("$directive isGround should be false") { directive.isGround.shouldBeFalse() }
        }

        directives("fresh-copy-is-itself", DirectiveUtils.groundDirectiveCases) { _, directive ->
            ConstantUtils.assertFreshCopyIsItself(directive)
        }

        directives("fresh-copy-renews-variables", DirectiveUtils.nonGroundDirectiveCases) { _, directive ->
            StructUtils.assertFreshCopyRenewsContainedVariables(directive)
        }

        directives("functor") { _, directive -> directive.functor shouldBe ":-" }

        directives("args") { case, directive -> directive.args shouldBe listOf(case.body) }

        directives("to-string") { _, directive ->
            directive.toString() shouldBe "${directive.functor} ${directive.body}"
        }
    })
