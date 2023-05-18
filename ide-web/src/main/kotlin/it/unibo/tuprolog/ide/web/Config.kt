package it.unibo.tuprolog.ide.web

data class Config(
    val sliderStep: Int = SLIDER_STEP,
    val maxSliderValue: Int = MAX_SLIDER_VALUE,
    val minSliderValue: Int = MIN_SLIDER_VALUE,
    val maxSolutions: Int = MAX_SOLUTION,
    val logEvents: Boolean = LOG_EVENTS
) {
    companion object {
        const val SLIDER_STEP = 100
        const val MAX_SLIDER_VALUE = 60000
        const val MIN_SLIDER_VALUE = 10
        const val MAX_SOLUTION = 10
        const val LOG_EVENTS = true
    }
}
val appConfig = Config()
