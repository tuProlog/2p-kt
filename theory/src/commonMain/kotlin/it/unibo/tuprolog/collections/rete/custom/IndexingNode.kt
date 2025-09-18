package it.unibo.tuprolog.collections.rete.custom

/**Marker interface to expand the capabilites of a [ReteNode] to behave as an [IndexingLeaf
 */
internal interface IndexingNode :
    ReteNode,
    IndexingLeaf
