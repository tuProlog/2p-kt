package it.unibo.tuprolog.primitive

/** A typealias for a primitive function that accepts a [Request] and returns a Sequence of [Response]s */
typealias Primitive = (Request) -> Sequence<Response>
