package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

internal class WhenStruct<T>(
    private val ifStruct: (Struct) -> T,
    private val otherwise: (Term) -> T
) : TermVisitor<T> {
    override fun defaultValue(term: Term): T = otherwise(term)
    override fun visitStruct(term: Struct): T = ifStruct(term)
}
