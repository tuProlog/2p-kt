package it.unibo.tuprolog.core

import kotlin.collections.List

interface Struct : Term {

    override val isStruct: Boolean
        get() = true

    val isFunctorWellFormed: Boolean

    override val isAtom: Boolean
        get() = arity == 0

    override val isList: Boolean
        get() = isCouple || isEmptyList

    override val isCouple: Boolean
        get() = Couple.FUNCTOR == functor && arity == 2

    override val isEmptyList: Boolean
        get() = Empty.EMPTY_LIST_FUNCTOR == functor && arity == 0

    override val isGround: Boolean
        get() = args.all { it.isGround }

    override fun clone(): Term {
        return if (isGround) {
            this
        } else {
            Struct.of(functor, args.map { it.clone() })
        }
    }

    val functor: String

    val args: Array<Term>

    val arity: Int
        get() = args.size

    val argsList: List<Term>
        get() = listOf(*args)

    val argsSequence: Sequence<Term>
        get() = sequenceOf(*args)

    fun getArgAt(index: Int): Term {
        return args[index]
    }

    operator fun get(index: Int): Term {
        return getArgAt(index)
    }

    companion object {
        val WELL_FORMED_FUNCTOR_PATTERN = Regex("""[a-z][A-Za-z_0-9]*""")

        fun of(functor: String, arg1: Term, vararg args: Term): Struct {
            return if (args.size == 1 && Empty.EMPTY_LIST_FUNCTOR == functor) {
                Couple.of(arg1, args[0])
            } else {
                StructImpl(functor, arrayOf(arg1) + args)
            }
        }

        fun of(functor: String, args: List<Term>): Struct {
            if (args.size == 2 && Couple.FUNCTOR == functor) {
                return Couple.of(args[0], args[1])
            } else if (args.isEmpty()) {
                return Atom.of(functor)
            }
            return StructImpl(functor, args.toTypedArray())
        }

        fun of(functor: String, args: Sequence<Term>): Struct {
            return Struct.of(functor, args.toList())
        }

        fun fold(operator: String, terms: List<Term>, terminal: Term? = null): Struct {
            return if (terminal === null) {
                terms.slice(0 until terms.lastIndex - 1)
                        .foldRight(structOf(operator, terms[terms.lastIndex - 1], terms[terms.lastIndex - 1])) {
                            a, b -> structOf(operator, a, b)
                        }
            } else {
                terms.slice(0 until terms.lastIndex)
                        .foldRight(structOf(operator, terms[terms.lastIndex], terminal)) {
                            a, b -> structOf(operator, a, b)
                        }
            }
        }

        fun fold(operator: String, terms: Sequence<Term>, terminal: Term? = null): Struct {
            return fold(operator, terms.toList(), terminal)
        }

        fun fold(operator: String, terms: Iterable<Term>, terminal: Term? = null): Struct {
            return fold(operator, terms.toList(), terminal)
        }

        fun fold(operator: String, vararg terms: Term, terminal: Term? = null): Struct {
            return fold(operator, terms.toList(), terminal)
        }

        fun conjunction(terms: Sequence<Term>): Struct {
            return fold(",", terms)
        }

        fun conjunction(terms: Iterable<Term>): Struct {
            return fold(",", terms)
        }

        fun conjunction(terms: List<Term>): Struct {
            return fold(",", terms)
        }

        fun conjunction(vararg terms: Term): Struct {
            return fold(",", *terms)
        }

    }
}
