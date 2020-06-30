package it.unibo.tuprolog.solve.library.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.function.Compute
import it.unibo.tuprolog.solve.function.PrologFunction
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.theory.Theory

/**
 * Utils singleton to help testing [Library]
 *
 * @author Enrico
 */
internal object LibraryUtils {

    /** A class to represent raw library data in tests */
    internal data class RawLibrary(
        val name: String,
        val opSet: OperatorSet,
        val theory: Theory,
        val primitives: Map<Signature, Primitive>,
        val functions: Map<Signature, PrologFunction>
    )

    private val plusOperator = Operator("+", Specifier.YFX, 500)
    private val minusOperator = Operator("-", Specifier.YFX, 300)

    private val minusOperatorOverridden = Operator("-", Specifier.YFX, 1000)

    private val theory = Theory.indexedOf(Rule.of(Atom.of("a")), Rule.of(Atom.of("b")))
    private val theoryWithDuplicates = Theory.indexedOf(Rule.of(Atom.of("c")), Rule.of(Atom.of("b")))

    private fun myPrimitive(@Suppress("UNUSED_PARAMETER") r: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
        throw NotImplementedError()

    private fun myOtherPrimitive(@Suppress("UNUSED_PARAMETER") r: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
        throw NotImplementedError()

    private val primitives = mapOf(Signature("myPrimitive1", 1) to ::myPrimitive)
    private val primitivesOverridden = mapOf(Signature("myPrimitive1", 1) to ::myOtherPrimitive)

    private fun myFunction(@Suppress("UNUSED_PARAMETER") r: Compute.Request<ExecutionContext>): Compute.Response =
        throw NotImplementedError()

    private fun myOtherFunction(@Suppress("UNUSED_PARAMETER") r: Compute.Request<ExecutionContext>): Compute.Response =
        throw NotImplementedError()

    private val functions = mapOf(Signature("myFunc1", 1) to ::myFunction)
    private val functionsOverridden = mapOf(Signature("myFunc1", 1) to ::myOtherFunction)

    /** An empty library */
    internal val emptyLibrary by lazy {
        RawLibrary("emptyLibrary", OperatorSet(), Theory.empty(), emptyMap(), emptyMap())
    }

    /** Contains a starting library, with some operators theory and primitives */
    internal val library by lazy {
        RawLibrary(
            "myLibrary",
            OperatorSet(plusOperator, minusOperator),
            theory,
            primitives,
            functions
        )
    }

    /** Contains a library that w.r.t [library] overrides some operators and primitives, adding clauses to theory */
    internal val overridingLibrary by lazy {
        RawLibrary(
            "myOverridingLibrary",
            OperatorSet(minusOperatorOverridden),
            theoryWithDuplicates,
            primitivesOverridden,
            functionsOverridden
        )
    }

    /** Contains the final library, that should result from combination of [library] the [overriddenLibrary] */
    internal val overriddenLibrary by lazy {
        RawLibrary(
            "myOverriddenLibrary",
            OperatorSet(plusOperator, minusOperatorOverridden),
            theory + theoryWithDuplicates,
            primitives + primitivesOverridden,
            functions + functionsOverridden
        )
    }

    /** A duplicated alias library w.r.t. [library] */
    internal val duplicatedAliasLibrary by lazy {
        RawLibrary("myLibrary", OperatorSet(), Theory.indexedOf(Fact.of(Truth.FAIL)), emptyMap(), emptyMap())
    }

    /** Contains various libraries */
    internal val allLibraries by lazy {
        listOf(
            emptyLibrary,
            library,
            overridingLibrary,
            overriddenLibrary,
            duplicatedAliasLibrary
        )
    }


    /** A method to disambiguate use of Library.of reference */
    internal fun libraryWithAliasConstructor(
        opSet: OperatorSet,
        theory: Theory,
        primitives: Map<Signature, Primitive>,
        functions: Map<Signature, PrologFunction>,
        alias: String
    ): AliasedLibrary = Library.of(opSet, theory, primitives, functions, alias)

    /** Utility function to construct a library from raw data */
    internal inline fun makeLib(
        rawLibrary: RawLibrary,
        constructor: (OperatorSet, Theory, Map<Signature, Primitive>, Map<Signature, PrologFunction>) -> Library
    ): Library = constructor(rawLibrary.opSet, rawLibrary.theory, rawLibrary.primitives, rawLibrary.functions)

    /** Utility function to construct a library with alias from raw data */
    internal inline fun makeLib(
        rawLibrary: RawLibrary,
        constructor: (OperatorSet, Theory, Map<Signature, Primitive>, Map<Signature, PrologFunction>, String) -> AliasedLibrary
    ): AliasedLibrary =
        constructor(rawLibrary.opSet, rawLibrary.theory, rawLibrary.primitives, rawLibrary.functions, rawLibrary.name)

    /** Utility function to alias a primitive/function */
    internal fun aliasPrimitiveOrFunction(libAlias: String, entry: Map.Entry<Signature, *>) =
        entry.key.copy(name = libAlias + AliasedLibrary.ALIAS_SEPARATOR + entry.key.name) to entry.value

    /** Utility function to duplicate all primitive/functions aliasing them in library */
    internal fun aliasLibraryMap(libAlias: String, toAliasMap: Map<Signature, *>) =
        toAliasMap.flatMap {
            listOf(it.toPair(), aliasPrimitiveOrFunction(libAlias, it))
        }.toMap()
}
