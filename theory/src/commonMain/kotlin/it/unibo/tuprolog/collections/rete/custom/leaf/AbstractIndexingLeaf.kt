package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.AbstractReteNode
import it.unibo.tuprolog.collections.rete.custom.IndexingLeaf
import it.unibo.tuprolog.unify.Unificator

internal abstract class AbstractIndexingLeaf(unificator: Unificator) : IndexingLeaf, AbstractReteNode(unificator) {
    override fun invalidateCache() {
        /* do nothing */
    }
}
