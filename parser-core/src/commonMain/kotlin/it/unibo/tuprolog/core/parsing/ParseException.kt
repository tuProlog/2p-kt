package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.js.JsName

/**
 * Reports a failure while turning Prolog source into a tuProlog domain object.
 *
 * This is the stable exception boundary of `parser-core` and `parser-theory`. Low-level lexical and
 * syntactic failures from `parser-impl` are retained as [cause], while their diagnostic data is
 * projected onto this type. Consequently, [line] and [column] are one-based even though the source
 * positions used by `parser-impl` are zero-based.
 *
 * [clauseIndex] is set by theory parsers and readers to the zero-based index of the clause that
 * failed. It remains `-1` when no clause index applies.
 *
 * @property input original input, or its diagnostic identifier when parsing a streamed source
 * @property offendingSymbol source text associated with the failure, if one is available
 * @property line one-based line containing the failure
 * @property column one-based column containing the failure
 * @param message human-readable description of the failure
 * @param throwable lower-level cause, normally a typed `parser-impl` syntax exception
 */
open class ParseException(
    @JsName("input") var input: Any?,
    @JsName("offendingSymbol") var offendingSymbol: String?,
    @JsName("line") var line: Int,
    @JsName("column") var column: Int,
    message: String?,
    throwable: Throwable?,
) : TuPrologException(message, throwable) {
    /** Zero-based failing clause index, or `-1` when the failure is not associated with a theory. */
    @JsName("clauseIndex")
    var clauseIndex = -1

    /** Returns a compact representation of the available diagnostic fields. */
    override fun toString(): String {
        var message =
            "ParseException{" +
                "message='" + message!!.replace("\\n", "\\\\n") + '\'' +
                ", line=" + line +
                ", column=" + column
        if (clauseIndex >= 0) {
            message += ", clause=Index" + clauseIndex
        }
        if (offendingSymbol != null) {
            message += ", offendingSymbol='" + offendingSymbol + "'"
        }
        return message + "}"
    }
}
