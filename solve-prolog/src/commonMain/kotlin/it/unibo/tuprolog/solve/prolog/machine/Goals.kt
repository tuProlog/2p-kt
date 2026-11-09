package it.unibo.tuprolog.solve.prolog.machine

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable

/** The goals of a conjunction, flattened; any other term is a single goal. */
internal fun Term.unfoldGoals(): Sequence<Term> =
    when {
        isTuple -> castToTuple().toSequence().flatMap { it.unfoldGoals() }
        else -> sequenceOf(this)
    }

/**
 * Whether this goal is an ISO control construct (`,`/2, `;`/2, `->`/2, `call/N`, `catch/3`, `throw/1`), or the
 * callability check `call/1` runs first ([EnsureExecutable]), which [it.unibo.tuprolog.solve.ResolutionCounters] don't
 * count: the machine runs them as library predicates, other engines natively, and the counts must not depend on that.
 */
internal val Struct?.isControlConstruct: Boolean
    get() =
        this != null &&
            when (functor) {
                "call" -> true
                ",", ";", "->" -> arity == 2
                "catch" -> arity == 3
                "throw" -> arity == 1
                EnsureExecutable.functor -> arity == 1
                else -> false
            }
