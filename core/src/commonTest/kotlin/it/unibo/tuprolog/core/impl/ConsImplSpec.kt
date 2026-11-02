package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

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

        conses("functor") { _, cons -> cons.functor shouldBe "." }

        conses("head") { case, cons -> assertEqualities(case.head, cons.head) }

        conses("tail") { case, cons -> assertEqualities(case.tail(::ConsImpl), cons.tail) }

        conses("args") { case, cons -> cons.args shouldBe listOf(case.head, case.tail(::ConsImpl)) }

        conses("unfolded-list") { case, cons -> cons.unfoldedList shouldBe case.unfoldedList }

        conses("unfolded-sequence") { case, cons -> cons.unfoldedSequence.toList() shouldBe case.unfoldedList }

        conses("unfolded-array") { case, cons -> cons.unfoldedArray.toList() shouldBe case.unfoldedList }

        conses("to-list") { case, cons -> cons.toList() shouldBe case.elements }

        conses("to-array") { case, cons -> cons.toArray().toList() shouldBe case.elements }

        conses("to-sequence") { case, cons -> cons.toSequence().toList() shouldBe case.elements }

        conses("to-string") { case, cons -> cons.toString() shouldBe case.expectedToString }

        conses("size") { case, cons -> cons.size shouldBe case.elements.size }

        conses("is-ground") { case, cons -> cons.isGround shouldBe case.isGround }

        conses("type-checks") { _, cons -> TermTypeAssertionUtils.assertIsCons(cons) }

        conses("two-arity") { _, cons -> 2 shouldBe cons.arity }

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
