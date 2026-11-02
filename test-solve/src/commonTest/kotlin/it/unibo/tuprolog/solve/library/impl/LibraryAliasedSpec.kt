package it.unibo.tuprolog.solve.library.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib

/**
 * Test class for aliased libraries
 *
 * @author Enrico
 */
class LibraryAliasedSpec :
    FunSpec({
        withTests(nameFn = { "alias-correct/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            makeLib(case.raw, ::libraryWithAliasConstructor).alias shouldBe case.raw.name
        }

        withTests(nameFn = { "alias-considered-in-equality-testing/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            val (alias, opSet, theory, primitives, functions) = case.raw
            libraryWithAliasConstructor(opSet, theory, primitives, functions, alias + "x") shouldNotBe
                libraryWithAliasConstructor(opSet, theory, primitives, functions, alias)
        }
    })
