package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import it.unibo.tuprolog.core.testutils.VarUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import kotlin.test.Test

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
        val numberValues =
            (
                with(IntegerUtils) { onlyBytes + onlyInts + onlyShorts + onlyLongs } +
                    with(RealUtils) { decimalsAsDoubles + decimalsAsFloats }
            ).map { it as Number }

        val correct = numberValues.map { Numeric.of(it) }
        val toBeTested = numberValues.map { it.toTerm() }

        onCorrespondingItems(toBeTested, correct, ::assertEqualities)
    }

    @Test
    fun stringAsAtom() {
        val correct = AtomUtils.mixedAtoms.map { Atom.of(it) }
        val toBeTested = AtomUtils.mixedAtoms.map { it.toAtom() }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun stringAsVar() {
        val correct = VarUtils.correctlyNamedVars.map { Var.of(it) }
        val toBeTested = VarUtils.correctlyNamedVars.map { it.toVar() }

        onCorrespondingItems(correct, toBeTested, ::assertDifferentVariableExceptForName)
    }

    @Test
    fun stringToTerm() {
        val correctAtoms = AtomUtils.mixedAtoms.filterNot { it matches Var.NAME_PATTERN }.map { Atom.of(it) }
        val toBeTestedAtoms = AtomUtils.mixedAtoms.filterNot { it matches Var.NAME_PATTERN }.map { it.toTerm() }

        onCorrespondingItems(correctAtoms, toBeTestedAtoms, ::assertEqualities)

        val correctVars = VarUtils.correctlyNamedVars.map { Var.of(it) }
        val toBeTestedVars = VarUtils.correctlyNamedVars.map { it.toTerm() as Var }

        onCorrespondingItems(correctVars, toBeTestedVars, ::assertDifferentVariableExceptForName)
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
}
