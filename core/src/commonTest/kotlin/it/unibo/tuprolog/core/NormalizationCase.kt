package it.unibo.tuprolog.core

internal class NormalizationCase(
    val id: String,
    /** Builds the original clause and the expected normalized one, sharing their variables. */
    val clauses: () -> Pair<Clause, Clause>,
)
