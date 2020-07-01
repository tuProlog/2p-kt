package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library

interface SideEffectFactory {

    companion object {
        val default = object : SideEffectFactory { }
    }

    fun resetStaticKb(clauses: Iterable<Clause>) =
        SideEffect.ResetStaticKb(clauses)

    fun resetStaticKb(clauses: Sequence<Clause>) =
        SideEffect.ResetStaticKb(clauses)

    fun resetStaticKb(vararg clauses: Clause) =
        SideEffect.ResetStaticKb(*clauses)

    fun addStaticClauses(clauses: Iterable<Clause>, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(clauses, onTop)

    fun addStaticClauses(clauses: Sequence<Clause>, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(clauses, onTop)

    fun addStaticClauses(vararg clauses: Clause, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(*clauses, onTop = onTop)

    fun removeStaticClauses(clauses: Iterable<Clause>) =
        SideEffect.RemoveStaticClauses(clauses)

    fun removeStaticClauses(clauses: Sequence<Clause>) =
        SideEffect.RemoveStaticClauses(clauses)

    fun removeStaticClauses(vararg clauses: Clause) =
        SideEffect.RemoveStaticClauses(*clauses)

    fun resetDynamicKb(clauses: Iterable<Clause>) =
        SideEffect.ResetDynamicKb(clauses)

    fun resetDynamicKb(clauses: Sequence<Clause>) =
        SideEffect.ResetDynamicKb(clauses)

    fun resetDynamicKb(vararg clauses: Clause) =
        SideEffect.ResetDynamicKb(*clauses)

    fun addDynamicClauses(clauses: Iterable<Clause>, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(clauses, onTop)

    fun addDynamicClauses(clauses: Sequence<Clause>, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(clauses, onTop)

    fun addDynamicClauses(vararg clauses: Clause, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(*clauses, onTop = onTop)

    fun removeDynamicClauses(clauses: Iterable<Clause>) =
        SideEffect.RemoveDynamicClauses(clauses)

    fun removeDynamicClauses(clauses: Sequence<Clause>) =
        SideEffect.RemoveDynamicClauses(clauses)

    fun removeDynamicClauses(vararg clauses: Clause) =
        SideEffect.RemoveDynamicClauses(*clauses)

    fun setFlags(flags: Map<String, Term>) =
        SideEffect.SetFlags(flags)

    fun setFlags(vararg flags: Pair<String, Term>) =
        SideEffect.SetFlags(*flags)

    fun setFlag(name: String, value: Term) =
        SideEffect.SetFlags(name to value)

    fun resetFlags(flags: Map<String, Term>) =
        SideEffect.ResetFlags(flags)

    fun resetFlags(vararg flags: Pair<String, Term>) =
        SideEffect.ResetFlags(*flags)

    fun clearFlags(names: Iterable<String>) =
        SideEffect.ClearFlags(names)

    fun clearFlags(names: Sequence<String>) =
        SideEffect.ClearFlags(names)

    fun clearFlags(vararg names: String) =
        SideEffect.ClearFlags(*names)

    fun loadLibrary(alias: String, library: Library) =
        SideEffect.LoadLibrary(alias, library)

    fun loadLibrary(aliasedLibrary: AliasedLibrary) =
        SideEffect.LoadLibrary(aliasedLibrary.alias, aliasedLibrary)

    fun unloadLibraries(aliases: Iterable<String>) =
        SideEffect.UnloadLibraries(aliases)

    fun unloadLibraries(aliases: Sequence<String>) =
        SideEffect.UnloadLibraries(aliases)

    fun unloadLibraries(vararg aliases: String) =
        SideEffect.UnloadLibraries(*aliases)

    fun updateLibrary(alias: String, library: Library) =
        SideEffect.UpdateLibrary(alias, library)

    fun updateLibrary(aliasedLibrary: AliasedLibrary) =
        SideEffect.UpdateLibrary(aliasedLibrary.alias, aliasedLibrary)

    fun resetLibraries(libraries: Libraries) =
        SideEffect.ResetLibraries(libraries)

    fun resetLibraries(libraries: Iterable<AliasedLibrary>) =
        SideEffect.ResetLibraries(libraries)

    fun resetLibraries(libraries: Sequence<AliasedLibrary>) =
        SideEffect.ResetLibraries(libraries)

    fun resetLibraries(vararg libraries: AliasedLibrary) =
        SideEffect.ResetLibraries(*libraries)

    fun addLibraries(libraries: Libraries) =
        SideEffect.AddLibraries(libraries)

    fun addLibraries(libraries: Iterable<AliasedLibrary>) =
        SideEffect.AddLibraries(libraries)

    fun addLibraries(libraries: Sequence<AliasedLibrary>) =
        SideEffect.AddLibraries(libraries)

    fun addLibraries(vararg libraries: AliasedLibrary) =
        SideEffect.AddLibraries(*libraries)

    fun setOperators(operators: Iterable<Operator>) =
        SideEffect.SetOperators(operators)

    fun setOperators(operators: Sequence<Operator>) =
        SideEffect.SetOperators(operators)

    fun setOperators(vararg operators: Operator) =
        SideEffect.SetOperators(*operators)

    fun resetOperators(operators: Iterable<Operator>) =
        SideEffect.ResetOperators(operators)

    fun resetOperators(operators: Sequence<Operator>) =
        SideEffect.ResetOperators(operators)

    fun resetOperators(vararg operators: Operator) =
        SideEffect.ResetOperators(*operators)

    fun removeOperators(operators: Iterable<Operator>) =
        SideEffect.RemoveOperators(operators)

    fun removeOperators(operators: Sequence<Operator>) =
        SideEffect.RemoveOperators(operators)

    fun removeOperators(vararg operators: Operator) =
        SideEffect.RemoveOperators(*operators)

    fun openInputChannels(inputChannels: Map<String, InputChannel<*>>) =
        SideEffect.OpenInputChannels(inputChannels)

    fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<*>>) =
        SideEffect.OpenInputChannels(*inputChannels)

    fun openInputChannel(name: String, inputChannel: InputChannel<*>) =
        SideEffect.OpenInputChannels(name to inputChannel)

    fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<*>>) =
        SideEffect.ResetInputChannels(*inputChannels)

    fun closeInputChannels(names: Iterable<String>) =
        SideEffect.CloseInputChannels(names)

    fun closeInputChannels(names: Sequence<String>) =
        SideEffect.CloseInputChannels(names)

    fun closeInputChannels(vararg names: String) =
        SideEffect.CloseInputChannels(*names)

    fun openOutputChannels(outputChannels: Map<String, OutputChannel<*>>) =
        SideEffect.OpenOutputChannels(outputChannels)

    fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<*>>) =
        SideEffect.OpenOutputChannels(*outputChannels)

    fun openOutputChannel(name: String, outputChannel: OutputChannel<*>) =
        SideEffect.OpenOutputChannels(name to outputChannel)

    fun resetOutputChannels(outputChannels: Map<String, OutputChannel<*>>) =
        SideEffect.ResetOutputChannels(outputChannels)

    fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<*>>) =
        SideEffect.ResetOutputChannels(*outputChannels)

    fun closeOutputChannels(names: Iterable<String>) =
        SideEffect.CloseOutputChannels(names)

    fun closeOutputChannels(names: Sequence<String>) =
        SideEffect.CloseOutputChannels(names)

    fun closeOutputChannels(vararg names: String) =
        SideEffect.CloseOutputChannels(*names)
}