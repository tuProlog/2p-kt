package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.companionObjectRef
import it.unibo.tuprolog.solve.libs.oop.invoke
import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class TypeRefImpl(override val type: KClass<*>) : TypeRef, Atom by Atom.of(nameOf(type)) {
    companion object {
        private fun nameOf(type: KClass<*>): String = "<type:${type.qualifiedName}>"
    }

    override fun invoke(methodName: String, arguments: List<Term>): Result =
        when (val companionObjectRef = type.companionObjectRef) {
            is Optional.Some<out Any> -> companionObjectRef.value.invoke(methodName, arguments)
            else -> type.invoke(methodName, arguments)
        }

    override val isConstant: Boolean
        get() = true

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    override fun <T : Term> `as`(): T = this as T

    override fun <T : Term> castTo(): T = this as T

    override fun apply(substitution: Substitution): Term = this

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun get(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun <T> accept(visitor: TermVisitor<T>): T =
        visitor.visit(this)
}