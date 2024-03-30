package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.VariablesProvider
import kotlin.test.Test

@Suppress("DEPRECATION")
class TestLogicProgrammingScopeWithPrologStandardLibrary :
    AbstractLogicProgrammingScopeTest<LogicProgrammingScopeWithPrologStandardLibrary<*>>() {
    override fun createLogicProgrammingScope(): LogicProgrammingScopeWithPrologStandardLibrary<*> =
        LogicProgrammingScope.empty()

    private val LogicProgrammingScopeWithPrologStandardLibrary<*>.vars
        get() = VariablesProvider.of(this)

    @Test
    fun testHalt() =
        logicProgramming {
            assertExpressionIsCorrect(atomOf("halt")) { halt }
        }

    @Test
    fun testAtEndOfStream0() =
        logicProgramming {
            assertExpressionIsCorrect(atomOf("at_end_of_stream")) { at_end_of_stream }
        }

    @Test
    fun testNl0() =
        logicProgramming {
            assertExpressionIsCorrect(atomOf("nl")) { nl }
        }

    @Test
    fun testRepeat0() =
        logicProgramming {
            assertExpressionIsCorrect(atomOf("repeat")) { repeat }
        }

    @Test
    fun testCut0() =
        logicProgramming {
            assertExpressionIsCorrect(atomOf("!")) { cut }
        }

    @Test
    fun testAtEndOfStream1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("at_end_of_stream", x)) { at_end_of_stream(x) }
        }

    @Test
    fun testCall1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("call", x)) { call(x) }
        }

    @Test
    fun testCatch3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("catch", x, y, z)) { catch(x, y, z) }
        }

    @Test
    fun testThrow1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("throw", x)) { `throw`(x) }
        }

    @Test
    fun testNot1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("not", x)) { not(x) }
        }

    @Test
    fun testNaf1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("\\+", x)) { naf(x) }
        }

    @Test
    fun testAssert1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("assert", x)) { assert(x) }
        }

    @Test
    fun testAsserta1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("asserta", x)) { asserta(x) }
        }

    @Test
    fun testAssertz1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("assertz", x)) { assertz(x) }
        }

    @Test
    fun testArg3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("arg", x, y, z)) { arg(x, y, z) }
        }

    @Test
    fun testAtom1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("atom", x)) { atom(x) }
        }

    @Test
    fun testAtomic1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("atomic", x)) { atomic(x) }
        }

    @Test
    fun testBetween3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("between", x, y, z)) { between(x, y, z) }
        }

    @Test
    fun testCallable1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("callable", x)) { callable(x) }
        }

    @Test
    fun testCompound1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("compound", x)) { compound(x) }
        }

    @Test
    fun testCurrentOp3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("current_op", x, y, z)) { current_op(x, y, z) }
        }

    @Test
    fun testOp3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("op", x, y, z)) { op(x, y, z) }
        }

    @Test
    fun testFindall3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("findall", x, y, z)) { findall(x, y, z) }
        }

    @Test
    fun testFloat1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("float", x)) { float(x) }
        }

    @Test
    fun testFunctor3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("functor", x, y, z)) { functor(x, y, z) }
        }

    @Test
    fun testGround1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("ground", x)) { ground(x) }
        }

    @Test
    fun testInteger1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("integer", x)) { integer(x) }
        }

    @Test
    fun testNatural() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("natural", x)) { natural(x) }
        }

    @Test
    fun testNonvar1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("nonvar", x)) { nonvar(x) }
        }

    @Test
    fun testNumber1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("number", x)) { number(x) }
        }

    @Test
    fun testVar1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("var", x)) { `var`(x) }
        }

    @Test
    fun testWrite1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("write", x)) { write(x) }
        }

    @Test
    fun testUniv2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("=..", x, y)) { x univ y }
        }

    @Test
    fun testEq2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("=", x, y)) { x eq y }
        }

    @Test
    fun testNeq2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("\\=", x, y)) { x neq y }
        }

    @Test
    fun testId2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("==", x, y)) { x id y }
        }

    @Test
    fun testNid2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("\\==", x, y)) { x nid y }
        }

    @Test
    fun testArithEq2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("=:=", x, y)) { x arithEq y }
        }

    @Test
    fun testArithNeq2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("=\\=", x, y)) { x arithNeq y }
        }

    @Test
    fun testMember2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("member", x, y)) { member(x, y) }
        }

    @Test
    fun testRetract1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("retract", x)) { retract(x) }
        }

    @Test
    fun testAppend3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("append", x, y, z)) { append(x, y, z) }
        }

    @Test
    fun testRetractall1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("retractall", x)) { retractall(x) }
        }

    @Test
    fun testAbolish1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("abolish", x)) { abolish(x) }
        }

    @Test
    fun testAtomChars2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("atom_chars", x, y)) { atom_chars(x, y) }
        }

    @Test
    fun testAtomCodes2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("atom_codes", x, y)) { atom_codes(x, y) }
        }

    @Test
    fun testAtomConcat3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("atom_concat", x, y, z)) { atom_concat(x, y, z) }
        }

    @Test
    fun testAtomLength2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("atom_length", x, y)) { atom_length(x, y) }
        }

    @Test
    fun testCharCode2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("char_code", x, y)) { char_code(x, y) }
        }

    @Test
    fun testClause2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("clause", x, y)) { clause(x, y) }
        }

    @Test
    fun testCopyTerm2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("copy_term", x, y)) { copy_term(x, y) }
        }

    @Test
    fun testCurrentFlag2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("current_flag", x, y)) { current_flag(x, y) }
        }

    @Test
    fun testCurrentPrologFlag2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("current_prolog_flag", x, y)) { current_prolog_flag(x, y) }
        }

    @Test
    fun testSubAtom5() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            val a by vars
            val b by vars
            assertExpressionIsCorrect(structOf("sub_atom", x, y, z, a, b)) { sub_atom(x, y, z, a, b) }
        }

    @Test
    fun testNumberChars2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("number_chars", x, y)) { number_chars(x, y) }
        }

    @Test
    fun testNumberCodes2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("number_codes", x, y)) { number_codes(x, y) }
        }

    @Test
    fun testBagof3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("bagof", x, y, z)) { bagof(x, y, z) }
        }

    @Test
    fun testSetof3() =
        logicProgramming {
            val x by vars
            val y by vars
            val z by vars
            assertExpressionIsCorrect(structOf("setof", x, y, z)) { setof(x, y, z) }
        }

    @Test
    fun testConsult1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("consult", x)) { consult(x) }
        }

    @Test
    fun testSetFlag2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("set_flag", x, y)) { set_flag(x, y) }
        }

    @Test
    fun testSetPrologFlag2() =
        logicProgramming {
            val x by vars
            val y by vars
            assertExpressionIsCorrect(structOf("set_prolog_flag", x, y)) { set_prolog_flag(x, y) }
        }

    @Test
    fun testDynamic1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("dynamic", x)) { dynamic(x) }
        }

    @Test
    fun testStatic1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("static", x)) { static(x) }
        }

    @Test
    fun testSolve1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("solve", x)) { solve(x) }
        }

    @Test
    fun testInitialization1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("initialization", x)) { initialization(x) }
        }

    @Test
    fun testLoad1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("load", x)) { load(x) }
        }

    @Test
    fun testInclude1() =
        logicProgramming {
            val x by vars
            assertExpressionIsCorrect(structOf("include", x)) { include(x) }
        }
}
