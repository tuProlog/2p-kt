package it.unibo.tuprolog.solve.library

import io.kotest.core.spec.style.FunSpec
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

        test("operators-correct-with-single-library") {
            val correct = allLibInstances.map { it.operators }
            val toBeTested = allLibInstances.map { Runtime.of(it).operators }

            correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }

        test("operators-should-return-overridden-ones-by-lastly-added-library") {
            assertEquals(library.operators, Runtime.of(library, emptyLibrary).operators)
            assertEquals(overriddenLibrary.operators, Runtime.of(library, overridingLibrary).operators)
        }

        test("theory-correct-with-single-library") {
            val correct = allLibInstances.map { it.clauses }
            val toBeTested = allLibInstances.map { Runtime.of(it).clauses }

            correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }

        test("theory-should-return-all-clauses-composing-all-provided-theories") {
            assertEquals(library.clauses, Runtime.of(library, emptyLibrary).clauses)
            assertEquals(overriddenLibrary.clauses, Runtime.of(library, overridingLibrary).clauses)
        }

        test("primitives-correct-with-single-library") {
            val correct = allLibInstances.map { aliasLibraryMap(it.alias, it.primitives) }
            val toBeTested = allLibInstances.map { Runtime.of(it).primitives }

            correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
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

        test("functions-correct-with-single-library") {
            val correct = allLibInstances.map { aliasLibraryMap(it.alias, it.functions) }
            val toBeTested = allLibInstances.map { Runtime.of(it).functions }

            correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
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
