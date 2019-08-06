package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.testutils.LibraryUtils
import it.unibo.tuprolog.libraries.testutils.LibraryUtils.aliasPrimitive
import it.unibo.tuprolog.libraries.testutils.LibraryUtils.makeLib
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals

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

    private val singleLibInstances = listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary)

    @Test
    fun operatorsCorrectWithSingleLibrary() {
        val correct = singleLibInstances.map { it.operators }
        val toBeTested = singleLibInstances.map { Libraries(it).operators }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun theoryCorrectWithSingleLibrary() {
        val correct = singleLibInstances.map { it.theory }
        val toBeTested = singleLibInstances.map { Libraries(it).theory }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun primitivesCorrectWithSingleLibrary() {
        val correct = singleLibInstances.map { lib -> lib.primitives.flatMap { aliasPrimitive(lib.alias, it) }.toMap() }
        val toBeTested = singleLibInstances.map { Libraries(it).primitives }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun libraryAliasesCorrect() {
        // TODO: 06/08/2019
    }

    // TODO: 06/08/2019 test libraries property

    // TODO: 06/08/2019 test what happens with same alias in constructor (maybe error should be thrown like in "plus" method)

    // TODO: 06/08/2019  test behaviour of operators and other properties when multiple libraries are present

    // TODO: 06/08/2019  test plus and update

}
