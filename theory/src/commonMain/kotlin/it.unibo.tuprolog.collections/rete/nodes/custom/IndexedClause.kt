package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.core.Clause

interface IndexedClause : Comparable<IndexedClause>{

    val index: Long

    val innerClause: Clause

    companion object {
        fun of(index: Long, clause: Clause): IndexedClause {
            return object : IndexedClause {
                override val index: Long
                     get() = index
                override val innerClause: Clause
                    get() = clause

                override fun compareTo(other: IndexedClause): Int =
                    (this.index - other.index).toInt()
            }
        }
    }

}