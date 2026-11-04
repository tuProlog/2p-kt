package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term

/** A [UnificationRequest] originated from a [Unificator.mgu] call on [term1] and [term2]. */
data class MguRequest(
    val term1: Term,
    val term2: Term,
    override val occurCheckEnabled: Boolean,
) : UnificationRequest
