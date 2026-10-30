package it.unibo.tuprolog.solve.library.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Test class for aliased libraries
 *
 * @author Enrico
 */
class LibraryAliasedSpec :
    FunSpec({
        val libraryAliasedInstances = LibraryUtils.allLibraries.map { makeLib(it, ::libraryWithAliasConstructor) }

        test("alias-correct") {
            val correct = LibraryUtils.allLibraries.map { (alias) -> alias }
            val toBeTested = libraryAliasedInstances.map { it.alias }

            correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }

        test("alias-considered-in-equality-testing") {
            LibraryUtils.allLibraries.map { (alias, opSet, theory, primitives, functions) ->
                assertNotEquals(
                    libraryWithAliasConstructor(opSet, theory, primitives, functions, alias),
                    libraryWithAliasConstructor(opSet, theory, primitives, functions, alias + "x"),
                )
            }
        }
    })
