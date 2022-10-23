package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.js.JsName

open class ParseException(
    @JsName("input") var input: Any?,
    @JsName("offendingSymbol") var offendingSymbol: String,
    @JsName("line") var line: Int,
    @JsName("column") var column: Int,
    message: String?,
    throwable: Throwable?
) : TuPrologException(message, throwable) {

    @JsName("clauseIndex")
    var clauseIndex = -1

    override fun toString(): String {
        return "ParseException{" +
            "message='" + message!!.replace("\\n", "\\\\n") + '\'' +
            ", line=" + line +
            ", column=" + column +
            ", offendingSymbol='" + offendingSymbol + '\'' +
            '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ParseException

        if (input != other.input) return false
        if (offendingSymbol != other.offendingSymbol) return false
        if (line != other.line) return false
        if (column != other.column) return false
        if (clauseIndex != other.clauseIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = input?.hashCode() ?: 0
        result = 31 * result + offendingSymbol.hashCode()
        result = 31 * result + line
        result = 31 * result + column
        result = 31 * result + clauseIndex
        return result
    }
}
