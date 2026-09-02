package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import java.io.InputStream
import java.io.Reader
import kotlin.jvm.JvmStatic

/**
 * Reads clauses and theories lazily from JVM character or byte streams.
 *
 * Lazy clause sequences are backed by one parse session and should be consumed once in order.
 * Reading starts on iteration. The default adapter closes its input at EOF; an [InputStream]
 * wrapped for decoding is closed with that reader. Parsing and I/O failures are translated to
 * [it.unibo.tuprolog.core.parsing.ParseException] and identify their zero-based clause index.
 *
 * @property defaultOperatorSet initial operators used by overloads that omit them
 * @see ClausesParser
 */
@Suppress("TooManyFunctions")
interface ClausesReader {
    /** Initial operators used when no explicit set is supplied. */
    val defaultOperatorSet: OperatorSet

    /**
     * Reads all clauses from [inputStream] into a theory using [operators].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException if reading or parsing fails
     */
    fun readTheory(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Theory = Theory.of(Unificator.default, readClausesLazily(inputStream, operators))

    /**
     * Reads all clauses from [reader] into a theory using [operators].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException if reading or parsing fails
     */
    fun readTheory(
        reader: Reader,
        operators: OperatorSet,
    ): Theory = Theory.of(Unificator.default, readClausesLazily(reader, operators))

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException if reading or parsing fails
     */
    fun readTheory(inputStream: InputStream): Theory = readTheory(inputStream, defaultOperatorSet)

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException if reading or parsing fails
     */
    fun readTheory(reader: Reader): Theory = readTheory(reader, defaultOperatorSet)

    /**
     * Returns clauses decoded lazily from [inputStream] using [operators].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException during iteration on failure
     */
    fun readClausesLazily(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Sequence<Clause>

    /**
     * Returns clauses read lazily from [reader] using [operators].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException during iteration on failure
     */
    fun readClausesLazily(
        reader: Reader,
        operators: OperatorSet,
    ): Sequence<Clause>

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException during iteration on failure
     */
    fun readClausesLazily(inputStream: InputStream): Sequence<Clause> =
        readClausesLazily(inputStream, defaultOperatorSet)

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException during iteration on failure
     */
    fun readClausesLazily(reader: Reader): Sequence<Clause> = readClausesLazily(reader, defaultOperatorSet)

    /**
     * Reads all clauses eagerly from [inputStream] using [operators].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
     */
    fun readClauses(
        inputStream: InputStream,
        operators: OperatorSet,
    ): List<Clause> = readClausesLazily(inputStream, operators).toList()

    /**
     * Reads all clauses eagerly from [reader] using [operators].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
     */
    fun readClauses(
        reader: Reader,
        operators: OperatorSet,
    ): List<Clause> = readClausesLazily(reader, operators).toList()

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
     */
    fun readClauses(inputStream: InputStream): List<Clause> = readClauses(inputStream, defaultOperatorSet)

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
     */
    fun readClauses(reader: Reader): List<Clause> = readClauses(reader, defaultOperatorSet)

    companion object {
        /** Creates a reader with no initial operators. */
        @JvmStatic
        fun withNoOperator(): ClausesReader = withOperators(OperatorSet.EMPTY)

        /** Creates a reader with the standard operator set. */
        @JvmStatic
        fun withStandardOperators(): ClausesReader = withOperators(OperatorSet.STANDARD)

        /** Creates a reader with the library's default operator set. */
        @JvmStatic
        fun withDefaultOperators(): ClausesReader = withOperators(OperatorSet.DEFAULT)

        /** Creates a reader using [operators] as its initial operator set. */
        @JvmStatic
        fun withOperators(operators: OperatorSet): ClausesReader = ClausesReaderImpl(operators)

        /** Creates a reader from individual initial [operators]. */
        @JvmStatic
        fun withOperators(vararg operators: Operator): ClausesReader = withOperators(OperatorSet(*operators))
    }
}
