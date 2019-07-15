package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*
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

    /**
     * Checks passed term to be a Variable or fails otherwise
     */
    fun assertIsVar(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Var)

        assertTrue(any.isVariable)

        assertFalse(any.isBound)
        assertFalse(any.isGround)
        assertFalse(any.isNumber)
        assertFalse(any.isReal)
        assertFalse(any.isInt)
        assertFalse(any.isClause)
        assertFalse(any.isDirective)
        assertFalse(any.isFact)
        assertFalse(any.isRule)
        assertFalse(any.isCons)
        assertFalse(any.isStruct)
        assertFalse(any.isAtom)
        assertFalse(any.isList)
        assertFalse(any.isSet)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)
        assertFalse(any.isTrue)
        assertFalse(any.isFail)
        assertFalse(any.isTuple)
        assertFalse(any.isConstant)

        assertFalse(any is Numeric)
        assertFalse(any is Struct)
        assertFalse(any is Clause)
        assertFalse(any is Cons)
        assertFalse(any is Constant)
    }

    /**
     * Checks passed term to be a Numeric or fails otherwise
     */
    private fun commonNumericAssertions(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Constant)
        assertTrue(any is Numeric)

        assertTrue(any.isConstant)
        assertTrue(any.isNumber)
        assertTrue(any.isGround)

        assertFalse(any.isVariable)
        assertFalse(any.isBound)
        assertFalse(any.isClause)
        assertFalse(any.isDirective)
        assertFalse(any.isFact)
        assertFalse(any.isRule)
        assertFalse(any.isCons)
        assertFalse(any.isStruct)
        assertFalse(any.isAtom)
        assertFalse(any.isList)
        assertFalse(any.isSet)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)
        assertFalse(any.isTrue)
        assertFalse(any.isFail)
        assertFalse(any.isTuple)

        assertFalse(any is Var)
        assertFalse(any is Struct)
        assertFalse(any is Clause)
        assertFalse(any is Cons)
    }

    /**
     * Checks passed term to be a Real or fails otherwise
     */
    fun assertIsReal(any: Any) {
        commonNumericAssertions(any)

        assertTrue(any is Real)

        assertTrue(any.isReal)

        assertFalse(any.isInt)
    }

    /**
     * Checks passed term to be an Integer or fails otherwise
     */
    fun assertIsInteger(any: Any) {
        commonNumericAssertions(any)

        assertTrue(any is Integer)

        assertTrue(any.isInt)

        assertFalse(any.isReal)
    }

    /**
     * Checks passed term to be *exactly* a Struct or fails otherwise
     */
    fun assertIsStruct(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)

        assertTrue(any.isStruct)

        assertFalse(any.isVariable)
        assertFalse(any.isBound)
        assertFalse(any.isNumber)
        assertFalse(any.isReal)
        assertFalse(any.isInt)
        assertFalse(any.isClause)
        assertFalse(any.isDirective)
        assertFalse(any.isFact)
        assertFalse(any.isRule)
        assertFalse(any.isCons)
        assertFalse(any.isAtom)
        assertFalse(any.isList)
        assertFalse(any.isSet)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)
        assertFalse(any.isTrue)
        assertFalse(any.isFail)
        assertFalse(any.isTuple)
        assertFalse(any.isConstant)

        assertFalse(any is Numeric)
        assertFalse(any is Var)
        assertFalse(any is Clause)
        assertFalse(any is Cons)
        assertFalse(any is Tuple)
        assertFalse(any is Constant)
    }

    /**
     * Type testing common to Atom subclasses
     */
    private fun commonAtomAssertions(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is Constant)
        assertTrue(any is Atom)

        assertTrue(any.isStruct)
        assertTrue(any.isConstant)
        assertTrue(any.isAtom)
        assertTrue(any.isGround)

        assertFalse(any.isNumber)
        assertFalse(any.isReal)
        assertFalse(any.isInt)
        assertFalse(any.isClause)
        assertFalse(any.isDirective)
        assertFalse(any.isFact)
        assertFalse(any.isRule)
        assertFalse(any.isCons)
        assertFalse(any.isVariable)
        assertFalse(any.isBound)
        assertFalse(any.isTuple)

        assertFalse(any is Numeric)
        assertFalse(any is Clause)
        assertFalse(any is Var)
        assertFalse(any is Cons)
    }

    /**
     * Checks passed term to be *exactly* an Atom or fails otherwise
     */
    fun assertIsAtom(any: Any) {
        commonAtomAssertions(any)

        assertTrue(any is Atom) // to enable smart casts in kotlin

        assertFalse(any.isTrue)
        assertFalse(any.isFail)
        assertFalse(any.isList)
        assertFalse(any.isSet)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)

        assertFalse(any is LogicList)
        assertFalse(any is LogicSet)
        assertFalse(any is Empty)
        assertFalse(any is Truth)
    }

    /**
     * Checks passed term to be a Truth or fails otherwise
     */
    fun assertIsTruth(any: Any) {
        commonAtomAssertions(any)

        assertTrue(any is Truth)

        assertTrue(any.isTrue || any.isFail)

        assertFalse(any.isTrue && any.isFail)
        assertFalse(any.isList)
        assertFalse(any.isSet)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)

        assertFalse(any is LogicSet)
        assertFalse(any is LogicList)
        assertFalse(any is Empty)
        assertFalse(any is EmptySet)
        assertFalse(any is EmptyList)
    }

    /**
     * Checks passed term to be a Tuple or fails otherwise
     */
    fun assertIsTuple(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is Tuple)

        assertTrue(any.isStruct)
        assertTrue(any.isTuple)

        assertFalse(any.isVariable)
        assertFalse(any.isBound)
        assertFalse(any.isNumber)
        assertFalse(any.isReal)
        assertFalse(any.isInt)
        assertFalse(any.isClause)
        assertFalse(any.isDirective)
        assertFalse(any.isFact)
        assertFalse(any.isRule)
        assertFalse(any.isCons)
        assertFalse(any.isAtom)
        assertFalse(any.isList)
        assertFalse(any.isSet)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)
        assertFalse(any.isTrue)
        assertFalse(any.isFail)
        assertFalse(any.isConstant)

        assertFalse(any is Numeric)
        assertFalse(any is Atom)
        assertFalse(any is Clause)
        assertFalse(any is Cons)
        assertFalse(any is Var)
        assertFalse(any is Constant)
    }

    /**
     * Checks passed term to be a Cons or fails otherwise
     */
    fun assertIsCons(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is LogicList)
        assertTrue(any is Cons)

        assertTrue(any.isStruct)
        assertTrue(any.isList)
        assertTrue(any.isCons)

        assertFalse(any.isVariable)
        assertFalse(any.isBound)
        assertFalse(any.isNumber)
        assertFalse(any.isReal)
        assertFalse(any.isInt)
        assertFalse(any.isClause)
        assertFalse(any.isDirective)
        assertFalse(any.isFact)
        assertFalse(any.isRule)
        assertFalse(any.isAtom)
        assertFalse(any.isSet)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)
        assertFalse(any.isTrue)
        assertFalse(any.isFail)
        assertFalse(any.isTuple)
        assertFalse(any.isConstant)

        assertFalse(any is Numeric)
        assertFalse(any is Atom)
        assertFalse(any is Clause)
        assertFalse(any is Var)
        assertFalse(any is Constant)
    }

    /**
     * Checks passed term to be a Set or fails otherwise
     */
    fun assertIsSet(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is LogicSet)

        assertTrue(any.isStruct)
        assertTrue(any.isSet)

        assertFalse(any.isVariable)
        assertFalse(any.isBound)
        assertFalse(any.isNumber)
        assertFalse(any.isReal)
        assertFalse(any.isInt)
        assertFalse(any.isClause)
        assertFalse(any.isDirective)
        assertFalse(any.isFact)
        assertFalse(any.isRule)
        assertFalse(any.isCons)
        assertFalse(any.isAtom)
        assertFalse(any.isList)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)
        assertFalse(any.isTrue)
        assertFalse(any.isFail)
        assertFalse(any.isTuple)
        assertFalse(any.isConstant)

        assertFalse(any is Numeric)
        assertFalse(any is LogicList)
        assertFalse(any is Clause)
        assertFalse(any is Cons)
        assertFalse(any is Var)
        assertFalse(any is Constant)
    }

    /**
     * Type testing common to Empty subclasses
     */
    private fun commonEmptyAssertions(any: Any) {
        commonAtomAssertions(any)

        assertTrue(any is Empty)

        assertFalse(any.isTrue)
        assertFalse(any.isFail)

        assertFalse(any is Truth)
    }

    /**
     * Checks passed term to be an EmptyList or fails otherwise
     */
    fun assertIsEmptyList(any: Any) {
        commonEmptyAssertions(any)

        assertTrue(any is LogicList)
        assertTrue(any is EmptyList)

        assertTrue(any.isList)
        assertTrue(any.isEmptyList)

        assertFalse(any.isEmptySet)
        assertFalse(any.isSet)

        assertFalse(any is LogicSet)
        assertFalse(any is EmptySet)
    }

    /**
     * Checks passed term to be an EmptySet or fails otherwise
     */
    fun assertIsEmptySet(any: Any) {
        commonEmptyAssertions(any)

        assertTrue(any is LogicSet)
        assertTrue(any is EmptySet)

        assertTrue(any.isEmptySet)
        assertTrue(any.isSet)

        assertFalse(any.isList)
        assertFalse(any.isEmptyList)

        assertFalse(any is LogicList)
        assertFalse(any is EmptyList)
    }

    /**
     * Type testing common to Clause subclasses
     */
    private fun commonClauseAssertions(any: Any) {
        assertTrue(any is Term)
        assertTrue(any is Struct)
        assertTrue(any is Clause)

        assertTrue(any.isStruct)
        assertTrue(any.isClause)

        assertFalse(any.isVariable)
        assertFalse(any.isBound)
        assertFalse(any.isNumber)
        assertFalse(any.isReal)
        assertFalse(any.isInt)
        assertFalse(any.isCons)
        assertFalse(any.isAtom)
        assertFalse(any.isList)
        assertFalse(any.isSet)
        assertFalse(any.isEmptyList)
        assertFalse(any.isEmptySet)
        assertFalse(any.isTrue)
        assertFalse(any.isFail)
        assertFalse(any.isTuple)
        assertFalse(any.isConstant)

        assertFalse(any is Var)
        assertFalse(any is Numeric)
        assertFalse(any is Cons)
        assertFalse(any is LogicSet)
        assertFalse(any is LogicList)
        assertFalse(any is Atom)
        assertFalse(any is Constant)
    }

    /**
     * Checks passed term to be a Rule or fails otherwise
     */
    fun assertIsRule(any: Any) {
        commonClauseAssertions(any)

        assertTrue(any is Rule)
        assertTrue(any.isRule)

        assertFalse(any.isDirective)
        assertFalse(any is Directive)
    }

    /**
     * Checks passed term to be a Directive or fails otherwise
     */
    fun assertIsDirective(any: Any) {
        commonClauseAssertions(any)

        assertTrue(any is Directive)
        assertTrue(any.isDirective)

        assertFalse(any.isRule)
        assertFalse(any.isFact)

        assertFalse(any is Rule)
        assertFalse(any is Fact)
    }

    /**
     * Checks passed term to be a Fact or fails otherwise
     */
    fun assertIsFact(any: Any) {
        commonClauseAssertions(any)

        assertTrue(any is Rule)
        assertTrue(any is Fact)

        assertTrue(any.isRule)
        assertTrue(any.isFact)

        assertFalse(any.isDirective)
        assertFalse(any is Directive)
    }
}
