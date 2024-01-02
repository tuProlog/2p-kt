package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import java.io.InputStream
import java.io.Reader

class TermReaderImpl(
    override val scope: Scope,
    override val defaultOperatorSet: OperatorSet,
) : TermReader {
    override fun readTerm(
        reader: Reader,
        operators: OperatorSet,
    ): Term? = readTerms(reader, operators).firstOrNull()

    override fun readTerm(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Term? = readTerms(inputStream, operators).firstOrNull()

    override fun readTerms(
        reader: Reader,
        operators: OperatorSet,
    ): Sequence<Term> =
        PrologParserFactory.parseExpressions(reader, operators).map {
            it.accept(PrologExpressionVisitor(scope))
        }

    override fun readTerms(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Sequence<Term> =
        PrologParserFactory.parseExpressions(inputStream, operators).map {
            it.accept(PrologExpressionVisitor(scope))
        }
}
