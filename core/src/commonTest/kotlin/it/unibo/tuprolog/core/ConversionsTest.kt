package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.*
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

/**
 * Test class for conversions to [Term]
 *
 * @author Enrico
 */
internal class ConversionsTest {

    @Test
    fun bigIntegersToTerm() {
        val correct = IntegerUtils.bigIntegers.map { Integer.of(it) }
        val toBeTested = IntegerUtils.bigIntegers.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun bigDecimalToTerm() {
        val correct = RealUtils.bigDecimals.map { Real.of(it) }
        val toBeTested = RealUtils.bigDecimals.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun floatToTerm() {
        val correct = RealUtils.decimalsAsFloats.map { Real.of(it) }
        val toBeTested = RealUtils.decimalsAsFloats.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun doubleToTerm() {
        val correct = RealUtils.decimalsAsDoubles.map { Real.of(it) }
        val toBeTested = RealUtils.decimalsAsDoubles.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun intToTerm() {
        val correct = IntegerUtils.onlyInts.map { Integer.of(it) }
        val toBeTested = IntegerUtils.onlyInts.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun longToTerm() {
        val correct = IntegerUtils.onlyLongs.map { Integer.of(it) }
        val toBeTested = IntegerUtils.onlyLongs.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun shortToTerm() {
        val correct = IntegerUtils.onlyShorts.map { Integer.of(it) }
        val toBeTested = IntegerUtils.onlyShorts.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun byteToTerm() {
        val correct = IntegerUtils.onlyBytes.map { Integer.of(it) }
        val toBeTested = IntegerUtils.onlyBytes.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun numberToTerm() {
        @Suppress("USELESS_CAST")
        val numberValues = (with(IntegerUtils) { onlyBytes + onlyInts + onlyShorts + onlyLongs } +
                with(RealUtils) { decimalsAsDoubles + decimalsAsFloats }).map { it as Number }

        val correct = numberValues.map { Numeric.of(it) }
        val toBeTested = numberValues.map { it.toTerm() }

        onCorrespondingItems(toBeTested, correct, ::assertEqualities)
    }

    @Test
    fun stringAsAtom() {
        val correct = AtomUtils.mixedAtoms.map { Atom.of(it) }
        val toBeTested = AtomUtils.mixedAtoms.map { it.asAtom() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun stringAsVar() {
        val correct = VarUtils.correctlyNamedVars.map { Var.of(it) }
        val toBeTested = VarUtils.correctlyNamedVars.map { it.asVar() }

        onCorrespondingItems(correct, toBeTested) { correctVar, underTest ->
            assertEquals(correctVar, underTest)
            assertStructurallyEquals(correctVar, underTest)
        }
    }

    @Test
    fun stringToTerm() {
        val correctAtoms = AtomUtils.mixedAtoms.filterNot { it matches Var.VAR_REGEX_PATTERN }.map { Atom.of(it) }
        val toBeTestedAtoms = AtomUtils.mixedAtoms.filterNot { it matches Var.VAR_REGEX_PATTERN }.map { it.toTerm() }

        onCorrespondingItems(correctAtoms, toBeTestedAtoms, ::assertEqualities)

        val correctVars = VarUtils.correctlyNamedVars.map { Var.of(it) }
        val toBeTestedVars = VarUtils.correctlyNamedVars.map { it.toTerm() }

        onCorrespondingItems(correctVars, toBeTestedVars) { correctVar, underTest ->
            assertEquals(correctVar, underTest)
            assertStructurallyEquals(correctVar, underTest)
        }
    }

    @Test
    fun listOfTermsToTerm() {
        val correct = ConsUtils.mixedConsInstancesElementLists.map { List.of(it) }
        val toBeTested = ConsUtils.mixedConsInstancesElementLists.map { it.toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun sequenceOfTermsToTerm() {
        val correct = ConsUtils.mixedConsInstancesElementLists.map { List.of(it) }
        val toBeTested = ConsUtils.mixedConsInstancesElementLists.map { it.asSequence().toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun iterableOfTermsToTerm() {
        val correct = ConsUtils.mixedConsInstancesElementLists.map { List.of(it) }
        val toBeTested = ConsUtils.mixedConsInstancesElementLists.map { it.asIterable().toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun arrayOfTermsToTerm() {
        val correct = ConsUtils.mixedConsInstancesElementLists.map { List.of(it) }
        val toBeTested = ConsUtils.mixedConsInstancesElementLists.map { it.toTypedArray().toTerm() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun anyToTermContainsAllConversions() {
        val numberValues = (with(IntegerUtils) { bigIntegers + onlyBytes + onlyInts + onlyShorts + onlyLongs } +
                with(RealUtils) { bigDecimals + decimalsAsDoubles + decimalsAsFloats })

        val invalidTestAtoms: (String) -> Boolean = { atomString ->
            atomString matches Var.VAR_REGEX_PATTERN ||
                    atomString matches Real.REAL_REGEX_PATTERN ||
                    atomString matches Integer.INTEGER_REGEX_PATTERN
        }

        val correct =
                numberValues.map {
                    when (it) {
                        is BigDecimal -> Numeric.of(it)
                        is BigInteger -> Numeric.of(it)
                        is Number -> Numeric.of(it)
                        else -> fail("Cannot reach this")
                    }
                } + VarUtils.correctlyNamedVars.map { Var.of(it) } +
                        AtomUtils.mixedAtoms.filterNot(invalidTestAtoms).map { Atom.of(it) } +
                        ConsUtils.mixedConsInstancesElementLists.map { List.of(it) }

        val toBeTested = (numberValues +
                VarUtils.correctlyNamedVars +
                AtomUtils.mixedAtoms.filterNot(invalidTestAtoms) +
                ConsUtils.mixedConsInstancesElementLists)
                .map { it.toTerm() }


        onCorrespondingItems(correct, toBeTested) { expected, actual ->
            when {
                expected.isVariable -> {
                    assertStructurallyEquals(expected, actual)
                    assertEquals(expected, actual)
                }
                else -> assertEqualities(expected, actual)
            }
        }
    }

    @Test
    fun anyToTermThrowsExceptionIfNoMatchingTypeIsFound() {
        assertFailsWith<IllegalArgumentException> { false.toTerm() }
    }
}
