package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.unify.Unificator.Companion.unifyWith

/**
 * Demonstrates the infix extension functions
 * [mguWith][it.unibo.tuprolog.unify.Unificator.Companion.mguWith],
 * [matches][it.unibo.tuprolog.unify.Unificator.Companion.matches] and
 * [unifyWith][it.unibo.tuprolog.unify.Unificator.Companion.unifyWith], which offer a
 * term-centric, operator-like syntax over `it.unibo.tuprolog.unify.Unificator.default` as an
 * alternative to calling `mgu`/`match`/`unify` directly on a `Unificator` instance.
 *
 * The same unification performed in the `Success` example of this package (`father(abraham,
 * isaac)` against `father(X, isaac)`) is expressed here as `term mguWith template`, `term matches
 * template` and `term unifyWith template`, which reads more naturally when the default
 * unificator is all that is needed.
 *
 * Running this example prints `{X_0=abraham}`, `true`, and `father(abraham, isaac)`.
 */
fun main() {
    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Var.of("X"), Atom.of("isaac"))

    println(term mguWith template) // {X_0=abraham}
    println(term matches template) // true
    println(term unifyWith template) // father(abraham, isaac)
}
