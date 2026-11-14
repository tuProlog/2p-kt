package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term

internal class KindCase(
    val id: String,
    val ref: Term,
    val ref1: Boolean,
    val objectRef: Boolean,
    val nullRef: Boolean,
    val typeRef: Boolean,
)
