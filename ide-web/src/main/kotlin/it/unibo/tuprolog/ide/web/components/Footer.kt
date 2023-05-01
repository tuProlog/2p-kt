package it.unibo.tuprolog.ide.web.components

import csstype.AlignItems
import csstype.JustifyContent
import csstype.em
import mui.material.GridDirection
import it.unibo.tuprolog.ide.web.utils.xs
import mui.material.BottomNavigation
import mui.material.Slider
import mui.material.Grid
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.p
import react.useState
import kotlin.time.DurationUnit
import kotlin.time.toDuration

const val MYTIMEOUTDURATION = 10
const val MYSTATE = 0
const val MYXS = 5
const val MYXSGRID = 7
const val MYMIN = 1
const val MYMAX = 1000
const val MYSTEP = 10

val Footer = FC<Props> {

    var state by useState(MYSTATE)
    var timeoutDuration by useState(MYTIMEOUTDURATION)

    BottomNavigation {
        value = state
        onChange = { _, value -> state = value }

        Grid {
            container = true

            direction = responsive(GridDirection.row)
            sx {
                justifyContent = JustifyContent.spaceBetween
                alignItems = AlignItems.center
                padding = 2.em
            }

            Grid {
                item = true
                p {
                    +"Stato: IDLE"
                }
            }
            Grid {
                item = true
                xs = MYXS

                Grid {
                    container = true
                    direction = responsive(GridDirection.row)

                    Grid {
                        item = true
                        xs = MYXS
                        Typography {
                            +"Timeout: ${timeoutDuration.toDuration(DurationUnit.MILLISECONDS)}"
                        }
                    }

                    Grid {
                        item = true
                        xs = MYXSGRID
                        Slider {
                            value = timeoutDuration
                            step = MYSTEP
                            onChange = { _, newValue, _ -> timeoutDuration = newValue as Int }
                            max = MYMAX
                            min = MYMIN
                        }
                    }
                }
            }
        }
    }
}
