package it.unibo.tuprolog.core.parsing


import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.*


internal object PrologParserFactoryImpl : PrologParserFactory {

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

    //EXPRESSION
    override fun parseExpression(string: String): SingletonExpressionContext =
        parseExpression(string, OperatorSet.EMPTY)

    override fun parseExpression(string: String, withOperators: OperatorSet): SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseExpression(parser, string)
    }

    private fun parseExpression(parser: PrologParser, source: String): SingletonExpressionContext {
        return try {
            parser.singletonExpression()
        } catch (ex: ParseCancellationException) {
            when {
                parser._interp.predictionMode === PredictionMode.SLL -> {
                    parser.tokenStream.seek(0)
                    parser._interp.predictionMode = PredictionMode.LL
                    parser._errHandler = DefaultErrorStrategy()
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


    override fun parseExpressionWithStandardOperators(string: String): SingletonExpressionContext =
        parseExpression(string, OperatorSet.DEFAULT)


    // TERM
    override fun parseTerm(string: String): SingletonTermContext =
        parseTerm(string, OperatorSet.EMPTY)

    override fun parseTerm(string: String, withOperators: OperatorSet): SingletonTermContext {
        val parser = createParser(string, withOperators)
        return parseTerm(parser)
    }

    override fun parseTermWithStandardOperators(string: String): SingletonTermContext =
        parseTerm(string, OperatorSet.DEFAULT)

    override fun parseClauses(source: String, withOperators: OperatorSet): List<ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser)
    }

    override fun parseClauses(source: String): List<ClauseContext> =
        parseClauses(source, OperatorSet.EMPTY)


    override fun parseClausesWithStandardOperators(source: String): List<ClauseContext> =
        parseClauses(source, OperatorSet.DEFAULT)

    override fun createParser(string: String): PrologParser =
        createParser(string, OperatorSet.DEFAULT)

    override fun createParser(source: String, operators: OperatorSet): PrologParser {
        val stream = InputStream(source)
        val lexer = PrologLexer(stream)
        lexer.removeErrorListeners()
        val tokenStream = CommonTokenStream(lexer)
        val parser = PrologParser(tokenStream)
        parser.removeErrorListeners()
        parser._errHandler = BailErrorStrategy()
        parser._interp.predictionMode = PredictionMode.SLL
        return addOperators(parser, operators)
    }

    private fun addOperators(prologParser: PrologParser, operators: OperatorSet): PrologParser {
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


    private fun parseTerm(parser: PrologParser): SingletonTermContext {
        return parser.singletonTerm()
    }

    private fun parseClause(parser: PrologParser): OptClauseContext {
        return parser.optClause()
    }

    private fun parseClauses(parser: PrologParser): List<ClauseContext> {
        return generateSequence(0) { it + 1 }
            .map {
                try {
                    parseClause(parser)
                } catch (e: ParseException) {
                    e.clauseIndex = it
                    throw e
                }
            }.takeWhile { !it.isOver }
            .map { it.clause() }
            .toList()
    }

}