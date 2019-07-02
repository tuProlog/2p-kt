package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.StructImpl
import kotlin.collections.List as KtList

interface Struct : Term {

    override val isStruct: Boolean
        get() = true

    val isFunctorWellFormed: Boolean

    override val isClause: Boolean
        get() = Clause.FUNCTOR == functor

    override val isRule: Boolean
        get() = isClause && arity == 2

    override val isDirective: Boolean
        get() = isClause && arity == 1

    override val isFact: Boolean
        get() = isRule && args[1].isTrue

    override val isConjunction: Boolean
        get() = functor == Conjunction.FUNCTOR && arity == 2

    override val isAtom: Boolean
        get() = arity == 0

    override val isList: Boolean
        get() = isCouple || isEmptyList

    override val isCouple: Boolean
        get() = Couple.FUNCTOR == functor && arity == 2

    override val isSet: Boolean
        get() = Set.FUNCTOR == functor || isEmptySet

    override val isEmptySet: Boolean
        get() = Empty.EMPTY_SET_FUNCTOR == functor && arity == 0

    override val isEmptyList: Boolean
        get() = Empty.EMPTY_LIST_FUNCTOR == functor && arity == 0

    override val isGround: Boolean
        get() = args.all { it.isGround }

    override val isTrue: Boolean
        get() = isAtom && Truth.TRUE_FUNCTOR == functor

    override val isFail: Boolean
        get() = isAtom && Truth.FAIL_FUNCTOR == functor

    override fun freshCopy(): Term = super.freshCopy() as Struct

    override fun freshCopy(scope: Scope): Struct =
            if (isGround) {
                this
            } else {
                scope.structOf(functor, argsSequence.map { it.freshCopy(scope) })
            }

    val functor: String

    val args: Array<Term>

    val arity: Int
        get() = args.size

    val argsList: KtList<Term>
        get() = listOf(*args)

    val argsSequence: Sequence<Term>
        get() = sequenceOf(*args)

    fun getArgAt(index: Int): Term = args[index]

    operator fun get(index: Int): Term = getArgAt(index)

    companion object {
        val WELL_FORMED_FUNCTOR_PATTERN = Regex("""^[a-z][A-Za-z_0-9]*$""")

        fun of(functor: String, vararg args: Term): Struct = of(functor, args.toList())

        fun of(functor: String, args: KtList<Term>): Struct =
                when {
                    args.size == 2 && Couple.FUNCTOR == functor -> Couple.of(args[0], args[1])
                    args.size == 2 && Clause.FUNCTOR == functor && args[0] is Struct -> Rule.of(args[0] as Struct, args[1])
                    args.size == 2 && Set.FUNCTOR == functor -> Set.of(args)
                    args.size == 2 && Conjunction.FUNCTOR == functor -> Conjunction.of(args)
                    args.size == 1 && Clause.FUNCTOR == functor -> Directive.of(args[0])
                    args.isEmpty() -> Atom.of(functor)
                    else -> StructImpl(functor, args.toTypedArray())
                }

        fun of(functor: String, args: Sequence<Term>): Struct = of(functor, args.toList())

        fun fold(operator: String, terms: KtList<Term>, terminal: Term? = null): Struct =
                if (terminal === null) {
                    terms.slice(0 until terms.lastIndex - 1)
                            .foldRight(structOf(operator, terms[terms.lastIndex - 1], terms[terms.lastIndex])) { a, b ->
                                structOf(operator, a, b)
                            }
                } else {
                    terms.slice(0 until terms.lastIndex)
                            .foldRight(structOf(operator, terms[terms.lastIndex], terminal)) { a, b ->
                                structOf(operator, a, b)
                            }
                }

        fun fold(operator: String, terms: Sequence<Term>, terminal: Term? = null): Struct =
                fold(operator, terms.toList(), terminal)

        fun fold(operator: String, terms: Iterable<Term>, terminal: Term? = null): Struct =
                fold(operator, terms.toList(), terminal)

        fun fold(operator: String, vararg terms: Term, terminal: Term? = null): Struct =
                fold(operator, terms.toList(), terminal)

        fun conjunction(terms: Sequence<Term>): Term = conjunction(terms.toList())

        fun conjunction(terms: Iterable<Term>): Term = conjunction(terms.toList())

        fun conjunction(terms: KtList<Term>): Term =
                when {
                    terms.isEmpty() -> throw IllegalArgumentException("At least one term should be provided as input")
                    terms.size == 1 -> terms[0]
                    else -> fold(",", terms)
                }

        fun conjunction(vararg terms: Term): Term = conjunction(listOf(*terms))

    }
}
