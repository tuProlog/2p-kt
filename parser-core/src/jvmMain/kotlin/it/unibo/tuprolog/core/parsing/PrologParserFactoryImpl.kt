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
import java.util.stream.Stream

class PrologParserFactoryImpl private constructor(): PrologParserFactory {

    private object GetInstance{
        val INSTANCE = PrologParserFactoryImpl()
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

    override fun parseExpression(
        string: String,
        withOperators: OperatorSet
    ): PrologParser.SingletonExpressionContext {
        val parser = createParser(string,withOperators)
        return parseExpression(parser,string)
    }

    override fun parseExpression(string: Reader): PrologParser.SingletonExpressionContext =
        parseExpression(string,OperatorSet.EMPTY)

    override fun parseExpression(
        string: Reader,
        withOperators: OperatorSet
    ): PrologParser.SingletonExpressionContext {
        val parser = createParser(string,withOperators)
        return parseExpression(parser,string)
    }

    override fun parseExpression(string: InputStream): PrologParser.SingletonExpressionContext =
        parseExpression(string,OperatorSet.EMPTY)

    override fun parseExpression(
        string: InputStream,
        withOperators: OperatorSet
    ): PrologParser.SingletonExpressionContext {
        val parser = createParser(string,withOperators)
        return parseExpression(parser,string)
    }

    private fun parseExpression(parser: PrologParser, source: Any): PrologParser.SingletonExpressionContext {
        return try {
            parser.singletonExpression()
        } catch (ex: ParseCancellationException) {
            if (parser.interpreter.predictionMode === PredictionMode.SLL) {
                parser.tokenStream.seek(0)
                parser.interpreter.predictionMode = PredictionMode.LL
                parser.errorHandler = DefaultErrorStrategy()
                parser.addErrorListener(PrologParserFactoryImpl.newErrorListener(source))
                parseExpression(parser, source)
            } else if (ex.cause is RecognitionException) {
                throw (ex.cause as RecognitionException?)!!
            } else {
                throw ex
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

    override fun parseTermWithStandardOperators(string: String): PrologParser.SingletonTermContext {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseTermWithStandardOperators(string: Reader): PrologParser.SingletonTermContext {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseTermWithStandardOperators(string: InputStream): PrologParser.SingletonTermContext {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //CLAUSES
    override fun parseClauses(source: String, withOperators: OperatorSet): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseClauses(source: Reader, withOperators: OperatorSet): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseClauses(source: InputStream, withOperators: OperatorSet): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseClauses(source: String): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseClauses(source: Reader): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseClauses(source: InputStream): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseClausesWithStandardOperators(source: String): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseClausesWithStandardOperators(source: Reader): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseClausesWithStandardOperators(source: InputStream): Stream<PrologParser.ClauseContext> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


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

    private fun <T> createParser(source: T, charStreamGenerator : (T) -> CharStream): PrologParser{
        val stream: CharStream = charStreamGenerator(source)
        val lexer = PrologLexer(stream)
        lexer.removeErrorListeners()
        val tokenStream: TokenStream = BufferedTokenStream(lexer)
        val parser = PrologParser(tokenStream)
        parser.removeErrorListeners()
        parser.errorHandler = BailErrorStrategy()
        parser.interpreter.predictionMode = PredictionMode.SLL
        return parser
    }

    private fun parseTerm(parser: PrologParser, source: Any): PrologParser.SingletonTermContext {
        return try {
            parser.singletonTerm()
        } catch (ex: ParseCancellationException) {
            if (parser.interpreter.predictionMode === PredictionMode.SLL) {
                parser.tokenStream.seek(0)
                parser.interpreter.predictionMode = PredictionMode.LL
                parser.errorHandler = DefaultErrorStrategy()
                parser.addErrorListener(newErrorListener(source))
                parseTerm(parser, source)
            } else if (ex.cause is RecognitionException) {
                throw (ex.cause as RecognitionException?)!!
            } else {
                throw ex
            }
        }
    }

}