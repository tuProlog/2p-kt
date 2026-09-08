package it.unibo.tuprolog.core

import kotlin.js.JsName

/**
 * Base type for [Term]s that carry a single, immutable, ground [value] and no sub-terms: [Atom]s (a [String]
 * value) and [Numeric]s (an [Integer]/[Real] value). Constants are always ground, since they contain no
 * [Var]iables.
 */
interface Constant : Term {
    override val isConstant: Boolean get() = true

    /** The (platform-native) value wrapped by this constant, e.g. a [String] for [Atom]s. */
    @JsName("value")
    val value: Any

    override fun freshCopy(): Constant

    override fun freshCopy(scope: Scope): Constant

    override fun asConstant(): Constant = this

    /**
     * Empty companion aimed at letting extensions be injected through extension methods
     */
    companion object
}
