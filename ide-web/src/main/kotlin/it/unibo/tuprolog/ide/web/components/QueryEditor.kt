package it.unibo.tuprolog.ide.web.components

import AppState
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ide.web.utils.InputProps
import it.unibo.tuprolog.ui.gui.Page
import js.core.jso
import mui.material.Button
import mui.material.ButtonGroup
import mui.material.ButtonVariant
import mui.material.FormControlVariant
import mui.material.InputAdornment
import mui.material.InputAdornmentPosition
import mui.material.Stack
import mui.material.StackDirection
import mui.material.TextField
import mui.system.responsive
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.createRef
import react.dom.onChange
import react.redux.useSelector
import web.html.HTMLInputElement

val QueryEditor = FC<Props> {
    val queryInputRef = createRef<HTMLInputElement>()
    val pageStatus =
        useSelector<AppState, Page.Status?> { s -> s.tuProlog.pageStatus }


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
                    TuPrologController.application.currentPage?.query =
                        it1.value
                }
            }

            InputProps = jso {
                endAdornment = InputAdornment.create {
                    position = InputAdornmentPosition.end

                    ButtonGroup {
                        Button {
                            variant = ButtonVariant.contained
                            disabled = pageStatus == Page.Status.COMPUTING
                            +"Solve"
                            onClick = {
                                if (TuPrologController.application.currentPage?.state == Page.Status.IDLE) {
                                    TuPrologController.application.currentPage?.solve(
                                        1
                                    )
                                } else {
                                    TuPrologController.application.currentPage?.next(
                                        1
                                    )
                                }
                            }
                        }
                        Button {
                            variant = ButtonVariant.contained
                            disabled = pageStatus == Page.Status.COMPUTING
                            +"Solve 10"
                            onClick = {
                                if (TuPrologController.application.currentPage?.state == Page.Status.IDLE) {
                                    TuPrologController.application.currentPage?.solve(
                                        10
                                    )
                                } else {
                                    TuPrologController.application.currentPage?.next(
                                        10
                                    )
                                }
                            }
                        }
                        Button {
                            variant = ButtonVariant.contained
                            disabled = pageStatus == Page.Status.COMPUTING
                            onClick = {
                                TuPrologController.application.currentPage?.stop()
                            }
                            +"Stop"
                        }
                        Button {
                            variant = ButtonVariant.contained
                            disabled = pageStatus == Page.Status.COMPUTING
                            onClick = {
                                TuPrologController.application.currentPage?.reset()
                            }
                            +"Reset"
                        }
                    }
                }
            }
        }
    }
}
