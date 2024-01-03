package it.unibo.tuprolog.solve.streams.stdlib

import it.unibo.tuprolog.solve.library.impl.ExtensionLibrary
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Call
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Catch
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Cut
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Not
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Throw

object DefaultBuiltins : ExtensionLibrary(CommonBuiltins) {
    override val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
        get() =
            listOf(
                Call,
                Catch,
                Conjunction,
                Cut,
                Throw,
                Not,
            )
}
