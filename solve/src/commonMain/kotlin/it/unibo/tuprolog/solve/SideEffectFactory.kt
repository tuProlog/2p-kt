package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SideEffectFactory {

    companion object {
        @JvmStatic
        @JsName("default")
        val default = object : SideEffectFactory { }
    }

    @JsName("resetStaticKbIterable")
    fun resetStaticKb(clauses: Iterable<Clause>) =
        SideEffect.ResetStaticKb(clauses)

    @JsName("resetStaticKbSequence")
    fun resetStaticKb(clauses: Sequence<Clause>) =
        SideEffect.ResetStaticKb(clauses)

    @JsName("resetStaticKb")
    fun resetStaticKb(vararg clauses: Clause) =
        SideEffect.ResetStaticKb(*clauses)

    @JsName("addStaticClausesIterable")
    fun addStaticClauses(clauses: Iterable<Clause>, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(clauses, onTop)

    @JsName("addStaticClausesSequence")
    fun addStaticClauses(clauses: Sequence<Clause>, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(clauses, onTop)

    @JsName("addStaticClauses")
    fun addStaticClauses(vararg clauses: Clause, onTop: Boolean = false) =
        SideEffect.AddStaticClauses(*clauses, onTop = onTop)

    @JsName("removeStaticClausesIterable")
    fun removeStaticClauses(clauses: Iterable<Clause>) =
        SideEffect.RemoveStaticClauses(clauses)

    @JsName("removeStaticClausesSequence")
    fun removeStaticClauses(clauses: Sequence<Clause>) =
        SideEffect.RemoveStaticClauses(clauses)

    @JsName("removeStaticClauses")
    fun removeStaticClauses(vararg clauses: Clause) =
        SideEffect.RemoveStaticClauses(*clauses)

    @JsName("resetDynamicKbIterable")
    fun resetDynamicKb(clauses: Iterable<Clause>) =
        SideEffect.ResetDynamicKb(clauses)

    @JsName("resetDynamicKbSequence")
    fun resetDynamicKb(clauses: Sequence<Clause>) =
        SideEffect.ResetDynamicKb(clauses)

    @JsName("resetDynamicKb")
    fun resetDynamicKb(vararg clauses: Clause) =
        SideEffect.ResetDynamicKb(*clauses)

    @JsName("addDynamicClausesIterable")
    fun addDynamicClauses(clauses: Iterable<Clause>, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(clauses, onTop)

    @JsName("addDynamicClausesSequence")
    fun addDynamicClauses(clauses: Sequence<Clause>, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(clauses, onTop)

    @JsName("addDynamicClauses")
    fun addDynamicClauses(vararg clauses: Clause, onTop: Boolean = false) =
        SideEffect.AddDynamicClauses(*clauses, onTop = onTop)

    @JsName("removeDynamicClausesIterable")
    fun removeDynamicClauses(clauses: Iterable<Clause>) =
        SideEffect.RemoveDynamicClauses(clauses)

    @JsName("removeDynamicClausesSequence")
    fun removeDynamicClauses(clauses: Sequence<Clause>) =
        SideEffect.RemoveDynamicClauses(clauses)

    @JsName("removeDynamicClauses")
    fun removeDynamicClauses(vararg clauses: Clause) =
        SideEffect.RemoveDynamicClauses(*clauses)

    @JsName("setFlagsMap")
    fun setFlags(flags: Map<String, Term>) =
        SideEffect.SetFlags(flags)

    @JsName("setFlags")
    fun setFlags(vararg flags: Pair<String, Term>) =
        SideEffect.SetFlags(*flags)

    @JsName("setFlag")
    fun setFlag(name: String, value: Term) =
        SideEffect.SetFlags(name to value)

    @JsName("resetFlagsMap")
    fun resetFlags(flags: Map<String, Term>) =
        SideEffect.ResetFlags(flags)

    @JsName("resetFlags")
    fun resetFlags(vararg flags: Pair<String, Term>) =
        SideEffect.ResetFlags(*flags)

    @JsName("clearFlagsIterable")
    fun clearFlags(names: Iterable<String>) =
        SideEffect.ClearFlags(names)

    @JsName("clearFlagsSequence")
    fun clearFlags(names: Sequence<String>) =
        SideEffect.ClearFlags(names)

    @JsName("clearFlags")
    fun clearFlags(vararg names: String) =
        SideEffect.ClearFlags(*names)

    @JsName("loadLibrary")
    fun loadLibrary(alias: String, library: Library) =
        SideEffect.LoadLibrary(alias, library)

    @JsName("loadAliasedLibrary")
    fun loadLibrary(aliasedLibrary: AliasedLibrary) =
        SideEffect.LoadLibrary(aliasedLibrary.alias, aliasedLibrary)

    @JsName("unloadLibrariesIterable")
    fun unloadLibraries(aliases: Iterable<String>) =
        SideEffect.UnloadLibraries(aliases)

    @JsName("unloadLibrariesSequence")
    fun unloadLibraries(aliases: Sequence<String>) =
        SideEffect.UnloadLibraries(aliases)

    @JsName("unloadLibraries")
    fun unloadLibraries(vararg aliases: String) =
        SideEffect.UnloadLibraries(*aliases)

    @JsName("updateLibrary")
    fun updateLibrary(alias: String, library: Library) =
        SideEffect.UpdateLibrary(alias, library)

    @JsName("updateAliasedLibrary")
    fun updateLibrary(aliasedLibrary: AliasedLibrary) =
        SideEffect.UpdateLibrary(aliasedLibrary.alias, aliasedLibrary)

    @JsName("resetLibraries")
    fun resetLibraries(libraries: Libraries) =
        SideEffect.ResetLibraries(libraries)

    @JsName("resetLibrariesIterable")
    fun resetLibraries(libraries: Iterable<AliasedLibrary>) =
        SideEffect.ResetLibraries(libraries)

    @JsName("resetLibrariesSequence")
    fun resetLibraries(libraries: Sequence<AliasedLibrary>) =
        SideEffect.ResetLibraries(libraries)

    @JsName("resetAliasedLibrary")
    fun resetLibraries(vararg libraries: AliasedLibrary) =
        SideEffect.ResetLibraries(*libraries)

    @JsName("addLibraries")
    fun addLibraries(libraries: Libraries) =
        SideEffect.AddLibraries(libraries)

    @JsName("addLibrariesIterable")
    fun addLibraries(libraries: Iterable<AliasedLibrary>) =
        SideEffect.AddLibraries(libraries)

    @JsName("addLibrariesSequence")
    fun addLibraries(libraries: Sequence<AliasedLibrary>) =
        SideEffect.AddLibraries(libraries)

    @JsName("addAliasedLibrary")
    fun addLibraries(vararg libraries: AliasedLibrary) =
        SideEffect.AddLibraries(*libraries)

    @JsName("setOperatorsIterable")
    fun setOperators(operators: Iterable<Operator>) =
        SideEffect.SetOperators(operators)

    @JsName("setOperatorsSequence")
    fun setOperators(operators: Sequence<Operator>) =
        SideEffect.SetOperators(operators)

    @JsName("setOperators")
    fun setOperators(vararg operators: Operator) =
        SideEffect.SetOperators(*operators)

    @JsName("resetOperatorsIterable")
    fun resetOperators(operators: Iterable<Operator>) =
        SideEffect.ResetOperators(operators)

    @JsName("resetOperatorsSequence")
    fun resetOperators(operators: Sequence<Operator>) =
        SideEffect.ResetOperators(operators)

    @JsName("resetOperators")
    fun resetOperators(vararg operators: Operator) =
        SideEffect.ResetOperators(*operators)

    @JsName("removeOperatorsIterable")
    fun removeOperators(operators: Iterable<Operator>) =
        SideEffect.RemoveOperators(operators)

    @JsName("removeOperatorsSequence")
    fun removeOperators(operators: Sequence<Operator>) =
        SideEffect.RemoveOperators(operators)

    @JsName("removeOperators")
    fun removeOperators(vararg operators: Operator) =
        SideEffect.RemoveOperators(*operators)

    @JsName("openInputChannelsMap")
    fun openInputChannels(inputChannels: Map<String, InputChannel<*>>) =
        SideEffect.OpenInputChannels(inputChannels)

    @JsName("openInputChannels")
    fun openInputChannels(vararg inputChannels: Pair<String, InputChannel<*>>) =
        SideEffect.OpenInputChannels(*inputChannels)

    @JsName("openInputChannel")
    fun openInputChannel(name: String, inputChannel: InputChannel<*>) =
        SideEffect.OpenInputChannels(name to inputChannel)

    @JsName("resetInputChannels")
    fun resetInputChannels(vararg inputChannels: Pair<String, InputChannel<*>>) =
        SideEffect.ResetInputChannels(*inputChannels)

    @JsName("closeInputChannelsIterable")
    fun closeInputChannels(names: Iterable<String>) =
        SideEffect.CloseInputChannels(names)

    @JsName("closeInputChannelsSequence")
    fun closeInputChannels(names: Sequence<String>) =
        SideEffect.CloseInputChannels(names)

    @JsName("closeInputChannels")
    fun closeInputChannels(vararg names: String) =
        SideEffect.CloseInputChannels(*names)

    @JsName("openOutputChannelsMap")
    fun openOutputChannels(outputChannels: Map<String, OutputChannel<*>>) =
        SideEffect.OpenOutputChannels(outputChannels)

    @JsName("openOutputChannels")
    fun openOutputChannels(vararg outputChannels: Pair<String, OutputChannel<*>>) =
        SideEffect.OpenOutputChannels(*outputChannels)

    @JsName("openOutputChannel")
    fun openOutputChannel(name: String, outputChannel: OutputChannel<*>) =
        SideEffect.OpenOutputChannels(name to outputChannel)

    @JsName("resetOutputChannelsMap")
    fun resetOutputChannels(outputChannels: Map<String, OutputChannel<*>>) =
        SideEffect.ResetOutputChannels(outputChannels)

    @JsName("resetOutputChannels")
    fun resetOutputChannels(vararg outputChannels: Pair<String, OutputChannel<*>>) =
        SideEffect.ResetOutputChannels(*outputChannels)

    @JsName("closeOutputChannelsIterable")
    fun closeOutputChannels(names: Iterable<String>) =
        SideEffect.CloseOutputChannels(names)

    @JsName("closeOutputChannelsSequence")
    fun closeOutputChannels(names: Sequence<String>) =
        SideEffect.CloseOutputChannels(names)

    @JsName("closeOutputChannels")
    fun closeOutputChannels(vararg names: String) =
        SideEffect.CloseOutputChannels(*names)
}