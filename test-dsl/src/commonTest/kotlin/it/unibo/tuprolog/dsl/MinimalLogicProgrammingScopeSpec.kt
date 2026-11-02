package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.dsl.PlatformSpecificValues.MINUS_THREE
import it.unibo.tuprolog.dsl.PlatformSpecificValues.ONE_POINT_ZERO
import it.unibo.tuprolog.dsl.PlatformSpecificValues.THREE_POINT_ONE_DOUBLE
import it.unibo.tuprolog.dsl.PlatformSpecificValues.THREE_POINT_ONE_FLOAT
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MinimalLogicProgrammingScopeSpec :
    FunSpec({
        with(LogicProgrammingScopeTester<MinimalLogicProgrammingScope<*>> { LogicProgrammingScope.empty() }) {
            fun MinimalLogicProgrammingScope<*>.createDirectiveUnderTest(): Term =
                directiveOf(structOf("test", varOf("X")))

            fun MinimalLogicProgrammingScope<*>.createRuleUnderTest(): Term =
                ruleOf(
                    structOf("grandparent", varOf("X"), varOf("Z")),
                    structOf("parent", varOf("X"), varOf("Y")),
                    structOf("parent", varOf("Y"), varOf("Z")),
                )

            fun MinimalLogicProgrammingScope<*>.createFactUnderTest(): Term =
                factOf(structOf("parent", varOf("X"), varOf("Y")))

            test("sub-scope") {
                val scope = createLogicProgrammingScope()
                val subScope = scope.newScope()
                val var1 = scope.varOf("X")
                val var2 = subScope.varOf("X")
                assertStructurallyEquals(var1, var2)
            }

            test("sub-scoping") {
                val scope = createLogicProgrammingScope()
                val var1 = scope.varOf("X")
                scope.scope {
                    val var2 = varOf("X")
                    assertStructurallyEquals(var1, var2)
                }
            }

            test("functor-expression") {
                logicProgramming {
                    val expected =
                        structOf(
                            "functor",
                            numOf(1),
                            numOf("2.3"),
                            truthOf(false),
                            atomOf("abc"),
                            structOf("f", varOf("X")),
                            logicListOf(varOf("X"), varOf("Y")),
                            blockOf(truthOf(true), atomOf("!")),
                        )
                    val actual =
                        "functor"(
                            1,
                            2.3,
                            false,
                            "abc",
                            "f"("X"),
                            logicListOf("X", "Y"),
                            blockOf(true, "!"),
                        )
                    assertEquals(expected, actual)
                }
            }

            test("directive") {
                logicProgramming {
                    val expected = createDirectiveUnderTest()
                    val actual = directive { createDirectiveUnderTest() }
                    assertTrue(expected.structurallyEquals(actual))
                    assertNotEquals(expected, actual)
                }
            }

            test("rule") {
                logicProgramming {
                    val expected = createRuleUnderTest()
                    val actual = rule { createRuleUnderTest() }
                    assertTrue(expected.structurallyEquals(actual))
                    assertNotEquals(expected, actual)
                }
            }

            test("rule-as-clause") {
                logicProgramming {
                    val expected = createRuleUnderTest()
                    val actual = clause { createRuleUnderTest() }
                    assertTrue(expected.structurallyEquals(actual))
                    assertNotEquals(expected, actual)
                }
            }

            test("fact") {
                logicProgramming {
                    val expected = createFactUnderTest()
                    val actual = fact { createFactUnderTest() }
                    assertTrue(expected.structurallyEquals(actual))
                    assertNotEquals(expected, actual)
                }
            }

            test("fact-as-clause") {
                logicProgramming {
                    val expected = factOf(structOf("parent", varOf("X"), varOf("Y")))
                    val actual =
                        clause {
                            "parent"("X", "Y")
                        }
                    assertTrue(expected.structurallyEquals(actual))
                    assertNotEquals(expected, actual)
                }
            }

            test("tailed-list") {
                logicProgramming {
                    val expected = logicList("a", "b", tail = "c")
                    val actual = consOf("a", consOf("b", "c"))
                    assertEquals(expected, actual)
                }
            }

            test("num-of-integer") {
                assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1) }
            }

            test("num-of-negative-integer") {
                assertLogicProgrammingExpressionIsCorrect(Integer.of(-3)) { numOf(-3) }
            }

            test("num-of-long") {
                assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1L) }
            }

            test("num-of-short") {
                assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1.toShort()) }
            }

            test("num-of-byte") {
                assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf(1.toByte()) }
            }

            test("num-of-string-integer") {
                assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) { numOf("1") }
            }

            test("num-of-double") {
                assertLogicProgrammingExpressionIsCorrect(Real.of("1.0")) { numOf(1.0) }
            }

            test("num-of-float") {
                assertLogicProgrammingExpressionIsCorrect(Real.of("1.0")) { numOf(1.0f) }
            }

            test("num-of-string-double") {
                assertLogicProgrammingExpressionIsCorrect(Real.of("1.0")) { numOf("1.0") }
            }

            test("num-of-double-value") {
                assertLogicProgrammingExpressionIsCorrect(THREE_POINT_ONE_DOUBLE) { numOf(3.1) }
            }

            test("num-of-float-value") {
                assertLogicProgrammingExpressionIsCorrect(THREE_POINT_ONE_FLOAT) { numOf(3.1f) }
            }

            test("num-of-string-float-value") {
                assertLogicProgrammingExpressionIsCorrect(Real.of("3.1")) { numOf("3.1") }
            }

            test("underscore-not-equals") {
                logicProgramming {
                    val first = `_`
                    val second = `_`
                    assertAreDifferentUnderscores(first, second)
                }
            }

            test("var-of-equals") {
                logicProgramming {
                    assertEquals(varOf("X"), varOf("X"))
                }
            }

            test("var-of-underscore-equals") {
                logicProgramming {
                    assertEquals(varOf("_"), varOf("_"))
                }
            }

            test("to-term-integer") {
                assertLogicProgrammingExpressionIsCorrect(Integer.of(1)) {
                    1.toTerm()
                }
            }

            test("to-term-negative-integer") {
                assertLogicProgrammingExpressionIsCorrect(MINUS_THREE) {
                    (-3).toTerm()
                }
            }

            test("to-term-double") {
                assertLogicProgrammingExpressionIsCorrect(ONE_POINT_ZERO) {
                    1.0.toTerm()
                }
            }

            test("to-term-double-value") {
                assertLogicProgrammingExpressionIsCorrect(Real.of("3.1")) {
                    3.1.toTerm()
                }
            }

            test("to-term-string-atom") {
                assertLogicProgrammingExpressionIsCorrect(Atom.of("a")) {
                    "a".toTerm()
                }
            }

            test("to-term-string-var") {
                logicProgramming {
                    val expected = varOf("X")
                    val actual = "X".toTerm()
                    assertEquals(expected, actual)
                }
            }

            test("to-term-atom") {
                logicProgramming {
                    atomOf("a").let {
                        assertSame(it, it.toTerm())
                    }
                }
            }

            test("to-term-list") {
                assertLogicProgrammingExpressionIsCorrect(Cons.singleton(Atom.of("a"))) {
                    listOf("a").toTerm()
                }
            }

            test("to-term-set") {
                assertLogicProgrammingExpressionIsCorrect(Block.of(Atom.of("a"))) {
                    setOf("a").toTerm()
                }
            }

            test("to-term-array") {
                assertLogicProgrammingExpressionIsCorrect(List.of(Atom.of("a"))) {
                    arrayOf("a").toTerm()
                }
            }

            test("to-term-sequence") {
                assertLogicProgrammingExpressionIsCorrect(List.of(Atom.of("a"))) {
                    sequenceOf("a").toTerm()
                }
            }

            test("to-term-block") {
                assertLogicProgrammingExpressionIsCorrect(Block.of(Atom.of("a"))) {
                    blockOf("a")
                }
            }

            test("to-term-tuple") {
                assertLogicProgrammingExpressionIsCorrect(Tuple.of(Atom.of("a"), Atom.of("b"))) {
                    tupleOf("a", "b")
                }
            }

            test("directive-of-plain-values") {
                assertLogicProgrammingExpressionIsCorrect(Directive.of(Atom.of("a"), Atom.of("b"))) {
                    directiveOf("a", "b")
                }
            }

            test("logic-list-without-a-tail") {
                assertLogicProgrammingExpressionIsCorrect(List.of(Atom.of("a"), Atom.of("b"))) {
                    logicList("a", "b")
                }
            }
        }
    })
