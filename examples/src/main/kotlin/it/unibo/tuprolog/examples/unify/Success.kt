package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

/**
 * Demonstrates the three ways of asking `it.unibo.tuprolog.unify.Unificator.default` whether two
 * terms unify, on a case where unification succeeds.
 *
 * The struct `father(abraham, isaac)` is unified against the template `father(X, isaac)`. Since
 * `X` can be bound to `abraham` while the rest of the structure matches, unification succeeds:
 * [Unificator.mgu][it.unibo.tuprolog.unify.Unificator.mgu] returns the most general unifier
 * `{X -> abraham}`, [Unificator.match][it.unibo.tuprolog.unify.Unificator.match] returns `true`,
 * and [Unificator.unify][it.unibo.tuprolog.unify.Unificator.unify] returns the unified term
 * `father(abraham, isaac)`. Contrast this with the `Failure` example in this same package, which
 * runs the same three operations on terms that do not unify.
 *
 * Running this example prints `{X_0=abraham}`, `true`, and `father(abraham, isaac)`.
 */
fun main() {
    val unificator = Unificator.default

    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Var.of("X"), Atom.of("isaac"))

    val substitution: Substitution = unificator.mgu(term, template)
    val match: Boolean = unificator.match(term, template)
    val unified: Term? = unificator.unify(term, template)

    println(substitution) // {X_0=abraham}
    println(match) // true
    println(unified) // father(abraham, isaac)
}
