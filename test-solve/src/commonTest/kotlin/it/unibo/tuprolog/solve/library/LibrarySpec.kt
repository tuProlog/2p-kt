package it.unibo.tuprolog.solve.library

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithoutAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Library.Companion]
 *
 * @author Enrico
 */
class LibrarySpec :
    FunSpec({
        val correctInstances = LibraryUtils.allLibraries.map { makeLib(it, ::libraryWithoutAliasConstructor) }

        val correctInstancesWithAlias = LibraryUtils.allLibraries.map { makeLib(it, ::libraryWithAliasConstructor) }

        test("of-creates-correct-instance") {
            val toBeTested =
                LibraryUtils.allLibraries.map { (_, opSet, theory, primitives, functions) ->
                    Library.of(primitives, theory, opSet, functions)
                }

            assertEquals(correctInstances, toBeTested)
        }

        test("of-with-omitted-parameters-creates-empty-library") {
            val toBeTested = Library.of()

            assertTrue { toBeTested.clauses.none() }
            assertTrue { toBeTested.operators.none() }
            assertTrue { toBeTested.primitives.none() }
        }

        test("of-with-alias-creates-correct-instance") {
            val toBeTested =
                LibraryUtils.allLibraries.map { (alias, opSet, theory, primitives, functions) ->
                    Library.of(alias, primitives, theory, opSet, functions)
                }

            assertEquals(correctInstancesWithAlias, toBeTested)
        }

        test("of-library-and-alias-creates-correct-instance") {
            val toBeTested =
                LibraryUtils.allLibraries.map { (alias, opSet, theory, primitives, functions) ->
                    Library.of(alias, libraryWithoutAliasConstructor(opSet, theory, primitives, functions))
                }

            assertEquals(correctInstancesWithAlias, toBeTested)
        }
    })
