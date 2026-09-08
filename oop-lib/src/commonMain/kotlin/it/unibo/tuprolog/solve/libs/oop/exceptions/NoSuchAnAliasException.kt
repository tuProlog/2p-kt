package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.libs.oop.primitives.DEALIASING_TEMPLATE
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/**
 * Thrown when [dealiasingExpression] -- a well-formed `$Alias` expression, unlike the one
 * triggering [MalformedAliasException] -- names an [alias] that no
 * [it.unibo.tuprolog.solve.libs.oop.rules.Alias] fact currently registers, e.g. `$undefined_alias`
 * when no `alias(undefined_alias, _)` clause exists (see `register/2` /
 * [it.unibo.tuprolog.solve.libs.oop.primitives.Register] for registering one).
 *
 * Surfaces to Prolog as an [it.unibo.tuprolog.solve.exception.error.ExistenceError] of type
 * [it.unibo.tuprolog.solve.exception.error.ExistenceError.ObjectType.OOP_ALIAS].
 *
 * @param dealiasingExpression the well-formed but unresolvable `$Alias` expression.
 * @see MalformedAliasException
 */
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

    /** The alias term (`Alias` in `$Alias`) that could not be resolved. */
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
