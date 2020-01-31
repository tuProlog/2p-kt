package it.unibo.tuprolog.core.parsing


import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.*


internal object PrologParserFactoryImpl : PrologParserFactory {


    //EXPRESSION
    override fun parseExpression(string: String): SingletonExpressionContext =
        parseExpression(string, OperatorSet.EMPTY)

    override fun parseExpression(string: String, withOperators: OperatorSet): SingletonExpressionContext {
        val parser = createParser(string, withOperators)
        return parseExpression(parser)
    }


    private fun parseExpression(parser: PrologParser): SingletonExpressionContext =
        parser.singletonExpression()


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


    //CLAUSES
    override fun parseClauses(source: String, withOperators: OperatorSet): List<ClauseContext> {
        val parser = createParser(source, withOperators)
        return parseClauses(parser)
    }


    override fun parseClauses(source: String): List<ClauseContext> =
        parseClauses(source, OperatorSet.EMPTY)


    override fun parseClausesWithStandardOperators(source: String): List<ClauseContext> =
        parseClauses(source, OperatorSet.DEFAULT)


    //CREATE PARSER
    override fun createParser(string: String): PrologParser =
        createParser(string, OperatorSet.DEFAULT)


    override fun createParser(source: String, operators: OperatorSet): PrologParser {
        val stream = InputStream(source)
        val lexer = PrologLexer(stream)
        val tokenStream = CommonTokenStream(lexer)
        return addOperators(PrologParser(tokenStream),operators)
    }



    //PRIVATE FUNCTIONS
    private fun addOperators(prologParser: PrologParser, operators: OperatorSet): PrologParser {
        var ops = mutableListOf<String>()
        var err = mutableListOf<Boolean>()
        operators.forEach {
            val op = when(it.specifier.name.toUpperCase()){
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
        ops.forEach{
            err.add(prologParser.isOperator(it))
        }
        //error(err.toString())
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