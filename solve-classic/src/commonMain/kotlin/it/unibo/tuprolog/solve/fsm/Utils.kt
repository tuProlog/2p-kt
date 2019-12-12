package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor

fun Sequence<Term>.ensureStructs(): Cursor<out Struct> =
    map { require(it is Struct); it as Struct }.cursor()

fun Sequence<Clause>.ensureRules(): Cursor<out Rule> =
    map { require(it is Rule); it as Rule }.cursor()

fun Term.unfoldGoals(): Sequence<Term> =
    when (this) {
        is Tuple -> toSequence().flatMap { it.unfoldGoals() }
        else -> sequenceOf(this)
    }

fun Term.toGoals(): Cursor<out Struct> =
    unfoldGoals().map {
        when (it) {
            is Struct -> it
            else -> Struct.of("call", it)
        }
    }.ensureStructs()

// TODO Giovanni's review needed!! with Git > Show History
fun ExecutionContextImpl.toRequest(
    signature: Signature,
    arguments: kotlin.collections.List<Term>
): Solve.Request<ExecutionContextImpl> =
    Solve.Request(
        signature,
        arguments,
        copy(
            libraries = libraries,
            flags = flags,
            staticKB = staticKB,
            dynamicKB = dynamicKB,
            substitution = substitution
        ),
        executionMaxDuration = maxDuration
    )