package it.unibo.tuprolog.theory

import it.unibo.tuprolog.theory.testutils.ReteTreeUtils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ReteTree.Companion]
 *
 * @author Enrico
 */
internal class ReteTreeTest {

    private val reteTreeTestContents by lazy {
        listOf(
                ReteTreeUtils.rules,
                ReteTreeUtils.directives,
                ReteTreeUtils.mixedClauses
        )
    }

    private val correctInstances = reteTreeTestContents.map { clauses ->
        ReteTree.RootNode().apply { clauses.forEach { put(it) } }
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
