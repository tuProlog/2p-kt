package it.unibo.tuprolog.core

interface Clause : Struct {

    override val functor: String
        get() = FUNCTOR

    val head: Struct?

    val body: Term

    /**
     * Checks whether this Clause is wellFormed
     *
     * A Clause is said to be "well formed" if:
     * - its head isn't a Numeric or a Var
     * - its body isn't a Numeric and doesn't contain a Numeric in arguments place, for notable Structs with functors ','/2, ';'/2 and '->'/2
     */
    val isWellFormed: Boolean

    override val args: Array<Term>
        get() = (if (head === null) arrayOf(body) else arrayOf(head!!, body))

    override val arity: Int
        get() = (if (head === null) 1 else 2)

    override val isClause: Boolean
        get() = true

    override val isRule: Boolean
        get() = head !== null

    override val isFact: Boolean
        get() = head !== null && body.isTrue

    override val isDirective: Boolean
        get() = head === null

    override fun freshCopy(): Clause = super.freshCopy() as Clause

    override fun freshCopy(scope: Scope): Clause = super.freshCopy(scope) as Clause

    companion object {
        const val FUNCTOR = ":-"

        /** Contains notable functor in determining if a Clause [isWellFormed] */
        val notableFunctors = listOf(",", ";", "->")

        fun of(head: Struct? = null, vararg body: Term): Clause =
                when (head) {
                    null -> {
                        require(body.any()) { "If Clause head is null, at least one body element, is required" }
                        Directive.of(body.asIterable())
                    }
                    else -> Rule.of(head, *body)
                }
    }

}
