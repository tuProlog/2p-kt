package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * A read-only view of [theory] (#1010): reads go to [theory]; every write returns a new, independent theory and leaves
 * [theory] untouched, and the view can't be cast to [MutableTheory].
 *
 * shortcut: reads are live, so the view shows clauses the resolution adds or removes later; the stateful engine's
 * generation-based store will give snapshots.
 */
internal class ReadOnlyTheory(
    val theory: Theory,
) : Theory by theory {
    private val copy: Theory get() = theory.toImmutableTheory()

    override val isMutable: Boolean get() = false

    override fun setUnificator(unificator: Unificator): Theory = copy.setUnificator(unificator)

    override fun toMutableTheory(): MutableTheory = copy.toMutableTheory()

    override fun toImmutableTheory(): Theory = copy

    override fun toMutable(): MutableTheory = toMutableTheory()

    override fun toImmutable(): Theory = copy

    override fun plus(theory: Theory): Theory = copy.plus(theory)

    override fun plus(clause: Clause): Theory = copy.plus(clause)

    override fun assertA(clause: Clause): Theory = copy.assertA(clause)

    override fun assertA(struct: Struct): Theory = copy.assertA(struct)

    override fun assertA(clauses: Iterable<Clause>): Theory = copy.assertA(clauses)

    override fun assertA(clauses: Sequence<Clause>): Theory = copy.assertA(clauses)

    override fun assertZ(clause: Clause): Theory = copy.assertZ(clause)

    override fun assertZ(struct: Struct): Theory = copy.assertZ(struct)

    override fun assertZ(clauses: Iterable<Clause>): Theory = copy.assertZ(clauses)

    override fun assertZ(clauses: Sequence<Clause>): Theory = copy.assertZ(clauses)

    override fun retract(clause: Clause): RetractResult<Theory> = copy.retract(clause)

    override fun retract(clauses: Iterable<Clause>): RetractResult<Theory> = copy.retract(clauses)

    override fun retract(clauses: Sequence<Clause>): RetractResult<Theory> = copy.retract(clauses)

    override fun retract(head: Struct): RetractResult<Theory> = copy.retract(head)

    override fun retractAll(clause: Clause): RetractResult<Theory> = copy.retractAll(clause)

    override fun retractAll(head: Struct): RetractResult<Theory> = copy.retractAll(head)

    override fun abolish(indicator: Indicator): Theory = copy.abolish(indicator)

    override fun replaceTags(tags: Map<String, Any>): Theory = copy.replaceTags(tags)

    override fun clone(): Theory = copy

    override fun equals(other: Any?): Boolean = theory == (if (other is ReadOnlyTheory) other.theory else other)

    override fun hashCode(): Int = theory.hashCode()

    override fun toString(): String = theory.toString()
}

private fun Theory.readOnly(): Theory = if (isMutable) ReadOnlyTheory(this) else this

private fun Theory.unwrap(): Theory = if (this is ReadOnlyTheory) theory else this

/**
 * The view of [context] that primitives and functions get in their requests (#1010): its knowledge bases are
 * [ReadOnlyTheory]s, and [apply]/[update] derive new contexts without touching [context]'s. Sub-solvers it creates
 * share [context]'s knowledge bases unless given others, as they do when created from [context] directly.
 */
internal class ReadOnlyExecutionContext(
    private val context: ExecutionContext,
) : ExecutionContext by context {
    override val staticKb: Theory = context.staticKb.readOnly()

    override val dynamicKb: Theory = context.dynamicKb.readOnly()

    override fun createSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): Solver =
        context.createSolver(
            unificator,
            libraries,
            flags,
            staticKb.unwrap(),
            dynamicKb.unwrap(),
            inputChannels,
            outputChannels,
        )

    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): MutableSolver =
        context.createMutableSolver(
            unificator,
            libraries,
            flags,
            staticKb.unwrap(),
            dynamicKb.unwrap(),
            inputChannels,
            outputChannels,
        )

    // through this view, so side effects on the knowledge bases write copies, not the engine's
    override fun apply(sideEffect: SideEffect): ExecutionContext = super<ExecutionContext>.apply(sideEffect)

    override fun apply(sideEffects: Iterable<SideEffect>): ExecutionContext = super<ExecutionContext>.apply(sideEffects)

    override fun apply(sideEffects: Sequence<SideEffect>): ExecutionContext = super<ExecutionContext>.apply(sideEffects)

    override fun equals(other: Any?): Boolean = other is ReadOnlyExecutionContext && context == other.context

    override fun hashCode(): Int = context.hashCode()

    override fun toString(): String = "ReadOnlyExecutionContext($context)"
}
