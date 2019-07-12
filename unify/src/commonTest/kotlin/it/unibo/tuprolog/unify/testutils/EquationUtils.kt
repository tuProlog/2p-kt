package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Equation
import kotlin.test.assertTrue

/**
 * Utils singleton for testing [Equation]
 *
 * @author Enrico
 */
internal object EquationUtils {

    /** A list of equations that should immediately be interpreted as Identities, without deeper exploration */
    internal val shallowIdentityEquations by lazy {
        listOf(
                Truth.`true`() to Truth.`true`(),
                Truth.fail() to Truth.fail(),
                Empty.list() to Empty.list(),
                Empty.set() to Empty.set(),
                Atom.of("a") to Atom.of("a"),
                Atom.of("X") to Atom.of("X"),
                Real.of("1.5") to Real.of("1.5"),
                Integer.of(0) to Integer.of(0),
                Var.anonymous() to Var.anonymous(),
                Var.of("X") to Var.of("X")
        )
    }

    /** A list of equations, that at last should be interpreted as Identities, exploring them in deep */
    internal val deepIdentityEquations by lazy {
        listOf(
                Struct.of("f", Var.of("A")) to Struct.of("f", Var.of("A")),
                Fact.of(Struct.of("aa", Var.of("A"))) to Fact.of(Struct.of("aa", Var.of("A"))),
                Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`())) to
                        Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`())),
                Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.fail()) to
                        Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.fail())
        )
    }

    /** A list of equations that, at last, should be interpreted as Identities */
    internal val allIdentityEquations by lazy { shallowIdentityEquations + deepIdentityEquations }

    /** A list of equations that should be interpreted as Assignments (left item is a Var always) */
    internal val assignmentEquations by lazy {
        listOf(
                Var.of("X") to Truth.`true`(),
                Var.of("X") to Truth.fail(),
                Var.of("X") to Empty.list(),
                Var.of("X") to Empty.set(),
                Var.of("X") to Atom.of("a"),
                Var.of("X") to Atom.of("X"),
                Var.anonymous() to Real.of("1.5"),
                Var.anonymous() to Integer.of(0),
                Var.anonymous() to Struct.of("f", Var.of("A")),
                Var.anonymous() to Fact.of(Struct.of("aa", Var.of("A"))),
                Var.anonymous() to Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`())),
                Var.anonymous() to Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.fail())
        )
    }

    /** The same equations present in [assignmentEquations] but equations on even positions are swapped: Term to Var */
    internal val assignmentEquationsShuffled by lazy {
        assignmentEquations.mapIndexed { i, (variable, term) ->
            if (i % 2 == 0)
                variable to term
            else
                term to variable
        }
    }

    /** A list of equations that should be interpreted as Comparisons */
    internal val comparisonEquations by lazy {
        listOf(
                Struct.of("f", Var.of("A")) to Struct.of("f", Var.of("B")),
                Fact.of(Struct.of("aa", Var.of("A"))) to Fact.of(Struct.of("aa", Var.of("B"))),
                Directive.of(Atom.of("here"), Struct.of("f", Var.of("A"))) to
                        Directive.of(Atom.of("here"), Struct.of("f", Var.of("B"))),
                Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.fail()) to
                        Rule.of(Struct.fold("k", Var.of("B"), Var.of("C")), Truth.fail())
        )
    }

    /** A list of equations that should immediately be interpreted as Contradictions, without deeper exploration */
    internal val shallowContradictionEquations by lazy {
        listOf(
                Truth.`true`() to Truth.fail(),
                Truth.fail() to Truth.`true`(),
                Empty.list() to Empty.set(),
                Empty.set() to Empty.list(),
                Atom.of("a") to Atom.of("b"),
                Atom.of("X") to Atom.of("Y"),
                Real.of("1.5") to Real.of("1.3"),
                Integer.of(0) to Integer.of(1)
        )
    }

    /** A list of equations, that at last should be interpreted as Contradictions, exploring them in deep */
    internal val deepContradictionEquations by lazy {
        listOf(
                Struct.of("f", Atom.of("A")) to Struct.of("f", Atom.of("B")),
                Fact.of(Struct.of("aa", Atom.of("A"))) to Fact.of(Struct.of("aa", Var.of("A"), Var.of("A"))),
                Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`())) to
                        Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`(), Atom.of("extra"))),
                Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.fail()) to
                        Rule.of(Struct.fold("different", Var.of("A"), Var.of("A")), Truth.fail())
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
    internal fun <T : Equation<*, *>> assertAllIdentities(equationSequence: Sequence<T>) =
            assertTrue("${equationSequence.toList()} all Identities") {
                equationSequence.all { it is Equation.Identity<*> }
            }

    /** Asserts that there's no Identity in given equation sequence */
    internal fun <T : Equation<*, *>> assertNoIdentities(equationSequence: Sequence<T>) =
            assertTrue("${equationSequence.toList()} no Identity") {
                equationSequence.none { it is Equation.Identity<*> }
            }

    /** Asserts that there's at least one Contradiction in given equation sequence */
    internal fun <T : Equation<*, *>> assertAnyContradiction(equationSequence: Sequence<T>) =
            assertTrue("${equationSequence.toList()} at least one Contradiction") {
                equationSequence.any { it is Equation.Contradiction<*, *> }
            }

    /** Asserts that there's no Comparison in given equation sequence */
    internal fun <T : Equation<*, *>> assertNoComparisons(equationSequence: Sequence<T>) =
            assertTrue("${equationSequence.toList()} no Comparison") {
                equationSequence.none { it is Equation.Comparison<*, *> }
            }

    /** Asserts that there's at least one Assignment in given equation sequence */
    internal fun <T : Equation<*, *>> assertAnyAssignment(equationSequence: Sequence<T>) =
            assertTrue("${equationSequence.toList()} at least one Assignment") {
                equationSequence.any { it is Equation.Assignment<*, *> }
            }

}
