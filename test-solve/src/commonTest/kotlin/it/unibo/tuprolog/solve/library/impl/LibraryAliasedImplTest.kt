package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for aliased libraries
 *
 * @author Enrico
 */
internal class LibraryAliasedImplTest {

    private val libraryAliasedInstances = LibraryUtils.allLibraries.map { makeLib(it, ::libraryWithAliasConstructor) }

    @Test
    fun aliasCorrect() {
        val correct = LibraryUtils.allLibraries.map { (alias) -> alias }
        val toBeTested = libraryAliasedInstances.map { it.alias }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun aliasNotConsideredInEqualityTesting() {
        LibraryUtils.allLibraries.map { (alias, opSet, theory, primitives, functions) ->
            assertEquals(
                libraryWithAliasConstructor(opSet, theory, primitives, functions, alias),
                libraryWithAliasConstructor(opSet, theory, primitives, functions, alias + "x")
            )
        }
    }
}
