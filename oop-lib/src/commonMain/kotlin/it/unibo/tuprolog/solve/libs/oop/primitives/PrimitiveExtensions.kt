package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.primitive.Solve

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is Ref -> throw TypeError.forArgument(context, signature, TypeError.Expected.REFERENCE, arg, index)
        else -> this
    }

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsObjectRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is ObjectRef -> throw TypeError.forArgument(context, signature, TypeError.Expected.OBJECT_REFERENCE, arg, index)
        else -> this
    }

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsTypeRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is TypeRef -> throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REFERENCE, arg, index)
        else -> this
    }

fun <C : ExecutionContext> Solve.Request<C>.findRefFromAlias(alias: Struct): Ref? {
    @Suppress("LocalVariableName")
    val ActualRef = Var.of("ActualRef")
    val found = solve(Struct.of(Alias.FUNCTOR, alias, ActualRef)).toList()
    if (found.isNotEmpty()) {
        return found.filterIsInstance<Solution.Yes>().firstOrNull()?.solvedQuery?.get(1)?.castTo()
    }
    return null
}

inline fun <C : ExecutionContext, Req : Solve.Request<C>, R> Req.catchingOopExceptions(action: Req.() -> R): R {
    try {
        return action()
    } catch (e: OopException) {
        throw e.toPrologError(context, signature)
    }
}