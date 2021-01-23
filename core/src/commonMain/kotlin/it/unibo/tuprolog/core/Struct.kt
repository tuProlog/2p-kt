package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.StructImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface Struct : Term {

    override val isStruct: Boolean
        get() = true

    override val isClause: Boolean
        get() = Clause.FUNCTOR == functor

    override val isRule: Boolean
        get() = isClause && arity == 2

    override val isDirective: Boolean
        get() = isClause && arity == 1

    override val isFact: Boolean
        get() = isRule && args[1].isTrue

    override val isTuple: Boolean
        get() = functor == Tuple.FUNCTOR && arity == 2

    override val isAtom: Boolean
        get() = arity == 0

    override val isList: Boolean
        get() = isCons || isEmptyList

    override val isCons: Boolean
        get() = Cons.FUNCTOR == functor && arity == 2

    override val isSet: Boolean
        get() = (Set.FUNCTOR == functor && arity == 1) || isEmptySet

    override val isEmptySet: Boolean
        get() = Empty.EMPTY_SET_FUNCTOR == functor && arity == 0

    override val isEmptyList: Boolean
        get() = Empty.EMPTY_LIST_FUNCTOR == functor && arity == 0

    override val isTrue: Boolean
        get() = isAtom && Truth.TRUE_FUNCTOR == functor

    override val isFail: Boolean
        get() = isAtom && Truth.FAIL_FUNCTOR == functor

    override val isIndicator: Boolean
        get() = Indicator.FUNCTOR == functor && arity == 2

    override val variables: Sequence<Var>
        get() = argsSequence.flatMap { it.variables }

    override fun freshCopy(): Struct

    override fun freshCopy(scope: Scope): Struct

    @JsName("append")
    fun append(argument: Term): Struct = addLast(argument)

    @JsName("addLast")
    fun addLast(argument: Term): Struct

    @JsName("addFirst")
    fun addFirst(argument: Term): Struct

    @JsName("insertAt")
    fun insertAt(index: Int, argument: Term): Struct

    @JsName("setFunctor")
    fun setFunctor(functor: String): Struct

    @JsName("functor")
    val functor: String

    @JsName("isFunctorWellFormed")
    val isFunctorWellFormed: Boolean

    @JsName("args")
    val args: Array<Term>

    @JsName("arity")
    val arity: Int
        get() = args.size

    @JsName("indicator")
    val indicator: Indicator
        get() = Indicator.of(functor, arity)

    @JsName("argsList")
    val argsList: KtList<Term>
        get() = listOf(*args)

    @JsName("argsSequence")
    val argsSequence: Sequence<Term>
        get() = sequenceOf(*args)

    @JsName("getArgAt")
    fun getArgAt(index: Int): Term = args[index]

    @JsName("get")
    operator fun get(index: Int): Term = getArgAt(index)

    companion object {

        /** The pattern of a well-formed functor for a Struct */
        @JvmField
        val WELL_FORMED_FUNCTOR_PATTERN =
            """^[a-z][A-Za-z_0-9]*$""".toRegex()

        @JvmField
        val NON_PRINTABLE_CHARACTER_PATTERN =
            """[\t\n\r'"\\]""".toRegex()

        @JvmStatic
        @JsName("isWellFormedFunctor")
        fun isWellFormedFunctor(string: String): Boolean =
            WELL_FORMED_FUNCTOR_PATTERN.matches(string)

        @JvmStatic
        @JsName("quoteFunctor")
        fun enquoteFunctor(string: String): String =
            "'$string'"

        @JvmStatic
        @JsName("quoteFunctorIfNecessary")
        fun enquoteFunctorIfNecessary(string: String): String =
            if (isWellFormedFunctor(string)) {
                string
            } else {
                enquoteFunctor(string)
            }

        @JvmStatic
        @JsName("functorNeedsEscape")
        fun functorNeedsEscape(string: String): Boolean =
            NON_PRINTABLE_CHARACTER_PATTERN.containsMatchIn(string)

        @JvmStatic
        @JsName("escapeFunctor")
        fun escapeFunctor(
            string: String,
            escapeSingleQuotes: Boolean = true,
            escapeDoubleQuotes: Boolean = !escapeSingleQuotes
        ): String = string.toCharArray()
            .asSequence()
            .map { RegexUtils.escapeChar(it, escapeSingleQuotes, escapeDoubleQuotes) }
            .reduceOrNull(String::plus)
            ?: ""

        @JvmStatic
        @JsName("escapeFunctorIfNeeded")
        fun escapeFunctorIfNecessary(
            string: String,
            escapeSingleQuotes: Boolean = true,
            escapeDoubleQuotes: Boolean = !escapeSingleQuotes
        ): String = if (functorNeedsEscape(string)) {
            escapeFunctor(string, escapeSingleQuotes, escapeDoubleQuotes)
        } else {
            string
        }

        @JvmStatic
        @JsName("template")
        fun template(functor: String, arity: Int): Struct {
            return of(functor, (0 until arity).map { Var.anonymous() })
        }

        @JvmStatic
        @JsName("ofList")
        fun of(functor: String, args: KtList<Term>): Struct =
            when {
                args.size == 2 && Cons.FUNCTOR == functor -> Cons.of(args.first(), args.last())
                args.size == 2 && Clause.FUNCTOR == functor && args.first() is Struct ->
                    Rule.of(args.first() as Struct, args.last())
                args.size == 2 && Tuple.FUNCTOR == functor -> Tuple.of(args)
                args.size == 2 && Indicator.FUNCTOR == functor -> Indicator.of(args.first(), args.last())
                args.size == 1 && Set.FUNCTOR == functor -> Set.of(args)
                args.size == 1 && Clause.FUNCTOR == functor -> Directive.of(args.first())
                args.isEmpty() -> Atom.of(functor)
                else -> StructImpl(functor, args.toTypedArray(), emptyMap())
            }

        @JvmStatic
        @JsName("of")
        fun of(functor: String, vararg args: Term): Struct = of(functor, args.toList())

        @JvmStatic
        @JsName("ofSequence")
        fun of(functor: String, args: Sequence<Term>): Struct = of(functor, args.toList())

        @JvmStatic
        @JsName("ofIterable")
        fun of(functor: String, args: Iterable<Term>): Struct = of(functor, args.toList())

        @JvmStatic
        @JsName("foldListNullTerminated")
        fun fold(operator: String, terms: KtList<Term>): Struct =
            fold(operator, terms, null)

        @JvmStatic
        @JsName("foldList")
        fun fold(operator: String, terms: KtList<Term>, terminal: Term?): Struct =
            when {
                operator == Cons.FUNCTOR && terminal == EmptyList() -> List.of(terms)
                operator == Cons.FUNCTOR && terminal === null ->
                    List.from(terms.slice(0 until terms.lastIndex), terms.last())
                operator == Tuple.FUNCTOR -> Tuple.of(terms + listOfNotNull(terminal))
                terminal === null -> {
                    require(terms.size >= 2) { "Struct requires at least two terms to fold" }
                    terms.slice(0 until terms.lastIndex - 1)
                        .foldRight(of(operator, terms[terms.lastIndex - 1], terms[terms.lastIndex])) { a, b ->
                            of(operator, a, b)
                        }
                }
                else -> {
                    require(terms.isNotEmpty()) { "Struct requires at least two terms to fold" }
                    terms.slice(0 until terms.lastIndex)
                        .foldRight(of(operator, terms[terms.lastIndex], terminal)) { a, b ->
                            of(operator, a, b)
                        }
                }
            }

        @JvmStatic
        @JsName("foldSequence")
        fun fold(operator: String, terms: Sequence<Term>, terminal: Term?): Struct =
            fold(operator, terms.toList(), terminal)

        @JvmStatic
        @JsName("foldSequenceNullTerminated")
        fun fold(operator: String, terms: Sequence<Term>): Struct =
            fold(operator, terms, null)

        @JvmStatic
        @JsName("foldIterable")
        fun fold(operator: String, terms: Iterable<Term>, terminal: Term?): Struct =
            fold(operator, terms.toList(), terminal)

        @JvmStatic
        @JsName("foldIterableNullTerminated")
        fun fold(operator: String, terms: Iterable<Term>): Struct =
            fold(operator, terms, null)

        @JvmStatic
        @JsName("fold")
        fun fold(operator: String, vararg terms: Term, terminal: Term?): Struct =
            fold(operator, terms.toList(), terminal)

        @JvmStatic
        @JsName("foldNullTerminated")
        fun fold(operator: String, vararg terms: Term): Struct =
            fold(operator, listOf(*terms))
    }
}
