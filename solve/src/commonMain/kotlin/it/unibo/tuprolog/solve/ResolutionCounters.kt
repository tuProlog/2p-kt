package it.unibo.tuprolog.solve

import kotlin.js.JsName

/**
 * Opt-in counts of the logical work a resolution does (#1010), filled by solvers that support them when passed via
 * [SolveOptions.setCounters]. They count logical events (calls, alternatives, choice points), not the internal steps of
 * a particular engine, so engines can be compared on the same work. A solver that does not support counting leaves
 * them at zero; one that does not have a notion (e.g. no trail) leaves that count at zero.
 *
 * shortcut: nested resolutions (`findall/3`, `bagof/3`, ...) are not counted, as their options don't carry these
 * counters; add an `ExecutionContext` accessor if benchmarks need them.
 *
 * Not thread-safe: use one instance per resolution, or per sequence of resolutions run one after the other.
 */
class ResolutionCounters {
    /** Predicate calls: each goal that selects a rule-defined predicate or a primitive. */
    @JsName("inferences")
    var inferences: Long = 0
        private set

    /** Clause or primitive alternatives tried, the first one of each call included. */
    @JsName("alternatives")
    var alternatives: Long = 0
        private set

    /** Choice points created, i.e. calls left with alternatives still to try. */
    @JsName("choicePoints")
    var choicePoints: Long = 0
        private set

    /** The most choice points open at once, or zero if the solver can't tell. */
    @JsName("maxChoicePoints")
    var maxChoicePoints: Long = 0
        private set

    /** The deepest call nesting reached. */
    @JsName("maxDepth")
    var maxDepth: Long = 0
        private set

    /** Bindings recorded for undoing on backtracking. */
    @JsName("trailEntries")
    var trailEntries: Long = 0
        private set

    /** Public terms built from internal engine state (solutions, requests, errors, views). */
    @JsName("reifications")
    var reifications: Long = 0
        private set

    /** Records a predicate call at nesting [depth]. */
    @JsName("countInference")
    fun countInference(depth: Int) {
        inferences++
        if (depth > maxDepth) maxDepth = depth.toLong()
    }

    /** Records an alternative being tried. */
    @JsName("countAlternative")
    fun countAlternative() {
        alternatives++
    }

    /** Records a choice point created when [open] choice points (this one included) are open, `0` if unknown. */
    @JsName("countChoicePoint")
    fun countChoicePoint(open: Int) {
        choicePoints++
        if (open > maxChoicePoints) maxChoicePoints = open.toLong()
    }

    /** Records [count] trail entries. */
    @JsName("countTrailEntries")
    fun countTrailEntries(count: Int = 1) {
        trailEntries += count
    }

    /** Records a term built for observation. */
    @JsName("countReification")
    fun countReification() {
        reifications++
    }

    /** Sets every count back to zero. */
    @JsName("reset")
    fun reset() {
        inferences = 0
        alternatives = 0
        choicePoints = 0
        maxChoicePoints = 0
        maxDepth = 0
        trailEntries = 0
        reifications = 0
    }

    override fun toString(): String =
        "ResolutionCounters(inferences=$inferences, alternatives=$alternatives, choicePoints=$choicePoints, " +
            "maxChoicePoints=$maxChoicePoints, maxDepth=$maxDepth, trailEntries=$trailEntries, " +
            "reifications=$reifications)"
}
