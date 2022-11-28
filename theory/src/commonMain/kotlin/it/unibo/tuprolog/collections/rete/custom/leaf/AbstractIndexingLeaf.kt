package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.IndexingLeaf
import it.unibo.tuprolog.unify.Unificator

internal abstract class AbstractIndexingLeaf(
    override val unificator: Unificator
) : IndexingLeaf {
    override fun invalidateCache() {
        /* do nothing */
    }
}
