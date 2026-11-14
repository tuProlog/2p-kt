package it.unibo.tuprolog.core

internal class UnchangedCase(
    val id: String,
    val clause: () -> Clause,
)
