package it.unibo.tuprolog.ide.web.components

import csstype.AlignItems.Companion.center
import csstype.JustifyContent.Companion.spaceBetween
import csstype.NamedColor.Companion.red
import csstype.em
import emotion.react.css
import mui.icons.material.Add
import mui.icons.material.DeleteForever
import mui.material.Button
import mui.material.ButtonVariant.Companion.outlined
import mui.material.Fab
import mui.material.FabColor
import mui.material.FabVariant.Companion.extended
import mui.material.Stack
import mui.material.StackDirection
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.redux.useDispatch
import react.redux.useSelector
import web.html.HTMLInputElement
import AppState
import csstype.NamedColor.Companion.green
import it.unibo.tuprolog.ide.web.redux.actions.MessageType
import it.unibo.tuprolog.ide.web.redux.actions.TypedMessage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologController
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ui.gui.PageName
import it.unibo.tuprolog.utils.io.JsFile
import js.uri.encodeURIComponent
import mui.icons.material.GetAppOutlined
import mui.icons.material.Info
import mui.icons.material.UploadFileOutlined
import mui.material.ButtonColor
import mui.material.ButtonVariant.Companion.contained
import mui.material.Dialog
import mui.material.DialogActions
import mui.material.DialogContent
import mui.material.DialogContentText
import mui.material.DialogTitle
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.createRef
import react.dom.html.ReactHTML.input
import react.useState
import redux.RAction
import redux.WrapperAction
import web.dom.document
import web.events.EventType
import web.html.HTML
import web.html.InputType

// TODO move all config magic-numbers into separate file (even better a css file)
const val MYSPACING = 3
const val MYHEIGHT = 56.0
const val MYWIDTH = 56.0

//TODO remove uppercase tab visualization name

val NavBar = FC<Props> {
    var isDialogOpen by useState(false)
    var isDialogRenameOpen by useState(false)
    var changeFileNameErrorInput by useState(false)
    var newFileName by useState("")

    val uploadInputRef = createRef<HTMLInputElement>()
    val newFileNameInputRef = createRef<HTMLInputElement>()

    val dispatcher = useDispatch<RAction, WrapperAction>()
    val currentPage = useSelector<AppState, TuPrologPage?> { s -> s.tuProlog.currentPage }

    Stack {
        direction = responsive(StackDirection.row)
        spacing = responsive(MYSPACING)

        sx {
            justifyContent = spaceBetween
            alignItems = center
        }

        div {
            img {
                src = "https://raw.githubusercontent.com/tuProlog/2p-kt/master/.img/logo.svg"
                height = MYHEIGHT
                width = MYWIDTH
                css {
                    paddingRight = 1.em
                    paddingLeft = 1.em
                }
            }
        }

        div {

            Fab {
                Add()
                color = FabColor.primary
                variant = extended
                onClick = {
                    TuPrologController.application.newPage()
                }
                Typography {
                    +"Add"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                }
            }

//            Fab {
//                Edit()
//                color = FabColor.secondary
//                variant = extended
//                onClick = {
//                    newFileName = editorSelectedTab
//                    changeFileNameErrorInput = false
//                    isDialogRenameOpen = true
//                }
//                Typography {
//                    +"Rename"
//                }
//                sx {
//                    marginRight = 10.em
//                    marginLeft = 1.em
//                }
//            }

            input {
                type = InputType.file
                ref = uploadInputRef
                hidden = true
                accept = ".pl, .txt"
                multiple = false
                onChange = {
                    it.target.files?.get(0)?.text()?.then { it1 ->
                        val name = it.target.files?.get(0)?.name
                        if (name != null) {
                            TuPrologController
                                .application
                                .newPage(PageName(name))
                                .theory = it1
                        }
                        // TODO allow multiple uploads of the same file (following line is not working as intended)
                        it.target.value = ""
                    }
                }
            }
            Button {
                startIcon = UploadFileOutlined.create()
                variant = outlined
                color = ButtonColor.success
                onClick = { uploadInputRef.current?.click() }
                Typography {
                    +"Upload"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                }
            }
            Button {
                startIcon = GetAppOutlined.create()
                variant = outlined
                onClick = {
                    val editorText = TuPrologController.application.currentPage?.theory ?: ""
                    if (editorText != "") {
                        val elem = document.createElement(HTML.a)
                        elem.setAttribute(
                            "href",
                            "data:text/plain;charset=utf-8," + encodeURIComponent(
                                editorText
                            )
                        )
                        elem.setAttribute(
                            "download",
                            TuPrologController.application.currentPage?.id?.name ?: "UNDEFINED.pl"
                        )
                        elem.click()
                    } else {
                        dispatcher(TypedMessage("Cannot download empty theory.", MessageType.ERROR))
                    }

                }
                Typography {
                    +"Download"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                    color = green
                }
            }
            Button {
                startIcon = DeleteForever.create()
                variant = outlined
                color = ButtonColor.error
                onClick = {
                    if (TuPrologController.application.pages.size > 1) {
                        // find the deletable tab panel index
                        val index = TuPrologController.application.pages.indexOfFirst {
                            it.id == TuPrologController.application.currentPage?.id
                        }
                        TuPrologController.application.currentPage?.close()

                        // select new ide
                        TuPrologController.application.select(
                            TuPrologController.application.pages.elementAt(
                                if (index == 0) index else index -1))
                    } else {
                        dispatcher(TypedMessage("Cannot remove last tab.", MessageType.ERROR))
                    }
                }
                Typography {
                    +"Delete"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                }
            }
            Button {
                startIcon = Info.create()
                variant = contained
                onClick = {
                    isDialogOpen = true
                }
                Typography {
                    +"About"
                }
                sx {
                    marginRight = 1.em
                    marginLeft = 1.em
                }
                color = ButtonColor.info
            }
        }

//        Snackbar {
//            open = isMessageOpen
//            autoHideDuration = 6000
//
//            Alert {
//                onClose={ isMessageOpen = false }
//                severity=actionResultSeverity
//                + actionResultMessage
//            }
//        }

        Dialog {
            open = isDialogOpen
            onClose = { _, _ -> isDialogOpen = false }

            DialogTitle {
                +"About"
            }
            DialogContent {
                DialogContentText {
                    +"TupKTWeb versione 0.1"
                }
                DialogActions {
                    Button {
                        onClick = { isDialogOpen = false }
                        +"Close"
                    }
                }
            }
        }

//        Dialog {
//            open = isDialogRenameOpen
//            onClose = { _, _ -> isDialogRenameOpen = false }
//            DialogTitle {
//                +"Rename editor"
//            }
//            DialogContent {
//                DialogContentText {
//                    +"Write here the new name for $currentPage"
//                }
//                TextField {
//                    autoFocus = true
//                    inputRef = newFileNameInputRef
//                    fullWidth = true
//                    error = changeFileNameErrorInput
//                    label = ReactNode("New file name")
//                    helperText = ReactNode("File name must end with .pl or .txt")
//                    defaultValue = currentPage
//                    onChange = {
//                        newFileNameInputRef.current?.let { it1 ->
//                            newFileName = it1.value
//                            changeFileNameErrorInput = !(it1.value.matches(Regex("\\w+(\\.pl|\\.txt)\$")))
//                        }
//                    }
//                }
//            }
//            DialogActions {
//                Button {
//                    onClick = {
//                        isDialogRenameOpen = false
//                    }
//                    +"Cancel"
//                }
//                Button {
//                    disabled = changeFileNameErrorInput
//                    onClick = {
//                        isDialogRenameOpen = false
//                        dispatcher(RenameEditor(newFileName))
//                    }
//                    +"Confirm"
//                }
//            }
//        }
    }
}
