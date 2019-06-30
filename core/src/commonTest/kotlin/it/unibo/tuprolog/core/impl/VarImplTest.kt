package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.VarUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [VarImpl] and [Var]
 *
 * @author Enrico
 */
internal class VarImplTest {

    private val mixedVarInstances = VarUtils.mixedVars.map { VarImpl(it) }

    @Test
    fun correctName() {
        onCorrespondingItems(VarUtils.mixedVars, mixedVarInstances.map { it.name }) { expectedName, varName ->
            assertEquals(expectedName, varName)
        }
    }

    @Test
    fun completeNameStartsWithName() {
        VarUtils.mixedVars.zip(mixedVarInstances).forEach { (varString, varInstance) ->
            assertTrue { varInstance.completeName.startsWith(varString) }
        }
    }

    @Test
    fun completeNamesContainsDifferentGeneratedNumbers() {
        val toTestUniqueIdentifiers = mixedVarInstances.map { it.completeName.substringAfterLast("_") }
        assertTrue { toTestUniqueIdentifiers.groupingBy { it }.eachCount().values.all { it == 1 } }
    }

    @Test
    fun strictlyAndStructurallyEqualsWorksAsExpected() {
        assertEqualities(VarImpl("Var", 0), VarImpl("Var", 0))

        val firstlyCreatedVars = VarUtils.mixedVars.map { VarImpl(it) }
        val secondlyCreatedVars = VarUtils.mixedVars.map { VarImpl(it) }

        onCorrespondingItems(firstlyCreatedVars, firstlyCreatedVars, ::assertEqualities)

        onCorrespondingItems(firstlyCreatedVars, secondlyCreatedVars, ::assertStructurallyEquals)
        onCorrespondingItems(firstlyCreatedVars, secondlyCreatedVars, ::assertNotStrictlyEquals)
    }

    // TODO Work in Progress!!

    // TODO anonymous test and is Well formed (move regex)

    @Test
    fun testIsPropertiesAndTypes() {
        mixedVarInstances.forEach(TermTypeAssertionUtils::assertIsVar)
    }
}
