package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*

internal abstract class AbstractClauseDatabase : ClauseDatabase {

//    Così creavi e memorizzavi due nuove liste, occupando spazio inutilmente
//    inoltre rendevi il tipo di ritorno più specifico del dovuto, per tutte le sottoclassi
//    (mi sa che l'ha fatta siboni sta scelta, ne approfitto per correggerla, ma questo cambia un pò le cose sotto)
//    override val rules: List<Rule> by lazy { super.rules.toList() }
//    override val directives: List<Directive> by lazy { super.directives.toList() }

    override fun plus(clause: Clause): ClauseDatabase = super.plus(checkClauseCorrect(clause))

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
        // questa implementazione funziona anche se clauses non è una lista
        if (this === other) return true
        if (other == null) return false
        if (other !is ClauseDatabase) return false

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
        // questa implementazione funziona anche se clauses non è una lista
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

    /** Utility method to check clause well-formed property */
    protected fun checkClauseCorrect(clause: Clause) = clause.also {
        require(clause.isWellFormed) { "ClauseDatabase can contain only well formed clauses: this isn't $clause" }
    }

    /** Utility method to check more than one clause well-formed property */
    protected fun checkClausesCorrect(clauses: Iterable<Clause>) = clauses.also {
        require(clauses.all { it.isWellFormed }) {
            "ClauseDatabase can contain only well formed clauses: these aren't " +
                    "${clauses.filterNot { it.isWellFormed }.toList()}"
        }
    }

}