package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.libs.oop.name
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass

/**
 * The base type for every error `:oop-lib` can run into while bridging Prolog and JVM/Kotlin
 * reflection -- e.g. an unresolvable method/constructor/property overload, a malformed alias
 * expression, or a reflective operation actually throwing.
 *
 * An [OopException] is a plain Kotlin [Throwable] (so it can be thrown from ordinary code, such
 * as a [it.unibo.tuprolog.solve.libs.oop.Ref] implementation), but every subtype knows how to
 * turn itself into the
 * [it.unibo.tuprolog.solve.exception.LogicError] a [it.unibo.tuprolog.solve.Solver] actually
 * throws -- via [toLogicError] -- which is what
 * [it.unibo.tuprolog.solve.libs.oop.primitives.catchingOopExceptions] does for every primitive
 * in this library.
 *
 * @see it.unibo.tuprolog.solve.libs.oop.primitives.catchingOopExceptions
 */
abstract class OopException : TuPrologException {
    @JvmOverloads
    constructor(message: String? = null, cause: Throwable? = null) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    /** Converts this exception into the [LogicError] that should actually be thrown by [context]'s solver, while executing [signature]. */
    abstract fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError

    /** The [Term] identifying what this exception is about, embedded in the [LogicError] produced by [toLogicError]. */
    protected abstract val culprit: Term

    companion object {
        internal fun List<Set<KClass<*>>>.pretty(): String = joinToString { it.pretty() }

        internal fun Set<KClass<*>>.pretty(): String = joinToString("|") { it.name }
    }
}
