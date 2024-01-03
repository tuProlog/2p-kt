package it.unibo.tuprolog.solve.classic.stdlib

import it.unibo.tuprolog.solve.classic.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.classic.stdlib.rule.Call
import it.unibo.tuprolog.solve.classic.stdlib.rule.Catch
import it.unibo.tuprolog.solve.classic.stdlib.rule.Comma
import it.unibo.tuprolog.solve.classic.stdlib.rule.Cut
import it.unibo.tuprolog.solve.classic.stdlib.rule.NegationAsFailure
import it.unibo.tuprolog.solve.library.impl.ExtensionLibrary
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins

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
