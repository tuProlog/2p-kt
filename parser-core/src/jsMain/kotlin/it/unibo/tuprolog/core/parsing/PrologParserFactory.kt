package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.*

object PrologParserFactory {

    private fun newErrorListener(whileParsing: Any): ErrorListener {
        return object : ErrorListener() {
            private fun symbolToString(obj: dynamic): String {
                return if (obj is Token) {
                    obj.text
                } else {
                    obj.toString()
                }
            }

            override fun syntaxError(
                recognizer: dynamic,
                offendingSymbol: dynamic,
                line: Int,
                column: Int,
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
                    column + 1,
                    msg,
                    e
                )
            }
        }
    }

    fun parseExpression(string: String): SingletonExpressionContext =
        parseExpression(string, OperatorSet.EMPTY)

    fun parseExpression(string: String, withOperators: OperatorSet): SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseExpression(parser, string)
    }

    private fun parseExpression(parserAndErrorStrategy: Pair<PrologParser, ErrorStrategy>, source: String): SingletonExpressionContext {
        var mark = -1
        var index = -1
        val parser = parserAndErrorStrategy.first
        return try {
            mark = parser.getTokenStream().mark()
            index = parser.getTokenStream().index.coerceAtLeast(0)
            parser.singletonExpression()
        } catch (e: dynamic) {
            when {
                isParseCancellationException(e) && parser._interp.predictionMode === PredictionMode.SLL -> {
                    parser.getTokenStream().seek(index)
                    parser._interp.predictionMode = PredictionMode.LL
                    parser._errHandler = parserAndErrorStrategy.second
                    parser.addErrorListener(newErrorListener(source))
                    parseExpression(parserAndErrorStrategy, source)
                }
                e.clause !== null && isRecognitionException(e.cause) -> {
                    throw e.cause as RecognitionException
                }
                else -> {
                    throw e as Throwable
                }
            }
        } finally {
            parser.getTokenStream().release(mark)
        }
    }


    fun parseExpressionWithStandardOperators(string: String): SingletonExpressionContext =
        parseExpression(string, OperatorSet.DEFAULT)

    fun parseClauses(source: String, withOperators: OperatorSet): Sequence<ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser, source)
    }


    fun parseClauses(source: String): Sequence<ClauseContext> =
        parseClauses(source, OperatorSet.EMPTY)

    fun parseClausesWithStandardOperators(source: String): Sequence<ClauseContext> =
        parseClauses(source, OperatorSet.DEFAULT)

    fun createParser(string: String): Pair<PrologParser, ErrorStrategy> =
        createParser(string, OperatorSet.DEFAULT)

    fun createParser(source: String, operators: OperatorSet): Pair<PrologParser, ErrorStrategy> {
        val stream = InputStream(source)
        val lexer = PrologLexer(stream)
        lexer.removeErrorListeners()
        val tokenStream = CommonTokenStream(lexer)
        val parser = PrologParser(tokenStream)
        parser.removeErrorListeners()
        val originalErrorStrategy = parser._errHandler
        parser._errHandler = BailErrorStrategy()
        parser._interp.predictionMode = PredictionMode.SLL
        return addOperators(parser, operators) to originalErrorStrategy
    }


    fun addOperators(prologParser: PrologParser, operators: OperatorSet): PrologParser {
        val ops = mutableListOf<String>()
        for (it in operators) {
            val op = when (it.specifier.name.toUpperCase()) {
                "FX" -> Associativity.FX
                "FY" -> Associativity.FY
                "YF" -> Associativity.YF
                "YFX" -> Associativity.YFX
                "XFY" -> Associativity.XFY
                "XF" -> Associativity.XF
                "XFX" -> Associativity.XFX
                else -> Associativity.YFX
            }
            ops.add(it.functor)
            prologParser.addOperator(it.functor, op, it.priority)
        }
        return prologParser
    }

    private fun parseClause(parserAndErrorStrategy: Pair<PrologParser, ErrorStrategy>, source: Any): OptClauseContext {
        var mark = -1
        var index = -1
        val parser = parserAndErrorStrategy.first
        return try {
            mark = parser.getTokenStream().mark()
            index = parser.getTokenStream().index.coerceAtLeast(0)
            parser.optClause()
        } catch (e: dynamic) {
            when {
                isParseCancellationException(e) && parser._interp.predictionMode === PredictionMode.SLL -> {
                    parser.getTokenStream().seek(index)
                    parser._interp.predictionMode = PredictionMode.LL
                    parser._errHandler = parserAndErrorStrategy.second
                    parser.addErrorListener(newErrorListener(source))
                    parseClause(parserAndErrorStrategy, source)
                }
                e.clause !== null && isRecognitionException(e.cause) -> {
                    throw e.cause as RecognitionException
                }
                else -> {
                    throw e as Throwable
                }
            }
        } finally {
            parser.getTokenStream().release(mark)
        }
    }

    private fun parseClauses(parser: Pair<PrologParser, ErrorStrategy>, source: Any): Sequence<ClauseContext> {
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