package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.libs.oop.primitives.DEALIASING_TEMPLATE
import it.unibo.tuprolog.unify.Unificator.Companion.matches

@Suppress("MemberVisibilityCanBePrivate")
class NoSuchAnAliasException(
    val dealiasingExpression: Struct,
) : OopException(
        "There exists no reference whose alias is `${dealiasingExpression[0]}`",
    ) {
    init {
        require(dealiasingExpression matches DEALIASING_TEMPLATE)
        require(dealiasingExpression[0] is Struct)
    }

    val alias: Struct get() = dealiasingExpression[0] as Struct

    override fun toLogicError(
        context: ExecutionContext,
        signature: Signature,
    ): LogicError =
        ExistenceError.of(
            context,
            ExistenceError.ObjectType.OOP_ALIAS,
            culprit,
            message ?: "",
        )

    override val culprit: Term get() = dealiasingExpression
}
