package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.CoupleImpl
import it.unibo.tuprolog.core.List as LogicList

interface Couple : Struct, LogicList {

    override val isCouple: Boolean
        get() = true

    override val isEmptyList: Boolean
        get() = false

    val head: Term

    val tail: Term

    override val functor: String
        get() = FUNCTOR

    override val args: Array<Term>
        get() = arrayOf(head, tail)

    override val arity: Int
        get() = 2

    override fun toArray(): Array<Term> = toList().toTypedArray()

    override fun toSequence(): Sequence<Term> =
            args.asSequence().flatMap {
                if (tail.isList) {
                    sequenceOf(it) + tail.castTo<LogicList>().toSequence()
                } else {
                    sequenceOf(it)
                }
            }

    override fun toList(): List<Term> = toSequence().toList()

    companion object {
        const val FUNCTOR = "."

        fun of(head: Term, tail: Term): Couple = CoupleImpl(head, tail)

        fun last(head: Term): Couple = Couple.of(head, Empty.list())
    }

    //    @Override
    //    default String toJava(boolean inline) {
    //        if (inline) {
    //            return Couple.class.getSimpleName()
    //                    + ".of("
    //                    + streamArgs().map(it -> it.toJava(inline)).collect(Collectors.joining(", "))
    //                    + ")";
    //        } else {
    //            return Couple.class.getSimpleName()
    //                    + ".of(\n    "
    //                    + streamArgs().map(it -> it.toJava(inline))
    //                        .map(it -> it.replace("\n", "\n    "))
    //                        .collect(Collectors.joining(",\n    "))
    //                    + "\n)";
    //        }
    //    }
}
