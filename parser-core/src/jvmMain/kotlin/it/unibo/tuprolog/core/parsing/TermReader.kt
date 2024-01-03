package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import java.io.InputStream
import java.io.Reader
import java.io.StringReader

interface TermReader {
    val scope: Scope

    val defaultOperatorSet: OperatorSet

    fun readTerm(
        reader: Reader,
        operators: OperatorSet,
    ): Term?

    fun readTerm(reader: Reader): Term? = readTerm(reader, defaultOperatorSet)

    fun readTerm(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Term?

    fun readTerm(inputStream: InputStream): Term? = readTerm(inputStream, defaultOperatorSet)

    fun readTerms(
        reader: Reader,
        operators: OperatorSet,
    ): Sequence<Term>

    fun readTerms(reader: Reader): Sequence<Term> = readTerms(reader, defaultOperatorSet)

    fun readTerms(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Sequence<Term>

    fun readTerms(inputStream: InputStream): Sequence<Term> = readTerms(inputStream, defaultOperatorSet)

    fun readTerms(
        string: String,
        operators: OperatorSet,
    ): Sequence<Term> = readTerms(StringReader(string), operators)

    fun readTerms(string: String): Sequence<Term> = readTerms(string, defaultOperatorSet)

    companion object {
        @JvmStatic
        @JvmOverloads
        fun withNoOperator(scope: Scope = Scope.empty()): TermReader = withOperators(OperatorSet.EMPTY, scope)

        @JvmStatic
        @JvmOverloads
        fun withStandardOperators(scope: Scope = Scope.empty()): TermReader = withOperators(OperatorSet.STANDARD, scope)

        @JvmStatic
        @JvmOverloads
        fun withDefaultOperators(scope: Scope = Scope.empty()): TermReader = withOperators(OperatorSet.DEFAULT, scope)

        @JvmStatic
        @JvmOverloads
        fun withOperators(
            operators: OperatorSet,
            scope: Scope = Scope.empty(),
        ): TermReader = TermReaderImpl(scope, operators)

        @JvmStatic
        @JvmOverloads
        fun withOperators(
            vararg operators: Operator,
            scope: Scope = Scope.empty(),
        ): TermReader = withOperators(OperatorSet(*operators), scope)
    }
}
