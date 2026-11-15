package it.unibo.tuprolog.core

internal class PatternFactoryCase(
    val id: String,
    val build: (Var, kotlin.collections.List<Term>) -> Compound,
)
