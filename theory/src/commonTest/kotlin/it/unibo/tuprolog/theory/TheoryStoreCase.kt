package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause

internal class TheoryStoreCase(
    val id: String,
    val store: (Clause) -> Theory,
)
