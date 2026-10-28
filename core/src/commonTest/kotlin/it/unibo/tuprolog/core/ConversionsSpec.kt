package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
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
        withTests(nameFn = { "big-integer-to-term/${it.id}" }, IntegerUtils.integerCases) { case ->
            assertEqualities(Integer.of(case.value), case.value.toTerm())
        }

        withTests(nameFn = { "big-decimal-to-term/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Real.of(case.value), case.value.toTerm())
        }

        withTests(nameFn = { "float-to-term/${it.id}" }, RealUtils.realCases.filter { it.float != null }) { case ->
            assertEqualities(Real.of(case.float!!), case.float!!.toTerm())
        }

        withTests(nameFn = { "double-to-term/${it.id}" }, RealUtils.realCases) { case ->
            assertEqualities(Real.of(case.double), case.double.toTerm())
        }

        withTests(nameFn = { "int-to-term/${it.id}" }, IntegerUtils.integerCases.filter { it.int != null }) { case ->
            assertEqualities(Integer.of(case.int!!), case.int!!.toTerm())
        }

        withTests(nameFn = { "long-to-term/${it.id}" }, IntegerUtils.integerCases.filter { it.long != null }) { case ->
            assertEqualities(Integer.of(case.long!!), case.long!!.toTerm())
        }

        withTests(
            nameFn = { "short-to-term/${it.id}" },
            IntegerUtils.integerCases.filter { it.short != null },
        ) { case ->
            assertEqualities(Integer.of(case.short!!), case.short!!.toTerm())
        }

        withTests(nameFn = { "byte-to-term/${it.id}" }, IntegerUtils.integerCases.filter { it.byte != null }) { case ->
            assertEqualities(Integer.of(case.byte!!), case.byte!!.toTerm())
        }

        fun numberToTerm(
            kind: String,
            values: kotlin.collections.List<Pair<String, Number>>,
        ) = withTests(nameFn = { "number-to-term/$kind/${it.first}" }, values) { (_, value) ->
            assertEqualities(Numeric.of(value), value.toTerm())
        }

        numberToTerm("byte", IntegerUtils.integerCases.mapNotNull { case -> case.byte?.let { case.id to it } })

        numberToTerm("int", IntegerUtils.integerCases.mapNotNull { case -> case.int?.let { case.id to it } })

        numberToTerm("short", IntegerUtils.integerCases.mapNotNull { case -> case.short?.let { case.id to it } })

        numberToTerm("long", IntegerUtils.integerCases.mapNotNull { case -> case.long?.let { case.id to it } })

        numberToTerm("double", RealUtils.realCases.map { it.id to it.double })

        numberToTerm("float", RealUtils.realCases.mapNotNull { case -> case.float?.let { case.id to it } })

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
