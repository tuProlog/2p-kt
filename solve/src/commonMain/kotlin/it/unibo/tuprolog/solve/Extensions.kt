@file:JvmName("Extensions")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.time.Duration

/** Extracts this [Struct] indicator and converts it to [Signature] */
@JsName("extractSignature")
fun Struct.extractSignature(): Signature = Signature.fromIndicator(indicator)!!

/**
 * Solves the goal built by [scopedContext] within a fresh, empty [Scope] (useful to build the goal [Struct] using
 * scoped variables inline), capping resolution to [maxDuration] ([Duration.INFINITE], i.e. no limit, by default).
 */
@JsName("solve")
fun Solver.solve(
    maxDuration: Duration = Duration.INFINITE,
    scopedContext: Scope.() -> Struct,
): SolutionStream = solve(scopedContext(Scope.empty()), maxDuration)

/**
 * Returns a [Sequence] equivalent to this one, but with every [Solution.Yes] whose [Solution.Yes.solvedQuery]
 * equals (structurally) one already emitted earlier in the sequence dropped. [Solution.No]/[Solution.Halt]
 * entries are never dropped. Backs the `UniqueSolutions` flag's `on` setting -- e.g. `member(X, [1, 1, 1])`
 * yields a single `yes` (`X = 1`) instead of three.
 *
 * Lazy and single-pass: safe to use on an infinite/backtracking sequence, at the cost of remembering every
 * distinct solved-query term seen so far for the lifetime of the sequence.
 */
@JsName("distinctSolutions")
fun Sequence<Solution>.distinctSolutions(): Sequence<Solution> =
    sequence {
        val seen = mutableSetOf<Struct>()
        for (solution in this@distinctSolutions) {
            if (solution.isYes) {
                if (seen.add(solution.castToYes().solvedQuery)) {
                    yield(solution)
                }
            } else {
                yield(solution)
            }
        }
    }
