package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.Theory
import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import kotlin.jvm.JvmStatic

interface ClausesReader {
    val defaultOperatorSet: OperatorSet

    fun readTheory(inputStream: InputStream, operators: OperatorSet): Theory =
        Theory.of(readClausesLazily(inputStream, operators))

    fun readTheory(reader: Reader, operators: OperatorSet): Theory =
        Theory.of(readClausesLazily(inputStream, operators))

    fun readTheory(inputStream: InputStream): Theory = readTheory(inputStream, defaultOperatorSet)

    fun readTheory(reader: Reader): Theory = readTheory(reader, defaultOperatorSet)

    fun readClausesLazily(inputStream: InputStream, operators: OperatorSet): Sequence<Clause>

    fun readClausesLazily(reader: Reader, operators: OperatorSet): Sequence<Clause>

    fun readClausesLazily(inputStream: InputStream): Sequence<Clause> =
        readClausesLazily(inputStream, defaultOperatorSet)

    fun readClausesLazily(reader: Reader): Sequence<Clause> = readClausesLazily(reader, defaultOperatorSet)

    fun readClauses(inputStream: InputStream, operators: OperatorSet): List<Clause> =
        readClausesLazily(inputStream, operators).toList()

    fun readClauses(reader: Reader, operators: OperatorSet): List<Clause> =
        readClausesLazily(inputStream).toList()

    fun readClauses(inputStream: InputStream): List<Clause> = readClauses(inputStream, defaultOperatorSet)

    fun readClauses(reader: Reader): List<Clause> = readClauses(reader, defaultOperatorSet)

    companion object {
        @JvmStatic
        val withNoOperator = withOperators()

        @JvmStatic
        val withStandardOperators = withOperators(OperatorSet.STANDARD)

        @JvmStatic
        val withDefaultOperators = withOperators(OperatorSet.DEFAULT)

        @JvmStatic
        fun withOperators(operators: OperatorSet) = TODO()

        @JvmStatic
        fun withOperators(vararg operators: Operator) = withOperators(OperatorSet(*operators))
    }
}
