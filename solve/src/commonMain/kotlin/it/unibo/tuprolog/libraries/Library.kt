package it.unibo.tuprolog.libraries

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.impl.LibraryAliasedImpl
import it.unibo.tuprolog.libraries.impl.LibraryImpl
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.function.PrologFunction
import it.unibo.tuprolog.primitive.toIndicator
import it.unibo.tuprolog.theory.ClauseDatabase

/** Represents a Prolog library */
interface Library {

    /** Library defined operators */
    val operators: OperatorSet

    /** The library theory clauses */
    val theory: ClauseDatabase

    /** The library primitives, identified by their signatures */
    val primitives: Map<Signature, Primitive>

    /** The library prolog functions, identified by their signature */
    val functions: Map<Signature, PrologFunction>

    /**
     * Checks whether this library contains the provided signature.
     *
     * The default implementation, checks for signature presence among primitives and theory clauses by indicator-like search
     */
    operator fun contains(signature: Signature): Boolean =
        primitives.containsKey(signature) ||
                signature.toIndicator()?.let { theory.contains(it) } ?: false

    /** Checks whether this library contains the definition of provided operator */
    operator fun contains(operator: Operator): Boolean = operator in operators

    /** Checks whether this library has a [Primitive] with provided signature */
    fun hasPrimitive(signature: Signature): Boolean = signature in primitives.keys

    /** Checks whether the provided signature, is protected in this library */
    fun hasProtected(signature: Signature): Boolean = signature in this

    companion object {

        /** Creates an instance of [Library] with given parameters */
        fun of(
            operatorSet: OperatorSet = OperatorSet(),
            theory: ClauseDatabase = ClauseDatabase.empty(),
            primitives: Map<Signature, Primitive> = emptyMap(),
            functions: Map<Signature, PrologFunction> = emptyMap()
        ): Library = LibraryImpl(operatorSet, theory, primitives, functions)

        /** Creates an instance of [LibraryAliased] with given parameters */
        fun of(
            operatorSet: OperatorSet = OperatorSet(),
            theory: ClauseDatabase = ClauseDatabase.empty(),
            primitives: Map<Signature, Primitive> = emptyMap(),
            functions: Map<Signature, PrologFunction> = emptyMap(),
            alias: String
        ): LibraryAliased = LibraryAliasedImpl(operatorSet, theory, primitives, functions, alias)

        /** Creates an instance of [LibraryAliased] starting from [Library] and an alias */
        fun of(library: Library, alias: String): LibraryAliased =
            LibraryAliasedImpl(library.operators, library.theory, library.primitives, library.functions, alias)
    }
}
