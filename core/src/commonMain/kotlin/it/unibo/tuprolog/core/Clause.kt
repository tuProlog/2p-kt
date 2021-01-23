package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Clause : Struct {

    override val functor: String
        get() = FUNCTOR

    @JsName("head")
    val head: Struct?

    @JsName("body")
    val body: Term

    /**
     * Checks whether this Clause is wellFormed
     *
     * A Clause is said to be "well formed" if:
     * - its head isn't a Numeric or a Var
     * - its body isn't a Numeric and doesn't contain a Numeric in arguments place, for notable Structs with functors ','/2, ';'/2 and '->'/2
     */
    @JsName("isWellFormed")
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

    override fun freshCopy(): Clause

    override fun freshCopy(scope: Scope): Clause

    companion object {

        const val FUNCTOR = ":-"

        @JvmStatic
        @JsName("of")
        fun of(head: Struct? = null, vararg body: Term): Clause =
            when (head) {
                null -> {
                    require(body.any()) { "If Clause head is null, at least one body element, is required" }
                    Directive.of(body.asIterable())
                }
                else -> Rule.of(head, *body)
            }

        /** Contains notable functor in determining if a Clause [isWellFormed] */
        @JvmStatic
        @JsName("notableFunctors")
        val notableFunctors = listOf(",", ";", "->")

        /** A visitor that checks whether [isWellFormed] (body constraints part) is respected */
        @JvmStatic
        @JsName("bodyWellFormedVisitor")
        val bodyWellFormedVisitor: TermVisitor<Boolean> = object : TermVisitor<Boolean> {

            override fun defaultValue(term: Term): Boolean = term !is Numeric

            override fun visit(term: Struct): Boolean = when {
                term.functor in notableFunctors && term.arity == 2 ->
                    term.argsSequence
                        .map { arg -> arg.accept(this) }
                        .reduce(Boolean::and)
                else -> true
            }
        }

        // TODO: 16/01/2020 test this method
        internal fun preparationForExecutionVisitor(unifier: Substitution.Unifier = Substitution.empty()) =
            object : TermVisitor<Term> {
                override fun defaultValue(term: Term) = term

                override fun visit(term: Struct): Term = when {
                    term is Clause -> visit(term)
                    term.functor in notableFunctors && term.arity == 2 ->
                        Struct.of(term.functor, term.argsSequence.map { arg -> arg.accept(this) })

                    else -> term
                }

                override fun visit(term: Clause): Term = of(term.head, visit(term.body))

                override fun visit(term: Var): Term = when (term) {
                    in unifier -> visit(unifier[term]!!)
                    else -> Struct.of("call", term)
                }
            }

        /**
         * A visitor to prepare Clauses for execution
         *
         * For example, the [Clause] `product(A) :- A, A` is transformed, after preparation for execution,
         * as the Term: `product(A) :- call(A), call(A)`
         */
        internal val defaultPreparationForExecutionVisitor = preparationForExecutionVisitor()
    }
}
