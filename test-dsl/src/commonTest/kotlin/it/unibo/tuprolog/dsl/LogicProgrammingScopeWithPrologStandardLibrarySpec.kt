@file:Suppress("DEPRECATION")

package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.VariablesProvider

class LogicProgrammingScopeWithPrologStandardLibrarySpec :
    FunSpec({
        with(
            LogicProgrammingScopeTester<LogicProgrammingScopeWithPrologStandardLibrary<*>> {
                LogicProgrammingScope
                    .empty()
            },
        ) {
            test("halt") {
                logicProgramming {
                    assertExpressionIsCorrect(atomOf("halt")) { halt }
                }
            }

            test("at-end-of-stream0") {
                logicProgramming {
                    assertExpressionIsCorrect(atomOf("at_end_of_stream")) { at_end_of_stream }
                }
            }

            test("nl0") {
                logicProgramming {
                    assertExpressionIsCorrect(atomOf("nl")) { nl }
                }
            }

            test("repeat0") {
                logicProgramming {
                    assertExpressionIsCorrect(atomOf("repeat")) { repeat }
                }
            }

            test("cut0") {
                logicProgramming {
                    assertExpressionIsCorrect(atomOf("!")) { cut }
                }
            }

            test("at-end-of-stream1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("at_end_of_stream", x)) { at_end_of_stream(x) }
                }
            }

            test("call1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("call", x)) { call(x) }
                }
            }

            test("catch3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("catch", x, y, z)) { catch(x, y, z) }
                }
            }

            test("throw1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("throw", x)) { `throw`(x) }
                }
            }

            test("not1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("not", x)) { not(x) }
                }
            }

            test("naf1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("\\+", x)) { naf(x) }
                }
            }

            test("assert1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("assert", x)) { assert(x) }
                }
            }

            test("asserta1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("asserta", x)) { asserta(x) }
                }
            }

            test("assertz1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("assertz", x)) { assertz(x) }
                }
            }

            test("arg3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("arg", x, y, z)) { arg(x, y, z) }
                }
            }

            test("atom1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("atom", x)) { atom(x) }
                }
            }

            test("atomic1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("atomic", x)) { atomic(x) }
                }
            }

            test("between3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("between", x, y, z)) { between(x, y, z) }
                }
            }

            test("callable1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("callable", x)) { callable(x) }
                }
            }

            test("compound1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("compound", x)) { compound(x) }
                }
            }

            test("current-op3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("current_op", x, y, z)) { current_op(x, y, z) }
                }
            }

            test("op3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("op", x, y, z)) { op(x, y, z) }
                }
            }

            test("findall3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("findall", x, y, z)) { findall(x, y, z) }
                }
            }

            test("float1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("float", x)) { float(x) }
                }
            }

            test("functor3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("functor", x, y, z)) { functor(x, y, z) }
                }
            }

            test("ground1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("ground", x)) { ground(x) }
                }
            }

            test("integer1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("integer", x)) { integer(x) }
                }
            }

            test("natural") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("natural", x)) { natural(x) }
                }
            }

            test("nonvar1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("nonvar", x)) { nonvar(x) }
                }
            }

            test("number1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("number", x)) { number(x) }
                }
            }

            test("var1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("var", x)) { `var`(x) }
                }
            }

            test("write1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("write", x)) { write(x) }
                }
            }

            test("univ2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("=..", x, y)) { x univ y }
                }
            }

            test("eq2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("=", x, y)) { x eq y }
                }
            }

            test("neq2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("\\=", x, y)) { x neq y }
                }
            }

            test("id2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("==", x, y)) { x id y }
                }
            }

            test("nid2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("\\==", x, y)) { x nid y }
                }
            }

            test("arith-eq2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("=:=", x, y)) { x arithEq y }
                }
            }

            test("arith-neq2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("=\\=", x, y)) { x arithNeq y }
                }
            }

            test("member2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("member", x, y)) { member(x, y) }
                }
            }

            test("retract1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("retract", x)) { retract(x) }
                }
            }

            test("append3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("append", x, y, z)) { append(x, y, z) }
                }
            }

            test("retractall1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("retractall", x)) { retractall(x) }
                }
            }

            test("abolish1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("abolish", x)) { abolish(x) }
                }
            }

            test("atom-chars2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("atom_chars", x, y)) { atom_chars(x, y) }
                }
            }

            test("atom-codes2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("atom_codes", x, y)) { atom_codes(x, y) }
                }
            }

            test("atom-concat3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("atom_concat", x, y, z)) { atom_concat(x, y, z) }
                }
            }

            test("atom-length2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("atom_length", x, y)) { atom_length(x, y) }
                }
            }

            test("char-code2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("char_code", x, y)) { char_code(x, y) }
                }
            }

            test("clause2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("clause", x, y)) { clause(x, y) }
                }
            }

            test("copy-term2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("copy_term", x, y)) { copy_term(x, y) }
                }
            }

            test("current-flag2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("current_flag", x, y)) { current_flag(x, y) }
                }
            }

            test("current-prolog-flag2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("current_prolog_flag", x, y)) { current_prolog_flag(x, y) }
                }
            }

            test("sub-atom5") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    val a by vars
                    val b by vars
                    assertExpressionIsCorrect(structOf("sub_atom", x, y, z, a, b)) { sub_atom(x, y, z, a, b) }
                }
            }

            test("number-chars2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("number_chars", x, y)) { number_chars(x, y) }
                }
            }

            test("number-codes2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("number_codes", x, y)) { number_codes(x, y) }
                }
            }

            test("bagof3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("bagof", x, y, z)) { bagof(x, y, z) }
                }
            }

            test("setof3") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    val z by vars
                    assertExpressionIsCorrect(structOf("setof", x, y, z)) { setof(x, y, z) }
                }
            }

            test("consult1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("consult", x)) { consult(x) }
                }
            }

            test("set-flag2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("set_flag", x, y)) { set_flag(x, y) }
                }
            }

            test("set-prolog-flag2") {
                logicProgramming {
                    val x by vars
                    val y by vars
                    assertExpressionIsCorrect(structOf("set_prolog_flag", x, y)) { set_prolog_flag(x, y) }
                }
            }

            test("dynamic1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("dynamic", x)) { dynamic(x) }
                }
            }

            test("static1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("static", x)) { static(x) }
                }
            }

            test("solve1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("solve", x)) { solve(x) }
                }
            }

            test("initialization1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("initialization", x)) { initialization(x) }
                }
            }

            test("load1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("load", x)) { load(x) }
                }
            }

            test("include1") {
                logicProgramming {
                    val x by vars
                    assertExpressionIsCorrect(structOf("include", x)) { include(x) }
                }
            }
        }
    })

private val LogicProgrammingScopeWithPrologStandardLibrary<*>.vars
    get() = VariablesProvider.of(this)
