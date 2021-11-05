package it.unibo.tuprolog.solve

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Taggable

@JsName("probability")
fun <T : Taggable<T>, U : T> probability(taggable: U): Double =
    taggable.probability

@JsName("isProbabilistic")
fun isProbabilistic(solveOptions: SolveOptions): Boolean =
    solveOptions.isProbabilistic

@JsName("hasBinaryDecisionDiagram")
fun hasBinaryDecisionDiagram(solution: Solution): Boolean =
    solution.hasBinaryDecisionDiagram

@JsName("binaryDecisionDiagram")
fun binaryDecisionDiagram(solution: Solution): BinaryDecisionDiagram<out Term>? =
    solution.binaryDecisionDiagram
