package it.unibo.tuprolog.theory.rete.clause

import it.unibo.tuprolog.theory.testutils.ReteNodeUtils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ReteTree]
 *
 * @author Enrico
 */
internal class ReteTreeTest {

    private val reteTreeTestContents by lazy {
        listOf(
            ReteNodeUtils.rules,
            ReteNodeUtils.directives,
            ReteNodeUtils.mixedClauses
        )
    }

    private val correctInstances = reteTreeTestContents.map { clauses ->
        RootNode().apply { clauses.forEach { put(it) } }
    }

    @Test
    fun reteTreeOfIterableCreatesCorrectInstances() {
        val toBeTested = reteTreeTestContents.map { ReteTree.of(it) }

        assertEquals(correctInstances, toBeTested)
    }

    @Test
    fun reteTreeOfVarargsCreatesCorrectInstances() {
        val toBeTested = reteTreeTestContents.map { ReteTree.of(*it.toTypedArray()) }

        assertEquals(correctInstances, toBeTested)
    }
}
