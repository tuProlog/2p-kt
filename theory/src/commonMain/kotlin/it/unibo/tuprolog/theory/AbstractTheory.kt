package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.TheoryUtils.checkClauseCorrect
import it.unibo.tuprolog.theory.TheoryUtils.checkClausesCorrect
import it.unibo.tuprolog.unify.Unificator

internal abstract class AbstractTheory(override val tags: Map<String, Any>) : Theory {

    abstract override var unificator: Unificator
        protected set

    override fun toImmutableTheory(): Theory = this

    override fun plus(clause: Clause): Theory = super.plus(checkClauseCorrect(clause))

    override fun contains(clause: Clause): Boolean =
        get(clause).any()

    override fun contains(head: Struct): Boolean =
        contains(Rule.of(head, Var.anonymous()))

    override fun contains(indicator: Indicator): Boolean =
        get(indicator).any()

    override fun get(head: Struct): Sequence<Rule> = get(Rule.of(head, Var.anonymous())).map { it.castToRule() }

    override fun get(indicator: Indicator): Sequence<Rule> {
        require(indicator.isWellFormed) { "The provided indicator is not well formed: $indicator" }

        return get(
            Rule.of(
                Struct.of(indicator.indicatedName!!, (1..indicator.indicatedArity!!).map { Var.anonymous() }),
                Var.anonymous()
            )
        ).map { it.castToRule() }
    }

    override fun abolish(indicator: Indicator): Theory {
        require(indicator.isWellFormed) { "The provided indicator is not well formed: $indicator" }

        return retractAll(Struct.template(indicator.indicatedName!!, indicator.indicatedArity!!)).theory
    }

    override fun toString(): String = "${this::class.simpleName}{ ${clauses.joinToString(". ")} }"

    override fun toString(asPrologText: Boolean): String =
        if (asPrologText) {
            clauses.joinToString(".\n", "", ".\n")
        } else {
            toString()
        }

    override fun iterator(): Iterator<Clause> = clauses.iterator()

    final override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Theory) return false

        return equals(other, true)
    }

    final override fun equals(other: Theory, useVarCompleteName: Boolean): Boolean {
        if (this === other) return true

        val i = clauses.iterator()
        val j = other.clauses.iterator()

        while (i.hasNext() && j.hasNext()) {
            if (i.next().equals(j.next(), useVarCompleteName).not()) {
                return false
            }
        }

        return i.hasNext() == j.hasNext()
    }

    override fun hashCode(): Int {
        val base = AbstractTheory::class.simpleName.hashCode()
        var result = (base xor (base ushr 32))
        for (clause in clauses) {
            result = 31 * result + clause.hashCode()
        }
        return result
    }

    override val size: Long
        get() {
            var i: Long = 0
            val iter = iterator()
            while (iter.hasNext()) { iter.next(); i++ }
            return i
        }

    override val isEmpty: Boolean
        get() = !isNonEmpty

    override val isNonEmpty: Boolean
        get() = iterator().hasNext()

    override fun setUnificator(unificator: Unificator): Theory =
        if (unificator != this.unificator) {
            createNewTheory(clauses.asSequence(), unificator = unificator)
        } else {
            this
        }

    override fun plus(theory: Theory): Theory =
        if (theory.isNonEmpty) {
            createNewTheory(clauses.asSequence() + checkClausesCorrect(theory.clauses.asSequence()))
        } else {
            this
        }

    override fun assertA(clause: Clause): Theory =
        createNewTheory(checkClausesCorrect(clause) + clauses.asSequence())

    override fun assertA(clauses: Iterable<Clause>): Theory =
        createNewTheory(checkClausesCorrect(clauses.asSequence()) + this.clauses.asSequence())

    override fun assertA(clauses: Sequence<Clause>): Theory =
        createNewTheory(checkClausesCorrect(clauses) + this.clauses.asSequence())

    override fun assertZ(clause: Clause): Theory =
        createNewTheory(clauses.asSequence() + checkClausesCorrect(sequenceOf(clause)))

    override fun assertZ(clauses: Iterable<Clause>): Theory =
        createNewTheory(this.clauses.asSequence() + checkClausesCorrect(clauses).asSequence())

    override fun assertZ(clauses: Sequence<Clause>): Theory =
        createNewTheory(this.clauses.asSequence() + checkClausesCorrect(clauses))

    protected abstract fun createNewTheory(
        clauses: Sequence<Clause>,
        tags: Map<String, Any> = this.tags,
        unificator: Unificator = this.unificator
    ): AbstractTheory

    override fun retract(clauses: Sequence<Clause>): RetractResult<AbstractTheory> =
        retract(clauses.asIterable())

    abstract override fun retract(clauses: Iterable<Clause>): RetractResult<AbstractTheory>

    override fun clone(): Theory = this
}
