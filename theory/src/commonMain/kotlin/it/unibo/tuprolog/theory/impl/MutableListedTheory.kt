package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.addFirst

internal class MutableListedTheory
private constructor(
    override val clauses: MutableList<Clause>
) : AbstractListedTheory(clauses), MutableTheory {

    constructor(clauses: Iterable<Clause>) : this(clauses.toMutableList()) {
        checkClausesCorrect(clauses)
    }

    constructor(clauses: Sequence<Clause>) : this(clauses.toMutableList()) {
        checkClausesCorrect(clauses)
    }

    override fun createNewTheory(clauses: Sequence<Clause>): AbstractTheory {
        return MutableListedTheory(clauses)
    }

    override fun retract(clause: Clause): RetractResult<MutableListedTheory> {
        val i = clauses.listIterator()
        while (i.hasNext()) {
            val c = i.next()
            if (c matches clause) {
                i.remove()
                return RetractResult.Success(this, listOf(c))
            }
        }
        return RetractResult.Failure(this)
    }

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableListedTheory> {
        val i = this.clauses.listIterator()
        val retracted = mutableListOf<Clause>()
        while (i.hasNext()) {
            val c = i.next()
            for (clause in clauses) {
                if (c matches clause) {
                    retracted.add(c)
                    i.remove()
                }
            }
        }
        return if (retracted.isEmpty()) RetractResult.Failure(this) else RetractResult.Success(this, retracted)
    }

    override fun retractAll(clause: Clause): RetractResult<MutableListedTheory> {
        val i = clauses.listIterator()
        val retracted = mutableListOf<Clause>()
        while (i.hasNext()) {
            val c = i.next()
            if (c matches clause) {
                retracted.add(c)
                i.remove()
            }
        }
        return if (retracted.isEmpty()) RetractResult.Failure(this) else RetractResult.Success(this, retracted)
    }

    override fun plus(clause: Clause): MutableListedTheory {
        return assertZ(clause)
    }

    override fun plus(theory: Theory): MutableListedTheory {
        return assertZ(theory)
    }

    override fun assertA(clause: Clause): MutableListedTheory {
        return this.also { it.clauses.addFirst(clause) }
    }

    override fun assertA(clauses: Iterable<Clause>): MutableListedTheory {
        return this.also { it.clauses.addFirst(clauses) }
    }

    override fun assertA(clauses: Sequence<Clause>): MutableListedTheory {
        return assertA(clauses.asIterable())
    }

    override fun assertZ(clause: Clause): MutableListedTheory {
        return this.also { it.clauses.add(clause) }
    }

    override fun assertZ(clauses: Iterable<Clause>): MutableListedTheory {
        return this.also { it.clauses.addAll(clauses) }
    }

    override fun assertZ(clauses: Sequence<Clause>): MutableListedTheory {
        return assertZ(clauses.asIterable())
    }

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableListedTheory> {
        return retract(clauses.asIterable())
    }

    override fun abolish(indicator: Indicator): MutableListedTheory {
        return super.abolish(indicator) as MutableListedTheory
    }
}
