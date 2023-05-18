package it.unibo.tuprolog.ide.web.components

import csstype.px
import emotion.react.css
import it.unibo.tuprolog.ide.web.appConfig
import it.unibo.tuprolog.ide.web.redux.AppState
import it.unibo.tuprolog.ide.web.redux.CleanSolutions
import it.unibo.tuprolog.ide.web.redux.PageWrapper
import it.unibo.tuprolog.ide.web.tuprolog.TPPageStatus
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ide.web.utils.InputProps
import js.core.jso
import mui.material.Button
import mui.material.ButtonGroup
import mui.material.ButtonVariant
import mui.material.FormControlVariant
import mui.material.InputAdornment
import mui.material.InputAdornmentPosition
import mui.material.TextField
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.createRef
import react.dom.html.ReactHTML.div
import react.dom.onChange
import react.redux.useDispatch
import react.redux.useSelector
import react.useEffect
import react.useState
import redux.RAction
import redux.WrapperAction
import web.html.HTMLInputElement

val QueryEditor = FC<Props> {
    val queryInputRef = createRef<HTMLInputElement>()
    val pageStatus = useSelector<AppState, TPPageStatus?> { s -> s.tuProlog.currentPage?.pageStatus }
    val currentPage = useSelector<AppState, PageWrapper?> { s -> s.tuProlog.currentPage }
    var fieldQuery by useState("")
    val dispatcher = useDispatch<RAction, WrapperAction>()

    useEffect {
        fieldQuery = currentPage?.query ?: ""
    }

    div {
        css {
            paddingLeft = 20.px
            paddingRight = 20.px
        }

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
                    fieldQuery = it1.value
                }
            }
            value = currentPage?.query ?: ""

            InputProps = jso {
                endAdornment = InputAdornment.create {
                    position = InputAdornmentPosition.end

                    ButtonGroup {
                        Button {
                            variant = ButtonVariant.contained
                            disabled = pageStatus == TPPageStatus.COMPUTING
                            if (pageStatus == TPPageStatus.IDLE) {
                                +"Solve"
                            } else {
                                +"Next"
                            }
                            onClick = {
                                if (pageStatus == TPPageStatus.IDLE) {
                                    dispatcher(CleanSolutions())
                                    TuPrologController.application.currentPage?.solve()
                                } else {
                                    TuPrologController.application.currentPage?.next()
                                }
                            }
                        }
                        Button {
                            variant = ButtonVariant.contained
                            disabled = (pageStatus == TPPageStatus.COMPUTING)
                            if (pageStatus == TPPageStatus.IDLE) {
                                +"Solve 10"
                            } else {
                                +"Next 10"
                            }
                            onClick = {
                                if (pageStatus == TPPageStatus.IDLE) {
                                    dispatcher(CleanSolutions())
                                    TuPrologController.application.currentPage?.solve(
                                        appConfig.maxSolutions
                                    )
                                } else {
                                    TuPrologController.application.currentPage?.next(
                                        appConfig.maxSolutions
                                    )
                                }
                            }
                        }
                        Button {
                            variant = ButtonVariant.contained
                            disabled = pageStatus == TPPageStatus.COMPUTING
                            onClick = {
                                TuPrologController.application.currentPage?.stop()
                            }
                            +"Stop"
                        }
                        Button {
                            variant = ButtonVariant.contained
                            disabled = pageStatus == TPPageStatus.COMPUTING
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
