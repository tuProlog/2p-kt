package it.unibo.tuprolog.libraries.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.operators.Associativity
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Request
import it.unibo.tuprolog.primitive.Response
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase

typealias RawLibrary = Pair<String, Triple<OperatorSet, ClauseDatabase, Map<Signature, Primitive>>>

/**
 * Utils singleton to help testing [Library]
 *
 * @author Enrico
 */
internal object LibraryUtils {

    internal val plusOperator = Operator("+", Associativity.YFX, 500)
    internal val minusOperator = Operator("-", Associativity.YFX, 300)

    internal val minusOperatorOverridden = Operator("-", Associativity.YFX, 1000)

    internal val theory = ClauseDatabase.of(Rule.of(Atom.of("a")), Rule.of(Atom.of("b")))
    internal val theoryWithDuplicates = ClauseDatabase.of(Rule.of(Atom.of("c")), Rule.of(Atom.of("b")))

    internal fun myPrimitive(r: Request): Sequence<Response> = throw NotImplementedError()
    internal fun myOtherPrimitive(r: Request): Sequence<Response> = throw NotImplementedError()
    internal val primitives = mapOf(Signature("myFunc1", 1) to ::myPrimitive)
    internal val primitivesOverridden = mapOf(Signature("myFunc1", 1) to ::myOtherPrimitive)

    /** An empty library */
    internal val emptyLibrary by lazy {
        "emptyLibrary" to Triple(OperatorSet(), ClauseDatabase.of(), emptyMap<Signature, Primitive>())
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

    /** Contains various libraries */
    internal val allLibraries by lazy { listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary) }

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

    /** Utility function to duplicate a primitive aliasing it */
    internal fun aliasPrimitive(libAlias: String, entry: Map.Entry<Signature, Primitive>): Iterable<Pair<Signature, Primitive>> =
            listOf(entry.toPair(), entry.key.copy(name = libAlias + LibraryAliased.ALIAS_SEPARATOR + entry.key.name) to entry.value)

}
