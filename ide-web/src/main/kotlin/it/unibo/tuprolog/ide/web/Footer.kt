package it.unibo.tuprolog.ide.web

import Counter
import Increase
import State
import csstype.AlignItems
import csstype.JustifyContent
import csstype.em
import mui.material.GridDirection
import it.unibo.tuprolog.ide.web.utils.xs
import mui.material.BottomNavigation
import mui.material.Button
import mui.material.Slider
import mui.material.Grid
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import myStore
import react.FC
import react.Props
import react.dom.html.ReactHTML.p
import react.redux.useDispatch
import react.redux.useSelector
import react.useState
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val Footer = FC<Props> {
    var state by useState(0)
    var timeoutDuration by useState(10)

    val c2 = myStore.getState().counter.count
    val c3 = c2 + 1

    val dispatcher = useDispatch<Increase, Counter>()
    val selector = useSelector<State, Int> { s -> s.counter.count }

    BottomNavigation {
        showLabels = true
        value = state
        onChange = { _, value -> state = value }


        Typography {
            +"$c2"
        }
        Typography {
            +"$c3"
        }
        Typography {
            +"$selector"
        }
        Button {
            onClick = {
                dispatcher(Increase())
            }
            +"test"
        }

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
