package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter.Companion.prettyExpressions
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected.DEALIASING_EXPRESSION
import it.unibo.tuprolog.solve.libs.oop.primitives.DEALIASING_TEMPLATE

@Suppress("MemberVisibilityCanBePrivate")
class MalformedAliasException(
    val dealiasingExpression: Struct,
) : OopException(
        "This is not a dealiasing expression `$dealiasingExpression` in the form ${
            DEALIASING_TEMPLATE.format(
                prettyExpressions(),
            )
        }",
    ) {
    override fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError =
        TypeError.of(
            context,
            DEALIASING_EXPRESSION,
            culprit,
            "Error in ${signature.toIndicator()}: ${message?.replaceFirstChar { it.lowercase() }}",
        )

    override val culprit: Term get() = dealiasingExpression
}
