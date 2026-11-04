package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.utils.time.ChronometerFactory

/** The chronometers the benchmarks compare, by JMH `chronometer` parameter value. */
enum class ChronometerSetting(
    val id: String,
    val factory: ChronometerFactory,
) {
    /** One clock reading per resolution step. */
    DIRECT("direct", ChronometerFactory.direct()),

    /** Clock readings planned from the estimated duration of resolution steps. */
    ADAPTIVE("adaptive", ChronometerFactory.adaptive()),
    ;

    companion object {
        /** The [ChronometerSetting] whose [id] is [id]. */
        fun byId(id: String): ChronometerSetting = entries.first { it.id == id }
    }
}
