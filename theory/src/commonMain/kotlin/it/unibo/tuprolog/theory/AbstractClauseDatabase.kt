package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.rete.clause.ReteTree
import kotlin.collections.List

internal abstract class AbstractClauseDatabase : ClauseDatabase {

    override val rules: List<Rule> by lazy { super.rules.toList() }

    override val directives: List<Directive> by lazy { super.directives.toList() }

    override fun plus(clause: Clause): ClauseDatabase = super.plus(checkClauseCorrect(clause))

    override fun contains(clause: Clause): Boolean = get(clause).any()

    override fun contains(head: Struct): Boolean = contains(Rule.of(head, Var.anonymous()))

    override fun contains(indicator: Indicator): Boolean = get(indicator).any()

    override fun get(head: Struct): Sequence<Rule> = get(Rule.of(head, Var.anonymous())).map { it as Rule }

    override fun get(indicator: Indicator): Sequence<Rule> {
        require(indicator.isWellFormed) { "Provided indicator should be wellFormed! $indicator" }

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AbstractClauseDatabase

        if (clauses != other.clauses) return false

        return true
    }

    override fun hashCode(): Int = clauses.hashCode()

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