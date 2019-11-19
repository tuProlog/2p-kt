package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertFalse
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.core.Set as LogicSet

/**
 * Utils to assert types in tests, that works only on properties defined in [Term] interface
 *
 * @author Enrico
 */
internal object TermTypeAssertionUtils {

    /** Checks passed term to be a Variable or fails otherwise */
    fun assertIsVar(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Var)

        assertTrue(any.isVariable)

        assertFalse(
            any.isBound,
            any.isGround,
            any.isNumber,
            any.isReal,
            any.isInt,
            any.isClause,
            any.isDirective,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isStruct,
            any.isAtom,
            any.isList,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isConstant,
            any.isIndicator,
            any is Numeric,
            any is Struct,
            any is Clause,
            any is Cons,
            any is Constant
        )
    }

    /** Checks passed term to be a Numeric or fails otherwise */
    private fun commonNumericAssertions(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Constant)
        assertTrue(any is Numeric)

        assertTrue(
            any.isConstant,
            any.isNumber,
            any.isGround
        )

        assertFalse(
            any.isVariable,
            any.isBound,
            any.isClause,
            any.isDirective,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isStruct,
            any.isAtom,
            any.isList,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isIndicator,
            any is Var,
            any is Struct,
            any is Clause,
            any is Cons
        )
    }

    /** Checks passed term to be a Real or fails otherwise */
    fun assertIsReal(any: Any) {
        commonNumericAssertions(any)

        assertTrue(any is Real)

        assertTrue(any.isReal)

        assertFalse(any.isInt)
    }

    /** Checks passed term to be an Integer or fails otherwise */
    fun assertIsInteger(any: Any) {
        commonNumericAssertions(any)

        assertTrue(any is Integer)

        assertTrue(any.isInt)

        assertFalse(any.isReal)
    }

    /** Checks passed term to be *exactly* a Struct or fails otherwise */
    fun assertIsStruct(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)

        assertTrue(any.isStruct)

        assertFalse(
            any.isVariable,
            any.isBound,
            any.isNumber,
            any.isReal,
            any.isInt,
            any.isClause,
            any.isDirective,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
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
            any is Indicator
        )
    }

    /** Type testing common to Atom subclasses */
    private fun commonAtomAssertions(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is Constant)
        assertTrue(any is Atom)

        assertTrue(
            any.isStruct,
            any.isConstant,
            any.isAtom,
            any.isGround
        )

        assertFalse(
            any.isNumber,
            any.isReal,
            any.isInt,
            any.isClause,
            any.isDirective,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isVariable,
            any.isBound,
            any.isTuple,
            any.isIndicator,
            any is Numeric,
            any is Clause,
            any is Var,
            any is Cons
        )
    }

    /** Checks passed term to be *exactly* an Atom or fails otherwise */
    fun assertIsAtom(any: Any) {
        commonAtomAssertions(any)

        assertTrue(any is Atom) // to enable smart casts in kotlin

        assertFalse(
            any.isTrue,
            any.isFail,
            any.isList,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
            any is LogicList,
            any is LogicSet,
            any is Empty,
            any is Truth
        )
    }

    /** Checks passed term to be a Truth or fails otherwise */
    fun assertIsTruth(any: Any) {
        commonAtomAssertions(any)

        assertTrue(any is Truth)

        assertTrue(any.isTrue || any.isFail)

        assertFalse(
            any.isTrue && any.isFail,
            any.isList,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
            any is LogicSet,
            any is LogicList,
            any is Empty,
            any is EmptySet,
            any is EmptyList
        )
    }

    /** Checks passed term to be a Tuple or fails otherwise */
    fun assertIsTuple(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is Tuple)

        assertTrue(
            any.isStruct,
            any.isTuple
        )

        assertFalse(
            any.isVariable,
            any.isBound,
            any.isNumber,
            any.isReal,
            any.isInt,
            any.isClause,
            any.isDirective,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
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
            any is Indicator
        )
    }


    /** Checks passed term to be an Indicator or fails otherwise */
    fun assertIsIndicator(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is Indicator)

        assertTrue(
            any.isStruct,
            any.isIndicator
        )

        assertFalse(
            any.isTuple,
            any.isVariable,
            any.isBound,
            any.isNumber,
            any.isReal,
            any.isInt,
            any.isClause,
            any.isDirective,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
            any.isTrue,
            any.isFail,
            any.isConstant,
            any is Numeric,
            any is Atom,
            any is Clause,
            any is Cons,
            any is Var,
            any is Constant,
            any is Tuple
        )
    }

    /** Checks passed term to be a Cons or fails otherwise */
    fun assertIsCons(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is LogicList)
        assertTrue(any is Cons)

        assertTrue(
            any.isStruct,
            any.isList,
            any.isCons
        )

        assertFalse(
            any.isVariable,
            any.isBound,
            any.isNumber,
            any.isReal,
            any.isInt,
            any.isClause,
            any.isDirective,
            any.isFact,
            any.isRule,
            any.isAtom,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isConstant,
            any.isIndicator,
            any is Numeric,
            any is Atom,
            any is Clause,
            any is Var,
            any is Constant
        )
    }

    /** Checks passed term to be a Set or fails otherwise */
    fun assertIsSet(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is LogicSet)

        assertTrue(
            any.isStruct,
            any.isSet
        )

        assertFalse(
            any.isVariable,
            any.isBound,
            any.isNumber,
            any.isReal,
            any.isInt,
            any.isClause,
            any.isDirective,
            any.isFact,
            any.isRule,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isEmptyList,
            any.isEmptySet,
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
            any is Constant
        )
    }

    /** Type testing common to Empty subclasses */
    private fun commonEmptyAssertions(any: Any) {
        commonAtomAssertions(any)

        assertTrue(any is Empty)

        assertFalse(
            any.isTrue,
            any.isFail,
            any is Truth
        )
    }

    /** Checks passed term to be an EmptyList or fails otherwise */
    fun assertIsEmptyList(any: Any) {
        commonEmptyAssertions(any)

        assertTrue(any is LogicList)
        assertTrue(any is EmptyList)

        assertTrue(
            any.isList,
            any.isEmptyList
        )

        assertFalse(
            any.isEmptySet,
            any.isSet,
            any is LogicSet,
            any is EmptySet
        )
    }

    /** Checks passed term to be an EmptySet or fails otherwise */
    fun assertIsEmptySet(any: Any) {
        commonEmptyAssertions(any)

        assertTrue(any is LogicSet)
        assertTrue(any is EmptySet)

        assertTrue(
            any.isEmptySet,
            any.isSet
        )

        assertFalse(
            any.isList,
            any.isEmptyList,
            any is LogicList,
            any is EmptyList
        )
    }

    /** Type testing common to Clause subclasses */
    private fun commonClauseAssertions(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is Clause)

        assertTrue(
            any.isStruct,
            any.isClause
        )

        assertFalse(
            any.isVariable,
            any.isBound,
            any.isNumber,
            any.isReal,
            any.isInt,
            any.isCons,
            any.isAtom,
            any.isList,
            any.isSet,
            any.isEmptyList,
            any.isEmptySet,
            any.isTrue,
            any.isFail,
            any.isTuple,
            any.isConstant,
            any.isIndicator,
            any is Var,
            any is Numeric,
            any is Cons,
            any is LogicSet,
            any is LogicList,
            any is Atom,
            any is Constant,
            any is Indicator
        )
    }

    /** Checks passed term to be a Rule or fails otherwise */
    fun assertIsRule(any: Any) {
        commonClauseAssertions(any)

        assertTrue(any is Rule)
        assertTrue(any.isRule)

        assertFalse(
            any.isDirective,
            any is Directive
        )
    }

    /** Checks passed term to be a Directive or fails otherwise */
    fun assertIsDirective(any: Any) {
        commonClauseAssertions(any)

        assertTrue(any is Directive)
        assertTrue(any.isDirective)

        assertFalse(
            any.isRule,
            any.isFact,
            any is Rule,
            any is Fact
        )
    }

    /** Checks passed term to be a Fact or fails otherwise */
    fun assertIsFact(any: Any) {
        commonClauseAssertions(any)

        assertTrue(any is Rule)
        assertTrue(any is Fact)

        assertTrue(
            any.isRule,
            any.isFact
        )

        assertFalse(
            any.isDirective,
            any is Directive
        )
    }
}
