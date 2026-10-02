package it.unibo.tuprolog.solve.prolog.stdlib

import it.unibo.tuprolog.solve.library.impl.ExtensionLibrary
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.prolog.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.prolog.stdlib.rule.Call
import it.unibo.tuprolog.solve.prolog.stdlib.rule.Catch
import it.unibo.tuprolog.solve.prolog.stdlib.rule.Comma
import it.unibo.tuprolog.solve.prolog.stdlib.rule.Cut
import it.unibo.tuprolog.solve.prolog.stdlib.rule.NegationAsFailure
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins

/**
 * The [it.unibo.tuprolog.solve.library.Library] of built-in predicates the prolog engine adds on top of
 * [CommonBuiltins]: this is what `it.unibo.tuprolog.solve.prolog.PrologSolverFactory.defaultBuiltins` returns,
 * and what a solver built via `Solver.prolog.solverWithDefaultBuiltins()` is loaded with.
 *
 * Everything here exists specifically because this engine resolves goals through the finite-state machine
 * under `it.unibo.tuprolog.solve.prolog.fsm` rather than host-language recursion, so control constructs need an
 * engine-specific implementation: [it.unibo.tuprolog.solve.prolog.stdlib.primitive.Throw] is a `Primitive`
 * (`throw/1`), while [it.unibo.tuprolog.solve.prolog.stdlib.rule.Catch] (`catch/3`),
 * [it.unibo.tuprolog.solve.prolog.stdlib.rule.Call] (`call/1`),
 * [it.unibo.tuprolog.solve.prolog.stdlib.rule.Comma] (`','/2`),
 * [it.unibo.tuprolog.solve.prolog.stdlib.rule.Cut] (`'!'/0`) and
 * [it.unibo.tuprolog.solve.prolog.stdlib.rule.NegationAsFailure] (`'\+'/1`) are all `RuleWrapper`s expanded into
 * ordinary rule bodies that `StateRuleSelection`/`StateException` know how to special-case (cut transparency,
 * exception propagation) at the FSM level.
 */
object DefaultBuiltins : ExtensionLibrary(CommonBuiltins) {
    override val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
        get() =
            listOf(
                Throw,
            )

    override val additionalRules: Iterable<RuleWrapper<*>>
        get() =
            listOf(
                Catch,
                Call,
                Comma,
                Cut,
                NegationAsFailure.Fail,
                NegationAsFailure.Success,
            )
}
