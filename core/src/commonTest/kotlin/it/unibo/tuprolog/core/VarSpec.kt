package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import it.unibo.tuprolog.core.impl.VarImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualsUsingVariablesSimpleNames
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.VarUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName

/**
 * Test class for [Var] companion object
 *
 * @author Enrico
 */
class VarSpec :
    FunSpec({
        withTests(nameFn = { "name-pattern-matches/${it.id}" }, VarUtils.correctlyNamedVarCases) { case ->
            (case.name matches Var.NAME_PATTERN).shouldBeTrue()
        }

        withTests(nameFn = { "name-pattern-does-not-match/${it.id}" }, VarUtils.incorrectlyNamedVarCases) { case ->
            (case.name matches Var.NAME_PATTERN).shouldBeFalse()
        }

        withTests(nameFn = { "of/${it.id}" }, VarUtils.mixedVarCases) { case ->
            assertDifferentVariableExceptForName(VarImpl(case.name), Var.of(case.name))
        }

        test("anonymous-works-as-expected") {
            val anonymousVarInstance = Var.anonymous()
            anonymousVarInstance.name shouldBe "_"

            assertEqualities(anonymousVarInstance, anonymousVarInstance)

            assertEqualsUsingVariablesSimpleNames(Var.of("_"), anonymousVarInstance)
            assertStructurallyEquals(Var.of("_"), anonymousVarInstance)
            assertEqualsUsingVariablesSimpleNames(Var.anonymous(), anonymousVarInstance)
            assertStructurallyEquals(Var.anonymous(), anonymousVarInstance)

            anonymousVarInstance shouldNotBe Var.of("_")
            anonymousVarInstance shouldNotBeSameInstanceAs Var.of("_")
            anonymousVarInstance shouldNotBe Var.anonymous()
            anonymousVarInstance shouldNotBeSameInstanceAs Var.anonymous()
        }

        test("wildcard-works-as-expected") {
            Var.anonymous().isWildcard.shouldBeTrue()
            Var.of("_").isWildcard.shouldBeTrue()
            Var.of("_P").isWildcard.shouldBeTrue()
            Var.of("_Foo").isWildcard.shouldBeTrue()
            Var.of("X").isWildcard.shouldBeFalse()
            Var.of("Foo").isWildcard.shouldBeFalse()

            (Var.of("_P").isWildcard && !Var.of("_P").isAnonymous).shouldBeTrue()
            (Var.anonymous().let { it.isWildcard && it.isAnonymous }).shouldBeTrue()
        }

        test("named-vars-works-as-expected") {
            val instance = Var.of("X")
            instance.name shouldBe "X"

            assertEqualities(instance, instance)

            assertEqualsUsingVariablesSimpleNames(Var.of("X"), instance)
            assertStructurallyEquals(Var.of("X"), instance)

            instance shouldNotBe Var.of("X")
            instance shouldNotBeSameInstanceAs Var.of("X")
        }

        withTests(
            nameFn = { "escape-name-if-necessary/${it.first}" },
            listOf("well-formed" to ("X" to "X"), "lower-case" to ("x" to "`x`"), "spaces" to ("A B" to "`A B`")),
        ) { (_, io) ->
            Var.escapeNameIfNecessary(io.first) shouldBe io.second
        }
    })
