package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * An [Objectifier] of [Term]s, implemented as a [TermVisitor] so that every [Term] sub-type is
 * converted into a plain object following a fixed, format-agnostic scheme. This scheme is what
 * lets a [TermSerializer] render any [Term] as JSON, YAML, or XML.
 *
 * The scheme used by the platform-provided implementations ([default]) is, informally:
 * - a [it.unibo.tuprolog.core.Var] becomes `{"var": "<name>"}`;
 * - an [it.unibo.tuprolog.core.Atom] becomes its `String` value (`true`/`false` for
 *   [it.unibo.tuprolog.core.Truth.TRUE]/[it.unibo.tuprolog.core.Truth.FALSE], the atom `"fail"`
 *   for [it.unibo.tuprolog.core.Truth.FAIL]);
 * - a small [it.unibo.tuprolog.core.Integer] becomes a plain number, a larger one (that overflows
 *   a platform `Long`/`Int`) becomes `{"integer": "<decimal string>"}`;
 * - a [it.unibo.tuprolog.core.Real] becomes `{"real": "<decimal string>"}`;
 * - a [it.unibo.tuprolog.core.Struct] becomes `{"fun": "<functor>", "args": [...]}`;
 * - a well-formed [it.unibo.tuprolog.core.List] becomes `{"list": [...]}`, a non-well-formed one
 *   additionally carries `"tail": ...`;
 * - a [it.unibo.tuprolog.core.Block] becomes `{"block": [...]}`, a
 *   [it.unibo.tuprolog.core.Tuple] becomes `{"tuple": [...]}`;
 * - a [it.unibo.tuprolog.core.Rule] becomes `{"head": ..., "body": ...}`, a
 *   [it.unibo.tuprolog.core.Fact] becomes `{"head": ...}`, a [it.unibo.tuprolog.core.Directive]
 *   becomes `{"body": ...}`.
 *
 * A [TermDeobjectifier] recognizes exactly this shape, so `deobjectifier.deobjectify(objectifier.objectify(term))`
 * round-trips (up to variable renaming and numeric literal formatting).
 *
 * @see TermDeobjectifier for the inverse conversion.
 */
interface TermObjectifier :
    Objectifier<Term>,
    TermVisitor<Any> {
    override fun objectify(value: Term): Any = value.accept(this)

    companion object {
        /** The platform-default [TermObjectifier] implementation. */
        @JsName("default")
        @JvmStatic
        val default: TermObjectifier
            get() = termObjectifier()
    }
}
