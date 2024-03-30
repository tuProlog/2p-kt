package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.CLAUSE_FUNCTOR
import it.unibo.tuprolog.core.impl.ClauseBodyWellFormedVisitor
import it.unibo.tuprolog.core.impl.PrepareClauseForExecutionVisitor
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Clause : Struct {
    override val functor: String
        get() = CLAUSE_FUNCTOR

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

    override fun asClause(): Clause = this

    @JsName("bodyItems")
    val bodyItems: Iterable<Term>

    @JsName("bodySize")
    val bodySize: Int

    @JsName("bodyAsTuple")
    val bodyAsTuple: Tuple?

    @JsName("getBodyItem")
    fun getBodyItem(index: Int): Term

    @JsName("setHead")
    fun setHead(head: Struct): Rule

    @JsName("setBody")
    fun setBody(body: Term): Clause

    @JsName("setHeadFunctor")
    fun setHeadFunctor(functor: String): Clause

    @JsName("setHeadArgs")
    fun setHeadArgs(vararg arguments: Term): Clause

    @JsName("setHeadArgsIterable")
    fun setHeadArgs(arguments: Iterable<Term>): Clause

    @JsName("setHeadArgsSequence")
    fun setHeadArgs(arguments: Sequence<Term>): Clause

    @JsName("insertHeadArg")
    fun insertHeadArg(
        index: Int,
        argument: Term,
    ): Clause

    @JsName("addFirstHeadArg")
    fun addFirstHeadArg(argument: Term): Clause

    @JsName("addLastHeadArg")
    fun addLastHeadArg(argument: Term): Clause

    @JsName("appendHeadArg")
    fun appendHeadArg(argument: Term): Clause

    @JsName("setBodyItems")
    fun setBodyItems(
        argument: Term,
        vararg arguments: Term,
    ): Clause

    @JsName("setBodyItemsIterable")
    fun setBodyItems(arguments: Iterable<Term>): Clause

    @JsName("setBodyItemsSequence")
    fun setBodyItems(arguments: Sequence<Term>): Clause

    @JsName("insertBodyItem")
    fun insertBodyItem(
        index: Int,
        argument: Term,
    ): Clause

    @JsName("addFirstBodyItem")
    fun addFirstBodyItem(argument: Term): Clause

    @JsName("addLastBodyItem")
    fun addLastBodyItem(argument: Term): Clause

    @JsName("appendBodyItem")
    fun appendBodyItem(argument: Term): Clause

    companion object {
        const val FUNCTOR = CLAUSE_FUNCTOR

        @JvmStatic
        @JsName("of")
        fun of(
            head: Struct? = null,
            vararg body: Term,
        ): Clause = TermFactory.default.clauseOf(head, *body)

        @JvmStatic
        @JsName("ofIterable")
        fun of(
            head: Struct? = null,
            body: Iterable<Term>,
        ): Clause = TermFactory.default.clauseOf(head, body)

        @JvmStatic
        @JsName("ofSequence")
        fun of(
            head: Struct? = null,
            body: Sequence<Term>,
        ): Clause = TermFactory.default.clauseOf(head, body)

        /** Contains notable functor in determining if a Clause [isWellFormed] */
        @JvmStatic
        @JsName("notableFunctors")
        val notableFunctors = Terms.NOTABLE_FUNCTORS_FOR_CLAUSES

        /** A visitor that checks whether [isWellFormed] (body constraints part) is respected */
        @JvmStatic
        @JsName("bodyWellFormedVisitor")
        val bodyWellFormedVisitor: TermVisitor<Boolean> = ClauseBodyWellFormedVisitor()

        // TODO: 16/01/2020 test this method
        internal fun preparationForExecutionVisitor(unifier: Substitution.Unifier = Substitution.empty()) =
            PrepareClauseForExecutionVisitor(unifier, TermFactory.default)

        /**
         * A visitor to prepare Clauses for execution
         *
         * For example, the [Clause] `product(A) :- A, A` is transformed, after preparation for execution,
         * as the Term: `product(A) :- call(A), call(A)`
         */
        internal val defaultPreparationForExecutionVisitor: TermVisitor<Term> = preparationForExecutionVisitor()
    }
}
