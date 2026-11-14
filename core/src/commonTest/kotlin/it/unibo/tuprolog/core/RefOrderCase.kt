package it.unibo.tuprolog.core

internal class RefOrderCase(
    val id: String,
    val smaller: () -> Term,
    val greater: () -> Term,
)
