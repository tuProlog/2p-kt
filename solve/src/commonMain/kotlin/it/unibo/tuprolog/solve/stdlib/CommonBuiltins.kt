package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.impl.AbstractLibrary
import it.unibo.tuprolog.solve.primitive.Primitive

/**
 * The `prolog.lang` standard [it.unibo.tuprolog.solve.library.Library]: the ISO-mandated core built-in predicates,
 * arithmetic functions, and control-construct rules every 2P-Kt solver is expected to ship with.
 *
 * It merely assembles the other `Common*` singletons in this package: [CommonRules.clauses] as [clauses],
 * [CommonPrimitives.primitives] as [primitives], and [CommonFunctions.functions] as [functions], plus the default
 * operator table ([it.unibo.tuprolog.core.operators.OperatorSet.DEFAULT]). It is what
 * [it.unibo.tuprolog.solve.SolverFactory.defaultBuiltins] is expected to return, and what
 * `solverWithDefaultBuiltins(...)`/`mutableSolverWithDefaultBuiltins(...)` add on top of any other
 * [it.unibo.tuprolog.solve.library.Runtime].
 */
object CommonBuiltins : AbstractLibrary() {
    override val alias: String
        get() = "prolog.lang"

    override val operators: OperatorSet
        get() = OperatorSet.DEFAULT

    override val clauses: List<Clause>
        get() = CommonRules.clauses

    override val primitives: Map<Signature, Primitive>
        get() = CommonPrimitives.primitives

    override val functions: Map<Signature, LogicFunction>
        get() = CommonFunctions.functions
}
