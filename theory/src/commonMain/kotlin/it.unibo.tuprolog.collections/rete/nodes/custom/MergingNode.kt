package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.core.Clause

interface MergingNode {

    fun retractAllOrdered(clause: Clause): Sequence<Clause>

    fun retractAllUnordered(clause: Clause): Sequence<Clause>

    fun retractFirstResult(clause: Clause): Sequence<Clause>

    fun retractAnyResult(clause: Clause): Sequence<Clause>

}