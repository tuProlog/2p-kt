package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution

/** A [UnificationRequest] originated from a [Unificator.merge] call on [substitution1] and [substitution2]. */
data class MergeRequest(
    val substitution1: Substitution,
    val substitution2: Substitution,
    override val occurCheckEnabled: Boolean,
) : UnificationRequest
