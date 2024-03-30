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
    /** A list of equations that should immediately be interpreted as Identities, without deeper exploration */
    internal val shallowIdentityEquations by lazy {
        listOf(
            Truth.TRUE to Truth.TRUE,
            Truth.FALSE to Truth.FALSE,
            Truth.FAIL to Truth.FAIL,
            Empty.list() to Empty.list(),
            Empty.block() to Empty.block(),
            Atom.of("a") to Atom.of("a"),
            Atom.of("X") to Atom.of("X"),
            Real.of("1.5") to Real.of("1.5"),
            Real.of("1.5") to Real.of("1.500"),
            Integer.of(0) to Integer.of(0),
            Var.anonymous().let { it to it },
            Scope.empty { varOf("X") to varOf("X") },
        )
    }

    /** A list of equations, that at last should be interpreted as Identities, exploring them in deep */
    internal val deepIdentityEquations by lazy {
        listOf(
            Scope.empty { logicListOf(atomOf("a"), varOf("V")) to logicListOf(atomOf("a"), varOf("V")) },
            Scope.empty {
                logicListFrom(arrayListOf(atomOf("a")), last = varOf("V")) to
                    logicListFrom(arrayListOf(atomOf("a")), last = varOf("V"))
            },
            Var.anonymous().let { anonymous ->
                Block.of(Numeric.of(1.5), anonymous) to Block.of(Numeric.of(1.5), anonymous)
            },
            Scope.empty { structOf("f", varOf("A")) to structOf("f", varOf("A")) },
            Scope.empty { factOf(structOf("aa", varOf("A"))) to factOf(structOf("aa", varOf("A"))) },
            Scope.empty {
                directiveOf(atomOf("here"), structOf("f", Truth.TRUE)) to
                    directiveOf(atomOf("here"), structOf("f", Truth.TRUE))
            },
            Scope.empty {
                ruleOf(Struct.fold("k", varOf("A"), varOf("A")), Truth.FALSE) to
                    ruleOf(Struct.fold("k", varOf("A"), varOf("A")), Truth.FALSE)
            },
            Scope.empty {
                indicatorOf(atomOf("ciao"), varOf("A")) to indicatorOf(atomOf("ciao"), varOf("A"))
            },
        )
    }

    /** A list of equations that, at last, should be interpreted as Identities */
    internal val allIdentityEquations by lazy { shallowIdentityEquations + deepIdentityEquations }

    /** A list of equations that should be interpreted as Assignments (left item is a Var always) */
    internal val assignmentEquations by lazy {
        listOf(
            Var.of("X") to Truth.TRUE,
            Var.of("X") to Truth.FALSE,
            Var.of("X") to Truth.FAIL,
            Var.of("X") to Empty.list(),
            Var.of("X") to Empty.block(),
            Var.of("X") to Atom.of("a"),
            Var.of("X") to Atom.of("X"),
            Var.of("X") to Var.of("X"),
            Var.anonymous() to Real.of("1.5"),
            Var.anonymous() to Integer.of(0),
            Var.anonymous() to Struct.of("f", Var.of("A")),
            Var.anonymous() to Fact.of(Struct.of("aa", Var.of("A"))),
            Var.anonymous() to Directive.of(Atom.of("here"), Struct.of("f", Truth.TRUE)),
            Var.anonymous() to Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.FALSE),
            Var.anonymous() to Var.anonymous(),
        )
    }

    /** The same equations present in [assignmentEquations] but equations on even positions are swapped: Term to Var */
    internal val assignmentEquationsShuffled by lazy {
        assignmentEquations.mapIndexed { i, (variable, term) ->
            // if rhs Term is variable, do not shuffle! Because will _not_ be automatically swapped back like others
            if (i % 2 == 0 || term.isVar) {
                variable to term
            } else {
                term to variable
            }
        }
    }

    /** A list of equations that should be interpreted as Comparisons */
    internal val comparisonEquations by lazy {
        listOf(
            Struct.of("f", Var.of("A")) to Struct.of("f", Var.of("B")),
            Fact.of(Struct.of("aa", Var.of("A"))) to Fact.of(Struct.of("aa", Var.of("B"))),
            Directive.of(Atom.of("here"), Struct.of("f", Var.of("A"))) to
                Directive.of(Atom.of("here"), Struct.of("f", Var.of("B"))),
            Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.FALSE) to
                Rule.of(Struct.fold("k", Var.of("B"), Var.of("C")), Truth.FALSE),
        )
    }

    /** A list of equations that should immediately be interpreted as Contradictions, without deeper exploration */
    internal val shallowContradictionEquations by lazy {
        listOf(
            Truth.TRUE to Truth.FALSE,
            Truth.FALSE to Truth.TRUE,
            Truth.TRUE to Truth.FAIL,
            Truth.FAIL to Truth.TRUE,
            Truth.FAIL to Truth.FALSE,
            Truth.FALSE to Truth.FAIL,
            Empty.list() to Empty.block(),
            Empty.block() to Empty.list(),
            Atom.of("a") to Atom.of("b"),
            Atom.of("X") to Atom.of("Y"),
            Real.of("1.5") to Real.of("1.3"),
            Integer.of(0) to Integer.of(1),
            Real.of(1.0) to Integer.of(1),
        )
    }

    /** A list of equations, that at last should be interpreted as Contradictions, exploring them in deep */
    internal val deepContradictionEquations by lazy {
        listOf(
            LogicList.of(Atom.of("b"), Var.of("V")) to LogicList.of(Atom.of("a"), Var.of("V")),
            LogicList.from(listOf(Atom.of("a")), last = Var.of("V")) to
                LogicList.from(listOf(Atom.of("b")), last = Var.of("V")),
            Block.of(Real.of(1.5), Var.anonymous()) to Block.of(Integer.of(1), Var.anonymous()),
            Struct.of("f", Atom.of("A")) to Struct.of("f", Atom.of("B")),
            Fact.of(Struct.of("aa", Atom.of("A"))) to Fact.of(Struct.of("aa", Var.of("A"), Var.of("A"))),
            Directive.of(Atom.of("here"), Struct.of("f", Truth.TRUE)) to
                Directive.of(Atom.of("here"), Struct.of("f", Truth.TRUE, Atom.of("extra"))),
            Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.FAIL) to
                Rule.of(Struct.fold("different", Var.of("A"), Var.of("A")), Truth.FAIL),
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
