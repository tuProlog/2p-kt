package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VariablesProviderImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.reflect.KProperty

/**
 * A [Scope] extended with 26 pre-declared [Var] properties, one per uppercase Latin letter ([A] to [Z]), for
 * quickly sketching terms in Kotlin code without spelling out `varOf("X")` for every commonly-named variable.
 * Being also a Kotlin property delegate provider (via [getValue]), a [VariablesProvider] lets a Kotlin
 * `val` declaration mint (or fetch) a same-named [Scope] variable directly:
 * ```
 * val provider = VariablesProvider.of()
 * val x by provider // equivalent to `val x = provider.varOf("X")`
 * ```
 */
@Suppress("PropertyName")
interface VariablesProvider : Scope {
    /** The underlying [Scope] this [VariablesProvider] delegates variable creation/caching to. */
    @JsName("scope")
    val scope: Scope

    /** Creates a copy of this [VariablesProvider], backed by [scope] (defaulting to this one's own [scope]). */
    fun copy(scope: Scope = this.scope): VariablesProvider

    /** Property-delegate hook letting `val x by variablesProvider` resolve to `variablesProvider.varOf("X")`. */
    @JsName("getValue")
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Var

    /** Shorthand for `varOf("A")`. */
    @JsName("A")
    val A: Var

    /** Shorthand for `varOf("B")`. */
    @JsName("B")
    val B: Var

    /** Shorthand for `varOf("C")`. */
    @JsName("C")
    val C: Var

    /** Shorthand for `varOf("D")`. */
    @JsName("D")
    val D: Var

    /** Shorthand for `varOf("E")`. */
    @JsName("E")
    val E: Var

    /** Shorthand for `varOf("F")`. */
    @JsName("F")
    val F: Var

    /** Shorthand for `varOf("G")`. */
    @JsName("G")
    val G: Var

    /** Shorthand for `varOf("H")`. */
    @JsName("H")
    val H: Var

    /** Shorthand for `varOf("I")`. */
    @JsName("I")
    val I: Var

    /** Shorthand for `varOf("J")`. */
    @JsName("J")
    val J: Var

    /** Shorthand for `varOf("K")`. */
    @JsName("K")
    val K: Var

    /** Shorthand for `varOf("L")`. */
    @JsName("L")
    val L: Var

    /** Shorthand for `varOf("M")`. */
    @JsName("M")
    val M: Var

    /** Shorthand for `varOf("N")`. */
    @JsName("N")
    val N: Var

    /** Shorthand for `varOf("O")`. */
    @JsName("O")
    val O: Var

    /** Shorthand for `varOf("P")`. */
    @JsName("P")
    val P: Var

    /** Shorthand for `varOf("Q")`. */
    @JsName("Q")
    val Q: Var

    /** Shorthand for `varOf("R")`. */
    @JsName("R")
    val R: Var

    /** Shorthand for `varOf("S")`. */
    @JsName("S")
    val S: Var

    /** Shorthand for `varOf("T")`. */
    @JsName("T")
    val T: Var

    /** Shorthand for `varOf("U")`. */
    @JsName("U")
    val U: Var

    /** Shorthand for `varOf("V")`. */
    @JsName("V")
    val V: Var

    /** Shorthand for `varOf("W")`. */
    @JsName("W")
    val W: Var

    /** Shorthand for `varOf("X")`. */
    @JsName("X")
    val X: Var

    /** Shorthand for `varOf("Y")`. */
    @JsName("Y")
    val Y: Var

    /** Shorthand for `varOf("Z")`. */
    @JsName("Z")
    val Z: Var

    companion object {
        /** Creates a new [VariablesProvider] backed by [scope] (an empty [Scope], by default). */
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(scope: Scope = Scope.empty()): VariablesProvider = VariablesProviderImpl(scope)
    }
}
