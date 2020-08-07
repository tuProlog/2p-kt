package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.MethodInvocationException
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import it.unibo.tuprolog.solve.libs.oop.TypeUtils.isSupertypeOf
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
        val argumentsExpectedTypes = methodRef.actualParameterTypes
        require(argumentsExpectedTypes.size == arguments.size) {
            """
            |
            |Error while invoking ${methodRef.name} the expected argument types 
            |   ${argumentsExpectedTypes.map { it.simpleName }} 
            |are not as many as the as the actual parameters (${argumentsExpectedTypes.size} vs. ${arguments.size}):
            |   $arguments
            |
            """.trimMargin()
        }
        val result = methodRef.call(`object`, *arguments.mapIndexed { i, it ->
            converter.convertInto(argumentsExpectedTypes[i], it)
        }.toTypedArray())
        return Result.Value(result)
    }

    private val KCallable<*>.actualParameterTypes: List<KClass<*>>
        get() = parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.map { it.type.classifier as KClass<*> }

    private fun findMethod(methodName: String, admissibleTypes: List<Set<KClass<*>>>): KCallable<*> =
        `object`::class.members.filter { it.name == methodName }
            .firstOrNull { method ->
                method.parameters.filterNot { it.kind == KParameter.Kind.INSTANCE }.match(admissibleTypes)
            } ?: throw MethodInvocationException(this, methodName)

    private fun List<KParameter>.match(types: List<Set<KClass<*>>>): Boolean {
        if (size != types.size) return false
        for (i in this.indices) {
            val possible = types[i]
            when(val formal = this[i].type.classifier) {
                is KClass<*> -> {
                    if (possible.none { formal isSupertypeOf it }) return false
                }
                else -> return false
            }
        }
        return true
    }
}