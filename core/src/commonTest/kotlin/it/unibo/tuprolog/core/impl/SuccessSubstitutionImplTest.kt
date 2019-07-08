package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Test class for [SuccessSubstitutionImpl] and [Substitution]
 *
 * @author Enrico
 */
internal class SuccessSubstitutionImplTest {

    private val aVar = Var.of("A")
    private val bVar = Var.of("B")
    private val xAtom = Atom.of("x")
    private val aVarToXAtomSubstitution = SuccessSubstitutionImpl(mapOf(aVar to xAtom))
    private val bVarToXAtomSubstitution = SuccessSubstitutionImpl(mapOf(bVar to xAtom))

    @Test
    fun shouldSubstituteVariableWithProvidedSubstitution() {
        val correct = SubstitutionUtils.termsWith(xAtom)
        val toBeTested = SubstitutionUtils.termsWith(aVar).map { aVarToXAtomSubstitution.ground(it) }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun shouldNotSubstituteNotCorrespondingVariable() {
        val correct = SubstitutionUtils.termsWith(aVar)
        val toBeTested = SubstitutionUtils.termsWith(aVar).map { bVarToXAtomSubstitution.ground(it) }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(SuccessSubstitutionImpl(mapOf(aVar to xAtom)), SuccessSubstitutionImpl(mapOf(aVar to xAtom)))
        assertNotEquals(aVarToXAtomSubstitution, bVarToXAtomSubstitution)
    }

}
