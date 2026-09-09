package it.unibo.tuprolog.ui.swing

internal val TIMEOUT_VALUES: List<Long> =
    buildList {
        addAll((0..10).map { it * 100L })
        var milliseconds = 2_000L
        while (milliseconds <= 3_600_000L) {
            add(milliseconds)
            milliseconds += 1_000L
        }
        milliseconds = 3_660_000L
        while (milliseconds <= 86_400_000L) {
            add(milliseconds)
            milliseconds += 60_000L
        }
        milliseconds = 90_000_000L
        while (milliseconds <= 604_800_000L) {
            add(milliseconds)
            milliseconds += 3_600_000L
        }
    }

internal fun timeoutForSliderPosition(position: Int): Long =
    TIMEOUT_VALUES[position.coerceIn(0, TIMEOUT_VALUES.lastIndex)]

internal fun sliderPositionForTimeout(milliseconds: Long): Int =
    TIMEOUT_VALUES.indexOfLast { it <= milliseconds.coerceAtLeast(0L) }.coerceAtLeast(0)
