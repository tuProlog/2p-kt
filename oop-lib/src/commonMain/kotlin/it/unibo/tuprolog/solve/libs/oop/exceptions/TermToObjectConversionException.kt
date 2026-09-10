package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.libs.oop.fullName
import kotlin.reflect.KClass

/**
 * Thrown by [it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter.convertInto] when [term]
 * cannot be turned into an instance of [targetType] (or of any type at all, if [targetType] is
 * `null`) -- e.g. converting a compound term that is not a recognized `as`/`$` expression, or an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef] whose wrapped object is not a subtype of the
 * requested type.
 *
 * Surfaces to Prolog as an [it.unibo.tuprolog.solve.exception.error.RepresentationError] with
 * limit [it.unibo.tuprolog.solve.exception.error.RepresentationError.Limit.OOP_OBJECT].
 *
 * @param term the term that could not be converted.
 * @param targetType the JVM/Kotlin type conversion was attempted into, if any was specified.
 */
@Suppress("MemberVisibilityCanBePrivate")
class TermToObjectConversionException(
    val term: Term,
    val targetType: KClass<*>? = null,
) : OopException(
        targetType?.let {
            "Term `$term` cannot be converted into an object of type ${targetType.fullName}"
        } ?: "Term `$term` cannot be converted into an object",
    ) {
    constructor(term: Term) : this(term, null)

    override fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError =
        RepresentationError.of(
            context,
            signature,
            RepresentationError.Limit.OOP_OBJECT,
            this,
        )

    override val culprit: Term
        get() = term
}
