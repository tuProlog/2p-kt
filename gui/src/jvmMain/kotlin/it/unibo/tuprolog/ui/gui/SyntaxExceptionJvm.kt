package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.parsing.ParseException

actual sealed class SyntaxException actual constructor(override val cause: ParseException) : TuPrologException(cause) {

    actual abstract override val message: String
    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SyntaxException

        if (cause != other.cause) return false
        if (message != other.message) return false

        return true
    }

    actual override fun hashCode(): Int {
        var result = cause.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }
}

actual class InTheorySyntaxError actual constructor(
    actual val page: PageID,
    actual val text: String,
    cause: ParseException
) : SyntaxException(cause) {
    actual override val message: String
        get() = cause.toTheoryMessage(page, text)
}

actual class InQuerySyntaxError actual constructor(
    actual val query: String,
    cause: ParseException
) : SyntaxException(cause) {
    actual override val message: String
        get() = cause.toQueryMessage(query)
}
