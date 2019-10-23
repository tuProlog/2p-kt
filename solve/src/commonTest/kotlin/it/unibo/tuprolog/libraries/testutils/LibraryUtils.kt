package it.unibo.tuprolog.libraries.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.libraries.LibraryAliased
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.function.NullaryFunction
import it.unibo.tuprolog.primitive.function.PrologFunction
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.theory.ClauseDatabase

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
            val theory: ClauseDatabase,
            val primitives: Map<Signature, Primitive>,
            val functions: Map<Signature, PrologFunction<Term>>
    )

    private val plusOperator = Operator("+", Specifier.YFX, 500)
    private val minusOperator = Operator("-", Specifier.YFX, 300)

    private val minusOperatorOverridden = Operator("-", Specifier.YFX, 1000)

    private val theory = ClauseDatabase.of(Rule.of(Atom.of("a")), Rule.of(Atom.of("b")))
    private val theoryWithDuplicates = ClauseDatabase.of(Rule.of(Atom.of("c")), Rule.of(Atom.of("b")))

    private fun myPrimitive(@Suppress("UNUSED_PARAMETER") r: Solve.Request<ExecutionContext>): Sequence<Solve.Response> = throw NotImplementedError()
    private fun myOtherPrimitive(@Suppress("UNUSED_PARAMETER") r: Solve.Request<ExecutionContext>): Sequence<Solve.Response> = throw NotImplementedError()
    private val primitives = mapOf(Signature("myPrimitive1", 1) to ::myPrimitive)
    private val primitivesOverridden = mapOf(Signature("myPrimitive1", 1) to ::myOtherPrimitive)

    private val myFunction: PrologFunction<Term> = object : NullaryFunction<Term> {
        override fun invoke(): Term = Truth.`true`()
    }
    private val myOtherFunction: PrologFunction<Term> = object : NullaryFunction<Term> {
        override fun invoke(): Term = Truth.fail()
    }
    private val functions = mapOf(Signature("myFunc1", 1) to myFunction)
    private val functionsOverridden = mapOf(Signature("myFunc1", 1) to myOtherFunction)

    /** An empty library */
    internal val emptyLibrary by lazy {
        RawLibrary("emptyLibrary", OperatorSet(), ClauseDatabase.empty(), emptyMap(), emptyMap())
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
        RawLibrary("myLibrary", OperatorSet(), ClauseDatabase.of(Fact.of(Truth.fail())), emptyMap(), emptyMap())
    }

    /** Contains various libraries */
    internal val allLibraries by lazy { listOf(emptyLibrary, library, overridingLibrary, overriddenLibrary, duplicatedAliasLibrary) }


    /** A method to disambiguate use of Library.of reference */
    internal fun libraryWithAliasConstructor(opSet: OperatorSet, theory: ClauseDatabase, primitives: Map<Signature, Primitive>, functions: Map<Signature, PrologFunction<Term>>, alias: String): LibraryAliased =
            Library.of(opSet, theory, primitives, functions, alias)

    /** Utility function to construct a library from raw data */
    internal inline fun makeLib(
            rawLibrary: RawLibrary,
            constructor: (OperatorSet, ClauseDatabase, Map<Signature, Primitive>, Map<Signature, PrologFunction<Term>>) -> Library
    ): Library = constructor(rawLibrary.opSet, rawLibrary.theory, rawLibrary.primitives, rawLibrary.functions)

    /** Utility function to construct a library with alias from raw data */
    internal inline fun makeLib(
            rawLibrary: RawLibrary,
            constructor: (OperatorSet, ClauseDatabase, Map<Signature, Primitive>, Map<Signature, PrologFunction<Term>>, String) -> LibraryAliased
    ): LibraryAliased = constructor(rawLibrary.opSet, rawLibrary.theory, rawLibrary.primitives, rawLibrary.functions, rawLibrary.name)

    /** Utility function to alias a primitive/function */
    internal fun aliasPrimitiveOrFunction(libAlias: String, entry: Map.Entry<Signature, *>) =
            entry.key.copy(name = libAlias + LibraryAliased.ALIAS_SEPARATOR + entry.key.name) to entry.value

    /** Utility function to duplicate all primitive/functions aliasing them in library */
    internal fun aliasLibraryMap(libAlias: String, toAliasMap: Map<Signature, *>) =
            toAliasMap.flatMap {
                listOf(it.toPair(), aliasPrimitiveOrFunction(libAlias, it))
            }.toMap()
}
