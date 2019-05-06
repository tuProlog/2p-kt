package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import kotlin.collections.List

class TheoryImpl : Theory {

    private val rete: ReteTree

    override val clauses: List<Clause>

    constructor(clauses: List<Clause>) {
        this.clauses = clauses
        rete = ReteTree.of(clauses)
    }

    private constructor(clauses: List<Clause>, reteTree: ReteTree) {
        this.clauses = clauses
        rete = reteTree
    }

    override fun plus(theory: Theory): Theory {
        return TheoryImpl(clauses + theory.clauses)
    }

    override fun contains(clause: Clause): Boolean {
        return rete.get(clause).any()
    }

    override fun contains(head: Struct): Boolean {
        return contains(Rule.of(head, Var.anonymous()))
    }

    override fun get(clause: Clause): Sequence<Clause> {
        return rete.get(clause)
    }

    override fun get(head: Struct): Sequence<Clause> {
        return get(Rule.of(head, Var.anonymous()))
    }

    override fun assertA(clause: Clause): Theory {
        return TheoryImpl(clauses + listOf(clause), rete.clone().apply { put(clause) })
    }

    override fun assertZ(clause: Clause): Theory {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retract(clause: Clause): Theory {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retractAll(clause: Clause): Theory {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iterator(): Iterator<Clause> {
        return clauses.iterator()
    }


}