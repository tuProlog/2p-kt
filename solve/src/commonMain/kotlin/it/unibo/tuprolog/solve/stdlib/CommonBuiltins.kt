package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.impl.AbstractLibrary
import it.unibo.tuprolog.solve.primitive.Primitive

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
