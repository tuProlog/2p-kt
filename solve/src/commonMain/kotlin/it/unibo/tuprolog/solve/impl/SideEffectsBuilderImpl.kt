package it.unibo.tuprolog.solve.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.SideEffect
import it.unibo.tuprolog.solve.SideEffectsBuilder
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Library

internal data class SideEffectsBuilderImpl(override val sideEffects: MutableList<SideEffect>) : SideEffectsBuilder {
    private fun <T : SideEffect> adding(f: () -> T): T {
        return f().also { sideEffects.add(it) }
    }

    override fun resetStaticKb(clauses: Iterable<Clause>): SideEffect.ResetStaticKb {
        return adding { super.resetStaticKb(clauses) }
    }

    override fun resetStaticKb(clauses: Sequence<Clause>): SideEffect.ResetStaticKb {
        return adding { super.resetStaticKb(clauses) }
    }

    override fun resetStaticKb(vararg clauses: Clause): SideEffect.ResetStaticKb {
        return adding { super.resetStaticKb(*clauses) }
    }

    override fun addStaticClauses(clauses: Iterable<Clause>, onTop: Boolean): SideEffect.AddStaticClauses {
        return adding { super.addStaticClauses(clauses, onTop) }
    }

    override fun addStaticClauses(clauses: Sequence<Clause>, onTop: Boolean): SideEffect.AddStaticClauses {
        return adding { super.addStaticClauses(clauses, onTop) }
    }

    override fun addStaticClauses(vararg clauses: Clause, onTop: Boolean): SideEffect.AddStaticClauses {
        return adding { super.addStaticClauses(*clauses, onTop = onTop) }
    }

    override fun removeStaticClauses(clauses: Iterable<Clause>): SideEffect.RemoveStaticClauses {
        return adding { super.removeStaticClauses(clauses) }
    }

    override fun removeStaticClauses(clauses: Sequence<Clause>): SideEffect.RemoveStaticClauses {
        return adding { super.removeStaticClauses(clauses) }
    }

    override fun removeStaticClauses(vararg clauses: Clause): SideEffect.RemoveStaticClauses {
        return adding { super.removeStaticClauses(*clauses) }
    }

    override fun resetDynamicKb(clauses: Iterable<Clause>): SideEffect.ResetDynamicKb {
        return adding { super.resetDynamicKb(clauses) }
    }

    override fun resetDynamicKb(clauses: Sequence<Clause>): SideEffect.ResetDynamicKb {
        return adding { super.resetDynamicKb(clauses) }
    }

    override fun resetDynamicKb(vararg clauses: Clause): SideEffect.ResetDynamicKb {
        return adding { super.resetDynamicKb(*clauses) }
    }

    override fun addDynamicClauses(clauses: Iterable<Clause>, onTop: Boolean): SideEffect.AddDynamicClauses {
        return adding { super.addDynamicClauses(clauses, onTop) }
    }

    override fun addDynamicClauses(clauses: Sequence<Clause>, onTop: Boolean): SideEffect.AddDynamicClauses {
        return adding { super.addDynamicClauses(clauses, onTop) }
    }

    override fun addDynamicClauses(vararg clauses: Clause, onTop: Boolean): SideEffect.AddDynamicClauses {
        return adding { super.addDynamicClauses(*clauses, onTop = onTop) }
    }

    override fun removeDynamicClauses(clauses: Iterable<Clause>): SideEffect.RemoveDynamicClauses {
        return adding { super.removeDynamicClauses(clauses) }
    }

    override fun removeDynamicClauses(clauses: Sequence<Clause>): SideEffect.RemoveDynamicClauses {
        return adding { super.removeDynamicClauses(clauses) }
    }

    override fun removeDynamicClauses(vararg clauses: Clause): SideEffect.RemoveDynamicClauses {
        return adding { super.removeDynamicClauses(*clauses) }
    }

    override fun setFlags(flags: Map<String, Term>): SideEffect.SetFlags {
        return adding { super.setFlags(flags) }
    }

    override fun setFlags(vararg flags: Pair<String, Term>): SideEffect.SetFlags {
        return adding { super.setFlags(*flags) }
    }

    override fun setFlag(name: String, value: Term): SideEffect.SetFlags {
        return adding { super.setFlag(name, value) }
    }

    override fun resetFlags(flags: Map<String, Term>): SideEffect.ResetFlags {
        return adding { super.resetFlags(flags) }
    }

    override fun resetFlags(vararg flags: Pair<String, Term>): SideEffect.ResetFlags {
        return adding { super.resetFlags(*flags) }
    }

    override fun clearFlags(names: Iterable<String>): SideEffect.ClearFlags {
        return adding { super.clearFlags(names) }
    }

    override fun clearFlags(names: Sequence<String>): SideEffect.ClearFlags {
        return adding { super.clearFlags(names) }
    }

    override fun clearFlags(vararg names: String): SideEffect.ClearFlags {
        return adding { super.clearFlags(*names) }
    }

    override fun loadLibrary(alias: String, library: Library): SideEffect.LoadLibrary {
        return adding { super.loadLibrary(alias, library) }
    }

    override fun loadLibrary(aliasedLibrary: AliasedLibrary): SideEffect.LoadLibrary {
        return adding { super.loadLibrary(aliasedLibrary) }
    }

    override fun unloadLibrary(alias: String): SideEffect.UnloadLibrary {
        return adding { super.unloadLibrary(alias) }
    }

    override fun updateLibrary(alias: String, library: Library): SideEffect.UpdateLibrary {
        return adding { super.updateLibrary(alias, library) }
    }

    override fun updateLibrary(aliasedLibrary: AliasedLibrary): SideEffect.UpdateLibrary {
        return adding { super.updateLibrary(aliasedLibrary) }
    }

    override fun setOperators(operators: Iterable<Operator>): SideEffect.SetOperators {
        return adding { super.setOperators(operators) }
    }

    override fun setOperators(operators: Sequence<Operator>): SideEffect.SetOperators {
        return adding { super.setOperators(operators) }
    }

    override fun setOperators(vararg operators: Operator): SideEffect.SetOperators {
        return adding { super.setOperators(*operators) }
    }

    override fun resetOperators(operators: Iterable<Operator>): SideEffect.ResetOperators {
        return adding { super.resetOperators(operators) }
    }

    override fun resetOperators(operators: Sequence<Operator>): SideEffect.ResetOperators {
        return adding { super.resetOperators(operators) }
    }

    override fun resetOperators(vararg operators: Operator): SideEffect.ResetOperators {
        return adding { super.resetOperators(*operators) }
    }

    override fun removeOperators(operators: Iterable<Operator>): SideEffect.RemoveOperators {
        return adding { super.removeOperators(operators) }
    }

    override fun removeOperators(operators: Sequence<Operator>): SideEffect.RemoveOperators {
        return adding { super.removeOperators(operators) }
    }

    override fun removeOperators(vararg operators: Operator): SideEffect.RemoveOperators {
        return adding { super.removeOperators(*operators) }
    }

    override fun openInputChannels(inputChannels: Map<String, InputChannel<*>>): SideEffect.OpenInputChannels {
        return adding { super.openInputChannels(inputChannels) }
    }

    override fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<*>>): SideEffect.OpenInputChannels {
        return adding { super.openInputChannels(*inputChannels) }
    }

    override fun openInputChannel(name: String, inputChannel: InputChannel<*>): SideEffect.OpenInputChannels {
        return adding { super.openInputChannel(name, inputChannel) }
    }

    override fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<*>>): SideEffect.ResetInputChannels {
        return adding { super.resetInputChannels(*inputChannels) }
    }

    override fun closeInputChannels(names: Iterable<String>): SideEffect.CloseInputChannels {
        return adding { super.closeInputChannels(names) }
    }

    override fun closeInputChannels(names: Sequence<String>): SideEffect.CloseInputChannels {
        return adding { super.closeInputChannels(names) }
    }

    override fun closeInputChannels(vararg names: String): SideEffect.CloseInputChannels {
        return adding { super.closeInputChannels(*names) }
    }

    override fun openOutputChannels(outputChannels: Map<String, OutputChannel<*>>): SideEffect.OpenOutputChannels {
        return adding { super.openOutputChannels(outputChannels) }
    }

    override fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<*>>): SideEffect.OpenOutputChannels {
        return adding { super.openOutputChannels(*outputChannels) }
    }

    override fun openOutputChannel(name: String, outputChannel: OutputChannel<*>): SideEffect.OpenOutputChannels {
        return adding { super.openOutputChannel(name, outputChannel) }
    }

    override fun resetOutputChannels(outputChannels: Map<String, OutputChannel<*>>): SideEffect.ResetOutputChannels {
        return adding { super.resetOutputChannels(outputChannels) }
    }

    override fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<*>>): SideEffect.ResetOutputChannels {
        return adding { super.resetOutputChannels(*outputChannels) }
    }

    override fun closeOutputChannels(names: Iterable<String>): SideEffect.CloseOutputChannels {
        return adding { super.closeOutputChannels(names) }
    }

    override fun closeOutputChannels(names: Sequence<String>): SideEffect.CloseOutputChannels {
        return adding { super.closeOutputChannels(names) }
    }

    override fun closeOutputChannels(vararg names: String): SideEffect.CloseOutputChannels {
        return adding { super.closeOutputChannels(*names) }
    }
}