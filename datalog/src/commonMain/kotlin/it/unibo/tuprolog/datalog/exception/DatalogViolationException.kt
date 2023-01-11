package it.unibo.tuprolog.datalog.exception

import it.unibo.tuprolog.core.exception.TuPrologException

@Suppress("MemberVisibilityCanBePrivate")
class DatalogViolationException(prefix: String = "", val culprit: Any, suffix: String = "", cause: Throwable? = null) :
    TuPrologException(
        message = "Datalog restriction violation: $prefix$culprit$suffix",
        cause = cause
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
