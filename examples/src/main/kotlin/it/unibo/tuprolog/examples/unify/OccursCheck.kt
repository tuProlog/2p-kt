package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.dsl.unify.prolog
import it.unibo.tuprolog.unify.Unificator

fun main() {
    prolog {
        Scope.empty {
            val term = "g"("X", "Y")
            val otherTerm = "g"("f"("X"), "a")

            val unificator = Unificator.default

            val mgu = unificator.mgu(term, otherTerm, occurCheckEnabled = false)
            println(mgu) // {X_0=f(X_0), Y_1=a} => WRONG
        }
    }
}
