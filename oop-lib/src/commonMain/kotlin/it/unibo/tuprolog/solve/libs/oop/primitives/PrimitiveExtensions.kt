package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.exceptions.MalformedAliasException
import it.unibo.tuprolog.solve.libs.oop.exceptions.NoSuchAnAliasException
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.matches

internal const val DEALIASING_OPERATOR = "$"

internal val DEALIASING_TEMPLATE = Struct.of(DEALIASING_OPERATOR, Var.of("Alias"))

internal val Term.isDealiasingExpression: Boolean
    get() = this is Struct && this matches DEALIASING_TEMPLATE && args[0] is Atom

internal const val CAST_OPERATOR = "as"

internal val CAST_TEMPLATE = Struct.template(CAST_OPERATOR, 2)

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is Ref -> throw TypeError.forArgument(context, signature, TypeError.Expected.REFERENCE, arg, index)
        else -> this
    }

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsObjectRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is ObjectRef -> throw TypeError.forArgument(
            context,
            signature,
            TypeError.Expected.OBJECT_REFERENCE,
            arg,
            index
        )
        else -> this
    }

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsTypeRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is TypeRef -> throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REFERENCE, arg, index)
        else -> this
    }

fun <C : ExecutionContext> Solve.Request<C>.findRefFromAlias(alias: Struct): Ref {
    val actualAlias = if (alias.isDealiasingExpression) {
        alias[0] as Struct
    } else {
        throw MalformedAliasException(alias)
    }

    @Suppress("LocalVariableName")
    val ActualRef = Var.of("ActualRef")
    return solve(Struct.of(Alias.FUNCTOR, actualAlias, ActualRef))
        .filterIsInstance<Solution.Yes>()
        .firstOrNull()
        ?.solvedQuery
        ?.get(1)
        ?.castTo()
        ?: throw NoSuchAnAliasException(alias)
}

inline fun <C : ExecutionContext, Req : Solve.Request<C>, R> Req.catchingOopExceptions(action: Req.() -> R): R {
    try {
        return action()
    } catch (e: OopException) {
        throw e.toPrologError(context, signature)
    }
}
