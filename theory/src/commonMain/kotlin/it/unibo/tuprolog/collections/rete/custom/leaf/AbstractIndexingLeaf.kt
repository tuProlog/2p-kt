package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.AbstractReteNode
import it.unibo.tuprolog.collections.rete.custom.IndexingLeaf
import it.unibo.tuprolog.unify.Unificator

internal abstract class AbstractIndexingLeaf(
    unificator: Unificator,
) : AbstractReteNode(unificator),
    IndexingLeaf {
    override fun invalidateCache() {
        // do nothing
    }
}
