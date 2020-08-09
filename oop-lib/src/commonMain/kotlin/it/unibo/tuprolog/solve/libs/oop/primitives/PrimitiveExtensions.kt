package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.oop.ObjectRef
import it.unibo.tuprolog.solve.libs.oop.Ref
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import it.unibo.tuprolog.solve.primitive.Solve

private val EXPECTED_OBJECT_REF = TypeError.Expected.of("object_reference")
private val EXPECTED_TYPE_REF = TypeError.Expected.of("type_reference")
private val EXPECTED_REF = TypeError.Expected.of("object_or_type_reference")

val TypeError.Expected.Companion.OBJECT_REF
    get() = EXPECTED_OBJECT_REF

val TypeError.Expected.Companion.TYPE_REF
    get() = EXPECTED_TYPE_REF

val TypeError.Expected.Companion.REF
    get() = EXPECTED_REF

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is Ref -> throw TypeError.forArgument(context, signature, TypeError.Expected.REF, arg, index)
        else -> this
    }

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsObjectRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is ObjectRef -> throw TypeError.forArgument(context, signature, TypeError.Expected.OBJECT_REF, arg, index)
        else -> this
    }

fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsTypeRef(index: Int): Solve.Request<C> =
    when (val arg = arguments[index]) {
        !is TypeRef -> throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REF, arg, index)
        else -> this
    }