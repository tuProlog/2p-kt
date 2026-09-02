package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.parsing.TermParser
import kotlin.test.Test

class CanonicalTermParserTest {
    private val parser = TermParser.withNoOperator()
    private val cases = ParsingExamples.canonicalTerms.toList()

    private fun assertCase(index: Int) = parser.assertTermIsCorrectlyParsed(cases[index].first, cases[index].second)

    @Test fun structWithOneVariableArgument() = assertCase(0)

    @Test fun structWithVariableAndAtomArguments() = assertCase(1)

    @Test fun structWithVariableAtomAndNumberArguments() = assertCase(2)

    @Test fun emptyList() = assertCase(3)

    @Test fun emptyListWithSpace() = assertCase(4)

    @Test fun singletonList() = assertCase(5)

    @Test fun listWithHeadAndVariableTail() = assertCase(6)

    @Test fun listWithTwoHeadsAndVariableTail() = assertCase(7)

    @Test fun listOfAtomNumberAndVariable() = assertCase(8)

    @Test fun emptyBlock() = assertCase(9)

    @Test fun emptyBlockWithSpace() = assertCase(10)

    @Test fun blockWithOneNumber() = assertCase(11)

    @Test fun blockWithNumberAndAtom() = assertCase(12)

    @Test fun blockWithNumberAtomAndVariable() = assertCase(13)

    @Test fun lowerCaseAtom() = assertCase(14)

    @Test fun upperCaseVariable() = assertCase(15)

    @Test fun quotedLowerCaseAtom() = assertCase(16)

    @Test fun quotedUpperCaseAtom() = assertCase(17)

    @Test fun quotedAtomWithSpace() = assertCase(18)

    @Test fun quotedNumericAtom() = assertCase(19)

    @Test fun quotedDecimalNumericAtom() = assertCase(20)

    @Test fun doubleQuotedLowerCaseAtom() = assertCase(21)

    @Test fun doubleQuotedUpperCaseAtom() = assertCase(22)

    @Test fun doubleQuotedAtomWithSpace() = assertCase(23)

    @Test fun doubleQuotedNumericAtom() = assertCase(24)

    @Test fun doubleQuotedDecimalNumericAtom() = assertCase(25)

    @Test fun integerNumber() = assertCase(26)

    @Test fun decimalNumber() = assertCase(27)

    @Test fun negativeIntegerNumber() = assertCase(28)

    @Test fun negativeDecimalNumber() = assertCase(29)

    @Test fun largeHexadecimalInteger() = assertCase(30)

    @Test fun lowerCaseHexadecimalInteger() = assertCase(31)

    @Test fun upperCaseHexadecimalInteger() = assertCase(32)

    @Test fun negativeLowerCaseHexadecimalInteger() = assertCase(33)

    @Test fun negativeUpperCaseHexadecimalInteger() = assertCase(34)

    @Test fun lowerCaseBinaryInteger() = assertCase(35)

    @Test fun upperCaseBinaryInteger() = assertCase(36)

    @Test fun negativeLowerCaseBinaryInteger() = assertCase(37)

    @Test fun negativeUpperCaseBinaryInteger() = assertCase(38)

    @Test fun lowerCaseOctalInteger() = assertCase(39)

    @Test fun upperCaseOctalInteger() = assertCase(40)

    @Test fun negativeLowerCaseOctalInteger() = assertCase(41)

    @Test fun negativeUpperCaseOctalInteger() = assertCase(42)

    @Test fun duplicateUpperCaseOctalInteger() = assertCase(43)

    @Test fun characterCodeLiteral() = assertCase(44)

    @Test fun negativeCharacterCodeLiteral() = assertCase(45)

    @Test fun piConstant() = assertCase(46)

    @Test fun parenthesizedPlusAtom() = assertCase(47)

    @Test fun parenthesizedMinusAtom() = assertCase(48)

    @Test fun dollarAtom() = assertCase(49)

    @Test fun plusStructWithOneArgument() = assertCase(50)

    @Test fun minusStructWithOneArgument() = assertCase(51)

    @Test fun dollarStructWithOneArgument() = assertCase(52)

    @Test fun plusStructWithTwoArguments() = assertCase(53)

    @Test fun minusStructWithTwoArguments() = assertCase(54)

    @Test fun dollarStructWithTwoArguments() = assertCase(55)

    @Test fun quotedPlusStructWithOneArgument() = assertCase(56)

    @Test fun quotedMinusStructWithOneArgument() = assertCase(57)

    @Test fun quotedDollarStructWithOneArgument() = assertCase(58)

    @Test fun quotedPlusStructWithTwoArguments() = assertCase(59)

    @Test fun quotedMinusStructWithTwoArguments() = assertCase(60)

    @Test fun quotedDollarStructWithTwoArguments() = assertCase(61)

    @Test fun quotedUpperCaseFunctorStruct() = assertCase(62)

    @Test fun quotedTrueStructWithArgument() = assertCase(63)

    @Test fun quotedFalseStructWithArgument() = assertCase(64)

    @Test fun quotedFailStructWithArgument() = assertCase(65)

    @Test fun unquotedTrueStructWithArgument() = assertCase(66)

    @Test fun unquotedFalseStructWithArgument() = assertCase(67)

    @Test fun unquotedFailStructWithArgument() = assertCase(68)
}
