package it.unibo.tuprolog.theory

internal class Scenario(
    val id: String,
    val check: (TheoryKind) -> Unit,
)
