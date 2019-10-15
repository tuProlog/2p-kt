package it.unibo.tuprolog.libraries.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.testutils.LibraryUtils
import it.unibo.tuprolog.libraries.testutils.LibraryUtils.makeLib
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [LibraryImpl] and [Library]
 *
 * @author Enrico
 */
internal class LibraryImplTest {

    private val libraryInstances = LibraryUtils.allLibraries.map { makeLib(it, ::LibraryImpl) }

    @Test
    fun operatorsCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, lib) -> val (opSet, _, _) = lib; opSet }
        val toBeTested = libraryInstances.map { it.operators }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun theoryCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, lib) -> val (_, theory, _) = lib; theory }
        val toBeTested = libraryInstances.map { it.theory }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun primitivesCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, lib) -> val (_, _, primitives) = lib; primitives }
        val toBeTested = libraryInstances.map { it.primitives }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun containsSignatureWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryToAlias, libraryInstance) ->
            val (_, theory, primitives) = libraryToAlias.second

            theory.rules.map { it.head.extractSignature() } + primitives.keys
                    .forEach { signature -> assertTrue { signature in libraryInstance } }

            assertFalse { Signature("ciao", 3) in libraryInstance }
        }
    }

    @Test
    fun containsSignatureDiscardsVarargSignatures() {
        val library = LibraryImpl(OperatorSet(), ClauseDatabase.of(Fact.of(Struct.of("f", Atom.of("a")))), emptyMap())

        assertTrue { Signature("f", 1, false) in library }
        assertFalse { Signature("f", 1, true) in library }
    }

    @Test
    fun containsOperatorWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryToAlias, libraryInstance) ->
            val (opSet, _, _) = libraryToAlias.second

            opSet.forEach { operator -> assertTrue { operator in libraryInstance } }

            assertFalse { Operator("ciao", Specifier.XFX, 9) in libraryInstance }
        }
    }

    @Test
    fun hasPrimitiveWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryToAlias, libraryInstance) ->
            val (_, theory, primitives) = libraryToAlias.second

            primitives.keys.forEach { signature -> assertTrue { libraryInstance.hasPrimitive(signature) } }

            (theory.rules.map { it.head.extractSignature() } + Signature("ciao", 3)).forEach {
                assertFalse { libraryInstance.hasPrimitive(it) }
            }
        }
    }

    @Test
    fun hasProtectedWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryToAlias, libraryInstance) ->
            val (_, theory, primitives) = libraryToAlias.second

            theory.rules.map { it.head.extractSignature() } + primitives.keys
                    .forEach { signature -> assertTrue { libraryInstance.hasProtected(signature) } }

            assertFalse { libraryInstance.hasProtected(Signature("ciao", 3)) }
        }
    }
}
