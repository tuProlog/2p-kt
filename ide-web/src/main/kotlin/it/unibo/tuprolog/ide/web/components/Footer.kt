package it.unibo.tuprolog.ide.web.components

import csstype.AlignItems
import csstype.AlignItems.Companion.center
import csstype.JustifyContent
import csstype.em
import csstype.px
import emotion.css.css
import emotion.react.css
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

    Grid {
        container = true
        direction = responsive(GridDirection.row)
        sx {
            justifyContent = JustifyContent.spaceBetween
            alignItems = center
        }

        Grid {
            item = true
            p {
                +"Stato: IDLE"
            }
        }

        Grid {
            item = true

            Grid {
                container = true
                direction = responsive(GridDirection.row)
                sx { alignItems = center }

                Grid {
                    item = true
                    Typography {
                        +"Timeout: ${timeoutDuration.toDuration(DurationUnit.MILLISECONDS)}"
                    }
                }

                Grid {
                    item = true
                    css {
                        width = 300.px
                        paddingLeft = 2.em
                    }

//                    Slider {
//                        value = timeoutDuration
//                        step = MYSTEP
//                        onChange = { _, newValue, _ -> timeoutDuration = newValue as Int }
//                        max = MYMAX
//                        min = MYMIN
//
//
//                    }
                }
            }
        }
    }
}
