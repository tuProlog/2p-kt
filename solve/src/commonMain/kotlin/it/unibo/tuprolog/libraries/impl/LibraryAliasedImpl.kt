package it.unibo.tuprolog.libraries.impl

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.AliasedLibrary
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.function.PrologFunction
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Default implementation class of [AliasedLibrary]
 *
 * @author Enrico
 */
open class LibraryAliasedImpl(
    override val operators: OperatorSet,
    override val theory: ClauseDatabase,
    override val primitives: Map<Signature, Primitive>,
    override val functions: Map<Signature, PrologFunction>,
    override val alias: String
) : LibraryImpl(operators, theory, primitives, functions), AliasedLibrary {

    override fun toString(): String =
        "Library(alias='$alias', operators=$operators, theory=$theory, primitives=$primitives, functions=$functions)"
}
