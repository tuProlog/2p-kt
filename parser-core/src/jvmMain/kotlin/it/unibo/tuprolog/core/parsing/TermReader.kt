package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import java.io.InputStream
import java.io.Reader
import java.io.StringReader

/**
 * Lazily reads Prolog terms from JVM character or byte streams.
 *
 * [readTerms] does not consume its source until the returned [Sequence] is iterated. The sequence
 * is backed by one stateful parse session and should be consumed once and sequentially. Syntax,
 * I/O, and buffer failures become [ParseException]s at the iteration step that encounters them.
 * The default reader adapter closes its input at EOF; wrapping an [InputStream] therefore also
 * closes that stream.
 *
 * @property scope scope used to construct terms
 * @property defaultOperatorSet operators used by overloads without an explicit set
 * @see TermParser
 */
interface TermReader {
    /** Scope used to construct parsed terms. */
    val scope: Scope

    /** Operators used by overloads without an explicit set. */
    val defaultOperatorSet: OperatorSet

    /**
     * Reads the next term from [reader], or `null` at EOF, using [operators].
     *
     * @throws ParseException if reading, lexing, or parsing the next term fails
     */
    fun readTerm(
        reader: Reader,
        operators: OperatorSet,
    ): Term?

    /**
     * @throws ParseException if reading, lexing, or parsing the next term fails
     */
    fun readTerm(reader: Reader): Term? = readTerm(reader, defaultOperatorSet)

    /**
     * Reads the next term from [inputStream], or `null` at EOF, using [operators].
     *
     * Bytes are decoded using [java.io.InputStreamReader]'s platform-default charset.
     *
     * @throws ParseException if reading, lexing, or parsing the next term fails
     */
    fun readTerm(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Term?

    /**
     * @throws ParseException if reading, lexing, or parsing the next term fails
     */
    fun readTerm(inputStream: InputStream): Term? = readTerm(inputStream, defaultOperatorSet)

    /**
     * Returns terms read lazily from [reader] using [operators].
     *
     * @throws ParseException during iteration if reading, lexing, or parsing fails
     */
    fun readTerms(
        reader: Reader,
        operators: OperatorSet,
    ): Sequence<Term>

    /**
     * @throws ParseException during iteration if reading, lexing, or parsing fails
     */
    fun readTerms(reader: Reader): Sequence<Term> = readTerms(reader, defaultOperatorSet)

    /**
     * Returns terms decoded lazily from [inputStream] using [operators].
     *
     * @throws ParseException during iteration if reading, lexing, or parsing fails
     */
    fun readTerms(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Sequence<Term>

    /**
     * @throws ParseException during iteration if reading, lexing, or parsing fails
     */
    fun readTerms(inputStream: InputStream): Sequence<Term> = readTerms(inputStream, defaultOperatorSet)

    /**
     * Returns terms parsed lazily from [string] using [operators].
     *
     * @throws ParseException during iteration if lexing or parsing fails
     */
    fun readTerms(
        string: String,
        operators: OperatorSet,
    ): Sequence<Term> = readTerms(StringReader(string), operators)

    /**
     * @throws ParseException during iteration if lexing or parsing fails
     */
    fun readTerms(string: String): Sequence<Term> = readTerms(string, defaultOperatorSet)

    companion object {
        /** Creates a reader with no default operators. */
        @JvmStatic
        @JvmOverloads
        fun withNoOperator(scope: Scope = Scope.empty()): TermReader = withOperators(OperatorSet.EMPTY, scope)

        /** Creates a reader with the standard operator set. */
        @JvmStatic
        @JvmOverloads
        fun withStandardOperators(scope: Scope = Scope.empty()): TermReader = withOperators(OperatorSet.STANDARD, scope)

        /** Creates a reader with the library's default operator set. */
        @JvmStatic
        @JvmOverloads
        fun withDefaultOperators(scope: Scope = Scope.empty()): TermReader = withOperators(OperatorSet.DEFAULT, scope)

        /** Creates a reader using [operators] and [scope]. */
        @JvmStatic
        @JvmOverloads
        fun withOperators(
            operators: OperatorSet,
            scope: Scope = Scope.empty(),
        ): TermReader = TermReaderImpl(scope, operators)

        /** Creates a reader from individual [operators] and [scope]. */
        @JvmStatic
        @JvmOverloads
        fun withOperators(
            vararg operators: Operator,
            scope: Scope = Scope.empty(),
        ): TermReader = withOperators(OperatorSet(*operators), scope)
    }
}
