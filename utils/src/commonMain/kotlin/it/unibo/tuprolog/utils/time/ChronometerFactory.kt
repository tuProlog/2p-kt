package it.unibo.tuprolog.utils.time

import it.unibo.tuprolog.utils.time.impl.AdaptiveChronometer
import it.unibo.tuprolog.utils.time.impl.DirectChronometer
import it.unibo.tuprolog.utils.time.impl.SimulatedChronometer
import kotlin.jvm.JvmStatic
import kotlin.time.Duration

/**
 * Creates a fresh, running [Chronometer] for each timed activity. Unlike a [Chronometer], a factory is stateless
 * configuration, so it can be shared freely, e.g. stored in default options.
 */
fun interface ChronometerFactory {
    /** Creates a [Chronometer], started now, whose budget is [timeout] ([Duration.INFINITE] for none). */
    fun create(timeout: Duration): Chronometer

    companion object {
        /**
         * Chronometers reading the clock of [timeline] at every [Chronometer.leap]: the reference implementation,
         * as precise as the clock, but paying for one clock reading per step.
         */
        @JvmStatic
        fun direct(timeline: Timeline = Timeline.system): ChronometerFactory =
            ChronometerFactory { DirectChronometer(timeline, it) }

        /**
         * Chronometers reading the clock of [timeline] only every so many [Chronometer.leap]s, as estimated by
         * [policy] from the average duration of past leaps, and never when the timeout is infinite. Expiration
         * may thus be detected slightly after it happens, never before.
         */
        @JvmStatic
        fun adaptive(
            policy: AdaptiveSamplingPolicy = AdaptiveSamplingPolicy.DEFAULT,
            timeline: Timeline = Timeline.system,
        ): ChronometerFactory = ChronometerFactory { AdaptiveChronometer(timeline, it, policy) }

        /**
         * Deterministic chronometers on a simulated [timeline], where each [Chronometer.leap] advances time by
         * [durationPerLeap] before checking it: e.g., with `1.milliseconds`, a never-ending activity with a
         * `20.milliseconds` timeout expires on its 21st leap, however fast the host machine is.
         */
        @JvmStatic
        fun simulated(
            timeline: ManualTimeline = Timeline.manual(),
            durationPerLeap: Duration = Duration.ZERO,
        ): ChronometerFactory = ChronometerFactory { SimulatedChronometer(timeline, it, durationPerLeap) }

        /** Child scopes of [parent] (see [Chronometer.childScope]), bounded by the requested timeout. */
        @JvmStatic
        fun childrenOf(parent: Chronometer): ChronometerFactory = ChronometerFactory { parent.childScope(it) }

        /** The factory used when none is configured. */
        @JvmStatic
        val default: ChronometerFactory
            get() = direct()
    }
}
