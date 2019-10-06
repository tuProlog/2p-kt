package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor

expect fun currentTimeMillis(): Long

fun Sequence<Term>.ensureStructs(): Cursor<out Struct> {
    return map { require(it is Struct); it as Struct }.cursor()
}

fun Sequence<Clause>.ensureRules(): Cursor<out Rule> {
    return map { require(it is Rule); it as Rule }.cursor()
}

fun Struct.toGoals(): Cursor<out Struct> {
    return when (this) {
        is Tuple -> toSequence()
        else -> sequenceOf(this)
    }.ensureStructs()
}

fun ExecutionContext.toRequest(signature: Signature, arguments: kotlin.collections.List<Term>): Solve.Request {
    return Solve.Request(
            query = query,
            currentGoal = goals.current!!,
            signature = signature,
            arguments = arguments,
            libraries = libraries,
            flags = flags,
            staticKB = staticKB,
            dynamicKB = dynamicKB
    )
}