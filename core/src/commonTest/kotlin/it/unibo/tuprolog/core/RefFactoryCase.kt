package it.unibo.tuprolog.core

internal class RefFactoryCase(
    val id: String,
    val build: (Any) -> Ref,
)
