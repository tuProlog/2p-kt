package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.ChannelStore
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_OR_ALIAS
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_PROPERTY
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_TYPE
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError.Limit.CHARACTER_CODE
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.io.IOMode
import it.unibo.tuprolog.solve.libs.io.Url
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsAtom
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsInstantiated
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsInteger
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsStruct
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.isCharacterCode
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.matches

object IOPrimitiveUtils {

    private val INPUT_STREAM_TERM_PATTERN by lazy { InputChannel.streamTerm() }
    private val OUTPUT_STREAM_TERM_PATTERN by lazy { OutputChannel.streamTerm() }
    private val PROPERTY_INPUT by lazy { Atom.of("input") }
    private val PROPERTY_OUTPUT by lazy { Atom.of("output") }
    private val PROPERTY_ALIAS by lazy { alias() }
    private val PROPERTY_TYPE_TEXT by lazy { Struct.of("type", Atom.of("text")) }

    private fun alias(alias: String? = null) =
        Struct.of("alias", alias?.let { Atom.of(it) } ?: Var.anonymous())

    @Suppress("UNCHECKED_CAST")
    fun <C : ExecutionContext, T : Any> Solve.Request<C>.propertiesOf(channel: Channel<T>): Sequence<Struct> =
        sequence {
            when (channel) {
                is InputChannel<*> -> {
                    yield(PROPERTY_INPUT)
                    yieldAll(
                        context.inputChannels
                            .aliasesOf(channel as InputChannel<String>)
                            .filterNot { it == ChannelStore.CURRENT }
                            .map(::alias)
                    )
                }
                is OutputChannel<*> -> {
                    yield(PROPERTY_OUTPUT)
                    yieldAll(
                        context.outputChannels
                            .aliasesOf(channel as OutputChannel<String>)
                            .filterNot { it == ChannelStore.CURRENT }
                            .map(::alias)
                    )
                }
            }
            yield(PROPERTY_TYPE_TEXT)
        }

    val <C : ExecutionContext> Solve.Request<C>.currentInputChannel: InputChannel<String>
        get() = context.inputChannels.let { it.current ?: it.stdIn }

    val <C : ExecutionContext> Solve.Request<C>.currentOutputChannel: OutputChannel<String>
        get() = context.outputChannels.let { it.current ?: it.stdOut }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsStreamProperty(index: Int): Solve.Request<C> {
        val term = arguments[index]
        when (term) {
            is Var -> return this
            is Struct -> {
                if (sequenceOf(PROPERTY_INPUT, PROPERTY_OUTPUT, PROPERTY_ALIAS).any { it matches term }) {
                    return this
                }
            }
        }
        throw DomainError.forArgument(context, signature, STREAM_PROPERTY, term, index)
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsUrl(index: Int): Url {
        ensuringArgumentIsInstantiated(index)
        val term = arguments[index]
        val error = DomainError.forArgument(context, signature, DomainError.Expected.SOURCE_SINK, term, index)
        if (term !is Atom) {
            throw error
        }
        try {
            return Url.of(term.value)
        } catch (_: InvalidUrlException) {
            throw error
        }
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsIOMode(index: Int): IOMode {
        ensuringArgumentIsInstantiated(index)
        ensuringArgumentIsAtom(index)
        val term = arguments[index] as Atom
        if (term !in IOMode.atomValues) {
            throw DomainError.forArgument(context, signature, DomainError.Expected.IO_MODE, term, index)
        }
        return IOMode.valueOf(term.value.toUpperCase())
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsVarOrStream(index: Int): Channel<String>? {
        val term = arguments[index]
        when (term) {
            is Var -> return null
            is Struct -> when {
                term matches OUTPUT_STREAM_TERM_PATTERN -> {
                    return context.outputChannels.findByTerm(term)
                        .firstOrNull()
                        ?: throw ExistenceError.forStream(context, term)
                }
                term matches INPUT_STREAM_TERM_PATTERN -> {
                    return context.inputChannels.findByTerm(term)
                        .firstOrNull()
                        ?: throw ExistenceError.forStream(context, term)
                }
            }
        }
        throw DomainError.forArgument(context, signature, STREAM_OR_ALIAS, term, index)
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsVarOrChar(index: Int): Solve.Request<C> {
        return when (val arg = arguments[index]) {
            is Var -> this
            is Atom -> when {
                arg.value == "end_of_file" -> {
                    this
                }
                arg.value.length == 1 -> {
                    this
                }
                else -> {
                    throw TypeError.forArgument(context, signature, TypeError.Expected.IN_CHARACTER, arg, index)
                }
            }
            else -> {
                throw TypeError.forArgument(context, signature, TypeError.Expected.IN_CHARACTER, arg, index)
            }
        }
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsVarOrCharCode(index: Int): Solve.Request<C> {
        val term = arguments[index]
        return when {
            term is Var -> this
            term !is Integer -> ensuringArgumentIsInteger(index)
            term == Integer.MINUS_ONE -> this
            term.isCharacterCode() -> throw RepresentationError.of(context, signature, CHARACTER_CODE)
            else -> this
        }
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsChannel(index: Int): Channel<String> {
        ensuringArgumentIsInstantiated(index)
        ensuringArgumentIsStruct(index)
        val term = arguments[index] as Struct
        return when {
            term is Atom -> {
                sequenceOf(context.inputChannels, context.outputChannels)
                    .firstOrNull { term.value in it }
                    ?.get(term.value)
                    ?: throw ExistenceError.forSourceSink(context, term)
            }
            term matches OUTPUT_STREAM_TERM_PATTERN -> {
                context.outputChannels.findByTerm(term)
                    .firstOrNull()
                    ?: throw ExistenceError.forStream(context, term)
            }
            term matches INPUT_STREAM_TERM_PATTERN -> {
                context.inputChannels.findByTerm(term)
                    .firstOrNull()
                    ?: throw ExistenceError.forStream(context, term)
            }
            else -> {
                throw DomainError.forArgument(context, signature, STREAM_OR_ALIAS, term, index)
            }
        }
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsInputChannel(index: Int): InputChannel<String> =
        when (val channel = ensuringArgumentIsChannel(index)) {
            is InputChannel<String> -> channel
            else -> throw DomainError.forArgument(context, signature, STREAM_TYPE, arguments[index], index)
        }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsOutputChannel(index: Int): OutputChannel<String> =
        when (val channel = ensuringArgumentIsChannel(index)) {
            is OutputChannel<String> -> channel
            else -> throw DomainError.forArgument(context, signature, STREAM_TYPE, arguments[index], index)
        }
}
