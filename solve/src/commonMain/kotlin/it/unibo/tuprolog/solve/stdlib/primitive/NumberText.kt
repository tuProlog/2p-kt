package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.primitive.Solve

private val RADIX = mapOf('x' to 16, 'o' to 8, 'b' to 2)

/**
 * Parses [text] as a number token, the way `number_chars/2` and `number_codes/2` read it: optional leading layout,
 * an optional `-`, then a decimal, `0x`/`0o`/`0b` or `0'c` (character code) literal.
 *
 * @throws SyntaxError if [text] is not such a number
 */
internal fun Solve.Request<ExecutionContext>.parseNumber(text: String): Numeric {
    val signed = text.trimStart()
    val sign = if (signed.startsWith("-")) "-" else ""
    val body = signed.removePrefix(sign)
    val radix = RADIX[body.getOrNull(1)]
    val number =
        try {
            when {
                body.length == 3 && body.startsWith("0'") -> Integer.of(sign + body[2].code)
                body.startsWith("0") && radix != null -> Integer.of(sign + body.substring(2), radix)
                // shortcut: the remaining checks are Kotlin's number syntax, e.g. `1e5` is accepted as a float
                body.firstOrNull()?.isDigit() == true && body.last().isDigit() -> Numeric.of(signed)
                else -> null
            }
        } catch (_: NumberFormatException) {
            null
        }
    return number ?: throw SyntaxError.of(context, "Cannot parse `$text` as a number")
}
