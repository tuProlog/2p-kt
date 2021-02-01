package it.unibo.tuprolog.bdd

import it.unibo.tuprolog.bdd.impl.BinaryDecisionDiagramTerminalImpl
import it.unibo.tuprolog.bdd.impl.BinaryDecisionDiagramVariableImpl
import kotlin.js.JsName

/**
 * A Binary Decision Diagram (BDD) is a rooted, directed, acyclic graph, which
 * consists of several decision nodes and terminal nodes and uses the concept of
 * Shannon Expansion to represent and encode complex Boolean Formulas.
 *
 * Each node of the diagram represents a single boolean entry with variable value
 * and part of a Boolean function. [T] is the type with which a variable is represented.
 * In the context of a formula, variables for which the [compareTo] method returns 0 indicate
 * the same Boolean variable.
 *
 * Each BDD node has a directed edge to two sub-BDDs: the "high"
 * BDD that leads to a true Terminal, and the "low" BDD that
 * leads to a false Terminal.
 *
 * @author Jason Dellaluce
 * */
interface BinaryDecisionDiagram<T : Comparable<T>> {

    @JsName("accept")
    fun <E> accept(visitor: BinaryDecisionDiagramVisitor<T, E>): E

    /**
     * A [Terminal] is a BDD mode that has no edges to other BDDs, and represent
     * a non-variable known Boolean value (either True or False)
     * */
    interface Terminal<T : Comparable<T>> : BinaryDecisionDiagram<T> {
        /** Boolean value of the terminal*/
        val value: Boolean
    }

    /**
     * A [Variable] is a BDD node representing a Boolean variable.
     * */
    interface Variable<T : Comparable<T>> : BinaryDecisionDiagram<T> {
        /** [value] represents the boolean variable */
        val value: T

        /** [low] the a [BinaryDecisionDiagram] that leads to a 0-terminal (a false terminal) */
        val low: BinaryDecisionDiagram<T>

        /** [high] the a [BinaryDecisionDiagram] that leads to a 1-terminal (a true terminal) */
        val high: BinaryDecisionDiagram<T>
    }

    companion object {
        /** Creates a new [BinaryDecisionDiagram] [Variable] from the given value */
        @JsName("ofVariable")
        fun <E : Comparable<E>> ofVariable(value: E): BinaryDecisionDiagram<E> {
            return ofVariable(value, ofTerminal(false), ofTerminal(true))
        }

        /** Creates a new [BinaryDecisionDiagram] [Variable] from the given value and low-high nodes. */
        @JsName("ofVariableWithNodes")
        fun <E : Comparable<E>> ofVariable(value: E, low: BinaryDecisionDiagram<E>, high: BinaryDecisionDiagram<E>):
            BinaryDecisionDiagram<E> {
                return BinaryDecisionDiagramVariableImpl(value, low, high)
            }

        /** Creates a new [BinaryDecisionDiagram] [Terminal] from the given boolean value */
        @JsName("terminalOf")
        fun <E : Comparable<E>> ofTerminal(value: Boolean): BinaryDecisionDiagram<E> {
            return BinaryDecisionDiagramTerminalImpl(value)
        }
    }
}
