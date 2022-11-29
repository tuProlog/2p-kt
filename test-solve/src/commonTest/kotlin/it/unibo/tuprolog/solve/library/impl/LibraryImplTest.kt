package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.libraryWithoutAliasConstructor
import it.unibo.tuprolog.solve.library.testutils.LibraryUtils.makeLib
import it.unibo.tuprolog.theory.Theory
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

    private val libraryInstances = LibraryUtils.allLibraries.map { makeLib(it, ::libraryWithoutAliasConstructor) }

    @Test
    fun operatorsCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, opSet) -> opSet }
        val toBeTested = libraryInstances.map { it.operators }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun theoryCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, _, theory) -> theory }
        val toBeTested = libraryInstances.map { it.clauses }.map { Theory.of(it) }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun primitivesCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, _, _, primitives) -> primitives }
        val toBeTested = libraryInstances.map { it.primitives }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun functionsCorrect() {
        val correct = LibraryUtils.allLibraries.map { (_, _, _, _, functions) -> functions }
        val toBeTested = libraryInstances.map { it.functions }

        correct.zip(toBeTested).forEach { (expected, actual) -> assertEquals(expected, actual) }
    }

    @Test
    fun containsSignatureWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryToAlias, libraryInstance) ->
            val (_, _, theory, primitives) = libraryToAlias

            theory.rules.map { it.head.extractSignature() } + primitives.keys
                .forEach { signature -> assertTrue { signature in libraryInstance } }

            assertFalse { Signature("ciao", 3) in libraryInstance }
        }
    }

    @Test
    fun containsSignatureDiscardsVarargSignatures() {
        val library = libraryWithoutAliasConstructor(
            OperatorSet(),
            Theory.of(Fact.of(Struct.of("f", Atom.of("a")))),
            emptyMap(),
            emptyMap()
        )

        assertTrue { Signature("f", 1, false) in library }
        assertFalse { Signature("f", 1, true) in library }
    }

    @Test
    fun containsOperatorWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryToAlias, libraryInstance) ->
            val (_, opSet) = libraryToAlias

            opSet.forEach { operator -> assertTrue { operator in libraryInstance } }

            assertFalse { Operator("ciao", Specifier.XFX, 9) in libraryInstance }
        }
    }

    @Test
    fun hasPrimitiveWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryToAlias, libraryInstance) ->
            val (_, _, theory, primitives) = libraryToAlias

            primitives.keys.forEach { signature -> assertTrue { libraryInstance.hasPrimitive(signature) } }

            (
                theory.rules.map { it.head.extractSignature() } + Signature(
                    "ciao",
                    3
                )
                ).forEach {
                assertFalse { libraryInstance.hasPrimitive(it) }
            }
        }
    }

    @Test
    fun hasProtectedWorksAsExpected() {
        LibraryUtils.allLibraries.zip(libraryInstances).forEach { (libraryToAlias, libraryInstance) ->
            val (_, _, theory, primitives) = libraryToAlias

            theory.rules.map { it.head.extractSignature() } + primitives.keys
                .forEach { signature -> assertTrue { libraryInstance.hasProtected(signature) } }

            assertFalse { libraryInstance.hasProtected(Signature("ciao", 3)) }
        }
    }
}
