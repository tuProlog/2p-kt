@file:JvmName("PrimitiveExtensions")

package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.oop.OOP.CAST_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.OOP.DEALIASING_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.TermToObjectConverter
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.exceptions.MalformedAliasException
import it.unibo.tuprolog.solve.libs.oop.exceptions.NoSuchAnAliasException
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsStruct
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.match
import it.unibo.tuprolog.solve.primitive.Solve
import kotlin.jvm.JvmName

internal val DEALIASING_TEMPLATE = Struct.of(DEALIASING_OPERATOR, Var.of("Alias"))

internal fun <C : ExecutionContext> Solve.Request<C>.isDealiasingExpression(term: Term): Boolean =
    term is Struct && match(term, DEALIASING_TEMPLATE) && term.args[0] is Atom

internal fun <C : ExecutionContext> Solve.Request<C>.matchesDealiasingTemplate(term: Term): Boolean =
    term is Struct && match(term, DEALIASING_TEMPLATE)

internal val CAST_TEMPLATE = Struct.template(CAST_OPERATOR, 2)

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsRef(index: Int): Solve.Request<C> {
    val arg = arguments[index]
    return when {
        matchesDealiasingTemplate(arg) && ensureAliasIsRegistered(arg.castToStruct()) -> this
        arg !is Ref -> throw TypeError.forArgument(context, signature, TypeError.Expected.REFERENCE, arg, index)
        else -> this
    }
}

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsObjectRef(index: Int): Solve.Request<C> {
    val arg = arguments[index]
    return when {
        matchesDealiasingTemplate(arg) && findRefFromAlias(arg.castToStruct()) is ObjectRef -> this
        arg !is ObjectRef -> throw TypeError.forArgument(
            context,
            signature,
            TypeError.Expected.OBJECT_REFERENCE,
            arg,
            index
        )
        else -> this
    }
}

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsTypeRef(index: Int): Solve.Request<C> {
    val arg = arguments[index]
    return when {
        matchesDealiasingTemplate(arg) && findRefFromAlias(arg.castToStruct()) is TypeRef -> this
        arg !is TypeRef -> throw TypeError.forArgument(
            context,
            signature,
            TypeError.Expected.TYPE_REFERENCE,
            arg,
            index
        )
        else -> this
    }
}

fun <C : ExecutionContext> Solve.Request<C>.getArgumentAsTypeRef(index: Int): TypeRef? {
    ensuringArgumentIsStruct(index)
    val arg = arguments[index]
    return when {
        arg is TypeRef -> {
            arg
        }
        arg is Atom -> {
            TypeFactory.default.typeRefFromName(arg.value)
        }
        isDealiasingExpression(arg) -> {
            findRefFromAlias(arg.castToStruct()) as? TypeRef
        }
        else -> {
            ensuringArgumentIsTypeRef(1)
            null
        }
    }
}

private fun <C : ExecutionContext> Solve.Request<C>.findRefFromAliasOrNull(alias: Struct): Ref? {
    val actualAlias = if (isDealiasingExpression(alias)) {
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
}

fun <C : ExecutionContext> Solve.Request<C>.isAliasRegistered(alias: Struct): Boolean =
    findRefFromAliasOrNull(alias) != null

fun <C : ExecutionContext> Solve.Request<C>.ensureAliasIsRegistered(alias: Struct): Boolean =
    if (isAliasRegistered(alias)) true else throw NoSuchAnAliasException(alias)

fun <C : ExecutionContext> Solve.Request<C>.findRefFromAlias(alias: Struct): Ref =
    findRefFromAliasOrNull(alias) ?: throw NoSuchAnAliasException(alias)

inline fun <C : ExecutionContext, Req : Solve.Request<C>, R> Req.catchingOopExceptions(action: Req.() -> R): R {
    try {
        return action()
    } catch (e: OopException) {
        throw e.toLogicError(context, signature)
    } catch (e: Throwable) {
        throw SystemError.forUncaughtException(context, e)
    }
}

val <C : ExecutionContext> Solve.Request<C>.termToObjectConverter: TermToObjectConverter
    get() = TermToObjectConverter.of { findRefFromAliasOrNull(it) }
