package it.unibo.tuprolog.core

interface Clause : Struct {

    override val functor: String
        get() = FUNCTOR

    val head: Struct?

    val body: Term

    override val args: Array<Term>
        get() {
            return if (head === null) {
                arrayOf(body)
            } else {
                arrayOf(head!!, body)
            }
        }

    override val arity: Int
        get() {
            return if (head === null) {
                1
            } else {
                2
            }
        }

    override val isClause: Boolean
        get() = true

    override val isRule: Boolean
        get() = head !== null

    override val isFact: Boolean
        get() = head !== null && body.isTrue

    override val isDirective: Boolean
        get() = head === null

    companion object {
        const val FUNCTOR = ":-"

        fun of(head: Struct? = null, vararg body: Term): Clause {
            return if (head === null) {
                Directive.of(body[0], *body.sliceArray(1..body.lastIndex))
            } else {
                Rule.of(head, *body)
            }
        }
    }

}

