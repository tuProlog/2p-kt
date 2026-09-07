package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.CLAUSE_FUNCTOR
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A logic clause, i.e. a [Struct] with functor `:-`, representing either a [Rule] (`head :- body`, [head]
 * non-`null`) or a [Directive] (`:- body`, [head] `null`). A theory (see `:theory`) is, at its core, a
 * sequence of [Clause]s.
 *
 * [Clause.of] is the general entry point for building either kind, dispatching on whether [head] is `null`;
 * reach for [Rule.of], [Fact.of], or [Directive.of] directly when the desired kind is already known.
 */
interface Clause : Struct {
    override val functor: String
        get() = CLAUSE_FUNCTOR

    /** The head of this clause, or `null` if this is a [Directive]. */
    @JsName("head")
    val head: Struct?

    /** The body of this clause: a single goal, or a right-nested [Tuple] of goals if there is more than one. */
    @JsName("body")
    val body: Term

    /**
     * Checks whether this [Clause] is well-formed.
     *
     * A [Clause] is well-formed if and only if:
     * - its [head] is neither a [Numeric] nor a [Var] (when non-`null`);
     * - its [body] is not a [Numeric], nor does it contain one as a direct argument of a `, `/2, `;`/2, or
     *   `->`/2 structure (see [notableFunctors]).
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

    /** The individual goals making up [body], obtained by unfolding it if it is a [Tuple]. */
    @JsName("bodyItems")
    val bodyItems: Iterable<Term>

    /** The number of goals in [bodyItems]. */
    @JsName("bodySize")
    val bodySize: Int

    /** [body], represented as a [Tuple] (wrapping it if necessary), or `null` if [body] is not a [Tuple]-like sequence. */
    @JsName("bodyAsTuple")
    val bodyAsTuple: Tuple?

    /**
     * Gets the [index]-th goal in [bodyItems].
     * @throws IndexOutOfBoundsException if [index] is out of [bodyItems] bounds
     */
    @JsName("getBodyItem")
    fun getBodyItem(index: Int): Term

    /** Creates a novel [Rule] which is a copy of the current [Clause], except that [head] is set to [head]. */
    @JsName("setHead")
    fun setHead(head: Struct): Rule

    /** Creates a novel [Clause] which is a copy of the current one, except that [body] is set to [body]. */
    @JsName("setBody")
    fun setBody(body: Term): Clause

    /** Creates a novel [Clause] which is a copy of the current one, except that [head]'s functor is set to [functor]. */
    @JsName("setHeadFunctor")
    fun setHeadFunctor(functor: String): Clause

    /** Creates a novel [Clause] which is a copy of the current one, except that [head]'s arguments are set to [arguments]. */
    @JsName("setHeadArgs")
    fun setHeadArgs(vararg arguments: Term): Clause

    /** @see setHeadArgs */
    @JsName("setHeadArgsIterable")
    fun setHeadArgs(arguments: Iterable<Term>): Clause

    /** @see setHeadArgs */
    @JsName("setHeadArgsSequence")
    fun setHeadArgs(arguments: Sequence<Term>): Clause

    /**
     * Creates a novel [Clause] which is a copy of the current one, except that [argument] is inserted into
     * [head]'s arguments at position [index].
     * @throws IndexOutOfBoundsException if [index] is out of [head]'s argument bounds
     */
    @JsName("insertHeadArg")
    fun insertHeadArg(
        index: Int,
        argument: Term,
    ): Clause

    /** Creates a novel [Clause] which is a copy of the current one, except that [argument] is prepended to [head]'s arguments. */
    @JsName("addFirstHeadArg")
    fun addFirstHeadArg(argument: Term): Clause

    /** Creates a novel [Clause] which is a copy of the current one, except that [argument] is appended to [head]'s arguments. */
    @JsName("addLastHeadArg")
    fun addLastHeadArg(argument: Term): Clause

    /** Alias for [addLastHeadArg]. */
    @JsName("appendHeadArg")
    fun appendHeadArg(argument: Term): Clause

    /** Creates a novel [Clause] which is a copy of the current one, except that [body] is set from [argument] and [arguments]. */
    @JsName("setBodyItems")
    fun setBodyItems(
        argument: Term,
        vararg arguments: Term,
    ): Clause

    /** @see setBodyItems */
    @JsName("setBodyItemsIterable")
    fun setBodyItems(arguments: Iterable<Term>): Clause

    /** @see setBodyItems */
    @JsName("setBodyItemsSequence")
    fun setBodyItems(arguments: Sequence<Term>): Clause

    /**
     * Creates a novel [Clause] which is a copy of the current one, except that [argument] is inserted into
     * [bodyItems] at position [index].
     * @throws IndexOutOfBoundsException if [index] is out of [bodyItems] bounds
     */
    @JsName("insertBodyItem")
    fun insertBodyItem(
        index: Int,
        argument: Term,
    ): Clause

    /** Creates a novel [Clause] which is a copy of the current one, except that [argument] is prepended to [bodyItems]. */
    @JsName("addFirstBodyItem")
    fun addFirstBodyItem(argument: Term): Clause

    /** Creates a novel [Clause] which is a copy of the current one, except that [argument] is appended to [bodyItems]. */
    @JsName("addLastBodyItem")
    fun addLastBodyItem(argument: Term): Clause

    /** Alias for [addLastBodyItem]. */
    @JsName("appendBodyItem")
    fun appendBodyItem(argument: Term): Clause

    companion object {
        /** The canonical clause functor: `:-` */
        const val FUNCTOR = CLAUSE_FUNCTOR

        /**
         * Creates a [Clause]: a [Rule] if [head] is non-`null`, a [Directive] otherwise.
         * @throws IllegalArgumentException if [head] is `null` and [body] is empty
         */
        @JvmStatic
        @JsName("of")
        fun of(
            head: Struct? = null,
            vararg body: Term,
        ): Clause = of(head, body.asIterable())

        /**
         * Creates a [Clause]: a [Rule] if [head] is non-`null`, a [Directive] otherwise.
         * @throws IllegalArgumentException if [head] is `null` and [body] is empty
         */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            head: Struct? = null,
            body: Iterable<Term>,
        ): Clause =
            when (head) {
                null -> {
                    require(body.any()) { "If Clause head is null, at least one body element, is required" }
                    Directive.of(body.asIterable())
                }
                else -> Rule.of(head, body)
            }

        /** @see of */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            head: Struct? = null,
            body: Sequence<Term>,
        ): Clause = of(head, body.asIterable())

        /** The functors ([Tuple], if-then, if-then-else) whose arguments matter for [isWellFormed]'s body check. */
        @JvmStatic
        @JsName("notableFunctors")
        val notableFunctors = listOf(",", ";", "->")

        /** A [TermVisitor] checking whether a term respects the body constraints part of [isWellFormed]. */
        @JvmStatic
        @JsName("bodyWellFormedVisitor")
        val bodyWellFormedVisitor: TermVisitor<Boolean> =
            object : TermVisitor<Boolean> {
                override fun defaultValue(term: Term): Boolean = true

                override fun visitNumeric(term: Numeric): Boolean = false

                override fun visitStruct(term: Struct): Boolean =
                    when {
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

                override fun visitStruct(term: Struct): Term =
                    when {
                        term.functor in notableFunctors && term.arity == 2 ->
                            Struct.of(term.functor, term.argsSequence.map { arg -> arg.accept(this) })
                        else -> term
                    }

                override fun visitClause(term: Clause): Term = of(term.head, term.body.accept(this))

                override fun visitVar(term: Var): Term =
                    when (term) {
                        in unifier -> unifier[term]!!.accept(this)
                        else -> Struct.of("call", term)
                    }
            }

        /**
         * A visitor to prepare Clauses for execution
         *
         * For example, the [Clause] `product(A) :- A, A` is transformed, after preparation for execution,
         * as the Term: `product(A) :- call(A), call(A)`
         */
        internal val defaultPreparationForExecutionVisitor: TermVisitor<Term> = preparationForExecutionVisitor()
    }
}
