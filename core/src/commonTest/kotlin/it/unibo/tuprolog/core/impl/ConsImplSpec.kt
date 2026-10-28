package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals

/**
 * Test class for [ConsImpl] and [Cons]
 *
 * @author Enrico
 */
class ConsImplSpec :
    FunSpec({
        fun conses(
            property: String,
            cases: List<ConsUtils.ConsCase> = ConsUtils.mixedConsCases,
            test: (ConsUtils.ConsCase, Cons) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it, it.cons(::ConsImpl)) }

        conses("functor") { _, cons -> assertEquals(".", cons.functor) }

        conses("head") { case, cons -> assertEqualities(case.head, cons.head) }

        conses("tail") { case, cons -> assertEqualities(case.tail(::ConsImpl), cons.tail) }

        conses("args") { case, cons -> assertEquals(listOf(case.head, case.tail(::ConsImpl)), cons.args) }

        conses("unfolded-list") { case, cons -> assertEquals(case.unfoldedList, cons.unfoldedList) }

        conses("unfolded-sequence") { case, cons -> assertEquals(case.unfoldedList, cons.unfoldedSequence.toList()) }

        conses("unfolded-array") { case, cons -> assertEquals(case.unfoldedList, cons.unfoldedArray.toList()) }

        conses("to-list") { case, cons -> assertEquals(case.elements, cons.toList()) }

        conses("to-array") { case, cons -> assertEquals(case.elements, cons.toArray().toList()) }

        conses("to-sequence") { case, cons -> assertEquals(case.elements, cons.toSequence().toList()) }

        conses("to-string") { case, cons -> assertEquals(case.expectedToString, cons.toString()) }

        conses("size") { case, cons -> assertEquals(case.elements.size, cons.size) }

        conses("is-ground") { case, cons -> assertEquals(case.isGround, cons.isGround) }

        conses("type-checks") { _, cons -> TermTypeAssertionUtils.assertIsCons(cons) }

        conses("two-arity") { _, cons -> assertEquals(cons.arity, 2) }

        conses("fresh-copy-is-itself", ConsUtils.mixedConsCases.filter { it.isGround }) { _, cons ->
            ConstantUtils.assertFreshCopyIsItself(cons)
        }

        conses("fresh-copy-renews-variables", ConsUtils.mixedConsCases.filterNot { it.isGround }) { _, cons ->
            StructUtils.assertFreshCopyRenewsContainedVariables(cons)
        }

        test("fresh-copy-merges-different-variables-with-same-name") {
            StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::ConsImpl)
        }
    })
