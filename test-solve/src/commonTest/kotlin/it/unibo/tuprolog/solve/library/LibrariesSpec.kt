package it.unibo.tuprolog.solve.library

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.library.exception.AlreadyLoadedLibraryException
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.aliasLibraryMap
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.aliasPrimitiveOrFunction
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

/**
 * Test class for [Runtime] and [Runtime]
 *
 * @author Enrico
 */
class LibrariesSpec :
    FunSpec({
        val emptyLibrary = makeLib(LibraryUtils.emptyLibrary, ::libraryWithAliasConstructor)

        val library = makeLib(LibraryUtils.library, ::libraryWithAliasConstructor)

        val overridingLibrary = makeLib(LibraryUtils.overridingLibrary, ::libraryWithAliasConstructor)

        val overriddenLibrary = makeLib(LibraryUtils.overriddenLibrary, ::libraryWithAliasConstructor)

        val duplicatedAliasLibrary = makeLib(LibraryUtils.duplicatedAliasLibrary, ::libraryWithAliasConstructor)

        val differentAliasInstances = listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary)

        val allLibInstances =
            listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary, duplicatedAliasLibrary)

        val librariesInstance = Runtime.of(differentAliasInstances)

        test("iterable-constructor") {
            assertEquals(differentAliasInstances.toSet(), Runtime.of(differentAliasInstances).libraries.toSet())
        }

        test("sequence-constructor") {
            assertEquals(
                differentAliasInstances.toSet(),
                Runtime.of(differentAliasInstances.asSequence()).libraries.toSet(),
            )
        }

        test("vararg-constructor") {
            assertEquals(
                differentAliasInstances.toSet(),
                Runtime.of(*differentAliasInstances.toTypedArray()).libraries.toSet(),
            )
        }

        test("constructors-override-duplicated-aliases-libraries-with-last-in-order") {
            assertEquals((allLibInstances - library).toSet(), Runtime.of(allLibInstances).libraries.toSet())
            assertEquals(
                (allLibInstances - library).toSet(),
                Runtime.of(*allLibInstances.toTypedArray()).libraries.toSet(),
            )
            assertEquals(
                (allLibInstances - library).toSet(),
                Runtime.of(allLibInstances.asSequence()).libraries.toSet(),
            )
        }

        test("libraries-returns-all-currently-present-libraries") {
            assertEquals(differentAliasInstances.toSet(), librariesInstance.libraries.toSet())
        }

        test("library-aliases-correct") {
            assertEquals(differentAliasInstances.map { it.alias }.toSet(), librariesInstance.aliases)
        }

        withTests(nameFn = { "operators-correct-with-single-library/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            val instance = makeLib(case.raw, ::libraryWithAliasConstructor)
            assertEquals(instance.operators, Runtime.of(instance).operators)
        }

        test("operators-should-return-overridden-ones-by-lastly-added-library") {
            assertEquals(library.operators, Runtime.of(library, emptyLibrary).operators)
            assertEquals(overriddenLibrary.operators, Runtime.of(library, overridingLibrary).operators)
        }

        withTests(nameFn = { "theory-correct-with-single-library/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            val instance = makeLib(case.raw, ::libraryWithAliasConstructor)
            assertEquals(instance.clauses, Runtime.of(instance).clauses)
        }

        test("theory-should-return-all-clauses-composing-all-provided-theories") {
            assertEquals(library.clauses, Runtime.of(library, emptyLibrary).clauses)
            assertEquals(overriddenLibrary.clauses, Runtime.of(library, overridingLibrary).clauses)
        }

        withTests(
            nameFn = { "primitives-correct-with-single-library/${it.id}" },
            LibraryUtils.allLibraryCases,
        ) { case ->
            val instance = makeLib(case.raw, ::libraryWithAliasConstructor)
            assertEquals(aliasLibraryMap(instance.alias, instance.primitives), Runtime.of(instance).primitives)
        }

        test("primitives-should-return-non-aliased-overridden-primitive-from-lastly-added-library") {
            val toBeTested = Runtime.of(library, overridingLibrary)

            assertEquals(
                overriddenLibrary.primitives,
                toBeTested.primitives.filterKeys { !it.name.contains(Library.ALIAS_SEPARATOR) },
            )
        }

        test("primitives-should-return-all-aliased-primitives-even-after-overriding") {
            val correct =
                aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.primitives) +
                    library.primitives.map { aliasPrimitiveOrFunction(library.alias, it) }
            val toBeTested = Runtime.of(library, overridingLibrary)

            assertEquals(correct, toBeTested.primitives)

            assertEquals(
                aliasLibraryMap(library.alias, library.primitives),
                Runtime.of(library, emptyLibrary).primitives,
            )
        }

        withTests(nameFn = { "functions-correct-with-single-library/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            val instance = makeLib(case.raw, ::libraryWithAliasConstructor)
            assertEquals(aliasLibraryMap(instance.alias, instance.functions), Runtime.of(instance).functions)
        }

        test("functions-should-return-non-aliased-overridden-primitive-from-lastly-added-library") {
            val toBeTested = Runtime.of(library, overridingLibrary)

            assertEquals(
                overriddenLibrary.functions,
                toBeTested.functions.filterKeys { !it.name.contains(Library.ALIAS_SEPARATOR) },
            )
        }

        test("functions-should-return-all-aliased-primitives-even-after-overriding") {
            val correct =
                aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.functions) +
                    library.functions.map { aliasPrimitiveOrFunction(library.alias, it) }
            val toBeTested = Runtime.of(library, overridingLibrary)

            assertEquals(correct, toBeTested.functions)

            assertEquals(aliasLibraryMap(library.alias, library.functions), Runtime.of(library, emptyLibrary).functions)
        }

        test("plus-library-should-add-a-non-present-library") {
            val toBeTested = Runtime.of(library) + overridingLibrary

            assertEquals(Runtime.of(library, overridingLibrary), toBeTested)
        }

        test("plus-library-with-already-present-alias-library-complains") {
            assertFailsWith<AlreadyLoadedLibraryException> { Runtime.of(library) + duplicatedAliasLibrary }
        }

        test("plus-library-group-should-add-non-present-library") {
            val instances = differentAliasInstances
            val instancesCount = instances.count()

            assertEquals(
                Runtime.of(instances),
                Runtime.of(instances.take(instancesCount / 2)) + Runtime.of(instances.drop(instancesCount / 2)),
            )
        }

        test("plus-library-group-with-already-present-alias-library-complains") {
            assertFailsWith<AlreadyLoadedLibraryException> { Runtime.of(library) + Runtime.of(duplicatedAliasLibrary) }
        }

        test("update-should-substitute-already-present-aliased-library") {
            val toBeTested = Runtime.of(library).update(duplicatedAliasLibrary)

            assertNotEquals(Runtime.of(library), toBeTested)
            assertEquals(Runtime.of(duplicatedAliasLibrary), toBeTested)
        }

        test("update-should-complain-if-no-library-loaded-with-some-alias") {
            assertFailsWith<IllegalArgumentException> { Runtime.of(library).update(emptyLibrary) }
        }

        test("equals-works-as-expected") {
            assertEquals(Runtime.of(differentAliasInstances), Runtime.of(differentAliasInstances))
        }
    })
