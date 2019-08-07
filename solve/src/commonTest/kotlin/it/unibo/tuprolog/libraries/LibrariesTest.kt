package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.testutils.LibraryUtils
import it.unibo.tuprolog.libraries.testutils.LibraryUtils.aliasDuplicatingPrimitives
import it.unibo.tuprolog.libraries.testutils.LibraryUtils.aliasPrimitive
import it.unibo.tuprolog.libraries.testutils.LibraryUtils.makeLib
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase
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

    /** A method to disambiguate use of Library.of reference */
    private fun myLibConstructor(opSet: OperatorSet, theory: ClauseDatabase, primitives: Map<Signature, Primitive>, alias: String): LibraryAliased =
            Library.of(opSet, theory, primitives, alias)

    private val emptyLibrary = makeLib(LibraryUtils.emptyLibrary, ::myLibConstructor)
    private val library = makeLib(LibraryUtils.library, ::myLibConstructor)
    private val overridingLibrary = makeLib(LibraryUtils.overridingLibrary, ::myLibConstructor)
    private val overriddenLibrary = makeLib(LibraryUtils.overriddenLibrary, ::myLibConstructor)
    private val duplicatedAliasLibrary = makeLib(LibraryUtils.duplicatedAliasLibrary, ::myLibConstructor)

    private val differentAliasInstances = listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary)
    private val allLibInstances = listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary, duplicatedAliasLibrary)

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
        assertEquals(differentAliasInstances.toSet(), Libraries(*differentAliasInstances.toTypedArray()).libraries.toSet())
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
        val correct = allLibInstances.map { aliasDuplicatingPrimitives(it) }
        val toBeTested = allLibInstances.map { Libraries(it).primitives }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun primitivesShouldReturnNonAliasedOverriddenPrimitiveFromLastlyAddedLibrary() {
        val toBeTested = Libraries(library, overridingLibrary)

        assertEquals(overriddenLibrary.primitives, toBeTested.primitives.filterKeys { !it.name.contains(LibraryAliased.ALIAS_SEPARATOR) })
    }

    @Test
    fun primitivesShouldReturnAllAliasedPrimitivesEvenAfterOverriding() {
        val correct = aliasDuplicatingPrimitives(overriddenLibrary, overridingLibrary.alias) +
                library.primitives.map { aliasPrimitive(library.alias, it) }
        val toBeTested = Libraries(library, overridingLibrary)

        assertEquals(correct, toBeTested.primitives)

        assertEquals(aliasDuplicatingPrimitives(library), Libraries(library, emptyLibrary).primitives)
    }

    @Test
    fun plusShouldAddANonPresentLibrary() {
        val toBeTested = Libraries(library) + overridingLibrary

        assertEquals(Libraries(library, overridingLibrary), toBeTested)
    }

    @Test
    fun plusAlreadyPresentAliasLibraryComplains() {
        assertFailsWith<AlreadyLoadedLibraryException> { Libraries(library) + duplicatedAliasLibrary }
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
