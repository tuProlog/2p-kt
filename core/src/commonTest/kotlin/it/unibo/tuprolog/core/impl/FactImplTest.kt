package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.*


/**
 * Test class for [FactImpl] and [Fact]
 *
 * @author Enrico
 */
internal class FactImplTest {

    private val mixedFactInstances by lazy { FactUtils.mixedFacts.map(::FactImpl) }
    private val groundFactInstances by lazy { FactUtils.groundFacts.map(::FactImpl) }
    private val nonGroundFactInstances by lazy { FactUtils.nonGroundFacts.map(::FactImpl) }

    @Test
    fun headCorrect() {
        onCorrespondingItems(FactUtils.mixedFacts, mixedFactInstances.map { it.head }, ::assertEqualities)
    }

    @Test
    fun argsCorrect() {
        val correctFactArgs = FactUtils.mixedFacts.map { listOf(it, Truth.TRUE) }

        onCorrespondingItems(correctFactArgs, mixedFactInstances.map { it.args.toList() }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun arityIsTwo() {
        mixedFactInstances.forEach { assertEquals(2, it.arity) }
    }

    @Test
    fun bodyIsTrue() {
        mixedFactInstances.forEach { assertSame(Truth.TRUE, it.body) }
    }

    @Test
    fun isWellFormedAlwaysTrue() {
        mixedFactInstances.forEach { assertTrue { it.isWellFormed } }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        mixedFactInstances.forEach(TermTypeAssertionUtils::assertIsFact)
    }

    @Test
    fun isGroundTrueIfNoVariablesArePresent() {
        groundFactInstances.forEach { assertTrue { it.isGround } }
        nonGroundFactInstances.forEach { assertFalse { it.isGround } }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        groundFactInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        nonGroundFactInstances.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }
}
