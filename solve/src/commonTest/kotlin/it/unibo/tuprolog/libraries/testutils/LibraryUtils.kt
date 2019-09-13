package it.unibo.tuprolog.libraries.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.operators.Associativity
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.theory.ClauseDatabase

typealias RawLibrary = Pair<String, Triple<OperatorSet, ClauseDatabase, Map<Signature, Primitive>>>

/**
 * Utils singleton to help testing [Library]
 *
 * @author Enrico
 */
internal object LibraryUtils {

    private val plusOperator = Operator("+", Associativity.YFX, 500)
    private val minusOperator = Operator("-", Associativity.YFX, 300)

    private val minusOperatorOverridden = Operator("-", Associativity.YFX, 1000)

    private val theory = ClauseDatabase.of(Rule.of(Atom.of("a")), Rule.of(Atom.of("b")))
    private val theoryWithDuplicates = ClauseDatabase.of(Rule.of(Atom.of("c")), Rule.of(Atom.of("b")))

    private fun myPrimitive(@Suppress("UNUSED_PARAMETER") r: Solve.Request): Sequence<Solve.Response> = throw NotImplementedError()
    private fun myOtherPrimitive(@Suppress("UNUSED_PARAMETER") r: Solve.Request): Sequence<Solve.Response> = throw NotImplementedError()
    private val primitives = mapOf(Signature("myFunc1", 1) to ::myPrimitive)
    private val primitivesOverridden = mapOf(Signature("myFunc1", 1) to ::myOtherPrimitive)

    /** An empty library */
    internal val emptyLibrary by lazy {
        "emptyLibrary" to Triple(OperatorSet(), ClauseDatabase.empty(), emptyMap<Signature, Primitive>())
    }

    /** Contains a starting library, with some operators theory and primitives */
    internal val library by lazy {
        "myLibrary" to Triple(
                OperatorSet(plusOperator, minusOperator),
                theory,
                primitives
        )
    }

    /** Contains a library that w.r.t [library] overrides some operators and primitives, adding clauses to theory */
    internal val overridingLibrary by lazy {
        "myOverridingLibrary" to Triple(
                OperatorSet(minusOperatorOverridden),
                theoryWithDuplicates,
                primitivesOverridden
        )
    }

    /** Contains the final library, that should result from combination of [library] the [overriddenLibrary] */
    internal val overriddenLibrary by lazy {
        "myOverriddenLibrary" to Triple(
                OperatorSet(plusOperator, minusOperatorOverridden),
                theory + theoryWithDuplicates,
                primitives + primitivesOverridden
        )
    }

    /** A duplicated alias library w.r.t. [library] */
    internal val duplicatedAliasLibrary by lazy {
        "myLibrary" to Triple(
                OperatorSet(),
                ClauseDatabase.of(Fact.of(Truth.fail())),
                emptyMap<Signature, Primitive>()
        )
    }

    /** Contains various libraries */
    internal val allLibraries by lazy { listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary, duplicatedAliasLibrary) }


    /** A method to disambiguate use of Library.of reference */
    internal fun libraryWithAliasConstructor(opSet: OperatorSet, theory: ClauseDatabase, primitives: Map<Signature, Primitive>, alias: String): LibraryAliased =
            Library.of(opSet, theory, primitives, alias)

    /** Utility function to construct a library from raw data */
    internal inline fun makeLib(
            rawLibrary: RawLibrary,
            constructor: (OperatorSet, ClauseDatabase, Map<Signature, Primitive>) -> Library
    ): Library {
        val (_, lib) = rawLibrary
        val (operatorSet, theory, primitives) = lib

        return constructor(operatorSet, theory, primitives)
    }

    /** Utility function to construct a library with alias from raw data */
    internal inline fun makeLib(
            rawLibrary: RawLibrary,
            constructor: (OperatorSet, ClauseDatabase, Map<Signature, Primitive>, String) -> LibraryAliased
    ): LibraryAliased {
        val (alias, lib) = rawLibrary
        val (operatorSet, theory, primitives) = lib

        return constructor(operatorSet, theory, primitives, alias)
    }

    /** Utility function to alias a primitive */
    internal fun aliasPrimitive(libAlias: String, entry: Map.Entry<Signature, Primitive>) =
            entry.key.copy(name = libAlias + LibraryAliased.ALIAS_SEPARATOR + entry.key.name) to entry.value

    /** Utility function to duplicate all primitive aliasing them in library */
    internal fun aliasDuplicatingPrimitives(library: LibraryAliased, forcedAlias: String? = null) =
            library.primitives.flatMap {
                listOf(it.toPair(), aliasPrimitive(forcedAlias ?: library.alias, it))
            }.toMap()
}
