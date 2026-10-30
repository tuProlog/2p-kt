package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.VarUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import it.unibo.tuprolog.core.testutils.VarUtils.assertSameVariable
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [VarImpl] and [Var]
 *
 * @author Enrico
 */
class VarImplSpec :
    FunSpec({
        fun vars(
            property: String,
            cases: List<VarUtils.VarCase> = VarUtils.mixedVarCases,
            test: (VarUtils.VarCase) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it) }

        vars("name") { assertEquals(it.name, VarImpl(it.name).name) }

        vars("name-handling-for-different-instances-of-same-var") {
            val firstInstance = VarImpl(it.name)
            val secondInstance = VarImpl(it.name)
            assertEquals(firstInstance.name, secondInstance.name)
            assertNotEquals(firstInstance.completeName, secondInstance.completeName)
        }

        vars("complete-name-starts-with-name") { assertTrue { VarImpl(it.name).completeName.startsWith(it.name) } }

        test("complete-names-contains-different-generated-numbers-if-simple-name-is-the-same") {
            assertTrue {
                VarUtils.mixedVarCases
                    .map { VarImpl(it.name) }
                    .groupingBy { it.name }
                    .eachCount()
                    .values
                    .all { it == 1 }
            }
        }

        test("providing-identifier-overrides-progressive-instance-count-naming") {
            assertEqualities(VarImpl("Var", 0), VarImpl("Var", 0))
        }

        vars("same-instance-equalities") {
            val instance = VarImpl(it.name)
            assertEqualities(instance, instance)
        }

        vars("different-instances-structurally-equal") {
            assertStructurallyEquals(VarImpl(it.name), VarImpl(it.name))
        }

        vars("different-instances-not-equal") { assertNotEquals(VarImpl(it.name), VarImpl(it.name)) }

        vars("is-not-anonymous", VarUtils.mixedVarCases.filter { it.name != Var.ANONYMOUS_NAME }) {
            assertFalse { VarImpl(it.name).isAnonymous }
        }

        test("anonymous-var-detection") {
            assertTrue(VarImpl("_").isAnonymous)
        }

        vars(
            "is-name-well-formed",
            VarUtils.correctlyNamedVarCases,
        ) { assertTrue { VarImpl(it.name).isNameWellFormed } }

        vars("is-name-not-well-formed", VarUtils.incorrectlyNamedVarCases) {
            assertFalse { VarImpl(it.name).isNameWellFormed }
        }

        vars("fresh-copy") {
            val instance = VarImpl(it.name)
            assertDifferentVariableExceptForName(instance.freshCopy(), instance)
        }

        vars("fresh-copy-with-scope-not-containing-it") {
            val instance = VarImpl(it.name)
            assertDifferentVariableExceptForName(instance.freshCopy(Scope.empty()), instance)
        }

        vars("fresh-copy-with-scope-containing-it", VarUtils.mixedVarCases.filter { it.name != Var.ANONYMOUS_NAME }) {
            val instance = VarImpl(it.name)
            assertSameVariable(instance.freshCopy(Scope.of(instance)), instance)
        }

        test("fresh-copy-of-anonymous-variable-always-returns-different-variable") {
            val anonymousVar = VarImpl("_")
            val refreshedAnonymous = anonymousVar.freshCopy(Scope.of("_"))

            assertDifferentVariableExceptForName(anonymousVar, refreshedAnonymous)
        }

        vars("to-string", VarUtils.correctlyNamedVarCases) {
            assertTrue { VarImpl(it.name).toString().matches("${it.name}_[0-9]*".toRegex()) }
        }

        vars("to-string-quoted", VarUtils.incorrectlyNamedVarCases) {
            assertTrue { VarImpl(it.name).toString().matches("`${it.name}_[0-9]*`".toRegex()) }
        }

        vars("type-checks") { TermTypeAssertionUtils.assertIsVar(VarImpl(it.name)) }

        test("apply-replaces-variable-if-correct-substitution") {
            val myAtom = Atom.of("hello")
            val myVar = Var.of("X")

            val toBeTested1 = myVar.apply(Substitution.of(myVar to myAtom))
            val toBeTested2 = myVar.apply(Substitution.of(myVar to myAtom), Substitution.empty())
            val toBeTested3 = myVar[Substitution.of(myVar to myAtom)]

            assertEqualities(myAtom, toBeTested1)
            assertEqualities(myAtom, toBeTested2)
            assertEqualities(myAtom, toBeTested3)
        }

        test("apply-doesnt-replace-anything-if-no-corresponding-variable-found") {
            val myAtom = Atom.of("hello")
            val myVar = Var.of("X")

            val toBeTested1 = myVar.apply(Substitution.of(Var.of("A") to myAtom))
            val toBeTested2 = myVar.apply(Substitution.empty(), Substitution.empty())
            val toBeTested3 = myVar[Substitution.of(Var.anonymous() to myAtom)]

            assertEqualities(myVar, toBeTested1)
            assertEqualities(myVar, toBeTested2)
            assertEqualities(myVar, toBeTested3)
        }

        test("apply-replaces-following-variable-chains") {
            val myAtom = Atom.of("hello")
            val xVar = Var.of("X")
            val yVar = Var.of("Y")

            val toBeTested1 = xVar.apply(Substitution.of(xVar to yVar, yVar to myAtom))
            val toBeTested2 = xVar.apply(Substitution.of(xVar to yVar), Substitution.of(yVar to myAtom))

            assertEqualities(myAtom, toBeTested1)
            assertEqualities(myAtom, toBeTested2)
        }

        test("apply-should-not-go-into-infinite-loop-substituting-a-var-with-same-var") {
            val xVar = Var.of("X")
            val toBeTested = xVar.apply(Substitution.of(xVar to xVar))

            assertSame(xVar, toBeTested)
        }

        test("apply-should-not-go-into-infinite-loop-substituting-circular-chain-of-variables") {
            val xVar = Var.of("X")
            val yVar = Var.of("Y")

            val toBeTested = xVar.apply(Substitution.of(xVar to yVar, yVar to xVar))

            assertSame(xVar, toBeTested)
        }
    })
