package it.unibo.tuprolog.solve.libs.oop

/** An opaque handle of a foreign object, e.g. a Python one, that only [ForeignBackend] reaches. */
class ForeignHandle(
    val id: String,
)
