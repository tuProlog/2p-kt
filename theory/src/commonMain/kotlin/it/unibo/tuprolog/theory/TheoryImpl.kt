package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import kotlin.collections.List

class TheoryImpl : Theory {

    private val rete: ReteTree

    override val clauses: List<Clause> by lazy {
        with(this) {
            return@lazy rete.clauses.toList()
        }
    }

    constructor(clauses: List<Clause>) {
        rete = ReteTree.of(clauses)
    }

    private constructor(reteTree: ReteTree) {
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
        return TheoryImpl(rete.clone().apply { put(clause, before = true) })
    }

    override fun assertZ(clause: Clause): Theory {
        return TheoryImpl(rete.clone().apply { put(clause, before = false) })
    }

    override fun retract(clause: Clause): RetractResult {
        val newTheory = rete.clone()
        val retracted = newTheory.remove(clause).toList()

        return if (retracted.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(TheoryImpl(newTheory), retracted)
        }
    }

    override fun retractAll(clause: Clause): RetractResult {
        val newTheory = rete.clone()
        val retracted = newTheory.removeAll(clause).toList()

        return if (retracted.isEmpty()) {
            RetractResult.Failure(this)
        } else {
            RetractResult.Success(TheoryImpl(newTheory), retracted)
        }
    }

    override fun iterator(): Iterator<Clause> {
        return clauses.iterator()
    }

    override fun toString(): String {
        return clauses.joinToString(".\n", "", ".\n")
    }

    override fun assertA(struct: Struct): Theory {
        return super.assertA(struct)
    }

    override fun assertZ(struct: Struct): Theory {
        return super.assertZ(struct)
    }

    override fun retract(head: Struct): RetractResult {
        return super.retract(head)
    }

    override fun retractAll(head: Struct): RetractResult {
        return super.retractAll(head)
    }
}