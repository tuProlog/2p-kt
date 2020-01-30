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


    override fun createParser(source: String, operators: OperatorSet): PrologParser =
        addOperators(createParser(source), operators)


    //PRIVATE FUNCTIONS
    private fun addOperators(prologParser: PrologParser, operators: OperatorSet): PrologParser {
        operators.forEach {
            prologParser.addOperator(it.functor, Associativity.valueOf(it.specifier.name), it.priority)
        }
        return prologParser
    }


    private fun parseTerm(parser: PrologParser): SingletonTermContext {
        return parser.singletonTerm()
    }

    private fun parseClause(parser: PrologParser): OptClauseContext {
        var mark = -1
        var index = -1
        mark = parser.getTokenStream().mark()
        index = parser.getTokenStream().index.coerceAtLeast(0)
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