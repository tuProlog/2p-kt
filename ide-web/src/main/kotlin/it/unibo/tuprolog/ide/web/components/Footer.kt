package it.unibo.tuprolog.ide.web.components

import it.unibo.tuprolog.ide.web.redux.AppState
import csstype.AlignItems.Companion.center
import csstype.JustifyContent
import csstype.em
import csstype.px
import emotion.react.css
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.times
import it.unibo.tuprolog.ui.gui.Page
import mui.material.Grid
import mui.material.GridDirection
import mui.material.LinearProgress
import mui.material.Size
import mui.material.Slider
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.p
import react.redux.useSelector
import react.useState

const val MYXS = 5
const val MYXSGRID = 7
const val MYMIN = 1
const val MYSTEP = 100

val Footer = FC<Props> {
    val pageStatus =
        useSelector<AppState, Page.Status?> { s -> s.tuProlog.currentPage?.pageStatus }
    var timeoutDuration by useState(1000)

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
                +"Stato: ${pageStatus?.name ?: "UNDEFINED"}"
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
                    if (pageStatus == Page.Status.COMPUTING)
                        LinearProgress {}
                }
                Grid {
                    item = true
                    Typography {
                        +"Timeout: $timeoutDuration ms"
                    }
                }

                Grid {
                    item = true
                    css {
                        width = 300.px
                        paddingLeft = 2.em
                    }

                    Slider {
                        value = timeoutDuration
                        step = MYSTEP
                        size = Size.small
                        onChange = { _, newValue, _ ->
                            timeoutDuration = newValue
                            TuPrologController.application.currentPage?.timeout =
                                newValue.unsafeCast<Int>() * TimeUnit.MILLIS
                        }
                        max = 60000
                        min = 10
                    }
                }
            }
        }
    }
}
