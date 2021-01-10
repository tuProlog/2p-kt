package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.TermFormatter.FuncFormat.LITERAL
import it.unibo.tuprolog.core.TermFormatter.FuncFormat.QUOTED_IF_NECESSARY
import it.unibo.tuprolog.core.TermFormatter.OpFormat.EXPRESSIONS
import it.unibo.tuprolog.core.TermFormatter.OpFormat.IGNORE_OPERATORS
import it.unibo.tuprolog.core.TermFormatter.VarFormat.UNDERSCORE
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.ChannelStore
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_OR_ALIAS
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_PROPERTY
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_TYPE
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.WRITE_OPTION
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError.Limit.CHARACTER_CODE
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.libs.io.IOMode
import it.unibo.tuprolog.solve.libs.io.Url
import it.unibo.tuprolog.solve.libs.io.asTermChannel
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import it.unibo.tuprolog.solve.libs.io.openInputChannel
import it.unibo.tuprolog.solve.libs.io.openOutputChannel
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsAtom
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsInstantiated
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsInteger
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsList
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsStruct
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsVariable
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.isCharacterCode
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object IOPrimitiveUtils {

    private val INPUT_STREAM_TERM_PATTERN by lazy { InputChannel.streamTerm() }
    private val OUTPUT_STREAM_TERM_PATTERN by lazy { OutputChannel.streamTerm() }
    private val PROPERTY_TYPE_PATTERN by lazy { Struct.of("type", Var.anonymous()) }
    private val PROPERTY_REPOSITION_PATTERN by lazy { Struct.of("reposition", Var.anonymous()) }
    private val PROPERTY_EOF_ACTION_PATTERN by lazy { Struct.of("eof_action", Var.anonymous()) }
    private val PROPERTY_ALIAS_PATTERN by lazy { alias() }
    private val PROPERTY_INPUT by lazy { Atom.of("input") }
    private val PROPERTY_OUTPUT by lazy { Atom.of("output") }
    private val PROPERTY_TYPE_TEXT by lazy { Struct.of("type", Atom.of("text")) }
    private val PROPERTY_REPOSITION_FALSE by lazy { Struct.of("reposition", Truth.FALSE) }
    private val PROPERTY_EOF_ACTION_EOF_CODE by lazy { Struct.of("eof_action", Atom.of("eof_code")) }
    private val OPTION_QUOTED_PATTERN by lazy { quoted() }
    private val OPTION_NUMBER_VARS_PATTERN by lazy { numberVars() }
    private val OPTION_IGNORE_OPS_PATTERN by lazy { ignoreOps() }

    private val validPropertiesPattern: Sequence<Term>
        get() = sequenceOf(
            PROPERTY_INPUT,
            PROPERTY_OUTPUT,
            PROPERTY_ALIAS_PATTERN,
            PROPERTY_TYPE_PATTERN,
            PROPERTY_EOF_ACTION_PATTERN,
            PROPERTY_REPOSITION_PATTERN
        )

    private val validOptionsPattern: Sequence<Term>
        get() = sequenceOf(this::quoted, this::ignoreOps, this::numberVars)
            .flatMap { sequenceOf(true, false).map(it) }

    private val supportedPropertiesPattern: Sequence<Term>
        get() = sequenceOf(
            PROPERTY_INPUT,
            PROPERTY_OUTPUT,
            PROPERTY_ALIAS_PATTERN,
            PROPERTY_TYPE_TEXT,
            PROPERTY_EOF_ACTION_EOF_CODE,
            PROPERTY_REPOSITION_FALSE
        )

    private fun alias(alias: String? = null) =
        Struct.of("alias", alias?.let { Atom.of(it) } ?: Var.anonymous())

    private fun quoted(value: Boolean? = null) =
        Struct.of("quoted", value?.let { Truth.of(it) } ?: Var.anonymous())

    private fun ignoreOps(value: Boolean? = null) =
        Struct.of("ignore_ops", value?.let { Truth.of(it) } ?: Var.anonymous())

    private fun numberVars(value: Boolean? = null) =
        Struct.of("numbervars", value?.let { Truth.of(it) } ?: Var.anonymous())

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

    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensureTermIsValidProperty(term: Term): Term =
        if (validPropertiesPattern.any { it matches term }) {
            term
        } else {
            throw DomainError.forTerm(context, STREAM_PROPERTY, term)
        }

    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensureTermIsSupportedProperty(term: Term): Term =
        if (supportedPropertiesPattern.any { it matches term }) {
            term
        } else {
            throw SystemError.forUncaughtException(context, IllegalStateException("unsupported option $term"))
        }

    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensureTermIsValidOption(term: Term): Term =
        if (validOptionsPattern.any { it matches term }) {
            term
        } else {
            throw DomainError.forTerm(context, WRITE_OPTION, term)
        }

    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsValidOptionList(index: Int): Solve.Request<C> {
        ensuringArgumentIsList(index)
        val list = arguments[index] as List
        for (term in list.toSequence()) {
            ensureTermIsValidOption(term)
        }
        return this
    }

    private fun kotlin.collections.List<Term>.getOption(pattern: Term): Boolean =
        asSequence()
            .filterIsInstance<Struct>()
            .filter { it matches pattern }
            .map { it[0] }
            .filterIsInstance<Truth>()
            .map { it.isTrue }
            .firstOrNull() ?: false

    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsFormatter(index: Int): TermFormatter {
        ensuringArgumentIsValidOptionList(index)
        val optionList = (arguments[index] as List).toList()
        val funcFormat = if (optionList.getOption(OPTION_QUOTED_PATTERN)) QUOTED_IF_NECESSARY else LITERAL
        val opFormat = if (optionList.getOption(OPTION_IGNORE_OPS_PATTERN)) IGNORE_OPERATORS else EXPRESSIONS
        val numberVars = optionList.getOption(OPTION_NUMBER_VARS_PATTERN)
        return TermFormatter.of(UNDERSCORE, opFormat, funcFormat, numberVars, context.operators)
    }

    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsStreamProperty(index: Int): Solve.Request<C> {
        val term = arguments[index]
        when (term) {
            is Var -> return this
            is Struct -> {
                if (sequenceOf(PROPERTY_INPUT, PROPERTY_OUTPUT, PROPERTY_ALIAS_PATTERN).any { it matches term }) {
                    return this
                }
            }
        }
        throw DomainError.forArgument(context, signature, STREAM_PROPERTY, term, index)
    }

    @Suppress("MemberVisibilityCanBePrivate")
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

    @Suppress("MemberVisibilityCanBePrivate")
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

    fun Solve.Request<ExecutionContext>.writeTermAndReply(
        channel: OutputChannel<String>,
        term: Term,
        formatter: TermFormatter
    ): Solve.Response {
        return try {
            channel.write(term.format(formatter))
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }

    fun Solve.Request<ExecutionContext>.writeCodeAndReply(
        channel: OutputChannel<String>,
        arg: Integer
    ): Solve.Response {
        return try {
            channel.write("${arg.intValue.toChar()}")
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }

    fun Solve.Request<ExecutionContext>.writeCharAndReply(channel: OutputChannel<String>, arg: Atom): Solve.Response {
        return try {
            channel.write(arg.value)
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }

    fun Solve.Request<ExecutionContext>.peekCodeAndReply(channel: InputChannel<String>, arg: Term): Solve.Response {
        return try {
            val code = channel.peek()?.get(0)?.toInt() ?: -1
            replyWith(arg mguWith Integer.of(code))
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }

    fun Solve.Request<ExecutionContext>.peekCharAndReply(channel: InputChannel<String>, arg: Term): Solve.Response {
        return try {
            val char = channel.peek() ?: "end_of_file"
            replyWith(arg mguWith Atom.of(char))
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }

    fun Solve.Request<ExecutionContext>.readCodeAndReply(channel: InputChannel<String>, arg: Term): Solve.Response {
        return try {
            val code = channel.read()?.get(0)?.toInt() ?: -1
            replyWith(arg mguWith Integer.of(code))
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }

    fun Solve.Request<ExecutionContext>.readCharAndReply(channel: InputChannel<String>, arg: Term): Solve.Response {
        return try {
            val char = channel.read() ?: "end_of_file"
            replyWith(arg mguWith Atom.of(char))
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }

    fun Solve.Request<ExecutionContext>.readTermAndReply(channel: InputChannel<String>, arg: Term): Solve.Response {
        try {
            val termsChannel = channel.asTermChannel(context.operators)
            if (!termsChannel.available) return replyFail()
            return when (val read = termsChannel.read()) {
                null -> replyFail()
                else -> replyWith(arg mguWith read)
            }
        } catch (e: IllegalStateException) {
            throw SystemError.forUncaughtException(context, e)
        }
    }

    private val Term?.alias: String?
        get() = ((this as? Struct)?.getArgAt(0) as? Atom)?.value

    fun Solve.Request<ExecutionContext>.open(third: Term): Solve.Response {
        val url = ensuringArgumentIsUrl(0)
        val mode = ensuringArgumentIsIOMode(1)
        ensuringArgumentIsVariable(2)
        val options = if (arguments.size >= 4) {
            ensuringArgumentIsList(3)
            (arguments[3] as List).toSequence()
                .map { ensureTermIsValidProperty(it) }
                .map { ensureTermIsSupportedProperty(it) }
                .toSet()
        } else {
            emptySet()
        }
        val alias = options.firstOrNull { it matches PROPERTY_ALIAS_PATTERN }?.alias
        return when (mode) {
            IOMode.READ -> replyOpeningStream(url.openInputChannel(), third, alias)
            IOMode.WRITE -> replyOpeningStream(url.openOutputChannel(false), third, alias)
            IOMode.APPEND -> replyOpeningStream(url.openOutputChannel(true), third, alias)
        }
    }

    private fun Solve.Request<ExecutionContext>.replyOpeningStream(
        channel: OutputChannel<String>,
        third: Term,
        alias: String? = null
    ): Solve.Response {
        val streamTerm = channel.streamTerm
        return replyWith(third mguWith streamTerm) {
            openOutputChannel(alias ?: "output_channel${channel.streamTerm[1]}", channel)
        }
    }

    private fun Solve.Request<ExecutionContext>.replyOpeningStream(
        channel: InputChannel<String>,
        third: Term,
        alias: String? = null
    ): Solve.Response {
        val streamTerm = channel.streamTerm
        return replyWith(third mguWith streamTerm) {
            openInputChannel(alias ?: "input_channel${channel.streamTerm[1]}", channel)
        }
    }
}
