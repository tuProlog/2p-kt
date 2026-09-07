package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Deobjectifier] of [Theory] instances: rebuilds a [Theory] from a plain list following the
 * same shape produced by [TheoryObjectifier] (a plain list of [TermObjectifier]-shaped clauses).
 * Each element of the list is deobjectified with [TermDeobjectifier] and must, in turn, denote a
 * clause. Used e.g. by a [TheoryDeserializer] after parsing a JSON/YAML/XML document into such a
 * plain list.
 *
 * Besides the usual [DeobjectificationException] (thrown when the top-level object is not a
 * list, or one of its elements does not have the shape expected for a
 * [it.unibo.tuprolog.core.Term]), implementations may also throw a `ClassCastException` if an
 * element, once deobjectified with [TermDeobjectifier], denotes a
 * [it.unibo.tuprolog.core.Term] that is not a [it.unibo.tuprolog.core.Clause] (e.g. a plain
 * [it.unibo.tuprolog.core.Struct] with neither a `"head"` nor a `"body"` key).
 *
 * @see TheoryObjectifier for the inverse conversion and the exact object shape recognized here.
 */
interface TheoryDeobjectifier : Deobjectifier<Theory> {
    companion object {
        /** The platform-default [TheoryDeobjectifier] implementation. */
        @JsName("default")
        @JvmStatic
        val default: TheoryDeobjectifier
            get() = theoryDeobjectifier()
    }
}
