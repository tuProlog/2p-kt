package it.unibo.tuprolog.solve.problog.lib.knowledge

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.knowledge.mapping.MappedProblogTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface ProblogTheory : Theory {

    override val isMutable: Boolean get() = false

    override fun toMutableTheory(): MutableProblogTheory

    operator fun plus(theory: ProblogTheory): ProblogTheory

    override operator fun plus(theory: Theory): ProblogTheory

    override operator fun plus(clause: Clause): ProblogTheory = assertZ(clause)

    override fun assertA(clause: Clause): ProblogTheory

    override fun assertA(struct: Struct): ProblogTheory = assertA(Fact.of(struct))

    override fun assertA(clauses: Iterable<Clause>): ProblogTheory

    override fun assertA(clauses: Sequence<Clause>): ProblogTheory

    override fun assertZ(clause: Clause): ProblogTheory

    override fun assertZ(struct: Struct): ProblogTheory = assertZ(Fact.of(struct))

    override fun assertZ(clauses: Iterable<Clause>): ProblogTheory

    override fun assertZ(clauses: Sequence<Clause>): ProblogTheory

    override fun retract(clause: Clause): RetractResult<ProblogTheory>

    override fun retract(clauses: Iterable<Clause>): RetractResult<ProblogTheory>

    override fun retract(clauses: Sequence<Clause>): RetractResult<ProblogTheory>

    override fun retract(head: Struct): RetractResult<ProblogTheory> =
        retract(Rule.of(head, Var.anonymous()))

    override fun retractAll(clause: Clause): RetractResult<ProblogTheory>

    override fun retractAll(head: Struct): RetractResult<ProblogTheory> =
        retractAll(Rule.of(head, Var.anonymous()))

    override fun abolish(indicator: Indicator): ProblogTheory

    companion object {
        /** Creates an empty [ProblogTheory] */
        @JvmStatic
        @JsName("empty")
        fun empty(): ProblogTheory =
            of(emptyList())

        /** Creates a [ProblogTheory], containing the given clauses */
        @JvmStatic
        @JsName("of")
        fun of(vararg clause: Clause): ProblogTheory =
            of(*clause)

        /** Creates a [ProblogTheory], containing the given clauses */
        @JvmStatic
        @JsName("ofSequence")
        fun of(clauses: Sequence<Clause>): ProblogTheory =
            of(clauses)

        /** Let developers easily create a [[ProblogTheory], while avoiding variables names clashing by using a
         * different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("ofScopes")
        fun of(vararg clauses: Scope.() -> Clause): ProblogTheory =
            of(*clauses)

        /** Creates a [ProblogTheory] containing the given clauses */
        @JvmStatic
        @JsName("ofIterable")
        fun of(clauses: Iterable<Clause>): ProblogTheory =
            MappedProblogTheory(clauses)
    }
}
