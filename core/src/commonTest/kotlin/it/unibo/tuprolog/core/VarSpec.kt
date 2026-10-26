package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.VarImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualsUsingVariablesSimpleNames
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.VarUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

/**
 * Test class for [Var] companion object
 *
 * @author Enrico
 */
class VarSpec :
    FunSpec({
        test("var-regex-correct") {
            assertTrue(VarUtils.correctlyNamedVars.all { it matches Var.NAME_PATTERN })
            assertFalse(VarUtils.incorrectlyNamedVars.any { it matches Var.NAME_PATTERN })
        }

        test("var-of-works-as-expected") {
            val correctInstances = VarUtils.mixedVars.map { VarImpl(it) }
            val toBeTested = VarUtils.mixedVars.map { Var.of(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertDifferentVariableExceptForName)
        }

        test("anonymous-works-as-expected") {
            val anonymousVarInstance = Var.anonymous()
            assertEquals("_", anonymousVarInstance.name)

            assertEqualities(anonymousVarInstance, anonymousVarInstance)

            assertEqualsUsingVariablesSimpleNames(Var.of("_"), anonymousVarInstance)
            assertStructurallyEquals(Var.of("_"), anonymousVarInstance)
            assertEqualsUsingVariablesSimpleNames(Var.anonymous(), anonymousVarInstance)
            assertStructurallyEquals(Var.anonymous(), anonymousVarInstance)

            assertNotEquals(Var.of("_"), anonymousVarInstance)
            assertNotSame(Var.of("_"), anonymousVarInstance)
            assertNotEquals(Var.anonymous(), anonymousVarInstance)
            assertNotSame(Var.anonymous(), anonymousVarInstance)
        }

        test("wildcard-works-as-expected") {
            assertTrue(Var.anonymous().isWildcard)
            assertTrue(Var.of("_").isWildcard)
            assertTrue(Var.of("_P").isWildcard)
            assertTrue(Var.of("_Foo").isWildcard)
            assertFalse(Var.of("X").isWildcard)
            assertFalse(Var.of("Foo").isWildcard)

            assertTrue(Var.of("_P").isWildcard && !Var.of("_P").isAnonymous)
            assertTrue(Var.anonymous().let { it.isWildcard && it.isAnonymous })
        }

        test("named-vars-works-as-expected") {
            val instance = Var.of("X")
            assertEquals("X", instance.name)

            assertEqualities(instance, instance)

            assertEqualsUsingVariablesSimpleNames(Var.of("X"), instance)
            assertStructurallyEquals(Var.of("X"), instance)

            assertNotEquals(Var.of("X"), instance)
            assertNotSame(Var.of("X"), instance)
        }
    })
