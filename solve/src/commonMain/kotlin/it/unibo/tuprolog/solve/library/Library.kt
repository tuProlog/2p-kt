package it.unibo.tuprolog.solve.library

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.PrologFunction
import it.unibo.tuprolog.solve.library.impl.LibraryAliasedImpl
import it.unibo.tuprolog.solve.library.impl.LibraryImpl
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** Represents a Prolog library */
interface Library {

    /** Library defined operators */
    @JsName("operators")
    val operators: OperatorSet

    /** The library theory clauses */
    @JsName("theory")
    val theory: Theory

    /** The library primitives, identified by their signatures */
    @JsName("primitives")
    val primitives: Map<Signature, Primitive>

    /** The library prolog functions, identified by their signature */
    @JsName("functions")
    val functions: Map<Signature, PrologFunction>

    /**
     * Checks whether this library contains the provided signature.
     *
     * The default implementation, checks for signature presence among primitives and theory clauses by indicator-like search
     */
    @JsName("containsSignature")
    operator fun contains(signature: Signature): Boolean =
        primitives.containsKey(signature) ||
                signature.toIndicator()?.let { theory.contains(it) } ?: false

    /** Checks whether this library contains the definition of provided operator */
    @JsName("containsOperator")
    operator fun contains(operator: Operator): Boolean = operator in operators

    /** Checks whether this library has a [Primitive] with provided signature */
    @JsName("hasPrimitive")
    fun hasPrimitive(signature: Signature): Boolean = signature in primitives.keys

    /** Checks whether the provided signature, is protected in this library */
    @JsName("hasProtected")
    fun hasProtected(signature: Signature): Boolean = signature in this

    companion object {

        /** Creates an instance of [Library] with given parameters */
        @JvmStatic
        @JsName("unaliased")
        fun unaliased(
            operatorSet: OperatorSet = OperatorSet(),
            theory: Theory = Theory.empty(),
            primitives: Map<Signature, Primitive> = emptyMap(),
            functions: Map<Signature, PrologFunction> = emptyMap()
        ): Library = LibraryImpl(operatorSet, theory, primitives, functions)

        /** Creates an instance of [AliasedLibrary] with given parameters */
        @JvmStatic
        @JsName("aliased")
        fun aliased(
            operatorSet: OperatorSet = OperatorSet(),
            theory: Theory = Theory.empty(),
            primitives: Map<Signature, Primitive> = emptyMap(),
            functions: Map<Signature, PrologFunction> = emptyMap(),
            alias: String
        ): AliasedLibrary = LibraryAliasedImpl(operatorSet, theory, primitives, functions, alias)

        /** Creates an instance of [AliasedLibrary] starting from [Library] and an alias */
        @JvmStatic
        @JsName("of")
        fun of(library: Library, alias: String): AliasedLibrary =
            LibraryAliasedImpl(library.operators, library.theory, library.primitives, library.functions, alias)
    }
}
