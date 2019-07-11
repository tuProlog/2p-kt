package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Equation

/**
 * Utils singleton for testing [Equation]
 *
 * @author Enrico
 */
internal object EquationUtils {

    /** A list of equations that should be interpreted as Identities */
    internal val identityEquations by lazy {
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
                Var.of("X") to Var.of("X"),
                Struct.of("f", Var.of("A")) to Struct.of("f", Var.of("A")),
                Fact.of(Struct.of("aa", Var.of("A"))) to Fact.of(Struct.of("aa", Var.of("A"))),
                Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`())) to
                        Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`())),
                Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.fail()) to
                        Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.fail())
        )
    }

    /** A list of equations that should be interpreted as Assignments */
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
        assignmentEquations.filterIndexed { i, _ -> i % 2 == 0 }
                .map { (variable, term) -> term to variable }
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

    /** A list of equations that should be interpreted as Contradictions */
    internal val contradictionEquations by lazy {
        listOf(
                Truth.`true`() to Truth.fail(),
                Truth.fail() to Truth.`true`(),
                Empty.list() to Empty.set(),
                Empty.set() to Empty.list(),
                Atom.of("a") to Atom.of("b"),
                Atom.of("X") to Atom.of("Y"),
                Real.of("1.5") to Real.of("1.3"),
                Integer.of(0) to Integer.of(1),
                Struct.of("f", Atom.of("A")) to Struct.of("f", Atom.of("B")),
                Fact.of(Struct.of("aa", Atom.of("A"))) to Fact.of(Struct.of("aa", Var.of("A"), Var.of("A"))),
                Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`())) to
                        Directive.of(Atom.of("here"), Struct.of("f", Truth.`true`(), Atom.of("extra"))),
                Rule.of(Struct.fold("k", Var.of("A"), Var.of("A")), Truth.fail()) to
                        Rule.of(Struct.fold("different", Var.of("A"), Var.of("A")), Truth.fail())
        )
    }

}
