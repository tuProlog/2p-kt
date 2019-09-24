package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.HaltException

/**
 * Utils singleton to help testing [Solution]
 *
 * @author Enrico
 */
internal object SolutionUtils {

    private val solutionScope = Scope.empty()
    internal val aQuery = with(solutionScope) { Struct.of("f", varOf("A")) }
    internal val querySignature = aQuery.extractSignature()
    internal val queryArgList = aQuery.argsList
    internal val aSubstitution = with(solutionScope) { Substitution.of(varOf("A"), Struct.of("c", varOf("B"))) }
    internal val theQuerySolved = with(solutionScope) { Struct.of("f", Struct.of("c", varOf("B"))) }
    internal val anException = HaltException(context = DummyInstances.executionContext)

}
