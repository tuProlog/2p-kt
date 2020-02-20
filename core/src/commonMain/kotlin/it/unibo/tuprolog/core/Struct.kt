package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.StructImpl
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

    override fun freshCopy(): Struct = super.freshCopy() as Struct

    override fun freshCopy(scope: Scope): Struct = when {
        isGround -> this
        else -> scope.structOf(functor, argsSequence.map { it.freshCopy(scope) })
    }

    val functor: String

    val isFunctorWellFormed: Boolean

    val args: Array<Term>

    val arity: Int
        get() = args.size

    val indicator: Indicator
        get() = Indicator.of(functor, arity)

    val argsList: KtList<Term>
        get() = listOf(*args)

    val argsSequence: Sequence<Term>
        get() = sequenceOf(*args)

    fun getArgAt(index: Int): Term = args[index]

    operator fun get(index: Int): Term = getArgAt(index)

    companion object {

        /** The pattern of a well-formed functor for a Struct */
        @JvmField
        val STRUCT_FUNCTOR_REGEX_PATTERN = """^[a-z][A-Za-z_0-9]*$""".toRegex()

        @JvmStatic
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
                else -> StructImpl(functor, args.toTypedArray())
            }

        @JvmStatic
        fun of(functor: String, vararg args: Term): Struct = of(functor, args.toList())

        @JvmStatic
        fun of(functor: String, args: Sequence<Term>): Struct = of(functor, args.toList())

        @JvmStatic
        fun fold(operator: String, terms: KtList<Term>, terminal: Term? = null): Struct =
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
        fun fold(operator: String, terms: Sequence<Term>, terminal: Term? = null): Struct =
            fold(operator, terms.toList(), terminal)

        @JvmStatic
        fun fold(operator: String, terms: Iterable<Term>, terminal: Term? = null): Struct =
            fold(operator, terms.toList(), terminal)

        @JvmStatic
        fun fold(operator: String, vararg terms: Term, terminal: Term? = null): Struct =
            fold(operator, terms.toList(), terminal)

    }
}
