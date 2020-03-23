package it.unibo.tuprolog.solve.libraries

import it.unibo.tuprolog.solve.libraries.exception.AlreadyLoadedLibraryException
import it.unibo.tuprolog.solve.libraries.testutils.LibraryUtils
import it.unibo.tuprolog.solve.libraries.testutils.LibraryUtils.aliasLibraryMap
import it.unibo.tuprolog.solve.libraries.testutils.LibraryUtils.aliasPrimitiveOrFunction
import it.unibo.tuprolog.solve.libraries.testutils.LibraryUtils.libraryWithAliasConstructor
import it.unibo.tuprolog.solve.libraries.testutils.LibraryUtils.makeLib
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
    private val librariesInstance = Libraries(differentAliasInstances)

    @Test
    fun iterableConstructor() {
        assertEquals(differentAliasInstances.toSet(), Libraries(differentAliasInstances).libraries.toSet())
    }

    @Test
    fun sequenceConstructor() {
        assertEquals(differentAliasInstances.toSet(), Libraries(differentAliasInstances.asSequence()).libraries.toSet())
    }

    @Test
    fun varargConstructor() {
        assertEquals(
            differentAliasInstances.toSet(),
            Libraries(*differentAliasInstances.toTypedArray()).libraries.toSet()
        )
    }

    @Test
    fun constructorsOverrideDuplicatedAliasesLibrariesWithLastInOrder() {
        assertEquals((allLibInstances - library).toSet(), Libraries(allLibInstances).libraries.toSet())
        assertEquals((allLibInstances - library).toSet(), Libraries(*allLibInstances.toTypedArray()).libraries.toSet())
        assertEquals((allLibInstances - library).toSet(), Libraries(allLibInstances.asSequence()).libraries.toSet())
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
        val toBeTested = allLibInstances.map { Libraries(it).operators }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun operatorsShouldReturnOverriddenOnesByLastlyAddedLibrary() {
        assertEquals(library.operators, Libraries(library, emptyLibrary).operators)
        assertEquals(overriddenLibrary.operators, Libraries(library, overridingLibrary).operators)
    }

    @Test
    fun theoryCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { it.theory }
        val toBeTested = allLibInstances.map { Libraries(it).theory }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun theoryShouldReturnAllClausesComposingAllProvidedTheories() {
        assertEquals(library.theory, Libraries(library, emptyLibrary).theory)
        assertEquals(overriddenLibrary.theory, Libraries(library, overridingLibrary).theory)
    }

    @Test
    fun primitivesCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { aliasLibraryMap(it.alias, it.primitives) }
        val toBeTested = allLibInstances.map { Libraries(it).primitives }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun primitivesShouldReturnNonAliasedOverriddenPrimitiveFromLastlyAddedLibrary() {
        val toBeTested = Libraries(library, overridingLibrary)

        assertEquals(
            overriddenLibrary.primitives,
            toBeTested.primitives.filterKeys { !it.name.contains(AliasedLibrary.ALIAS_SEPARATOR) }
        )
    }

    @Test
    fun primitivesShouldReturnAllAliasedPrimitivesEvenAfterOverriding() {
        val correct = aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.primitives) +
                library.primitives.map { aliasPrimitiveOrFunction(library.alias, it) }
        val toBeTested = Libraries(library, overridingLibrary)

        assertEquals(correct, toBeTested.primitives)

        assertEquals(aliasLibraryMap(library.alias, library.primitives), Libraries(library, emptyLibrary).primitives)
    }

    @Test
    fun functionsCorrectWithSingleLibrary() {
        val correct = allLibInstances.map { aliasLibraryMap(it.alias, it.functions) }
        val toBeTested = allLibInstances.map { Libraries(it).functions }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun functionsShouldReturnNonAliasedOverriddenPrimitiveFromLastlyAddedLibrary() {
        val toBeTested = Libraries(library, overridingLibrary)

        assertEquals(
            overriddenLibrary.functions,
            toBeTested.functions.filterKeys { !it.name.contains(AliasedLibrary.ALIAS_SEPARATOR) }
        )
    }

    @Test
    fun functionsShouldReturnAllAliasedPrimitivesEvenAfterOverriding() {
        val correct = aliasLibraryMap(overridingLibrary.alias, overriddenLibrary.functions) +
                library.functions.map { aliasPrimitiveOrFunction(library.alias, it) }
        val toBeTested = Libraries(library, overridingLibrary)

        assertEquals(correct, toBeTested.functions)

        assertEquals(aliasLibraryMap(library.alias, library.functions), Libraries(library, emptyLibrary).functions)
    }

    @Test
    fun plusLibraryShouldAddANonPresentLibrary() {
        val toBeTested = Libraries(library) + overridingLibrary

        assertEquals(Libraries(library, overridingLibrary), toBeTested)
    }

    @Test
    fun plusLibraryWithAlreadyPresentAliasLibraryComplains() {
        assertFailsWith<AlreadyLoadedLibraryException> { Libraries(library) + duplicatedAliasLibrary }
    }

    @Test
    fun plusLibraryGroupShouldAddNonPresentLibrary() {
        val instances = differentAliasInstances
        val instancesCount = instances.count()

        assertEquals(
            Libraries(instances),
            Libraries(instances.take(instancesCount / 2)) + Libraries(instances.drop(instancesCount / 2))
        )
    }

    @Test
    fun plusLibraryGroupWithAlreadyPresentAliasLibraryComplains() {
        assertFailsWith<AlreadyLoadedLibraryException> { Libraries(library) + Libraries(duplicatedAliasLibrary) }
    }

    @Test
    fun updateShouldSubstituteAlreadyPresentAliasedLibrary() {
        val toBeTested = Libraries(library).update(duplicatedAliasLibrary)

        assertNotEquals(Libraries(library), toBeTested)
        assertEquals(Libraries(duplicatedAliasLibrary), toBeTested)
    }

    @Test
    fun updateShouldComplainIfNoLibraryLoadedWithSomeAlias() {
        assertFailsWith<IllegalArgumentException> { Libraries(library).update(emptyLibrary) }
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(Libraries(differentAliasInstances), Libraries(differentAliasInstances))
    }
}
