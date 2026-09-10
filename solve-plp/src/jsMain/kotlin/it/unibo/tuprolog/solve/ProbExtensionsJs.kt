/**
 * JS-only top-level wrappers around the `commonMain` extension properties from `ProbExtensions.kt`
 * (`Taggable.probability`, `SolveOptions.isProbabilistic`, `Solution.hasBinaryDecisionDiagram` and
 * `Solution.binaryDecisionDiagram`), exposed as plain functions so JavaScript callers — for whom
 * Kotlin extension properties aren't idiomatically callable — can still read them, e.g.
 * `probability(fact)` instead of `fact.probability`.
 */
package it.unibo.tuprolog.solve

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Taggable

/** JS-callable equivalent of [Taggable.probability]. */
@JsName("probability")
fun <T : Taggable<T>, U : T> probability(taggable: U): Double = taggable.probability

/** JS-callable equivalent of [SolveOptions.isProbabilistic]. */
@JsName("isProbabilistic")
fun isProbabilistic(solveOptions: SolveOptions): Boolean = solveOptions.isProbabilistic

/** JS-callable equivalent of [Solution.hasBinaryDecisionDiagram]. */
@JsName("hasBinaryDecisionDiagram")
fun hasBinaryDecisionDiagram(solution: Solution): Boolean = solution.hasBinaryDecisionDiagram

/** JS-callable equivalent of [Solution.binaryDecisionDiagram]. */
@JsName("binaryDecisionDiagram")
fun binaryDecisionDiagram(solution: Solution): BinaryDecisionDiagram<out Term>? = solution.binaryDecisionDiagram
