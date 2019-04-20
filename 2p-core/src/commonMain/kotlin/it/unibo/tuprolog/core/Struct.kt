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
        val WELL_FORMED_FUNCTOR_PATTERN = Regex.fromLiteral("""[a-z][A-Za-z_0-9]*""")

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
    }
}
