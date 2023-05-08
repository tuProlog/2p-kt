package it.unibo.tuprolog.ide.web.components

import AppState
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ui.gui.Page
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.FormControlVariant
import mui.material.Stack
import mui.material.StackDirection
import mui.material.TextField
import mui.system.responsive
import react.FC
import react.Props
import react.ReactNode
import react.createRef
import react.dom.onChange
import react.redux.useSelector
import web.html.HTMLInputElement

val QueryEditor = FC<Props> {
    val queryInputRef = createRef<HTMLInputElement>()
    val pageStatus = useSelector<AppState, Page.Status> { s -> s.tuProlog.pageStatus }


    Stack {
        direction = responsive(StackDirection.row)
        TextField {
            id = "query"
            label = ReactNode("Query")
            variant = FormControlVariant.outlined
            fullWidth = true
            inputRef = queryInputRef
            onChange = {
                queryInputRef.current?.let { it1 ->
                    TuPrologController.application.currentPage?.query = it1.value
                }
            }
        }
        Button {
            variant = ButtonVariant.contained
            +"Solve"
            onClick = {
                if ( TuPrologController.application.currentPage?.state == Page.Status.IDLE) {
                    TuPrologController.application.currentPage?.solve(1)
                } else {
                    TuPrologController.application.currentPage?.next(1)
                }
            }
        }
        Button {
            variant = ButtonVariant.contained
            +"Solve All"
            onClick = {
                if ( TuPrologController.application.currentPage?.state == Page.Status.IDLE) {
                    TuPrologController.application.currentPage?.solve(Int.MAX_VALUE)
                } else {
                    TuPrologController.application.currentPage?.next(Int.MAX_VALUE)
                }
            }
        }
        Button {
            variant = ButtonVariant.contained
            disabled = pageStatus == Page.Status.COMPUTING
            onClick = {
                TuPrologController.application.currentPage?.reset()
            }
            +"Stop"
        }
        Button {
            variant = ButtonVariant.contained
            onClick = {
                TuPrologController.application.currentPage?.reset()
            }
            +"Reset"
        }
    }
}
