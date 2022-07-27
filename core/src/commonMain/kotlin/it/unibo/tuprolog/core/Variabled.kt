package it.unibo.tuprolog.core

import kotlin.js.JsName

interface Variabled {
    /** The sequence of [Var]iables directly or indirectly contained in the current object.
     * Variables are lazily returned in a non-deterministic order.
     * Notice that no occurrence-check is performed.
     * Thus, if a [Term] contains the same [Var]iable twice or more times, then the [variables] sequence
     * may contain as many occurrences of that [Var]iable
     *
     * @return a [Sequence] of [Var]
     */
    @JsName("variables")
    val variables: Sequence<Var>

    /**
     * Checks whether the current object is ground.
     * An object is ground if and only if it does not contain any variable.
     * This method is guaranteed to return `true` if and only if the [variables] property
     * of the current object refers to an empty sequence.
     * @return `true` if the current object is ground, or `false`, otherwise
     */
    @JsName("isGround")
    val isGround: Boolean get() = variables.none()
}
