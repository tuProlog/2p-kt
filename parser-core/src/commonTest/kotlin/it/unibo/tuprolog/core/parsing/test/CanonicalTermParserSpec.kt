package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.parsing.TermParser

class CanonicalTermParserSpec :
    FunSpec({
        val parser = TermParser.withNoOperator()

        val cases = ParsingExamples.canonicalTerms.toList()

        fun assertCase(index: Int) = parser.assertTermIsCorrectlyParsed(cases[index].first, cases[index].second)

        test("struct-with-one-variable-argument") {
            assertCase(0)
        }

        test("struct-with-variable-and-atom-arguments") {
            assertCase(1)
        }

        test("struct-with-variable-atom-and-number-arguments") {
            assertCase(2)
        }

        test("empty-list") {
            assertCase(3)
        }

        test("empty-list-with-space") {
            assertCase(4)
        }

        test("singleton-list") {
            assertCase(5)
        }

        test("list-with-head-and-variable-tail") {
            assertCase(6)
        }

        test("list-with-two-heads-and-variable-tail") {
            assertCase(7)
        }

        test("list-of-atom-number-and-variable") {
            assertCase(8)
        }

        test("empty-block") {
            assertCase(9)
        }

        test("empty-block-with-space") {
            assertCase(10)
        }

        test("block-with-one-number") {
            assertCase(11)
        }

        test("block-with-number-and-atom") {
            assertCase(12)
        }

        test("block-with-number-atom-and-variable") {
            assertCase(13)
        }

        test("lower-case-atom") {
            assertCase(14)
        }

        test("upper-case-variable") {
            assertCase(15)
        }

        test("quoted-lower-case-atom") {
            assertCase(16)
        }

        test("quoted-upper-case-atom") {
            assertCase(17)
        }

        test("quoted-atom-with-space") {
            assertCase(18)
        }

        test("quoted-numeric-atom") {
            assertCase(19)
        }

        test("quoted-decimal-numeric-atom") {
            assertCase(20)
        }

        test("double-quoted-lower-case-atom") {
            assertCase(21)
        }

        test("double-quoted-upper-case-atom") {
            assertCase(22)
        }

        test("double-quoted-atom-with-space") {
            assertCase(23)
        }

        test("double-quoted-numeric-atom") {
            assertCase(24)
        }

        test("double-quoted-decimal-numeric-atom") {
            assertCase(25)
        }

        test("integer-number") {
            assertCase(26)
        }

        test("decimal-number") {
            assertCase(27)
        }

        test("negative-integer-number") {
            assertCase(28)
        }

        test("negative-decimal-number") {
            assertCase(29)
        }

        test("large-hexadecimal-integer") {
            assertCase(30)
        }

        test("lower-case-hexadecimal-integer") {
            assertCase(31)
        }

        test("upper-case-hexadecimal-integer") {
            assertCase(32)
        }

        test("negative-lower-case-hexadecimal-integer") {
            assertCase(33)
        }

        test("negative-upper-case-hexadecimal-integer") {
            assertCase(34)
        }

        test("lower-case-binary-integer") {
            assertCase(35)
        }

        test("upper-case-binary-integer") {
            assertCase(36)
        }

        test("negative-lower-case-binary-integer") {
            assertCase(37)
        }

        test("negative-upper-case-binary-integer") {
            assertCase(38)
        }

        test("lower-case-octal-integer") {
            assertCase(39)
        }

        test("upper-case-octal-integer") {
            assertCase(40)
        }

        test("negative-lower-case-octal-integer") {
            assertCase(41)
        }

        test("negative-upper-case-octal-integer") {
            assertCase(42)
        }

        test("duplicate-upper-case-octal-integer") {
            assertCase(43)
        }

        test("character-code-literal") {
            assertCase(44)
        }

        test("negative-character-code-literal") {
            assertCase(45)
        }

        test("pi-constant") {
            assertCase(46)
        }

        test("parenthesized-plus-atom") {
            assertCase(47)
        }

        test("parenthesized-minus-atom") {
            assertCase(48)
        }

        test("dollar-atom") {
            assertCase(49)
        }

        test("plus-struct-with-one-argument") {
            assertCase(50)
        }

        test("minus-struct-with-one-argument") {
            assertCase(51)
        }

        test("dollar-struct-with-one-argument") {
            assertCase(52)
        }

        test("plus-struct-with-two-arguments") {
            assertCase(53)
        }

        test("minus-struct-with-two-arguments") {
            assertCase(54)
        }

        test("dollar-struct-with-two-arguments") {
            assertCase(55)
        }

        test("quoted-plus-struct-with-one-argument") {
            assertCase(56)
        }

        test("quoted-minus-struct-with-one-argument") {
            assertCase(57)
        }

        test("quoted-dollar-struct-with-one-argument") {
            assertCase(58)
        }

        test("quoted-plus-struct-with-two-arguments") {
            assertCase(59)
        }

        test("quoted-minus-struct-with-two-arguments") {
            assertCase(60)
        }

        test("quoted-dollar-struct-with-two-arguments") {
            assertCase(61)
        }

        test("quoted-upper-case-functor-struct") {
            assertCase(62)
        }

        test("quoted-true-struct-with-argument") {
            assertCase(63)
        }

        test("quoted-false-struct-with-argument") {
            assertCase(64)
        }

        test("quoted-fail-struct-with-argument") {
            assertCase(65)
        }

        test("unquoted-true-struct-with-argument") {
            assertCase(66)
        }

        test("unquoted-false-struct-with-argument") {
            assertCase(67)
        }

        test("unquoted-fail-struct-with-argument") {
            assertCase(68)
        }
    })
