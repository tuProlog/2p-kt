package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.unify.Unificator

internal abstract class AbstractClauseCollection<Self : AbstractClauseCollection<Self>>
protected constructor(protected val rete: ReteTree) : ClauseCollection {

    override val unificator: Unificator
        get() = rete.unificator

    protected abstract val self: Self

    override val size: Int
        get() = rete.size

    override val directives: Iterable<Directive>
        get() = rete.directives.asIterable()

    override val rules: Iterable<Rule>
        get() = rete.rules.asIterable()

    override fun isEmpty(): Boolean = rete.isEmpty

    override fun isNonEmpty(): Boolean = !isEmpty()

    override fun contains(element: Clause): Boolean = rete.get(element).any()

    @Suppress("RedundantAsSequence")
    override fun containsAll(elements: Iterable<Clause>): Boolean = elements.asSequence().all { it in this }

    abstract override fun add(clause: Clause): Self

    abstract override fun addAll(clauses: Iterable<Clause>): Self

    abstract override fun retrieve(clause: Clause): RetrieveResult<out Self>

    abstract override fun retrieveAll(clause: Clause): RetrieveResult<out Self>

    override fun iterator(): Iterator<Clause> = rete.clauses.iterator()

    override fun toString(): String = "${this::class.simpleName}(${this.joinToString(", ")})"
}
