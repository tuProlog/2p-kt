package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.AtomImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.AtomUtils
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test class for [Atom] companion object
 *
 * @author Enrico
 */
internal class AtomTest {
    @Test
    fun emptySetAtomDetected() {
        assertEqualities(Atom.of("{}"), Empty.block())
    }

    @Test
    fun emptyListAtomDetected() {
        assertEqualities(Atom.of("[]"), Empty.list())
    }

    @Test
    fun trueAtomDetected() {
        assertEqualities(Atom.of("true"), Truth.TRUE)
    }

    @Test
    fun falseAtomDetected() {
        assertEqualities(Atom.of("false"), Truth.FALSE)
    }

    @Test
    fun failAtomDetected() {
        assertEqualities(Atom.of("fail"), Truth.FAIL)
    }

    @Test
    fun atomOfWorksAsExpected() {
        val correctInstances = AtomUtils.nonSpecialAtoms.map(::AtomImpl)
        val toBeTested = AtomUtils.nonSpecialAtoms.map { Atom.of(it) }

        onCorrespondingItems(toBeTested, correctInstances, ::assertEqualities)
    }

    @Test
    fun atomOfWorksWithNotableAtoms() {
        val correctInstances = listOf(EmptyList(), EmptyBlock(), Truth.TRUE, Truth.FAIL, Truth.FALSE)
        val toBeTested = AtomUtils.specialAtoms.map { Atom.of(it) }

        onCorrespondingItems(toBeTested, correctInstances, ::assertEqualities)
    }

    @Test
    fun atomIsAValidFunctor() {
        val correctAtoms = AtomUtils.mixedAtoms
        val correctAtomInstances = correctAtoms.map { Atom.of(it) }

        correctAtoms
            .zip(correctAtomInstances)
            .filter { (atomString, _) -> atomString matches Atom.ATOM_PATTERN }
            .forEach { (_, atomInstance) ->
                assertTrue { atomInstance.isFunctorWellFormed }
            }
    }
}
