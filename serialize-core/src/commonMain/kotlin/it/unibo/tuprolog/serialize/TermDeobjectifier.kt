package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Deobjectifier] of [Term]s: rebuilds a [Term] from a plain object tree following the same
 * shape produced by [TermObjectifier] (see its documentation for the mapping scheme). Used e.g.
 * by a [TermDeserializer] after parsing a JSON/YAML/XML document into such a plain tree.
 *
 * @see TermObjectifier for the inverse conversion and the exact object shape recognized here.
 */
interface TermDeobjectifier : Deobjectifier<Term> {
    companion object {
        /** The platform-default [TermDeobjectifier] implementation. */
        @JsName("default")
        @JvmStatic
        val default: TermDeobjectifier
            get() = termDeobjectifier()
    }
}
