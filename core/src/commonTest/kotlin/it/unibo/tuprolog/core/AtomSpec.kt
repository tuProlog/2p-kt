package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.AtomImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.AtomUtils
import kotlin.test.assertTrue

/**
 * Test class for [Atom] companion object
 *
 * @author Enrico
 */
class AtomSpec :
    FunSpec({
        test("empty-set-atom-detected") {
            assertEqualities(Atom.of("{}"), Empty.block())
        }

        test("empty-list-atom-detected") {
            assertEqualities(Atom.of("[]"), Empty.list())
        }

        test("true-atom-detected") {
            assertEqualities(Atom.of("true"), Truth.TRUE)
        }

        test("false-atom-detected") {
            assertEqualities(Atom.of("false"), Truth.FALSE)
        }

        test("fail-atom-detected") {
            assertEqualities(Atom.of("fail"), Truth.FAIL)
        }

        test("atom-of-works-as-expected") {
            val correctInstances = AtomUtils.nonSpecialAtoms.map(::AtomImpl)
            val toBeTested = AtomUtils.nonSpecialAtoms.map { Atom.of(it) }

            onCorrespondingItems(toBeTested, correctInstances, ::assertEqualities)
        }

        test("atom-of-works-with-notable-atoms") {
            val correctInstances = listOf(EmptyList(), EmptyBlock(), Truth.TRUE, Truth.FAIL, Truth.FALSE)
            val toBeTested = AtomUtils.specialAtoms.map { Atom.of(it) }

            onCorrespondingItems(toBeTested, correctInstances, ::assertEqualities)
        }

        test("atom-is-a-valid-functor") {
            val correctAtoms = AtomUtils.mixedAtoms
            val correctAtomInstances = correctAtoms.map { Atom.of(it) }

            correctAtoms
                .zip(correctAtomInstances)
                .filter { (atomString, _) -> atomString matches Atom.ATOM_PATTERN }
                .forEach { (_, atomInstance) ->
                    assertTrue { atomInstance.isFunctorWellFormed }
                }
        }
    })
