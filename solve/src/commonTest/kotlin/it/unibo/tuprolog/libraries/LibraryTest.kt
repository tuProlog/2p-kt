package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.libraries.impl.LibraryImpl
import it.unibo.tuprolog.libraries.testutils.LibraryUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Library.Companion]
 *
 * @author Enrico
 */
internal class LibraryTest {

    private val correctInstances = LibraryUtils.allLibraries.map { (opSet, theory, primitives) -> LibraryImpl(opSet, theory, primitives) }

    @Test
    fun ofCreatesCorrectInstance() {
        val toBeTested = LibraryUtils.allLibraries.map { (opSet, theory, primitives) -> Library.of(opSet, theory, primitives) }

        assertEquals(correctInstances, toBeTested)
    }

    @Test
    fun ofWithOmittedParametersCreatesEmptyLibrary() {
        val toBeTested = Library.of()

        assertTrue { toBeTested.theory.none() }
        assertTrue { toBeTested.operators.none() }
        assertTrue { toBeTested.primitives.none() }
    }

}
