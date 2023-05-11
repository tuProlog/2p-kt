package it.unibo.tuprolog.ide.web.components

import AppState
import csstype.AlignItems
import csstype.AlignItems.Companion.center
import csstype.JustifyContent
import csstype.em
import csstype.px
import emotion.css.css
import emotion.react.css
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologApplication
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import mui.material.GridDirection
import it.unibo.tuprolog.ide.web.utils.xs
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.times
import it.unibo.tuprolog.ui.gui.Page
import mui.material.BaseSize
import mui.material.BottomNavigation
import mui.material.Slider
import mui.material.Grid
import mui.material.LinearProgress
import mui.material.Size
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.p
import react.redux.useDispatch
import react.redux.useSelector
import react.useState
import redux.RAction
import redux.WrapperAction
import kotlin.time.DurationUnit
import kotlin.time.toDuration

const val MYXS = 5
const val MYXSGRID = 7
const val MYMIN = 1
const val MYSTEP = 10

val Footer = FC<Props> {
    val pageStatus = useSelector<AppState, Page.Status?> { s -> s.tuProlog.pageStatus }
    val myTimeout = Page.DEFAULT_TIMEOUT
    var timeoutDuration by useState(myTimeout)

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
                        +"Timeout: ${timeoutDuration.toDuration(DurationUnit.MILLISECONDS)}"
                    }
                }

                Grid {
                    item = true
                    css {
                        width = 300.px
                        paddingLeft = 2.em
                    }

                    Slider {
                        value = 10
                        step = MYSTEP
                        size = Size.small
                        onChange = { _, newValue, _ ->
                            timeoutDuration = newValue as Long
//                            TuPrologController.application.currentPage?.timeout = 10
                        }
                        max = 60000
                        min = 10
                    }
                }
            }
        }
    }
}
