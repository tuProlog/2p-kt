package it.unibo.tuprolog.solve.sideffects

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.data.CustomDataStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.sideffects.impl.DefaultSideEffectFactory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/** An [ExecutionContext] whose [update] really updates, so side effects can be applied to it. */
private data class Context(
    override val libraries: Runtime = Runtime.empty(),
    override val flags: FlagStore = FlagStore.empty(),
    override val staticKb: Theory = Theory.empty(),
    override val dynamicKb: Theory = MutableTheory.empty(Unificator.default),
    override val operators: OperatorSet = OperatorSet.EMPTY,
    override val inputChannels: InputStore = InputStore.fromStandard(),
    override val outputChannels: OutputStore = OutputStore.fromStandard(),
    override val customData: CustomDataStore = CustomDataStore.empty(),
    override val unificator: Unificator = Unificator.default,
) : ExecutionContext {
    override val procedure: Struct? = null
    override val substitution: Substitution.Unifier = Substitution.empty()
    override val logicStackTrace: List<Struct> = emptyList()
    override val startTime: TimeInstant = 0
    override val maxDuration: TimeDuration = TimeDuration.MAX_VALUE

    override fun createSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): Solver = error("not needed")

    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): MutableSolver = error("not needed")

    override fun update(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore,
        outputChannels: OutputStore,
        customData: CustomDataStore,
    ): ExecutionContext =
        copy(
            unificator = unificator,
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            customData = customData,
        )
}

private fun p(n: Int): Clause = Fact.of(Struct.of("p", Integer.of(n)))

private val libA = Library.of("lib.a", clauses = listOf(p(1)))
private val libA2 = Library.of("lib.a", clauses = listOf(p(2)))
private val libB = Library.of("lib.b")
private val op1 = Operator("op1", Specifier.XFX, 700)
private val op2 = Operator("op2", Specifier.XFX, 700)
private val inA: InputChannel<String> = InputChannel.of("a")
private val inB: InputChannel<String> = InputChannel.of("b")
private val outA: OutputChannel<String> = OutputChannel.of {}
private val outB: OutputChannel<String> = OutputChannel.of {}
private val one: Term = Atom.of("one")
private val two: Term = Atom.of("two")

/**
 * Every case starts from a fresh one of these: one clause, flag, library, operator, extra channel and datum of each
 * kind. Fresh, because adding dynamic clauses mutates the context's [MutableTheory] in place.
 */
private fun base() =
    Context(
        libraries = Runtime.of(libA),
        flags = FlagStore.of("f" to one),
        staticKb = Theory.of(p(1)),
        dynamicKb = MutableTheory.of(Unificator.default, p(1)),
        operators = OperatorSet(op1),
        inputChannels = InputStore.fromStandard() + ("in.a" to inA),
        outputChannels = OutputStore.fromStandard() + ("out.a" to outA),
        customData =
            CustomDataStore(
                persistent = mapOf("k" to 1),
                durable = mapOf("k" to 1),
                ephemeral = mapOf("k" to 1),
            ),
    )

/** What one side effect must leave observable in [base], however it is created. */
private class Effect(
    val id: String,
    val observe: (ExecutionContext) -> Any?,
    val expected: Any?,
    vararg val ways: Pair<String, SideEffectFactory.() -> SideEffect>,
)

private fun ExecutionContext.static() = staticKb.clauses.toList()

private fun ExecutionContext.dynamic() = (dynamicKb is MutableTheory) to dynamicKb.clauses.toList()

private fun ExecutionContext.flagMap() = flags.toMap()

private fun ExecutionContext.aliases() = libraries.aliases

private fun ExecutionContext.libraryClauses() = libraries.libraries.flatMap { it.clauses }

private fun ExecutionContext.operatorSet() = operators.toSet()

private fun ExecutionContext.inputs() = inputChannels.mapValues { it.value }.filterKeys { it.startsWith("in.") }

private fun ExecutionContext.outputs() = outputChannels.mapValues { it.value }.filterKeys { it.startsWith("out.") }

private val effects =
    listOf(
        Effect(
            "reset-static-kb",
            { it.static() },
            listOf(p(2)),
            "iterable" to { resetStaticKb(listOf(p(2))) },
            "sequence" to { resetStaticKb(sequenceOf(p(2))) },
            "vararg" to { resetStaticKb(p(2)) },
            "theory" to { resetStaticKb(Theory.of(p(2))) },
        ),
        Effect(
            "add-static-clauses-at-the-bottom",
            { it.static() },
            listOf(p(1), p(2)),
            "iterable" to { addStaticClauses(listOf(p(2))) },
            "sequence" to { addStaticClauses(sequenceOf(p(2))) },
            "vararg" to { addStaticClauses(p(2)) },
        ),
        Effect(
            "add-static-clauses-on-top",
            { it.static() },
            listOf(p(2), p(1)),
            "iterable" to { addStaticClauses(listOf(p(2)), onTop = true) },
            "sequence" to { addStaticClauses(sequenceOf(p(2)), onTop = true) },
            "vararg" to { addStaticClauses(p(2), onTop = true) },
        ),
        Effect(
            "remove-static-clauses",
            { it.static() },
            emptyList<Clause>(),
            "iterable" to { removeStaticClauses(listOf(p(1))) },
            "sequence" to { removeStaticClauses(sequenceOf(p(1))) },
            "vararg" to { removeStaticClauses(p(1)) },
        ),
        Effect(
            "reset-dynamic-kb",
            { it.dynamic() },
            true to listOf(p(2)),
            "iterable" to { resetDynamicKb(listOf(p(2))) },
            "sequence" to { resetDynamicKb(sequenceOf(p(2))) },
            "vararg" to { resetDynamicKb(p(2)) },
            "theory" to { resetDynamicKb(Theory.of(p(2))) },
            "mutable-theory" to { resetDynamicKb(MutableTheory.of(Unificator.default, p(2))) },
        ),
        Effect(
            "add-dynamic-clauses-at-the-bottom",
            { it.dynamic() },
            true to listOf(p(1), p(2)),
            "iterable" to { addDynamicClauses(listOf(p(2))) },
            "sequence" to { addDynamicClauses(sequenceOf(p(2))) },
            "vararg" to { addDynamicClauses(p(2)) },
        ),
        Effect(
            "add-dynamic-clauses-on-top",
            { it.dynamic() },
            true to listOf(p(2), p(1)),
            "iterable" to { addDynamicClauses(listOf(p(2)), onTop = true) },
            "sequence" to { addDynamicClauses(sequenceOf(p(2)), onTop = true) },
            "vararg" to { addDynamicClauses(p(2), onTop = true) },
        ),
        Effect(
            "remove-dynamic-clauses",
            { it.dynamic() },
            true to emptyList<Clause>(),
            "iterable" to { removeDynamicClauses(listOf(p(1))) },
            "sequence" to { removeDynamicClauses(sequenceOf(p(1))) },
            "vararg" to { removeDynamicClauses(p(1)) },
        ),
        Effect(
            "set-flags",
            { it.flagMap() },
            mapOf("f" to one, "g" to two),
            "map" to { setFlags(mapOf("g" to two)) },
            "vararg" to { setFlags("g" to two) },
            "single" to { setFlag("g", two) },
            "constructor-iterable" to { SideEffect.SetFlags(listOf("g" to two)) },
            "constructor-sequence" to { SideEffect.SetFlags(sequenceOf("g" to two)) },
        ),
        Effect(
            "reset-flags",
            { it.flagMap() },
            mapOf("g" to two),
            "map" to { resetFlags(mapOf("g" to two)) },
            "vararg" to { resetFlags("g" to two) },
            "constructor-iterable" to { SideEffect.ResetFlags(listOf("g" to two)) },
            "constructor-sequence" to { SideEffect.ResetFlags(sequenceOf("g" to two)) },
        ),
        Effect(
            "clear-flags",
            { it.flagMap() },
            emptyMap<String, Term>(),
            "iterable" to { clearFlags(listOf("f")) },
            "sequence" to { clearFlags(sequenceOf("f")) },
            "vararg" to { clearFlags("f") },
        ),
        Effect(
            "load-library",
            { it.aliases() },
            setOf("lib.a", "lib.b"),
            "single" to { loadLibrary(libB) },
        ),
        Effect(
            "add-libraries",
            { it.aliases() },
            setOf("lib.a", "lib.b"),
            "runtime" to { addLibraries(Runtime.of(libB)) },
            "iterable" to { addLibraries(listOf(libB)) },
            "sequence" to { addLibraries(sequenceOf(libB)) },
            "vararg" to { addLibraries(libB) },
        ),
        Effect(
            "unload-libraries",
            { it.aliases() },
            emptySet<String>(),
            "iterable" to { unloadLibraries(listOf("lib.a")) },
            "sequence" to { unloadLibraries(sequenceOf("lib.a")) },
            "vararg" to { unloadLibraries("lib.a") },
            "constructor-list" to { SideEffect.UnloadLibraries(listOf("lib.a")) },
        ),
        Effect(
            "update-library",
            { it.libraryClauses() },
            listOf(p(2)),
            "single" to { updateLibrary(libA2) },
        ),
        Effect(
            "reset-runtime",
            { it.aliases() },
            setOf("lib.b"),
            "runtime" to { resetRuntime(Runtime.of(libB)) },
            "iterable" to { resetRuntime(listOf(libB)) },
            "sequence" to { resetRuntime(sequenceOf(libB)) },
            "vararg" to { resetRuntime(libB) },
        ),
        Effect(
            "set-operators",
            { it.operatorSet() },
            setOf(op1, op2),
            "iterable" to { setOperators(listOf(op2)) },
            "sequence" to { setOperators(sequenceOf(op2)) },
            "vararg" to { setOperators(op2) },
        ),
        Effect(
            "reset-operators",
            { it.operatorSet() },
            setOf(op2),
            "iterable" to { resetOperators(listOf(op2)) },
            "sequence" to { resetOperators(sequenceOf(op2)) },
            "vararg" to { resetOperators(op2) },
        ),
        Effect(
            "remove-operators",
            { it.operatorSet() },
            emptySet<Operator>(),
            "iterable" to { removeOperators(listOf(op1)) },
            "sequence" to { removeOperators(sequenceOf(op1)) },
            "vararg" to { removeOperators(op1) },
        ),
        Effect(
            "open-input-channels",
            { it.inputs() },
            mapOf("in.a" to inA, "in.b" to inB),
            "map" to { openInputChannels(mapOf("in.b" to inB)) },
            "vararg" to { openInputChannels("in.b" to inB) },
            "single" to { openInputChannel("in.b", inB) },
            "constructor-iterable" to { SideEffect.OpenInputChannels(listOf("in.b" to inB)) },
            "constructor-sequence" to { SideEffect.OpenInputChannels(sequenceOf("in.b" to inB)) },
        ),
        Effect(
            "reset-input-channels",
            { it.inputs() },
            mapOf("in.b" to inB),
            "map" to { resetInputChannels(mapOf("in.b" to inB)) },
            "vararg" to { resetInputChannels("in.b" to inB) },
            "iterable" to { resetInputChannels(listOf("in.b" to inB)) },
            "sequence" to { resetInputChannels(sequenceOf("in.b" to inB)) },
        ),
        Effect(
            "close-input-channels",
            { it.inputs() },
            emptyMap<String, InputChannel<String>>(),
            "iterable" to { closeInputChannels(listOf("in.a")) },
            "sequence" to { closeInputChannels(sequenceOf("in.a")) },
            "vararg" to { closeInputChannels("in.a") },
        ),
        Effect(
            "open-output-channels",
            { it.outputs() },
            mapOf("out.a" to outA, "out.b" to outB),
            "map" to { openOutputChannels(mapOf("out.b" to outB)) },
            "vararg" to { openOutputChannels("out.b" to outB) },
            "single" to { openOutputChannel("out.b", outB) },
            "constructor-iterable" to { SideEffect.OpenOutputChannels(listOf("out.b" to outB)) },
            "constructor-sequence" to { SideEffect.OpenOutputChannels(sequenceOf("out.b" to outB)) },
        ),
        Effect(
            "reset-output-channels",
            { it.outputs() },
            mapOf("out.b" to outB),
            "map" to { resetOutputChannels(mapOf("out.b" to outB)) },
            "vararg" to { resetOutputChannels("out.b" to outB) },
            "iterable" to { resetOutputChannels(listOf("out.b" to outB)) },
            "sequence" to { resetOutputChannels(sequenceOf("out.b" to outB)) },
        ),
        Effect(
            "close-output-channels",
            { it.outputs() },
            emptyMap<String, OutputChannel<String>>(),
            "iterable" to { closeOutputChannels(listOf("out.a")) },
            "sequence" to { closeOutputChannels(sequenceOf("out.a")) },
            "vararg" to { closeOutputChannels("out.a") },
        ),
        Effect(
            "add-persistent-data",
            { it.customData.persistent },
            mapOf("k" to 1, "j" to 2),
            "single" to { addPersistentData("j", 2) },
            "map" to { addPersistentData(mapOf("j" to 2)) },
            "vararg" to { addPersistentData("j" to 2) },
            "set-single" to { setPersistentData("j", 2) },
            "set-map" to { setPersistentData(mapOf("j" to 2)) },
            "set-vararg" to { setPersistentData("j" to 2) },
        ),
        Effect(
            "add-durable-data",
            { it.customData.durable },
            mapOf("k" to 1, "j" to 2),
            "single" to { addDurableData("j", 2) },
            "map" to { addDurableData(mapOf("j" to 2)) },
            "vararg" to { addDurableData("j" to 2) },
            "set-single" to { setDurableData("j", 2) },
            "set-map" to { setDurableData(mapOf("j" to 2)) },
            "set-vararg" to { setDurableData("j" to 2) },
        ),
        Effect(
            "add-ephemeral-data",
            { it.customData.ephemeral },
            mapOf("k" to 1, "j" to 2),
            "single" to { addEphemeralData("j", 2) },
            "map" to { addEphemeralData(mapOf("j" to 2)) },
            "vararg" to { addEphemeralData("j" to 2) },
            "set-single" to { setEphemeralData("j", 2) },
            "set-map" to { setEphemeralData(mapOf("j" to 2)) },
            "set-vararg" to { setEphemeralData("j" to 2) },
        ),
        Effect(
            "reset-persistent-data",
            { it.customData.persistent },
            mapOf("j" to 2),
            "constructor-single" to { SideEffect.SetPersistentData("j", 2, reset = true) },
            "constructor-vararg" to { SideEffect.SetPersistentData("j" to 2, reset = true) },
        ),
        Effect(
            "reset-durable-data",
            { it.customData.durable },
            mapOf("j" to 2),
            "constructor-single" to { SideEffect.SetDurableData("j", 2, reset = true) },
            "constructor-vararg" to { SideEffect.SetDurableData("j" to 2, reset = true) },
        ),
        Effect(
            "reset-ephemeral-data",
            { it.customData.ephemeral },
            mapOf("j" to 2),
            "constructor-single" to { SideEffect.SetEphemeralData("j", 2, reset = true) },
            "constructor-vararg" to { SideEffect.SetEphemeralData("j" to 2, reset = true) },
        ),
    )

/**
 * One way of creating one side effect, through [via]: the default factory, a builder that also collects it, or (for
 * constructor overloads the factory doesn't use) directly.
 */
private data class Way(
    val id: String,
    val effect: Effect,
    val create: SideEffectFactory.() -> SideEffect,
    val via: String,
)

private val ways =
    effects.flatMap { effect ->
        effect.ways.flatMap { (id, create) ->
            val vias = if (id.startsWith("constructor")) listOf("direct") else listOf("factory", "builder")
            vias.map { Way(id, effect, create, it) }
        }
    }

class SideEffectSpec :
    FunSpec({
        withTests(nameFn = { "${it.effect.id}/${it.id}/${it.via}" }, ways) { way ->
            val sideEffect =
                when (way.via) {
                    "factory", "direct" -> DefaultSideEffectFactory.(way.create)()
                    else ->
                        SideEffectsBuilder.empty().let { builder ->
                            builder.(way.create)().also { builder.build() shouldBe listOf(it) }
                        }
                }
            way.effect.observe(base().apply(sideEffect)) shouldBe way.effect.expected
        }

        test("builder/collects-every-side-effect-in-order") {
            val builder = SideEffectsBuilder.of(SideEffect.SetFlags("a" to one))
            builder.setFlag("b", two)
            builder.clearFlags("a")
            builder.build().map { it::class } shouldBe
                listOf(SideEffect.SetFlags::class, SideEffect.SetFlags::class, SideEffect.ClearFlags::class)
            builder.buildArray().toList() shouldBe builder.build()
            base().apply(builder.build().asSequence()).flagMap() shouldBe mapOf("f" to one, "b" to two)
        }

        test("builder/starts-from-the-given-side-effects") {
            val initial = listOf(SideEffect.ClearFlags("f"), SideEffect.SetFlags("g" to two))
            SideEffectsBuilder.of(initial).build() shouldBe initial
            SideEffectsBuilder.of(initial.asSequence()).build() shouldBe initial
        }
    })
