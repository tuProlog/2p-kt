package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
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
        fun atoms(
            property: String,
            cases: List<AtomUtils.AtomCase> = AtomUtils.mixedAtomCases,
            test: (AtomImpl) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(AtomImpl(it.value)) }

        withTests(nameFn = { "functor/${it.id}" }, AtomUtils.mixedAtomCases) { case ->
            assertEquals(case.value, AtomImpl(case.value).functor)
        }

        atoms("functor-is-the-value") { assertSame(it.value, it.functor) }

        atoms("no-arguments", test = AtomUtils::assertNoArguments)

        atoms("zero-arity") { assertEquals(0, it.arity) }

        atoms("no-variables") { assertTrue { it.variables.none() } }

        atoms("type-checks", AtomUtils.nonSpecialAtomCases, TermTypeAssertionUtils::assertIsAtom)

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

        atoms("fresh-copy-is-itself", test = ConstantUtils::assertFreshCopyIsItself)

        atoms("fresh-copy-with-scope-is-itself", test = ConstantUtils::assertFreshCopyWithScopeIsItself)

        atoms("apply-is-itself", test = ConstantUtils::assertApplyingSubstitutionIsItself)
    })
