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

/**
 * A single `alias(Alias, Ref)` fact, associating the ground term [alias] with the
 * [it.unibo.tuprolog.solve.libs.oop.Ref] [ref] it names -- the mechanism backing `$Alias`
 * dealiasing expressions and the default aliases (`string`, `int`, `system`, ...) `OOPLib`
 * registers. New aliases can be added at solve-time via `register/2`
 * ([it.unibo.tuprolog.solve.libs.oop.primitives.Register]) and removed via `unregister/1`
 * ([it.unibo.tuprolog.solve.libs.oop.primitives.Unregister]); [forObject] and [forType] are the
 * two convenience factories used to build the library's own default aliases.
 *
 * @see it.unibo.tuprolog.solve.libs.oop.OOPLib
 */
class Alias private constructor(
    val alias: Struct,
    val ref: Ref,
) : RuleWrapper<ExecutionContext>(FUNCTOR, 2) {
    companion object {
        /** Builds an [Alias] fact naming [object] (wrapped into an [ObjectRef]) as [alias]. */
        fun forObject(
            alias: String,
            `object`: Any?,
        ) = Alias(Atom.of(alias), ObjectRef.of(`object`))

        /** Builds an [Alias] fact naming [type] (wrapped into a [TypeRef]) as [alias]. */
        fun forType(
            alias: String,
            type: KClass<*>,
        ) = Alias(Atom.of(alias), TypeRef.of(type))

        /** Builds an [Alias] fact naming [ref] as the (ground) term [alias]. */
        fun of(
            alias: Struct,
            ref: Ref,
        ) = Alias(alias, ref)

        /** The functor of `alias/2` facts, i.e. `"alias"`. */
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
