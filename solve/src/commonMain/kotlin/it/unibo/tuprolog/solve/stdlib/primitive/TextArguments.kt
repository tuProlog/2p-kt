package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsList
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

/**
 * The text spelled by the list of one-character atoms in the [index]-th argument.
 *
 * @throws InstantiationError if the list is partial or has an unbound element
 * @throws TypeError if it is not a list, or has an element that is not a one-character atom
 */
internal fun Solve.Request<ExecutionContext>.charListText(index: Int): String {
    ensuringArgumentIsList(index)
    val list = arguments[index].castToList()
    val tail = list.last
    when {
        tail is Var -> throw InstantiationError.forArgument(context, signature, tail, index)
        !list.isWellFormed -> throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, list, index)
    }
    return list.toSequence().joinToString("") {
        when {
            it is Var -> throw InstantiationError.forArgument(context, signature, it, index)
            it !is Atom || it.value.length != 1 ->
                throw TypeError.forArgumentList(context, signature, TypeError.Expected.CHARACTER, it, index)
            else -> it.value
        }
    }
}
