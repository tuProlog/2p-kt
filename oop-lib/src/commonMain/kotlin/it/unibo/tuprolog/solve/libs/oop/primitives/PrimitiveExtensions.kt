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

/**
 * Ensures the argument at [index] is a [Ref] -- resolving it first if it is a `$Alias` dealiasing
 * expression -- returning this request for chaining, as the other `ensuringArgumentIs*` helpers
 * in [it.unibo.tuprolog.solve.primitive.PrimitiveWrapper] do.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError with expected type
 * [it.unibo.tuprolog.solve.exception.error.TypeError.Expected.REFERENCE] if it is neither.
 * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.NoSuchAnAliasException if it is a
 * dealiasing expression whose alias is not registered.
 */
fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsRef(index: Int): Solve.Request<C> {
    val arg = arguments[index]
    return when {
        matchesDealiasingTemplate(arg) && ensureAliasIsRegistered(arg.castToStruct()) -> this
        arg !is Ref -> throw TypeError.forArgument(context, signature, TypeError.Expected.REFERENCE, arg, index)
        else -> this
    }
}

/**
 * Like [ensuringArgumentIsRef], but requiring the argument to be (or resolve, via `$Alias`, to)
 * an [ObjectRef].
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError with expected type
 * [it.unibo.tuprolog.solve.exception.error.TypeError.Expected.OBJECT_REFERENCE] otherwise.
 */
fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsObjectRef(index: Int): Solve.Request<C> {
    val arg = arguments[index]
    return when {
        matchesDealiasingTemplate(arg) && findRefFromAlias(arg.castToStruct()) is ObjectRef -> this
        arg !is ObjectRef -> throw TypeError.forArgument(
            context,
            signature,
            TypeError.Expected.OBJECT_REFERENCE,
            arg,
            index,
        )
        else -> this
    }
}

/**
 * Like [ensuringArgumentIsRef], but requiring the argument to be (or resolve, via `$Alias`, to)
 * a [TypeRef].
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError with expected type
 * [it.unibo.tuprolog.solve.exception.error.TypeError.Expected.TYPE_REFERENCE] otherwise.
 */
fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsTypeRef(index: Int): Solve.Request<C> {
    val arg = arguments[index]
    return when {
        matchesDealiasingTemplate(arg) && findRefFromAlias(arg.castToStruct()) is TypeRef -> this
        arg !is TypeRef -> throw TypeError.forArgument(
            context,
            signature,
            TypeError.Expected.TYPE_REFERENCE,
            arg,
            index,
        )
        else -> this
    }
}

/**
 * Resolves the argument at [index] into a [TypeRef], accepting it directly as a [TypeRef], as an
 * [it.unibo.tuprolog.core.Atom] naming a type (via [TypeFactory.default]), or as a `$Alias`
 * dealiasing expression -- returning `null` if resolution fails for one of these forms.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if the argument is none of the above.
 */
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
    val actualAlias =
        if (isDealiasingExpression(alias)) {
            alias[0] as Struct
        } else {
            throw MalformedAliasException(alias)
        }

    @Suppress("LocalVariableName", "ktlint:standard:property-naming")
    val ActualRef = Var.of("ActualRef")
    return solve(Struct.of(Alias.FUNCTOR, actualAlias, ActualRef))
        .filterIsInstance<Solution.Yes>()
        .firstOrNull()
        ?.solvedQuery
        ?.get(1)
        ?.castTo()
}

/**
 * Whether [alias] (the `Alias` argument of a well-formed `$Alias` expression) is currently
 * registered, i.e. whether an [it.unibo.tuprolog.solve.libs.oop.rules.Alias] fact for it can be
 * found in the current knowledge base.
 */
fun <C : ExecutionContext> Solve.Request<C>.isAliasRegistered(alias: Struct): Boolean =
    findRefFromAliasOrNull(alias) != null

/**
 * Like [isAliasRegistered], but throwing instead of returning `false`.
 *
 * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.NoSuchAnAliasException if [alias] is not registered.
 */
fun <C : ExecutionContext> Solve.Request<C>.ensureAliasIsRegistered(alias: Struct): Boolean =
    if (isAliasRegistered(alias)) true else throw NoSuchAnAliasException(alias)

/**
 * Resolves the well-formed `$Alias` dealiasing expression [alias] into the [Ref] it currently
 * points to.
 *
 * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.MalformedAliasException if [alias] is not
 * of the shape `$Alias`.
 * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.NoSuchAnAliasException if [alias] is
 * well-formed but not registered.
 */
fun <C : ExecutionContext> Solve.Request<C>.findRefFromAlias(alias: Struct): Ref =
    findRefFromAliasOrNull(alias) ?: throw NoSuchAnAliasException(alias)

/**
 * Runs [action], converting any [it.unibo.tuprolog.solve.libs.oop.exceptions.OopException] it
 * throws into the [it.unibo.tuprolog.solve.exception.LogicError] this request's solver actually
 * expects (via [it.unibo.tuprolog.solve.libs.oop.exceptions.OopException.toLogicError]), and any
 * other unexpected [Throwable] into an
 * [it.unibo.tuprolog.solve.exception.error.SystemError.forUncaughtException]. Every primitive in
 * `:oop-lib` wraps its logic with this to turn reflection failures into well-formed Prolog errors.
 */
inline fun <C : ExecutionContext, Req : Solve.Request<C>, R> Req.catchingOopExceptions(action: Req.() -> R): R {
    try {
        return action()
    } catch (e: OopException) {
        throw e.toLogicError(context, signature)
    } catch (e: Throwable) {
        throw SystemError.forUncaughtException(context, e)
    }
}

/**
 * A [TermToObjectConverter] wired to resolve `$Alias` dealiasing expressions against this
 * request's current knowledge base (via [findRefFromAlias]/[findRefFromAliasOrNull]) -- the
 * converter every primitive in `:oop-lib` should use in place of [TermToObjectConverter.default].
 */
val <C : ExecutionContext> Solve.Request<C>.termToObjectConverter: TermToObjectConverter
    get() = TermToObjectConverter.of { findRefFromAliasOrNull(it) }
