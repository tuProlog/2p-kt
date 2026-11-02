package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.parser.exceptions.InvalidEscapeException
import it.unibo.tuprolog.parser.exceptions.MalformedNumericLiteralException
import it.unibo.tuprolog.parser.exceptions.PrologLexingException
import it.unibo.tuprolog.parser.exceptions.SyntaxErrorCode
import it.unibo.tuprolog.parser.exceptions.UnexpectedCharacterException
import it.unibo.tuprolog.parser.exceptions.UnterminatedBlockCommentException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenChannel
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tokens.TokenPayload
import kotlin.reflect.KClass

private class MalformedCase(
    val id: String,
    val text: String,
    val expected: KClass<out PrologLexingException>,
)

private val malformed =
    listOf(
        MalformedCase("character-code/without-a-character", "0'", MalformedNumericLiteralException::class),
        MalformedCase("character-code/with-a-line-break", "0'\n", MalformedNumericLiteralException::class),
        MalformedCase("character-code/with-a-tab", "0'\t", MalformedNumericLiteralException::class),
        MalformedCase("character-code/escape-at-end-of-input", "0'\\", InvalidEscapeException::class),
        MalformedCase("character-code/escape-beyond-a-code-unit", "0'\\x1F600\\", InvalidEscapeException::class),
        MalformedCase("escape/hexadecimal-without-digits", "'\\x\\'", InvalidEscapeException::class),
        MalformedCase("escape/numeric-without-closing-backslash", "'\\x41'", InvalidEscapeException::class),
        MalformedCase("escape/octal-without-closing-backslash", "'\\101'", InvalidEscapeException::class),
        MalformedCase("quoted/line-break-inside", "'a\nb'", UnterminatedQuotedLiteralException::class),
    )

private class EscapeCase(
    val id: String,
    val text: String,
    val decoded: String,
)

private val escapes =
    listOf(
        EscapeCase("alert", "'\\a'", "\u0007"),
        EscapeCase("backspace", "'\\b'", "\b"),
        EscapeCase("form-feed", "'\\f'", "\u000c"),
        EscapeCase("carriage-return", "'\\r'", "\r"),
        EscapeCase("tab", "'\\t'", "\t"),
        EscapeCase("vertical-tab", "'\\v'", "\u000b"),
        EscapeCase("backslash", "'\\\\'", "\\"),
        EscapeCase("single-quote", "'\\''", "'"),
        EscapeCase("double-quote", "'\\\"'", "\""),
        EscapeCase("back-quote", "'\\`'", "`"),
        EscapeCase("line-continuation/lf", "'a\\\nb'", "ab"),
        EscapeCase("line-continuation/crlf", "'a\\\r\nb'", "ab"),
        EscapeCase("upper-case-hexadecimal", "'\\X41\\'", "A"),
    )

class SourceAndLexerSpec :
    FunSpec({
        test("unsupported-characters-have-a-typed-error-and-exact-coordinates") {
            val error = shouldThrow<UnexpectedCharacterException> { lex("a\né").materialize() }
            error.code shouldBe SyntaxErrorCode.UNEXPECTED_CHARACTER
            error.offendingText shouldBe "é"
            error.span.start.line shouldBe 1
            error.span.start.column shouldBe 0
        }

        test("positions-treat-cr-lf-as-one-line-break") {
            val source = SourceText("a\r\nb\rc\nd")
            source.positionAt(0) shouldBe SourcePosition(0, 0, 0)
            source.positionAt(3) shouldBe SourcePosition(3, 1, 0)
            source.positionAt(5) shouldBe SourcePosition(5, 2, 0)
            source.positionAt(7) shouldBe SourcePosition(7, 3, 0)
        }

        test("token-slices-reconstruct-the-original-source") {
            val text = "foo(X) /* block */ :- X = 1.\n% tail\n"
            val source = lex(text)
            val reconstructed =
                source.tokens
                    .filter { it.kind != TokenKind.END_OF_INPUT }
                    .joinToString(separator = "") { source.textOf(it) }
            reconstructed shouldBe text
        }

        test("lexes-variables-and-atom-candidates-without-operator-state") {
            val tokens = lex("_ _X X foo foo_bar ++ ;").significantTokens().dropLast(1)
            tokens.map(Token::kind) shouldBe
                listOf(
                    TokenKind.VARIABLE,
                    TokenKind.VARIABLE,
                    TokenKind.VARIABLE,
                    TokenKind.WORD_ATOM,
                    TokenKind.WORD_ATOM,
                    TokenKind.GRAPHIC_ATOM,
                    TokenKind.GRAPHIC_ATOM,
                )
            (tokens[0].payload as TokenPayload.Name).value shouldBe "_"
        }

        test("lexes-all-numeric-families") {
            val tokens = lex("42 0xff 0O17 0b101 1.25 2.0e-3 0'a").significantTokens().dropLast(1)
            tokens.map(Token::kind) shouldBe
                listOf(
                    TokenKind.DECIMAL_INTEGER,
                    TokenKind.HEX_INTEGER,
                    TokenKind.OCTAL_INTEGER,
                    TokenKind.BINARY_INTEGER,
                    TokenKind.FLOAT,
                    TokenKind.FLOAT,
                    TokenKind.CHARACTER_CODE,
                )
            (tokens.last().payload as TokenPayload.CharacterCode).codePoint shouldBe 97
        }

        test("single-signs-are-dedicated-but-longer-graphic-sequences-are-atoms") {
            val tokens = lex("+ - +- -- ->").significantTokens().dropLast(1)
            tokens.map(Token::kind) shouldBe
                listOf(
                    TokenKind.SIGN,
                    TokenKind.SIGN,
                    TokenKind.GRAPHIC_ATOM,
                    TokenKind.GRAPHIC_ATOM,
                    TokenKind.GRAPHIC_ATOM,
                )
        }

        test("quoted-text-is-decoded-without-losing-raw-coordinates") {
            val source = lex("'I''m' \"a\\n\\x41\\\"")
            val tokens = source.significantTokens().dropLast(1)
            (tokens[0].payload as TokenPayload.QuotedText).decoded shouldBe "I'm"
            (tokens[1].payload as TokenPayload.QuotedText).decoded shouldBe "a\nA"
            source.textOf(tokens[0]) shouldBe "'I''m'"
            tokens[0].span.start.column shouldBe 0
            tokens[0].span.endExclusive.column shouldBe 6
        }

        test("octal-and-hexadecimal-escapes-are-decoded") {
            val tokens = lex("'\\101\\' '\\x41\\'").significantTokens().dropLast(1)
            (tokens[0].payload as TokenPayload.QuotedText).decoded shouldBe "A"
            (tokens[1].payload as TokenPayload.QuotedText).decoded shouldBe "A"
        }

        test("comments-and-whitespace-are-retained-as-trivia") {
            val tokens = lex("a % line\n /* block */ b").tokens
            (tokens.any { it.kind == TokenKind.LINE_COMMENT && it.channel == TokenChannel.TRIVIA }).shouldBeTrue()
            (tokens.any { it.kind == TokenKind.BLOCK_COMMENT && it.channel == TokenChannel.TRIVIA }).shouldBeTrue()
            (tokens.any { it.kind == TokenKind.WHITESPACE && it.channel == TokenChannel.TRIVIA }).shouldBeTrue()
            tokens.first().channel shouldNotBe TokenChannel.TRIVIA
        }

        test("a-period-is-a-full-stop-only-in-terminating-context") {
            val terminating = lex("a. b").significantTokens().dropLast(1)
            terminating[1].kind shouldBe TokenKind.FULL_STOP

            val embedded = lex("a.b").significantTokens().dropLast(1)
            embedded[1].kind shouldBe TokenKind.GRAPHIC_ATOM
            lex("a.b").textOf(embedded[1]) shouldBe "."
        }

        test("empty-delimiters-remain-separate-tokens") {
            val tokens = lex("[ ] {\n}").significantTokens().dropLast(1)
            tokens.map(Token::kind) shouldBe
                listOf(
                    TokenKind.LEFT_BRACKET,
                    TokenKind.RIGHT_BRACKET,
                    TokenKind.LEFT_BRACE,
                    TokenKind.RIGHT_BRACE,
                )
        }

        test("unterminated-quote-reports-its-opening-coordinate") {
            val error = shouldThrow<UnterminatedQuotedLiteralException> { lex("a\n'bad").materialize() }
            error.code shouldBe SyntaxErrorCode.UNTERMINATED_QUOTED_LITERAL
            error.span.start.line shouldBe 1
            error.span.start.column shouldBe 0
        }

        test("unterminated-block-comment-is-typed") {
            val error = shouldThrow<UnterminatedBlockCommentException> { lex("a /* bad").materialize() }
            error.code shouldBe SyntaxErrorCode.UNTERMINATED_BLOCK_COMMENT
            error.offendingText shouldBe "/* bad"
        }

        test("malformed-base-prefix-is-rejected") {
            val error = shouldThrow<MalformedNumericLiteralException> { lex("0x").materialize() }
            error.code shouldBe SyntaxErrorCode.MALFORMED_NUMERIC_LITERAL
        }

        test("a-lone-carriage-return-escape-is-rejected") {
            val error = shouldThrow<InvalidEscapeException> { lex("'\\\rstill quoted'").materialize() }
            error.code shouldBe SyntaxErrorCode.INVALID_ESCAPE
        }

        test("unsupported-escapes-are-rejected") {
            val error = shouldThrow<InvalidEscapeException> { lex("'\\q'").materialize() }
            error.code shouldBe SyntaxErrorCode.INVALID_ESCAPE
            error.offendingText shouldBe "\\q"
        }

        withTests(nameFn = { "malformed/${it.id}" }, malformed) { case ->
            val error = shouldThrow<PrologLexingException> { lex(case.text).materialize() }
            error::class shouldBe case.expected
        }

        withTests(nameFn = { "escape/${it.id}" }, escapes) { case ->
            val payload = lex(case.text).significantTokens().first().payload
            (payload as TokenPayload.QuotedText).decoded shouldBe case.decoded
        }
    })
