package it.unibo.tuprolog.core

internal class OrderCase(
    val id: String,
    val smaller: () -> Term,
    val greater: () -> Term,
)
