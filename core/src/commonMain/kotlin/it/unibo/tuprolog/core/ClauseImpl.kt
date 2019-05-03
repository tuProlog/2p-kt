package it.unibo.tuprolog.core

internal open class ClauseImpl(override val head: Struct?, override val body: Term)
    : StructImpl(Clause.FUNCTOR, (if (head === null) arrayOf(body) else arrayOf(head, body))), Clause {

    override val functor: String
        get() = super<Clause>.functor

    override val args: Array<Term>
        get() = super<StructImpl>.args

    override fun toString(): String {
        return if (head === null) {
            "$functor $body"
        } else {
            "$head $functor $body"
        }
    }

}

