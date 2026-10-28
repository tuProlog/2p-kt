package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Equation
import kotlin.test.assertTrue
import kotlin.test.fail
import it.unibo.tuprolog.core.List.Companion as LogicList

/**
 * Utils singleton for testing [Equation]
 *
 * @author Enrico
 */
internal object EquationUtils {
    /** A named equation between [lhs] and [rhs] */
    internal data class EquationCase<out L : Term, out R : Term>(
        val id: String,
        val lhs: L,
        val rhs: R,
    ) {
        val pair: Pair<L, R>
            get() = lhs to rhs

        fun swapped(): EquationCase<R, L> = EquationCase(id, rhs, lhs)
    }

    private fun <L : Term, R : Term> named(
        id: String,
        pair: Pair<L, R>,
    ) = EquationCase(id, pair.first, pair.second)

    /** A list of equations that should immediately be interpreted as Identities, without deeper exploration */
    internal val shallowIdentityEquations by lazy {
        listOf(
            named("identity/true", Truth.TRUE to Truth.TRUE),
            named("identity/false", Truth.FALSE to Truth.FALSE),
            named("identity/fail", Truth.FAIL to Truth.FAIL),
            named("identity/empty-list", Empty.list() to Empty.list()),
            named("identity/empty-block", Empty.block() to Empty.block()),
            named("identity/atom", Atom.of("a") to Atom.of("a")),
            named("identity/uppercase-atom", Atom.of("X") to Atom.of("X")),
            named("identity/real", Real.of("1.5") to Real.of("1.5")),
            named("identity/real-with-trailing-zeros", Real.of("1.5") to Real.of("1.500")),
            named("identity/integer", Integer.of(0) to Integer.of(0)),
            named("identity/anonymous-var", Var.anonymous().let { it to it }),
            named("identity/same-named-var", Scope.empty { varOf("X") to varOf("X") }),
        )
    }

    /** A list of equations, that at last should be interpreted as Identities, exploring them in deep */
    internal val deepIdentityEquations by lazy {
        listOf(
            named(
                "identity/list-with-var",
                Scope.empty {
                    logicListOf(atomOf("a"), varOf("V")) to
                        logicListOf(atomOf("a"), varOf("V"))
                },
            ),
            named(
                "identity/partial-list",
                Scope.empty {
                    logicListFrom(arrayListOf(atomOf("a")), last = varOf("V")) to
                        logicListFrom(arrayListOf(atomOf("a")), last = varOf("V"))
                },
            ),
            named(
                "identity/block-with-anonymous-var",
                Var.anonymous().let { anonymous ->
                    Block.of(Numeric.of(1.5), anonymous) to Block.of(Numeric.of(1.5), anonymous)
                },
            ),
            named("identity/struct-with-var", Scope.empty { structOf("f", varOf("A")) to structOf("f", varOf("A")) }),
            named(
                "identity/fact",
                Scope.empty {
                    factOf(structOf("aa", varOf("A"))) to
                        factOf(structOf("aa", varOf("A")))
                },
            ),
            named(
                "identity/directive",
                Scope.empty {
                    directiveOf(atomOf("here"), structOf("f", Truth.TRUE)) to
                        directiveOf(atomOf("here"), structOf("f", Truth.TRUE))
                },
            ),
            named(
                "identity/rule",
                Scope.empty {
                    ruleOf(Struct.fold("k", varOf("A"), varOf("A")), Truth.FALSE) to
                        ruleOf(Struct.fold("k", varOf("A"), varOf("A")), Truth.FALSE)
                },
            ),
            named(
                "identity/indicator",
                Scope.empty {
                    indicatorOf(atomOf("ciao"), varOf("A")) to indicatorOf(atomOf("ciao"), varOf("A"))
                },
            ),
        )
    }

    /** A list of equations that, at last, should be interpreted as Identities */
    internal val allIdentityEquations by lazy { shallowIdentityEquations + deepIdentityEquations }

    /** A list of equations that should be interpreted as Assignments (left item is a Var always) */
    internal val assignmentEquations by lazy {
        listOf(
            named("assignment/var-to-true", Var.of("X") to Truth.TRUE),
            named("assignment/var-to-false", Var.of("X") to Truth.FALSE),
            named("assignment/var-to-fail", Var.of("X") to Truth.FAIL),
            named("assignment/var-to-empty-list", Var.of("X") to Empty.list()),
            named("assignment/var-to-empty-block", Var.of("X") to Empty.block()),
            named("assignment/var-to-atom", Var.of("X") to Atom.of("a")),
            named("assignment/var-to-uppercase-atom", Var.of("X") to Atom.of("X")),
            named("assignment/var-to-same-named-var", Var.of("X") to Var.of("X")),
            named("assignment/anonymous-to-real", Var.anonymous() to Real.of("1.5")),
            named("assignment/anonymous-to-integer", Var.anonymous() to Integer.of(0)),
            named("assignment/anonymous-to-struct", Var.anonymous() to Struct.of("f", Var.of("A"))),
            named("assignment/anonymous-to-fact", Var.anonymous() to Fact.of(Struct.of("aa", Var.of("A")))),
            named(
                "assignment/anonymous-to-directive",
                Var.anonymous() to Directive.of(Atom.of("here"), Struct.of("f", Truth.TRUE)),
            ),
            named(
                "assignment/anonymous-to-rule",
                Var.anonymous() to Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.FALSE),
            ),
            named("assignment/anonymous-to-anonymous", Var.anonymous() to Var.anonymous()),
        )
    }

    /** The same equations present in [assignmentEquations] but equations on even positions are swapped: Term to Var */
    internal val assignmentEquationsShuffled by lazy {
        assignmentEquations.mapIndexed { i, case ->
            // if rhs Term is variable, do not shuffle! Because will _not_ be automatically swapped back like others
            if (i % 2 == 0 || case.rhs.isVar) case else case.swapped()
        }
    }

    /** A list of equations that should be interpreted as Comparisons */
    internal val comparisonEquations by lazy {
        listOf(
            named("comparison/struct", Struct.of("f", Var.of("A")) to Struct.of("f", Var.of("B"))),
            named("comparison/fact", Fact.of(Struct.of("aa", Var.of("A"))) to Fact.of(Struct.of("aa", Var.of("B")))),
            named(
                "comparison/directive",
                Directive.of(Atom.of("here"), Struct.of("f", Var.of("A"))) to
                    Directive.of(Atom.of("here"), Struct.of("f", Var.of("B"))),
            ),
            named(
                "comparison/rule",
                Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.FALSE) to
                    Rule.of(Struct.fold("k", Var.of("B"), Var.of("C")), Truth.FALSE),
            ),
        )
    }

    /** A list of equations that should immediately be interpreted as Contradictions, without deeper exploration */
    internal val shallowContradictionEquations by lazy {
        listOf(
            named("contradiction/true-false", Truth.TRUE to Truth.FALSE),
            named("contradiction/false-true", Truth.FALSE to Truth.TRUE),
            named("contradiction/true-fail", Truth.TRUE to Truth.FAIL),
            named("contradiction/fail-true", Truth.FAIL to Truth.TRUE),
            named("contradiction/fail-false", Truth.FAIL to Truth.FALSE),
            named("contradiction/false-fail", Truth.FALSE to Truth.FAIL),
            named("contradiction/empty-list-empty-block", Empty.list() to Empty.block()),
            named("contradiction/empty-block-empty-list", Empty.block() to Empty.list()),
            named("contradiction/different-atoms", Atom.of("a") to Atom.of("b")),
            named("contradiction/different-uppercase-atoms", Atom.of("X") to Atom.of("Y")),
            named("contradiction/different-reals", Real.of("1.5") to Real.of("1.3")),
            named("contradiction/different-integers", Integer.of(0) to Integer.of(1)),
            named("contradiction/real-integer", Real.of(1.0) to Integer.of(1)),
        )
    }

    /** A list of equations, that at last should be interpreted as Contradictions, exploring them in deep */
    internal val deepContradictionEquations by lazy {
        listOf(
            named(
                "contradiction/different-list-heads",
                LogicList.of(Atom.of("b"), Var.of("V")) to LogicList.of(Atom.of("a"), Var.of("V")),
            ),
            named(
                "contradiction/different-partial-list-heads",
                LogicList.from(listOf(Atom.of("a")), last = Var.of("V")) to
                    LogicList.from(listOf(Atom.of("b")), last = Var.of("V")),
            ),
            named(
                "contradiction/different-block-items",
                Block.of(Real.of(1.5), Var.anonymous()) to Block.of(Integer.of(1), Var.anonymous()),
            ),
            named("contradiction/different-struct-args", Struct.of("f", Atom.of("A")) to Struct.of("f", Atom.of("B"))),
            named(
                "contradiction/different-fact-arities",
                Fact.of(Struct.of("aa", Atom.of("A"))) to Fact.of(Struct.of("aa", Var.of("A"), Var.of("A"))),
            ),
            named(
                "contradiction/different-directive-arities",
                Directive.of(Atom.of("here"), Struct.of("f", Truth.TRUE)) to
                    Directive.of(Atom.of("here"), Struct.of("f", Truth.TRUE, Atom.of("extra"))),
            ),
            named(
                "contradiction/different-rule-functors",
                Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.FAIL) to
                    Rule.of(Struct.fold("different", Var.of("A"), Var.of("A")), Truth.FAIL),
            ),
        )
    }

    /** A list of equations that, at last, should be interpreted as Contradictions */
    internal val allContradictionEquations by lazy { shallowContradictionEquations + deepContradictionEquations }

    /** Mixed list of all types of equations, even with deep nested [Term]s, without shuffled assignments */
    internal val mixedAllEquations by lazy {
        allIdentityEquations + assignmentEquations + comparisonEquations + allContradictionEquations
    }

    /** Mixed list of different types of equations, whose type can be recognized from their shallow */
    internal val mixedShuffledShallowEquations by lazy {
        shallowIdentityEquations + assignmentEquationsShuffled + comparisonEquations + shallowContradictionEquations
    }

    /** Mixed list of all types of equations, even with deep nested [Term]s and shuffled assignments */
    internal val mixedShuffledAllEquations by lazy {
        allIdentityEquations + assignmentEquationsShuffled + comparisonEquations + allContradictionEquations
    }

    /** Asserts that all given equations are Identities instances */
    internal fun <T : Equation> assertAllIdentities(equationSequence: Sequence<T>) =
        assertTrue("${equationSequence.toList()} all Identities") {
            equationSequence.all { it is Equation.Identity }
        }

    /** Asserts that there's no Identity in given equation sequence */
    internal fun <T : Equation> assertNoIdentities(equationSequence: Sequence<T>) =
        assertTrue("${equationSequence.toList()} no Identity") {
            equationSequence.none { it is Equation.Identity }
        }

    /** Asserts that there's at least one Contradiction in given equation sequence */
    internal fun <T : Equation> assertAnyContradiction(equationSequence: Sequence<T>) =
        assertTrue("${equationSequence.toList()} at least one Contradiction") {
            equationSequence.any { it is Equation.Contradiction }
        }

    /** Asserts that there's no Comparison in given equation sequence */
    internal fun <T : Equation> assertNoComparisons(equationSequence: Sequence<T>) =
        assertTrue("${equationSequence.toList()} no Comparison") {
            equationSequence.none { it is Equation.Comparison }
        }

    /** Asserts that there's at least one Assignment in given equation sequence */
    internal fun <T : Equation> assertAnyAssignment(equationSequence: Sequence<T>) =
        assertTrue("${equationSequence.toList()} at least one Assignment") {
            equationSequence.any { it is Equation.Assignment }
        }

    /** A function to count in how many equations will be transformed a term */
    internal fun countDeepGeneratedEquations(term: Term): Int =
        when (term) {
            is Var -> 1
            is Constant -> 1
            is Struct -> term.argsSequence.sumOf { countDeepGeneratedEquations(it) }
            else -> fail("Should never be there")
        }
}
