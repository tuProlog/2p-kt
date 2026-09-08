package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.core.Clause
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

/**
 * Base class for a [Library] that decorates an [extended] one, contributing everything the extended library already
 * has plus whatever [additionalOperators]/[additionalRules]/[additionalPrimitives]/[additionalFunctions] the
 * subclass overrides. Keeps [extended]'s [Library.alias].
 *
 * @param extended the [Library] this one extends with additional contributions.
 * @throws IllegalArgumentException (lazily, on first access of [primitives]/[functions]) if an
 * [additionalPrimitives]/[additionalFunctions] signature clashes with one already in [extended] or among the
 * additional ones themselves (see [Library.Companion.toMapEnsuringNoDuplicates]).
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class ExtensionLibrary(
    private val extended: Library,
) : AbstractLibrary() {
    override val alias: String
        get() = extended.alias

    override val operators: OperatorSet by lazy {
        extended.operators + OperatorSet(additionalOperators)
    }

    /** Extra [Operator]s to add on top of [extended]'s; empty by default. */
    open val additionalOperators: Iterable<Operator>
        get() = emptyList()

    final override val clauses: List<Clause> by lazy {
        (extended.clauses.asSequence() + additionalRules.asSequence().map { it.implementation }).toList()
    }

    /** Extra rules to add on top of [extended]'s [Library.clauses]; empty by default. */
    protected open val additionalRules: Iterable<RuleWrapper<*>>
        get() = emptyList()

    override val primitives: Map<Signature, Primitive> by lazy {
        val initial = extended.primitives.asSequence().map { it.toPair() }
        val additional = additionalPrimitives.asSequence().map { it.descriptionPair }
        (initial + additional).toMapEnsuringNoDuplicates()
    }

    /** Extra [Primitive]s to add on top of [extended]'s; empty by default. */
    protected open val additionalPrimitives: Iterable<PrimitiveWrapper<*>>
        get() = emptyList()

    override val functions: Map<Signature, LogicFunction> by lazy {
        val initial = extended.functions.asSequence().map { it.toPair() }
        val additional = additionalFunctions.asSequence().map { it.descriptionPair }
        (initial + additional).toMapEnsuringNoDuplicates()
    }

    /** Extra [LogicFunction]s to add on top of [extended]'s; empty by default. */
    protected open val additionalFunctions: Iterable<FunctionWrapper<*>>
        get() = emptyList()

    override fun hasRule(signature: Signature): Boolean = super<AbstractLibrary>.hasRule(signature)
}
