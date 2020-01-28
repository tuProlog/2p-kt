package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.ClauseContext
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.SingletonExpressionContext
import it.unibo.tuprolog.parser.SingletonTermContext

interface PrologParserFactory {

    companion object {
        val instance: PrologParserFactory = PrologParserFactoryImpl

        // this operator method allows developers to create a new PrologParserFactory by simply writing `PrologParserFactory()`
        operator fun invoke(): PrologParserFactory = PrologParserFactoryImpl
    }

    fun parseExpression(string: String): SingletonExpressionContext

    fun parseExpression(string: String, withOperators: OperatorSet): SingletonExpressionContext

    fun parseExpressionWithStandardOperators(string: String): SingletonExpressionContext

    fun parseTerm(string: String): SingletonTermContext

    fun parseTerm(string: String, withOperators: OperatorSet): SingletonTermContext

    fun parseTermWithStandardOperators(string: String): SingletonTermContext

    fun parseClauses(source: String, withOperators: OperatorSet): List<ClauseContext>

    fun parseClauses(source: String): List<ClauseContext>

    fun parseClausesWithStandardOperators(source: String): List<ClauseContext>


    fun createParser(string: String): PrologParser


    fun createParser(source: String, operators: OperatorSet): PrologParser


}