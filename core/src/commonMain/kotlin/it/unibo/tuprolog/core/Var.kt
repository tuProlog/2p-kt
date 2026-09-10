package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VarImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * A logic variable, i.e. a placeholder for a [Term] that is yet to be determined.
 *
 * A [Var] is created out of a simple, human-readable [name] (e.g. `X`), but its real identity is its
 * [completeName], which pairs [name] with a hidden, per-name sequential [id]. This is why, perhaps
 * surprisingly, `Var.of("X") == Var.of("X")` is always `false`: each call mints a genuinely new variable, and
 * relying on the [name] alone for equality would make every clause containing a variable called `X`
 * accidentally alias every other clause using that same name. See the "Variables and Scoping" explanation
 * page in the project documentation for the full rationale.
 *
 * Because of this, code that needs to refer to *the same* variable more than once while building a term
 * (e.g. `member(H, [_|T]) :- member(H, T).`, where `H` and `T` each occur twice) should not call [Var.of]
 * repeatedly with the same name: doing so creates unrelated variables. Instead, either keep a single [Var]
 * reference around and reuse it, or use a [Scope], which caches variables by [name] and hands back the same
 * instance on repeated requests:
 * ```
 * Scope.of("H", "T") {
 *     ruleOf(structOf("member", varOf("H"), consOf(anonymous(), varOf("T"))), structOf("member", varOf("H"), varOf("T")))
 * }
 * ```
 *
 * The [anonymous] variable (conventionally named `_`) is the deliberate exception to variable reuse: every
 * call to [anonymous] (or [Var.anonymous]) produces a fresh, unrelated variable, matching Prolog's convention
 * that `_` never binds to anything meaningful shared across occurrences.
 *
 * @see Scope
 * @see Term.freshCopy
 */
interface Var : Term {
    override val isVar: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = sequenceOf(this)

    /**
     * Whether this variable is the anonymous variable, i.e. whether its [name] is `"_"`.
     * Anonymous variables are never meant to be shared: each one created via [Var.anonymous] is distinct,
     * regardless of this property being `true` for all of them.
     */
    @JsName("isAnonymous")
    val isAnonymous: Boolean
        get() = Terms.ANONYMOUS_VAR_NAME == name

    /**
     * The simple, human-readable name of this variable, as it would appear in Prolog source (e.g. `"X"`).
     * This is *not* used to determine variable identity/equality; see [completeName] for that.
     */
    @JsName("name")
    val name: String

    /**
     * The identifier distinguishing this variable from any other [Var] sharing the same [name].
     * Combined with [name], it forms [completeName]. Identifiers are assigned automatically and cannot be
     * chosen by client code, precisely so that no two independently-created variables can accidentally
     * collide.
     */
    @JsName("id")
    val id: String

    /**
     * The full identity of this variable, obtained by combining [name] and [id] (e.g. `"X_1"`).
     * Two [Var] instances are `equals` to each other (via `Term.equals(Any?)`) if and only if their
     * [completeName]s match; see [Term.equals] for how to compare variables by [name] alone instead.
     */
    @JsName("completeName")
    val completeName: String

    /**
     * Returns a fresh variable, sharing this one's [name] but a newly minted, distinct [completeName].
     * Used, in particular, when a [Term] containing this variable is refreshed via [Term.freshCopy].
     */
    override fun freshCopy(): Var

    override fun freshCopy(scope: Scope): Var

    /**
     * Whether [name] matches [NAME_PATTERN], i.e. whether it could legally appear as a variable name in
     * Prolog syntax. This is purely informational: constructing a [Var] via [Var.of] never validates or
     * throws because of an ill-formed [name].
     */
    @JsName("isNameWellFormed")
    val isNameWellFormed: Boolean

    override fun asVar(): Var = this

    @Suppress("MayBeConstant")
    companion object {
        /** The conventional name (`"_"`) used by the [anonymous] variable. */
        @JvmField
        val ANONYMOUS_NAME = Terms.ANONYMOUS_VAR_NAME

        /** The pattern a [name] must match to be considered [isNameWellFormed]. */
        @JvmField
        val NAME_PATTERN = Terms.VAR_NAME_PATTERN

        /**
         * Creates a new, distinct [Var] named [name].
         * Calling this twice with the same [name] yields two different variables (see the class documentation);
         * use a [Scope] instead when the same variable must occur more than once in the term(s) being built.
         * @param name the simple name of the variable; not validated against [NAME_PATTERN]
         * @return a fresh [Var] instance
         */
        @JvmStatic
        @JsName("of")
        fun of(name: String): Var = VarImpl(name)

        /**
         * Creates a new anonymous variable (conventionally represented as `_`).
         * Every invocation returns a distinct instance: anonymous variables are never meant to be shared.
         * @return a fresh, anonymous [Var]
         */
        @JvmStatic
        @JsName("anonymous")
        fun anonymous(): Var = VarImpl(Terms.ANONYMOUS_VAR_NAME)

        /**
         * Wraps [string] between backticks, unconditionally.
         * @return a new [String] surrounded by `` ` ``
         */
        @JvmStatic
        @JsName("escapeName")
        fun escapeName(string: String): String = "`$string`"

        /**
         * Wraps [string] between backticks, but only if it does not already match [NAME_PATTERN].
         * @return either [string] itself, or a backtick-escaped copy of it
         */
        @JvmStatic
        @JsName("escapeNameIfNecessary")
        fun escapeNameIfNecessary(string: String): String =
            if (Terms.VAR_NAME_PATTERN.matches(string)) {
                string
            } else {
                escapeName(string)
            }
    }
}
