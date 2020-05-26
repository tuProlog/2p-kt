package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.impl.IndexedTheory
import it.unibo.tuprolog.theory.impl.ListedTheory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Theory : Iterable<Clause> {

    /** All [Clause]s in this database */
    @JsName("clauses")
    val clauses: Iterable<Clause>

    /** Only [clauses] that are [Rule]s */
    @JsName("rules")
    val rules: Iterable<Rule>
        get() = clauses.filterIsInstance<Rule>()

    /** Only [clauses] that are [Directive]s */
    @JsName("directives")
    val directives: Iterable<Directive>
        get() = clauses.filterIsInstance<Directive>()

    /** The amount of clauses in this [Theory] */
    @JsName("size")
    val size: Long

    /** Adds given ClauseDatabase to this */
    @JsName("plusTheory")
    operator fun plus(theory: Theory): Theory

    /** Adds given Clause to this ClauseDatabase */
    @JsName("plus")
    operator fun plus(clause: Clause): Theory = assertZ(clause)

    /** Checks if given clause is contained in this database */
    @JsName("contains")
    operator fun contains(clause: Clause): Boolean

    /** Checks if given clause is present in this database */
    @JsName("containsHead")
    operator fun contains(head: Struct): Boolean

    /** Checks if clauses exists in this database having the specified [Indicator] as head; this should be [well-formed][Indicator.isWellFormed] */
    @JsName("containsIndicator")
    operator fun contains(indicator: Indicator): Boolean

    /** Retrieves matching clauses from this database */
    @JsName("get")
    operator fun get(clause: Clause): Sequence<Clause>

    /** Retrieves matching rules from this database */
    @JsName("getByHead")
    operator fun get(head: Struct): Sequence<Rule>

    /** Retrieves all rules in this database having the specified [Indicator] as head; this should be [well-formed][Indicator.isWellFormed] */
    @JsName("getByIndicator")
    operator fun get(indicator: Indicator): Sequence<Rule>

    /** Adds given clause before all other clauses in this database */
    @JsName("assertA")
    fun assertA(clause: Clause): Theory

    /** Adds given clause before all other clauses in this database */
    @JsName("assertAFact")
    fun assertA(struct: Struct): Theory = assertA(Fact.of(struct))

    /** Adds given clause after all other clauses in this database */
    @JsName("assertZ")
    fun assertZ(clause: Clause): Theory

    /** Adds given clause after all other clauses in this database */
    @JsName("assertZFact")
    fun assertZ(struct: Struct): Theory = assertZ(Fact.of(struct))

    /** Tries to delete a matching clause from this database */
    @JsName("retract")
    fun retract(clause: Clause): RetractResult

    /** Tries to delete a matching clause from this database */
    @JsName("retractByHead")
    fun retract(head: Struct): RetractResult = retract(Rule.of(head, Var.anonymous()))

    /** Tries to delete all matching clauses from this database */
    @JsName("retractAll")
    fun retractAll(clause: Clause): RetractResult

    /** Tries to delete all matching clauses from this database */
    @JsName("retractAllByHead")
    fun retractAll(head: Struct): RetractResult = retractAll(Rule.of(head, Var.anonymous()))

    @JsName("abolish")
    fun abolish(indicator: Indicator): Theory = TODO()

    /** An enhanced toString that prints the database in a Prolog program format, if [asPrologText] is `true` */
    @JsName("toStringAsProlog")
    fun toString(asPrologText: Boolean): String

    companion object {

        private val EMPTY: Theory =
            indexedOf(emptySequence())

        /** Creates an empty ClauseDatabase */
        @JvmStatic
        @JsName("empty")
        fun empty(): Theory =
            EMPTY

        /** Creates an empty ClauseDatabase */
        @JvmStatic
        @JsName("emptyIndexed")
        fun emptyIndexed(): Theory =
            indexedOf(emptyList())

        /** Creates a ClauseDatabase with given clauses */
        @JvmStatic
        @JsName("indexedOf")
        fun indexedOf(vararg clause: Clause): Theory =
            indexedOf(clause.asIterable())

        /** Let developers easily create a ClauseDatabase programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("indexedOfScopes")
        fun indexedOf(vararg clause: Scope.() -> Clause): Theory =
            indexedOf(clause.map {
                Scope.empty(it)
            })

        /** Creates a ClauseDatabase with given clauses */
        @JvmStatic
        @JsName("indexedOfSequence")
        fun indexedOf(clauses: Sequence<Clause>): Theory =
            indexedOf(clauses.asIterable())

        /** Creates a ClauseDatabase with given clauses */
        @JvmStatic
        @JsName("indexedOfIterable")
        fun indexedOf(clauses: Iterable<Clause>): Theory =
            IndexedTheory(clauses)

        /** Creates an empty ClauseDatabase */
        @JvmStatic
        @JsName("emptyListed")
        fun emptyListed(): Theory =
            listedOf(emptySequence())

        /** Creates a ClauseDatabase with given clauses */
        @JvmStatic
        @JsName("listedOf")
        fun listedOf(vararg clause: Clause): Theory =
            listedOf(clause.asIterable())

        /** Let developers easily create a ClauseDatabase programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("listedOfScopes")
        fun listedOf(vararg clause: Scope.() -> Clause): Theory =
            listedOf(clause.map {
                Scope.empty(it)
            })

        /** Creates a ClauseDatabase with given clauses */
        @JvmStatic
        @JsName("listedOfSequence")
        fun listedOf(clauses: Sequence<Clause>): Theory =
            listedOf(clauses.asIterable())

        /** Creates a ClauseDatabase with given clauses */
        @JvmStatic
        @JsName("listedOfIterable")
        fun listedOf(clauses: Iterable<Clause>): Theory =
            ListedTheory(clauses)
    }
}
