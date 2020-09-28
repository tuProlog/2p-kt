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
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface MutableTheory : Theory {

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

    companion object {

        /** Creates an empty [MutableTheory] */
        @JvmStatic
        @JsName("empty")
        fun empty(): MutableTheory =
            indexedOf(emptyList())

        /** Creates a [MutableTheory], containing the given clauses */
        @JvmStatic
        @JsName("of")
        fun of(vararg clause: Clause): MutableTheory =
            indexedOf(*clause)

        /** Creates a [MutableTheory], containing the given clauses */
        @JvmStatic
        @JsName("ofIterable")
        fun of(clauses: Iterable<Clause>): MutableTheory =
            indexedOf(clauses)

        /** Creates a [MutableTheory], containing the given clauses */
        @JvmStatic
        @JsName("ofSequence")
        fun of(clauses: Sequence<Clause>): MutableTheory =
            indexedOf(clauses)

        /** Let developers easily create a [MutableTheory], while avoiding variables names clashing by using a
         * different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("ofScopes")
        fun of(vararg clauses: Scope.() -> Clause): MutableTheory =
            indexedOf(*clauses)

        /** Creates an empty [MutableTheory] backed by an indexed data structure */
        @JvmStatic
        @JsName("emptyIndexed")
        fun emptyIndexed(): MutableTheory =
            indexedOf(emptyList())

        /** Creates a [MutableTheory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOf")
        fun indexedOf(vararg clause: Clause): MutableTheory =
            indexedOf(clause.asIterable())

        /** Let developers easily create a [MutableTheory] backed by an indexed data structure, while avoiding variables names
         * clashing by using a different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("indexedOfScopes")
        fun indexedOf(vararg clauses: Scope.() -> Clause): MutableTheory =
            indexedOf(
                clauses.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [MutableTheory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOfSequence")
        fun indexedOf(clauses: Sequence<Clause>): MutableTheory =
            indexedOf(clauses.asIterable())

        /** Creates a [MutableTheory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOfIterable")
        fun indexedOf(clauses: Iterable<Clause>): MutableTheory =
            MutableIndexedTheory(clauses)

        /** Creates a [MutableTheory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("emptyListed")
        fun emptyListed(): MutableTheory =
            listedOf(emptySequence())

        /** Creates a [MutableTheory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOf")
        fun listedOf(vararg clause: Clause): MutableTheory =
            listedOf(clause.asIterable())

        /** Let developers easily create a [MutableTheory] backed by a list, while avoiding variables names
         * clashing by using a different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("listedOfScopes")
        fun listedOf(vararg clause: Scope.() -> Clause): MutableTheory =
            listedOf(
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [MutableTheory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOfSequence")
        fun listedOf(clauses: Sequence<Clause>): MutableTheory =
            listedOf(clauses.asIterable())

        /** Creates a [MutableTheory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOfIterable")
        fun listedOf(clauses: Iterable<Clause>): MutableTheory =
            MutableListedTheory(clauses)
    }
}
