package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOP
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.reflect.KClass

class Alias private constructor(
    val alias: Struct,
    val ref: Ref,
) : RuleWrapper<ExecutionContext>(FUNCTOR, 2) {
    companion object {
        fun forObject(
            alias: String,
            `object`: Any?,
        ) = Alias(Atom.of(alias), ObjectRef.of(`object`))

        fun forType(
            alias: String,
            type: KClass<*>,
        ) = Alias(Atom.of(alias), TypeRef.of(type))

        fun of(
            alias: Struct,
            ref: Ref,
        ) = Alias(alias, ref)

        const val FUNCTOR = OOP.ALIAS_FUNCTOR
    }

    init {
        require(alias.isGround) {
            "Alias must be a ground term, got: $alias"
        }
    }

    override val Scope.head: List<Term>
        get() = sequenceOf(alias, ref).toList()
}
