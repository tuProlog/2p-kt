package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithoutAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Library.Companion]
 *
 * @author Enrico
 */
internal class LibraryTest {

    private val correctInstances = LibraryUtils.allLibraries.map { makeLib(it, ::libraryWithoutAliasConstructor) }
    private val correctInstancesWithAlias = LibraryUtils.allLibraries.map { makeLib(it, ::libraryWithAliasConstructor) }

    @Test
    fun ofCreatesCorrectInstance() {
        val toBeTested = LibraryUtils.allLibraries.map { (_, opSet, theory, primitives, functions) ->
            Library.of(primitives, theory, opSet, functions)
        }

        assertEquals(correctInstances, toBeTested)
    }

    @Test
    fun ofWithOmittedParametersCreatesEmptyLibrary() {
        val toBeTested = Library.of()

        assertTrue { toBeTested.clauses.none() }
        assertTrue { toBeTested.operators.none() }
        assertTrue { toBeTested.primitives.none() }
    }

    @Test
    fun ofWithAliasCreatesCorrectInstance() {
        val toBeTested = LibraryUtils.allLibraries.map { (alias, opSet, theory, primitives, functions) ->
            Library.of(alias, primitives, theory, opSet, functions)
        }

        assertEquals(correctInstancesWithAlias, toBeTested)
    }

    @Test
    fun ofLibraryAndAliasCreatesCorrectInstance() {
        val toBeTested = LibraryUtils.allLibraries.map { (alias, opSet, theory, primitives, functions) ->
            Library.of(alias, libraryWithoutAliasConstructor(opSet, theory, primitives, functions))
        }

        assertEquals(correctInstancesWithAlias, toBeTested)
    }
}
