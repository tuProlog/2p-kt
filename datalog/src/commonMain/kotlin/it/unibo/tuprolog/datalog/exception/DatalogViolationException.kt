package it.unibo.tuprolog.datalog.exception

import it.unibo.tuprolog.core.exception.TuPrologException

/**
 * Thrown by the `ensure*` functions of [it.unibo.tuprolog.datalog] (e.g.
 * [it.unibo.tuprolog.datalog.ensureIsDatalog], [it.unibo.tuprolog.datalog.ensureHasNoCompound]) when a
 * [it.unibo.tuprolog.core.Clause] or an [it.unibo.tuprolog.theory.Theory] fails one of the Datalog
 * well-formedness checks (contains a compound argument, has an unsafe head or negated variable, or is
 * recursive).
 *
 * @param culprit the offending [it.unibo.tuprolog.core.Clause]/[it.unibo.tuprolog.theory.Theory] (or its
 * string rendering), included as-is in [message] between [prefix] and [suffix].
 */
@Suppress("MemberVisibilityCanBePrivate")
class DatalogViolationException(
    prefix: String = "",
    val culprit: Any,
    suffix: String = "",
    cause: Throwable? = null,
) : TuPrologException(
        message = "Datalog restriction violation: $prefix$culprit$suffix",
        cause = cause,
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DatalogViolationException

        if (culprit != other.culprit) return false
        if (message != other.message) return false
        if (cause != other.cause) return false

        return true
    }

    override fun hashCode(): Int {
        var result = culprit.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (cause?.hashCode() ?: 0)
        return result
    }
}
