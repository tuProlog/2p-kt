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
 * Test class for [Libraries] and [LibraryGroup]
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
    private val librariesInstance = Libraries.of(differentAliasInstances)

    @Test
    fun iterableConstructor() {
        assertEquals(differentAliasInstances.toSet(), Libraries.of(differentAliasInstances).libraries.toSet())
    }

    @Test
    fun sequenceConstructor() {
        assertEquals(differentAliasInstances.toSet(), Libraries.of(differentAliasInstances.asSequence()).libraries.toSet())
    }

    @Test
    fun varargConstructor() {
        assertEquals(
            differentAliasInstances.toSet(),
            Libraries.of(*differentAliasInstances.toTypedArray()).libraries.toSet()
        )
    }

    @Test
    fun constructorsOverrideDuplicatedAliasesLibrariesWithLastInOrder() {
        assertEquals((allLibInstances - library).toSet(), Libraries.of(allLibInstances).libraries.toSet())
        assertEquals((allLibInstances - library).toSet(), Libraries.of(*allLibInstances.toTypedArray()).libraries.toSet())
        assertEquals((allLibInstances - library).toSet(), Libraries.of(allLibInstances.asSequence()).libraries.toSet())
    }

    @Test
    fun librariesReturnsAllCurrentlyPresentLibraries() {
        assertEquals(differentAliasInstances.toSet(), librariesInstance.libraries.toSet())
    }

    @Test
    fun libraryAliasesCorrect() {
        assertEquals(differentAliasInstances.map { it.alias }.toSet(), librariesInstance.libraryAliases)
    }

    @Test
    fun operatorsCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { it.operators }
        val toBeTested = allLibInstances.map { Libraries.of(it).operators }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun operatorsShouldReturnOverriddenOnesByLastlyAddedLibrary() {
        assertEquals(library.operators, Libraries.of(library, emptyLibrary).operators)
        assertEquals(overriddenLibrary.operators, Libraries.of(library, overridingLibrary).operators)
    }

    @Test
    fun theoryCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { it.theory }
        val toBeTested = allLibInstances.map { Libraries.of(it).theory }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun theoryShouldReturnAllClausesComposingAllProvidedTheories() {
        assertEquals(library.theory, Libraries.of(library, emptyLibrary).theory)
        assertEquals(overriddenLibrary.theory, Libraries.of(library, overridingLibrary).theory)
    }

    @Test
    fun primitivesCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { aliasLibraryMap(it.alias, it.primitives) }
        val toBeTested = allLibInstances.map { Libraries.of(it).primitives }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun primitivesShouldReturnNonAliasedOverriddenPrimitiveFromLastlyAddedLibrary() {
        val toBeTested = Libraries.of(library, overridingLibrary)

        assertEquals(
            overriddenLibrary.primitives,
            toBeTested.primitives.filterKeys { !it.name.contains(AliasedLibrary.ALIAS_SEPARATOR) }
        )
    }

    @Test
    fun primitivesShouldReturnAllAliasedPrimitivesEvenAfterOverriding() {
        val correct = aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.primitives) +
            library.primitives.map { aliasPrimitiveOrFunction(library.alias, it) }
        val toBeTested = Libraries.of(library, overridingLibrary)

        assertEquals(correct, toBeTested.primitives)

        assertEquals(aliasLibraryMap(library.alias, library.primitives), Libraries.of(library, emptyLibrary).primitives)
    }

    @Test
    fun functionsCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { aliasLibraryMap(it.alias, it.functions) }
        val toBeTested = allLibInstances.map { Libraries.of(it).functions }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun functionsShouldReturnNonAliasedOverriddenPrimitiveFromLastlyAddedLibrary() {
        val toBeTested = Libraries.of(library, overridingLibrary)

        assertEquals(
            overriddenLibrary.functions,
            toBeTested.functions.filterKeys { !it.name.contains(AliasedLibrary.ALIAS_SEPARATOR) }
        )
    }

    @Test
    fun functionsShouldReturnAllAliasedPrimitivesEvenAfterOverriding() {
        val correct = aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.functions) +
            library.functions.map { aliasPrimitiveOrFunction(library.alias, it) }
        val toBeTested = Libraries.of(library, overridingLibrary)

        assertEquals(correct, toBeTested.functions)

        assertEquals(aliasLibraryMap(library.alias, library.functions), Libraries.of(library, emptyLibrary).functions)
    }

    @Test
    fun plusLibraryShouldAddANonPresentLibrary() {
        val toBeTested = Libraries.of(library) + overridingLibrary

        assertEquals(Libraries.of(library, overridingLibrary), toBeTested)
    }

    @Test
    fun plusLibraryWithAlreadyPresentAliasLibraryComplains() {
        assertFailsWith<AlreadyLoadedLibraryException> { Libraries.of(library) + duplicatedAliasLibrary }
    }

    @Test
    fun plusLibraryGroupShouldAddNonPresentLibrary() {
        val instances = differentAliasInstances
        val instancesCount = instances.count()

        assertEquals(
            Libraries.of(instances),
            Libraries.of(instances.take(instancesCount / 2)) + Libraries.of(instances.drop(instancesCount / 2))
        )
    }

    @Test
    fun plusLibraryGroupWithAlreadyPresentAliasLibraryComplains() {
        assertFailsWith<AlreadyLoadedLibraryException> { Libraries.of(library) + Libraries.of(duplicatedAliasLibrary) }
    }

    @Test
    fun updateShouldSubstituteAlreadyPresentAliasedLibrary() {
        val toBeTested = Libraries.of(library).update(duplicatedAliasLibrary)

        assertNotEquals(Libraries.of(library), toBeTested)
        assertEquals(Libraries.of(duplicatedAliasLibrary), toBeTested)
    }

    @Test
    fun updateShouldComplainIfNoLibraryLoadedWithSomeAlias() {
        assertFailsWith<IllegalArgumentException> { Libraries.of(library).update(emptyLibrary) }
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(Libraries.of(differentAliasInstances), Libraries.of(differentAliasInstances))
    }
}
