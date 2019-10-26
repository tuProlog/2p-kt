package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.libraries.impl.LibraryAliasedImpl
import it.unibo.tuprolog.libraries.impl.LibraryImpl
import it.unibo.tuprolog.libraries.testutils.LibraryUtils
import it.unibo.tuprolog.libraries.testutils.LibraryUtils.makeLib
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Library.Companion]
 *
 * @author Enrico
 */
internal class LibraryTest {

    private val correctInstances = LibraryUtils.allLibraries.map { makeLib(it, ::LibraryImpl) }
    private val correctInstancesWithAlias = LibraryUtils.allLibraries.map { makeLib(it, ::LibraryAliasedImpl) }

    @Test
    fun ofCreatesCorrectInstance() {
        val toBeTested = LibraryUtils.allLibraries.map { (_, opSet, theory, primitives, functions) ->
            Library.of(opSet, theory, primitives, functions)
        }

        assertEquals(correctInstances, toBeTested)
    }

    @Test
    fun ofWithOmittedParametersCreatesEmptyLibrary() {
        val toBeTested = Library.of()

        assertTrue { toBeTested.theory.none() }
        assertTrue { toBeTested.operators.none() }
        assertTrue { toBeTested.primitives.none() }
    }

    @Test
    fun ofWithAliasCreatesCorrectInstance() {
        val toBeTested = LibraryUtils.allLibraries.map { (alias, opSet, theory, primitives, functions) ->
            Library.of(opSet, theory, primitives, functions, alias)
        }

        assertEquals(correctInstancesWithAlias, toBeTested)
    }

    @Test
    fun ofLibraryAndAliasCreatesCorrectInstance() {
        val toBeTested = LibraryUtils.allLibraries.map { (alias, opSet, theory, primitives, functions) ->
            Library.of(LibraryImpl(opSet, theory, primitives, functions), alias)
        }

        assertEquals(correctInstancesWithAlias, toBeTested)
    }
}
