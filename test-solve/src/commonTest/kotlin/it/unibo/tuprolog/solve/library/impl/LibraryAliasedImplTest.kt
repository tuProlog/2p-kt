package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [LibraryAliasedImpl] and [AliasedLibrary]
 *
 * @author Enrico
 */
internal class LibraryAliasedImplTest {

    private val libraryAliasedInstances = LibraryUtils.allLibraries.map { makeLib(it, ::LibraryAliasedImpl) }

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
                LibraryAliasedImpl(opSet, theory, primitives, functions, alias),
                LibraryAliasedImpl(opSet, theory, primitives, functions, alias + "x")
            )
        }
    }

}
