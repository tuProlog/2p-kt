package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [AtomImpl] and [Atom]
 *
 * @author Enrico
 */
class AtomImplSpec :
    FunSpec({
        val mixedAtomInstances = AtomUtils.mixedAtoms.map(::AtomImpl)

        test("functor-correctness") {
            onCorrespondingItems(
                AtomUtils.mixedAtoms,
                mixedAtomInstances.map { it.functor },
            ) { atomString, atomInstanceFunctor -> assertEquals(atomString, atomInstanceFunctor) }
        }

        test("atom-functor-and-value-are-the-same") {
            mixedAtomInstances.forEach { assertSame(it.value, it.functor) }
        }

        test("no-arguments") {
            mixedAtomInstances.forEach(AtomUtils::assertNoArguments)
        }

        test("zero-arity") {
            mixedAtomInstances.forEach { assertEquals(0, it.arity) }
        }

        test("variables-empty") {
            mixedAtomInstances.forEach { assertTrue { it.variables.none() } }
        }

        test("is-properties-and-types-for-non-special-atom") {
            AtomUtils.nonSpecialAtoms
                .map(::AtomImpl)
                .forEach(TermTypeAssertionUtils::assertIsAtom)
        }

        test("empty-block-atom-detected") {
            assertTrue(AtomImpl("{}").isEmptyBlock)
        }

        test("empty-list-atom-detected") {
            assertTrue(AtomImpl("[]").isEmptyList)
        }

        test("true-atom-detected") {
            assertTrue(AtomImpl("true").isTrue)
        }

        test("fail-atom-detected") {
            assertTrue(AtomImpl("fail").isFail)
        }

        test("equals-works-as-expected") {
            val trueStruct = StructImpl("true", emptyList())
            val trueAtom = AtomImpl("true")
            val trueTruth = Truth.TRUE

            assertEquals<Struct>(trueAtom, trueStruct)
            assertEquals<Atom>(trueAtom, trueTruth)
        }

        test("structurally-equals-works-as-expected") {
            val trueStruct = StructImpl("true", emptyList())
            val trueAtom = AtomImpl("true")
            val trueTruth = Truth.TRUE

            assertStructurallyEquals(trueAtom, trueStruct)
            assertStructurallyEquals(trueAtom, trueTruth)
        }

        test("atom-fresh-copy-should-return-the-instance-itself") {
            mixedAtomInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("atom-fresh-copy-with-scope-should-return-the-instance-itself") {
            mixedAtomInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
        }

        test("apply-always-returns-the-instance-itself") {
            mixedAtomInstances.forEach(ConstantUtils::assertApplyingSubstitutionIsItself)
        }
    })
