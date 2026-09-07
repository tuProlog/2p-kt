package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.TermToObjectConverterImpl
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * Converts a Prolog [Term] into a JVM/Kotlin value -- the direction opposite to
 * [ObjectToTermConverter] -- and drives the overload-resolution machinery
 * ([OverloadSelector]) used to pick the right method/constructor/property to invoke for a given
 * set of Prolog arguments.
 *
 * A [Term] can be ambiguous with respect to which JVM/Kotlin type it should become (e.g. a Prolog
 * integer could become a [Long], an [Int], a [Short]... or `org.gciatto.kt.math.BigInteger`): this
 * converter enumerates all the [admissibleTypes] a term could be converted into, ranks how well
 * each candidate [priorityOfConversion] fits (lower is better; `null` means "not admissible"), and
 * exposes [mostAdequateType] as the best default guess -- which is what the parameterless
 * [convert] uses.
 *
 * [ObjectRef]s and `null` (via [it.unibo.tuprolog.solve.libs.oop.NullRef]) convert straightforwardly
 * to their wrapped object (subject to a subtype check against the requested [convertInto] type);
 * an explicit `X as Type` expression ([it.unibo.tuprolog.solve.libs.oop.OOP.CAST_OPERATOR]) or a
 * dealiasing expression `$Alias` (resolved via the [dealiaser] passed to [of]) is honored too.
 *
 * @see ObjectToTermConverter
 * @see OverloadSelector
 */
interface TermToObjectConverter {
    /**
     * Converts [term] into an instance of [type].
     *
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException if
     * [term] cannot be converted into an instance of [type].
     */
    fun convertInto(
        type: KClass<*>,
        term: Term,
    ): Any?

    /** Eagerly converts [term] into every one of its [admissibleTypes], in priority order. */
    fun possibleConversions(term: Term): Sequence<Any?>

    /** Every JVM/Kotlin type [term] could reasonably be converted into. */
    fun admissibleTypes(term: Term): Set<KClass<*>>

    /**
     * How well [term] fits as an instance of [type], lower being a better fit, or `null` if
     * [term] cannot be converted into [type] at all. Used by [OverloadSelector] to rank
     * candidate overloads.
     */
    fun priorityOfConversion(
        type: KClass<*>,
        term: Term,
    ): Int?

    /** The best (lowest-priority-value) type among [admissibleTypes] for [term]. */
    fun mostAdequateType(term: Term): KClass<*>

    /** Converts [term] into an instance of its [mostAdequateType]. */
    fun convert(term: Term): Any? = convertInto(mostAdequateType(term), term)

    companion object {
        /**
         * Builds a [TermToObjectConverter] that resolves Prolog type names via [typeFactory]
         * (for casts and constructor selection) and resolves `$Alias` dealiasing expressions via
         * [dealiaser] (which returns `null`, causing dealiasing to fail, by default).
         */
        @JvmStatic
        fun of(
            typeFactory: TypeFactory = TypeFactory.default,
            dealiaser: (Struct) -> Ref? = { null },
        ): TermToObjectConverter = TermToObjectConverterImpl(typeFactory, dealiaser)

        /** The default [TermToObjectConverter], with no alias resolution. */
        @JvmStatic
        val default: TermToObjectConverter = of()
    }
}
