package it.unibo.tuprolog.bdd.impl.utils

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

/**
 * This visitor simulates the behavior of the `when` statement for
 * simple casting purposes. By using this we avoid type checking,
 * which is unoptimized in some target platforms (JS).
 *
 * The following code snippet shows how to properly use this visitor:
 * ```
 * val castVisitor = CastVisitor<...>()
 * castVisitor.onTerminal = { it -> /* Handle terminal case */ }
 * castVisitor.onVariable = { it -> /* Handle variable case */ }
 * bdd.accept(castVisitor)
 * ```
 *
 * @author Jason Dellaluce
 * */
internal class CastVisitor<T : Comparable<T>, E> : BinaryDecisionDiagramVisitor<T, E> {
    var onTerminal: ((o: BinaryDecisionDiagram.Terminal<T>) -> E)? = null
    var onVariable: ((o: BinaryDecisionDiagram.Variable<T>) -> E)? = null

    override fun visit(node: BinaryDecisionDiagram.Terminal<T>): E = onTerminal!!(node)

    override fun visit(node: BinaryDecisionDiagram.Variable<T>): E = onVariable!!(node)
}
