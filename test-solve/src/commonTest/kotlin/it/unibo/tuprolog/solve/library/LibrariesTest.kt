package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.solve.library.exception.AlreadyLoadedLibraryException
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.aliasLibraryMap
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.aliasPrimitiveOrFunction
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

/**
 * Test class for [Runtime] and [Runtime]
 *
 * @author Enrico
 */
internal class LibrariesTest {

    private val emptyLibrary = makeLib(LibraryUtils.emptyLibrary, ::libraryWithAliasConstructor)
    private val library = makeLib(LibraryUtils.library, ::libraryWithAliasConstructor)
    private val overridingLibrary = makeLib(LibraryUtils.overridingLibrary, ::libraryWithAliasConstructor)
    private val overriddenLibrary = makeLib(LibraryUtils.overriddenLibrary, ::libraryWithAliasConstructor)
    private val duplicatedAliasLibrary = makeLib(LibraryUtils.duplicatedAliasLibrary, ::libraryWithAliasConstructor)

    private val differentAliasInstances = listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary)
    private val allLibInstances =
        listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary, duplicatedAliasLibrary)

    /** A test instance with [differentAliasInstances] */
    private val librariesInstance = Runtime.of(differentAliasInstances)

    @Test
    fun iterableConstructor() {
        assertEquals(differentAliasInstances.toSet(), Runtime.of(differentAliasInstances).libraries.toSet())
    }

    @Test
    fun sequenceConstructor() {
        assertEquals(differentAliasInstances.toSet(), Runtime.of(differentAliasInstances.asSequence()).libraries.toSet())
    }

    @Test
    fun varargConstructor() {
        assertEquals(
            differentAliasInstances.toSet(),
            Runtime.of(*differentAliasInstances.toTypedArray()).libraries.toSet()
        )
    }

    @Test
    fun constructorsOverrideDuplicatedAliasesLibrariesWithLastInOrder() {
        assertEquals((allLibInstances - library).toSet(), Runtime.of(allLibInstances).libraries.toSet())
        assertEquals((allLibInstances - library).toSet(), Runtime.of(*allLibInstances.toTypedArray()).libraries.toSet())
        assertEquals((allLibInstances - library).toSet(), Runtime.of(allLibInstances.asSequence()).libraries.toSet())
    }

    @Test
    fun librariesReturnsAllCurrentlyPresentLibraries() {
        assertEquals(differentAliasInstances.toSet(), librariesInstance.libraries.toSet())
    }

    @Test
    fun libraryAliasesCorrect() {
        assertEquals(differentAliasInstances.map { it.alias }.toSet(), librariesInstance.aliases)
    }

    @Test
    fun operatorsCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { it.operators }
        val toBeTested = allLibInstances.map { Runtime.of(it).operators }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun operatorsShouldReturnOverriddenOnesByLastlyAddedLibrary() {
        assertEquals(library.operators, Runtime.of(library, emptyLibrary).operators)
        assertEquals(overriddenLibrary.operators, Runtime.of(library, overridingLibrary).operators)
    }

    @Test
    fun theoryCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { it.clauses }
        val toBeTested = allLibInstances.map { Runtime.of(it).clauses }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun theoryShouldReturnAllClausesComposingAllProvidedTheories() {
        assertEquals(library.clauses, Runtime.of(library, emptyLibrary).clauses)
        assertEquals(overriddenLibrary.clauses, Runtime.of(library, overridingLibrary).clauses)
    }

    @Test
    fun primitivesCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { aliasLibraryMap(it.alias, it.primitives) }
        val toBeTested = allLibInstances.map { Runtime.of(it).primitives }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun primitivesShouldReturnNonAliasedOverriddenPrimitiveFromLastlyAddedLibrary() {
        val toBeTested = Runtime.of(library, overridingLibrary)

        assertEquals(
            overriddenLibrary.primitives,
            toBeTested.primitives.filterKeys { !it.name.contains(Library.ALIAS_SEPARATOR) }
        )
    }

    @Test
    fun primitivesShouldReturnAllAliasedPrimitivesEvenAfterOverriding() {
        val correct = aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.primitives) +
            library.primitives.map { aliasPrimitiveOrFunction(library.alias, it) }
        val toBeTested = Runtime.of(library, overridingLibrary)

        assertEquals(correct, toBeTested.primitives)

        assertEquals(aliasLibraryMap(library.alias, library.primitives), Runtime.of(library, emptyLibrary).primitives)
    }

    @Test
    fun functionsCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { aliasLibraryMap(it.alias, it.functions) }
        val toBeTested = allLibInstances.map { Runtime.of(it).functions }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun functionsShouldReturnNonAliasedOverriddenPrimitiveFromLastlyAddedLibrary() {
        val toBeTested = Runtime.of(library, overridingLibrary)

        assertEquals(
            overriddenLibrary.functions,
            toBeTested.functions.filterKeys { !it.name.contains(Library.ALIAS_SEPARATOR) }
        )
    }

    @Test
    fun functionsShouldReturnAllAliasedPrimitivesEvenAfterOverriding() {
        val correct = aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.functions) +
            library.functions.map { aliasPrimitiveOrFunction(library.alias, it) }
        val toBeTested = Runtime.of(library, overridingLibrary)

        assertEquals(correct, toBeTested.functions)

        assertEquals(aliasLibraryMap(library.alias, library.functions), Runtime.of(library, emptyLibrary).functions)
    }

    @Test
    fun plusLibraryShouldAddANonPresentLibrary() {
        val toBeTested = Runtime.of(library) + overridingLibrary

        assertEquals(Runtime.of(library, overridingLibrary), toBeTested)
    }

    @Test
    fun plusLibraryWithAlreadyPresentAliasLibraryComplains() {
        assertFailsWith<AlreadyLoadedLibraryException> { Runtime.of(library) + duplicatedAliasLibrary }
    }

    @Test
    fun plusLibraryGroupShouldAddNonPresentLibrary() {
        val instances = differentAliasInstances
        val instancesCount = instances.count()

        assertEquals(
            Runtime.of(instances),
            Runtime.of(instances.take(instancesCount / 2)) + Runtime.of(instances.drop(instancesCount / 2))
        )
    }

    @Test
    fun plusLibraryGroupWithAlreadyPresentAliasLibraryComplains() {
        assertFailsWith<AlreadyLoadedLibraryException> { Runtime.of(library) + Runtime.of(duplicatedAliasLibrary) }
    }

    @Test
    fun updateShouldSubstituteAlreadyPresentAliasedLibrary() {
        val toBeTested = Runtime.of(library).update(duplicatedAliasLibrary)

        assertNotEquals(Runtime.of(library), toBeTested)
        assertEquals(Runtime.of(duplicatedAliasLibrary), toBeTested)
    }

    @Test
    fun updateShouldComplainIfNoLibraryLoadedWithSomeAlias() {
        assertFailsWith<IllegalArgumentException> { Runtime.of(library).update(emptyLibrary) }
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(Runtime.of(differentAliasInstances), Runtime.of(differentAliasInstances))
    }
}
