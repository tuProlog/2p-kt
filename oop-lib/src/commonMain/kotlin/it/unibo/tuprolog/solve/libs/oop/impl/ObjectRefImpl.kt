package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.MethodInvocationException
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

class ObjectRefImpl(override val `object`: Any) : ObjectRef, Atom by Atom.of(nameOf(`object`))  {
    companion object {
        private fun nameOf(any: Any): String = "<object:${any::class.qualifiedName}#${any.hashCode()}>"
    }

    override fun invoke(methodName: String, arguments: List<Term>): Result {
        val converter = TermToObjectConverter.default
        val methodRef = findMethod(methodName, arguments.map { converter.admissibleTypes(it) })
//        methodRef.parameters[0].type.classifier
        return Result.None
    }

    private fun findMethod(methodName: String, admissibleTypes: List<Set<KClass<*>>>): KCallable<*> =
        `object`::class.members.filter { it.name == methodName }
            .firstOrNull {
                it.parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.match(admissibleTypes)
            } ?: throw MethodInvocationException(this, methodName)

    private fun List<KParameter>.match(types: List<Set<KClass<*>>>): Boolean {
        if (size != types.size) return false
        for (i in this.indices) {
            val possible = types[i]
            when(val formal = this[i].type.classifier) {
                is KClass<*> -> {
                    if (possible.none { formal.isSupertypeOf(it) }) return false
                }
                else -> return false
            }
        }
        return true
    }

    private fun KClass<*>.isSupertypeOf(other: KClass<*>): Boolean =
        (sequenceOf(this) + supertypes.asSequence()
            .map { it.classifier }
            .filterIsInstance<KClass<*>>()).any { it == other }
}