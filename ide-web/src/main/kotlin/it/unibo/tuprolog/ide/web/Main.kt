package it.unibo.tuprolog.ide.web

import csstype.AlignItems
import csstype.BackgroundColor
import csstype.Color
import csstype.FlexDirection
import csstype.FlexWrap
import csstype.JustifyContent
import csstype.Overflow
import csstype.em
import csstype.pct
import csstype.px
import csstype.vh
import csstype.vw
import emotion.react.css
import it.unibo.tuprolog.ide.web.components.TheoryEditors
import it.unibo.tuprolog.ide.web.components.Footer
import it.unibo.tuprolog.ide.web.components.Messages
import it.unibo.tuprolog.ide.web.components.NavBar
import it.unibo.tuprolog.ide.web.components.QueryEditor
import it.unibo.tuprolog.ide.web.components.SolutionsContainer
import it.unibo.tuprolog.ide.web.utils.xs
import mui.material.Grid
import mui.material.GridDirection
import mui.material.Stack
import mui.system.responsive
import mui.system.sx
import myStore
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.redux.Provider
import react.useEffectOnce
import web.dom.document
import web.html.HTML

fun main() {
    val root = document.createElement(HTML.div)
        .also { document.body.appendChild(it) }
    createRoot(root)
        .render(Root.create())
}

val Root = FC<Props> {
    Provider {
        store = myStore

        App {}
    }
}

val App = FC<Props> {
    useEffectOnce {}

//        Messages {}

    Grid {
        container = true
        direction = responsive(GridDirection.column)
        sx {
            justifyContent = JustifyContent.flexStart
            alignItems = AlignItems.center
//            flexDirection = FlexDirection.column
            flexWrap = FlexWrap.nowrap
            height = 100.vh
        }

        Grid {
            item = true
            css {
                width = 100.vw
            }
            NavBar {}
        }

        Grid {
            item = true
            css {
                backgroundColor =  Color("pink")
                width = 100.vw
            }
            xs = true
            TheoryEditors {}
        }

        Grid {
            item = true
            css {
                backgroundColor =  Color("blue")
                width = 100.vw
            }
            QueryEditor {}
        }

        Grid {
            item = true
            css {
                backgroundColor =  Color("yellow")
                width = 100.vw
                height = 400.px
                overflowY = Overflow.scroll
            }

            SolutionsContainer {}
        }

        Grid {
            item = true
            css {
                backgroundColor =  Color("green")
                width = 100.vw
            }
            Footer {}
        }
    }
}
//            Snackbar {
//                open = isErrorAlertOpen
//                autoHideDuration = AUTOHIDEDURATION
//                onClose = { _, _ -> isErrorAlertOpen = false }
//
//                Alert {
//                    severity = AlertColor.error
//                    +errorAlertMessage
//                }
//                // TODO change snack-bar anchor
//            }