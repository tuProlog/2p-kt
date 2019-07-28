package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*

internal abstract class ClauseImpl(override val head: Struct?, override val body: Term)
    : StructImpl(Clause.FUNCTOR, (if (head === null) arrayOf(body) else arrayOf(head, body))), Clause {

    override val isWellFormed: Boolean by lazy { body.accept(bodyWellFormedVisitor) }

    override val functor: String
        get() = super<Clause>.functor

    override val args: Array<Term>
        get() = super<StructImpl>.args

    override fun toString(): String =
            when (head) {
                null -> "$functor $body"
                else -> "$head $functor $body"
            }

    companion object {

        /** A visitor that checks whether [isWellFormed] is respected */
        private val bodyWellFormedVisitor: TermVisitor<Boolean> = object : TermVisitor<Boolean> {

            override fun defaultValue(term: Term): Boolean = term !is Numeric

            override fun visit(term: Struct): Boolean = when {
                term.functor in Clause.notableFunctors && term.arity == 2 ->
                    term.argsSequence
                            .map { arg -> arg.accept(this) }
                            .reduce(Boolean::and)
                else -> true
            }
        }
    }
}