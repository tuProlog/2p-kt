package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.AtomImpl
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.EqualityUtils.assertEqualities
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
        assertEqualities(Atom.of("{}"), Empty.set())
    }

    @Test
    fun emptyListAtomDetected() {
        assertEqualities(Atom.of("[]"), Empty.list())
    }

    @Test
    fun trueAtomDetected() {
        assertEqualities(Atom.of("true"), Truth.`true`())
    }

    @Test
    fun failAtomDetected() {
        assertEqualities(Atom.of("fail"), Truth.fail())
    }

    @Test
    fun atomOfWorksAsExpected() {
        val correctInstances = AtomUtils.nonSpecialAtoms.map(::AtomImpl)
        val toBeTested = AtomUtils.nonSpecialAtoms.map { Atom.of(it) }

        assertEqualities(toBeTested, correctInstances)
    }

    @Test
    fun atomOfWorksWithNotableAtoms() {
        val correctInstances = listOf(EmptyList(), EmptySet(), Truth.`true`(), Truth.fail())
        val toBeTested = AtomUtils.specialAtoms.map { Atom.of(it) }

        assertEqualities(toBeTested, correctInstances)
    }

    @Test
    fun atomIsAValidFunctor() {
        val correctAtoms = AtomUtils.mixedAtoms
        val correctAtomInstances = correctAtoms.map { Atom.of(it) }

        correctAtoms.zip(correctAtomInstances)
                .filter { (atomString, _) -> atomString.matches(Atom.ATOM_REGEX_PATTERN) }
                .forEach { (_, atomInstance) ->
                    assertTrue { atomInstance.isFunctorWellFormed }
                }
    }
}
