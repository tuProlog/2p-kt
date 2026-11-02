package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

/**
 * Test class for [AtomImpl] and [Atom]
 *
 * @author Enrico
 */
class AtomImplSpec :
    FunSpec({
        fun atoms(
            property: String,
            cases: List<AtomUtils.AtomCase> = AtomUtils.mixedAtomCases,
            test: (AtomImpl) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(AtomImpl(it.value)) }

        withTests(nameFn = { "functor/${it.id}" }, AtomUtils.mixedAtomCases) { case ->
            AtomImpl(case.value).functor shouldBe case.value
        }

        atoms("functor-is-the-value") { it.functor shouldBeSameInstanceAs it.value }

        atoms("no-arguments", test = AtomUtils::assertNoArguments)

        atoms("zero-arity") { it.arity shouldBe 0 }

        atoms("no-variables") { it.variables.none().shouldBeTrue() }

        atoms("type-checks", AtomUtils.nonSpecialAtomCases, TermTypeAssertionUtils::assertIsAtom)

        test("empty-block-atom-detected") {
            AtomImpl("{}").isEmptyBlock.shouldBeTrue()
        }

        test("empty-list-atom-detected") {
            AtomImpl("[]").isEmptyList.shouldBeTrue()
        }

        test("true-atom-detected") {
            AtomImpl("true").isTrue.shouldBeTrue()
        }

        test("fail-atom-detected") {
            AtomImpl("fail").isFail.shouldBeTrue()
        }

        test("equals-works-as-expected") {
            val trueStruct = StructImpl("true", emptyList())
            val trueAtom = AtomImpl("true")
            val trueTruth = Truth.TRUE

            trueStruct.shouldBe<Struct>(trueAtom)
            trueTruth.shouldBe<Atom>(trueAtom)
        }

        test("structurally-equals-works-as-expected") {
            val trueStruct = StructImpl("true", emptyList())
            val trueAtom = AtomImpl("true")
            val trueTruth = Truth.TRUE

            assertStructurallyEquals(trueAtom, trueStruct)
            assertStructurallyEquals(trueAtom, trueTruth)
        }

        atoms("fresh-copy-is-itself", test = ConstantUtils::assertFreshCopyIsItself)

        atoms("fresh-copy-with-scope-is-itself", test = ConstantUtils::assertFreshCopyWithScopeIsItself)

        atoms("apply-is-itself", test = ConstantUtils::assertApplyingSubstitutionIsItself)
    })
