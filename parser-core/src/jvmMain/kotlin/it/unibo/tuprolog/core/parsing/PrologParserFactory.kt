package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.dynamic.Associativity
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.misc.ParseCancellationException
import java.io.InputStream
import java.io.Reader

object PrologParserFactory {

    private fun newErrorListener(whileParsing: Any): ANTLRErrorListener {
        return object : BaseErrorListener() {
            private fun symbolToString(obj: Any): String {
                return if (obj is Token) {
                    obj.text
                } else {
                    obj.toString()
                }
            }

            override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any,
                line: Int,
                charPositionInLine: Int,
                msg: String,
                e: RecognitionException
            ) {
                if (recognizer is PrologParser) {
                    recognizer.removeParseListeners()
                }
                throw ParseException(
                    whileParsing,
                    symbolToString(offendingSymbol),
                    line,
                    charPositionInLine + 1,
                    msg,
                    e
                )
            }
        }
    }

    fun parseExpression(string: String): PrologParser.SingletonExpressionContext =
        parseExpression(string, OperatorSet.EMPTY)

    fun parseExpression(string: String, withOperators: OperatorSet): PrologParser.SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseExpression(parser, string)
    }

    fun parseExpression(string: Reader): PrologParser.SingletonExpressionContext =
        parseExpression(string, OperatorSet.EMPTY)

    fun parseExpression(string: Reader, withOperators: OperatorSet): PrologParser.SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseExpression(parser, string)
    }

    fun parseExpression(string: InputStream): PrologParser.SingletonExpressionContext =
        parseExpression(string, OperatorSet.EMPTY)

    fun parseExpression(
        string: InputStream,
        withOperators: OperatorSet
    ): PrologParser.SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseExpression(parser, string)
    }

    private fun parseExpression(parser: PrologParser, source: Any): PrologParser.SingletonExpressionContext {
        return try {
            parser.singletonExpression()
        } catch (ex: ParseCancellationException) {
            when {
                parser.interpreter.predictionMode === PredictionMode.SLL -> {
                    parser.tokenStream.seek(0)
                    parser.interpreter.predictionMode = PredictionMode.LL
                    parser.errorHandler = DefaultErrorStrategy()
                    parser.addErrorListener(newErrorListener(source))
                    parseExpression(parser, source)
                }
                ex.cause is RecognitionException -> {
                    throw ex.cause as RecognitionException
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    fun parseExpressionWithStandardOperators(string: String): PrologParser.SingletonExpressionContext =
        parseExpression(string, OperatorSet.DEFAULT)

    fun parseExpressionWithStandardOperators(string: Reader): PrologParser.SingletonExpressionContext =
        parseExpression(string, OperatorSet.DEFAULT)

    fun parseExpressionWithStandardOperators(string: InputStream): PrologParser.SingletonExpressionContext =
        parseExpression(string, OperatorSet.DEFAULT)

    fun parseTerm(string: String): PrologParser.SingletonTermContext =
        parseTerm(string, OperatorSet.EMPTY)

    fun parseTerm(string: String, withOperators: OperatorSet): PrologParser.SingletonTermContext {
        val parser = createParser(string, withOperators)
        return parseTerm(parser, string)
    }

    fun parseTerm(string: Reader): PrologParser.SingletonTermContext =
        parseTerm(string, OperatorSet.EMPTY)

    fun parseTerm(string: Reader, withOperators: OperatorSet): PrologParser.SingletonTermContext {
        val parser = createParser(string, withOperators)
        return parseTerm(parser, string)
    }

    fun parseTerm(string: InputStream): PrologParser.SingletonTermContext =
        parseTerm(string, OperatorSet.EMPTY)

    fun parseTerm(string: InputStream, withOperators: OperatorSet): PrologParser.SingletonTermContext {
        val parser = createParser(string, withOperators)
        return parseTerm(parser, string)
    }

    fun parseTermWithStandardOperators(string: String): PrologParser.SingletonTermContext =
        parseTerm(string, OperatorSet.DEFAULT)

    fun parseTermWithStandardOperators(string: Reader): PrologParser.SingletonTermContext =
        parseTerm(string, OperatorSet.DEFAULT)

    fun parseTermWithStandardOperators(string: InputStream): PrologParser.SingletonTermContext =
        parseTerm(string, OperatorSet.DEFAULT)

    fun parseClauses(source: String, withOperators: OperatorSet): Sequence<PrologParser.ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser, source)
    }

    fun parseClauses(source: Reader, withOperators: OperatorSet): Sequence<PrologParser.ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser, source)
    }

    fun parseClauses(source: InputStream, withOperators: OperatorSet): Sequence<PrologParser.ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser, source)
    }

    fun parseClauses(source: String): Sequence<PrologParser.ClauseContext> =
        parseClauses(source, OperatorSet.EMPTY)

    fun parseClauses(source: Reader): Sequence<PrologParser.ClauseContext> =
        parseClauses(source, OperatorSet.EMPTY)

    fun parseClauses(source: InputStream): Sequence<PrologParser.ClauseContext> =
        parseClauses(source, OperatorSet.EMPTY)

    fun parseClausesWithStandardOperators(source: String): Sequence<PrologParser.ClauseContext> =
        parseClauses(source, OperatorSet.DEFAULT)

    fun parseClausesWithStandardOperators(source: Reader): Sequence<PrologParser.ClauseContext> =
        parseClauses(source, OperatorSet.DEFAULT)

    fun parseClausesWithStandardOperators(source: InputStream): Sequence<PrologParser.ClauseContext> =
        parseClauses(source, OperatorSet.DEFAULT)

    fun createParser(string: String): PrologParser =
        createParser(string, CharStreams::fromString)

    fun createParser(source: Reader): PrologParser =
        createParser(source, CharStreams::fromReader)

    fun createParser(source: InputStream): PrologParser =
        createParser(source, CharStreams::fromStream)

    fun createParser(source: String, operators: OperatorSet): PrologParser =
        addOperators(createParser(source), operators)

    fun createParser(source: Reader, operators: OperatorSet): PrologParser =
        addOperators(createParser(source), operators)

    fun createParser(source: InputStream, operators: OperatorSet): PrologParser =
        addOperators(createParser(source), operators)

    fun addOperators(prologParser: PrologParser, operators: OperatorSet): PrologParser {
        operators.forEach {
            prologParser.addOperator(it.functor, Associativity.valueOf(it.specifier.name), it.priority)
        }
//        prologParser.addParseListener(DynamicOpListener.of(prologParser, OperatorSet()::plus))
        return prologParser
    }

    private fun <T> createParser(source: T, charListGenerator: (T) -> CharStream): PrologParser {
        val stream: CharStream = charListGenerator(source)
        val lexer = PrologLexer(stream)
        lexer.removeErrorListeners()
        val tokenList: TokenStream = BufferedTokenStream(lexer)
        val parser = PrologParser(tokenList)
        parser.removeErrorListeners()
        parser.errorHandler = BailErrorStrategy()
        parser.interpreter.predictionMode = PredictionMode.SLL
        return parser
    }

    private fun parseTerm(parser: PrologParser, source: Any): PrologParser.SingletonTermContext {
        return try {
            parser.singletonTerm()
        } catch (ex: ParseCancellationException) {
            when {
                parser.interpreter.predictionMode === PredictionMode.SLL -> {
                    parser.tokenStream.seek(0)
                    parser.interpreter.predictionMode = PredictionMode.LL
                    parser.errorHandler = DefaultErrorStrategy()
                    parser.addErrorListener(newErrorListener(source))
                    parseTerm(parser, source)
                }
                ex.cause is RecognitionException -> {
                    throw (ex.cause as RecognitionException?)!!
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    private fun parseClause(parser: PrologParser, input: Any): PrologParser.OptClauseContext {
        var mark = -1
        var index = -1
        return try {
            mark = parser.tokenStream.mark()
            index = parser.tokenStream.index().coerceAtLeast(0)
            parser.optClause()
        } catch (ex: ParseCancellationException) {
            when {
                parser.interpreter.predictionMode === PredictionMode.SLL -> {
                    parser.tokenStream.seek(index)
                    parser.interpreter.predictionMode = PredictionMode.LL
                    parser.errorHandler = DefaultErrorStrategy()
                    parser.addErrorListener(newErrorListener(input))
                    parser.optClause()
                }
                ex.cause is RecognitionException -> {
                    throw ex.cause as RecognitionException
                }
                else -> {
                    throw ex
                }
            }
        } finally {
            parser.tokenStream.release(mark)
        }
    }

    private fun parseClauses(parser: PrologParser, source: Any): Sequence<PrologParser.ClauseContext> {
        return generateSequence(0) { it + 1 }
            .map {
                try {
                    parseClause(parser, source)
                } catch (e: ParseException) {
                    e.clauseIndex = it
                    throw e
                }
            }.takeWhile { !it.isOver }
            .map { it.clause() }
    }

}