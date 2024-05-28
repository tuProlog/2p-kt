package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.solve.libs.oop.Objectifier
import it.unibo.tuprolog.solve.libs.oop.OverloadSelector
import it.unibo.tuprolog.solve.libs.oop.Result
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.catchingPlatformSpecificException
import it.unibo.tuprolog.solve.libs.oop.formalParameterTypes
import it.unibo.tuprolog.solve.libs.oop.invoke
import it.unibo.tuprolog.solve.libs.oop.setterMethod
import it.unibo.tuprolog.utils.name
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

internal abstract class AbstractDecoratorImpl(
    protected open val ref: Any,
    override val termificator: Termificator,
    override val objectifier: Objectifier,
    override val typeFactory: TypeFactory,
) : BaseOOPContext(termificator, objectifier, typeFactory) {
    private fun KCallable<*>.ensureArgumentsListIsOfSize(actualArguments: List<Term>): List<KClass<*>> {
        return formalParameterTypes.also { formalArgumentsTypes ->
            require(formalParameterTypes.size == actualArguments.size) {
                """
            |
            |Error while invoking $name the expected argument types 
            |   ${formalArgumentsTypes.map { it.name }} 
            |are not as many as the as the actual parameters (${formalArgumentsTypes.size} vs. ${actualArguments.size}):
            |   $actualArguments
            |
            """.trimMargin()
            }
        }
    }

    protected fun Any.invoke(
        method: String,
        arguments: List<Term>,
    ): Result = this::class.invoke(method, arguments, this)

    private fun KClass<*>.invoke(
        method: String,
        arguments: List<Term>,
        instance: Any?,
    ): Result {
        val methodRef = OverloadSelector.of(this, objectifier).findMethod(method, arguments)
        return methodRef.callWithPrologArguments(arguments, instance)
    }

    private fun KCallable<*>.callWithPrologArguments(
        arguments: List<Term>,
        instance: Any? = null,
    ): Result {
        val formalArgumentsTypes = ensureArgumentsListIsOfSize(arguments)
        val args =
            arguments.mapIndexed { i, it ->
                objectifier.convertInto(formalArgumentsTypes[i], it)
            }.toTypedArray()
        return catchingPlatformSpecificException(instance) {
            val result = invoke(instance, *args)
            Result.Value(result, termificator)
        }
    }

    protected fun Any.assign(
        property: String,
        value: Term,
    ): Result = this::class.assign(property, value, this)

    private fun KClass<*>.assign(
        property: String,
        value: Term,
        instance: Any?,
    ): Result {
        val setterRef = OverloadSelector.of(this, objectifier).findProperty(property, value).setterMethod
        return setterRef.callWithPrologArguments(listOf(value), instance)
    }

    protected fun KClass<*>.create(
        arguments: List<Term>,
    ): Result {
        val constructorRef = OverloadSelector.of(this, objectifier).findConstructor(arguments)
        return constructorRef.callWithPrologArguments(arguments)
    }
}
