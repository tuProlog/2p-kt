package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.impl.IndexedTheory
import it.unibo.tuprolog.theory.impl.ListedTheory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Taggable
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Theory : Iterable<Clause>, Taggable<Theory> {

    @JsName("unificator")
    val unificator: Unificator

    @JsName("isMutable")
    val isMutable: Boolean
        get() = false

    @JsName("setUnificator")
    fun setUnificator(unificator: Unificator): Theory

    @JsName("toMutableTheory")
    fun toMutableTheory(): MutableTheory

    @JsName("toImmutableTheory")
    fun toImmutableTheory(): Theory

    /** All [Clause]s in this theory */
    @JsName("clauses")
    val clauses: Iterable<Clause>

    /** Only [clauses] that are [Rule]s */
    @JsName("rules")
    val rules: Iterable<Rule>
        get() = clauses.asSequence().map { it.asRule() }.filterNotNull().asIterable()

    /** Only [clauses] that are [Directive]s */
    @JsName("directives")
    val directives: Iterable<Directive>
        get() = clauses.asSequence().map { it.asDirective() }.filterNotNull().asIterable()

    /** The amount of clauses in this [Theory] */
    @JsName("size")
    val size: Long

    /** Whether this [Theory] is empty or not */
    @JsName("isEmpty")
    val isEmpty: Boolean

    /** Whether this [Theory] is full or not */
    @JsName("isNonEmpty")
    val isNonEmpty: Boolean

    /** Adds given [Theory] to this */
    @JsName("plusTheory")
    operator fun plus(theory: Theory): Theory

    /** Adds given Clause to this [Theory] */
    @JsName("plus")
    operator fun plus(clause: Clause): Theory = assertZ(clause)

    /** Checks if given clause is contained in this theory */
    @JsName("contains")
    operator fun contains(clause: Clause): Boolean

    /** Checks if given clause is present in this theory */
    @JsName("containsHead")
    operator fun contains(head: Struct): Boolean

    /** Checks if clauses exists in this theory having the specified [Indicator] as head; this should be [well-formed][Indicator.isWellFormed] */
    @JsName("containsIndicator")
    operator fun contains(indicator: Indicator): Boolean

    /** Retrieves matching clauses from this theory */
    @JsName("get")
    operator fun get(clause: Clause): Sequence<Clause>

    /** Retrieves matching rules from this theory */
    @JsName("getByHead")
    operator fun get(head: Struct): Sequence<Rule>

    /** Retrieves all rules in this theory having the specified [Indicator] as head; this should be [well-formed][Indicator.isWellFormed] */
    @JsName("getByIndicator")
    operator fun get(indicator: Indicator): Sequence<Rule>

    /** Adds given clause before all other clauses in this theory */
    @JsName("assertA")
    fun assertA(clause: Clause): Theory

    /** Adds given clause before all other clauses in this theory */
    @JsName("assertAFact")
    fun assertA(struct: Struct): Theory = assertA(Fact.of(struct))

    /** Adds the given clauses before all other clauses in this theory */
    @JsName("assertAIterable")
    fun assertA(clauses: Iterable<Clause>): Theory

    /** Adds the given clauses before all other clauses in this theory */
    @JsName("assertASequence")
    fun assertA(clauses: Sequence<Clause>): Theory

    /** Adds given clause after all other clauses in this theory */
    @JsName("assertZ")
    fun assertZ(clause: Clause): Theory

    /** Adds given clause after all other clauses in this theory */
    @JsName("assertZFact")
    fun assertZ(struct: Struct): Theory = assertZ(Fact.of(struct))

    /** Adds the given clauses after all other clauses in this theory */
    @JsName("assertZIterable")
    fun assertZ(clauses: Iterable<Clause>): Theory

    /** Adds the given clauses after all other clauses in this theory */
    @JsName("assertZSequence")
    fun assertZ(clauses: Sequence<Clause>): Theory

    /** Tries to delete a matching clause from this theory */
    @JsName("retract")
    fun retract(clause: Clause): RetractResult<Theory>

    /** Tries to delete the matching clauses from this theory */
    @JsName("retractIterable")
    fun retract(clauses: Iterable<Clause>): RetractResult<Theory>

    /** Tries to delete the matching clauses from this theory */
    @JsName("retractSequence")
    fun retract(clauses: Sequence<Clause>): RetractResult<Theory>

    /** Tries to delete a matching clause from this theory */
    @JsName("retractByHead")
    fun retract(head: Struct): RetractResult<Theory> =
        retract(Rule.of(head, Var.anonymous()))

    /** Tries to delete all matching clauses from this theory */
    @JsName("retractAll")
    fun retractAll(clause: Clause): RetractResult<Theory>

    /** Tries to delete all matching clauses from this theory */
    @JsName("retractAllByHead")
    fun retractAll(head: Struct): RetractResult<Theory> =
        retractAll(Rule.of(head, Var.anonymous()))

    @JsName("abolish")
    fun abolish(indicator: Indicator): Theory

    /** An enhanced toString that prints the theory in a Prolog program format, if [asPrologText] is `true` */
    @JsName("toStringAsProlog")
    fun toString(asPrologText: Boolean): String

    @JsName("equalsUsingVarCompleteNames")
    fun equals(other: Theory, useVarCompleteName: Boolean): Boolean

    @JsName("clone")
    fun clone(): Theory

    companion object {

        /** Creates an empty [Theory] */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator): Theory =
            indexedOf(unificator, emptySequence())

        @JvmStatic
        @JsName("emptyWithDefaultUnificator")
        fun empty(): Theory = empty(Unificator.default)

        /** Creates a [Theory], containing the given clauses */
        @JvmStatic
        @JsName("of")
        fun of(unificator: Unificator, vararg clause: Clause): Theory =
            indexedOf(unificator, *clause)

        @JvmStatic
        @JsName("ofWithDefaultUnificator")
        fun of(vararg clause: Clause): Theory = indexedOf(Unificator.default, *clause)

        /** Creates a [Theory], containing the given clauses */
        @JvmStatic
        @JsName("ofIterable")
        fun of(unificator: Unificator, clauses: Iterable<Clause>): Theory =
            indexedOf(unificator, clauses)

        @JvmStatic
        @JsName("ofIterableWithDefaultUnificator")
        fun of(clauses: Iterable<Clause>): Theory = indexedOf(Unificator.default, clauses)

        /** Creates a [Theory], containing the given clauses */
        @JvmStatic
        @JsName("ofSequence")
        fun of(unificator: Unificator, clauses: Sequence<Clause>): Theory =
            indexedOf(unificator, clauses)

        @JvmStatic
        @JsName("ofSequenceWithDefaultUnificator")
        fun of(clauses: Sequence<Clause>): Theory = indexedOf(Unificator.default, clauses)

        /** Let developers easily create a [Theory], while avoiding variables names clashing by using a
         * different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("ofScopes")
        fun of(unificator: Unificator, vararg clauses: Scope.() -> Clause): Theory =
            indexedOf(unificator, *clauses)

        @JvmStatic
        @JsName("ofScopesWithDefaultUnificator")
        fun of(vararg clauses: Scope.() -> Clause): Theory = indexedOf(Unificator.default, *clauses)

        /** Creates an empty [Theory] backed by an indexed data structure */
        @JvmStatic
        @JsName("emptyIndexed")
        fun emptyIndexed(unificator: Unificator): Theory =
            indexedOf(unificator, emptyList())

        /** Creates a [Theory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOf")
        fun indexedOf(unificator: Unificator, vararg clause: Clause): Theory =
            indexedOf(unificator, clause.asIterable())

        /** Let developers easily create a [Theory] backed by an indexed data structure, while avoiding variables names
         * clashing by using a different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("indexedOfScopes")
        fun indexedOf(unificator: Unificator, vararg clauses: Scope.() -> Clause): Theory =
            indexedOf(
                unificator,
                clauses.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [Theory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOfSequence")
        fun indexedOf(unificator: Unificator, clauses: Sequence<Clause>): Theory =
            indexedOf(unificator, clauses.asIterable())

        /** Creates a [Theory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOfIterable")
        fun indexedOf(unificator: Unificator, clauses: Iterable<Clause>): Theory =
            IndexedTheory(unificator, clauses)

        /** Creates a [Theory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("emptyListed")
        fun emptyListed(unificator: Unificator): Theory =
            listedOf(unificator, emptySequence())

        /** Creates a [Theory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOf")
        fun listedOf(unificator: Unificator, vararg clause: Clause): Theory =
            listedOf(unificator, clause.asIterable())

        /** Let developers easily create a [Theory] backed by a list, while avoiding variables names
         * clashing by using a different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("listedOfScopes")
        fun listedOf(unificator: Unificator, vararg clause: Scope.() -> Clause): Theory =
            listedOf(
                unificator,
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [Theory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOfSequence")
        fun listedOf(unificator: Unificator, clauses: Sequence<Clause>): Theory =
            listedOf(unificator, clauses.asIterable())

        /** Creates a [Theory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOfIterable")
        fun listedOf(unificator: Unificator, clauses: Iterable<Clause>): Theory =
            ListedTheory(unificator, clauses)
    }
}
