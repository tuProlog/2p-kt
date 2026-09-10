package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
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
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.ChannelStore
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.READ_OPTION
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_OR_ALIAS
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_PROPERTY
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.STREAM_TYPE
import it.unibo.tuprolog.solve.exception.error.DomainError.Expected.WRITE_OPTION
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError.Limit.CHARACTER_CODE
import it.unibo.tuprolog.solve.exception.error.SyntaxError
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
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.match
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.mgu
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator
import kotlin.collections.List as KtList

/**
 * Shared helpers backing every I/O primitive of `:io-lib` (`Open3`, `Close1`, `Read1`, `Write2`, ...): argument
 * validation (raising the appropriate ISO error on mismatch), and the read/write/peek "compute, then reply"
 * boilerplate common to most stream predicates.
 *
 * These are extension members of [Solve.Request], so every primitive implementation calls them, e.g.
 * `ensuringArgumentIsOutputChannel(0)`, as if they were request methods; none of this is meant to be used outside
 * of a primitive's `compute`/`computeOne`/`computeAll` body.
 */
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
    private val INFO_VARIABLES_PATTERN by lazy { variables() }
    private val INFO_VARIABLE_NAMES_PATTERN by lazy { variableNames() }
    private val INFO_SINGLETONS_PATTERN by lazy { singletons() }

    private val validPropertiesPattern: Sequence<Term>
        get() =
            sequenceOf(
                PROPERTY_INPUT,
                PROPERTY_OUTPUT,
                PROPERTY_ALIAS_PATTERN,
                PROPERTY_TYPE_PATTERN,
                PROPERTY_EOF_ACTION_PATTERN,
                PROPERTY_REPOSITION_PATTERN,
            )

    private val validOptionsPattern: Sequence<Term>
        get() =
            sequenceOf(this::quoted, this::ignoreOps, this::numberVars)
                .flatMap { sequenceOf(true, false).map(it) }

    private val validInfoPattern: Sequence<Term>
        get() =
            sequenceOf(
                INFO_VARIABLES_PATTERN,
                INFO_VARIABLE_NAMES_PATTERN,
                INFO_SINGLETONS_PATTERN,
            )

    private val supportedPropertiesPattern: Sequence<Term>
        get() =
            sequenceOf(
                PROPERTY_INPUT,
                PROPERTY_OUTPUT,
                PROPERTY_ALIAS_PATTERN,
                PROPERTY_TYPE_TEXT,
                PROPERTY_EOF_ACTION_EOF_CODE,
                PROPERTY_REPOSITION_FALSE,
            )

    private fun alias(alias: String? = null) = Struct.of("alias", alias?.let { Atom.of(it) } ?: Var.anonymous())

    private fun quoted(value: Boolean? = null) = Struct.of("quoted", value?.let { Truth.of(it) } ?: Var.anonymous())

    private fun ignoreOps(value: Boolean? = null) =
        Struct.of(
            "ignore_ops",
            value?.let { Truth.of(it) } ?: Var.anonymous(),
        )

    private fun numberVars(value: Boolean? = null) =
        Struct.of(
            "numbervars",
            value?.let { Truth.of(it) } ?: Var.anonymous(),
        )

    private fun variables(variables: KtList<Var>? = null) =
        Struct.of(
            "variables",
            variables?.let {
                List.of(it)
            } ?: Var.anonymous(),
        )

    private fun vn(
        functor: String,
        vNames: KtList<Pair<Atom, Var>>? = null,
    ) = Struct.of(
        functor,
        vNames
            ?.map { (a, v) -> Struct.of("=", a, v) }
            ?.let { List.of(it) }
            ?: Var.anonymous(),
    )

    private fun variableNames(vNames: KtList<Pair<Atom, Var>>? = null) = vn("variable_names", vNames)

    private fun singletons(vNames: KtList<Pair<Atom, Var>>? = null) = vn("singletons", vNames)

    /**
     * The `stream_property/2` properties (`input`/`output`, plus one `alias(_)` per alias other than
     * [ChannelStore.CURRENT]) that hold for [channel] in this request's context, always terminated by `type(text)`
     * since only text streams are supported.
     */
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
                            .map(::alias),
                    )
                }
                is OutputChannel<*> -> {
                    yield(PROPERTY_OUTPUT)
                    yieldAll(
                        context.outputChannels
                            .aliasesOf(channel as OutputChannel<String>)
                            .filterNot { it == ChannelStore.CURRENT }
                            .map(::alias),
                    )
                }
            }
            yield(PROPERTY_TYPE_TEXT)
        }

    /** The input channel implicitly targeted by `get_char/1`-like unary predicates: the context's current input, or its standard input. */
    val <C : ExecutionContext> Solve.Request<C>.currentInputChannel: InputChannel<String>
        get() = context.inputChannels.let { it.current ?: it.stdIn }

    /** The output channel implicitly targeted by `put_char/1`-like unary predicates: the context's current output, or its standard output. */
    val <C : ExecutionContext> Solve.Request<C>.currentOutputChannel: OutputChannel<String>
        get() = context.outputChannels.let { it.current ?: it.stdOut }

    /**
     * Ensures [term] is one of the `stream_property/2` shapes (`input`, `output`, `alias(_)`, `type(_)`,
     * `eof_action(_)`, `reposition(_)`), whether or not this implementation actually reports it.
     * @throws DomainError (`stream_property`) if [term] is none of the above.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensureTermIsValidProperty(term: Term): Term =
        if (validPropertiesPattern.any { match(term, it) }) {
            term
        } else {
            throw DomainError.forTerm(context, STREAM_PROPERTY, term)
        }

    /**
     * Ensures [term] is one of the `stream_property/2` shapes this implementation actually reports for `open/4`'s
     * option list (`input`, `output`, `alias(_)`, `type(text)`, `eof_action(eof_code)`, `reposition(false)`).
     * @throws SystemError if [term] is a valid-but-unsupported property value (e.g. `type(binary)`).
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensureTermIsSupportedProperty(term: Term): Term =
        if (supportedPropertiesPattern.any { match(term, it) }) {
            term
        } else {
            throw SystemError.forUncaughtException(context, IllegalStateException("unsupported option $term"))
        }

    /**
     * Ensures [term] is one of the `write_term/2,3` option shapes (`quoted(_)`, `ignore_ops(_)`, `numbervars(_)`).
     * @throws DomainError (`write_option`) if [term] is none of the above.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensureTermIsValidOption(term: Term): Term =
        if (validOptionsPattern.any { match(term, it) }) {
            term
        } else {
            throw DomainError.forTerm(context, WRITE_OPTION, term)
        }

    /**
     * Ensures [term] is one of the `read_term/2,3` info shapes (`variables(_)`, `variable_names(_)`, `singletons(_)`).
     * @throws DomainError (`read_option`) if [term] is none of the above.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensureTermIsValidInfo(term: Term): Term =
        if (validInfoPattern.any { match(term, it) }) {
            term
        } else {
            throw DomainError.forTerm(context, READ_OPTION, term)
        }

    /**
     * Ensures the argument at [index] is a proper list of `write_term/2,3` options, validating each element via
     * [ensureTermIsValidOption].
     * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if it is a partial list or unbound.
     * @throws TypeError if it is not a list.
     * @throws DomainError (`write_option`) if any element is not a valid option shape.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsValidOptionList(index: Int): Solve.Request<C> {
        ensuringArgumentIsList(index)
        val list = arguments[index] as List
        for (term in list.toSequence()) {
            ensureTermIsValidOption(term)
        }
        return this
    }

    /**
     * Ensures the argument at [index] is either unbound (returning `null`, meaning "report every info") or a
     * proper list of `read_term/2,3` info shapes, validating each element via [ensureTermIsValidInfo].
     * @throws TypeError if it is bound but not a list.
     * @throws DomainError (`read_option`) if any element is not a valid info shape.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsValidInfoList(index: Int): KtList<Term>? =
        when (val list = arguments[index]) {
            is Var -> null
            else -> {
                ensuringArgumentIsList(index)
                val listOfInfo = (list as List).toList()
                for (term in listOfInfo) {
                    ensureTermIsValidInfo(term)
                }
                listOfInfo
            }
        }

    private fun KtList<Term>.getOption(
        unificator: Unificator,
        pattern: Term,
    ): Boolean =
        asSequence()
            .filterIsInstance<Struct>()
            .filter { unificator.match(pattern, it) }
            .map { it[0] }
            .filterIsInstance<Truth>()
            .map { it.isTrue }
            .firstOrNull() ?: false

    private fun KtList<Term>.getInfo(
        unificator: Unificator,
        pattern: Term,
        value: Term,
    ): Substitution =
        asSequence()
            .filterIsInstance<Struct>()
            .firstOrNull { unificator.match(pattern, it) }
            ?.let { unificator.mgu(it, value) }
            ?: Substitution.empty()

    /**
     * Reads the `write_term/2,3` option list at [index] (via [ensuringArgumentIsValidOptionList]) and turns it into
     * the [TermFormatter] it describes: `quoted(true)` selects [QUOTED_IF_NECESSARY] atom/functor quoting,
     * `ignore_ops(true)` disables operator notation, and `numbervars(true)` renders `'$VAR'(N)` terms as letters.
     * Options not present in the list default to `false`.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsFormatter(index: Int): TermFormatter {
        ensuringArgumentIsValidOptionList(index)
        val optionList = (arguments[index] as List).toList()
        val funcFormat =
            if (optionList.getOption(context.unificator, OPTION_QUOTED_PATTERN)) {
                QUOTED_IF_NECESSARY
            } else {
                LITERAL
            }
        val opFormat =
            if (optionList.getOption(context.unificator, OPTION_IGNORE_OPS_PATTERN)) {
                IGNORE_OPERATORS
            } else {
                EXPRESSIONS
            }
        val numberVars = optionList.getOption(context.unificator, OPTION_NUMBER_VARS_PATTERN)
        return TermFormatter.of(UNDERSCORE, opFormat, funcFormat, numberVars, context.operators)
    }

    /**
     * Ensures the argument at [index] of `stream_property/2` is either unbound, or one of `input`, `output`,
     * `alias(_)` (the only properties this predicate accepts as a query pattern, as opposed to a reported result).
     * @throws DomainError (`stream_property`) otherwise.
     */
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsStreamProperty(index: Int): Solve.Request<C> {
        val term = arguments[index]
        when (term) {
            is Var -> return this
            is Struct -> {
                if (sequenceOf(PROPERTY_INPUT, PROPERTY_OUTPUT, PROPERTY_ALIAS_PATTERN).any { match(term, it) }) {
                    return this
                }
            }
        }
        throw DomainError.forArgument(context, signature, STREAM_PROPERTY, term, index)
    }

    /**
     * Ensures the argument at [index] is an atom parsing (via [Url.of]) into a valid source/sink [Url], as required
     * by `open/3,4` and `consult/1`.
     * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if it is unbound.
     * @throws DomainError (`source_sink`) if it is bound but not an atom, or is an atom that does not parse into a
     * valid [Url] (including as a bare filesystem path, see [Url.Companion.of]).
     */
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

    /**
     * Ensures the argument at [index] is one of the `read`/`write`/`append` atoms, as required by `open/3,4`'s
     * `mode` argument, and returns the corresponding [IOMode].
     * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if it is unbound.
     * @throws TypeError if it is bound but not an atom.
     * @throws DomainError (`io_mode`) if it is an atom but not one of [IOMode.atomValues].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsIOMode(index: Int): IOMode {
        ensuringArgumentIsInstantiated(index)
        ensuringArgumentIsAtom(index)
        val term = arguments[index] as Atom
        if (term !in IOMode.atomValues) {
            throw DomainError.forArgument(context, signature, DomainError.Expected.IO_MODE, term, index)
        }
        return IOMode.valueOf(term.value.uppercase())
    }

    /**
     * Ensures the argument at [index] is either unbound (returning `null`) or a `$stream(...)` term identifying an
     * already-open channel (returning it), as required by the single argument of `current_input/1`/`current_output/1`.
     * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError (`stream`) if it is a well-formed stream term
     * for which no channel is currently open.
     * @throws DomainError (`stream_or_alias`) if it is bound to something other than a `$stream(...)` term.
     */
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsVarOrStream(index: Int): Channel<String>? {
        val term = arguments[index]
        when (term) {
            is Var -> return null
            is Struct ->
                when {
                    match(term, OUTPUT_STREAM_TERM_PATTERN) -> {
                        return context.outputChannels
                            .findByTerm(term)
                            .firstOrNull()
                            ?: throw ExistenceError.forStream(context, term)
                    }
                    match(term, INPUT_STREAM_TERM_PATTERN) -> {
                        return context.inputChannels
                            .findByTerm(term)
                            .firstOrNull()
                            ?: throw ExistenceError.forStream(context, term)
                    }
                }
        }
        throw DomainError.forArgument(context, signature, STREAM_OR_ALIAS, term, index)
    }

    /**
     * Ensures the argument at [index] is either unbound, `end_of_file`, or a one-character atom, as required by the
     * "character" argument of `get_char/1,2`, `peek_char/1,2` and `put_char/1,2`.
     * @throws TypeError (`in_character`) otherwise.
     */
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsVarOrChar(index: Int): Solve.Request<C> =
        when (val arg = arguments[index]) {
            is Var -> this
            is Atom ->
                when {
                    arg.value == "end_of_file" -> this
                    arg.value.length == 1 -> this
                    else -> throw TypeError.forArgument(context, signature, TypeError.Expected.IN_CHARACTER, arg, index)
                }
            else -> throw TypeError.forArgument(context, signature, TypeError.Expected.IN_CHARACTER, arg, index)
        }

    /**
     * Ensures the argument at [index] is either unbound, `-1` (end-of-file), or a valid character code, as
     * required by the "code" argument of `get_code/1,2` and `peek_code/1,2`.
     * @throws TypeError if it is bound but not an [Integer].
     * @throws RepresentationError (`character_code`) if it is an [Integer] outside the representable character-code range.
     */
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

    /**
     * Ensures the argument at [index] denotes an already-open [Channel], resolved either by alias (a bound atom
     * registered in the context's input or output channels) or by `$stream(...)` term, as required by predicates
     * taking a `Stream_or_alias` argument (e.g. `close/1`, `stream_property/2`'s subject).
     * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if it is unbound.
     * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError (`source_sink`/`stream`) if it names an alias
     * or a stream term for which no channel is currently open.
     * @throws DomainError (`stream_or_alias`) if it is neither an atom nor a `$stream(...)` term.
     */
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
            match(term, OUTPUT_STREAM_TERM_PATTERN) -> {
                context.outputChannels
                    .findByTerm(term)
                    .firstOrNull()
                    ?: throw ExistenceError.forStream(context, term)
            }
            match(term, INPUT_STREAM_TERM_PATTERN) -> {
                context.inputChannels
                    .findByTerm(term)
                    .firstOrNull()
                    ?: throw ExistenceError.forStream(context, term)
            }
            else -> {
                throw DomainError.forArgument(context, signature, STREAM_OR_ALIAS, term, index)
            }
        }
    }

    /**
     * Same as [ensuringArgumentIsChannel], additionally requiring the resolved channel to be an [InputChannel], as
     * required by predicates reading from a stream (e.g. `get_char/2`, `read/2`).
     * @throws DomainError (`stream_type`, i.e. `permission_error(input, stream, _)`-like) if the channel resolves
     * to an [OutputChannel] instead.
     */
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsInputChannel(index: Int): InputChannel<String> =
        when (val channel = ensuringArgumentIsChannel(index)) {
            is InputChannel<String> -> channel
            else -> throw DomainError.forArgument(context, signature, STREAM_TYPE, arguments[index], index)
        }

    /**
     * Same as [ensuringArgumentIsChannel], additionally requiring the resolved channel to be an [OutputChannel], as
     * required by predicates writing to a stream (e.g. `put_char/2`, `write/2`).
     * @throws DomainError (`stream_type`) if the channel resolves to an [InputChannel] instead.
     */
    fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsOutputChannel(index: Int): OutputChannel<String> =
        when (val channel = ensuringArgumentIsChannel(index)) {
            is OutputChannel<String> -> channel
            else -> throw DomainError.forArgument(context, signature, STREAM_TYPE, arguments[index], index)
        }

    /** Formats [term] via [formatter] and writes it to [channel], replying success, or failing on a closed channel. */
    fun Solve.Request<ExecutionContext>.writeTermAndReply(
        channel: OutputChannel<String>,
        term: Term,
        formatter: TermFormatter,
    ): Solve.Response =
        try {
            channel.write(term.format(formatter))
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }

    /** Writes the character coded by [arg] to [channel], replying success, or failing on a closed channel. */
    fun Solve.Request<ExecutionContext>.writeCodeAndReply(
        channel: OutputChannel<String>,
        arg: Integer,
    ): Solve.Response =
        try {
            channel.write("${arg.intValue.toChar()}")
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }

    /** Writes the character [arg] to [channel], replying success, or failing on a closed channel. */
    fun Solve.Request<ExecutionContext>.writeCharAndReply(
        channel: OutputChannel<String>,
        arg: Atom,
    ): Solve.Response =
        try {
            channel.write(arg.value)
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }

    /** Peeks the next character code from [channel] (`-1` at end of stream) and unifies [arg] with it, or fails on a closed channel. */
    fun Solve.Request<ExecutionContext>.peekCodeAndReply(
        channel: InputChannel<String>,
        arg: Term,
    ): Solve.Response =
        try {
            val code = channel.peek()?.get(0)?.code ?: -1
            replyWith(mgu(arg, Integer.of(code)))
        } catch (_: IllegalStateException) {
            replyFail()
        }

    /** Peeks the next character from [channel] (`end_of_file` atom at end of stream) and unifies [arg] with it, or fails on a closed channel. */
    fun Solve.Request<ExecutionContext>.peekCharAndReply(
        channel: InputChannel<String>,
        arg: Term,
    ): Solve.Response =
        try {
            val char = channel.peek() ?: "end_of_file"
            replyWith(mgu(arg, Atom.of(char)))
        } catch (_: IllegalStateException) {
            replyFail()
        }

    /** Reads (consuming it) the next character code from [channel] (`-1` at end of stream) and unifies [arg] with it, or fails on a closed channel. */
    fun Solve.Request<ExecutionContext>.readCodeAndReply(
        channel: InputChannel<String>,
        arg: Term,
    ): Solve.Response =
        try {
            val code = channel.read()?.get(0)?.code ?: -1
            replyWith(mgu(arg, Integer.of(code)))
        } catch (_: IllegalStateException) {
            replyFail()
        }

    /** Reads (consuming it) the next character from [channel] (`end_of_file` atom at end of stream) and unifies [arg] with it, or fails on a closed channel. */
    fun Solve.Request<ExecutionContext>.readCharAndReply(
        channel: InputChannel<String>,
        arg: Term,
    ): Solve.Response =
        try {
            val char = channel.read() ?: "end_of_file"
            replyWith(mgu(arg, Atom.of(char)))
        } catch (_: IllegalStateException) {
            replyFail()
        }

    private val Term.singletons: Set<Var>
        get() =
            variables
                .groupBy { it }
                .asSequence()
                .filter { it.value.size == 1 }
                .map { it.key }
                .toSet()

    private fun Iterable<Var>.toAssignments(): KtList<Pair<Atom, Var>> = asSequence().toAssignments()

    private fun Sequence<Var>.toAssignments(): KtList<Pair<Atom, Var>> =
        groupBy { Atom.of(it.name) }
            .flatMap { (n, vs) -> vs.asSequence().map { n to it } }
            .toList()

    /**
     * Implements `read/1,2` and `read_term/2,3`: parses the next [it.unibo.tuprolog.core.Term] out of [channel]
     * (via [asTermChannel]) and unifies it with [arg], failing (not erroring) if the channel has no more terms
     * available.
     *
     * When [lastIsInfoList] (i.e. this is a `read_term/2,3` call), the last argument is read as a list of
     * `variables(_)`/`variable_names(_)`/`singletons(_)` info requests (via [ensuringArgumentIsValidInfoList]) and
     * unified with the corresponding read-term metadata; for plain `read/1,2`, that metadata is discarded.
     *
     * @throws SystemError if the underlying channel does not support reading terms at all (e.g. JS's not-yet-supported
     * term channels, or a channel closed mid-read).
     * @throws SyntaxError if the channel's next term is malformed Prolog syntax.
     */
    fun Solve.Request<ExecutionContext>.readTermAndReply(
        channel: InputChannel<String>,
        arg: Term,
        lastIsInfoList: Boolean = false,
    ): Solve.Response {
        try {
            val termsChannel = channel.asTermChannel(context.operators)
            val infoList =
                if (lastIsInfoList) {
                    ensuringArgumentIsValidInfoList(arguments.lastIndex)
                } else {
                    emptyList()
                }
            if (!termsChannel.available) return replyFail()
            return when (val read = termsChannel.read()) {
                null -> replyFail()
                else -> {
                    val variables = variables(read.variables.toList())
                    val variableNames = variableNames(read.variables.toAssignments())
                    val singletons = singletons(read.singletons.toAssignments())
                    val info =
                        infoList?.let {
                            val u = context.unificator
                            it.getInfo(u, INFO_SINGLETONS_PATTERN, singletons) +
                                it.getInfo(u, INFO_VARIABLES_PATTERN, variables) +
                                it.getInfo(u, INFO_VARIABLE_NAMES_PATTERN, variableNames)
                        } ?: mgu(arguments.last(), List.of(variables, variableNames, singletons))
                    replyWith(mgu(arg, read) + info)
                }
            }
        } catch (e: IllegalStateException) {
            throw SystemError.forUncaughtException(context, e)
        } catch (e: ParseException) {
            throw SyntaxError.whileParsingTerm(
                context,
                channel.streamTerm.toString(),
                e.line,
                e.column,
                e.message ?: "<no details provided>",
            )
        }
    }

    private val Term?.alias: String?
        get() = ((this as? Struct)?.getArgAt(0) as? Atom)?.value

    /**
     * Implements `open/3,4`: validates the `SourceSink`/`Mode`/`Stream`/`Options` arguments (`third` is the `Stream`
     * output argument, shared by both arities), opens the corresponding channel via
     * [it.unibo.tuprolog.solve.libs.io.openInputChannel]/[it.unibo.tuprolog.solve.libs.io.openOutputChannel], and
     * registers it (under the `alias(_)` option, if given, or an auto-generated one) before replying with the new
     * `$stream(...)` term unified with [third].
     *
     * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if `SourceSink`, `Mode` or `Options` is unbound.
     * @throws DomainError (`source_sink`, `io_mode`, `stream_property`) if `SourceSink` is not a valid [Url],
     * `Mode` is not `read`/`write`/`append`, or an element of `Options` is not a `stream_property/2` shape.
     * @throws SystemError if an element of `Options` is a stream property this implementation does not report
     * (e.g. `type(binary)`).
     * @throws it.unibo.tuprolog.solve.libs.io.exceptions.IOException if the resource cannot actually be opened
     * (e.g. missing file, writing attempted on a non-file [Url]), converted into a
     * [it.unibo.tuprolog.solve.exception.error.SystemError].
     */
    fun Solve.Request<ExecutionContext>.open(third: Term): Solve.Response {
        val url = ensuringArgumentIsUrl(0)
        val mode = ensuringArgumentIsIOMode(1)
        ensuringArgumentIsVariable(2)
        val options =
            if (arguments.size >= 4) {
                ensuringArgumentIsList(3)
                (arguments[3] as List)
                    .toSequence()
                    .map { ensureTermIsValidProperty(it) }
                    .map { ensureTermIsSupportedProperty(it) }
                    .toSet()
            } else {
                emptySet()
            }
        val alias = options.firstOrNull { match(PROPERTY_ALIAS_PATTERN, it) }?.alias
        return when (mode) {
            IOMode.READ -> replyOpeningStream(url.openInputChannel(), third, alias)
            IOMode.WRITE -> replyOpeningStream(url.openOutputChannel(false), third, alias)
            IOMode.APPEND -> replyOpeningStream(url.openOutputChannel(true), third, alias)
        }
    }

    private fun Solve.Request<ExecutionContext>.replyOpeningStream(
        channel: OutputChannel<String>,
        third: Term,
        alias: String? = null,
    ): Solve.Response {
        val streamTerm = channel.streamTerm
        return replyWith(mgu(third, streamTerm)) {
            openOutputChannel(alias ?: "output_channel${channel.streamTerm[1]}", channel)
        }
    }

    private fun Solve.Request<ExecutionContext>.replyOpeningStream(
        channel: InputChannel<String>,
        third: Term,
        alias: String? = null,
    ): Solve.Response {
        val streamTerm = channel.streamTerm
        return replyWith(mgu(third, streamTerm)) {
            openInputChannel(alias ?: "input_channel${channel.streamTerm[1]}", channel)
        }
    }
}
