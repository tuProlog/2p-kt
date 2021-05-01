package it.unibo.tuprolog.bdd

internal actual fun <E : Comparable<E>>
createDefaultBinaryDecisionDiagramBuilder():
    BinaryDecisionDiagramBuilder<E> {
        return BinaryDecisionDiagramBuilder.simpleOf()
    }
