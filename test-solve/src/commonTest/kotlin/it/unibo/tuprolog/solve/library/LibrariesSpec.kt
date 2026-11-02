package it.unibo.tuprolog.solve.library

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.solve.library.exception.AlreadyLoadedLibraryException
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.aliasLibraryMap
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.aliasPrimitiveOrFunction
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib

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
            Runtime.of(differentAliasInstances).libraries.toSet() shouldBe differentAliasInstances.toSet()
        }

        test("sequence-constructor") {
            Runtime.of(differentAliasInstances.asSequence()).libraries.toSet() shouldBe differentAliasInstances.toSet()
        }

        test("vararg-constructor") {
            Runtime.of(*differentAliasInstances.toTypedArray()).libraries.toSet() shouldBe
                differentAliasInstances.toSet()
        }

        test("constructors-override-duplicated-aliases-libraries-with-last-in-order") {
            Runtime.of(allLibInstances).libraries.toSet() shouldBe (allLibInstances - library).toSet()
            Runtime.of(*allLibInstances.toTypedArray()).libraries.toSet() shouldBe (allLibInstances - library).toSet()
            Runtime.of(allLibInstances.asSequence()).libraries.toSet() shouldBe (allLibInstances - library).toSet()
        }

        test("libraries-returns-all-currently-present-libraries") {
            librariesInstance.libraries.toSet() shouldBe differentAliasInstances.toSet()
        }

        test("library-aliases-correct") {
            librariesInstance.aliases shouldBe (differentAliasInstances.map { it.alias }.toSet())
        }

        withTests(nameFn = { "operators-correct-with-single-library/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            val instance = makeLib(case.raw, ::libraryWithAliasConstructor)
            Runtime.of(instance).operators shouldBe instance.operators
        }

        test("operators-should-return-overridden-ones-by-lastly-added-library") {
            Runtime.of(library, emptyLibrary).operators shouldBe library.operators
            Runtime.of(library, overridingLibrary).operators shouldBe overriddenLibrary.operators
        }

        withTests(nameFn = { "theory-correct-with-single-library/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            val instance = makeLib(case.raw, ::libraryWithAliasConstructor)
            Runtime.of(instance).clauses shouldBe instance.clauses
        }

        test("theory-should-return-all-clauses-composing-all-provided-theories") {
            Runtime.of(library, emptyLibrary).clauses shouldBe library.clauses
            Runtime.of(library, overridingLibrary).clauses shouldBe overriddenLibrary.clauses
        }

        withTests(
            nameFn = { "primitives-correct-with-single-library/${it.id}" },
            LibraryUtils.allLibraryCases,
        ) { case ->
            val instance = makeLib(case.raw, ::libraryWithAliasConstructor)
            Runtime.of(instance).primitives shouldBe aliasLibraryMap(instance.alias, instance.primitives)
        }

        test("primitives-should-return-non-aliased-overridden-primitive-from-lastly-added-library") {
            val toBeTested = Runtime.of(library, overridingLibrary)

            (toBeTested.primitives.filterKeys { !it.name.contains(Library.ALIAS_SEPARATOR) }) shouldBe
                overriddenLibrary.primitives
        }

        test("primitives-should-return-all-aliased-primitives-even-after-overriding") {
            val correct =
                aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.primitives) +
                    library.primitives.map { aliasPrimitiveOrFunction(library.alias, it) }
            val toBeTested = Runtime.of(library, overridingLibrary)

            toBeTested.primitives shouldBe correct

            Runtime.of(library, emptyLibrary).primitives shouldBe aliasLibraryMap(library.alias, library.primitives)
        }

        withTests(nameFn = { "functions-correct-with-single-library/${it.id}" }, LibraryUtils.allLibraryCases) { case ->
            val instance = makeLib(case.raw, ::libraryWithAliasConstructor)
            Runtime.of(instance).functions shouldBe aliasLibraryMap(instance.alias, instance.functions)
        }

        test("functions-should-return-non-aliased-overridden-primitive-from-lastly-added-library") {
            val toBeTested = Runtime.of(library, overridingLibrary)

            (toBeTested.functions.filterKeys { !it.name.contains(Library.ALIAS_SEPARATOR) }) shouldBe
                overriddenLibrary.functions
        }

        test("functions-should-return-all-aliased-primitives-even-after-overriding") {
            val correct =
                aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.functions) +
                    library.functions.map { aliasPrimitiveOrFunction(library.alias, it) }
            val toBeTested = Runtime.of(library, overridingLibrary)

            toBeTested.functions shouldBe correct

            Runtime.of(library, emptyLibrary).functions shouldBe aliasLibraryMap(library.alias, library.functions)
        }

        test("plus-library-should-add-a-non-present-library") {
            val toBeTested = Runtime.of(library) + overridingLibrary

            toBeTested shouldBe Runtime.of(library, overridingLibrary)
        }

        test("plus-library-with-already-present-alias-library-complains") {
            shouldThrow<AlreadyLoadedLibraryException> { Runtime.of(library) + duplicatedAliasLibrary }
        }

        test("plus-library-group-should-add-non-present-library") {
            val instances = differentAliasInstances
            val instancesCount = instances.count()

            (Runtime.of(instances.take(instancesCount / 2)) + Runtime.of(instances.drop(instancesCount / 2))) shouldBe
                Runtime.of(instances)
        }

        test("plus-library-group-with-already-present-alias-library-complains") {
            shouldThrow<AlreadyLoadedLibraryException> { Runtime.of(library) + Runtime.of(duplicatedAliasLibrary) }
        }

        test("update-should-substitute-already-present-aliased-library") {
            val toBeTested = Runtime.of(library).update(duplicatedAliasLibrary)

            toBeTested shouldNotBe Runtime.of(library)
            toBeTested shouldBe Runtime.of(duplicatedAliasLibrary)
        }

        test("update-should-complain-if-no-library-loaded-with-some-alias") {
            shouldThrow<IllegalArgumentException> { Runtime.of(library).update(emptyLibrary) }
        }

        test("equals-works-as-expected") {
            Runtime.of(differentAliasInstances) shouldBe Runtime.of(differentAliasInstances)
        }
    })
