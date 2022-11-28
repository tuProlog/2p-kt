package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.impl.MutableIndexedTheory
import it.unibo.tuprolog.theory.impl.MutableListedTheory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface MutableTheory : Theory {

    override fun setUnificator(unificator: Unificator): MutableTheory

    override val isMutable: Boolean get() = true

    override fun toMutableTheory(): MutableTheory = this

    override operator fun plus(theory: Theory): MutableTheory

    override operator fun plus(clause: Clause): MutableTheory = assertZ(clause)

    override fun assertA(clause: Clause): MutableTheory

    override fun assertA(struct: Struct): MutableTheory = assertA(Fact.of(struct))

    override fun assertA(clauses: Iterable<Clause>): MutableTheory

    override fun assertA(clauses: Sequence<Clause>): MutableTheory

    override fun assertZ(clause: Clause): MutableTheory

    override fun assertZ(struct: Struct): MutableTheory = assertZ(Fact.of(struct))

    override fun assertZ(clauses: Iterable<Clause>): MutableTheory

    override fun assertZ(clauses: Sequence<Clause>): MutableTheory

    override fun retract(clause: Clause): RetractResult<MutableTheory>

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableTheory>

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableTheory>

    override fun retract(head: Struct): RetractResult<MutableTheory> =
        retract(Rule.of(head, Var.anonymous()))

    override fun retractAll(clause: Clause): RetractResult<MutableTheory>

    override fun retractAll(head: Struct): RetractResult<MutableTheory> =
        retractAll(Rule.of(head, Var.anonymous()))

    override fun abolish(indicator: Indicator): MutableTheory

    override fun replaceTags(tags: Map<String, Any>): MutableTheory

    override fun clone(): MutableTheory

    companion object {

        /** Creates an empty [MutableTheory] */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator): MutableTheory =
            indexedOf(unificator, emptyList())

        /** Creates a [MutableTheory], containing the given clauses */
        @JvmStatic
        @JsName("of")
        fun of(unificator: Unificator, vararg clause: Clause): MutableTheory =
            indexedOf(unificator, *clause)

        /** Creates a [MutableTheory], containing the given clauses */
        @JvmStatic
        @JsName("ofIterable")
        fun of(unificator: Unificator, clauses: Iterable<Clause>): MutableTheory =
            indexedOf(unificator, clauses)

        /** Creates a [MutableTheory], containing the given clauses */
        @JvmStatic
        @JsName("ofSequence")
        fun of(unificator: Unificator, clauses: Sequence<Clause>): MutableTheory =
            indexedOf(unificator, clauses)

        /** Let developers easily create a [MutableTheory], while avoiding variables names clashing by using a
         * different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("ofScopes")
        fun of(unificator: Unificator, vararg clauses: Scope.() -> Clause): MutableTheory =
            indexedOf(unificator, *clauses)

        /** Creates an empty [MutableTheory] backed by an indexed data structure */
        @JvmStatic
        @JsName("emptyIndexed")
        fun emptyIndexed(unificator: Unificator): MutableTheory =
            indexedOf(unificator, emptyList())

        /** Creates a [MutableTheory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOf")
        fun indexedOf(unificator: Unificator, vararg clause: Clause): MutableTheory =
            indexedOf(unificator, clause.asIterable())

        /** Let developers easily create a [MutableTheory] backed by an indexed data structure, while avoiding variables names
         * clashing by using a different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("indexedOfScopes")
        fun indexedOf(unificator: Unificator, vararg clauses: Scope.() -> Clause): MutableTheory =
            indexedOf(
                unificator,
                clauses.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [MutableTheory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOfSequence")
        fun indexedOf(unificator: Unificator, clauses: Sequence<Clause>): MutableTheory =
            indexedOf(unificator, clauses.asIterable())

        /** Creates a [MutableTheory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOfIterable")
        fun indexedOf(unificator: Unificator, clauses: Iterable<Clause>): MutableTheory =
            MutableIndexedTheory(unificator, clauses)

        /** Creates a [MutableTheory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("emptyListed")
        fun emptyListed(unificator: Unificator): MutableTheory =
            listedOf(unificator, emptySequence())

        /** Creates a [MutableTheory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOf")
        fun listedOf(unificator: Unificator, vararg clause: Clause): MutableTheory =
            listedOf(unificator, clause.asIterable())

        /** Let developers easily create a [MutableTheory] backed by a list, while avoiding variables names
         * clashing by using a different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("listedOfScopes")
        fun listedOf(unificator: Unificator, vararg clause: Scope.() -> Clause): MutableTheory =
            listedOf(
                unificator,
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [MutableTheory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOfSequence")
        fun listedOf(unificator: Unificator, clauses: Sequence<Clause>): MutableTheory =
            listedOf(unificator, clauses.asIterable())

        /** Creates a [MutableTheory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOfIterable")
        fun listedOf(unificator: Unificator, clauses: Iterable<Clause>): MutableTheory =
            MutableListedTheory(unificator, clauses)
    }
}
