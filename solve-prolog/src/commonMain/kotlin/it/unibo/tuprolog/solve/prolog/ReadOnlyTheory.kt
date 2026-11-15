package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * A read-only view of [theory] (#1010): reads go to [theory]; every write returns a new, independent theory and leaves
 * [theory] untouched, and the view can't be cast to [MutableTheory].
 *
 * shortcut: reads are live, so the view shows clauses the resolution adds or removes later; a generation-based store
 * would give snapshots, if a primitive ever needs them.
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

    override fun assertA(struct: Compound): Theory = copy.assertA(struct)

    override fun assertA(clauses: Iterable<Clause>): Theory = copy.assertA(clauses)

    override fun assertA(clauses: Sequence<Clause>): Theory = copy.assertA(clauses)

    override fun assertZ(clause: Clause): Theory = copy.assertZ(clause)

    override fun assertZ(struct: Compound): Theory = copy.assertZ(struct)

    override fun assertZ(clauses: Iterable<Clause>): Theory = copy.assertZ(clauses)

    override fun assertZ(clauses: Sequence<Clause>): Theory = copy.assertZ(clauses)

    override fun retract(clause: Clause): RetractResult<Theory> = copy.retract(clause)

    override fun retract(clauses: Iterable<Clause>): RetractResult<Theory> = copy.retract(clauses)

    override fun retract(clauses: Sequence<Clause>): RetractResult<Theory> = copy.retract(clauses)

    override fun retract(head: Compound): RetractResult<Theory> = copy.retract(head)

    override fun retractAll(clause: Clause): RetractResult<Theory> = copy.retractAll(clause)

    override fun retractAll(head: Compound): RetractResult<Theory> = copy.retractAll(head)

    override fun abolish(indicator: Indicator): Theory = copy.abolish(indicator)

    override fun replaceTags(tags: Map<String, Any>): Theory = copy.replaceTags(tags)

    override fun clone(): Theory = copy

    override fun equals(other: Any?): Boolean = theory == (if (other is ReadOnlyTheory) other.theory else other)

    override fun hashCode(): Int = theory.hashCode()

    override fun toString(): String = theory.toString()
}

internal fun Theory.readOnly(): Theory = if (isMutable) ReadOnlyTheory(this) else this

internal fun Theory.unwrap(): Theory = if (this is ReadOnlyTheory) theory else this
