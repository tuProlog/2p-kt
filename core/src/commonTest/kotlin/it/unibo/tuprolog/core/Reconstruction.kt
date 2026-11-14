package it.unibo.tuprolog.core

import kotlin.reflect.KClass

/** Instantiating the functor of a pattern to [functor], with [args], must yield an instance of [type]. */
internal class Reconstruction(
    val id: String,
    val functor: String,
    val args: () -> kotlin.collections.List<Term>,
    val type: KClass<out Term>,
)
