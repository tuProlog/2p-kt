package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.PrologParser.*
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.util.stream.Stream

interface  PrologParserFactory  {
    //Singleton
    companion object {
        val instance = PrologParserFactoryImpl.instance
    }

    fun parseExpression(string: String): SingletonExpressionContext

    fun parseExpression(string: String, withOperators: OperatorSet): SingletonExpressionContext

    fun parseExpressionWithStandardOperators(string: String): SingletonExpressionContext

    @Throws(IOException::class)
    fun parseExpression(string: Reader): SingletonExpressionContext

    @Throws(IOException::class)
    fun parseExpression(string: Reader, withOperators: OperatorSet): SingletonExpressionContext

    @Throws(IOException::class)
    fun parseExpressionWithStandardOperators(string: Reader): SingletonExpressionContext

    @Throws(IOException::class)
    fun parseExpression(string: InputStream): SingletonExpressionContext

    @Throws(IOException::class)
    fun parseExpression(string: InputStream, withOperators: OperatorSet): SingletonExpressionContext

    @Throws(IOException::class)
    fun parseExpressionWithStandardOperators(string: InputStream): SingletonExpressionContext

    fun parseTerm(string: String): SingletonTermContext

    fun parseTerm(string: String, withOperators: OperatorSet): SingletonTermContext

    fun parseTermWithStandardOperators(string: String): SingletonTermContext

    @Throws(IOException::class)
    fun parseTerm(string: Reader): SingletonTermContext

    @Throws(IOException::class)
    fun parseTerm(string: Reader, withOperators: OperatorSet): SingletonTermContext

    @Throws(IOException::class)
    fun parseTermWithStandardOperators(string: Reader): SingletonTermContext

    @Throws(IOException::class)
    fun parseTerm(string: InputStream): SingletonTermContext

    @Throws(IOException::class)
    fun parseTerm(string: InputStream, withOperators: OperatorSet): SingletonTermContext

    @Throws(IOException::class)
    fun parseTermWithStandardOperators(string: InputStream): SingletonTermContext

    fun parseClauses(
        source: String,
        withOperators: OperatorSet
    ): Stream<ClauseContext>

    @Throws(IOException::class)
    fun parseClauses(
        source: Reader,
        withOperators: OperatorSet
    ): Stream<ClauseContext>

    @Throws(IOException::class)
    fun parseClauses(
        source: InputStream,
        withOperators: OperatorSet
    ): Stream<ClauseContext>

    fun parseClauses(source: String): Stream<ClauseContext>

    @Throws(IOException::class)
    fun parseClauses(source: Reader): Stream<ClauseContext>

    @Throws(IOException::class)
    fun parseClauses(source: InputStream): Stream<ClauseContext>

    fun parseClausesWithStandardOperators(source: String): Stream<ClauseContext>

    @Throws(IOException::class)
    fun parseClausesWithStandardOperators(source: Reader): Stream<ClauseContext>

    @Throws(IOException::class)
    fun parseClausesWithStandardOperators(source: InputStream): Stream<ClauseContext>

    fun createParser(string: String): PrologParser

    @Throws(IOException::class)
    fun createParser(source: Reader): PrologParser

    @Throws(IOException::class)
    fun createParser(source: InputStream): PrologParser

    fun createParser(source: String, operators: OperatorSet): PrologParser

    @Throws(IOException::class)
    fun createParser(source: Reader, operators: OperatorSet): PrologParser

    @Throws(IOException::class)
    fun createParser(source: InputStream, operators: OperatorSet): PrologParser


}