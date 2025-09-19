package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.assign
import it.unibo.tuprolog.solve.libs.oop.companionObjectRef
import it.unibo.tuprolog.solve.libs.oop.create
import it.unibo.tuprolog.solve.libs.oop.fullName
import it.unibo.tuprolog.solve.libs.oop.invoke
import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class TypeRefImpl(
    override val type: KClass<*>,
) : TypeRef,
    Atom by Atom.of(nameOf(type)) {
    companion object {
        private fun nameOf(type: KClass<*>): String = "<type:${type.fullName}>"
    }

    override fun create(
        objectConverter: TermToObjectConverter,
        arguments: List<Term>,
    ): Result = type.create(objectConverter, arguments)

    override fun invoke(
        objectConverter: TermToObjectConverter,
        methodName: String,
        arguments: List<Term>,
    ): Result =
        when (val companionObjectRef = type.companionObjectRef) {
            is Optional.Some<out Any> -> companionObjectRef.value.invoke(objectConverter, methodName, arguments)
            else -> type.invoke(objectConverter, methodName, arguments, null)
        }

    override val isConstant: Boolean
        get() = true

    override fun asConstant(): Constant = this

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    override fun <T : Term> `as`(): T? = this as? T

    override fun <T : Term> castTo(): T = this as T

    override fun apply(substitution: Substitution): Term = this

    override fun apply(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): Term = this

    override fun get(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): Term = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitAtom(this)

    override fun assign(
        objectConverter: TermToObjectConverter,
        propertyName: String,
        value: Term,
    ): Boolean {
        when (val companionObjectRef = type.companionObjectRef) {
            is Optional.Some<out Any> -> companionObjectRef.value.assign(objectConverter, propertyName, value)
            else -> type.assign(objectConverter, propertyName, value, null)
        }
        return true
    }
}
