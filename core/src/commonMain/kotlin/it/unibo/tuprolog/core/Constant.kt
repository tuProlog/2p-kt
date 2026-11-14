package it.unibo.tuprolog.core

import kotlin.js.JsName

/**
 * Base type for [Term]s that carry a single, immutable, ground [value] and no sub-terms: [Atom]s (a [String]
 * value), [Numeric]s (an [Integer]/[Real] value), and [Ref]s (an opaque host value, possibly `null`). Constants are
 * always ground, since they contain no [Var]iables. Notice that a constant is not necessarily an atom or a number.
 */
interface Constant : Term {
    override val isConstant: Boolean get() = true

    /**
     * The (platform-native) value wrapped by this constant, e.g. a [String] for [Atom]s.
     * It is `null` only for [NullRef]s: sub-types other than [Ref] refine it to a non-null type.
     */
    @JsName("value")
    val value: Any?

    override fun freshCopy(): Constant

    override fun freshCopy(scope: Scope): Constant

    override fun asConstant(): Constant = this

    /**
     * Empty companion aimed at letting extensions be injected through extension methods
     */
    companion object
}
