package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmStatic
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

/**
 * Resolves Java/Kotlin overloading -- i.e. picks, among all public members of [type] named
 * alike, the one whose formal parameters best match a given list of Prolog [Term] arguments --
 * using [termToObjectConverter] to score how well each candidate parameter type fits each
 * argument (see [TermToObjectConverter.priorityOfConversion]).
 *
 * This is what allows Prolog code such as `X.println(1)` and `X.println("a")` to reach different
 * overloads of `println` on the same [type] without the Prolog programmer ever naming a type
 * explicitly (see `as`/2, [it.unibo.tuprolog.solve.libs.oop.OOP.CAST_OPERATOR], for when the
 * automatic choice needs to be overridden).
 *
 * @see it.unibo.tuprolog.solve.libs.oop.impl.OverloadSelectorImpl for the JVM reflection-based implementation.
 */
interface OverloadSelector {
    /** The type whose members this selector resolves overloads on. */
    val type: KClass<*>

    /** The converter used to score how well a [Term] argument fits a candidate parameter type. */
    val termToObjectConverter: TermToObjectConverter

    /**
     * Finds the public method (or property getter) of [type] named [name] whose formal parameters
     * best match [arguments].
     *
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.MethodInvocationException if no such
     * member exists.
     */
    fun findMethod(
        name: String,
        arguments: List<Term>,
    ): KCallable<*>

    /**
     * Finds the public mutable property of [type] named [name] whose setter accepts [value].
     *
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.PropertyAssignmentException if no such
     * property exists.
     */
    fun findProperty(
        name: String,
        value: Term,
    ): KMutableProperty<*>

    /**
     * Finds the public constructor of [type] whose formal parameters best match [arguments].
     *
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.ConstructorInvocationException if no
     * such constructor exists.
     */
    fun findConstructor(arguments: List<Term>): KCallable<*>

    companion object {
        /** Builds an [OverloadSelector] for [type], scoring arguments via [termToObjectConverter]. */
        @JvmStatic
        fun of(
            type: KClass<*>,
            termToObjectConverter: TermToObjectConverter = TermToObjectConverter.default,
        ): OverloadSelector = overloadSelector(type, termToObjectConverter)
    }
}
