package it.unibo.tuprolog.solve.library

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithoutAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib

/**
 * Test class for [Library.Companion]
 *
 * @author Enrico
 */
class LibrarySpec :
    FunSpec({
        val cases = LibraryUtils.allLibraryCases

        withTests(nameFn = { "of/${it.id}" }, cases) { case ->
            val (_, opSet, theory, primitives, functions) = case.raw
            Library.of(primitives, theory, opSet, functions) shouldBe
                makeLib(case.raw, ::libraryWithoutAliasConstructor)
        }

        test("of-with-omitted-parameters-creates-empty-library") {
            val toBeTested = Library.of()

            toBeTested.clauses.none().shouldBeTrue()
            toBeTested.operators.none().shouldBeTrue()
            toBeTested.primitives.none().shouldBeTrue()
        }

        withTests(nameFn = { "of-with-alias/${it.id}" }, cases) { case ->
            val (alias, opSet, theory, primitives, functions) = case.raw
            Library.of(alias, primitives, theory, opSet, functions) shouldBe
                makeLib(case.raw, ::libraryWithAliasConstructor)
        }

        withTests(nameFn = { "of-library-and-alias/${it.id}" }, cases) { case ->
            val (alias, opSet, theory, primitives, functions) = case.raw
            Library.of(alias, libraryWithoutAliasConstructor(opSet, theory, primitives, functions)) shouldBe
                makeLib(case.raw, ::libraryWithAliasConstructor)
        }
    })
