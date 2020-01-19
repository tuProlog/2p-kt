package it.unibo.tuprolog.core.parsing


import com.sun.javafx.collections.ObservableListWrapper
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.dynamic.Associativity
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.misc.ParseCancellationException
import java.io.InputStream
import java.io.Reader

class PrologParserFactoryImpl private constructor(): PrologParserFactory {

    private object GetInstance{
        val INSTANCE: PrologParserFactoryImpl = PrologParserFactoryImpl()
    }

    companion object {
        val instance: PrologParserFactoryImpl by lazy{
            GetInstance.INSTANCE
        }

        private fun newErrorListener(whileParsing: Any): ANTLRErrorListener? {
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
    }



    //EXPRESSION
    override fun parseExpression(string: String): PrologParser.SingletonExpressionContext =
        parseExpression(string,OperatorSet.EMPTY)

    override fun parseExpression(string: String, withOperators: OperatorSet): PrologParser.SingletonExpressionContext {
        val parser = createParser(string,withOperators)
        return parseExpression(parser,string)
    }

    override fun parseExpression(string: Reader): PrologParser.SingletonExpressionContext =
        parseExpression(string,OperatorSet.EMPTY)

    override fun parseExpression(string: Reader, withOperators: OperatorSet): PrologParser.SingletonExpressionContext {
        val parser = createParser(string,withOperators)
        return parseExpression(parser,string)
    }

    override fun parseExpression(string: InputStream): PrologParser.SingletonExpressionContext =
        parseExpression(string,OperatorSet.EMPTY)

    override fun parseExpression(string: InputStream, withOperators: OperatorSet): PrologParser.SingletonExpressionContext {
        val parser = createParser(string,withOperators)
        return parseExpression(parser,string)
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
                    throw (ex.cause as RecognitionException?)!!
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    override fun parseExpressionWithStandardOperators(string: String): PrologParser.SingletonExpressionContext =
        parseExpression(string,OperatorSet.DEFAULT)

    override fun parseExpressionWithStandardOperators(string: Reader): PrologParser.SingletonExpressionContext =
        parseExpression(string,OperatorSet.DEFAULT)

    override fun parseExpressionWithStandardOperators(string: InputStream): PrologParser.SingletonExpressionContext =
        parseExpression(string,OperatorSet.DEFAULT)


    // TERM
    override fun parseTerm(string: String): PrologParser.SingletonTermContext =
        parseTerm(string,OperatorSet.EMPTY)

    override fun parseTerm(string: String, withOperators: OperatorSet): PrologParser.SingletonTermContext {
        val parser = createParser(string,withOperators)
        return parseTerm(parser,string)
    }

    override fun parseTerm(string: Reader): PrologParser.SingletonTermContext =
        parseTerm(string,OperatorSet.EMPTY)

    override fun parseTerm(string: Reader, withOperators: OperatorSet): PrologParser.SingletonTermContext {
        val parser = createParser(string,withOperators)
        return parseTerm(parser,string)
    }

    override fun parseTerm(string: InputStream): PrologParser.SingletonTermContext =
        parseTerm(string,OperatorSet.EMPTY)

    override fun parseTerm(string: InputStream, withOperators: OperatorSet): PrologParser.SingletonTermContext {
        val parser = createParser(string,withOperators)
        return parseTerm(parser,string)
    }

    override fun parseTermWithStandardOperators(string: String): PrologParser.SingletonTermContext =
        parseTerm(string,OperatorSet.DEFAULT)

    override fun parseTermWithStandardOperators(string: Reader): PrologParser.SingletonTermContext =
        parseTerm(string,OperatorSet.DEFAULT)

    override fun parseTermWithStandardOperators(string: InputStream): PrologParser.SingletonTermContext =
        parseTerm(string,OperatorSet.DEFAULT)


    //CLAUSES
    override fun parseClauses(source: String, withOperators: OperatorSet): List<PrologParser.ClauseContext> {
        val parser = createParser(source,withOperators)
        return parseClauses(parser,source)
    }

    override fun parseClauses(source: Reader, withOperators: OperatorSet): List<PrologParser.ClauseContext> {
        val parser = createParser(source,withOperators)
        return parseClauses(parser,source)
    }

    override fun parseClauses(source: InputStream, withOperators: OperatorSet): List<PrologParser.ClauseContext> {
        val parser = createParser(source,withOperators)
        return parseClauses(parser,source)
    }

    override fun parseClauses(source: String): List<PrologParser.ClauseContext> =
        parseClauses(source,OperatorSet.EMPTY)

    override fun parseClauses(source: Reader): List<PrologParser.ClauseContext> =
        parseClauses(source,OperatorSet.EMPTY)

    override fun parseClauses(source: InputStream): List<PrologParser.ClauseContext>  =
        parseClauses(source,OperatorSet.EMPTY)

    override fun parseClausesWithStandardOperators(source: String): List<PrologParser.ClauseContext>  =
        parseClauses(source,OperatorSet.DEFAULT)

    override fun parseClausesWithStandardOperators(source: Reader): List<PrologParser.ClauseContext>  =
        parseClauses(source,OperatorSet.DEFAULT)

    override fun parseClausesWithStandardOperators(source: InputStream): List<PrologParser.ClauseContext>  =
        parseClauses(source,OperatorSet.DEFAULT)


    //CREATE PARSER
    override fun createParser(string: String): PrologParser =
        createParser(string,CharStreams::fromString)

    override fun createParser(source: Reader): PrologParser =
        createParser(source,CharStreams::fromReader)

    override fun createParser(source: InputStream): PrologParser =
        createParser(source, CharStreams::fromStream)


    override fun createParser(source: String, operators: OperatorSet): PrologParser =
        addOperators(createParser(source),operators)

    override fun createParser(source: Reader, operators: OperatorSet): PrologParser =
        addOperators(createParser(source),operators)

    override fun createParser(source: InputStream, operators: OperatorSet): PrologParser =
        addOperators(createParser(source),operators)


    //PRIVATE FUNCTIONS
   private fun addOperators(prologParser: PrologParser,operators : OperatorSet): PrologParser{
        operators.forEach{
            prologParser.addOperator(it.functor,Associativity.valueOf(it.specifier.name),it.priority)
        }
        prologParser.addParseListener(DynamicOpListener.of(prologParser,OperatorSet()::plus))
        return prologParser
    }

    private fun <T> createParser(source: T, charListGenerator : (T) -> CharStream): PrologParser{
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
                    throw (ex.cause as RecognitionException?)!!
                }
                else -> {
                    throw ex
                }
            }
        } finally {
            parser.tokenStream.release(mark)
        }
    }

    private fun parseClauses(parser: PrologParser, source: Any):  List<PrologParser.ClauseContext> {
        val optClauses: MutableList<PrologParser.OptClauseContext> = listOf<PrologParser.OptClauseContext>().toMutableList()
        generateSequence(0 ){it+1}.forEach{
            try{
                optClauses.add(parseClause(parser,source))
            }catch (e: ParseException){
                e.clauseIndex = it
                throw e
            }
        }
        return ObservableListWrapper(optClauses).takeWhile{
            !it.isOver
        }.map(PrologParser.OptClauseContext::clause)
    }

}