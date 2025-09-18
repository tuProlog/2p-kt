import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.PrologExpressionVisitor
import it.unibo.tuprolog.core.parsing.PrologParserFactory
import it.unibo.tuprolog.core.parsing.toClause
import it.unibo.tuprolog.theory.parsing.ClausesReader
import java.io.InputStream
import java.io.Reader

internal class ClausesReaderImpl(
    override val defaultOperatorSet: OperatorSet,
) : ClausesReader {
    override fun readClausesLazily(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Sequence<Clause> =
        PrologParserFactory
            .parseClauses(inputStream, operators)
            .asSequence()
            .map { it.accept(PrologExpressionVisitor()) }
            .map { it.toClause() }

    override fun readClausesLazily(
        reader: Reader,
        operators: OperatorSet,
    ): Sequence<Clause> =
        PrologParserFactory
            .parseClauses(reader, operators)
            .asSequence()
            .map { it.accept(PrologExpressionVisitor()) }
            .map { it.toClause() }
}
