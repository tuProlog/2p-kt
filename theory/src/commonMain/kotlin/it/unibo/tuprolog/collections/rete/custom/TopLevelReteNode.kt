package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.core.Clause

internal interface TopLevelReteNode : ReteNode {

    fun retractFirst(clause: Clause) : Sequence<Clause>

}