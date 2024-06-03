@file:JvmName("PrimitiveExtensions")

package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.libs.oop.OOP.CAST_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.OOP.DEALIASING_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.OOPContext
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.libs.oop.exceptions.MalformedAliasException
import it.unibo.tuprolog.solve.libs.oop.exceptions.NoSuchAnAliasException
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.rules.Alias
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsObjectRef
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsTypeRef
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.match
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.utils.Cache
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

internal val DEALIASING_TEMPLATE = Struct.of(DEALIASING_OPERATOR, Var.of("Alias"))

internal fun <C : ExecutionContext> Solve.Request<C>.isDealiasingExpression(term: Term): Boolean =
    term.isStruct && match(term, DEALIASING_TEMPLATE) && term.castToStruct().args[0].isAtom

internal fun <C : ExecutionContext> Solve.Request<C>.matchesDealiasingTemplate(term: Term): Boolean =
    term.isStruct && match(term, DEALIASING_TEMPLATE)

internal val CAST_TEMPLATE = Struct.template(CAST_OPERATOR, 2)

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsObjectRefOrAlias(index: Int): Solve.Request<C> {
    val arg = arguments[index]
    return when {
        matchesDealiasingTemplate(arg) && findRefFromAlias(arg.castToStruct()).isObjectRef -> this
        else -> ensuringArgumentIsObjectRef(index)
    }
}

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsTypeRefOrAlias(index: Int): Solve.Request<C> {
    val arg = arguments[index]
    return when {
        matchesDealiasingTemplate(arg) && findRefFromAlias(arg.castToStruct()).value is KClass<*> -> this
        else -> ensuringArgumentIsTypeRef(index)
    }
}

context(OOPContext)
private fun ObjectRef.ensureTypeRefIfNecessary(): ObjectRef {
    val value = value
    return if (value is KClass<*> && this !is TypeRef) {
        termFactory.typeRef(value)
    } else {
        this
    }
}

context(OOPContext)
fun <C : ExecutionContext> Solve.Request<C>.dealiasIfNecessary(term: Term): Term {
    return when (term) {
        is Struct -> {
            findRefFromAlias(term).ensureTypeRefIfNecessary()
        }
        else -> term
    }
}

context(OOPContext)
fun <C : ExecutionContext> Solve.Request<C>.getArgumentAsObjectRef(index: Int): ObjectRef {
    ensuringArgumentIsObjectRefOrAlias(index)
    val arg = arguments[index]
    return when {
        arg is ObjectRef -> {
            arg.ensureTypeRefIfNecessary()
        }
        arg is Struct -> {
            dealiasIfNecessary(arg).castToObjectRef()
        }
        else -> throw NotImplementedError("This is a bug, please report it.")
    }
}

context(OOPContext)
fun <C : ExecutionContext> Solve.Request<C>.getArgumentAsTypeRef(index: Int): TypeRef {
    ensuringArgumentIsTypeRef(index)
    val arg = arguments[index]
    return when {
        arg is TypeRef -> {
            arg
        }
        arg is ObjectRef -> {
            arg.ensureTypeRefIfNecessary() as TypeRef
        }
        arg is Struct -> {
            dealiasIfNecessary(arg) as TypeRef
        }
        else -> throw NotImplementedError("This is a bug, please report it.")
    }
}

private val aliasCache: Cache<Pair<Solve.Request<*>, Struct>, ObjectRef?> = Cache.simpleLru()

private fun <C : ExecutionContext> Solve.Request<C>.findRefFromAliasOrNull(alias: Struct): ObjectRef? {
    val actualAlias =
        if (isDealiasingExpression(alias)) {
            alias[0] as Struct
        } else {
            throw MalformedAliasException(alias)
        }
    return aliasCache.getOrSet(this to actualAlias) {
        @Suppress("LocalVariableName", "ktlint:standard:property-naming")
        val ActualRef = Var.of("ActualRef")
        solve(Struct.of(Alias.FUNCTOR, actualAlias, ActualRef))
            .filterIsInstance<Solution.Yes>()
            .firstOrNull()
            ?.solvedQuery
            ?.get(1)
            ?.castToObjectRef()
    }
}

fun <C : ExecutionContext> Solve.Request<C>.isAliasRegistered(alias: Struct): Boolean =
    findRefFromAliasOrNull(alias) != null

fun <C : ExecutionContext> Solve.Request<C>.ensureAliasIsRegistered(alias: Struct): Boolean =
    if (isAliasRegistered(alias)) true else throw NoSuchAnAliasException(alias)

fun <C : ExecutionContext> Solve.Request<C>.findRefFromAlias(alias: Struct): ObjectRef =
    findRefFromAliasOrNull(alias) ?: throw NoSuchAnAliasException(alias)

inline fun <C : ExecutionContext, Req : Solve.Request<C>, R> Req.catchingOopExceptions(action: Req.() -> R): R {
    try {
        return action()
    } catch (e: OopException) {
        throw e.toLogicError(context, signature)
    } catch (e: ResolutionException) {
        throw e
    } catch (e: Throwable) {
        throw SystemError.forUncaughtException(context, e)
    }
}
