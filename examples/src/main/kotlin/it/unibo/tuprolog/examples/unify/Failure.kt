package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator

/**
 * Demonstrates the three ways of asking `it.unibo.tuprolog.unify.Unificator.default` whether two
 * terms unify, on a case where unification fails.
 *
 * The struct `father(abraham, isaac)` is unified against the template `father(isaac, abraham)`:
 * the two constant arguments are swapped, so no substitution can make the two terms equal.
 * [Unificator.mgu][it.unibo.tuprolog.unify.Unificator.mgu] returns a
 * `it.unibo.tuprolog.core.Substitution.Fail` (for which
 * [Substitution.isFailed][it.unibo.tuprolog.core.Substitution.isFailed] is `true`),
 * [Unificator.match][it.unibo.tuprolog.unify.Unificator.match] returns `false`, and
 * [Unificator.unify][it.unibo.tuprolog.unify.Unificator.unify] returns `null`. Contrast this with
 * the `Success` example in this same package, which runs the same three operations on terms that
 * do unify.
 *
 * Running this example prints `true` (is a `Fail`), `true` (`isFailed`), `false` (`match`), and
 * `null` (`unify`).
 */
fun main() {
    val unificator = Unificator.default

    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Atom.of("isaac"), Atom.of("abraham"))

    val substitution: Substitution = unificator.mgu(term, template)
    val match: Boolean = unificator.match(term, template)
    val unified: Term? = unificator.unify(term, template)

    println(substitution is Substitution.Fail) // true
    println(substitution.isFailed) // true
    println(match) // false
    println(unified) // null
}
