package it.unibo.tuprolog.solve.testutils

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.extractSignature

/**
 * Utils singleton to help testing [Solution]
 *
 * @author Enrico
 */
internal object SolutionUtils {
    private val solutionScope = Scope.empty()
    internal val aQuery = with(solutionScope) { Struct.of("f", varOf("A")) }
    internal val querySignature = aQuery.extractSignature()
    internal val queryArgList = aQuery.args
    internal val aSubstitution = with(solutionScope) { Substitution.of(varOf("A"), Struct.of("c", varOf("B"))) }
    internal val theQuerySolved = with(solutionScope) { Struct.of("f", Struct.of("c", varOf("B"))) }
    internal val anException = ResolutionException(context = DummyInstances.executionContext)
}
