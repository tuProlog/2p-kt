package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import org.antlr.v4.runtime.ANTLRErrorListener
import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.DefaultErrorStrategy
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.misc.ParseCancellationException
import java.io.InputStream
import java.io.Reader

@Suppress("TooManyFunctions")
object PrologParserFactory {
    private fun newErrorListener(whileParsing: Any): ANTLRErrorListener =
        object : BaseErrorListener() {
            private fun symbolToString(obj: Any): String =
                if (obj is Token) {
                    obj.text
                } else {
                    obj.toString()
                }

            override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any,
                line: Int,
                charPositionInLine: Int,
                msg: String,
                e: RecognitionException?,
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
                    e,
                )
            }
        }

    fun parseSingletonExpr(
        string: String,
        withOperators: OperatorSet,
    ): PrologParser.SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseSingletonExpr(parser, string)
    }

    fun parseSingletonExpr(
        string: Reader,
        withOperators: OperatorSet,
    ): PrologParser.SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseSingletonExpr(parser, string)
    }

    fun parseSingletonExpr(
        string: InputStream,
        withOperators: OperatorSet,
    ): PrologParser.SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseSingletonExpr(parser, string)
    }

    fun parseSingletonTerm(
        string: String,
        withOperators: OperatorSet,
    ): PrologParser.SingletonTermContext {
        val parser = createParser(string, withOperators)
        return parseSingletonTerm(parser, string)
    }

    fun parseSingletonTerm(
        string: Reader,
        withOperators: OperatorSet,
    ): PrologParser.SingletonTermContext {
        val parser = createParser(string, withOperators)
        return parseSingletonTerm(parser, string)
    }

    fun parseSingletonTerm(
        string: InputStream,
        withOperators: OperatorSet,
    ): PrologParser.SingletonTermContext {
        val parser = createParser(string, withOperators)
        return parseSingletonTerm(parser, string)
    }

    private fun parseSingletonExpr(
        parser: PrologParser,
        source: Any,
    ): PrologParser.SingletonExpressionContext = parseSingle(parser, source) { singletonExpression() }

    fun parseClauses(
        source: String,
        withOperators: OperatorSet,
    ): Sequence<PrologParser.ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser, source)
    }

    fun parseClauses(
        source: Reader,
        withOperators: OperatorSet,
    ): Sequence<PrologParser.ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser, source)
    }

    fun parseClauses(
        source: InputStream,
        withOperators: OperatorSet,
    ): Sequence<PrologParser.ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser, source)
    }

    fun parseExpressions(
        source: String,
        withOperators: OperatorSet,
    ): Sequence<PrologParser.ExpressionContext> {
        val parser = createParser(source, withOperators)
        return parseExpressions(parser, source)
    }

    fun parseExpressions(
        source: Reader,
        withOperators: OperatorSet,
    ): Sequence<PrologParser.ExpressionContext> {
        val parser = createParser(source, withOperators)
        return parseExpressions(parser, source)
    }

    fun parseExpressions(
        source: InputStream,
        withOperators: OperatorSet,
    ): Sequence<PrologParser.ExpressionContext> {
        val parser = createParser(source, withOperators)
        return parseExpressions(parser, source)
    }

    private fun createParser(string: String): PrologParser = createParser(string, CharStreams::fromString)

    private fun createParser(source: Reader): PrologParser = createParser(source, CharStreams::fromReader)

    private fun createParser(source: InputStream): PrologParser = createParser(source, CharStreams::fromStream)

    private fun createParser(
        source: String,
        operators: OperatorSet,
    ): PrologParser = addOperators(createParser(source), operators)

    private fun createParser(
        source: Reader,
        operators: OperatorSet,
    ): PrologParser = addOperators(createParser(source), operators)

    private fun createParser(
        source: InputStream,
        operators: OperatorSet,
    ): PrologParser = addOperators(createParser(source), operators)

    private fun addOperators(
        prologParser: PrologParser,
        operators: OperatorSet,
    ): PrologParser {
        operators.forEach {
            prologParser.addOperator(it.functor, it.specifier.toAssociativity(), it.priority)
        }
        prologParser.addParseListener(DynamicOpListener.of(prologParser))
        return prologParser
    }

    private fun <T> createParser(
        source: T,
        charListGenerator: (T) -> CharStream,
    ): PrologParser {
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

    @Suppress("SwallowedException")
    private fun <T : ParserRuleContext> parseSingle(
        parser: PrologParser,
        source: Any,
        rule: PrologParser.() -> T,
    ): T =
        try {
            parser.rule()
        } catch (ex: ParseCancellationException) {
            when {
                parser.interpreter.predictionMode === PredictionMode.SLL -> {
                    parser.tokenStream.seek(0)
                    parser.interpreter.predictionMode = PredictionMode.LL
                    parser.errorHandler = DefaultErrorStrategy()
                    parser.addErrorListener(newErrorListener(source))
                    parseSingle(parser, source, rule)
                }
                ex.cause is RecognitionException -> {
                    throw ex.cause as RecognitionException
                }
                else -> {
                    throw ex
                }
            }
        }

    private fun parseSingletonTerm(
        parser: PrologParser,
        source: Any,
    ): PrologParser.SingletonTermContext = parseSingle(parser, source) { singletonTerm() }

    @Suppress("SwallowedException", "ThrowsCount")
    private fun <T : ParserRuleContext> parseNext(
        parser: PrologParser,
        input: Any,
        rule: PrologParser.() -> T,
    ): T {
        var mark = -1
        var index = -1
        return try {
            mark = parser.tokenStream.mark()
            index = parser.tokenStream.index().coerceAtLeast(0)
            val result = parser.rule()
            result
        } catch (ex: ParseCancellationException) {
            when {
                parser.interpreter.predictionMode === PredictionMode.SLL -> {
                    parser.tokenStream.seek(index)
                    parser.interpreter.predictionMode = PredictionMode.LL
                    parser.errorHandler = DefaultErrorStrategy()
                    parser.addErrorListener(newErrorListener(input))
                    parseNext(parser, input, rule)
                }
                ex.cause is RecognitionException -> throw ex.cause as RecognitionException
                else -> throw ex
            }
        } catch (e: ParseException) {
            parser.tokenStream.let {
                if (it[it.index()].type != Token.EOF) {
                    it.consume()
                }
            }
            throw e
        } finally {
            parser.tokenStream.release(mark)
        }
    }

    private fun parseNextClause(
        parser: PrologParser,
        input: Any,
    ): PrologParser.OptClauseContext = parseNext(parser, input) { optClause() }

    private fun parseClauses(
        parser: PrologParser,
        source: Any,
    ): Sequence<PrologParser.ClauseContext> =
        generateSequence(0) { it + 1 }
            .map {
                try {
                    parseNextClause(parser, source)
                } catch (e: ParseException) {
                    e.clauseIndex = it
                    throw e
                }
            }.takeWhile { !it.isOver }
            .map { it.clause() }
            .filterNotNull()

    private fun parseNextExpression(
        parser: PrologParser,
        input: Any,
    ): PrologParser.OptExpressionContext = parseNext(parser, input) { optExpression() }

    private fun parseExpressions(
        parser: PrologParser,
        source: Any,
    ): Sequence<PrologParser.ExpressionContext> =
        generateSequence(0) { it + 1 }
            .map {
                try {
                    parseNextExpression(parser, source)
                } catch (e: ParseException) {
                    e.clauseIndex = it
                    throw e
                }
            }.takeWhile { !it.isOver }
            .map { it.expression() }
            .filterNotNull()
}
