package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.ObjectRef.Companion.nameOf
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import it.unibo.tuprolog.solve.libs.oop.assign
import it.unibo.tuprolog.solve.libs.oop.invoke

@Suppress("UNCHECKED_CAST")
internal class ObjectRefImpl(
    override val `object`: Any,
) : ObjectRef,
    Atom by Atom.of(nameOf(`object`)) {
    override val isConstant: Boolean
        get() = true

    override fun asConstant(): Constant = this

    override fun invoke(
        objectConverter: TermToObjectConverter,
        methodName: String,
        arguments: List<Term>,
    ): Result = `object`.invoke(objectConverter, methodName, arguments)

    override fun assign(
        objectConverter: TermToObjectConverter,
        propertyName: String,
        value: Term,
    ): Boolean {
        `object`.assign(objectConverter, propertyName, value)
        return true
    }

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
}
