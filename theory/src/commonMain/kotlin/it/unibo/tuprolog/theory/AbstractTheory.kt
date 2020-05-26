package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*

internal abstract class AbstractTheory : Theory {

    override fun plus(clause: Clause): Theory = super.plus(TheoryUtils.checkClauseCorrect(clause))

    override fun contains(clause: Clause): Boolean = get(clause).any()

    override fun contains(head: Struct): Boolean = contains(Rule.of(head, Var.anonymous()))

    override fun contains(indicator: Indicator): Boolean = get(indicator).any()

    override fun get(head: Struct): Sequence<Rule> = get(Rule.of(head, Var.anonymous())).map { it as Rule }

    override fun get(indicator: Indicator): Sequence<Rule> {
        require(indicator.isWellFormed) { "The provided indicator is not well formed: $indicator" }

        return get(
            Rule.of(
                Struct.of(indicator.indicatedName!!, (1..indicator.indicatedArity!!).map { Var.anonymous() }),
                Var.anonymous()
            )
        ).map { it as Rule }
    }

    override fun toString(): String = "ClauseDatabase(clauses=$clauses)"

    override fun toString(asPrologText: Boolean): String = when (asPrologText) {
        true -> clauses.joinToString(".\n", "", ".\n")
        false -> toString()
    }

    override fun iterator(): Iterator<Clause> = clauses.iterator()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (other !is Theory) return false

        val i = clauses.iterator()
        val j = other.clauses.iterator()

        while (i.hasNext() && j.hasNext()) {
            if (i.next() != j.next()) {
                return false
            }
        }

        return i.hasNext() == j.hasNext()
    }

    override fun hashCode(): Int {
        val base = "AbstractClauseDatabase".hashCode()
        var result = (base xor (base ushr 32))
        for (clause in clauses) {
            result = 31 * result + clause.hashCode()
        }
        return result
    }

    override val size: Long by lazy {
        var i: Long = 0
        for (clause in clauses) {
            i++
        }
        i
    }

}