package it.unibo.tuprolog.core.testutils

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllFalse
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllTrue
import it.unibo.tuprolog.core.List as LogicList

/**
 * Utils to assert types in tests, that works only on properties defined in [Term] interface
 *
 * @author Enrico
 */
internal object TermTypeAssertionUtils {
    /** Checks passed term to be a Variable or fails otherwise */
    fun assertIsVar(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Var>()

        any.isVar.shouldBeTrue()

        assertAllFalse(
            any.isGround,
            any.isNumber,
            any.isReal,
            any.isInteger,
            any.isClause,
            any.isGoal,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isStruct,
            any.isAtom,
            any.isList,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isConstant,
            any.isIndicator,
            any is Numeric,
            any is Struct,
            any is Clause,
            any is Cons,
            any is Constant,
        )
    }

    /** Checks passed term to be a Numeric or fails otherwise */
    private fun commonNumericAssertions(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Constant>()
        any.shouldBeInstanceOf<Numeric>()

        assertAllTrue(
            any.isConstant,
            any.isNumber,
            any.isGround,
        )

        assertAllFalse(
            any.isVar,
            any.isClause,
            any.isGoal,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isStruct,
            any.isAtom,
            any.isList,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isIndicator,
            any is Var,
            any is Struct,
            any is Clause,
            any is Cons,
        )
    }

    /** Checks passed term to be a Real or fails otherwise */
    fun assertIsReal(any: Any) {
        commonNumericAssertions(any)

        any.shouldBeInstanceOf<Real>()

        any.isReal.shouldBeTrue()

        any.isInteger.shouldBeFalse()
    }

    /** Checks passed term to be an Integer or fails otherwise */
    fun assertIsInteger(any: Any) {
        commonNumericAssertions(any)

        any.shouldBeInstanceOf<Integer>()

        any.isInteger.shouldBeTrue()

        any.isReal.shouldBeFalse()
    }

    /** Checks passed term to be *exactly* a Struct or fails otherwise */
    fun assertIsStruct(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Struct>()

        any.isStruct.shouldBeTrue()

        assertAllFalse(
            any.isVar,
            any.isNumber,
            any.isReal,
            any.isInteger,
            any.isClause,
            any.isGoal,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isConstant,
            any.isIndicator,
            any is Numeric,
            any is Var,
            any is Clause,
            any is Cons,
            any is Tuple,
            any is Constant,
            any is Indicator,
        )
    }

    /** Type testing common to Atom subclasses */
    private fun commonAtomAssertions(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Struct>()
        any.shouldBeInstanceOf<Constant>()
        any.shouldBeInstanceOf<Atom>()

        assertAllTrue(
            any.isStruct,
            any.isConstant,
            any.isAtom,
            any.isGround,
        )

        assertAllFalse(
            any.isNumber,
            any.isReal,
            any.isInteger,
            any.isClause,
            any.isGoal,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isVar,
            any.isTuple,
            any.isIndicator,
            any is Numeric,
            any is Clause,
            any is Var,
            any is Cons,
        )
    }

    /** Checks passed term to be *exactly* an Atom or fails otherwise */
    fun assertIsAtom(any: Any) {
        commonAtomAssertions(any)

        any.shouldBeInstanceOf<Atom>() // to enable smart casts in kotlin

        assertAllFalse(
            any.isTrue,
            any.isFail,
            any.isList,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any is LogicList,
            any is Block,
            any is Empty,
            any is Truth,
        )
    }

    /** Checks passed term to be a Truth or fails otherwise */
    fun assertIsTruth(any: Any) {
        commonAtomAssertions(any)

        any.shouldBeInstanceOf<Truth>()

        (any.isTrue || any.isFail).shouldBeTrue()

        assertAllFalse(
            any.isTrue && any.isFail,
            any.isList,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any is Block,
            any is LogicList,
            any is Empty,
            any is EmptyBlock,
            any is EmptyList,
        )
    }

    /** Checks passed term to be a Tuple or fails otherwise */
    fun assertIsTuple(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Struct>()
        any.shouldBeInstanceOf<Tuple>()

        assertAllTrue(
            any.isStruct,
            any.isTuple,
        )

        assertAllFalse(
            any.isVar,
            any.isNumber,
            any.isReal,
            any.isInteger,
            any.isClause,
            any.isGoal,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any.isTrue,
            any.isFail,
            any.isConstant,
            any.isIndicator,
            any is Numeric,
            any is Atom,
            any is Clause,
            any is Cons,
            any is Var,
            any is Constant,
            any is Indicator,
        )
    }

    /** Checks passed term to be an Indicator or fails otherwise */
    fun assertIsIndicator(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Struct>()
        any.shouldBeInstanceOf<Indicator>()

        assertAllTrue(
            any.isStruct,
            any.isIndicator,
        )

        assertAllFalse(
            any.isTuple,
            any.isVar,
            any.isNumber,
            any.isReal,
            any.isInteger,
            any.isClause,
            any.isGoal,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any.isTrue,
            any.isFail,
            any.isConstant,
            any is Numeric,
            any is Atom,
            any is Clause,
            any is Cons,
            any is Var,
            any is Constant,
            any is Tuple,
        )
    }

    /** Checks passed term to be a Cons or fails otherwise */
    fun assertIsCons(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Struct>()
        any.shouldBeInstanceOf<LogicList>()
        any.shouldBeInstanceOf<Cons>()

        assertAllTrue(
            any.isStruct,
            any.isList,
            any.isCons,
        )

        assertAllFalse(
            any.isVar,
            any.isNumber,
            any.isReal,
            any.isInteger,
            any.isClause,
            any.isGoal,
            any.isFact,
            any.isRule,
            any.isAtom,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isConstant,
            any.isIndicator,
            any is Numeric,
            any is Atom,
            any is Clause,
            any is Var,
            any is Constant,
        )
    }

    /** Checks passed term to be a Block or fails otherwise */
    fun assertIsBlock(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Struct>()
        any.shouldBeInstanceOf<Block>()

        assertAllTrue(
            any.isStruct,
            any.isBlock,
        )

        assertAllFalse(
            any.isVar,
            any.isNumber,
            any.isReal,
            any.isInteger,
            any.isClause,
            any.isGoal,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isEmptyList,
            any.isEmptyBlock,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isConstant,
            any.isIndicator,
            any is Numeric,
            any is LogicList,
            any is Clause,
            any is Cons,
            any is Var,
            any is Constant,
        )
    }

    /** Type testing common to Empty subclasses */
    private fun commonEmptyAssertions(any: Any) {
        commonAtomAssertions(any)

        any.shouldBeInstanceOf<Empty>()

        assertAllFalse(
            any.isTrue,
            any.isFail,
            any is Truth,
        )
    }

    /** Checks passed term to be an EmptyList or fails otherwise */
    fun assertIsEmptyList(any: Any) {
        commonEmptyAssertions(any)

        any.shouldBeInstanceOf<LogicList>()
        any.shouldBeInstanceOf<EmptyList>()

        assertAllTrue(
            any.isList,
            any.isEmptyList,
        )

        assertAllFalse(
            any.isEmptyBlock,
            any.isBlock,
            any is Block,
            any is EmptyBlock,
        )
    }

    /** Checks passed term to be an EmptyBlock or fails otherwise */
    fun assertIsEmptyBlock(any: Any) {
        commonEmptyAssertions(any)

        any.shouldBeInstanceOf<Block>()
        any.shouldBeInstanceOf<EmptyBlock>()

        assertAllTrue(
            any.isEmptyBlock,
            any.isBlock,
        )

        assertAllFalse(
            any.isList,
            any.isEmptyList,
            any is LogicList,
            any is EmptyList,
        )
    }

    /** Type testing common to Clause subclasses */
    private fun commonClauseAssertions(any: Any) {
        any.shouldBeInstanceOf<Term>()
        any.shouldBeInstanceOf<Struct>()
        any.shouldBeInstanceOf<Clause>()

        assertAllTrue(
            any.isStruct,
            any.isClause,
        )

        assertAllFalse(
            any.isVar,
            any.isNumber,
            any.isReal,
            any.isInteger,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isBlock,
            any.isEmptyList,
            any.isEmptyBlock,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isConstant,
            any.isIndicator,
            any is Var,
            any is Numeric,
            any is Cons,
            any is Block,
            any is LogicList,
            any is Atom,
            any is Constant,
            any is Indicator,
        )
    }

    /** Checks passed term to be a Rule or fails otherwise */
    fun assertIsRule(any: Any) {
        commonClauseAssertions(any)

        any.shouldBeInstanceOf<Rule>()
        any.isRule.shouldBeTrue()

        assertAllFalse(
            any.isGoal,
            any is Goal,
        )
    }

    /** Checks passed term to be a Goal or fails otherwise */
    fun assertIsGoal(any: Any) {
        commonClauseAssertions(any)

        any.shouldBeInstanceOf<Goal>()
        any.isGoal.shouldBeTrue()

        assertAllFalse(
            any.isRule,
            any.isFact,
            any is Rule,
            any is Fact,
        )
    }

    /** Checks passed term to be a Fact or fails otherwise */
    fun assertIsFact(any: Any) {
        commonClauseAssertions(any)

        any.shouldBeInstanceOf<Rule>()
        any.shouldBeInstanceOf<Fact>()

        assertAllTrue(
            any.isRule,
            any.isFact,
        )

        assertAllFalse(
            any.isGoal,
            any is Goal,
        )
    }
}
