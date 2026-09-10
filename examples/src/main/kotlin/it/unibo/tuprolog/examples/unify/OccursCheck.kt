package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.dsl.unify.logicProgramming
import it.unibo.tuprolog.unify.Unificator

/**
 * Demonstrates why the occurs check matters, by deliberately disabling it via
 * `occurCheckEnabled = false` on
 * [Unificator.mgu][it.unibo.tuprolog.unify.Unificator.mgu].
 *
 * Unifying `g(X, Y)` with `g(f(X), a)` requires binding `X` to `f(X)`, a term that contains `X`
 * itself. Standard (sound) unification rejects this as a cyclic, infinite term via the occurs
 * check; here the check is switched off, so the unifier happily returns the unsound binding
 * `{X -> f(X), Y -> a}`, which is marked `WRONG` in the inline comment because `X` occurs in its
 * own binding. This illustrates the soundness/performance trade-off exposed by the
 * `occurCheckEnabled` parameter: it defaults to `true` on
 * [Unificator.mgu][it.unibo.tuprolog.unify.Unificator.mgu], and only explicitly disabling it (as
 * done here) exposes this kind of unsound, self-referential binding.
 *
 * Running this example prints `{X_0=f(X_0), Y_1=a}`.
 */
fun main() {
    logicProgramming {
        Scope.empty {
            val term = "g"("X", "Y")
            val otherTerm = "g"("f"("X"), "a")

            val unificator = Unificator.default

            val mgu = unificator.mgu(term, otherTerm, occurCheckEnabled = false)
            println(mgu) // {X_0=f(X_0), Y_1=a} => WRONG
        }
    }
}
