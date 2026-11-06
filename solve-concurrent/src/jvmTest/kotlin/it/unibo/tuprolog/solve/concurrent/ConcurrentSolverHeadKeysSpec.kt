package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.IndexKey

/** JVM only: concurrent solving is not implemented on Kotlin/JS (see `CoroutinesUtils`). */
class ConcurrentSolverHeadKeysSpec :
    FunSpec({
        test("custom-head-keys/ignores-directive-candidates") {
            val unificator =
                object : AbstractWorklistUnificator() {
                    override fun indexKey(term: Term): IndexKey = IndexKey.Wildcard
                }
            val goal = Atom.of("p")
            val solver =
                ConcurrentSolverFactory.solverWithDefaultBuiltins(
                    unificator = unificator,
                    staticKb = Theory.of(unificator, Directive.of(Atom.of("init")), Fact.of(goal)),
                )
            solver.solveOnce(goal).isYes shouldBe true
        }
    })
