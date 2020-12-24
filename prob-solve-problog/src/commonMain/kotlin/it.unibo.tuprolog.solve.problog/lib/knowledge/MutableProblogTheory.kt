package it.unibo.tuprolog.solve.problog.lib.knowledge

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.MappedMutableProblogTheory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface MutableProblogTheory : ProblogTheory, MutableTheory {

    override val isMutable: Boolean get() = true

    override fun toMutableTheory(): MutableProblogTheory = this

    override operator fun plus(theory: ProblogTheory): MutableProblogTheory

    override operator fun plus(theory: Theory): MutableProblogTheory

    override operator fun plus(clause: Clause): MutableProblogTheory = assertZ(clause)

    override fun assertA(clause: Clause): MutableProblogTheory

    override fun assertA(struct: Struct): MutableProblogTheory = assertA(Fact.of(struct))

    override fun assertA(clauses: Iterable<Clause>): MutableProblogTheory

    override fun assertA(clauses: Sequence<Clause>): MutableProblogTheory

    override fun assertZ(clause: Clause): MutableProblogTheory

    override fun assertZ(struct: Struct): MutableProblogTheory = assertZ(Fact.of(struct))

    override fun assertZ(clauses: Iterable<Clause>): MutableProblogTheory

    override fun assertZ(clauses: Sequence<Clause>): MutableProblogTheory

    override fun retract(clause: Clause): RetractResult<MutableProblogTheory>

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableProblogTheory>

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableProblogTheory>

    override fun retract(head: Struct): RetractResult<MutableProblogTheory> =
        retract(Rule.of(head, Var.anonymous()))

    override fun retractAll(clause: Clause): RetractResult<MutableProblogTheory>

    override fun retractAll(head: Struct): RetractResult<MutableProblogTheory> =
        retractAll(Rule.of(head, Var.anonymous()))

    override fun abolish(indicator: Indicator): MutableProblogTheory

    companion object {
        /** Creates an empty [MutableProblogTheory] */
        @JvmStatic
        @JsName("empty")
        fun empty(): MutableTheory =
            of(emptyList())

        /** Creates a [MutableProblogTheory], containing the given clauses */
        @JvmStatic
        @JsName("of")
        fun of(vararg clause: Clause): MutableTheory =
            of(*clause)

        /** Creates a [MutableProblogTheory], containing the given clauses */
        @JvmStatic
        @JsName("ofSequence")
        fun of(clauses: Sequence<Clause>): MutableTheory =
            of(clauses)

        /** Let developers easily create a [[MutableProblogTheory], while avoiding variables names clashing by using a
         * different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("ofScopes")
        fun of(vararg clauses: Scope.() -> Clause): MutableTheory =
            of(*clauses)

        /** Creates a [MutableProblogTheory] containing the given clauses */
        @JvmStatic
        @JsName("ofIterable")
        fun of(clauses: Iterable<Clause>): MutableProblogTheory =
            MappedMutableProblogTheory(clauses)
    }
}
