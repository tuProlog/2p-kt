package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * An [Objectifier] of [Theory] instances: since a [Theory] is itself an
 * `Iterable<it.unibo.tuprolog.core.Clause>`, it is objectified as a plain list holding the
 * [TermObjectifier]-produced representation of each of its clauses, in iteration order. This is
 * what lets a [TheorySerializer] render a whole knowledge base as JSON, YAML, or XML.
 *
 * A [TheoryDeobjectifier] recognizes exactly this shape, so
 * `deobjectifier.deobjectify(objectifier.objectify(theory))` round-trips (up to variable renaming
 * and numeric literal formatting, and up to any clause reordering/deduplication the underlying
 * [Theory] implementation performs).
 *
 * @see TheoryDeobjectifier for the inverse conversion.
 */
interface TheoryObjectifier : Objectifier<Theory> {
    companion object {
        /** The platform-default [TheoryObjectifier] implementation. */
        @JsName("default")
        @JvmStatic
        val default: TheoryObjectifier
            get() = theoryObjectifier()
    }
}
