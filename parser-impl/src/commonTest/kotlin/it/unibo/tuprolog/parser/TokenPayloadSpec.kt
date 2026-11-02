package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.parser.tokens.TokenPayload
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private class PayloadCase(
    val id: String,
    val text: String,
    val representation: String,
)

private val payloads =
    listOf(
        PayloadCase("decimal", "42", "42"),
        PayloadCase("binary", "0b101", "0b101"),
        PayloadCase("octal", "0o17", "0o17"),
        PayloadCase("hexadecimal", "0Xff", "0xff"),
        PayloadCase("single-quoted", "'a b'", "'a b'"),
        PayloadCase("single-quoted-with-quote", "'I''m'", "'I\\'m'"),
        PayloadCase("single-quoted-with-backslash", "'a\\\\b'", "'a\\\\b'"),
        PayloadCase("double-quoted", "\"say \\\"hi\\\"\"", "\"say \\\"hi\\\"\""),
        PayloadCase("character-code", "0'a", "97"),
        PayloadCase("name", "foo", "foo"),
    )

/** The payload of the first token of [text]. */
private fun payloadOf(text: String): TokenPayload = lex(text).significantTokens().first().payload!!

class TokenPayloadSpec :
    FunSpec({
        withTests(nameFn = { "representation/${it.id}" }, payloads) { case ->
            assertEquals(case.representation, payloadOf(case.text).toRepresentation())
        }

        withTests(nameFn = { "representation/${it.id}/reads-back-as-the-same-payload" }, payloads) { case ->
            val payload = payloadOf(case.text)
            if (payload !is TokenPayload.CharacterCode) {
                assertEquals(payload, payloadOf(payload.toRepresentation()))
            }
        }

        test("representation/unknown-radix-is-rejected") {
            assertFailsWith<IllegalArgumentException> { TokenPayload.IntegerDigits(3, "12").toRepresentation() }
        }
    })
