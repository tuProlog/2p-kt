package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.parsing.TermParser
import kotlin.test.Test

class StandardOperatorTermParserTest {
    private val parser = TermParser.withStandardOperators()
    private val cases = ParsingExamples.expressions.toList()

    private fun assertCase(index: Int) = parser.assertTermIsCorrectlyParsed(cases[index].first, cases[index].second)

    @Test
    fun nestedStructArgument() = assertCase(0)

    @Test
    fun parenthesizedConjunctionArgument() = assertCase(1)

    @Test
    fun simpleAddition() = assertCase(2)

    @Test
    fun arithmeticExpressionWithPrecedence() = assertCase(3)

    @Test
    fun disjunctionOfVariables() = assertCase(4)

    @Test
    fun clauseWithDisjunctionHead() = assertCase(5)

    @Test
    fun clauseWithDisjunctionBody() = assertCase(6)

    @Test
    fun clauseWithDisjunctionHeadAndConjunctionBody() = assertCase(7)

    @Test
    fun clauseWithConjunctionInHeadDisjunctionAndConjunctionBody() = assertCase(8)

    @Test
    fun clauseWithConjunctionInBothHeadAndBodyDisjunctions() = assertCase(9)

    @Test
    fun clauseWithListsInHeadAndConjunctionsInBody() = assertCase(10)

    @Test
    fun clauseWithIfThenElseInHeadAndDisjunctionInBody() = assertCase(11)

    @Test
    fun factWithListArgument() = assertCase(12)

    @Test
    fun clauseWithListDestructuringInHead() = assertCase(13)

    @Test
    fun factWithListPatternArgument() = assertCase(14)

    @Test
    fun recursiveClauseWithListDestructuring() = assertCase(15)

    @Test
    fun clauseWithComparisonAndArithmeticInBody() = assertCase(16)

    @Test
    fun baseFactWithEmptyListAndZero() = assertCase(17)

    @Test
    fun recursiveClauseComputingLength() = assertCase(18)

    @Test
    fun clauseDelegatingToHelperPredicate() = assertCase(19)

    @Test
    fun baseClauseWithCut() = assertCase(20)

    @Test
    fun recursiveClauseAccumulatingReversedList() = assertCase(21)

    @Test
    fun clauseCallingBuiltinReverse() = assertCase(22)

    @Test
    fun clauseWithNegationAsFailure() = assertCase(23)

    @Test
    fun baseFactWithTwoEmptyLists() = assertCase(24)

    @Test
    fun recursiveClauseFlatteningNestedLists() = assertCase(25)

    @Test
    fun clauseWithInequalityAndRecursiveCall() = assertCase(26)

    @Test
    fun clauseWithComparisonOverBaseCase() = assertCase(27)

    @Test
    fun clauseWithInequalityOverBaseCase() = assertCase(28)

    @Test
    fun clauseWithComparisonAndInequalityConjunction() = assertCase(29)

    @Test
    fun clauseWithArithmeticAndRecursiveCall() = assertCase(30)

    @Test
    fun clauseComputingUpperBoundsFromMapSize() = assertCase(31)

    @Test
    fun clauseWithNestedComparisonsAndConjunctions() = assertCase(32)

    @Test
    fun clauseWithNegatedCallInBody() = assertCase(33)

    @Test
    fun clauseWithMultipleGoalsAndArithmeticUpdate() = assertCase(34)

    @Test
    fun ifThenElseWithConjunctionAndFormatCall() = assertCase(35)

    @Test
    fun clauseWithDeeplyNestedConjunctionsAndArithmetic() = assertCase(36)
}
