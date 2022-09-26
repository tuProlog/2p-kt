package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.FunctionWrapper
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Library.Companion.toMapEnsuringNoDuplicates
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.theory.Theory

@Suppress("MemberVisibilityCanBePrivate")
abstract class ExtensionLibrary(
    private val extended: Library
) : AbstractLibrary() {
    override val alias: String
        get() = extended.alias

    override val operators: OperatorSet by lazy {
        extended.operators + OperatorSet(additionalOperators)
    }

    open val additionalOperators: Iterable<Operator>
        get() = emptyList()

    final override val theory: Theory by lazy {
        extended.theory + Theory.of(additionalRules.map { it.implementation })
    }

    protected open val additionalRules: Iterable<RuleWrapper<*>>
        get() = emptyList()

    override val primitives: Map<Signature, Primitive> by lazy {
        val initial = extended.primitives.asSequence().map { it.toPair() }
        val additional = additionalPrimitives.asSequence().map { it.descriptionPair }
        (initial + additional).toMapEnsuringNoDuplicates()
    }

    protected open val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
        get() = emptyList()

    override val functions: Map<Signature, LogicFunction> by lazy {
        val initial = extended.functions.asSequence().map { it.toPair() }
        val additional = additionalFunctions.asSequence().map { it.descriptionPair }
        (initial + additional).toMapEnsuringNoDuplicates()
    }

    protected open val additionalFunctions: Iterable<FunctionWrapper<*>>
        get() = emptyList()
}
