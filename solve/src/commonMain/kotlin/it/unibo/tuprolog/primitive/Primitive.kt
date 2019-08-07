package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.solve.Solve

/** A typealias for a primitive function that accepts a [Solve.Request] and returns a Sequence of [Solve.Response]s */
typealias Primitive = (Solve.Request) -> Sequence<Solve.Response>
