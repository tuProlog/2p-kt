package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.DirectiveUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.*


/**
 * Test class for [DirectiveImpl] and [Directive]
 *
 * @author Enrico
 */
internal class DirectiveImplTest {

    private val groundDirectivesInstances = DirectiveUtils.groundDirectives.map(::DirectiveImpl)
    private val nonGroundDirectivesInstances = DirectiveUtils.nonGroundDirectives.map(::DirectiveImpl)
    private val wellFormedDirectivesInstances = DirectiveUtils.wellFormedDirectives.map(::DirectiveImpl)
    private val nonWellFormedDirectivesInstances = DirectiveUtils.nonWellFormedDirectives.map(::DirectiveImpl)
    private val mixedDirectivesInstances = DirectiveUtils.mixedDirectives.map(::DirectiveImpl)

    @Test
    fun headNull() {
        mixedDirectivesInstances.forEach { assertNull(it.head) }
    }

    @Test
    fun bodyCorrect() {
        onCorrespondingItems(
            DirectiveUtils.mixedDirectives,
            mixedDirectivesInstances.map { it.body },
            ::assertEqualities
        )
    }

    @Test
    fun isWellFormedReturnsTrueIfDirectiveWellFormed() {
        wellFormedDirectivesInstances.forEach { assertTrue("$it isWellFormed should be true") { it.isWellFormed } }
    }

    @Test
    fun isWellFormedReturnsFalseIfDirectiveNotWellFormed() {
        nonWellFormedDirectivesInstances.forEach { assertFalse("$it isWellFormed should be false") { it.isWellFormed } }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        mixedDirectivesInstances.forEach(TermTypeAssertionUtils::assertIsDirective)
    }

    @Test
    fun isGroundTrueIfNoVariablesArePresent() {
        groundDirectivesInstances.forEach { assertTrue("$it isGround should be true") { it.isGround } }
        nonGroundDirectivesInstances.forEach { assertFalse("$it isGround should be false") { it.isGround } }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        groundDirectivesInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        nonGroundDirectivesInstances.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun functorCorrect() {
        mixedDirectivesInstances.forEach { assertEquals(":-", it.functor) }
    }

    @Test
    fun argsCorrect() {
        val correctArgs = DirectiveUtils.mixedDirectives.map { arrayOf(it) }

        onCorrespondingItems(correctArgs, mixedDirectivesInstances.map { it.args }) { expected, actual ->
            assertEquals(expected.toList(), actual.toList())
            assertTrue { expected.contentDeepEquals(actual) }
        }
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctToStrings = mixedDirectivesInstances.map { "${it.functor} ${it.body}" }

        onCorrespondingItems(correctToStrings, mixedDirectivesInstances.map { it.toString() }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }
}
