package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

        directives("head-null") { _, directive -> assertNull(directive.head) }

        directives("body") { case, directive -> assertEqualities(case.body, directive.body) }

        directives("is-well-formed", DirectiveUtils.wellFormedDirectiveCases) { _, directive ->
            assertTrue("$directive isWellFormed should be true") { directive.isWellFormed }
        }

        directives("is-not-well-formed", DirectiveUtils.nonWellFormedDirectiveCases) { _, directive ->
            assertFalse("$directive isWellFormed should be false") { directive.isWellFormed }
        }

        directives("type-checks") { _, directive -> TermTypeAssertionUtils.assertIsDirective(directive) }

        directives("is-ground", DirectiveUtils.groundDirectiveCases) { _, directive ->
            assertTrue("$directive isGround should be true") { directive.isGround }
        }

        directives("is-not-ground", DirectiveUtils.nonGroundDirectiveCases) { _, directive ->
            assertFalse("$directive isGround should be false") { directive.isGround }
        }

        directives("fresh-copy-is-itself", DirectiveUtils.groundDirectiveCases) { _, directive ->
            ConstantUtils.assertFreshCopyIsItself(directive)
        }

        directives("fresh-copy-renews-variables", DirectiveUtils.nonGroundDirectiveCases) { _, directive ->
            StructUtils.assertFreshCopyRenewsContainedVariables(directive)
        }

        directives("functor") { _, directive -> assertEquals(":-", directive.functor) }

        directives("args") { case, directive -> assertEquals(listOf(case.body), directive.args) }

        directives("to-string") { _, directive ->
            assertEquals("${directive.functor} ${directive.body}", directive.toString())
        }
    })
