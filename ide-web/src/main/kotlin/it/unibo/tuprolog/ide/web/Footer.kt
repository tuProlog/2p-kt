package it.unibo.tuprolog.ide.web

import csstype.AlignItems
import csstype.JustifyContent
import csstype.em
import it.unibo.tuprolog.ide.web.utils.xs
import mui.material.*
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.p
import react.useState
import kotlin.time.DurationUnit
import kotlin.time.toDuration


val Footer = FC<Props> {
    var state by useState(0)
    var timeoutDuration by useState(10)

    BottomNavigation {
        showLabels = true
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
                xs = 5

                Grid {
                    container = true
                    direction = responsive(GridDirection.row)

                    Grid {
                        item = true
                        xs = 5
                        Typography {
                            +"Timeout: ${timeoutDuration.toDuration(DurationUnit.MILLISECONDS)}"
                        }
                    }

                    Grid {
                        item = true
                        xs = 7
                        Slider {
                            value = timeoutDuration
                            step = 10
                            onChange = { _, newValue, _ -> timeoutDuration = newValue as Int }
                            max = 1000
                            min = 1
                        }
                    }
                }
            }
        }
    }
}