@file:JvmName("TypeUtils")

package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional
import it.unibo.tuprolog.utils.indexed
import kotlin.jvm.JvmName
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty

private val PRIMITIVE_TYPES =
    setOf(
        Long::class,
        Int::class,
        Short::class,
        Byte::class,
        Char::class,
        Double::class,
        Float::class,
    )

internal const val ID = "[a-zA-Z_][a-zA-Z0-9_]*"

/**
 * A per-platform, stable-for-the-object's-lifetime identity string (e.g. the hex identity hash
 * code on the JVM), used to keep the Prolog rendering of two distinct [ObjectRef]s over `equal`
 * objects distinct (see [ObjectRef.nameOf]).
 */
expect val Any.identifier: String

/**
 * The pattern a fully-qualified type name must match to be looked up by [kClassFromName] (and,
 * transitively, by [TypeFactory.default]) -- e.g. `java.util.ArrayList` on the JVM, or
 * `<module>:<qualified name>` on JS.
 */
expect val CLASS_NAME_PATTERN: Regex

/**
 * This [KClass]'s companion object instance, or its Kotlin `object` singleton instance if it is
 * one itself -- used by [it.unibo.tuprolog.solve.libs.oop.impl.TypeRefImpl] so that invoking a
 * member on a [TypeRef] reaches the companion/singleton rather than requiring a `null` instance.
 *
 * Always [Optional.none] on Kotlin/JS, which exposes no reflective access to companion objects.
 */
expect val KClass<*>.companionObjectRef: Optional<out Any>

/** The [KClass] of [companionObjectRef], if any. Always [Optional.none] on Kotlin/JS. */
expect val KClass<*>.companionObjectType: Optional<out KClass<*>>

/**
 * Resolves [qualifiedName] (which must match [CLASS_NAME_PATTERN]) into the [KClass] it names,
 * or [Optional.none] if no such type can be found. Backs [TypeFactory.default].
 *
 * On the JVM this also recognizes Kotlin's own standard-library type names (via an internal
 * name table) before falling back to `Class.forName`, retrying with `.`-to-`$` substitutions to
 * find nested classes; results are cached. On Kotlin/JS, [qualifiedName] must be of the form
 * `<module>:<qualified name>` and is resolved via Node's `require`, so this only works in a
 * Node.js-hosted environment.
 *
 * @throws IllegalArgumentException if [qualifiedName] does not match [CLASS_NAME_PATTERN].
 */
expect fun kClassFromName(qualifiedName: String): Optional<out KClass<*>>

internal expect fun <T> KCallable<*>.catchingPlatformSpecificException(
    instance: Any?,
    action: () -> T,
): T

/**
 * Every supertype (superclass and implemented interface, transitively) of this [KClass], plus
 * this [KClass] itself unless [strict] is `true`.
 *
 * @throws NotImplementedError on Kotlin/JS, where reflective access to supertypes is unsupported.
 */
expect fun KClass<*>.allSupertypes(strict: Boolean): Sequence<KClass<*>>

/**
 * The formal parameter types of this callable (constructor, method, or property getter/setter),
 * excluding the implicit instance receiver -- used to score how well a list of Prolog arguments
 * matches a candidate overload (see [OverloadSelector]).
 *
 * @throws NotImplementedError on Kotlin/JS, where reflective access to parameter types is unsupported.
 */
expect val KCallable<*>.formalParameterTypes: List<KClass<*>>

/**
 * This [KClass]'s fully-qualified name (e.g. `java.util.ArrayList`), as used to render
 * [TypeRef] and [ObjectRef] atoms and in exception messages.
 *
 * @throws NotImplementedError on Kotlin/JS, where reflective access to qualified names is unsupported.
 */
expect val KClass<*>.fullName: String

/**
 * This [KClass]'s simple (unqualified) name, as returned by the `type/2` predicate
 * ([it.unibo.tuprolog.solve.libs.oop.primitives.Type]) when converting a [TypeRef] to its name.
 *
 * @throws NotImplementedError on Kotlin/JS, where reflective access to type names is unsupported.
 */
expect val KClass<*>.name: String

/**
 * A human-readable rendering of this callable's name, parameters and return type, used in
 * [it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException] and
 * [it.unibo.tuprolog.solve.libs.oop.exceptions.RuntimePermissionException] messages.
 *
 * @throws NotImplementedError on Kotlin/JS, where reflective access to this metadata is unsupported.
 */
expect fun KCallable<*>.pretty(): String

/**
 * Invokes this callable (constructor, method, or property getter/setter) on [instance] (`null`
 * for a constructor, a static member, or a companion-object member reached through
 * [companionObjectRef]) with the given [args], returning whatever it returns.
 *
 * @throws NotImplementedError on Kotlin/JS, where reflective invocation is unsupported.
 */
expect fun <T> KCallable<T>.invoke(
    instance: Any?,
    vararg args: Any?,
): T

/** The setter of this mutable property, as a [KFunction], used to actually perform an [Ref.assign]. */
expect val <T> KMutableProperty<T>.setterMethod: KFunction<Unit>

internal expect fun overloadSelector(
    type: KClass<*>,
    termToObjectConverter: TermToObjectConverter,
): OverloadSelector

/** Whether this [KClass] is one of Kotlin's primitive numeric/character types. */
val KClass<*>.isPrimitiveType: Boolean get() = this in PRIMITIVE_TYPES

/** Whether this [KClass] is a (non-strict, by default) supertype of [other]. */
infix fun KClass<*>.isSupertypeOf(other: KClass<*>): Boolean = isSupertypeOf(other, false)

/**
 * Whether this [KClass] is a supertype of [other] -- strictly (excluding `other == this`) if
 * [strict] is `true`.
 *
 * @throws NotImplementedError on Kotlin/JS (see [allSupertypes]).
 */
fun KClass<*>.isSupertypeOf(
    other: KClass<*>,
    strict: Boolean,
): Boolean = other.allSupertypes(strict).any { it == this }

/**
 * How many steps up [other]'s supertype hierarchy this [KClass] is found at (`0` if `other == this`),
 * or `null` if this is not a supertype of [other] at all. Used to rank overload candidates: the
 * closer a formal parameter type is to an argument's actual type, the better the match.
 *
 * @throws NotImplementedError on Kotlin/JS (see [allSupertypes]).
 */
fun KClass<*>.superTypeDistance(other: KClass<*>): Int? =
    other
        .allSupertypes(
            false,
        ).indexed()
        .firstOrNull { (_, it) -> it == this }
        ?.index

/** Whether this [KClass] is a (non-strict, by default) subtype of [other]. */
infix fun KClass<*>.isSubtypeOf(other: KClass<*>): Boolean = isSubtypeOf(other, false)

/**
 * Whether this [KClass] is a subtype of [other] -- strictly (excluding `other == this`) if
 * [strict] is `true`.
 *
 * @throws NotImplementedError on Kotlin/JS (see [allSupertypes]).
 */
fun KClass<*>.isSubtypeOf(
    other: KClass<*>,
    strict: Boolean,
): Boolean = other.isSupertypeOf(this, strict)

/** Like [superTypeDistance], with the roles of `this` and [other] swapped. */
fun KClass<*>.subTypeDistance(other: KClass<*>): Int? = other.superTypeDistance(this)

internal fun Any.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: List<Term>,
): Result = this::class.invoke(objectConverter, methodName, arguments, this)

private fun KCallable<*>.ensureArgumentsListIsOfSize(actualArguments: List<Term>): List<KClass<*>> =
    formalParameterTypes.also { formalArgumentsTypes ->
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

internal fun KClass<*>.invoke(
    objectConverter: TermToObjectConverter,
    methodName: String,
    arguments: List<Term>,
    instance: Any?,
): Result {
    val methodRef = OverloadSelector.of(this, objectConverter).findMethod(methodName, arguments)
    return methodRef.callWithPrologArguments(objectConverter, arguments, instance)
}

private fun KCallable<*>.callWithPrologArguments(
    converter: TermToObjectConverter,
    arguments: List<Term>,
    instance: Any? = null,
): Result {
    val formalArgumentsTypes = ensureArgumentsListIsOfSize(arguments)
    val args =
        arguments
            .mapIndexed { i, it ->
                converter.convertInto(formalArgumentsTypes[i], it)
            }.toTypedArray()
    return catchingPlatformSpecificException(instance) {
        val result = invoke(instance, *args)
        Result.Value(result)
    }
}

internal fun Any.assign(
    objectConverter: TermToObjectConverter,
    propertyName: String,
    value: Term,
): Result = this::class.assign(objectConverter, propertyName, value, this)

internal fun KClass<*>.assign(
    objectConverter: TermToObjectConverter,
    propertyName: String,
    value: Term,
    instance: Any?,
): Result {
    val setterRef = OverloadSelector.of(this, objectConverter).findProperty(propertyName, value).setterMethod
    return setterRef.callWithPrologArguments(objectConverter, listOf(value), instance)
}

internal fun KClass<*>.create(
    objectConverter: TermToObjectConverter,
    arguments: List<Term>,
): Result {
    val constructorRef = OverloadSelector.of(this, objectConverter).findConstructor(arguments)
    return constructorRef.callWithPrologArguments(objectConverter, arguments)
}
