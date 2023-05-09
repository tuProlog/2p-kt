package it.unibo.tuprolog.ide.web

import AppState
import csstype.AlignItems
import csstype.FlexWrap
import csstype.JustifyContent
import csstype.Overflow
import csstype.pct
import csstype.px
import csstype.vh
import emotion.react.css
import it.unibo.tuprolog.ide.web.components.TheoryEditors
import it.unibo.tuprolog.ide.web.components.Footer
import it.unibo.tuprolog.ide.web.components.NavBar
import it.unibo.tuprolog.ide.web.components.QueryEditor
import it.unibo.tuprolog.ide.web.components.SolutionsContainer
import it.unibo.tuprolog.ide.web.redux.actions.CleanPageError
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologApplication
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ide.web.utils.xs
import it.unibo.tuprolog.solve.TimeUnit
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.times
import it.unibo.tuprolog.ui.gui.DefaultJsRunner
import it.unibo.tuprolog.ui.gui.InQuerySyntaxError
import it.unibo.tuprolog.ui.gui.InTheorySyntaxError
import mui.material.Button
import mui.material.Dialog
import mui.material.DialogActions
import mui.material.DialogContent
import mui.material.DialogContentText
import mui.material.DialogTitle
import mui.material.Grid
import mui.material.GridDirection
import mui.system.responsive
import mui.system.sx
import myStore
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.redux.Provider
import react.redux.useDispatch
import react.redux.useSelector
import react.useEffectOnce
import redux.RAction
import redux.WrapperAction
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

        useEffectOnce {
            TuPrologController.bindApplication(
                TuPrologApplication.of(
                    DefaultJsRunner(),
                    ClassicSolverFactory,
                    defaultTimeout = 1 * TimeUnit.SECONDS))
            TuPrologController.registerReduxStore(myStore)
            TuPrologController.application.newPage()
            // TODO move into main() ???
        }

        App {}
    }
}



val App = FC<Props> {
    val pageException = useSelector<AppState, Throwable?> { s -> s.tuProlog.pageException }
    val dispatcher = useDispatch<RAction, WrapperAction>()

    fun getExceptionTextHeader(): String {
        return when (pageException) {
            is InTheorySyntaxError -> "Syntax error in ${pageException.page.name}"
            is InQuerySyntaxError -> "Syntax error in query"
            else -> "Error"
        }
    }
    fun getExceptionTextContent(): String {
        if (pageException?.message != null) {
            return pageException.message.toString()
        }
        return ""
    }

    Grid {
        container = true
        direction = responsive(GridDirection.column)
        sx {
            justifyContent = JustifyContent.flexStart
            alignItems = AlignItems.center
            flexWrap = FlexWrap.nowrap
            height = 100.vh
        }

        Grid {
            item = true
            css {
                width = 100.pct
            }
            NavBar {}
        }

        Grid {
            item = true
            css {
                width = 100.pct
            }
            xs = true
            TheoryEditors {}
        }

        Grid {
            item = true
            css {
                width = 100.pct
            }
            QueryEditor {}
        }

        Grid {
            item = true
            css {
                width = 100.pct
                height = 400.px
                overflowY = Overflow.scroll
            }

            SolutionsContainer {}
        }

        Grid {
            item = true
            css {
                width = 100.pct
            }
            Footer {}
        }
    }

    Dialog {
        open = pageException != null
        onClose = { _, _ -> dispatcher(CleanPageError())}

        DialogTitle {
            +getExceptionTextHeader()
        }
        DialogContent {
            DialogContentText {
                +  getExceptionTextContent() //pageException?::class.simpleName +
            }
            DialogActions {
                Button {
                    onClick = { dispatcher(CleanPageError()) }
                    +"Close"
                }
            }
        }
    }
}