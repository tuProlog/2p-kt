package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.IndexingLeaf

internal abstract class AbstractIndexingLeaf : IndexingLeaf {
    override fun invalidateCache() {
        /* do nothing */
    }
}
