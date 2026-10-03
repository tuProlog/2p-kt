package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.assertExamplesHold
import it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins
import kotlin.test.Test

class TestDefaultBuiltinsExamples {
    @Test
    fun documentationExamplesOfEngineSpecificConstructsHold() {
        // the other examples document prolog.lang as a whole, whose solution order this engine doesn't preserve;
        // JVM only, since sub-solving (used by `\+/1`, `catch/3`, ...) is not implemented on JS yet
        val engineSpecific = setOf("!/0", ",/2", "\\+/1", "call/1", "catch/3", "throw/1")
        assertExamplesHold(
            DefaultBuiltins.documentation
                .mapKeys { (signature, _) -> "${signature.name}/${signature.arity}" }
                .filterKeys { it in engineSpecific },
        ) { ConcurrentSolverFactory.solverWithDefaultBuiltins() }
    }
}
