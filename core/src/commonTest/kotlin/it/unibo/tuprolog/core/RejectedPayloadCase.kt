package it.unibo.tuprolog.core

internal class RejectedPayloadCase(
    val id: String,
    val payload: () -> Term,
)
