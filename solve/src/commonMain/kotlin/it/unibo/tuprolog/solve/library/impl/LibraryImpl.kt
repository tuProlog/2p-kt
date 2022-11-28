package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Primitive

/**
 * Default implementation class of [Library]
 *
 * @author Enrico
 */
internal data class LibraryImpl(
    override val alias: String,
    override val operators: OperatorSet,
    override val clauses: List<Clause>,
    override val primitives: Map<Signature, Primitive>,
    override val functions: Map<Signature, LogicFunction>
) : AbstractLibrary() {
    init {
        require(alias.isNotBlank())
    }
}
