package it.unibo.tuprolog.solve

import kotlin.js.JsName

/**
 * Something that can describe itself to Prolog users.
 *
 * Help text is Markdown with Prolog-oriented signatures, e.g. `functor(+Term, -Name, -Arity)`.
 * Implementations may leave it empty when no documentation is available.
 */
interface Helpable {
    @JsName("help")
    val help: String
        get() = ""
}
