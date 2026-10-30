package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
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
        val mixedVarInstances = VarUtils.mixedVars.map { VarImpl(it) }

        val mixedVarsSecondInstance = VarUtils.mixedVars.map { VarImpl(it) }

        test("correct-name") {
            onCorrespondingItems(VarUtils.mixedVars, mixedVarInstances.map { it.name }) { expectedName, varName ->
                assertEquals(expectedName, varName)
            }
        }

        test("name-handling-for-different-instances-of-same-var") {
            onCorrespondingItems(mixedVarInstances, mixedVarsSecondInstance) { firstInstance, secondInstance ->
                assertEquals(firstInstance.name, secondInstance.name)
                assertNotEquals(firstInstance.completeName, secondInstance.completeName)
            }
        }

        test("complete-name-starts-with-name") {
            VarUtils.mixedVars.zip(mixedVarInstances).forEach { (varString, varInstance) ->
                assertTrue { varInstance.completeName.startsWith(varString) }
            }
        }

        test("complete-names-contains-different-generated-numbers-if-simple-name-is-the-same") {
            assertTrue {
                mixedVarInstances
                    .groupingBy { it.name }
                    .eachCount()
                    .values
                    .all { it == 1 }
            }
        }

        test("providing-identifier-overrides-progressive-instance-count-naming") {
            assertEqualities(VarImpl("Var", 0), VarImpl("Var", 0))
        }

        test("same-var-instances-are-equals") {
            onCorrespondingItems(mixedVarInstances, mixedVarInstances, ::assertEqualities)
        }

        test("structurally-equals-works-as-expected") {
            onCorrespondingItems(mixedVarInstances, mixedVarsSecondInstance, ::assertStructurallyEquals)
        }

        test("equals-works-as-expected") {
            onCorrespondingItems(mixedVarInstances, mixedVarsSecondInstance) { firstCreatedVar, secondCreatedVar ->
                assertNotEquals(firstCreatedVar, secondCreatedVar)
            }
        }

        test("anonymous-var-detection") {
            VarUtils.mixedVars
                .filter { it != Var.ANONYMOUS_NAME }
                .map { VarImpl(it) }
                .forEach { nonAnonymousVarInstance ->
                    assertFalse { nonAnonymousVarInstance.isAnonymous }
                }

            assertTrue(VarImpl("_").isAnonymous)
        }

        test("is-name-well-formed-property") {
            VarUtils.correctlyNamedVars.map { VarImpl(it) }.forEach { assertTrue { it.isNameWellFormed } }
            VarUtils.incorrectlyNamedVars.map { VarImpl(it) }.forEach { assertFalse { it.isNameWellFormed } }
        }

        test("fresh-copy-works-as-expected") {
            val refreshedVarInstances = mixedVarInstances.map { it.freshCopy() }

            onCorrespondingItems(refreshedVarInstances, mixedVarInstances, ::assertDifferentVariableExceptForName)
        }

        test("fresh-copy-with-scope-and-no-matching-variables-inside-it") {
            val refreshedVarInstances = mixedVarInstances.map { it.freshCopy(Scope.empty()) }

            onCorrespondingItems(refreshedVarInstances, mixedVarInstances, ::assertDifferentVariableExceptForName)
        }

        test("fresh-copy-with-scope-and-matching-variable-inside-it") {
            val notAnonymousVarInstances = mixedVarInstances.filterNot { it.isAnonymous }
            val refreshedNamedVarInstances = notAnonymousVarInstances.map { it.freshCopy(Scope.of(it)) }

            onCorrespondingItems(refreshedNamedVarInstances, notAnonymousVarInstances, ::assertSameVariable)
        }

        test("fresh-copy-of-anonymous-variable-always-returns-different-variable") {
            val anonymousVar = VarImpl("_")
            val refreshedAnonymous = anonymousVar.freshCopy(Scope.of("_"))

            assertDifferentVariableExceptForName(anonymousVar, refreshedAnonymous)
        }

        test("to-string-works-as-expected") {
            val correctNamedVarsToString = VarUtils.correctlyNamedVars.map { VarImpl(it).toString() }
            val incorrectNamedVarsToString = VarUtils.incorrectlyNamedVars.map { VarImpl(it).toString() }

            onCorrespondingItems(correctNamedVarsToString, VarUtils.correctlyNamedVars) { underTestToString, varName ->
                assertTrue { underTestToString.matches("${varName}_[0-9]*".toRegex()) }
            }

            onCorrespondingItems(
                incorrectNamedVarsToString,
                VarUtils.incorrectlyNamedVars,
            ) { underTestToString, varName ->
                assertTrue { underTestToString.matches("`${varName}_[0-9]*`".toRegex()) }
            }
        }

        test("is-properties-and-types") {
            mixedVarInstances.forEach(TermTypeAssertionUtils::assertIsVar)
        }

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
