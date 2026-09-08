package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.utils.Cached
import javafx.application.Platform
import javafx.concurrent.Task
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.reactfx.Subscription
import java.time.Duration
import java.util.Optional
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool

/**
 * Regex-based Prolog syntax highlighter for a RichTextFX [CodeArea], as used by [FileTabView] for the editor pane.
 *
 * It recognizes comments, string/quoted-atom literals, numbers (integer, float, hex/octal/binary, character
 * codes), parentheses/braces/brackets, clause-terminating full stops, variables, functors, plain atoms, and
 * (via [operators]) the current [OperatorSet]'s functors as keywords, then applies a JavaFX CSS style class
 * per matched token (see the stylesheets in `Resources`) using RichTextFX's [org.fxmisc.richtext.model.StyleSpans].
 *
 * A single regex combining all token patterns is built lazily and cached; it is invalidated and rebuilt whenever
 * [operators] is reassigned, so that keywords stay in sync with the theory currently loaded into the solver
 * (see [FileTabView.notifyOperators]).
 *
 * Example:
 * ```kotlin
 * val coloring = SyntaxColoring(codeArea)
 * coloring.activate() // highlights on every edit, debounced by `delay`
 * ```
 */
class SyntaxColoring(
    private val codeArea: CodeArea,
    delay: Duration = DEFAULT_UPDATE_DELAY,
    operators: OperatorSet = OperatorSet.DEFAULT,
    private val executor: ExecutorService = ForkJoinPool.commonPool(),
) {
    /** How long to wait, after the user stops editing [codeArea],
     * before recomputing the highlighting; see [activate]. */
    @Volatile
    var delay: Duration = delay
        @Synchronized get

        @Synchronized set(value) {
            field = value
            if (isActive) {
                activate()
                deactivate()
            }
        }

    /** The [OperatorSet] whose functors are highlighted as keywords; reassigning it invalidates the cached pattern. */
    @Volatile
    var operators: OperatorSet = operators
        @Synchronized get

        @Synchronized set(value) {
            field = value
            patternCache.invalidate()
        }

    private fun computeHighlightingAsync(): Task<StyleSpans<Collection<String>>> {
        val task: Task<StyleSpans<Collection<String>>> =
            object : Task<StyleSpans<Collection<String>>>() {
                override fun call(): StyleSpans<Collection<String>> = computeHighlighting(codeArea.text)
            }
        executor.execute(task)
        return task
    }

    private fun applyHighlighting(highlighting: StyleSpans<Collection<String>>) {
        codeArea.setStyleSpans(0, highlighting)
    }

    /** Synchronously recomputes and applies highlighting for [codeArea]'s current text, bypassing [delay]. */
    fun applyHighlightingNow() {
        Platform.runLater {
            applyHighlighting(computeHighlighting(codeArea.text))
        }
    }

    @Volatile
    private var subscription: Subscription? = null

    /** Whether [activate] has been called without a matching [deactivate] yet. */
    val isActive: Boolean
        @Synchronized get() = subscription != null

    /**
     * Starts observing [codeArea] for edits, recomputing and applying highlighting (on [executor],
     * debounced by [delay]) after each burst of changes settles.
     *
     * @throws IllegalStateException if already [isActive].
     */
    @Synchronized
    fun activate() {
        if (subscription == null) {
            subscription =
                codeArea
                    .multiPlainChanges()
                    .successionEnds(delay)
                    .supplyTask { computeHighlightingAsync() }
                    .awaitLatest(codeArea.multiPlainChanges())
                    .filterMap {
                        when {
                            it.isSuccess -> {
                                Optional.of(it.get())
                            }
                            else -> {
                                it.failure.printStackTrace()
                                Optional.empty()
                            }
                        }
                    }.subscribe { applyHighlighting(it) }
        } else {
            error("Syntax coloring is already active")
        }
    }

    /**
     * Stops observing [codeArea] for edits (undoing [activate]); does not clear any highlighting already applied.
     *
     * @throws IllegalStateException if not currently [isActive].
     */
    @Synchronized
    fun deactivate() {
        subscription.let {
            if (it != null) {
                it.unsubscribe()
                subscription = null
            } else {
                error("Syntax coloring is not active")
            }
        }
    }

    private val patternCache: Cached<Regex> =
        Cached.of {
            pattern(this.operators)
        }

    private fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
        val matches = patternCache.value.findAll(text)
        var lastKwEnd = 0
        val spansBuilder = StyleSpansBuilder<Collection<String>>()
        for (match in matches) {
            val styleClass = match.styleClass
            val from = match.range.first
            val to = match.range.last + 1
            (from - lastKwEnd).let {
                if (it > 0) {
                    spansBuilder.add(emptyList(), from - lastKwEnd)
                }
            }
            spansBuilder.add(listOf(styleClass), to - from)
            lastKwEnd = to
        }
        spansBuilder.add(emptyList(), text.length - lastKwEnd)
        return spansBuilder.create()
    }

    companion object {
        private val DEFAULT_UPDATE_DELAY = Duration.ofMillis(100)

        private fun keywords(operators: OperatorSet): Regex =
            operators
                .map { it.functor }
                .map {
                    if (it.matches(BASIC_ATOM_PATTERN)) {
                        wordify(it)
                    } else {
                        Regex.escape(it)
                    }
                }.joinToString("|") { "($it)" }
                .toRegex()

//        private val FOLLOWED_BY_NONWORD = "(?=[^_A-Za-z0-9])"

        private val PAREN_PATTERN = Regex("[()]")

        private val BRACE_PATTERN = Regex("[{}]")

        private val BRACKET_PATTERN = Regex("[\\[\\]]")

        private val FULLSTOP_PATTERN = Regex("\\.\\s*([\\n\\r]+|$)")

        private val SINGLE_QUOTED_STRING_PATTERN = Regex("\"([^\"\\\\]|\\\\.)*\"")

        private val DOUBLE_QUOTED_STRING_PATTERN = Regex("'([^'\\\\]|\\\\.)*'")

        private val STRING_PATTERN = anyOf(SINGLE_QUOTED_STRING_PATTERN, DOUBLE_QUOTED_STRING_PATTERN)

        private val SINGLE_LINE_COMMENT_PATTERN = Regex("%[^\\n]*")

        private val MULTI_LINE_COMMENT_PATTERN = Regex("/\\*(.|\\R)*?\\*/")

        private val COMMENT_PATTERN = anyOf(SINGLE_LINE_COMMENT_PATTERN, MULTI_LINE_COMMENT_PATTERN)

        private val VARIABLE_PATTERN = Regex("[_A-Z][_A-Za-z0-9]*").asWord()

        private val BASIC_ATOM_PATTERN = Regex("[a-z][_A-Za-z0-9]*")

        private val ATOM_PATTERN = BASIC_ATOM_PATTERN.asWord()

        private val FUNCTOR_PATTERN = ATOM_PATTERN and Regex("\\s*(?=[(])")

        private val INTEGER_PATTERN = Regex("[0-9]+")

        private val HEX_PATTERN = Regex("0[xX][0-9A-Fa-f]+")

        private val BIN_PATTERN = Regex("0[bB][0-1]+")

        private val OCT_PATTERN = Regex("0[oO][0-7]+")

        private val FLOAT_PATTERN = Regex("[0-9]+\\.[0-9]+([eE][-+]?[0-9]+)?")

        private val CHAR_PATTERN = Regex("0'(\\\\[abfnrtv'`\"]|.)")

        private val NUMBER_PATTERN =
            anyOf(
                FLOAT_PATTERN,
                INTEGER_PATTERN,
                HEX_PATTERN,
                BIN_PATTERN,
                OCT_PATTERN,
                CHAR_PATTERN,
            ).asWord()

        private const val KEYWORD = "KEYWORD"
        private const val PAREN = "PAREN"
        private const val NUMBER = "NUMBER"
        private const val BRACE = "BRACE"
        private const val BRACKET = "BRACKET"
        private const val STRING = "STRING"
        private const val COMMENT = "COMMENT"
        private const val VARIABLE = "VARIABLE"
        private const val FUNCTOR = "FUNCTOR"
        private const val ATOM = "ATOM"
        private const val FULLSTOP = "FULLSTOP"

        private fun pattern(operators: OperatorSet) =
            anyOf(
                COMMENT_PATTERN.asGroup(COMMENT),
                keywords(operators).asGroup(KEYWORD),
                PAREN_PATTERN.asGroup(PAREN),
                BRACE_PATTERN.asGroup(BRACE),
                BRACKET_PATTERN.asGroup(BRACKET),
                FUNCTOR_PATTERN.asGroup(FUNCTOR),
                ATOM_PATTERN.asGroup(ATOM),
                VARIABLE_PATTERN.asGroup(VARIABLE),
                NUMBER_PATTERN.asGroup(NUMBER),
                STRING_PATTERN.asGroup(STRING),
                FULLSTOP_PATTERN.asGroup(FULLSTOP),
            )

        private val MatchResult.styleClass: String
            get() =
                when {
                    groups[KEYWORD] != null -> KEYWORD.lowercase()
                    groups[PAREN] != null -> PAREN.lowercase()
                    groups[NUMBER] != null -> NUMBER.lowercase()
                    groups[BRACE] != null -> BRACE.lowercase()
                    groups[BRACKET] != null -> BRACKET.lowercase()
                    groups[STRING] != null -> STRING.lowercase()
                    groups[COMMENT] != null -> COMMENT.lowercase()
                    groups[VARIABLE] != null -> VARIABLE.lowercase()
                    groups[ATOM] != null -> ATOM.lowercase()
                    groups[FUNCTOR] != null -> FUNCTOR.lowercase()
                    groups[FULLSTOP] != null -> FULLSTOP.lowercase()
                    else -> ""
                }
    }
}
