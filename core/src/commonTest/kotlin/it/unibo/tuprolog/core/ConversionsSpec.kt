package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import it.unibo.tuprolog.core.testutils.VarUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName

/**
 * Test class for conversions to [Term]
 *
 * @author Enrico
 */
class ConversionsSpec :
    FunSpec({
        test("big-integers-to-term") {
            val correct = IntegerUtils.bigIntegers.map { Integer.of(it) }
            val toBeTested = IntegerUtils.bigIntegers.map { it.toTerm() }

            onCorrespondingItems(correct, toBeTested, ::assertEqualities)
        }

        test("big-decimal-to-term") {
            val correct = RealUtils.bigDecimals.map { Real.of(it) }
            val toBeTested = RealUtils.bigDecimals.map { it.toTerm() }

            onCorrespondingItems(correct, toBeTested, ::assertEqualities)
        }

        test("float-to-term") {
            val correct = RealUtils.decimalsAsFloats.map { Real.of(it) }
            val toBeTested = RealUtils.decimalsAsFloats.map { it.toTerm() }

            onCorrespondingItems(correct, toBeTested, ::assertEqualities)
        }

        test("double-to-term") {
            val correct = RealUtils.decimalsAsDoubles.map { Real.of(it) }
            val toBeTested = RealUtils.decimalsAsDoubles.map { it.toTerm() }

            onCorrespondingItems(correct, toBeTested, ::assertEqualities)
        }

        test("int-to-term") {
            val correct = IntegerUtils.onlyInts.map { Integer.of(it) }
            val toBeTested = IntegerUtils.onlyInts.map { it.toTerm() }

            onCorrespondingItems(correct, toBeTested, ::assertEqualities)
        }

        test("long-to-term") {
            val correct = IntegerUtils.onlyLongs.map { Integer.of(it) }
            val toBeTested = IntegerUtils.onlyLongs.map { it.toTerm() }

            onCorrespondingItems(correct, toBeTested, ::assertEqualities)
        }

        test("short-to-term") {
            val correct = IntegerUtils.onlyShorts.map { Integer.of(it) }
            val toBeTested = IntegerUtils.onlyShorts.map { it.toTerm() }

            onCorrespondingItems(correct, toBeTested, ::assertEqualities)
        }

        test("byte-to-term") {
            val correct = IntegerUtils.onlyBytes.map { Integer.of(it) }
            val toBeTested = IntegerUtils.onlyBytes.map { it.toTerm() }

            onCorrespondingItems(correct, toBeTested, ::assertEqualities)
        }

        test("number-to-term") {
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

        withTests(nameFn = { "string-as-atom/${it.id}" }, AtomUtils.mixedAtomCases) { case ->
            assertEqualities(Atom.of(case.value), case.value.toAtom())
        }

        withTests(nameFn = { "string-as-var/${it.id}" }, VarUtils.correctlyNamedVarCases) { case ->
            assertDifferentVariableExceptForName(Var.of(case.name), case.name.toVar())
        }

        withTests(
            nameFn = { "string-to-term/atom/${it.id}" },
            AtomUtils.mixedAtomCases.filterNot { it.value matches Var.NAME_PATTERN },
        ) { case ->
            assertEqualities(Atom.of(case.value), case.value.toTerm())
        }

        withTests(nameFn = { "string-to-term/var/${it.id}" }, VarUtils.correctlyNamedVarCases) { case ->
            assertDifferentVariableExceptForName(Var.of(case.name), case.name.toTerm() as Var)
        }

        fun listsToTerm(
            property: String,
            toBeTested: (kotlin.collections.List<Term>) -> Term,
        ) = withTests(nameFn = { "$property/${it.id}" }, ConsUtils.mixedConsCases) { case ->
            assertEqualities(List.of(case.elements), toBeTested(case.elements))
        }

        listsToTerm("list-of-terms-to-term") { it.toTerm() }

        listsToTerm("sequence-of-terms-to-term") { it.asSequence().toTerm() }

        listsToTerm("iterable-of-terms-to-term") { it.asIterable().toTerm() }

        listsToTerm("array-of-terms-to-term") { it.toTypedArray().toTerm() }
    })
